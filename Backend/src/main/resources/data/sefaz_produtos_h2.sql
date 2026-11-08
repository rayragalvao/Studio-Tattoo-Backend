-- Carga de produtos SEFAZ - Orcana

-- Gerado automaticamente pelo ETL.

-- Não altera os registros existentes.



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
    'ALCOOL 70% ANTISSEPTICO 250ML',
    'ALCOOL',
    '30039099',
    NULL,
    NULL,
    NULL,
    9433.75,
    '32260135997345000146550000001683821113336016',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260135997345000146550000001683821113336016'
      AND descricao = 'ALCOOL 70% ANTISSEPTICO 250ML'
      AND ncm = '30039099'
      AND valor_total_nfe = 9433.75
);

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
    'LUVA PROC NITRILICA AZUL TAM M C/100',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    990.00,
    '32260128345933000130550010000209341255332232',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260128345933000130550010000209341255332232'
      AND descricao = 'LUVA PROC NITRILICA AZUL TAM M C/100'
      AND ncm = '40151200'
      AND valor_total_nfe = 990.00
);

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
    'ALCOOL 70% 1L',
    'ALCOOL',
    '38085200',
    NULL,
    NULL,
    NULL,
    2550.00,
    '32260145149907000155550010000046481653330188',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260145149907000155550010000046481653330188'
      AND descricao = 'ALCOOL 70% 1L'
      AND ncm = '38085200'
      AND valor_total_nfe = 2550.00
);

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
    'ALCOOL 70% 1L',
    'ALCOOL',
    '38085200',
    NULL,
    NULL,
    NULL,
    2550.00,
    '32260145149907000155550010000046681032955814',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260145149907000155550010000046681032955814'
      AND descricao = 'ALCOOL 70% 1L'
      AND ncm = '38085200'
      AND valor_total_nfe = 2550.00
);

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
    'LUVA CIRURGICA LATEX C/ PO TAM 6,5 PAR MEDIX LT 250717PW',
    'LUVAS',
    '62160000',
    NULL,
    NULL,
    NULL,
    3100.00,
    '32260136035318000155550010000038391100038146',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260136035318000155550010000038391100038146'
      AND descricao = 'LUVA CIRURGICA LATEX C/ PO TAM 6,5 PAR MEDIX LT 250717PW'
      AND ncm = '62160000'
      AND valor_total_nfe = 3100.00
);

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
    'ALCOOL 70% 1L',
    'ALCOOL',
    '38085200',
    NULL,
    NULL,
    NULL,
    2550.00,
    '32260145149907000155550010000046761567504682',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260145149907000155550010000046761567504682'
      AND descricao = 'ALCOOL 70% 1L'
      AND ncm = '38085200'
      AND valor_total_nfe = 2550.00
);

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
    'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    540.00,
    '35260159309302000199550010001591381139552490',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260159309302000199550010001591381139552490'
      AND descricao = 'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)'
      AND ncm = '40151200'
      AND valor_total_nfe = 540.00
);

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
    'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    540.00,
    '35260159309302000199550010001591391765230002',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260159309302000199550010001591391765230002'
      AND descricao = 'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)'
      AND ncm = '40151200'
      AND valor_total_nfe = 540.00
);

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
    'COMPRESSA DE GAZE ESTERIL 7,5X7,5 13F C/10 BMEDICAL',
    'GAZES',
    '30059090',
    NULL,
    NULL,
    NULL,
    23636.16,
    '31260103945035000191550010003367211012117634',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '31260103945035000191550010003367211012117634'
      AND descricao = 'COMPRESSA DE GAZE ESTERIL 7,5X7,5 13F C/10 BMEDICAL'
      AND ncm = '30059090'
      AND valor_total_nfe = 23636.16
);

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
    'LUVA P/ PROCEDIMENTO LATEX PEQUENA',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    165.00,
    '35260101862347000106550010000013951100013957',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260101862347000106550010000013951100013957'
      AND descricao = 'LUVA P/ PROCEDIMENTO LATEX PEQUENA'
      AND ncm = '40151900'
      AND valor_total_nfe = 165.00
);

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
    'ALCOOL HOSPITALAR 70% 1000ML',
    'ALCOOL',
    '29121200',
    NULL,
    NULL,
    NULL,
    611.50,
    '35260101862347000106550010000013951100013957',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260101862347000106550010000013951100013957'
      AND descricao = 'ALCOOL HOSPITALAR 70% 1000ML'
      AND ncm = '29121200'
      AND valor_total_nfe = 611.50
);

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
    'LUVA P/ PROCEDIMENTO LATEX MEDIA',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    99.00,
    '35260101862347000106550010000013951100013957',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260101862347000106550010000013951100013957'
      AND descricao = 'LUVA P/ PROCEDIMENTO LATEX MEDIA'
      AND ncm = '40151900'
      AND valor_total_nfe = 99.00
);

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
    'COMPRESSA DE GAZE 7,5X7,5 13F C/500 BMEDICAL',
    'GAZES',
    '30059090',
    NULL,
    NULL,
    NULL,
    243.00,
    '31260103945035000191550010003370361012133035',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '31260103945035000191550010003370361012133035'
      AND descricao = 'COMPRESSA DE GAZE 7,5X7,5 13F C/500 BMEDICAL'
      AND ncm = '30059090'
      AND valor_total_nfe = 243.00
);

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
    'COMPRESSA DE GAZE ESTERIL 7,5X7,5 13F C/10 BMEDICAL',
    'GAZES',
    '30059090',
    NULL,
    NULL,
    NULL,
    1026.72,
    '31260103945035000191550010003370361012133035',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '31260103945035000191550010003370361012133035'
      AND descricao = 'COMPRESSA DE GAZE ESTERIL 7,5X7,5 13F C/10 BMEDICAL'
      AND ncm = '30059090'
      AND valor_total_nfe = 1026.72
);

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
    'LUVA DE LATEX TAM. M PROCEDIMENTO BR 302',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    35.77,
    '32260128410074000115550060013746161263321200',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260128410074000115550060013746161263321200'
      AND descricao = 'LUVA DE LATEX TAM. M PROCEDIMENTO BR 302'
      AND ncm = '40151900'
      AND valor_total_nfe = 35.77
);

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
    'LUVA PROCED LATEX G C/100U',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    1285.90,
    '32260145607332000177550010000003231533603332',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260145607332000177550010000003231533603332'
      AND descricao = 'LUVA PROCED LATEX G C/100U'
      AND ncm = '40151900'
      AND valor_total_nfe = 1285.90
);

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
    'LUVA CIRURGICA LATEX ESTERIL COM PO 7.0 CX 50',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    2078.31,
    '32260129010039000171550010000417021343774947',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260129010039000171550010000417021343774947'
      AND descricao = 'LUVA CIRURGICA LATEX ESTERIL COM PO 7.0 CX 50'
      AND ncm = '40151200'
      AND valor_total_nfe = 2078.31
);

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
    'LUVA EM LATEX DE PROCEDIMENTO P',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    100.98,
    '33260126889274000177550050000020561819766345',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '33260126889274000177550050000020561819766345'
      AND descricao = 'LUVA EM LATEX DE PROCEDIMENTO P'
      AND ncm = '40151200'
      AND valor_total_nfe = 100.98
);

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
    'LUVA EM LATEX DE PROCEDIMENTO G',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    102.60,
    '33260126889274000177550050000020561819766345',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '33260126889274000177550050000020561819766345'
      AND descricao = 'LUVA EM LATEX DE PROCEDIMENTO G'
      AND ncm = '40151200'
      AND valor_total_nfe = 102.60
);

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
    'LUVA PARA PROCED. NAO ESTERIL NITRILICA S/PO G C/10- ZELARA (FORNECEDOR: 4339, LOTE: IN25016256, QTDE: 4 ,DATA',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    660.00,
    '35260111206099000441550010009337051063278811',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260111206099000441550010009337051063278811'
      AND descricao = 'LUVA PARA PROCED. NAO ESTERIL NITRILICA S/PO G C/10- ZELARA (FORNECEDOR: 4339, LOTE: IN25016256, QTDE: 4 ,DATA'
      AND ncm = '40151200'
      AND valor_total_nfe = 660.00
);

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
    'LUVA PARA PROCED. NAO ESTERIL NITRILICA S/PO P C/10- ZELARA (FORNECEDOR: 4339, LOTE: IN25016487, QTDE: 6 ,DATA',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    990.00,
    '35260111206099000441550010009337051063278811',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260111206099000441550010009337051063278811'
      AND descricao = 'LUVA PARA PROCED. NAO ESTERIL NITRILICA S/PO P C/10- ZELARA (FORNECEDOR: 4339, LOTE: IN25016487, QTDE: 6 ,DATA'
      AND ncm = '40151200'
      AND valor_total_nfe = 990.00
);

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
    'LUVA NITRILICA S PO TAM. PP AZUL C 100 DESCARPACK',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    3341.57,
    '32260119691725000100550010000197081013382757',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260119691725000100550010000197081013382757'
      AND descricao = 'LUVA NITRILICA S PO TAM. PP AZUL C 100 DESCARPACK'
      AND ncm = '40151200'
      AND valor_total_nfe = 3341.57
);

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
    'ALCOOL 70% ANTISSEPTICO 250ML',
    'ALCOOL',
    '30039099',
    NULL,
    NULL,
    NULL,
    754.70,
    '32260135997345000146550000001691501831932493',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260135997345000146550000001691501831932493'
      AND descricao = 'ALCOOL 70% ANTISSEPTICO 250ML'
      AND ncm = '30039099'
      AND valor_total_nfe = 754.70
);

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
    'ALCOOL 70% ANTISSEPTICO 250ML',
    'ALCOOL',
    '30039099',
    NULL,
    NULL,
    NULL,
    14339.29,
    '32260135997345000146550000001691501831932493',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260135997345000146550000001691501831932493'
      AND descricao = 'ALCOOL 70% ANTISSEPTICO 250ML'
      AND ncm = '30039099'
      AND valor_total_nfe = 14339.29
);

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
    'LUVA NITRILICA AZUL G VASCULAR',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    34840.00,
    '35260101862347000106550010000014001100014009',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260101862347000106550010000014001100014009'
      AND descricao = 'LUVA NITRILICA AZUL G VASCULAR'
      AND ncm = '40151900'
      AND valor_total_nfe = 34840.00
);

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
    'LUVA CIRURGICA 7.0 SENSITEX',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    2120.00,
    '35260101862347000106550010000014001100014009',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260101862347000106550010000014001100014009'
      AND descricao = 'LUVA CIRURGICA 7.0 SENSITEX'
      AND ncm = '40151900'
      AND valor_total_nfe = 2120.00
);

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
    'LUVA CIRURGICA 7.5 SENSITEX',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    2240.00,
    '35260101862347000106550010000014001100014009',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260101862347000106550010000014001100014009'
      AND descricao = 'LUVA CIRURGICA 7.5 SENSITEX'
      AND ncm = '40151900'
      AND valor_total_nfe = 2240.00
);

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
    'LUVA NITRILICA AZUL M VASCULAR',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    34840.00,
    '35260101862347000106550010000014001100014009',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260101862347000106550010000014001100014009'
      AND descricao = 'LUVA NITRILICA AZUL M VASCULAR'
      AND ncm = '40151900'
      AND valor_total_nfe = 34840.00
);

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
    'LUVA CIRURGICA 6.5 SENSITEX',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    2120.00,
    '35260101862347000106550010000014001100014009',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260101862347000106550010000014001100014009'
      AND descricao = 'LUVA CIRURGICA 6.5 SENSITEX'
      AND ncm = '40151900'
      AND valor_total_nfe = 2120.00
);

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
    'LUVA P/ PROCEDIMENTO LATEX PEQUENA',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    66.00,
    '35260101862347000106550010000014031100014035',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260101862347000106550010000014031100014035'
      AND descricao = 'LUVA P/ PROCEDIMENTO LATEX PEQUENA'
      AND ncm = '40151900'
      AND valor_total_nfe = 66.00
);

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
    'LUVA CIRURGICA 7.0 SENSITEX',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    5300.00,
    '35260101862347000106550010000014031100014035',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260101862347000106550010000014031100014035'
      AND descricao = 'LUVA CIRURGICA 7.0 SENSITEX'
      AND ncm = '40151900'
      AND valor_total_nfe = 5300.00
);

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
    'LUVA NITRILICA AZUL M VASCULAR',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    34840.00,
    '35260101862347000106550010000014031100014035',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260101862347000106550010000014031100014035'
      AND descricao = 'LUVA NITRILICA AZUL M VASCULAR'
      AND ncm = '40151900'
      AND valor_total_nfe = 34840.00
);

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
    'LUVA NITRILICA AZUL G VASCULAR',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    34840.00,
    '35260101862347000106550010000014031100014035',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260101862347000106550010000014031100014035'
      AND descricao = 'LUVA NITRILICA AZUL G VASCULAR'
      AND ncm = '40151900'
      AND valor_total_nfe = 34840.00
);

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
    'LUVA CIRURGICA 8.0',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    1325.00,
    '35260101862347000106550010000014031100014035',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260101862347000106550010000014031100014035'
      AND descricao = 'LUVA CIRURGICA 8.0'
      AND ncm = '40151200'
      AND valor_total_nfe = 1325.00
);

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
    'LUVA CIRURGICA 6.5 SENSITEX',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    1590.00,
    '35260101862347000106550010000014031100014035',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260101862347000106550010000014031100014035'
      AND descricao = 'LUVA CIRURGICA 6.5 SENSITEX'
      AND ncm = '40151900'
      AND valor_total_nfe = 1590.00
);

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
    'LUVA P/ PROCEDIMENTO LATEX MEDIA',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    66.00,
    '35260101862347000106550010000014031100014035',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260101862347000106550010000014031100014035'
      AND descricao = 'LUVA P/ PROCEDIMENTO LATEX MEDIA'
      AND ncm = '40151900'
      AND valor_total_nfe = 66.00
);

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
    'LUVA CIRURGICA 7.5 SENSITEX',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    7280.00,
    '35260101862347000106550010000014031100014035',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260101862347000106550010000014031100014035'
      AND descricao = 'LUVA CIRURGICA 7.5 SENSITEX'
      AND ncm = '40151900'
      AND valor_total_nfe = 7280.00
);

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
    'ALCOOL HOSPITALAR 70% 1000ML',
    'ALCOOL',
    '29121200',
    NULL,
    NULL,
    NULL,
    733.80,
    '35260101862347000106550010000014031100014035',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260101862347000106550010000014031100014035'
      AND descricao = 'ALCOOL HOSPITALAR 70% 1000ML'
      AND ncm = '29121200'
      AND valor_total_nfe = 733.80
);

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
    'LUVA CIRURGICA 6.0',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    450.00,
    '35260101862347000106550010000014031100014035',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260101862347000106550010000014031100014035'
      AND descricao = 'LUVA CIRURGICA 6.0'
      AND ncm = '40151900'
      AND valor_total_nfe = 450.00
);

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
    'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    10800.00,
    '35260159309302000199550010001595591168382206',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260159309302000199550010001595591168382206'
      AND descricao = 'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)'
      AND ncm = '40151200'
      AND valor_total_nfe = 10800.00
);

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
    'ALCOOL 70% LIQUIDO 1L',
    'ALCOOL',
    '38089919',
    NULL,
    NULL,
    NULL,
    990.09,
    '32260132444895000140550010000170561885310480',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260132444895000140550010000170561885310480'
      AND descricao = 'ALCOOL 70% LIQUIDO 1L'
      AND ncm = '38089919'
      AND valor_total_nfe = 990.09
);

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
    'LUVA PROCED LATEX G C/100UN',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    11088.00,
    '32260145607332000177550010000003291070483722',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260145607332000177550010000003291070483722'
      AND descricao = 'LUVA PROCED LATEX G C/100UN'
      AND ncm = '40151900'
      AND valor_total_nfe = 11088.00
);

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
    'ALCOOL 70% 100ML ALMOT ANTISSEPTICO SEPTPRO - PROLINK',
    'ALCOOL',
    '38089429',
    NULL,
    NULL,
    NULL,
    8458.83,
    '32260119691725000100550010000197151830367751',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260119691725000100550010000197151830367751'
      AND descricao = 'ALCOOL 70% 100ML ALMOT ANTISSEPTICO SEPTPRO - PROLINK'
      AND ncm = '38089429'
      AND valor_total_nfe = 8458.83
);

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
    'COMPRESSA DE GAZE 13 FIOS ESTERIL C/10 - SOFT ERIMAX',
    'GAZES',
    '30059090',
    NULL,
    NULL,
    NULL,
    11280.00,
    '43260141836567000180550010000029061068145441',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '43260141836567000180550010000029061068145441'
      AND descricao = 'COMPRESSA DE GAZE 13 FIOS ESTERIL C/10 - SOFT ERIMAX'
      AND ncm = '30059090'
      AND valor_total_nfe = 11280.00
);

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
    'ALCOOL ETILICO 70% 1 LT',
    'ALCOOL',
    '22072019',
    NULL,
    NULL,
    NULL,
    1680.00,
    '32260110610928000141550010000053061000220945',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260110610928000141550010000053061000220945'
      AND descricao = 'ALCOOL ETILICO 70% 1 LT'
      AND ncm = '22072019'
      AND valor_total_nfe = 1680.00
);

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
    'ALCOOL ETILICO 70% 1 LT',
    'ALCOOL',
    '22072019',
    NULL,
    NULL,
    NULL,
    570.00,
    '32260110610928000141550010000053091000479363',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260110610928000141550010000053091000479363'
      AND descricao = 'ALCOOL ETILICO 70% 1 LT'
      AND ncm = '22072019'
      AND valor_total_nfe = 570.00
);

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
    'LUVA PROCEDIMENTO M NAO ESTERIL COM PO CX C/ 100 UND ABL',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    18390.00,
    '26260104614288000145550010000113901649135163',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '26260104614288000145550010000113901649135163'
      AND descricao = 'LUVA PROCEDIMENTO M NAO ESTERIL COM PO CX C/ 100 UND ABL'
      AND ncm = '40151200'
      AND valor_total_nfe = 18390.00
);

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
    'LUVA PROCEDIMENTO G NAO ESTERIL COM PO CX C/ 100 UND ABL',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    5547.00,
    '26260104614288000145550010000113901649135163',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '26260104614288000145550010000113901649135163'
      AND descricao = 'LUVA PROCEDIMENTO G NAO ESTERIL COM PO CX C/ 100 UND ABL'
      AND ncm = '40151200'
      AND valor_total_nfe = 5547.00
);

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
    'LUVA LATEX PARA PROCEDIMENTOS',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    499.80,
    '32260151112965000107550010000000371011004100',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260151112965000107550010000000371011004100'
      AND descricao = 'LUVA LATEX PARA PROCEDIMENTOS'
      AND ncm = '40151900'
      AND valor_total_nfe = 499.80
);

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
    'ALCOOL GEL 70% GALAO 5 LITROS',
    'ALCOOL',
    '22072019',
    NULL,
    NULL,
    NULL,
    195.00,
    '32260210610928000141550010000053101000748679',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260210610928000141550010000053101000748679'
      AND descricao = 'ALCOOL GEL 70% GALAO 5 LITROS'
      AND ncm = '22072019'
      AND valor_total_nfe = 195.00
);

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
    'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    216.00,
    '35260259309302000199550010001597761631096832',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260259309302000199550010001597761631096832'
      AND descricao = 'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)'
      AND ncm = '40151200'
      AND valor_total_nfe = 216.00
);

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
    'LUVA CIRURGICA 7.5 SENSITEX',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    4200.00,
    '35260201862347000106550010000014141100014148',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260201862347000106550010000014141100014148'
      AND descricao = 'LUVA CIRURGICA 7.5 SENSITEX'
      AND ncm = '40151900'
      AND valor_total_nfe = 4200.00
);

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
    'LUVA CIRURGICA 6.0',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    2250.00,
    '35260201862347000106550010000014141100014148',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260201862347000106550010000014141100014148'
      AND descricao = 'LUVA CIRURGICA 6.0'
      AND ncm = '40151900'
      AND valor_total_nfe = 2250.00
);

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
    'LUVA P/ PROCEDIMENTO LATEX MEDIA',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    132.00,
    '35260201862347000106550010000014141100014148',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260201862347000106550010000014141100014148'
      AND descricao = 'LUVA P/ PROCEDIMENTO LATEX MEDIA'
      AND ncm = '40151900'
      AND valor_total_nfe = 132.00
);

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
    'LUVA P/ PROCEDIMENTO LATEX PEQUENA',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    132.00,
    '35260201862347000106550010000014141100014148',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260201862347000106550010000014141100014148'
      AND descricao = 'LUVA P/ PROCEDIMENTO LATEX PEQUENA'
      AND ncm = '40151900'
      AND valor_total_nfe = 132.00
);

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
    'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    50000.00,
    '41260239707683000157550010000104161212245510',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '41260239707683000157550010000104161212245510'
      AND descricao = 'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX'
      AND ncm = '40151200'
      AND valor_total_nfe = 50000.00
);

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
    'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    14420.00,
    '41260239707683000157550010000104171998998309',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '41260239707683000157550010000104171998998309'
      AND descricao = 'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX'
      AND ncm = '40151200'
      AND valor_total_nfe = 14420.00
);

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
    'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    17200.00,
    '41260239707683000157550010000104171998998309',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '41260239707683000157550010000104171998998309'
      AND descricao = 'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX'
      AND ncm = '40151200'
      AND valor_total_nfe = 17200.00
);

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
    'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    16800.00,
    '41260239707683000157550010000104171998998309',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '41260239707683000157550010000104171998998309'
      AND descricao = 'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX'
      AND ncm = '40151200'
      AND valor_total_nfe = 16800.00
);

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
    'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    1580.00,
    '41260239707683000157550010000104171998998309',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '41260239707683000157550010000104171998998309'
      AND descricao = 'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX'
      AND ncm = '40151200'
      AND valor_total_nfe = 1580.00
);

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
    'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    216.00,
    '35260259309302000199550010001598151156086637',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260259309302000199550010001598151156086637'
      AND descricao = 'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)'
      AND ncm = '40151200'
      AND valor_total_nfe = 216.00
);

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
    'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    5400.00,
    '35260259309302000199550010001598631343520458',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260259309302000199550010001598631343520458'
      AND descricao = 'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)'
      AND ncm = '40151200'
      AND valor_total_nfe = 5400.00
);

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
    'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    5400.00,
    '35260259309302000199550010001598791407365024',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260259309302000199550010001598791407365024'
      AND descricao = 'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)'
      AND ncm = '40151200'
      AND valor_total_nfe = 5400.00
);

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
    'ALCOOL 70% LIQUIDO 1L',
    'ALCOOL',
    '38089919',
    NULL,
    NULL,
    NULL,
    208.44,
    '32260232444895000140550010000170591441556074',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260232444895000140550010000170591441556074'
      AND descricao = 'ALCOOL 70% LIQUIDO 1L'
      AND ncm = '38089919'
      AND valor_total_nfe = 208.44
);

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
    'LUVA PROCEDIMENTO G NAO ESTERIL COM PO CX C/ 100 UND ABL',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    7396.00,
    '26260204614288000145550010000114281132854742',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '26260204614288000145550010000114281132854742'
      AND descricao = 'LUVA PROCEDIMENTO G NAO ESTERIL COM PO CX C/ 100 UND ABL'
      AND ncm = '40151200'
      AND valor_total_nfe = 7396.00
);

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
    'LUVA PROCEDIMENTO P NAO ESTERIL COM PO CX C/ 100 UND ABL',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    9390.00,
    '26260204614288000145550010000114281132854742',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '26260204614288000145550010000114281132854742'
      AND descricao = 'LUVA PROCEDIMENTO P NAO ESTERIL COM PO CX C/ 100 UND ABL'
      AND ncm = '40151200'
      AND valor_total_nfe = 9390.00
);

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
    'LUVA PROCEDIMENTO M NAO ESTERIL COM PO CX C/ 100 UND ABL',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    14712.00,
    '26260204614288000145550010000114281132854742',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '26260204614288000145550010000114281132854742'
      AND descricao = 'LUVA PROCEDIMENTO M NAO ESTERIL COM PO CX C/ 100 UND ABL'
      AND ncm = '40151200'
      AND valor_total_nfe = 14712.00
);

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
    'LUVA PROC NITRILICA AZUL TAM M C/100',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    16170.00,
    '32260228345933000130550010000216211186142240',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260228345933000130550010000216211186142240'
      AND descricao = 'LUVA PROC NITRILICA AZUL TAM M C/100'
      AND ncm = '40151200'
      AND valor_total_nfe = 16170.00
);

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
    'LUVA PROC NITRILICA AZUL TAM M C/100',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    3646.50,
    '32260228345933000130550010000216211186142240',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260228345933000130550010000216211186142240'
      AND descricao = 'LUVA PROC NITRILICA AZUL TAM M C/100'
      AND ncm = '40151200'
      AND valor_total_nfe = 3646.50
);

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
    'COMPRESSA DE GAZE 13 FIOS ESTERIL C/10 - SOFT ERIMAX',
    'GAZES',
    '30059090',
    NULL,
    NULL,
    NULL,
    5470.80,
    '43260241836567000180550010000029611689299215',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '43260241836567000180550010000029611689299215'
      AND descricao = 'COMPRESSA DE GAZE 13 FIOS ESTERIL C/10 - SOFT ERIMAX'
      AND ncm = '30059090'
      AND valor_total_nfe = 5470.80
);

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
    'COMPRESSA DE GAZE 13 FIOS ESTERIL C/10 - SOFT ERIMAX',
    'GAZES',
    '30059090',
    NULL,
    NULL,
    NULL,
    1410.00,
    '43260241836567000180550010000029611689299215',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '43260241836567000180550010000029611689299215'
      AND descricao = 'COMPRESSA DE GAZE 13 FIOS ESTERIL C/10 - SOFT ERIMAX'
      AND ncm = '30059090'
      AND valor_total_nfe = 1410.00
);

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
    'COMPRESSA DE GAZE 13 FIOS ESTERIL C/10 - SOFT ERIMAX',
    'GAZES',
    '30059090',
    NULL,
    NULL,
    NULL,
    21150.00,
    '43260241836567000180550010000029621984169375',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '43260241836567000180550010000029621984169375'
      AND descricao = 'COMPRESSA DE GAZE 13 FIOS ESTERIL C/10 - SOFT ERIMAX'
      AND ncm = '30059090'
      AND valor_total_nfe = 21150.00
);

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
    'ALCOOL LIQUIDO SAFRA 70 INPM HOSPITALAR 1L',
    'ALCOOL',
    '38089429',
    NULL,
    NULL,
    NULL,
    46.00,
    '32260203445714000100550010000042731376915381',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260203445714000100550010000042731376915381'
      AND descricao = 'ALCOOL LIQUIDO SAFRA 70 INPM HOSPITALAR 1L'
      AND ncm = '38089429'
      AND valor_total_nfe = 46.00
);

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
    'LUVA P/ PROCEDIMENTO LATEX MEDIA',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    99.00,
    '35260201862347000106550010000014381100014387',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260201862347000106550010000014381100014387'
      AND descricao = 'LUVA P/ PROCEDIMENTO LATEX MEDIA'
      AND ncm = '40151900'
      AND valor_total_nfe = 99.00
);

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
    'LUVA P/ PROCEDIMENTO LATEX PEQUENA',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    99.00,
    '35260201862347000106550010000014381100014387',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260201862347000106550010000014381100014387'
      AND descricao = 'LUVA P/ PROCEDIMENTO LATEX PEQUENA'
      AND ncm = '40151900'
      AND valor_total_nfe = 99.00
);

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
    'LUVA CIRURGICA ESTERIL S/PO 6.5',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    499.00,
    '32260228345933000130550010000218501154200205',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260228345933000130550010000218501154200205'
      AND descricao = 'LUVA CIRURGICA ESTERIL S/PO 6.5'
      AND ncm = '40151200'
      AND valor_total_nfe = 499.00
);

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
    'LUVA CIRURGICA ESTERIL S/PO 8.0',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    499.00,
    '32260228345933000130550010000218501154200205',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260228345933000130550010000218501154200205'
      AND descricao = 'LUVA CIRURGICA ESTERIL S/PO 8.0'
      AND ncm = '40151200'
      AND valor_total_nfe = 499.00
);

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
    'LUVA NITRILICA AZUL M VASCULAR',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    2680.00,
    '35260201862347000106550010000014391100014392',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260201862347000106550010000014391100014392'
      AND descricao = 'LUVA NITRILICA AZUL M VASCULAR'
      AND ncm = '40151900'
      AND valor_total_nfe = 2680.00
);

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
    'LUVA NITRILICA AZUL G VASCULAR',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    1340.00,
    '35260201862347000106550010000014391100014392',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260201862347000106550010000014391100014392'
      AND descricao = 'LUVA NITRILICA AZUL G VASCULAR'
      AND ncm = '40151900'
      AND valor_total_nfe = 1340.00
);

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
    'LUVA NITRILICA AZUL P VASCULAR',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    2144.00,
    '35260201862347000106550010000014391100014392',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260201862347000106550010000014391100014392'
      AND descricao = 'LUVA NITRILICA AZUL P VASCULAR'
      AND ncm = '40151900'
      AND valor_total_nfe = 2144.00
);

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
    'ALCOOL EM GEL 70% 800ML',
    'ALCOOL',
    '38089429',
    NULL,
    NULL,
    NULL,
    8280.00,
    '26260233613876000162550000000087971163107172',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '26260233613876000162550000000087971163107172'
      AND descricao = 'ALCOOL EM GEL 70% 800ML'
      AND ncm = '38089429'
      AND valor_total_nfe = 8280.00
);

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
    'ALCOOL 70% LIQUIDO 1L',
    'ALCOOL',
    '38089919',
    NULL,
    NULL,
    NULL,
    550.05,
    '32260232444895000140550010000170601600945100',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260232444895000140550010000170601600945100'
      AND descricao = 'ALCOOL 70% LIQUIDO 1L'
      AND ncm = '38089919'
      AND valor_total_nfe = 550.05
);

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
    'DRENO SUC. S/GAZE ESTERIL N.1 - WALTEX LOTE: EA29126 VALIDADE: 29/01/2028 FAB: 29/01/2026',
    'GAZES',
    '90183929',
    NULL,
    NULL,
    NULL,
    6.12,
    '35260243556958000176550010000041701004640323',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260243556958000176550010000041701004640323'
      AND descricao = 'DRENO SUC. S/GAZE ESTERIL N.1 - WALTEX LOTE: EA29126 VALIDADE: 29/01/2028 FAB: 29/01/2026'
      AND ncm = '90183929'
      AND valor_total_nfe = 6.12
);

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
    'DRENO SUC. S/GAZE ESTERIL N.2 - WALTEX LOTE: EB11226 VALIDADE: 11/02/2028 FAB: 11/02/2026',
    'GAZES',
    '90183929',
    NULL,
    NULL,
    NULL,
    56.61,
    '35260243556958000176550010000041701004640323',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260243556958000176550010000041701004640323'
      AND descricao = 'DRENO SUC. S/GAZE ESTERIL N.2 - WALTEX LOTE: EB11226 VALIDADE: 11/02/2028 FAB: 11/02/2026'
      AND ncm = '90183929'
      AND valor_total_nfe = 56.61
);

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
    'DRENO SUC. S/GAZE ESTERIL N.2 - WALTEX LOTE: EF09024 VALIDADE: 09/10/2026 FAB: 09/10/2024',
    'GAZES',
    '90183929',
    NULL,
    NULL,
    NULL,
    3.06,
    '35260243556958000176550010000041701004640323',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260243556958000176550010000041701004640323'
      AND descricao = 'DRENO SUC. S/GAZE ESTERIL N.2 - WALTEX LOTE: EF09024 VALIDADE: 09/10/2026 FAB: 09/10/2024'
      AND ncm = '90183929'
      AND valor_total_nfe = 3.06
);

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
    'DRENO SUC. S/GAZE ESTERIL N.2 - WALTEX LOTE: TE28025 VALIDADE: 28/10/2027 FAB: 28/10/2025',
    'GAZES',
    '90183929',
    NULL,
    NULL,
    NULL,
    16.83,
    '35260243556958000176550010000041701004640323',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260243556958000176550010000041701004640323'
      AND descricao = 'DRENO SUC. S/GAZE ESTERIL N.2 - WALTEX LOTE: TE28025 VALIDADE: 28/10/2027 FAB: 28/10/2025'
      AND ncm = '90183929'
      AND valor_total_nfe = 16.83
);

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
    'DRENO SUC. S/GAZE ESTERIL N.1 - WALTEX LOTE: TF21825 VALIDADE: 21/08/2027 FAB: 21/08/2025',
    'GAZES',
    '90183929',
    NULL,
    NULL,
    NULL,
    70.38,
    '35260243556958000176550010000041701004640323',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260243556958000176550010000041701004640323'
      AND descricao = 'DRENO SUC. S/GAZE ESTERIL N.1 - WALTEX LOTE: TF21825 VALIDADE: 21/08/2027 FAB: 21/08/2025'
      AND ncm = '90183929'
      AND valor_total_nfe = 70.38
);

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
    'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    50000.00,
    '41260239707683000157550010000105391817183513',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '41260239707683000157550010000105391817183513'
      AND descricao = 'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX'
      AND ncm = '40151200'
      AND valor_total_nfe = 50000.00
);

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
    'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    50000.00,
    '41260239707683000157550010000105401125703235',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '41260239707683000157550010000105401125703235'
      AND descricao = 'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX'
      AND ncm = '40151200'
      AND valor_total_nfe = 50000.00
);

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
    'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    16800.00,
    '41260239707683000157550010000105411012575522',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '41260239707683000157550010000105411012575522'
      AND descricao = 'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX'
      AND ncm = '40151200'
      AND valor_total_nfe = 16800.00
);

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
    'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    14420.00,
    '41260239707683000157550010000105411012575522',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '41260239707683000157550010000105411012575522'
      AND descricao = 'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX'
      AND ncm = '40151200'
      AND valor_total_nfe = 14420.00
);

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
    'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    17200.00,
    '41260239707683000157550010000105411012575522',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '41260239707683000157550010000105411012575522'
      AND descricao = 'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX'
      AND ncm = '40151200'
      AND valor_total_nfe = 17200.00
);

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
    'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    1580.00,
    '41260239707683000157550010000105411012575522',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '41260239707683000157550010000105411012575522'
      AND descricao = 'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX'
      AND ncm = '40151200'
      AND valor_total_nfe = 1580.00
);

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
    'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    16800.00,
    '41260239707683000157550010000105421878800879',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '41260239707683000157550010000105421878800879'
      AND descricao = 'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX'
      AND ncm = '40151200'
      AND valor_total_nfe = 16800.00
);

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
    'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    17200.00,
    '41260239707683000157550010000105421878800879',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '41260239707683000157550010000105421878800879'
      AND descricao = 'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX'
      AND ncm = '40151200'
      AND valor_total_nfe = 17200.00
);

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
    'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    14420.00,
    '41260239707683000157550010000105421878800879',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '41260239707683000157550010000105421878800879'
      AND descricao = 'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX'
      AND ncm = '40151200'
      AND valor_total_nfe = 14420.00
);

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
    'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    1580.00,
    '41260239707683000157550010000105421878800879',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '41260239707683000157550010000105421878800879'
      AND descricao = 'LUVA PROCEDIMENTO LATEX C/ PO TAM G, 100UN MEDIX'
      AND ncm = '40151200'
      AND valor_total_nfe = 1580.00
);

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
    'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    216.00,
    '35260259309302000199550010001603651349086024',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260259309302000199550010001603651349086024'
      AND descricao = 'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)'
      AND ncm = '40151200'
      AND valor_total_nfe = 216.00
);

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
    'ALCOOL 70% ANTISSEPTICO 250ML',
    'ALCOOL',
    '30039099',
    NULL,
    NULL,
    NULL,
    2792.39,
    '32260235997345000146550000001707121133133186',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260235997345000146550000001707121133133186'
      AND descricao = 'ALCOOL 70% ANTISSEPTICO 250ML'
      AND ncm = '30039099'
      AND valor_total_nfe = 2792.39
);

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
    'ALCOOL 70% ANTISSEPTICO 250ML',
    'ALCOOL',
    '30039099',
    NULL,
    NULL,
    NULL,
    4754.61,
    '32260235997345000146550000001707121133133186',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260235997345000146550000001707121133133186'
      AND descricao = 'ALCOOL 70% ANTISSEPTICO 250ML'
      AND ncm = '30039099'
      AND valor_total_nfe = 4754.61
);

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
    'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    216.00,
    '35260259309302000199550010001604501900912110',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260259309302000199550010001604501900912110'
      AND descricao = 'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)'
      AND ncm = '40151200'
      AND valor_total_nfe = 216.00
);

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
    'COMPRESSA DE GAZE ESTERIL 7,5X7,5 13F PCT10UND CX500UND NOBRE',
    'GAZES',
    '30059090',
    NULL,
    NULL,
    NULL,
    1853.28,
    '31260303945035000191550010003397711012289144',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '31260303945035000191550010003397711012289144'
      AND descricao = 'COMPRESSA DE GAZE ESTERIL 7,5X7,5 13F PCT10UND CX500UND NOBRE'
      AND ncm = '30059090'
      AND valor_total_nfe = 1853.28
);

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
    'LUVA CIRURGICA 6.5 SENSITEX',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    1060.00,
    '35260301862347000106550010000014481100014484',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014481100014484'
      AND descricao = 'LUVA CIRURGICA 6.5 SENSITEX'
      AND ncm = '40151900'
      AND valor_total_nfe = 1060.00
);

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
    'LUVA CIRURGICA 7.5 SENSITEX',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    1120.00,
    '35260301862347000106550010000014481100014484',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014481100014484'
      AND descricao = 'LUVA CIRURGICA 7.5 SENSITEX'
      AND ncm = '40151900'
      AND valor_total_nfe = 1120.00
);

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
    'LUVA P/ PROCEDIMENTO LATEX PEQUENA',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    33.00,
    '35260301862347000106550010000014481100014484',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014481100014484'
      AND descricao = 'LUVA P/ PROCEDIMENTO LATEX PEQUENA'
      AND ncm = '40151900'
      AND valor_total_nfe = 33.00
);

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
    'ALCOOL HOSPITALAR 70% 1000ML',
    'ALCOOL',
    '29121200',
    NULL,
    NULL,
    NULL,
    122.30,
    '35260301862347000106550010000014481100014484',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014481100014484'
      AND descricao = 'ALCOOL HOSPITALAR 70% 1000ML'
      AND ncm = '29121200'
      AND valor_total_nfe = 122.30
);

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
    'ALCOOL 70% LIQUIDO 1L',
    'ALCOOL',
    '38089919',
    NULL,
    NULL,
    NULL,
    277.92,
    '32260332444895000140550010000170621148084546',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260332444895000140550010000170621148084546'
      AND descricao = 'ALCOOL 70% LIQUIDO 1L'
      AND ncm = '38089919'
      AND valor_total_nfe = 277.92
);

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
    'LUVA CIRURGICA 6.0',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    1012.50,
    '35260301862347000106550010000014501100014504',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014501100014504'
      AND descricao = 'LUVA CIRURGICA 6.0'
      AND ncm = '40151900'
      AND valor_total_nfe = 1012.50
);

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
    'LUVA P/ PROCEDIMENTO LATEX MEDIA',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    99.00,
    '35260301862347000106550010000014501100014504',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014501100014504'
      AND descricao = 'LUVA P/ PROCEDIMENTO LATEX MEDIA'
      AND ncm = '40151900'
      AND valor_total_nfe = 99.00
);

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
    'LUVA P/ PROCEDIMENTO LATEX PEQUENA',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    33.00,
    '35260301862347000106550010000014501100014504',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014501100014504'
      AND descricao = 'LUVA P/ PROCEDIMENTO LATEX PEQUENA'
      AND ncm = '40151900'
      AND valor_total_nfe = 33.00
);

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
    'LUVA CIRURGICA 7.5 SENSITEX',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    1680.00,
    '35260301862347000106550010000014501100014504',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014501100014504'
      AND descricao = 'LUVA CIRURGICA 7.5 SENSITEX'
      AND ncm = '40151900'
      AND valor_total_nfe = 1680.00
);

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
    'LUVA NITRILICA AZUL G VASCULAR',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    26800.00,
    '35260301862347000106550010000014501100014504',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014501100014504'
      AND descricao = 'LUVA NITRILICA AZUL G VASCULAR'
      AND ncm = '40151900'
      AND valor_total_nfe = 26800.00
);

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
    'LUVA CIRURGICA ESTERIL N° 8,5 LIVE LIVE 80630310013',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    1966.50,
    '41260355674216000124550010000004921251912274',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '41260355674216000124550010000004921251912274'
      AND descricao = 'LUVA CIRURGICA ESTERIL N° 8,5 LIVE LIVE 80630310013'
      AND ncm = '40151200'
      AND valor_total_nfe = 1966.50
);

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
    'LUVA CIRURGICA ESTERIL N° 6,5 LIVE LIVE 0080630310013',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    57.50,
    '41260355674216000124550010000004921251912274',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '41260355674216000124550010000004921251912274'
      AND descricao = 'LUVA CIRURGICA ESTERIL N° 6,5 LIVE LIVE 0080630310013'
      AND ncm = '40151200'
      AND valor_total_nfe = 57.50
);

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
    'LUVA NITRILICA VERDE 09 G VOLK CA 40570',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    210.00,
    '32260311712083000168550010000132351000532316',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260311712083000168550010000132351000532316'
      AND descricao = 'LUVA NITRILICA VERDE 09 G VOLK CA 40570'
      AND ncm = '40151900'
      AND valor_total_nfe = 210.00
);

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
    'ALCOOL 70% 12X1000ML PROLINK',
    'ALCOOL',
    '38089429',
    NULL,
    NULL,
    NULL,
    214.20,
    '32260331804909000126550020000612881851865191',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260331804909000126550020000612881851865191'
      AND descricao = 'ALCOOL 70% 12X1000ML PROLINK'
      AND ncm = '38089429'
      AND valor_total_nfe = 214.20
);

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
    'LUVA DE LATEX C/100 P/ PROCEDIMENTOS',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    36.90,
    '32260309441903000182550010000453651002242500',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260309441903000182550010000453651002242500'
      AND descricao = 'LUVA DE LATEX C/100 P/ PROCEDIMENTOS'
      AND ncm = '40151900'
      AND valor_total_nfe = 36.90
);

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
    'LUVA CIRURGICA LATEX ESTERIL COM PO 8.0 CX 50',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    2078.31,
    '32260329010039000171550010000418601977717811',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260329010039000171550010000418601977717811'
      AND descricao = 'LUVA CIRURGICA LATEX ESTERIL COM PO 8.0 CX 50'
      AND ncm = '40151200'
      AND valor_total_nfe = 2078.31
);

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
    'LUVA P/ PROCEDIMENTO LATEX MEDIA',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    99.00,
    '35260301862347000106550010000014581100014588',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014581100014588'
      AND descricao = 'LUVA P/ PROCEDIMENTO LATEX MEDIA'
      AND ncm = '40151900'
      AND valor_total_nfe = 99.00
);

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
    'LUVA CIRURGICA 7.5 SENSITEX',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    2800.00,
    '35260301862347000106550010000014581100014588',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014581100014588'
      AND descricao = 'LUVA CIRURGICA 7.5 SENSITEX'
      AND ncm = '40151900'
      AND valor_total_nfe = 2800.00
);

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
    'LUVA CIRURGICA 7.0 SENSITEX',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    2650.00,
    '35260301862347000106550010000014581100014588',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014581100014588'
      AND descricao = 'LUVA CIRURGICA 7.0 SENSITEX'
      AND ncm = '40151900'
      AND valor_total_nfe = 2650.00
);

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
    'ALCOOL HOSPITALAR 70% 1000ML',
    'ALCOOL',
    '29121200',
    NULL,
    NULL,
    NULL,
    733.80,
    '35260301862347000106550010000014581100014588',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014581100014588'
      AND descricao = 'ALCOOL HOSPITALAR 70% 1000ML'
      AND ncm = '29121200'
      AND valor_total_nfe = 733.80
);

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
    'LUVA CIRURGICA 6.5 SENSITEX',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    2650.00,
    '35260301862347000106550010000014581100014588',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014581100014588'
      AND descricao = 'LUVA CIRURGICA 6.5 SENSITEX'
      AND ncm = '40151900'
      AND valor_total_nfe = 2650.00
);

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
    'ALCOOL LIQ 70 INPM PET 1L TUPI - 2143',
    'ALCOOL',
    '38089419',
    NULL,
    NULL,
    NULL,
    39.60,
    '32260361330805000198550010000007011100007010',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260361330805000198550010000007011100007010'
      AND descricao = 'ALCOOL LIQ 70 INPM PET 1L TUPI - 2143'
      AND ncm = '38089419'
      AND valor_total_nfe = 39.60
);

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
    'DRENO SUC. S/GAZE ESTERIL N.2 - WALTEX LOTE: EF09024 VALIDADE: 09/10/2026 FAB: 09/10/2024',
    'GAZES',
    '90183929',
    NULL,
    NULL,
    NULL,
    16.83,
    '35260343556958000176550010000043151004640326',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260343556958000176550010000043151004640326'
      AND descricao = 'DRENO SUC. S/GAZE ESTERIL N.2 - WALTEX LOTE: EF09024 VALIDADE: 09/10/2026 FAB: 09/10/2024'
      AND ncm = '90183929'
      AND valor_total_nfe = 16.83
);

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
    'DRENO SUC. S/GAZE ESTERIL N.2 - WALTEX LOTE: EM03326 VALIDADE: 03/03/2028 FAB: 03/03/2026',
    'GAZES',
    '90183929',
    NULL,
    NULL,
    NULL,
    289.17,
    '35260343556958000176550010000043151004640326',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260343556958000176550010000043151004640326'
      AND descricao = 'DRENO SUC. S/GAZE ESTERIL N.2 - WALTEX LOTE: EM03326 VALIDADE: 03/03/2028 FAB: 03/03/2026'
      AND ncm = '90183929'
      AND valor_total_nfe = 289.17
);

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
    'DRENO SUC. S/GAZE ESTERIL N.1 - WALTEX LOTE: EB20225 VALIDADE: 20/02/2027 FAB: 20/02/2025',
    'GAZES',
    '90183929',
    NULL,
    NULL,
    NULL,
    182.07,
    '35260343556958000176550010000043151004640326',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260343556958000176550010000043151004640326'
      AND descricao = 'DRENO SUC. S/GAZE ESTERIL N.1 - WALTEX LOTE: EB20225 VALIDADE: 20/02/2027 FAB: 20/02/2025'
      AND ncm = '90183929'
      AND valor_total_nfe = 182.07
);

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
    'DRENO SUC. S/GAZE ESTERIL N.1 - WALTEX LOTE: EA29126 VALIDADE: 29/01/2028 FAB: 29/01/2026',
    'GAZES',
    '90183929',
    NULL,
    NULL,
    NULL,
    32.13,
    '35260343556958000176550010000043151004640326',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260343556958000176550010000043151004640326'
      AND descricao = 'DRENO SUC. S/GAZE ESTERIL N.1 - WALTEX LOTE: EA29126 VALIDADE: 29/01/2028 FAB: 29/01/2026'
      AND ncm = '90183929'
      AND valor_total_nfe = 32.13
);

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
    'ALCOOL GEL 70% 800ML REFIL - CICLOFARMA',
    'ALCOOL',
    '38089429',
    NULL,
    NULL,
    NULL,
    4061.33,
    '32260336396059000198550010000248331762384310',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260336396059000198550010000248331762384310'
      AND descricao = 'ALCOOL GEL 70% 800ML REFIL - CICLOFARMA'
      AND ncm = '38089429'
      AND valor_total_nfe = 4061.33
);

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
    'LUVA CIRURGICA DE LATEX C/PO 7,5',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    2880.00,
    '53260352824013000189550010000007051000503891',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '53260352824013000189550010000007051000503891'
      AND descricao = 'LUVA CIRURGICA DE LATEX C/PO 7,5'
      AND ncm = '40151900'
      AND valor_total_nfe = 2880.00
);

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
    'LUVA CIRURGICA DE LATEX C/PO 8,0',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    1200.00,
    '53260352824013000189550010000007051000503891',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '53260352824013000189550010000007051000503891'
      AND descricao = 'LUVA CIRURGICA DE LATEX C/PO 8,0'
      AND ncm = '40151900'
      AND valor_total_nfe = 1200.00
);

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
    'LUVA P/ PROCEDIMENTO LATEX MEDIA',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    66.00,
    '35260301862347000106550010000014701100014701',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014701100014701'
      AND descricao = 'LUVA P/ PROCEDIMENTO LATEX MEDIA'
      AND ncm = '40151900'
      AND valor_total_nfe = 66.00
);

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
    'LUVA P/ PROCEDIMENTO LATEX PEQUENA',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    66.00,
    '35260301862347000106550010000014701100014701',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014701100014701'
      AND descricao = 'LUVA P/ PROCEDIMENTO LATEX PEQUENA'
      AND ncm = '40151900'
      AND valor_total_nfe = 66.00
);

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
    'LUVA P/ PROCEDIMENTO LATEX MEDIA',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    165.00,
    '35260301862347000106550010000014721100014722',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014721100014722'
      AND descricao = 'LUVA P/ PROCEDIMENTO LATEX MEDIA'
      AND ncm = '40151900'
      AND valor_total_nfe = 165.00
);

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
    'LUVA P/ PROCEDIMENTO LATEX PEQUENA',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    165.00,
    '35260301862347000106550010000014721100014722',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014721100014722'
      AND descricao = 'LUVA P/ PROCEDIMENTO LATEX PEQUENA'
      AND ncm = '40151900'
      AND valor_total_nfe = 165.00
);

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
    'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    540.00,
    '35260359309302000199550010001611261219913876',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260359309302000199550010001611261219913876'
      AND descricao = 'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)'
      AND ncm = '40151200'
      AND valor_total_nfe = 540.00
);

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
    'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    2160.00,
    '35260359309302000199550010001611261219913876',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260359309302000199550010001611261219913876'
      AND descricao = 'LUVA DE LATEX CIRURGICA ESTERIL TAMANHO 7,0 (CAIXA COM 50 PARES (100 UNID) CAIXA MASTER COM 500 PARES (1000 UNID)'
      AND ncm = '40151200'
      AND valor_total_nfe = 2160.00
);

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
    'LUVA CIRURGICA C/PO 7,5',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    2970.00,
    '32260333846912000138550010000111901789378710',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260333846912000138550010000111901789378710'
      AND descricao = 'LUVA CIRURGICA C/PO 7,5'
      AND ncm = '40151200'
      AND valor_total_nfe = 2970.00
);

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
    'LUVA CIRURGICA C/PO 7,0',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    990.00,
    '32260333846912000138550010000111901789378710',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260333846912000138550010000111901789378710'
      AND descricao = 'LUVA CIRURGICA C/PO 7,0'
      AND ncm = '40151200'
      AND valor_total_nfe = 990.00
);

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
    'DRENO SUC. S/GAZE ESTERIL N.3 - WALTEX LOTE: EA13624 VALIDADE: 13/06/2026 FAB: 13/06/2024',
    'GAZES',
    '90183929',
    NULL,
    NULL,
    NULL,
    299.25,
    '35260324774241000156550010000101111004640321',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260324774241000156550010000101111004640321'
      AND descricao = 'DRENO SUC. S/GAZE ESTERIL N.3 - WALTEX LOTE: EA13624 VALIDADE: 13/06/2026 FAB: 13/06/2024'
      AND ncm = '90183929'
      AND valor_total_nfe = 299.25
);

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
    'DRENO SUC. S/GAZE ESTERIL N.4 - WALTEX LOTE: TE01D25 VALIDADE: 01/12/2027 FAB: 01/12/2025',
    'GAZES',
    '90183929',
    NULL,
    NULL,
    NULL,
    264.00,
    '35260324774241000156550010000101111004640321',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260324774241000156550010000101111004640321'
      AND descricao = 'DRENO SUC. S/GAZE ESTERIL N.4 - WALTEX LOTE: TE01D25 VALIDADE: 01/12/2027 FAB: 01/12/2025'
      AND ncm = '90183929'
      AND valor_total_nfe = 264.00
);

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
    'DRENO SUC. S/GAZE ESTERIL N.3 - WALTEX LOTE: TF02D25 VALIDADE: 02/12/2027 FAB: 02/12/2025',
    'GAZES',
    '90183929',
    NULL,
    NULL,
    NULL,
    38.25,
    '35260324774241000156550010000101111004640321',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260324774241000156550010000101111004640321'
      AND descricao = 'DRENO SUC. S/GAZE ESTERIL N.3 - WALTEX LOTE: TF02D25 VALIDADE: 02/12/2027 FAB: 02/12/2025'
      AND ncm = '90183929'
      AND valor_total_nfe = 38.25
);

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
    'ALCOOL 70% ANTISSEPTICO 250ML FRASCO QTD. 1000.00 FR',
    'ALCOOL',
    '30039099',
    NULL,
    NULL,
    NULL,
    3144.58,
    '32260335997345000146550000001719451724412230',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260335997345000146550000001719451724412230'
      AND descricao = 'ALCOOL 70% ANTISSEPTICO 250ML FRASCO QTD. 1000.00 FR'
      AND ncm = '30039099'
      AND valor_total_nfe = 3144.58
);

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
    'ALCOOL 70% LIQUIDO 1L',
    'ALCOOL',
    '38089919',
    NULL,
    NULL,
    NULL,
    990.09,
    '32260332444895000140550010000170651338870650',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260332444895000140550010000170651338870650'
      AND descricao = 'ALCOOL 70% LIQUIDO 1L'
      AND ncm = '38089919'
      AND valor_total_nfe = 990.09
);

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
    'ALCOOL 70% ANTISSEPTICO 250ML FRASCO QTD. 1920.00 FR',
    'ALCOOL',
    '30039099',
    NULL,
    NULL,
    NULL,
    6037.60,
    '32260335997345000146550000001720631492059956',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260335997345000146550000001720631492059956'
      AND descricao = 'ALCOOL 70% ANTISSEPTICO 250ML FRASCO QTD. 1920.00 FR'
      AND ncm = '30039099'
      AND valor_total_nfe = 6037.60
);

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
    'ALCOOL 70% ANTISSEPTICO 250ML FRASCO QTD. 1200.00 FR',
    'ALCOOL',
    '30039099',
    NULL,
    NULL,
    NULL,
    3773.50,
    '32260335997345000146550000001720631492059956',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '32260335997345000146550000001720631492059956'
      AND descricao = 'ALCOOL 70% ANTISSEPTICO 250ML FRASCO QTD. 1200.00 FR'
      AND ncm = '30039099'
      AND valor_total_nfe = 3773.50
);

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
    'LUVA CIRURGICA 7.0 SENSITEX',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    6360.00,
    '35260301862347000106550010000014771100014770',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014771100014770'
      AND descricao = 'LUVA CIRURGICA 7.0 SENSITEX'
      AND ncm = '40151900'
      AND valor_total_nfe = 6360.00
);

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
    'LUVA CIRURGICA 6.0',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    2250.00,
    '35260301862347000106550010000014771100014770',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014771100014770'
      AND descricao = 'LUVA CIRURGICA 6.0'
      AND ncm = '40151900'
      AND valor_total_nfe = 2250.00
);

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
    'LUVA NITRILICA AZUL G VASCULAR',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    53600.00,
    '35260301862347000106550010000014771100014770',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014771100014770'
      AND descricao = 'LUVA NITRILICA AZUL G VASCULAR'
      AND ncm = '40151900'
      AND valor_total_nfe = 53600.00
);

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
    'LUVA P/ PROCEDIMENTO LATEX MEDIA',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    330.00,
    '35260301862347000106550010000014771100014770',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014771100014770'
      AND descricao = 'LUVA P/ PROCEDIMENTO LATEX MEDIA'
      AND ncm = '40151900'
      AND valor_total_nfe = 330.00
);

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
    'LUVA NITRILICA AZUL M VASCULAR',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    53600.00,
    '35260301862347000106550010000014771100014770',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014771100014770'
      AND descricao = 'LUVA NITRILICA AZUL M VASCULAR'
      AND ncm = '40151900'
      AND valor_total_nfe = 53600.00
);

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
    'LUVA CIRURGICA 7.5 SENSITEX',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    7000.00,
    '35260301862347000106550010000014771100014770',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014771100014770'
      AND descricao = 'LUVA CIRURGICA 7.5 SENSITEX'
      AND ncm = '40151900'
      AND valor_total_nfe = 7000.00
);

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
    'LUVA NITRILICA AZUL P VASCULAR',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    53600.00,
    '35260301862347000106550010000014771100014770',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014771100014770'
      AND descricao = 'LUVA NITRILICA AZUL P VASCULAR'
      AND ncm = '40151900'
      AND valor_total_nfe = 53600.00
);

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
    'LUVA P/ PROCEDIMENTO LATEX PEQUENA',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    330.00,
    '35260301862347000106550010000014771100014770',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014771100014770'
      AND descricao = 'LUVA P/ PROCEDIMENTO LATEX PEQUENA'
      AND ncm = '40151900'
      AND valor_total_nfe = 330.00
);

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
    'ALCOOL HOSPITALAR 70% 1000ML',
    'ALCOOL',
    '29121200',
    NULL,
    NULL,
    NULL,
    1467.60,
    '35260301862347000106550010000014771100014770',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014771100014770'
      AND descricao = 'ALCOOL HOSPITALAR 70% 1000ML'
      AND ncm = '29121200'
      AND valor_total_nfe = 1467.60
);

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
    'LUVA CIRURGICA 6.5 SENSITEX',
    'LUVAS',
    '40151900',
    NULL,
    NULL,
    NULL,
    4770.00,
    '35260301862347000106550010000014771100014770',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '35260301862347000106550010000014771100014770'
      AND descricao = 'LUVA CIRURGICA 6.5 SENSITEX'
      AND ncm = '40151900'
      AND valor_total_nfe = 4770.00
);

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
    'LUVA PROCEDIMENTO M NAO ESTERIL COM PO CX C/ 100 UND ABL',
    'LUVAS',
    '40151200',
    NULL,
    NULL,
    NULL,
    33420.00,
    '26260304614288000145550010000117001364965860',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM sefaz_produto_referencia
    WHERE chave_nfe = '26260304614288000145550010000117001364965860'
      AND descricao = 'LUVA PROCEDIMENTO M NAO ESTERIL COM PO CX C/ 100 UND ABL'
      AND ncm = '40151200'
      AND valor_total_nfe = 33420.00
);
