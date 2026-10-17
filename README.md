# FIAP Bank - Plataforma de Concessão de Crédito 🏦💳

Monorepo do **Checkpoint 5 – Domain Driven Design (Java)** da FIAP. Reúne o ecossistema da esteira de crédito do FIAP Bank: **Core Bancário**, **Plataforma de Crédito**, **Mesa de Operações** e **Internet Banking Mobile**.

O trabalho tem duas partes:
- construir o **Core Bancário** do zero;
- refatorar a **Plataforma de Crédito** legada.

Nas duas, a aplicação segue **Domain-Driven Design** e **Maven multi-módulo** e expõe **APIs RESTful**. Os front-ends legados continuam funcionando sem quebra de contrato.

---

## 👥 Integrantes do Grupo

| Nome completo | RM |
| :--- | :---: |
| `Antonio Neto` | `561777` |
| `Felipe Bicaletto` | `563524` |
| `Mauro Maia` | `556645` |

**Turma:** 2ESPH 

---

## 📁 Aplicações do Ecossistema

| Projeto | Tecnologia | Porta | Papel |
| :--- | :--- | :---: | :--- |
| [`fiap-bank-core-bancario`](./fiap-bank-core-bancario/README.md) | Java 21, Spring Boot 4.1, Spring Data JPA, DDD multi-módulo | `8080` | Clientes, contas e saldos (desembolso do crédito) |
| [`fiap-bank-plataforma-credito`](./fiap-bank-plataforma-credito/README.md) | Java 21, Spring Boot 4.1, Spring Data JPA, OpenFeign, DDD multi-módulo | `8081` | Esteira e motor de decisão de crédito |
| [`fiap-bank-mesa-operacoes-credito`](./fiap-bank-mesa-operacoes-credito/README.md) | Java 21, Spring Boot, Thymeleaf, OpenFeign | `8082` | Front-end do operador da mesa de crédito |
| [`fiap-bank-internet-banking-mobile`](./fiap-bank-internet-banking-mobile/README.md) | Angular 17+, TypeScript | `4200` | App do cliente final para solicitar crédito |

```
Cliente ──► Internet Banking Mobile ──► Plataforma de Crédito ──► Core Bancário
                                          ▲      │
Operador ──► Mesa de Operações ───────────┘      ├──► SCR (Banco Central)   [simulado]
                                                 ├──► Bureau de Crédito     [simulado]
                                                 ├──► CRM corporativo       [simulado]
                                                 └──► Sistema de Notificações [simulado]
```

---

## 🏛️ Arquitetura DDD Multi-Módulo

O **Core Bancário** e a **Plataforma de Crédito** são projetos Maven com um `pom.xml` agregador (`packaging pom`) e quatro submódulos físicos. A hierarquia de dependências é obrigatória:

```
presentation ──► application ──► infrastructure ──► domain
                      └──────────────────────────────► domain
```

| Módulo | Responsabilidade |
| :--- | :--- |
| **domain** | Java puro: entidades, Value Objects, enums, serviços de domínio, exceções e interfaces (ports) de repositório e de sistemas externos. Não depende de nenhum framework. |
| **infrastructure** | Adapters dos ports: entidades JPA (`@Entity`), repositórios Spring Data, mappers domínio ⇄ JPA e clients dos sistemas externos (OpenFeign para o Core Bancário). |
| **application** | Casos de uso (services), DTOs imutáveis (Java Records) com Bean Validation e exceções de aplicação. |
| **presentation** | Classe `@SpringBootApplication`, controllers REST e `GlobalExceptionHandler` (`@RestControllerAdvice`). |

### Verificação automática das fronteiras (maven-enforcer-plugin)

As violações de camada fazem o build falhar:
- **`pureza-domain`:** o módulo `domain` não pode depender de Spring, Jakarta Persistence, Hibernate ou Feign.
- **`isolamento-presentation`:** o módulo `presentation` só depende de `domain` e `infrastructure` em escopo `runtime`. Por isso, um controller não consegue importar classes dessas camadas.

O **Lombok** simplifica getters, construtores, builders e logs em todas as camadas.

---

## 🏦 Core Bancário — API RESTful (`/api/v1`)

| Verbo | URI | Sucesso | Descrição |
| :--- | :--- | :---: | :--- |
| `GET` | `/api/v1/clientes` | 200 | Lista os clientes |
| `GET` | `/api/v1/clientes/{id}` | 200 | Busca o cliente pelo ID |
| `GET` | `/api/v1/clientes/cpf/{cpf}` | 200 | Busca o cliente pelo CPF (com ou sem máscara) |
| `POST` | `/api/v1/clientes` | 201 | Cadastra um cliente (CPF e e-mail únicos) |
| `PUT` | `/api/v1/clientes/{id}` | 200 | Atualiza os dados cadastrais |
| `DELETE` | `/api/v1/clientes/{id}` | 204 | Remove o cliente |
| `GET` | `/api/v1/contas` | 200 | Lista as contas |
| `GET` | `/api/v1/contas/{numeroConta}` | 200 | Busca a conta pelo número |
| `GET` | `/api/v1/contas/cliente/{cpf}` | 200 | Busca a conta do titular pelo CPF |
| `POST` | `/api/v1/contas` | 201 | Abre uma conta para um cliente existente |
| `PUT` | `/api/v1/contas/{numeroConta}/saldo` | 200 | Movimenta o saldo (`DEPOSITO` / `SAQUE`) |
| `DELETE` | `/api/v1/contas/{numeroConta}` | 204 | Encerra a conta (inativação lógica, idempotente) |

**Modelo de domínio:**
- Entidades: `Cliente` e `ContaBancaria`.
- Value Objects: `Cpf`, `Email` e `Saldo`.
- Enums: `TipoConta` e `StatusConta`.

---

## 💳 Plataforma de Crédito

### Diagnóstico do estado inicial (legado)

| Problema | Onde | Por que é um problema |
| :--- | :--- | :--- |
| Domínio acoplado ao JPA | `model/SolicitacaoCredito` com `@Entity`/`@Column` e setters | Mistura regra de negócio com persistência relacional, o que viola o isolamento do domínio |
| Regra de negócio no service do Spring | `PlataformaCreditoService` | O motor de crédito não pode ser testado nem reutilizado fora do framework |
| DTO dependente da entidade | `SolicitacaoCreditoResponseDTO.fromEntity` | O contrato da API fica preso ao modelo de persistência |
| Rotas orientadas a ação, todas `POST` | `CreditoOperacoesNonRestfulController` | A URI tem verbos (`/solicitarCredito`, `/aprovarSolicitacao`…), as consultas usam `POST` com corpo, não há versionamento e a criação responde `200` em vez de `201` |
| Consulta ao Core ignorada | Motor de regras | A conta do cliente é buscada, mas nunca entra na decisão |
| Sem desembolso | Aprovação | O crédito aprovado não é registrado no Core Bancário |
| Dados falsos em falha | `CoreBancarioClientService` | Quando o Core está fora, a consulta devolve um saldo fictício e esconde a falha |
| Sem validação e sem tratamento de erros | DTOs e controllers | Parcelas `0` causam divisão por zero, ID inexistente vira `500` e não há `@RestControllerAdvice` |
| CRM ausente | Integrações | O cenário prevê consulta ao CRM, mas não há client |

### Domínio refatorado

- **Agregado:** `SolicitacaoCredito`. Não tem setters; o estado muda só por `registrar`, `aprovarPelaMesa` e `reprovarPelaMesa`. A Mesa só decide solicitações que estão aguardando análise.
- **Value Objects:** `Cpf`, `Dinheiro`, `PropostaCredito`, `AnaliseRisco`, `ContaCliente` e `DecisaoCredito`.
- **Enums:** `StatusSolicitacao` e `TipoCredito`. O `TipoCredito` aceita os valores com acento enviados pelo app mobile, como `"EMPRÉSTIMO PESSOAL"`.
- **Serviços de domínio:**
  - `AvaliadorDeRisco`: consulta SCR, Bureau, CRM e Core pelos ports.
  - `MotorDeCredito`: aplica as regras de concessão.
- **Ports:** `SolicitacaoCreditoRepository`, `ScrGateway`, `BureauCreditoGateway`, `CrmGateway`, `CoreBancarioGateway` e `NotificacaoGateway`.

### Motor de regras de crédito

As regras são avaliadas em ordem; a primeira que se aplica decide:

| # | Regra | Resultado |
| :---: | :--- | :--- |
| 1 | Restrição ativa no SCR | `REPROVADO` |
| 2 | Score do Bureau menor que 500 | `REPROVADO` |
| 3 | Sem conta ativa no Core Bancário (ou Core indisponível) | `EM_ANALISE` (Mesa) |
| 4 | Histórico de inadimplência no CRM | `EM_ANALISE` (Mesa) |
| 5 | Parcela estimada acima de 30% da renda mensal | `EM_ANALISE` (Mesa) |
| 6 | Nenhuma das anteriores | `APROVADO` |

Quando a solicitação é aprovada, o valor é creditado na conta do cliente via `PUT /api/v1/contas/{numeroConta}/saldo` (`DEPOSITO`) no Core Bancário.

### Integrações (infrastructure)

| Sistema | Implementação |
| :--- | :--- |
| Core Bancário | OpenFeign: `GET /contas/cliente/{cpf}` e `PUT /contas/{numeroConta}/saldo` |
| SCR (Banco Central) | Simulado: CPFs terminados em `9` têm restrição |
| Bureau de Crédito | Simulado: score determinístico de 400 a 950 calculado a partir do CPF |
| CRM corporativo | Simulado: CPFs terminados em `8` têm histórico negativo |
| Sistema de Notificações | Simulado: a mensagem enviada ao cliente é registrada no log |

### API RESTful (`/api/v1`)

| Verbo | URI | Sucesso | Descrição |
| :--- | :--- | :---: | :--- |
| `POST` | `/api/v1/solicitacoes` | 201 | Registra uma solicitação e executa o motor de crédito |
| `GET` | `/api/v1/solicitacoes` | 200 | Lista as solicitações (filtro opcional `?cpf=`) |
| `GET` | `/api/v1/solicitacoes/{id}` | 200 | Busca a solicitação pelo ID |
| `PUT` | `/api/v1/solicitacoes/{id}/status` | 200 | Decisão da Mesa (aprovar ou reprovar) |
| `GET` | `/api/v1/clientes/{cpf}/conta` | 200 | Consulta a conta do cliente no Core Bancário |

### Retrocompatibilidade com os front-ends

Um **controller adaptador** mantém as rotas legadas em `/api/credito/*`. Ele só traduz cada chamada para os mesmos casos de uso da API RESTful, sem duplicar regra de negócio.

| Rota legada (`POST`) | Rota RESTful equivalente | Consumidor |
| :--- | :--- | :--- |
| `/api/credito/solicitarCredito` | `POST /api/v1/solicitacoes` | Internet Banking Mobile |
| `/api/credito/consultarStatusCredito` | `GET /api/v1/solicitacoes?cpf={cpf}` | Internet Banking Mobile |
| `/api/credito/consultarClienteCore` | `GET /api/v1/clientes/{cpf}/conta` | Internet Banking Mobile |
| `/api/credito/listarTodasSolicitacoes` | `GET /api/v1/solicitacoes` | — |
| `/api/credito/buscarSolicitacaoPorId` | `GET /api/v1/solicitacoes/{id}` | — |
| `/api/credito/aprovarSolicitacao` | `PUT /api/v1/solicitacoes/{id}/status` | — |
| `/api/credito/reprovarSolicitacao` | `PUT /api/v1/solicitacoes/{id}/status` | — |

- **Mesa de Operações:** o Feign client passou a consumir a API RESTful `/api/v1`.
- **Internet Banking Mobile:** continua usando as rotas legadas por meio do adaptador, sem nenhuma alteração no app.

---

## 🚦 Como Executar

### Pré-requisitos
- **Java JDK 21+**
- **Apache Maven 3.9+**
- **Node.js 18+** e **npm** (apenas para o app mobile)

Inicie os serviços nesta ordem para testar o fluxo completo.

### 1. Core Bancário (`8080`)
```bash
cd fiap-bank-core-bancario
mvn clean install
mvn -pl fiap-bank-core-bancario-presentation spring-boot:run
```
- API: `http://localhost:8080/api/v1`
- H2 Console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:corebancariodb`, usuário `sa`, sem senha)

### 2. Plataforma de Crédito (`8081`)
```bash
cd fiap-bank-plataforma-credito
mvn clean install
mvn -pl fiap-bank-plataforma-credito-presentation spring-boot:run
```
- API RESTful: `http://localhost:8081/api/v1`
- Rotas legadas (adaptador): `http://localhost:8081/api/credito`
- H2 Console: `http://localhost:8081/h2-console` (JDBC URL `jdbc:h2:mem:plataformacreditodb`, usuário `sa`, sem senha)
- URL do Core Bancário configurável pela variável de ambiente `CORE_BANCARIO_URL` (padrão `http://localhost:8080/api/v1`)

### 3. Mesa de Operações (`8082`)
```bash
cd fiap-bank-mesa-operacoes-credito
mvn clean compile
mvn spring-boot:run
```
- Painel do operador: `http://localhost:8082`

### 4. Internet Banking Mobile (`4200`)
```bash
cd fiap-bank-internet-banking-mobile
npm install
npm run start
```
- App: `http://localhost:4200`

---

## 🧪 Dados Pré-Carregados

| Cliente | CPF | Renda | Conta no Core | Solicitação inicial | Resultado do motor |
| :--- | :--- | ---: | :--- | :--- | :--- |
| Ana Silva | `12345678900` | R$ 7.500,00 | `0001 / 12345-6`, saldo R$ 12.500,00 | Empréstimo pessoal, R$ 15.000 em 24x | `APROVADO` |
| Carlos Eduardo Oliveira | `98765432100` | R$ 12.000,00 | `0001 / 78901-2`, saldo R$ 45.800,50 | Crédito imobiliário, R$ 350.000 em 180x | `APROVADO` |
| Mariana Souza | `55544433399` | R$ 4.200,00 | Sem conta | Crédito automotivo, R$ 45.000 em 48x | `REPROVADO` (SCR) |

> Se a Plataforma subir com o Core Bancário desligado, as solicitações de Ana e Carlos são encaminhadas para a Mesa (`EM_ANALISE`), porque a conta ativa não pode ser confirmada.

**Para testar a Mesa:** envie pelo app uma proposta cuja parcela passe de 30% da renda. Exemplo: Ana Silva, R$ 60.000 em 12x (parcela de R$ 5.000).
