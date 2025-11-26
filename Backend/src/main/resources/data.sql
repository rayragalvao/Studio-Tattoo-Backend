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

-- INSERT INTO usuario (nome, email, telefone, senha, dt_nasc, is_admin) VALUES
  ('admin', 'admin@gmail.com', '(11) 99999-9999', '$2a$10$mLZ3Uq7mYdoZZuO8OnBGmeeZ5eMG2sslJiJunOtylsjr.yU9jw1bC', '1990-01-01', TRUE),
  ('user', 'user@gmail.com', '(11) 98888-8888', '$2a$10$Vh8y2vflQWGG/VEqjaC1yOfZQhoGYDEZruob3lngf3Rfnh7wwTH66', '1995-05-15', FALSE);

-- Inserindo orçamentos de exemplo para o usuário 'user' (id = 2)
INSERT INTO orcamento (codigo_orcamento, nome, email, ideia, valor, tamanho, estilo, cores, tempo, local_corpo, status, usuario_id) VALUES
  ('ORC001', 'João Silva', 'joao@email.com', 'Tatuagem de dragão no braço', 350.00, 15.5, 'Realismo', 'Preto e cinza', '03:00:00', 'Braço direito', 'PENDENTE', 2),
  ('ORC002', 'Maria Santos', 'maria@email.com', 'Rosa pequena no punho', 150.00, 5.0, 'Old School', 'Vermelho e verde', '01:30:00', 'Punho esquerdo', 'APROVADO', 2),
  ('ORC003', 'Pedro Oliveira', 'pedro@email.com', 'Mandala nas costas', 500.00, 25.0, 'Geométrico', 'Preto', '04:00:00', 'Costas', 'REJEITADO', 2),
  ('ORC004', 'Ana Costa', 'ana@email.com', 'Borboleta colorida', 200.00, 8.0, 'Aquarela', 'Azul, rosa, amarelo', '02:00:00', 'Ombro', 'APROVADO', 2),
  ('ORC005', 'Carlos Mendes', 'carlos@email.com', 'Frase inspiracional', 180.00, 3.0, 'Lettering', 'Preto', '01:00:00', 'Antebraço', 'PENDENTE', 2),
  ('ORC006', 'Luiza Pompermayer', 'luiza@email.com', 'Tatuagem minimalista', 120.00, 4.0, 'Minimalista', 'Preto', '01:15:00', 'Tornozelo', 'APROVADO', 2);

-- Inserindo agendamentos de exemplo para o usuário 'user' (id = 2)
-- Alguns para hoje (26/11/2025) e outros para 08/12/2025
INSERT INTO agendamento (data_hora, status, usuario_id, orcamento_id) VALUES
  -- Agendamentos para hoje (26/11/2025)
  ('2025-11-26 09:00:00', 'CONFIRMADO', 2, 2), -- Rosa no punho - aprovada
  ('2025-11-26 14:30:00', 'AGUARDANDO', 2, 1), -- Dragão no braço - pendente
  ('2025-11-26 16:00:00', 'CONCLUIDO', 2, 6), -- Tatuagem minimalista - aprovada

  -- Agendamentos para 08/12/2025
  ('2025-12-08 10:00:00', 'CONFIRMADO', 2, 4), -- Borboleta colorida - aprovada
  ('2025-12-08 13:00:00', 'PENDENTE', 2, 5), -- Frase inspiracional - pendente
  ('2025-12-08 15:30:00', 'CANCELADO', 2, 3), -- Mandala - rejeitada

  -- Outros agendamentos em datas variadas
  ('2025-12-01 11:00:00', 'AGUARDANDO', 2, 1); -- Dragão no braço
