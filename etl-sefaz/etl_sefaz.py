
"""ETL SEFAZ/ES.
Dependências: pip install pandas openpyxl
Execução: python etl_sefaz.py --dry-run
"""

import argparse
import shutil

import os
from decimal import Decimal
from getpass import getpass
from urllib.parse import urlparse

import re
import unicodedata
from pathlib import Path

import pandas as pd

BASE = Path(__file__).resolve().parent

COLUNAS = [
    "COD_CHAVE_NFE",
    "COD_PROD",
    "COD_NCM",
    "DSC_PRODUTO",
    "VLR_TOT_BRUTO_PRODUTO",
]

CAMPOS_FINAIS = [
    "DESCRICAO",
    "CATEGORIA",
    "NCM",
    "QUANTIDADE",
    "UNIDADE",
    "VALOR_UNITARIO",
    "VALOR_TOTAL",
    "CHAVE_NFE",
    "VALOR_CALCULADO",
    "VALOR_CONFERE",
]


def normalizar(valor):
    if pd.isna(valor):
        return ""

    texto = unicodedata.normalize(
        "NFKD", str(valor).upper()
    )

    return " ".join(
        "".join(
            c for c in texto
            if not unicodedata.combining(c)
        ).split()
    )


def classificar(descricao):
    d = descricao

    if re.search(r"\b(LUVA|LUVAS)\b", d):
        categoria = "LUVAS"

        exclusoes = [
            "MULTIUSO",
            "NAO SAUDE",
            "RANHURADA",
            "BORRACHA",
            "PLASTICA",
            "LIMPEZA",
            "DOMESTICA",
            "JARDINAGEM",
        ]

        if any(x in d for x in exclusoes):
            return categoria, "REVISAR"

        inclusoes = [
            "PROCED",
            "CIRURG",
            "NITRILICA",
            "NITRILO",
            "EXAME",
        ]

        if any(x in d for x in inclusoes):
            return categoria, "APROVADO"

        return categoria, "REVISAR"

    if "ALCOOL" in d:
        categoria = "ALCOOL"

        if re.search(r"\b70\s*(?:%|GL|INPM)\b", d) or "70%" in d:
            return categoria, "APROVADO"

        return categoria, "REVISAR"

    if "GAZE" in d:
        categoria = "GAZES"

        if "COMPRESSA" in d or "ESTERIL" in d:
            return categoria, "APROVADO"

        return categoria, "REVISAR"

    if "MASCARA" in d:
        return "MASCARAS", "REVISAR"

    if "AGULHA" in d:
        return "AGULHAS", "REVISAR"

    if "HECTOGRAFICO" in d:
        return "PAPEL HECTOGRAFICO", "REVISAR"

    if "TINTA" in d:
        return "TINTAS", "REVISAR"

    return "", "FORA_ESCOPO"


def executar(arquivo, saida):
    if not arquivo.is_file():
        raise FileNotFoundError(
            f"Planilha não encontrada: {arquivo}"
        )

    df = pd.read_excel(
        arquivo,
        dtype={
            "COD_CHAVE_NFE": str,
            "COD_PROD": str,
            "COD_NCM": str,
        },
    )

    faltantes = set(COLUNAS) - set(df.columns)

    if faltantes:
        raise ValueError(
            f"Colunas ausentes: {sorted(faltantes)}"
        )

    df["PRODUTO_NORMALIZADO"] = (
        df["DSC_PRODUTO"].map(normalizar)
    )

    resultados = df["PRODUTO_NORMALIZADO"].map(
        classificar
    )

    df["CATEGORIA"] = resultados.map(
        lambda r: r[0]
    )

    df["STATUS_VALIDACAO"] = resultados.map(
        lambda r: r[1]
    )

    df = df[
        df["STATUS_VALIDACAO"] != "FORA_ESCOPO"
    ].copy()

    df["VALOR_NUMERICO"] = pd.to_numeric(
        df["VLR_TOT_BRUTO_PRODUTO"],
        errors="coerce",
    )

    df["COD_CHAVE_NFE"] = (
        df["COD_CHAVE_NFE"]
        .fillna("")
        .astype(str)
        .str.strip()
    )

    df["COD_NCM"] = (
        df["COD_NCM"]
        .fillna("")
        .astype(str)
        .str.strip()
    )

    integridade = (
        df["COD_CHAVE_NFE"].str.fullmatch(r"\d{44}")
        & df["COD_NCM"].str.fullmatch(r"\d{8}")
        & df["VALOR_NUMERICO"].gt(0)
        & df["PRODUTO_NORMALIZADO"].ne("")
    )

    df.loc[
        ~integridade, "STATUS_VALIDACAO"
    ] = "REJEITADO_DADOS"

    aprovados = df[
        df["STATUS_VALIDACAO"] == "APROVADO"
    ].copy()

    revisao = df[
        df["STATUS_VALIDACAO"] == "REVISAR"
    ].copy()

    rejeitados = df[
        df["STATUS_VALIDACAO"] == "REJEITADO_DADOS"
    ].copy()

    aprovados = aprovados.drop_duplicates(
        subset=COLUNAS
    )

    final = pd.DataFrame(
        {
            "DESCRICAO": aprovados["PRODUTO_NORMALIZADO"],
            "CATEGORIA": aprovados["CATEGORIA"],
            "NCM": aprovados["COD_NCM"],
            "QUANTIDADE": None,
            "UNIDADE": None,
            "VALOR_UNITARIO": None,
            "VALOR_TOTAL": aprovados["VALOR_NUMERICO"],
            "CHAVE_NFE": aprovados["COD_CHAVE_NFE"],
            "VALOR_CALCULADO": None,
            "VALOR_CONFERE": None,
        },
        columns=CAMPOS_FINAIS,
    )

    saida.mkdir(
        parents=True,
        exist_ok=True,
    )

    arquivos = {
        "produtos_candidatos.csv": df,
        "produtos_revisao.csv": revisao,
        "produtos_rejeitados.csv": rejeitados,
        "dataset_final.csv": final,
    }

    for nome, tabela in arquivos.items():
        tabela.to_csv(
            saida / nome,
            sep=";",
            index=False,
            encoding="utf-8-sig",
        )

        print(
            f"{nome}: {len(tabela)} registros"
        )

    print(
        "Descrições distintas aprovadas:",
        aprovados["PRODUTO_NORMALIZADO"].nunique(),
    )

    return final



def carregar_config_mysql():
    """
    Lê as variáveis MYSQL_* do ambiente ou do Backend/.env.
    Não altera o arquivo .env.
    """
    configuracao = {}

    arquivo_env = BASE.parent / "Backend" / ".env"

    if arquivo_env.is_file():
        for linha in arquivo_env.read_text(
            encoding="utf-8-sig"
        ).splitlines():
            linha = linha.strip()

            if not linha or linha.startswith("#"):
                continue

            if "=" not in linha:
                continue

            chave, valor = linha.split("=", 1)
            configuracao[chave.strip()] = (
                valor.strip().strip('"').strip("'")
            )

    # Variáveis do sistema têm prioridade
    configuracao.update(os.environ)

    url = configuracao.get("MYSQL_URL", "")

    if url.startswith("jdbc:"):
        url = url[5:]

    dados_url = urlparse(url)

    if dados_url.scheme != "mysql":
        raise ValueError(
            "MYSQL_URL inválida ou não configurada."
        )

    usuario = configuracao.get("MYSQL_USER")

    if not usuario:
        raise ValueError("MYSQL_USER não configurado.")

    senha = configuracao.get("MYSQL_PASSWORD")

    if senha is None:
        senha = getpass("Senha do MySQL: ")

    banco = dados_url.path.lstrip("/")

    if not banco:
        raise ValueError(
            "Nome do banco ausente em MYSQL_URL."
        )

    return {
        "host": dados_url.hostname or "localhost",
        "port": dados_url.port or 3306,
        "database": banco,
        "user": usuario,
        "password": senha,
        "autocommit": False,
    }


def chave_produto(chave, descricao, ncm, valor):
    """
    Identificação operacional do dataset atual.
    """
    return (
        str(chave).strip(),
        str(descricao).strip(),
        str(ncm).strip() if ncm is not None else "",
        Decimal(str(valor)).quantize(Decimal("0.01")),
    )


def inserir_mysql(final):
    try:
        import mysql.connector
    except ImportError as erro:
        raise RuntimeError(
            "Instale a dependência: "
            "pip install mysql-connector-python"
        ) from erro

    if final.empty:
        print("Nenhum produto aprovado para inserir.")
        return

    config = carregar_config_mysql()

    print(
        f"\nDestino: {config['host']} / "
        f"{config['database']}"
    )
    print(f"Produtos aprovados: {len(final)}")

    confirmar = input(
        "Deseja iniciar a importação? (S/N): "
    ).strip().upper()

    if confirmar != "S":
        print("Importação cancelada.")
        return

    conexao = None
    cursor = None
    bloqueio = False

    try:
        conexao = mysql.connector.connect(**config)
        cursor = conexao.cursor()

        # Evita duas execuções simultâneas deste ETL
        cursor.execute(
            "SELECT GET_LOCK(%s, 10)",
            ("orcana_etl_sefaz_mysql",),
        )

        resultado = cursor.fetchone()
        bloqueio = resultado[0] == 1

        if not bloqueio:
            raise RuntimeError(
                "Outra importação está em andamento."
            )

        conexao.start_transaction()

        # Consulta os registros já existentes
        cursor.execute("""
            SELECT
                chave_nfe,
                descricao,
                ncm,
                valor_total_nfe
            FROM sefaz_produto_referencia
        """)

        existentes = set()

        for chave, descricao, ncm, valor in cursor.fetchall():
            existentes.add(
                chave_produto(
                    chave, descricao, ncm, valor
                )
            )

        total_antes = len(existentes)
        novos = []
        ignorados = 0

        for _, produto in final.iterrows():
            identificador = chave_produto(
                produto["CHAVE_NFE"],
                produto["DESCRICAO"],
                produto["NCM"],
                produto["VALOR_TOTAL"],
            )

            if identificador in existentes:
                ignorados += 1
                continue

            existentes.add(identificador)

            novos.append((
                produto["DESCRICAO"],
                produto["CATEGORIA"],
                produto["NCM"],
                Decimal(
                    str(produto["VALOR_TOTAL"])
                ).quantize(Decimal("0.01")),
                produto["CHAVE_NFE"],
            ))

        print(f"\nRegistros distintos existentes: {total_antes}")
        print(f"Registros já encontrados: {ignorados}")
        print(f"Novos registros: {len(novos)}")

        if not novos:
            conexao.rollback()
            print("Nenhuma inserção necessária.")
            return

        cursor.executemany("""
            INSERT INTO sefaz_produto_referencia (
                descricao,
                categoria,
                ncm,
                quantidade_nfe,
                unidade_nfe,
                valor_unitario_nfe,
                valor_total_nfe,
                chave_nfe,
                valor_calculado,
                valor_confere
            )
            VALUES (
                %s, %s, %s,
                NULL, NULL, NULL,
                %s, %s,
                NULL, NULL
            )
        """, novos)

        print(
            f"\n{len(novos)} registros preparados "
            "para gravação."
        )

        confirmar_gravacao = input(
            "Confirmar gravação no MySQL? (S/N): "
        ).strip().upper()

        if confirmar_gravacao == "S":
            conexao.commit()
            print(
                f"Importação concluída: "
                f"{len(novos)} registros inseridos."
            )
        else:
            conexao.rollback()
            print(
                "Gravação cancelada. "
                "Transação revertida."
            )

    except Exception:
        if conexao and conexao.is_connected():
            conexao.rollback()
        print("Falha na importação. Transação revertida.")
        raise

    finally:
        if cursor and bloqueio and conexao.is_connected():
            try:
                cursor.execute(
                    "SELECT RELEASE_LOCK(%s)",
                    ("orcana_etl_sefaz_mysql",),
                )
                cursor.fetchone()
            except Exception:
                pass

        if cursor:
            cursor.close()

        if conexao and conexao.is_connected():
            conexao.close()



def gerar_sql_h2(final, saida):
    """
    Gera um arquivo SQL para carregar os produtos no H2.
    Não modifica o data.sql existente.
    """

    def texto_sql(valor):
        if pd.isna(valor):
            return "NULL"

        texto = str(valor).replace("'", "''")
        return f"'{texto}'"

    def numero_sql(valor):
        if pd.isna(valor):
            return "NULL"

        return str(
            Decimal(str(valor)).quantize(Decimal("0.01"))
        )

    if final.empty:
        print("Nenhum produto aprovado para o H2.")
        return

    caminho = saida / "sefaz_produtos_h2.sql"

    comandos = [
        "-- Carga de produtos SEFAZ - Orcana",
        "-- Gerado automaticamente pelo ETL.",
        "-- Não altera os registros existentes.",
        "",
    ]

    for _, produto in final.iterrows():
        descricao = texto_sql(produto["DESCRICAO"])
        categoria = texto_sql(produto["CATEGORIA"])
        ncm = texto_sql(produto["NCM"])
        chave = texto_sql(produto["CHAVE_NFE"])
        valor = numero_sql(produto["VALOR_TOTAL"])

        comandos.append(f"""
INSERT INTO sefaz_produto_referencia (
    descricao,
    categoria,
    ncm,
    quantidade_nfe,
    unidade_nfe,
    valor_unitario_nfe,
    valor_total_nfe,
    chave_nfe,
    valor_calculado,
    valor_confere
)
SELECT
    {descricao},
    {categoria},
    {ncm},
    NULL,
    NULL,
    NULL,
    {valor},
    {chave},
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = {chave}
      AND descricao = {descricao}
      AND ncm = {ncm}
      AND valor_total_nfe = {valor}
);
""".strip())

    caminho.write_text(
        "\n\n".join(comandos) + "\n",
        encoding="utf-8",
    )

    destino = BASE.parent / "Backend" / "src" / "main" / "resources" / "sefaz_produtos_h2.sql"

    if not destino.parent.is_dir():
        raise FileNotFoundError(
            f"Pasta de recursos do backend não encontrada: {destino.parent}"
        )

    shutil.copy2(caminho, destino)

    print(f"Arquivo SQL atualizado no backend: {destino}")

    print("\nArquivo SQL para H2 gerado com sucesso.")
    print(f"Local: {caminho}")
    print(f"Produtos preparados: {len(final)}")
    print("Nenhuma alteração realizada no banco H2.")



def main():
    parser = argparse.ArgumentParser(
        description="ETL SEFAZ/ES - Orcana"
    )

    parser.add_argument(
        "--arquivo",
        type=Path,
        default=BASE / "dados" / "notas_fiscais.xlsx",
    )

    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Processa a planilha sem inserir no banco",
    )

    parser.add_argument(
        "--banco",
        choices=["mysql", "h2"],
        help="Seleciona o banco de destino",
    )

    args = parser.parse_args()

    # Permite executar diretamente pelo terminal
    if args.dry_run:
        executar(args.arquivo, BASE / "saida")
        return

    if args.banco == "mysql":
        final = executar(args.arquivo, BASE / "saida")
        inserir_mysql(final)
        return

    if args.banco == "h2":
        final = executar(args.arquivo, BASE / "saida")
        gerar_sql_h2(final, BASE / "saida")
        return

    # Menu para execução pelo VS Code
    while True:
        print("\n================================")
        print("       ETL SEFAZ - ORCANA")
        print("================================")
        print("1 - Inserir no MySQL")
        print("2 - Preparar carga H2")
        print("3 - Apenas processar (Dry-run)")
        print("0 - Sair")
        print("================================")

        opcao = input("Escolha uma opção: ").strip()

        if opcao == "1":
           print("\nIniciando processamento para MySQL...\n")
           final = executar(args.arquivo, BASE / "saida")
           inserir_mysql(final)

        elif opcao == "2":
            print("\nPreparando carga para H2...\n")
            final = executar(args.arquivo, BASE / "saida")
            gerar_sql_h2(final, BASE / "saida")

        elif opcao == "3":
            print("\nIniciando processamento...\n")
            executar(args.arquivo, BASE / "saida")
            print("\nProcessamento concluído.")

        elif opcao == "0":
            print("\nEncerrando ETL.")
            break

        else:
            print("\nOpção inválida. Tente novamente.")



if __name__ == "__main__":
    main()
