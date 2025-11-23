-- Adiciona coluna de status na tabela orcamento se não existir
ALTER TABLE orcamento 
ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'AGUARDANDO_RESPOSTA';

-- Atualiza registros existentes para ter o status padrão
UPDATE orcamento 
SET status = 'AGUARDANDO_RESPOSTA' 
WHERE status IS NULL;
