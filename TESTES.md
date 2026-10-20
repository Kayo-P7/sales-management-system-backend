# Estratégia e Guia Completo de Testes da Aplicação

Este documento estabelece o padrão arquitetural, catálogo, cobertura e diretrizes operacionais de testes automatizados do backend.

## 1. Avaliação do Estado Atual dos Testes

A aplicação dispõe de uma suíte completa composta por **testes unitários** e **testes de integração**, totalizando **68 testes automatizados com 100% de sucesso**.

### 1.1 Testes Unitários
Os testes unitários isolam a regra de negócio de qualquer dependência externa ou banco de dados, utilizando `Mockito` e `@ExtendWith(MockitoExtension.class)`:
- **Camada de Serviço (`service`):**
  - `BrandServiceTest`: criação, busca por id, tratamento de entidade inexistente e listagem geral.
  - `ClienteServiceTest`: criação, validação de unicidade de dados, isolamento por criador, busca paginada e tratamento de não encontrado.
  - `VehicleServiceTest`: criação com validação de marca ativa, busca paginada com especificações, atualização, exclusão e tratamento de marca inexistente.
  - `EquipeServiceTest`: montagem de equipe, vinculação de supervisor e vendedores, validação de existência de usuários e busca por identificador.
  - `UserServiceTest`: criptografia obrigatória de senha com BCrypt, bloqueio de duplicidade de e-mail/telefone e busca por nome de usuário.
  - `RefreshTokenServiceTest`: geração de token de atualização, validação temporal de expiração, revogação explícita e expurgo.
  - `RateLimitServiceTest`: consumo de tokens, bloqueio ao atingir capacidade máxima do bucket e limpeza de buckets inativos.
- **Camada de Filtros e Infraestrutura (`config`):**
  - `RateLimitFilterTest`: verificação do status HTTP 429 Too Many Requests quando a taxa é violada e liberação de rotas públicas.
  - `CorrelationIdFilterTest`: geração automática do header `X-Correlation-Id` e preservação de identificadores recebidos.
- **Camada de Tratamento de Exceções (`exception`):**
  - `GlobalExceptionHandlerTest`: serialização padronizada de `ResourceNotFoundException` (404), `BusinessException` (400) e violações de Bean Validation (`MethodArgumentNotValidException` 400).

### 1.2 Testes de Integração
Os testes de integração validam a interação entre componentes, filtros de segurança, persistência e ciclo de vida HTTP:
- **Camada Web (`controller` com MockMvc):**
  - `BrandControllerTest`: contrato REST de marcas (201 Created, 200 OK, 404 Not Found) e paginação.
  - `ClienteControllerTest`: rotas plurais (`/api/v1/clientes`), persistência e validação de payload.
  - `VehicleControllerTest`: operações completas sobre veículos (`/api/v1/vehicles`), filtros e serialização JSON.
  - `EquipeControllerTest`: gestão de equipes (`/api/v1/equipes`), associação hierárquica e integridade de resposta.
  - `HealthControllerTest`: verificação do status de saúde da aplicação.
- **Segurança e Ciclo de Vida de Autenticação:**
  - `AuthenticationRefreshIntegrationTest`: fluxo ponta a ponta de autenticação com geração de access token e refresh token, seguido pela rotação atômica via endpoint `/api/v1/auth/refresh`.
  - `ActuatorSecurityTest`: proteção de endpoints administrativos (`/actuator`), bloqueio com 401 para rotas corporativas de veículos e usuários sem autenticação, e restrição de métricas para usuários não autenticados.
- **Persistência e Concorrência Otimista (Slice JPA com H2):**
  - `ClienteRepositoryTest`: testes de slice com `@DataJpaTest` validando consultas customizadas por criador, paginação e filtros case-insensitive.
  - `BrandOptimisticLockingTest`: validação de lock otimista (`@Version`), garantindo que tentativas de alteração simultânea com versão defasada disparem `ObjectOptimisticLockingFailureException`.

## 2. Tipos e Modalidades de Testes Abrangidos

Para cobrir 100% dos aspectos do sistema, a arquitetura de testes divide-se nas seguintes categorias:

1. **Testes Unitários de Regra de Negócio:**
   - Foco: serviços, mappers, cálculos e validadores puros.
   - Execução ultra-rápida em memória, sem inicialização do Spring Context.
2. **Testes de Integração Web (MockMvc):**
   - Foco: serialização/deserialização JSON, validações de anotações `@Valid`, headers e códigos de status HTTP.
3. **Testes de Persistência e Repositório (`@DataJpaTest`):**
   - Foco: integridade de esquemas, consultas JPQL, derived queries, índices únicos e auditoria JPA.
4. **Testes de Concorrência e Transação:**
   - Foco: versionamento otimista (`@Version`), bloqueio pessimista e controle de integridade referencial.
5. **Testes de Segurança e RBAC:**
   - Foco: filtros do Spring Security, tokens JWT, permissões por perfil (`@PreAuthorize`) e controle de acesso a dados multitenant.
6. **Testes de Performance e Rate Limiting:**
   - Foco: contenção de ataques de força bruta, esgotamento de quota e vazamento de memória em caches.
7. **Testes de Mutação (Mutation Testing - Pitest):**
   - Foco: injetar falhas deliberadas no bytecode para testar a eficácia real das asserções dos testes.
8. **Testes de Arquitetura (ArchUnit):**
   - Foco: regras estáticas automatizadas (ex: garantir que entidades não usem DTOs, métodos usem `FetchType.LAZY`, controllers nunca injetem repositórios).
9. **Testes de Carga e Estresse (k6 / Gatling):**
   - Foco: validação da taxa de transferência (RPS), latência percentil (p95/p99) e limites de conexão do pool HikariCP.
10. **Testes de Contrato (OpenAPI / Pact):**
    - Foco: garantir que as respostas da API cumpram rigorosamente as especificações documentadas no Swagger/OpenAPI.

## 3. Diretriz Operacional de Execução Obrigatória

> **Regra Primária:** Sempre que qualquer alteração de código for realizada (seja refatoração, correção de bug ou nova funcionalidade), execute imediatamente a suíte completa de testes antes de concluir o trabalho ou gerar commits.

### Comandos de Execução:
- Execução rápida de toda a suíte:
  ```bash
  ./mvnw test
  ```
- Execução limpa com relatório completo:
  ```bash
  ./mvnw clean test
  ```
- Execução isolada de um teste específico:
  ```bash
  ./mvnw test -Dtest=BrandOptimisticLockingTest
  ```
- Execução com log detalhado de falhas:
  ```bash
  ./mvnw test -Dsurefire.useFile=false
  ```
