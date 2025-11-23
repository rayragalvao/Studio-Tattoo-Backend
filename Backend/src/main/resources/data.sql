-- Inserindo dados iniciais na tabela estoque
INSERT INTO estoque (nome, quantidade, unidade_medida, min_aviso) VALUES
    ('Luvas', 1, 'unidades', 10),
    ('Vaselina', 150, 'g', NULL),
    ('Tinta preta', 30, 'ml', NULL),
    ('Tinta vermelha', 30, 'ml', NULL),
    ('Transfer', 30, 'ml', NULL),
    ('Solvente', 60, 'ml', NULL),
    ('Papel toalha', 4, 'rolos', NULL),
    ('Plástico em gel', 60, 'g', NULL),
    ('Cartuchos', 5, 'unidades', NULL),
    ('Batoque', 100, 'unidades', NULL),
    ('Palito de sorvete', 100, 'unidades', NULL),
    ('Lâmina de Barbear', 7, 'unidades', NULL),
    ('Álcool 70', 1, 'litros', NULL),
    ('Plástico filme', 2, 'rolos', NULL),
    ('Sabonete líquido neutro', 180, 'ml', NULL),
    ('Papel hectográfico', 100, 'folhas', NULL),
    ('Máscara descartável', 50, 'unidades', NULL),
    ('Papel higiênico', 8, 'unidades', NULL),
    ('Água mineral', 4, 'litros', NULL),
    ('Sabonete higienizador para as mãos', 200, 'ml', NULL);

INSERT INTO usuario (nome, email, telefone, senha, dt_nasc, is_admin) VALUES
  ('admin', 'admin@gmail.com', '(11) 99999-9999', '$2a$10$mLZ3Uq7mYdoZZuO8OnBGmeeZ5eMG2sslJiJunOtylsjr.yU9jw1bC', '1990-01-01', TRUE),
  ('user', 'user@gmail.com', '(11) 98888-8888', '$2a$10$Vh8y2vflQWGG/VEqjaC1yOfZQhoGYDEZruob3lngf3Rfnh7wwTH66', '1995-05-15', FALSE)