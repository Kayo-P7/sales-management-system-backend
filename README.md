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

## 🛡️ Controle de Taxa (Rate Limiting)

A API conta com proteção contra abuso e requisições excessivas implementada via algoritmo **Token Bucket** (Bucket4j):

- **Rotas de Autenticação (`/api/v1/auth/**`)**: limite restrito para prevenção de força bruta (padrão: 10 requisições por minuto por IP).
- **Rotas Gerais (`/api/v1/**`)**: limite de uso da API (padrão: 60 requisições por minuto por IP).
- **Exclusões**: endpoints de documentação (`/swagger-ui/**`, `/v3/api-docs/**`) e integridade (`/health`) não sofrem restrição de taxa.

### Cabeçalhos de Resposta:
- `X-Rate-Limit-Remaining`: tokens restantes na janela atual.
- `Retry-After`: tempo em segundos até a recarga de tokens caso o limite seja atingido.
- `X-Rate-Limit-Retry-After-Seconds`: tempo de espera em segundos.

Caso a cota seja ultrapassada, o servidor responde com status **HTTP 429 Too Many Requests** e corpo de erro padronizado (`ErroResponse`).

---

## 🗄️ Versionamento de Banco de Dados com Flyway

O projeto utiliza o **Flyway** para gerenciamento profissional, rastreável e automatizado das evoluções do esquema do banco de dados PostgreSQL.

### 1. Localização e Convenção de Nomenclatura
As migrations residem em:
```
src/main/resources/db/migration/
```

Seguem o padrão canônico do Flyway:
- `V<VERSAO>__<descricao_em_snake_case>.sql`
- Exemplo: `V1__create_initial_schema.sql`, `V2__add_new_column_to_users.sql`

> **Regras Fundamentais**:
> - Migrations versionadas com prefixo `V` são imutáveis após aplicadas em ambientes compartilhados.
> - O separador entre a versão e a descrição é composto por **dois sublinhados** (`__`).
> - A descrição deve ser concisa e refletir a responsabilidade da alteração.

### 2. Como Criar uma Nova Migration
1. Crie o arquivo `V<PROXIMA_VERSAO>__<nome_da_alteracao>.sql` no diretório `src/main/resources/db/migration/`.
2. Escreva o SQL declarativo compatível com PostgreSQL (tabelas, índices, constraints).
3. Atualize ou crie as entidades JPA correspondentes, garantindo alinhamento com a validação do Hibernate (`ddl-auto: validate`).
4. Execute os testes automatizados para validar a consistência da aplicação.

### 3. Adoção em Bancos de Dados Existentes (Baseline)
Para bases que já contêm tabelas prévias, o Flyway está parametrizado com suporte a baseline seguro:
- `FLYWAY_BASELINE_ON_MIGRATE=true`: se o banco não estiver vazio e não possuir o histórico, cria `flyway_schema_history` e marca a versão 1 como linha de base sem recriar tabelas existentes.
- Em bancos novos (instalações limpas), o Flyway executa todas as migrations a partir da `V1`.

### 4. Resolução de Falhas em Migrations
Caso uma migration falhe durante a inicialização:
1. Analise o log de erro para identificar o comando SQL causador do problema.
2. Em ambiente de desenvolvimento, reverta o estado do banco afetado para um ponto consistente.
3. Corrija o script SQL ou execute a intervenção manual necessária.
4. Execute o comando de reparo do histórico se uma falha tiver sido registrada:
   ```bash
   ./mvnw flyway:repair
   ```
5. Suba a aplicação novamente para reprocessar a migration corrigida.

### 5. Comandos Úteis do Maven Flyway
- **Verificar status das migrations**:
  ```bash
  ./mvnw flyway:info -Dflyway.url=jdbc:postgresql://localhost:5439/sales_management -Dflyway.user=postgres -Dflyway.password=postgres
  ```
- **Executar migrações manualmente**:
  ```bash
  ./mvnw flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5439/sales_management -Dflyway.user=postgres -Dflyway.password=postgres
  ```

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
