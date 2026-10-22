import pandas as pd
from pathlib import Path


# ==========================================
# 1. LEITURA DA BASE CONSOLIDADA
# ==========================================

arquivo = "dados/produtos_consolidados_sefaz.csv"

df = pd.read_csv(
    arquivo,
    sep=";",
    decimal=",",
    dtype={
        "NCM": str,
        "CHAVE_NFE": str
    }
)

print("Produtos carregados:", len(df))


# ==========================================
# 2. VALIDAÇÃO DA ESTRUTURA
# ==========================================

colunas_obrigatorias = [
    "DESCRICAO",
    "CATEGORIA",
    "NCM",
    "QUANTIDADE",
    "UNIDADE",
    "VALOR_UNITARIO",
    "VALOR_TOTAL",
    "CHAVE_NFE"
]

faltantes = [
    coluna
    for coluna in colunas_obrigatorias
    if coluna not in df.columns
]

if faltantes:
    raise ValueError(
        f"Colunas obrigatórias ausentes: {faltantes}"
    )


# ==========================================
# 3. VALIDAÇÃO DOS REGISTROS
# ==========================================

if df[colunas_obrigatorias].isnull().any().any():
    raise ValueError(
        "Existem campos vazios na base consolidada."
    )

if (df["QUANTIDADE"] <= 0).any():
    raise ValueError(
        "Existem produtos com quantidade inválida."
    )

if (df["VALOR_UNITARIO"] <= 0).any():
    raise ValueError(
        "Existem produtos com valor unitário inválido."
    )

if (df["VALOR_TOTAL"] <= 0).any():
    raise ValueError(
        "Existem produtos com valor total inválido."
    )

if not df["NCM"].str.fullmatch(r"\d{8}").all():
    raise ValueError(
        "Existem NCMs fora do padrão de 8 dígitos."
    )

if not df["CHAVE_NFE"].str.fullmatch(r"\d{44}").all():
    raise ValueError(
        "Existem chaves de NF-e fora do padrão de 44 dígitos."
    )


# ==========================================
# 4. CONFERÊNCIA DOS VALORES
# ==========================================

from decimal import Decimal, ROUND_HALF_UP

def calcular_valor_total(linha):
    quantidade = Decimal(str(linha["QUANTIDADE"]))
    valor_unitario = Decimal(str(linha["VALOR_UNITARIO"]))

    return float(
        (quantidade * valor_unitario).quantize(
            Decimal("0.01"),
            rounding=ROUND_HALF_UP
        )
    )


df["VALOR_CALCULADO"] = df.apply(
    calcular_valor_total,
    axis=1
)

df["VALOR_CONFERE"] = (
    (df["VALOR_CALCULADO"] - df["VALOR_TOTAL"])
    .abs()
    <= 0.01
)

# Mostra registros cujo valor não confere
df_divergencias = df[
    ~df["VALOR_CONFERE"]
]

if not df_divergencias.empty:
    print("\nProdutos com divergência de valor:")

    print(
        df_divergencias[
            [
                "DESCRICAO",
                "QUANTIDADE",
                "VALOR_UNITARIO",
                "VALOR_CALCULADO",
                "VALOR_TOTAL"
            ]
        ].to_string(index=False)
    )

# ==========================================
# 5. EXPORTAÇÃO
# ==========================================

Path("saida").mkdir(exist_ok=True)

df.to_csv(
    "saida/dataset_final.csv",
    index=False,
    sep=";",
    decimal=",",
    encoding="utf-8-sig"
)

print("\nValidação concluída.")
print("Produtos:", len(df))
print(
    "Valores conferidos:",
    df["VALOR_CONFERE"].sum(),
    "/",
    len(df)
)

print("\nDataset final gerado:")
print("saida/dataset_final.csv")