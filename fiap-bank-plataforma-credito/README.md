# FIAP Bank - Plataforma de Crédito 💳

Microsserviço de **Esteira e Motor de Decisão de Crédito** do FIAP Bank. Recebe propostas de crédito, avalia o risco (SCR e Bureau de Crédito, simulados), encaminha para a Mesa de Operações quando a parcela compromete demais a renda, consulta a conta do cliente no Core Bancário via **Spring Cloud OpenFeign** e notifica o cliente.

O projeto foi refatorado para **Domain-Driven Design** em **Maven multi-módulo**, com API **RESTful** em `/api/v1`. As rotas legadas (`POST /api/credito/*`) continuam disponíveis por um controller adaptador, para que o Internet Banking e a Mesa de Operações sigam funcionando sem alteração.

---

## 🛠️ Tecnologias

- **Java 21** e **Spring Boot 4.1**
- **Spring Cloud OpenFeign** (cliente do Core Bancário)
- **Spring Data JPA** e **H2** (em memória)
- **Jakarta Bean Validation** e **Lombok**
- **Maven** multi-módulo com `maven-enforcer-plugin`

---

## 🏛️ Módulos

| Módulo | Conteúdo |
| :--- | :--- |
| `fiap-bank-plataforma-credito-domain` | Java puro: agregado `SolicitacaoCredito` (com o motor de regras em `avaliar`), Value Objects (`Cpf`, `Dinheiro`, `PropostaCredito`, `ContaCliente`), enums, exceções e ports (`SolicitacaoCreditoRepository`, `ScrGateway`, `BureauCreditoGateway`, `CoreBancarioGateway`, `NotificacaoGateway`) |
| `fiap-bank-plataforma-credito-infrastructure` | Entidade JPA, repositório Spring Data, mapper, adapters dos ports, Feign do Core, simuladores de SCR/Bureau/Notificações e carga inicial |
| `fiap-bank-plataforma-credito-application` | `SolicitacaoCreditoService`, DTOs (records com Bean Validation) e exceções de aplicação |
| `fiap-bank-plataforma-credito-presentation` | Classe principal, controllers REST, adapter das rotas legadas e `GlobalExceptionHandler` |

O enforcer faz o build falhar se o `domain` depender de algum framework ou se o `presentation` importar `domain`/`infrastructure` em tempo de compilação.

---

## 🚀 Como Executar

Pré-requisitos: **Java 21** e **Maven 3.9+**. Suba o **Core Bancário** (porta `8080`) antes, para que a consulta de conta funcione.

Na raiz de `fiap-bank-plataforma-credito`:

```bash
mvn clean install
mvn -pl fiap-bank-plataforma-credito-presentation spring-boot:run
```

Ou pelo JAR gerado:

```bash
java -jar fiap-bank-plataforma-credito-presentation/target/fiap-bank-plataforma-credito-presentation-1.0.0-SNAPSHOT.jar
```

A aplicação fica em **`http://localhost:8081`**.

| Propriedade | Variável de ambiente | Padrão | Descrição |
| :--- | :--- | :--- | :--- |
| `server.port` | `SERVER_PORT` | `8081` | Porta HTTP |
| `core.bancario.url` | `CORE_BANCARIO_URL` | `http://localhost:8080/api/v1` | URL base da API do Core Bancário |

**H2 Console:** `http://localhost:8081/h2-console` (JDBC URL `jdbc:h2:mem:plataformacreditodb`, usuário `sa`, sem senha).

---

## ⚖️ Motor de Regras

Executado por `SolicitacaoCredito.avaliar(restricaoScr, scoreBureau)`. As regras são avaliadas em ordem; a primeira que se aplica decide:

| # | Regra | Resultado |
| :---: | :--- | :--- |
| 1 | Restrição ativa no SCR | `REPROVADO` |
| 2 | Score do Bureau menor que 500 | `REPROVADO` |
| 3 | Parcela estimada (`valorSolicitado / quantidadeParcelas`, 2 casas, `HALF_UP`) acima de 30% da renda | `EM_ANALISE` (Mesa) |
| 4 | Nenhuma das anteriores | `APROVADO` |

A Mesa de Operações só pode aprovar ou reprovar solicitações `EM_ANALISE`; qualquer outra transição responde `400`.

| Integração | Implementação |
| :--- | :--- |
| Core Bancário | OpenFeign: `GET /api/v1/contas/cliente/{cpf}` |
| SCR (Banco Central) | Simulado: CPFs terminados em `9` têm restrição |
| Bureau de Crédito | Simulado: score determinístico de 400 a 950 calculado a partir do CPF |
| Sistema de Notificações | Simulado: a mensagem é registrada no log |

---

## 🌐 API RESTful (`/api/v1`)

| Verbo | URI | Sucesso | Erros |
| :--- | :--- | :---: | :--- |
| `POST` | `/api/v1/solicitacoes` | 201 + `Location` | 400 |
| `GET` | `/api/v1/solicitacoes` | 200 | — |
| `GET` | `/api/v1/solicitacoes?cpf={cpf}` | 200 | 400 (CPF inválido) |
| `GET` | `/api/v1/solicitacoes/{id}` | 200 | 404 |
| `PUT` | `/api/v1/solicitacoes/{id}/status` | 200 | 400 (status inválido ou solicitação fora de `EM_ANALISE`), 404 |
| `GET` | `/api/v1/clientes/{cpf}/conta` | 200 | 404 (sem conta), 502 (Core indisponível) |

Exemplo de criação:

```json
POST /api/v1/solicitacoes
{
  "cpfCliente": "12345678900",
  "nomeCliente": "Ana Silva",
  "tipoCredito": "EMPRESTIMO_PESSOAL",
  "valorSolicitado": 15000.00,
  "quantidadeParcelas": 24,
  "rendaMensal": 7500.00
}
```

Decisão da Mesa:

```json
PUT /api/v1/solicitacoes/1/status
{ "status": "APROVADO", "parecer": "Renda comprovada" }
```

Os erros seguem um corpo padronizado: `timestamp`, `status`, `erro`, `mensagem`, `path` e `campos` (lista de `{campo, mensagem}` em erros de validação).

---

## 🔁 Rotas Legadas (compatibilidade)

O `CreditoLegacyAdapterController` (`@Deprecated`) mantém as rotas antigas. Ele só converte o payload legado e chama o mesmo `SolicitacaoCreditoService` da API RESTful, respondendo `200 OK` com os mesmos nomes de campo do sistema original.

| Rota legada (`POST`) | Corpo | Equivalente RESTful | Consumidor |
| :--- | :--- | :--- | :--- |
| `/api/credito/solicitarCredito` | `{cpfCliente, nomeCliente, tipoCredito, valorSolicitado, quantidadeParcelas, rendaMensal}` | `POST /api/v1/solicitacoes` | Internet Banking |
| `/api/credito/consultarStatusCredito` | `{cpfCliente}` | `GET /api/v1/solicitacoes?cpf=` | Internet Banking |
| `/api/credito/consultarClienteCore` | `{cpfCliente}` | `GET /api/v1/clientes/{cpf}/conta` | Internet Banking |
| `/api/credito/listarTodasSolicitacoes` | sem corpo | `GET /api/v1/solicitacoes` | Mesa de Operações |
| `/api/credito/buscarSolicitacaoPorId` | `{solicitacaoId}` | `GET /api/v1/solicitacoes/{id}` | Mesa de Operações |
| `/api/credito/aprovarSolicitacao` | `{solicitacaoId, parecerOperador}` | `PUT /api/v1/solicitacoes/{id}/status` | Mesa de Operações |
| `/api/credito/reprovarSolicitacao` | `{solicitacaoId, motivoReprovacao}` | `PUT /api/v1/solicitacoes/{id}/status` | Mesa de Operações |

Campos da resposta de solicitação (rotas novas e legadas): `id, cpfCliente, nomeCliente, tipoCredito, valorSolicitado, quantidadeParcelas, rendaMensal, scoreCredito, status, parecerOperador, scrRestricoes, scoreBureau, dataSolicitacao, dataAtualizacao`. O `tipoCredito` é devolvido exatamente como foi enviado (por exemplo, `"EMPRÉSTIMO PESSOAL"`).

**Fallback de `consultarClienteCore`:** como o Internet Banking não trata erro nessa chamada, a rota legada sempre responde `200`. Se o cliente não tem conta, `statusConta` vem `SEM_CONTA`; se o Core está fora do ar, vem `INDISPONIVEL` (a falha é registrada no log). Nos dois casos `numeroConta`, `agencia` e `saldo` vêm nulos — nenhum dado é inventado. A rota RESTful equivalente responde `404` e `502`.

---

## 📌 Dados Iniciais

Carregados ao subir o serviço (`PlataformaDataInitializer`). O resultado não depende do Core Bancário estar no ar.

| Cliente | CPF | Solicitação | Resultado |
| :--- | :--- | :--- | :--- |
| Ana Silva | `12345678900` | Empréstimo pessoal, R$ 15.000 em 24x, renda R$ 7.500 | `APROVADO` |
| Carlos Eduardo Oliveira | `98765432100` | Crédito imobiliário, R$ 350.000 em 180x, renda R$ 12.000 | `APROVADO` |
| Mariana Souza | `55544433399` | Crédito automotivo, R$ 45.000 em 48x, renda R$ 4.200 | `REPROVADO` (SCR) |
