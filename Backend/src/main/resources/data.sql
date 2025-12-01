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
  ('user', 'user@gmail.com', '(11) 98888-8888', '$2a$10$Vh8y2vflQWGG/VEqjaC1yOfZQhoGYDEZruob3lngf3Rfnh7wwTH66', '1995-05-15', FALSE);

-- Inserindo orçamentos de exemplo para o usuário 'user' (id = 2)
INSERT INTO orcamento (codigo_orcamento, nome, email, ideia, valor, tamanho, cores, tempo, local_corpo, status, usuario_id) VALUES
  ('ORC001', 'João Silva', 'joao@email.com', 'Tatuagem de dragão no braço', 350.00, 15.5, 'Preto, Vermelho', '03:00:00', 'Braço direito', 'PENDENTE', 2),
  ('ORC002', 'Maria Santos', 'maria@email.com', 'Rosa pequena no punho', 150.00, 5.0,  'Vermelho, Preto', '01:30:00', 'Punho esquerdo', 'APROVADO', 2),
  ('ORC003', 'Pedro Oliveira', 'pedro@email.com', 'Mandala nas costas', 500.00, 25.0,  'Preto', '04:00:00', 'Costas', 'REJEITADO', 2),
  ('ORC004', 'Ana Costa', 'ana@email.com', 'Borboleta colorida', 200.00, 8.0,  'Vermelho, Preto', '02:00:00', 'Ombro', 'APROVADO', 2),
  ('ORC005', 'Carlos Mendes', 'carlos@email.com', 'Frase inspiracional', 180.00, 3.0,  'Preto', '01:00:00', 'Antebraço', 'PENDENTE', 2),
  ('ORC006', 'Luiza Pompermayer', 'luiza@email.com', 'Tatuagem minimalista', 120.00, 4.0,  'Preto', '01:15:00', 'Tornozelo', 'APROVADO', 2);

-- Inserindo imagens de referência para os orçamentos
INSERT INTO orcamento_imagens (orcamento_codigo, imagem_url) VALUES
  ('ORC001', 'https://i.pinimg.com/736x/60/90/60/609060f0c63bc1f05cfba42e06ceb5a8.jpg'),
  ('ORC001', 'https://i.pinimg.com/736x/ac/85/9a/ac859a1c2308be0aaa94b28d9e76ce28.jpg'),
  ('ORC002', 'https://i.pinimg.com/736x/94/99/d4/9499d4c2b1c34ead6a1f7e12c2bfbb3b.jpg'),
  ('ORC003', 'https://i.pinimg.com/736x/ab/61/93/ab6193c5c2b45d8a3bd6f39eb59e5e90.jpg'),
  ('ORC004', 'https://i.pinimg.com/736x/46/fb/db/46fbdba86147a4c0c4c9e8e1dc8ed4e6.jpg'),
  ('ORC005', 'https://i.pinimg.com/736x/d3/8f/94/d38f949c4b87d9f5ca97f0e01c31c8f2.jpg'),
  ('ORC006', 'https://i.pinimg.com/736x/2e/3e/4c/2e3e4c8e0b5f6f4b3a1f8e9d7c6b5a4d.jpg');

-- Inserindo agendamentos de exemplo para o usuário 'user' (id = 2)
-- Alguns para hoje (26/11/2025) e outros para 08/12/2025
INSERT INTO agendamento (data_hora, status, usuario_id, codigo_orcamento) VALUES
  -- Agendamentos para hoje (26/11/2025)
  ('2025-11-26 09:00:00', 'CONFIRMADO', 2, 'ORC002'), -- Rosa no punho - aprovada
  ('2025-11-26 14:30:00', 'PENDENTE', 2, 'ORC001'), -- Dragão no braço - pendente
  ('2025-11-26 16:00:00', 'CONCLUIDO', 2, 'ORC006'), -- Tatuagem minimalista - aprovada

  -- Agendamentos para 08/12/2025
  ('2025-12-08 10:00:00', 'CONFIRMADO', 2, 'ORC004'), -- Borboleta colorida - aprovada
  ('2025-12-08 13:00:00', 'PENDENTE', 2, 'ORC005'), -- Frase inspiracional - pendente
  ('2025-12-08 15:30:00', 'CANCELADO', 2, 'ORC003'); -- Dragão no braço
