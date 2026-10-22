
import pandas as pd
import unicodedata
from pathlib import Path


# ==========================================
# 1. EXTRAÇÃO DOS DADOS
# ==========================================

df = pd.read_excel(
    "dados/notas_fiscais.xlsx",
    dtype={
        "COD_CHAVE_NFE": str,
        "COD_PROD": str,
        "COD_NCM": str
    }
)

print("Total de registros:", len(df))


# ==========================================
# 2. NORMALIZAÇÃO DOS PRODUTOS
# ==========================================

def normalizar(texto):
    if pd.isna(texto):
        return ""

    texto = str(texto).upper().strip()

    # Remove acentos
    texto = unicodedata.normalize("NFKD", texto)

    texto = "".join(
        caractere
        for caractere in texto
        if not unicodedata.combining(caractere)
    )

    # Remove espaços duplicados
    texto = " ".join(texto.split())

    return texto


# Preserva a descrição original
df["PRODUTO_NORMALIZADO"] = (
    df["DSC_PRODUTO"].apply(normalizar)
)


# ==========================================
# 3. FILTRAGEM DOS PRODUTOS
# ==========================================

# Produtos desejados
produtos = [
    "LUVA",
    "AGULHA",
    "MASCARA",
    "ALCOOL",
    "GAZE",
    "TINTA",
    "PAPEL HECTOGRAFICO"
]

# Termos indesejados
exclusoes = [
    "ANEST",
    "SUTURA",
    "ANCORA",
    "NASAL",
    "ORONASAL",
    "PISO",
    "PAREDE",
    "FOSCO",
    "IMPRESSORA",
    "TONER"
]

filtro = "|".join(produtos)
filtro_exclusao = "|".join(exclusoes)

# Seleciona produtos desejados
df_filtrado = df[
    df["PRODUTO_NORMALIZADO"].str.contains(
        filtro,
        na=False,
        regex=True
    )
].copy()

print("Registros após inclusão:", len(df_filtrado))

# Remove produtos indesejados
df_filtrado = df_filtrado[
    ~df_filtrado["PRODUTO_NORMALIZADO"].str.contains(
        filtro_exclusao,
        na=False,
        regex=True
    )
].copy()

print("Produtos após exclusões:", len(df_filtrado))


# ==========================================
# 4. CLASSIFICAÇÃO POR CATEGORIA
# ==========================================

def classificar_produto(descricao):

    if "LUVA" in descricao:
        return "LUVAS"

    elif "AGULHA" in descricao:
        return "AGULHAS"

    elif "MASCARA" in descricao:
        return "MASCARAS"

    elif "ALCOOL" in descricao:
        return "ALCOOL"

    elif "GAZE" in descricao:
        return "GAZES"

    elif "TINTA" in descricao:
        return "TINTAS"

    elif "HECTOGRAFICO" in descricao:
        return "PAPEL HECTOGRAFICO"

    else:
        return "OUTROS"


df_filtrado["CATEGORIA"] = (
    df_filtrado["PRODUTO_NORMALIZADO"]
    .apply(classificar_produto)
)

print("\nProdutos por categoria:")
print(df_filtrado["CATEGORIA"].value_counts())


# ==========================================
# 5. VALIDAÇÃO AUTOMÁTICA
# ==========================================

def validar_produto(descricao, categoria):

    # Produtos claramente incompatíveis
    descartes = [
        "VINAGRE",
        "CARIMBO",
        "IMPRESSAO",
        "IMPRESSORA",
        "PINTURA",
        "HIDRAULICA",
        "CONEXAO",
        "TUBULACAO",
        "BOMBA",
        "BIOPSIA",
        "COLETA DE SANGUE",
        "PORTA AGULHA",
        "DRENO",
        "RESPIRADOR",
        "OXIGENIO",
        "SOLDAGEM"
    ]

    if any(termo in descricao for termo in descartes):
        return "DESCARTAR"

    # Luvas
    if categoria == "LUVAS":
        if any(termo in descricao for termo in [
            "NITRILICA",
            "PROCEDIMENTO",
            "LATEX",
            "DESCARTAVEL"
        ]):
            return "CANDIDATO"

    # Álcool
    elif categoria == "ALCOOL":
        if "70%" in descricao or "70 GL" in descricao:
            return "CANDIDATO"

    # Gazes
    elif categoria == "GAZES":
        if (
            "COMPRESSA" in descricao
            or "GAZE ESTERIL" in descricao
        ):
            return "CANDIDATO"

    # Máscaras
    elif categoria == "MASCARAS":
        if (
            "CIRURGICA" in descricao
            or "DESCARTAVEL" in descricao
        ):
            return "CANDIDATO"

    # Produtos que exigem análise manual
    return "REVISAR"


# Aplica a validação aos registros
df_filtrado["STATUS_VALIDACAO"] = df_filtrado.apply(
    lambda linha: validar_produto(
        linha["PRODUTO_NORMALIZADO"],
        linha["CATEGORIA"]
    ),
    axis=1
)

print("\nResultado da validação:")
print(
    df_filtrado["STATUS_VALIDACAO"].value_counts()
)


# ==========================================
# 6. PREPARAÇÃO DA EXPORTAÇÃO
# ==========================================

# Cria a pasta de saída
Path("saida").mkdir(exist_ok=True)

# Colunas que serão exportadas
colunas = [
    "COD_CHAVE_NFE",
    "COD_PROD",
    "COD_NCM",
    "DSC_PRODUTO",
    "PRODUTO_NORMALIZADO",
    "CATEGORIA",
    "STATUS_VALIDACAO",
    "VLR_TOT_BRUTO_PRODUTO",
    "DAT_EMISSAO",
    "NME_EMITENTE"
]


# ==========================================
# 7. EXPORTAÇÃO DOS CANDIDATOS
# ==========================================

# Todos os registros filtrados
df_filtrado[colunas].to_csv(
    "saida/produtos_candidatos.csv",
    index=False,
    sep=";",
    encoding="utf-8-sig"
)

print("\nArquivo produtos_candidatos.csv gerado!")


# ==========================================
# 8. PRODUTOS PARA REVISÃO
# ==========================================

# Uma ocorrência de cada descrição
df_revisao = df_filtrado.drop_duplicates(
    subset=["PRODUTO_NORMALIZADO"]
).copy()

# Organiza por categoria e descrição
df_revisao = df_revisao.sort_values(
    by=["CATEGORIA", "PRODUTO_NORMALIZADO"]
)

df_revisao[colunas].to_csv(
    "saida/produtos_revisao.csv",
    index=False,
    sep=";",
    encoding="utf-8-sig"
)

print("Produtos para revisão:", len(df_revisao))


# ==========================================
# 9. PRÉ-SELEÇÃO DE PRODUTOS
# ==========================================

# Seleciona candidatos das regras automáticas
df_candidatos = df_filtrado[
    df_filtrado["STATUS_VALIDACAO"] == "CANDIDATO"
].copy()

# Mantém uma ocorrência por descrição
df_candidatos = df_candidatos.drop_duplicates(
    subset=["PRODUTO_NORMALIZADO"]
)

# Organiza por categoria e descrição
df_candidatos = df_candidatos.sort_values(
    by=["CATEGORIA", "PRODUTO_NORMALIZADO"]
)

df_candidatos[colunas].to_csv(
    "saida/produtos_validados.csv",
    index=False,
    sep=";",
    encoding="utf-8-sig"
)

print("Candidatos para conferência:", len(df_candidatos))

print("\nProcessamento finalizado!")
