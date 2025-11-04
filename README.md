<h1 align="center">Studio Tattoo - Backend</h1>

API REST para gerenciamento de um estúdio de tatuagem, desenvolvida em Java com Spring Boot. Fornece endpoints para agendamento, orçamento, cadastro de clientes/usuários e controle de estoque de materiais.

## ✨ Funcionalidades

- **Sistema de agendamento**: Endpoints para criação, consulta e gerenciamento de horários
- **Sistema de orçamento**: Criação e cálculo automático de orçamentos personalizados
- **Gerenciamento de usuários**: CRUD completo para clientes, tatuadores e funcionários
- **Controle de estoque**: API para gerenciamento de materiais e suprimentos
- **Autenticação e autorização**: Sistema de login e controle de acesso

## 🛠️ Tecnologias Utilizadas

- **Java 21**: Linguagem de programação
- **Spring Boot**: Framework principal
- **Spring Data JPA**: Persistência de dados
- **Spring Security**: Autenticação e autorização
- **H2 Database**: Banco de dados em memória para desenvolvimento
- **Maven**: Gerenciamento de dependências
- **Bean Validation**: Validação de dados

## 📁 Estrutura do Projeto

```
src/main/java/
├── controller/     # Controladores REST
├── model/         # Entidades JPA
├── repository/    # Repositórios de dados
├── service/       # Lógica de negócio
├── dto/           # Data Transfer Objects
├── config/        # Configurações
└── exception/     # Tratamento de exceções
```

## 🗄️ Banco de Dados

O projeto utiliza H2 Database (banco em memória) com as seguintes tabelas principais:
- `usuarios` - Dados dos usuários do sistema
- `clientes` - Informações dos clientes
- `agendamentos` - Horários agendados
- `orcamentos` - Orçamentos gerados
- `materiais` - Estoque de materiais
- `tatuadores` - Dados dos profissionais

O console do H2 está disponível em `http://localhost:8080/h2-console` durante o desenvolvimento.

## 🚀 Como Executar o Projeto

### Pré-requisitos
- Java 21 ou superior
- Maven 3.6 ou superior

### Configuração das Variáveis de Ambiente

Crie um arquivo `.env` na pasta `Back-end/` com as seguintes variáveis:

```env
# Configuração do Banco H2
DB_USER=orcana-adm
DB_PASSWORD=
DB_URL=jdbc:h2:file:./data/orcana-db;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE

# Configuração do Brevo/SendinBlue
BREVO_USER=seu_email@smtp-brevo.com
BREVO_PASSWORD=sua_senha_smtp

# Configuração JWT
JWT_SECRET=sua_chave_secreta_jwt
JWT_VALIDITY=3600000
```

### Instalação e Execução

```bash
# Clonar o repositório (se necessário)
git clone <url-do-repositorio>

# Navegar até o diretório do projeto
cd studio-tattoo-backend

# Instalar dependências
mvn clean install

# Executar a aplicação
mvn spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

### Acesso ao Banco H2

Durante o desenvolvimento, você pode acessar o console do H2:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:./data/orcana-db`
- Username: `orcana-adm`
- Password: (deixar em branco)

## 📋 Endpoints Principais

### Autenticação
- `POST /usuario/login` - Login de usuário
- `POST /usuario/cadastro` - Registro de novo usuário

### Usuários
- `GET /usuario` - Listar usuários
- `POST /usuario` - Criar usuário
- `PUT /usuario/{id}` - Atualizar usuário
- `DELETE /usuario/{id}` - Excluir usuário

### Agendamentos
- `GET /agendamento` - Listar agendamento
- `POST /agendamento` - Criar agendamento
- `PUT /agendamento/{id}` - Atualizar agendamento
- `DELETE /agendamento/{id}` - Cancelar agendamento

### Orçamentos
- `GET /orcamento` - Listar orçamentos
- `POST /orcamento` - Criar orçamento
- `GET /orcamento/{id}` - Buscar orçamento por ID

### Materiais
- `GET /estoque` - Listar materiais
- `POST /estoque` - Adicionar material
- `PUT /estoque/{id}` - Atualizar material
- `DELETE /estoque/{id}` - Remover material

## ⚙️ Configuração

### Configuração do application.properties

O projeto utiliza variáveis de ambiente definidas no arquivo `.env` para configuração:

```properties
# Configuração do Banco H2
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Console H2 habilitado para desenvolvimento
spring.h2.console.enabled=true

# Configuração de E-mail (Brevo/SendinBlue)
spring.mail.host=smtp-relay.brevo.com
spring.mail.port=587
spring.mail.username=${BREVO_USER}
spring.mail.password=${BREVO_PASSWORD}

# Configuração JWT
orcana.jwt.secret=${JWT_SECRET}
orcana.jwt.validity=${JWT_VALIDITY}
```

### Principais Configurações

- **Banco de Dados**: H2 em arquivo persistente
- **Autenticação**: JWT com validade configurável
- **E-mail**: Integração com Brevo para envio de notificações

## 👥 Integrantes

- Luiza Vicente Pompermayer
- Linya Alves Mendonça
- Kawan Fritoli Gomes
- Nicollas Bispo Pereira
- Rayra Ferreira Galvão
- Viviane dos Santos

## 📄 Licença

Este projeto está licenciado sob a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International (CC BY-NC-SA 4.0).