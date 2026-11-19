# ETL SEFAZ/ES — Orcana

## Objetivo

Processar uma planilha de itens de notas fiscais, normalizar e classificar descrições de produtos de interesse, validar campos essenciais e preparar dados para uso no projeto Orcana. O ETL é um script Python independente do backend Java.

## Estrutura de pastas

```text
Orcana-Backend-SEFAZ/
├── Backend/
│   ├── .env                         # configuração local; não publicar credenciais
│   └── src/main/resources/
│       ├── application-h2.properties
│       └── sefaz_produtos_h2.sql   # atualizado pela opção 2
└── etl-sefaz/
    ├── etl_sefaz.py
    ├── dados/
    │   └── notas_fiscais.xlsx      # arquivo de entrada padrão
    └── saida/                      # criada automaticamente
```

A pasta `dados` e a planilha precisam existir. A pasta `saida` é criada pelo script. É possível indicar outro caminho de planilha com `--arquivo`; o nome `notas_fiscais.xlsx` é obrigatório apenas para a execução padrão.

## Pré-requisitos

- Python com `pandas` e `openpyxl`.
- Para usar MySQL: `mysql-connector-python`, servidor acessível, tabela `sefaz_produto_referencia` existente e variáveis `MYSQL_URL`, `MYSQL_USER` e `MYSQL_PASSWORD` configuradas em `Backend/.env` ou no ambiente.
- Para usar H2: backend Spring Boot configurado com o perfil `h2`, Java 21 e Maven Wrapper.

Instalação das dependências Python:

```powershell
python -m pip install pandas openpyxl mysql-connector-python
```

## Planilha de entrada

O caminho padrão é `etl-sefaz/dados/notas_fiscais.xlsx`. A planilha precisa conter as colunas:

- `COD_CHAVE_NFE`
- `COD_PROD`
- `COD_NCM`
- `DSC_PRODUTO`
- `VLR_TOT_BRUTO_PRODUTO`

A planilha utilizada nos testes foi preparada a partir de dados fiscais da SEFAZ/ES. O ETL não realiza download automático nem garante compatibilidade com arquivos de layouts diferentes.

## Como executar

No diretório `etl-sefaz`:

```powershell
python etl_sefaz.py
```

Menu:

| Opção | Resultado |
| --- | --- |
| `1` — Inserir no MySQL | Processa a planilha, lê as credenciais de `Backend/.env` ou do ambiente, identifica registros já existentes e solicita confirmação antes de gravar novos registros no MySQL. |
| `2` — Preparar carga H2 | Processa a planilha, gera `saida/sefaz_produtos_h2.sql` e copia o SQL para `Backend/src/main/resources/sefaz_produtos_h2.sql`. Não abre conexão com H2. |
| `3` — Dry-run | Processa e gera os CSVs, sem inserir no banco. |
| `0` — Sair | Encerra o menu. |

Execução sem menu:

```powershell
python etl_sefaz.py --dry-run
python etl_sefaz.py --banco mysql
python etl_sefaz.py --banco h2
python etl_sefaz.py --arquivo dados/outra_planilha.xlsx --dry-run
```

Os arquivos CSV da pasta `saida` são regenerados em novas execuções: `produtos_candidatos.csv`, `produtos_revisao.csv`, `produtos_rejeitados.csv` e `dataset_final.csv`.

## Regras de processamento

O script normaliza descrições, classifica produtos e separa registros aprovados, em revisão e rejeitados. As categorias com aprovação automática atualmente são luvas de uso médico, álcool 70% e gazes conforme palavras-chave e regras do código. Os demais casos podem permanecer em revisão ou fora do escopo.

A integridade dos candidatos é verificada quanto à chave NF-e de 44 dígitos, NCM de 8 dígitos, valor total positivo e descrição preenchida. A classificação por palavras-chave não substitui validação de negócio.

Os campos quantidade, unidade, valor unitário e valor calculado permanecem nulos no dataset final porque não estão disponíveis nas colunas de entrada utilizadas.

## Carga no H2

1. Execute a opção `2` do ETL.
2. Na pasta `Backend`, inicie o Spring Boot com o perfil `h2`:

   ```powershell
   .\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=h2"
   ```

3. Abra `http://localhost:8080/h2-console`.
4. Conecte-se usando `jdbc:h2:mem:orcanadb`, usuário `sa` e senha em branco, conforme a configuração do perfil H2.
5. Valide:

   ```sql
   SELECT COUNT(*) AS TOTAL FROM SEFAZ_PRODUTO_REFERENCIA;
   ```

O perfil H2 utiliza banco em memória. A carga SQL é executada na inicialização do Spring Boot; após alterar o arquivo SQL, reinicie o backend para aplicar a nova carga. A porta 8080 precisa estar livre.

## Carga no MySQL

O script lê `MYSQL_URL`, `MYSQL_USER` e `MYSQL_PASSWORD` de `Backend/.env`, com prioridade para variáveis de ambiente do sistema. Ele se conecta diretamente ao MySQL, compara registros existentes e utiliza transação com confirmação para inserir apenas os novos conforme a chave operacional do ETL. Não exclui automaticamente dados antigos.

Atenção: `localhost` na URL do MySQL aponta para a máquina de quem executa o script. Compartilhar o mesmo `.env` não significa compartilhar o mesmo servidor de banco. Nunca publique senhas ou credenciais reais no Git.

## Resultado validado em 07/11/2026

Na planilha utilizada para testes, o processamento retornou:

| Indicador | Quantidade |
| --- | ---: |
| Candidatos | 2.980 |
| Para revisão | 2.821 |
| Rejeitados | 2 |
| Aprovados | 157 |
| Descrições distintas aprovadas | 67 |
| Registros confirmados por `COUNT(*)` no H2 | 157 |

Esses valores pertencem à base testada e podem mudar com novas planilhas ou alterações nas regras de classificação.

## Limitações e evoluções futuras

- A entrada de dados é manual; não existe download automático da SEFAZ.
- O ETL é um script independente, não uma API REST.
- A opção H2 gera e copia SQL; não faz conexão direta com o backend.
- Não existe, nesta entrega, integração implementada entre este ETL, aplicativo móvel, leitura de QR Code ou OCR.
- Uma evolução possível é expor uma API no backend Java para receber os dados tratados e persistir de forma controlada, com autenticação, validação e tratamento de duplicidade.

## Cuidados antes de publicar

- Revisar `git status` e `.gitignore`.
- Não versionar `Backend/.env`, senhas, tokens, arquivos temporários ou planilhas fiscais sem autorização.
- Avaliar se o SQL gerado e os CSVs devem ser versionados; os CSVs são artefatos de execução e normalmente podem ficar fora do Git.
- Não remover scripts antigos sem verificar se são referenciados por outros arquivos, documentação ou rotinas.
