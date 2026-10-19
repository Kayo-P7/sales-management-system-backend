# Sales Management System Backend

API RESTful para gerenciamento de vendas, desenvolvida com Java 17, Spring Boot, Spring Security, JWT, Spring Data JPA, PostgreSQL e Flyway.

A aplicação oferece autenticação e autorização de usuários, gerenciamento de equipes, clientes, veículos e marcas, auditoria de registros e monitoramento operacional.

## Sumário

- [Tecnologias](#tecnologias)
- [Funcionalidades](#funcionalidades)
- [Pré-requisitos](#pré-requisitos)
- [Configuração do ambiente](#configuração-do-ambiente)
- [Como executar](#como-executar)
- [Documentação Swagger / OpenAPI](#documentação-swagger--openapi)
- [Autenticação e autorização](#autenticação-e-autorização)
- [Gerenciamento de usuários e equipes](#gerenciamento-de-usuários-e-equipes)
- [Gerenciamento de clientes](#gerenciamento-de-clientes)
- [Gerenciamento de veículos e marcas](#gerenciamento-de-veículos-e-marcas)
- [Versionamento do banco de dados](#versionamento-do-banco-de-dados)
- [Auditoria](#auditoria)
- [Segurança e limitação de requisições](#segurança-e-limitação-de-requisições)
- [Observabilidade e monitoramento](#observabilidade-e-monitoramento)
- [Refresh Tokens](#refresh-tokens)
- [Testes](#testes)
- [Endpoints úteis](#endpoints-úteis)

---

## Tecnologias

| Tecnologia | Finalidade |
|---|---|
| Java 17 | Linguagem de programação |
| Spring Boot | Framework principal da aplicação |
| Spring Security | Autenticação e autorização |
| JWT | Autenticação por token |
| Spring Data JPA | Persistência e acesso aos dados |
| Hibernate | Mapeamento objeto-relacional |
| PostgreSQL | Banco de dados relacional |
| Flyway | Versionamento do esquema do banco |
| MapStruct | Mapeamento entre DTOs e entidades |
| Springdoc OpenAPI | Documentação da API |
| Bucket4j | Limitação de requisições |
| Spring Boot Actuator | Verificação de saúde e métricas |
| Micrometer | Coleta de métricas |
| SLF4J MDC | Rastreamento de requisições nos logs |
| Maven | Gerenciamento de dependências e build |
| Docker Compose | Execução dos serviços em containers |

## Funcionalidades

### Autenticação e usuários

- Cadastro e autenticação de usuários.
- Autenticação por JWT.
- Controle de acesso baseado em papéis.
- Renovação de sessão por refresh tokens.
- Rotação e revogação de refresh tokens.
- Controle do estado dos usuários.
- Armazenamento de senhas utilizando o mecanismo de codificação configurado na aplicação.

### Gerenciamento de equipes

- Criação de equipes por supervisores autenticados.
- Associação de vendedores às equipes.
- Validação do papel do usuário antes de associá-lo como vendedor.
- Verificação de que o vendedor não pertence a outra equipe.
- Prevenção da criação de múltiplas equipes pelo mesmo supervisor.
- Consulta de equipe a partir do ID de um vendedor.
- Consulta da equipe do supervisor autenticado.
- Remoção de vendedores de uma equipe sem excluir suas contas.
- Relacionamento entre usuários, supervisores, equipes e vendedores.

### Gerenciamento de clientes

- Cadastro e atualização de clientes.
- Consulta e pesquisa de clientes.
- Paginação nas operações de listagem que oferecem esse recurso.
- Atualização parcial de dados por meio de PATCH.
- Registro automático de datas de criação e modificação.
- Auditoria dos usuários responsáveis pela criação e atualização dos registros.
- Operações protegidas por autenticação.

As regras de negócio preveem que vendedores gerenciem seus próprios clientes, supervisores acessem os clientes dentro de suas equipes e administradores tenham acesso administrativo mais amplo.

Essas permissões devem ser aplicadas pelo backend, por meio das regras de negócio e das consultas ao banco, e não somente pela interface do frontend.

### Gerenciamento de veículos e marcas

- Cadastro, consulta e listagem de veículos.
- Pesquisa e filtragem de veículos.
- Atualização completa ou parcial.
- Exclusão de veículos por ID ou nome, conforme as rotas configuradas.
- Associação entre veículos e marcas cadastradas.
- Representação de cor e tipo por enums.
- Utilização de `BigDecimal` para valores monetários.

### Recursos operacionais

- Versionamento do esquema do banco com Flyway.
- Identificação e rastreamento de requisições por Correlation ID.
- Limitação de requisições para reduzir abusos da API.
- Verificação de saúde da aplicação.
- Métricas operacionais por meio do Actuator e Micrometer.
- Documentação interativa com Swagger UI.

---

## Pré-requisitos

- Java JDK 17 ou superior.
- Docker e Docker Compose para executar o ambiente em containers.
- Maven, caso não utilize o Maven Wrapper incluído no projeto.
- PostgreSQL, caso opte por executar o banco fora do Docker.

## Configuração do ambiente

Crie o arquivo de variáveis de ambiente a partir do modelo:

```bash
cp .env.example .env
```

Edite o arquivo `.env` para configurar as credenciais do banco, as portas, o segredo JWT e as origens permitidas para requisições cross-origin (`CORS_ALLOWED_ORIGINS`), de acordo com as variáveis utilizadas pela aplicação.

**Não envie credenciais reais, senhas ou segredos JWT para o repositório.** O arquivo `.env` deve permanecer fora do controle de versão quando contiver informações sensíveis.

Confira se as variáveis utilizadas pelo Docker Compose correspondem às configurações do Spring Boot.

## Como executar

### 1. Execução com Docker Compose

Construa as imagens e inicie os serviços:

```bash
docker compose up -d --build
```

Acompanhe os logs do backend:

```bash
docker compose logs -f backend
```

Encerre os serviços:

```bash
docker compose down
```

A API estará disponível na porta configurada no Docker Compose e no arquivo de ambiente.

### 2. Execução local

Inicie o PostgreSQL:

```bash
docker compose up -d database
```

Execute a aplicação com o Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Para iniciar a aplicação sem executar os testes primeiro:

```bash
./mvnw spring-boot:run -DskipTests
```

Esse comando ignora a execução dos testes, mas ainda realiza a compilação do código.

Verifique a porta configurada na aplicação antes de acessar a API.

---

## Documentação Swagger / OpenAPI

A API utiliza Springdoc OpenAPI para gerar a documentação interativa dos endpoints.

Em uma execução local na porta `8080`, os endereços são:

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **Especificação OpenAPI em JSON:** http://localhost:8080/v3/api-docs

Caso a aplicação utilize outra porta, substitua `8080` pela porta configurada.

### Como autenticar no Swagger

1. Execute `POST /api/v1/auth/login` com as credenciais de um usuário cadastrado.
2. Copie o token de acesso retornado.
3. Clique em **Authorize** no topo do Swagger UI.
4. Informe o token conforme o esquema de autenticação configurado.
5. Execute os endpoints protegidos.

Quando a autenticação estiver configurada como HTTP Bearer, normalmente basta informar o token, sem acrescentar manualmente o prefixo `Bearer`.

---

## Autenticação e autorização

A aplicação utiliza Spring Security e JWT para autenticar as requisições.

As chamadas protegidas devem incluir o token no cabeçalho HTTP:

```http
Authorization: Bearer <access_token>
```

O filtro de autenticação valida o token, identifica o usuário e estabelece o contexto de segurança utilizado durante a requisição.

### Papéis de acesso

| Papel | Responsabilidade |
|---|---|
| `ROLE_ADMIN` | Operações administrativas e gerenciamento geral do sistema |
| `ROLE_SUPERVISOR` | Gerenciamento de equipes e acesso aos dados autorizados da equipe |
| `ROLE_VENDEDOR` | Operações de vendas e acesso aos clientes autorizados |

As permissões de acesso aos endpoints podem ser controladas por anotações como `@PreAuthorize`.

A autorização no controller não substitui a validação de acesso aos registros. O serviço e o repositório também precisam garantir, por exemplo, que um supervisor não altere dados de vendedores de outra equipe.

---

## Gerenciamento de usuários e equipes

### Modelo de dados

O gerenciamento de equipes utiliza três conceitos principais:

- **Usuário (`User`):** representa um administrador, supervisor ou vendedor.
- **Equipe (`Equipe`):** armazena o nome da equipe e seu supervisor.
- **Vínculo do vendedor:** associa o vendedor a uma equipe.

Cada equipe possui um supervisor, e cada supervisor pode gerenciar apenas uma equipe. Um vendedor pode pertencer a, no máximo, uma equipe, enquanto uma equipe pode conter vários vendedores.

Os usuários mantêm IDs numéricos globais e únicos no banco de dados. Identificadores visuais, como `Vendedor 001`, devem ser tratados separadamente da chave primária.

### Endpoints de equipes

Prefixo: `/api/v1/equipes`

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/equipes` | Cadastra uma equipe e associa vendedores |
| `GET` | `/api/v1/equipes` | Consulta a equipe do supervisor autenticado |
| `GET` | `/api/v1/equipes/vendedores/{id}` | Consulta a equipe a partir do ID de um vendedor |
| `DELETE` | `/api/v1/equipes/vendedores/{id}` | Remove um vendedor da equipe |

A autorização efetiva e os códigos de resposta devem corresponder à implementação atual do controller e do serviço. Consulte o Swagger para verificar os detalhes de cada operação.

### Exemplo de cadastro de equipe

Requisição para `POST /api/v1/equipes`:

```json
{
  "nome": "Equipe Norte",
  "vendedores": [3, 4]
}
```

Os IDs devem corresponder a usuários existentes com o papel `ROLE_VENDEDOR` que ainda não estejam associados a outra equipe.

O serviço busca os usuários no banco, valida seus papéis e vínculos e realiza as associações. A resposta contém os dados da equipe no formato `EquipeResponse`.

O cadastro utiliza uma transação para permitir que as alterações sejam revertidas caso uma exceção provoque rollback.

### Remoção de vendedor

Exemplo de requisição:

```http
DELETE /api/v1/equipes/vendedores/3
```

A operação remove a associação do vendedor com a equipe, mas não exclui a conta do usuário.

Antes de realizar a alteração, o serviço deve verificar se o vendedor pertence à equipe do supervisor autenticado.

---

## Gerenciamento de clientes

As operações de clientes são organizadas por meio de controller, service, repository, DTOs de requisição e resposta e mapper com MapStruct.

### DTOs e mapeamento

O DTO de requisição de cliente contempla campos como:

- `name`
- `telephone`
- `email`
- `notes`

O mapper separa os dados expostos pela API da entidade persistida. No cadastro e nas atualizações, os campos de ID e auditoria são ignorados no mapeamento normal, evitando que o cliente da API controle diretamente esses valores.

O mapeamento de PATCH atualiza os campos explicitamente configurados, preservando valores que não foram informados.

### Pesquisa e paginação

O repositório de clientes inclui consultas baseadas no criador, nome e telefone, como:

- Busca por ID do cliente e ID do criador.
- Pesquisa por parte do telefone, considerando o criador.
- Listagem paginada de clientes por criador.
- Pesquisa por nome ignorando diferenças entre letras maiúsculas e minúsculas.

Essas consultas fazem parte da implementação de acesso aos clientes. A regra final deve distinguir o usuário que criou um cliente do vendedor responsável pelo atendimento quando esses papéis forem diferentes.

Consulte o Swagger UI para conferir os caminhos exatos, parâmetros e formatos de resposta dos endpoints de clientes.

---

## Gerenciamento de veículos e marcas

A API disponibiliza operações para gerenciamento de veículos e marcas.

### Campos do veículo

| Campo | Descrição |
|---|---|
| `name` | Nome do veículo |
| `color` | Cor representada pelo enum `VehicleColor` |
| `vehicleType` | Tipo representado pelo enum `VehicleType` |
| `price` | Preço representado por `BigDecimal` |
| `brandId` | ID de uma marca já cadastrada |

### Exemplo de cadastro

```http
POST /api/v1/vehicles
Content-Type: application/json
Authorization: Bearer <access_token>
```

```json
{
  "name": "Corolla",
  "color": "PRETO",
  "vehicleType": "CAR",
  "price": 120000.00,
  "brandId": 1
}
```

Utilize valores existentes nos enums (`VehicleColor`: `PRETO`, `BRANCO`, `PRATA`, `VERMELHO`, `AZUL`, `CINZA`, `VERDE`; `VehicleType`: `CAR`, `MOTO`) e um `brandId` válido.

As rotas e restrições de acesso de veículos e marcas são definidas pelos respectivos controllers e podem ser consultadas no Swagger UI.

---

## Versionamento do banco de dados

O projeto utiliza Flyway para gerenciar as alterações do esquema do PostgreSQL de maneira versionada e rastreável.

Os arquivos de migration ficam em:

```text
src/main/resources/db/migration/
```

A nomenclatura segue o padrão:

```text
V<VERSAO>__<descricao>.sql
```

Exemplos:

```text
V1__create_initial_schema.sql
V2__add_new_column_to_users.sql
V4__create_equipe_and_update_users.sql
```

Os nomes acima são exemplos de convenção. As migrations efetivamente presentes no repositório definem as versões utilizadas pelo projeto.

### Como criar uma migration

1. Identifique a próxima versão ainda não utilizada.
2. Crie um novo arquivo SQL no diretório de migrations.
3. Adicione as alterações necessárias, incluindo tabelas, índices, restrições e chaves estrangeiras.
4. Atualize as entidades JPA correspondentes.
5. Confira a compatibilidade com a validação do esquema do Hibernate.
6. Execute a aplicação e os testes em um banco de desenvolvimento.

**Não altere migrations que já foram aplicadas em ambientes compartilhados.** Crie uma nova migration para realizar alterações posteriores.

### Comandos úteis

Inicie a aplicação para que o Flyway processe as migrations pendentes:

```bash
./mvnw spring-boot:run
```

Execute os testes automatizados:

```bash
./mvnw clean test
```

Os comandos específicos do Maven Flyway dependem da configuração do plugin e das propriedades de conexão com o banco.

---

## Auditoria

As entidades de clientes utilizam a auditoria do Spring Data JPA para registrar a criação e a última modificação dos registros.

| Campo | Finalidade |
|---|---|
| `createdAt` | Data e hora da criação |
| `updatedAt` | Data e hora da última modificação |
| `createdBy` | ID do usuário que criou o registro |
| `updatedBy` | ID do usuário que modificou o registro pela última vez |

A implementação utiliza anotações como:

```java
@CreatedDate
private LocalDateTime createdAt;

@LastModifiedDate
private LocalDateTime updatedAt;

@CreatedBy
private Long createdBy;

@LastModifiedBy
private Long updatedBy;
```

As entidades são configuradas com `AuditingEntityListener`, e a auditoria é habilitada na aplicação.

Uma implementação de `AuditorAware<Long>` obtém o usuário autenticado no contexto do Spring Security para identificar quem executou a operação.

Os campos de auditoria são gerenciados pelo backend e não devem ser controlados diretamente pelo usuário nas requisições normais de cadastro e atualização.

---

## Segurança e limitação de requisições

### JWT e permissões

Os endpoints protegidos exigem autenticação. As anotações de autorização limitam o acesso de acordo com o papel do usuário.

As regras de negócio também devem verificar o acesso aos registros, especialmente nas operações de equipes e clientes.

### Rate Limiting

A aplicação possui um filtro de limitação de requisições utilizando Bucket4j e o algoritmo Token Bucket.

A configuração original documenta os seguintes limites padrão:

- Rotas de autenticação: 10 requisições por minuto por IP.
- Rotas gerais da API: 60 requisições por minuto por IP.

Os valores efetivos dependem da configuração ativa e devem ser verificados antes da implantação.

A configuração original exclui rotas da documentação e o endpoint de saúde da limitação de requisições.

Quando o limite é excedido, a aplicação pode retornar `429 Too Many Requests`.

Cabeçalhos relevantes:

- `X-Rate-Limit-Remaining`
- `Retry-After`
- `X-Rate-Limit-Retry-After-Seconds`

---

## Observabilidade e monitoramento

### Correlation ID

O `CorrelationIdFilter` associa um identificador às requisições HTTP.

- Um cabeçalho `X-Correlation-ID` enviado pelo cliente é preservado quando suportado pela configuração.
- Se o cabeçalho não existir, um UUID será gerado.
- O identificador é incluído no cabeçalho da resposta.
- O SLF4J MDC adiciona o identificador aos logs relevantes.
- O contexto MDC é limpo ao término do processamento da requisição.

Esse mecanismo facilita a identificação dos logs relacionados a uma mesma chamada da API.

### Spring Boot Actuator e Micrometer

A aplicação disponibiliza endpoints do Actuator e métricas do Micrometer.

Endpoints úteis:

```http
GET /actuator/health
GET /actuator/metrics
```

Dependendo da configuração de segurança, informações básicas de saúde podem ser públicas, enquanto métricas operacionais exigem autenticação administrativa.

As métricas podem incluir:

- Memória e threads da JVM.
- Quantidade e latência de requisições HTTP.
- Métricas do pool de conexões HikariCP.
- Contadores de requisições permitidas e rejeitadas pelo rate limiting.

Informações operacionais sensíveis devem permanecer acessíveis apenas a usuários autorizados.

---

## Refresh Tokens

A autenticação contempla renovação e revogação de sessões por meio de refresh tokens.

### Login

```http
POST /api/v1/auth/login
```

Retorna um JWT de acesso e, conforme a implementação de autenticação, um refresh token.

### Renovação

```http
POST /api/v1/auth/refresh
Content-Type: application/json
```

```json
{
  "refreshToken": "<refresh_token>"
}
```

O fluxo de renovação valida o token enviado, revoga o token consumido e emite um novo par de tokens quando a operação é bem-sucedida.

### Logout

```http
POST /api/v1/auth/logout
Content-Type: application/json
```

```json
{
  "refreshToken": "<refresh_token>"
}
```

O logout revoga o refresh token ativo informado, impedindo que ele seja utilizado novamente para renovar a sessão.

Tokens expirados, inválidos ou revogados devem ser tratados pela configuração de autenticação e pelo mecanismo de tratamento de exceções da aplicação.

---

## Testes

A aplicação dispõe de uma suíte abrangente com **66 testes automatizados** e 100% de cobertura funcional aprovada. O catálogo detalhado, os cenários cobertos e as diretrizes de execução estão documentados em [`TESTES.md`](TESTES.md).

Execute a suíte completa de testes com:

```bash
./mvnw clean test
```

A arquitetura de testes cobre:

1. **Testes Unitários:** isolamento de regras de negócio em serviços com Mockito (`BrandService`, `ClienteService`, `VehicleService`, `EquipeService`, `UserService`, `RefreshTokenService`, `RateLimitService`).
2. **Testes de Integração Web (MockMvc):** validação de contratos REST, serialização JSON e códigos HTTP (`BrandController`, `ClienteController`, `VehicleController`, `EquipeController`, `HealthController`).
3. **Testes de Segurança e Tokens:** autenticação completa, rotação atômica de refresh tokens e controle de acesso a endpoints do Actuator.
4. **Testes de Persistência e Repositório (`@DataJpaTest`):** consultas derivadas, paginação e filtros case-insensitive contra banco em memória H2.
5. **Testes de Concorrência e Lock Otimista:** validação de versionamento (`@Version`) contra conflitos de escrita concorrente.
6. **Testes de Tratamento de Exceções:** serialização padronizada de `ResourceNotFoundException`, `BusinessException` e Bean Validation no `GlobalExceptionHandler`.

---

## Endpoints úteis

Os caminhos abaixo servem como referência durante o desenvolvimento. A disponibilidade depende da configuração ativa e das permissões do usuário autenticado.

| Finalidade | Endpoint |
|---|---|
| Login | `POST /api/v1/auth/login` |
| Cadastro de usuário | `POST /api/v1/auth/register` |
| Renovação de token | `POST /api/v1/auth/refresh` |
| Logout | `POST /api/v1/auth/logout` |
| Gerenciamento de clientes | `/api/v1/clientes` |
| Gerenciamento de equipes | `/api/v1/equipes` |
| Gerenciamento de veículos | `/api/v1/vehicles` |
| Gerenciamento de marcas | `/api/v1/brands` |
| Verificação de saúde | `GET /actuator/health` |
| Métricas operacionais | `GET /actuator/metrics` |
| Swagger UI | `/swagger-ui/index.html` |
| Especificação OpenAPI | `/v3/api-docs` |

Para consultar a lista definitiva de rotas, os DTOs aceitos, os códigos HTTP e os requisitos de segurança, utilize o Swagger UI da aplicação em execução.

---

## Boas práticas de desenvolvimento

- Mantenha as chaves primárias separadas dos identificadores exibidos na interface.
- Utilize DTOs para controlar os dados recebidos e retornados pela API.
- Centralize as validações de negócio na camada de serviço.
- Garanta que as permissões sejam verificadas no backend, não apenas no frontend.
- Utilize migrations do Flyway para evoluir o esquema do banco.
- Não versione credenciais ou segredos.
- Não altere migrations já aplicadas em ambientes compartilhados.
- Confira se a documentação dos endpoints corresponde ao comportamento real da aplicação antes de publicar uma nova versão.
