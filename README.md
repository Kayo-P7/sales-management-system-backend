# Sales Management System Backend

API RESTful para gestão de vendas, usuários, marcas e veículos desenvolvida com Spring Boot, Spring Security (JWT), Spring Data JPA e PostgreSQL.

---

## 🛠️ Pré-requisitos

- **Java JDK 17** ou superior (recomendado JDK 21)
- **Docker** e **Docker Compose**
- **Maven** (opcional, wrapper `./mvnw` incluso no projeto)

---

## ⚙️ Configuração de Ambiente

Antes de iniciar, crie o arquivo de variáveis de ambiente a partir do modelo de exemplo:

```bash
cp .env.example .env
```

Edite o arquivo `.env` para ajustar portas, credenciais do banco ou segredo JWT, caso necessário.

---

## 🚀 Como Executar

### 1. Execução com Docker (Aplicação + Banco)

Sobe toda a infraestrutura em containers conectados por rede interna:

```bash
# constroi as imagens e inicia os containers em segundo plano
docker compose up -d --build

# visualiza os logs da aplicacao
docker compose logs -f backend

# encerra os containers
docker compose down
```

A API estará acessível em `http://localhost:8089`.

---

### 2. Execução sem Docker (Desenvolvimento Local)

Para rodar o backend localmente na sua máquina:

1. **Inicie o banco de dados PostgreSQL via Docker Compose**:
   ```bash
   docker compose up -d database
   ```
   *(Caso já possua um PostgreSQL instalado localmente, garanta que ele esteja rodando na porta informada no `.env`)*

2. **Inicie a aplicação utilizando o Maven Wrapper**:
   ```bash
   ./mvnw spring-boot:run
   ```

A API estará acessível em `http://localhost:8089`.

---

## 📚 Documentação Interativa (Swagger / OpenAPI)

A API possui documentação interativa completa gerada via Springdoc OpenAPI:

- **Swagger UI**: [http://localhost:8089/swagger-ui.html](http://localhost:8089/swagger-ui.html)
- **Especificação OpenAPI JSON**: [http://localhost:8089/v3/api-docs](http://localhost:8089/v3/api-docs)

### Autenticação no Swagger UI:
1. Execute o endpoint `POST /api/v1/auth/login` informando suas credenciais.
2. Copie o token retornado no campo `token`.
3. No topo da página do Swagger UI, clique no botão **Authorize** (com ícone de cadeado).
4. No campo de valor, insira o token obtido e clique em **Authorize**.
5. As requisições subsequentes aos endpoints protegidos (`/api/v1/users`, `/api/v1/brands`, etc.) enviarão o cabeçalho `Authorization: Bearer <token>` automaticamente.

---

## 🧪 Testes Automatizados

Para executar os testes unitários e de integração com banco em memória (H2):

```bash
./mvnw clean test
```

---

## 📖 Endpoints Úteis

- **Verificação de Saúde (Health Check)**: `GET http://localhost:8089/health`
- **Documentação Swagger UI**: `http://localhost:8089/swagger-ui.html`
- **Especificação OpenAPI**: `http://localhost:8089/v3/api-docs`
- **Login**: `POST http://localhost:8089/api/v1/auth/login`
- **Registro de Usuário**: `POST http://localhost:8089/api/v1/auth/register`
- **Gerenciamento de Usuários**: `http://localhost:8089/api/v1/users`
- **Gerenciamento de Marcas**: `http://localhost:8089/api/v1/brands`
