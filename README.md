# Sistema de Gestão de Concessionária

Sistema web desenvolvido como atividade prática da disciplina de Engenharia de Software, com o objetivo de apoiar o gerenciamento de uma concessionária de veículos.

A aplicação possui frontend em Angular, backend em Spring Boot, autenticação baseada em JWT e persistência de dados. O sistema também está disponível em ambiente de produção no Render.

## Aplicação em produção

- Frontend: https://concessionaria-frontend-bev3.onrender.com
- Backend: https://concessionaria-backend-bqlh.onrender.com

> Os serviços utilizam o plano gratuito do Render e podem levar alguns segundos para responder após um período de inatividade.

## Funcionalidades

Entre as principais funcionalidades implementadas estão:

- cadastro e autenticação de usuários;
- controle de acesso por autenticação JWT;
- cadastro de clientes;
- consulta e pesquisa de clientes;
- visualização dos detalhes de clientes;
- cadastro de veículos;
- consulta e pesquisa de veículos;
- registro de vendas;
- consulta e pesquisa de vendas;
- visualização dos detalhes de vendas;
- atualização do estado do veículo após a realização de uma venda.

## Tecnologias utilizadas

### Backend

- Java 21
- Spring Boot 3.2.5
- Spring Web
- Spring Data JPA
- Hibernate
- Spring Security
- JWT
- Maven
- JaCoCo

### Frontend

- Angular 22
- TypeScript
- RxJS
- Angular Router
- Angular Forms
- Vitest
- npm

### Banco de dados

- MySQL 8 para desenvolvimento local e testes
- PostgreSQL para o ambiente de produção

### Infraestrutura

- Docker
- Nginx
- Render
- GitHub Actions
- Git e GitHub

## Integrantes do grupo

### Laysa Beatriz do Nascimento Beserra

- Matrícula: 200751129
- GitHub: @laysabeatriizz
- E-mail: laysa.beatriz@ufape.edu.br

### Ricardo Matias de Lima

- Matrícula: 200751121
- GitHub: @RicardoMatiassl
- E-mail: ricardo.matias@ufape.edu.br

### Luana Siqueira de Sousa

- Matrícula: XXXXXX
- GitHub: @LuanaSiqueira-1
- E-mail: luana.siqueira@ufape.edu.br

### Riana de Queiroz Tenorio Vaz

- Matrícula: XXXXXX
- GitHub: @riannavaz
- E-mail: rianna.vaz@ufape.edu.br

## Estrutura do projeto

O repositório está dividido principalmente em duas aplicações:

```text
SistemaDeGestao/
├── .github/
│   └── workflows/
├── backend/
├── frontend/
├── .gitignore
└── README.md
```

- `backend/`: aplicação Spring Boot e acesso aos bancos de dados;
- `frontend/`: aplicação Angular;
- `.github/workflows/`: pipelines de integração contínua do frontend e backend.

## Pré-requisitos para execução local

Antes de executar o projeto localmente, é necessário possuir:

- Java 21
- Node.js
- npm
- MySQL Server 8
- Git

O Docker também pode ser utilizado para criação das imagens das aplicações.

## Banco de dados local

No ambiente de desenvolvimento e testes, o backend utiliza MySQL 8.

O servidor MySQL precisa estar instalado e em execução antes da inicialização da aplicação.

No Windows, o serviço pode ser consultado com:

```powershell
Get-Service MySQL80
```

As configurações de acesso ao banco devem ser definidas no ambiente local de acordo com a configuração da aplicação.

Credenciais e outros dados sensíveis não devem ser adicionados ao repositório.

## Executando o backend localmente

A partir da raiz do repositório:

```powershell
cd backend
.\mvnw spring-boot:run
```

Por padrão, o backend local é acessado em:

```text
http://localhost:8080
```

## Executando o frontend localmente

Em outro terminal, a partir da raiz do repositório:

```powershell
cd frontend
npm ci
npm start
```

O frontend de desenvolvimento é acessado em:

```text
http://localhost:4200
```

Durante o desenvolvimento local, o frontend utiliza o backend disponível em:

```text
http://localhost:8080
```

## Build do frontend

Para gerar o build de produção do Angular:

```powershell
cd frontend
npm run build
```

Os arquivos gerados para execução em produção são servidos pelo Nginx no container do frontend.

## Testes do frontend

Os testes podem ser executados com:

```powershell
cd frontend
npm test -- --no-watch --no-progress
```

O pipeline do frontend também executa automaticamente testes e build.

## Testes e cobertura do backend

O backend utiliza Maven e JaCoCo para execução de testes e geração das informações de cobertura.

Para executar a verificação do backend:

```powershell
cd backend
.\mvnw verify -Dspring.profiles.active=test
```

O ambiente de testes utiliza MySQL.

## Integração contínua

O projeto possui pipelines do GitHub Actions para frontend e backend.

### Frontend CI

O workflow do frontend executa:

1. checkout do código;
2. configuração do Node.js;
3. instalação das dependências com `npm ci`;
4. execução dos testes;
5. build da aplicação Angular.

O workflow é executado em pushes e pull requests direcionados à branch `main`.

### Backend CI

O workflow do backend executa:

1. checkout do código;
2. inicialização de um serviço MySQL 8 para os testes;
3. configuração do Java 21;
4. execução do Maven;
5. testes automatizados;
6. geração de cobertura com JaCoCo.

O workflow também é executado em pushes e pull requests direcionados à branch `main`.

## Docker

As duas partes da aplicação possuem configuração própria para Docker.

### Backend

O backend utiliza uma imagem com Java 21.

O processo é dividido em duas etapas:

1. compilação da aplicação com Maven;
2. execução do arquivo JAR utilizando uma imagem JRE.

A aplicação expõe a porta `8080`.

### Frontend

O frontend também utiliza um build em múltiplas etapas:

1. compilação da aplicação Angular utilizando Node.js;
2. disponibilização dos arquivos estáticos utilizando Nginx.

O Nginx também está configurado para permitir o funcionamento das rotas da SPA, redirecionando as rotas da aplicação para o `index.html` quando necessário.

## Ambiente de produção

A aplicação foi publicada no Render utilizando serviços separados para frontend, backend e banco de dados.

### Frontend

O frontend Angular é compilado dentro de um container Docker e servido pelo Nginx.

URL:

```text
https://concessionaria-frontend-bev3.onrender.com
```

### Backend

O backend Spring Boot é executado em um container Docker no Render.

URL:

```text
https://concessionaria-backend-bqlh.onrender.com
```

### Banco de dados

No ambiente de produção é utilizado PostgreSQL.

O backend utiliza um profile específico de produção e recebe os dados de conexão por meio de variáveis de ambiente.

## Variáveis de ambiente de produção

O backend utiliza as seguintes variáveis no ambiente de produção:

```text
PORT
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
FRONTEND_URL
```

Nenhum valor sensível dessas variáveis deve ser armazenado diretamente no repositório.

O profile de produção pode ser ativado com:

```text
SPRING_PROFILES_ACTIVE=prod
```

## Integração entre frontend e backend

O frontend possui configurações diferentes para desenvolvimento e produção.

Em desenvolvimento, a API utilizada é:

```text
http://localhost:8080
```

Em produção, a aplicação utiliza:

```text
https://concessionaria-backend-bqlh.onrender.com
```

O backend possui configuração de CORS para aceitar requisições da origem definida pela variável:

```text
FRONTEND_URL
```

No ambiente publicado, essa variável corresponde ao endereço do frontend no Render.

## Segurança

A aplicação utiliza Spring Security e autenticação baseada em JWT.

Após uma autenticação válida, o frontend utiliza o token recebido para acessar as rotas protegidas da API.

As configurações sensíveis utilizadas na geração e validação dos tokens são fornecidas por variáveis de ambiente no ambiente de produção.

## Validação em produção

Após a publicação e integração dos serviços, foram validados os principais fluxos da aplicação no ambiente do Render, incluindo:

- cadastro de usuário;
- login;
- acesso às áreas protegidas;
- cadastro de cliente;
- consulta de clientes;
- visualização de detalhes de cliente;
- cadastro de veículo;
- consulta de veículos;
- registro de venda;
- consulta de vendas.

Os testes confirmaram a comunicação entre:

```text
Frontend Angular
        ↓
Backend Spring Boot
        ↓
PostgreSQL
```

## Fluxo de desenvolvimento

O desenvolvimento do projeto utiliza Git e GitHub com trabalho realizado em branches e integração por pull requests.

O fluxo adotado consiste, de forma geral, em:

1. criação ou atualização da branch de trabalho;
2. implementação da tarefa;
3. testes locais;
4. commit das alterações;
5. push da branch;
6. abertura de pull request;
7. revisão;
8. merge na branch `main`;
9. execução dos workflows de integração contínua;
10. atualização automática dos serviços publicados quando aplicável.

## Status da aplicação

A aplicação encontra-se integrada e disponível em ambiente de produção, com frontend, backend e banco de dados comunicando-se corretamente.