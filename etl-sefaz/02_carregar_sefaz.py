"""Importação idempotente dos produtos tratados da SEFAZ para MySQL.
Uso: python 02_carregar_sefaz.py --csv dataset_final.csv --dry-run
     python 02_carregar_sefaz.py --csv dataset_final.csv --executar
Variáveis: DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD.
"""
import argparse
import csv
import os
from decimal import Decimal, InvalidOperation
from pathlib import Path

COLUNAS = ['DESCRICAO','CATEGORIA','NCM','QUANTIDADE','UNIDADE','VALOR_UNITARIO','VALOR_TOTAL','CHAVE_NFE','VALOR_CALCULADO','VALOR_CONFERE']


def numero(texto):
    s = str(texto).strip().replace(' ', '')
    if ',' in s:
        s = s.replace('.', '').replace(',', '.')
    try:
        return Decimal(s)
    except InvalidOperation as e:
        raise ValueError(f'Número inválido: {texto!r}') from e


def ler_csv(caminho):
    with open(caminho, newline='', encoding='utf-8-sig') as arquivo:
        amostra = arquivo.read(4096)
        arquivo.seek(0)
        separador = csv.Sniffer().sniff(amostra, delimiters=',;').delimiter
        leitor = csv.DictReader(arquivo, delimiter=separador)
        faltando = set(COLUNAS) - set(leitor.fieldnames or [])
        if faltando:
            raise ValueError(f'Colunas ausentes: {sorted(faltando)}')
        for i, linha in enumerate(leitor, 2):
            chave = linha['CHAVE_NFE'].strip()
            descricao = linha['DESCRICAO'].strip()
            if len(chave) != 44 or not chave.isdigit() or not descricao:
                raise ValueError(f'Linha {i}: chave de NF-e ou descrição inválida')
            qtd, preco, total, calculado = map(numero, (linha['QUANTIDADE'],linha['VALOR_UNITARIO'],linha['VALOR_TOTAL'],linha['VALOR_CALCULADO']))
            if qtd < 0 or preco < 0 or total < 0:
                raise ValueError(f'Linha {i}: valores negativos')
            yield (descricao,linha['CATEGORIA'].strip(),linha['NCM'].strip(),qtd,linha['UNIDADE'].strip(),preco,total,chave,calculado,linha['VALOR_CONFERE'].strip().lower() in ('true','1','sim'))


SQL = '''INSERT INTO sefaz_produto_referencia
(descricao,categoria,ncm,quantidade_nfe,unidade_nfe,valor_unitario_nfe,valor_total_nfe,chave_nfe,valor_calculado,valor_confere)
VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
ON DUPLICATE KEY UPDATE categoria=VALUES(categoria),ncm=VALUES(ncm),quantidade_nfe=VALUES(quantidade_nfe),
valor_unitario_nfe=VALUES(valor_unitario_nfe),valor_total_nfe=VALUES(valor_total_nfe),
valor_calculado=VALUES(valor_calculado),valor_confere=VALUES(valor_confere)'''


def main():
    p = argparse.ArgumentParser()
    p.add_argument('--csv', type=Path, required=True)
    modo = p.add_mutually_exclusive_group(required=True)
    modo.add_argument('--dry-run', action='store_true')
    modo.add_argument('--executar', action='store_true')
    args = p.parse_args()
    linhas = list(ler_csv(args.csv))
    print(f'CSV validado: {len(linhas)} registros. Exemplo: {linhas[0][0] if linhas else "vazio"}')
    if args.dry_run:
        print('Nenhuma alteração realizada no banco.')
        return
    import mysql.connector
    obrigatorias = ['DB_HOST','DB_NAME','DB_USER','DB_PASSWORD']
    ausentes = [v for v in obrigatorias if not os.getenv(v)]
    if ausentes:
        raise RuntimeError(f'Configure as variáveis de ambiente: {", ".join(ausentes)}')
    conexao = mysql.connector.connect(host=os.environ['DB_HOST'],port=int(os.getenv('DB_PORT','3306')),
        database=os.environ['DB_NAME'],user=os.environ['DB_USER'],password=os.environ['DB_PASSWORD'])
    try:
        cursor = conexao.cursor()
        try:
            cursor.executemany(SQL, linhas)
            conexao.commit()
            print(f'Importação concluída: {len(linhas)} linhas processadas (inseridas ou atualizadas).')
        except Exception:
            conexao.rollback()
            raise
        finally:
            cursor.close()
    finally:
        conexao.close()


if __name__ == '__main__':
    main()
