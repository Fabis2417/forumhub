
ForumHub API Uma API RESTful para um fórum online, permitindo aos usuários criar, ler, atualizar e deletar tópicos e respostas, com autenticação JWT e validação de regras de negócio.

🚀 Tecnologias Utilizadas O projeto foi desenvolvido utilizando as seguintes tecnologias:

Java 17

Spring Boot 3.3.1

Spring Security para autenticação JWT

JPA / Hibernate para persistência de dados

PostgreSQL como banco de dados

Maven para gerenciamento de dependências

Flyway para migrações de banco de dados (se você usou)

Lombok para reduzir código boilerplate

SpringDoc OpenAPI (Swagger UI) para documentação da API

✨ Funcionalidades A API do FórumHub oferece as seguintes funcionalidades principais:

Autenticação de Usuários: Login para obter um token JWT.

CRUD Completo de Tópicos: Gerenciamento de tópicos (criar, listar, detalhar, atualizar, excluir).

CRUD Completo de Respostas: Gerenciamento de respostas dentro de um tópico.

Validações de Negócio:

Tópicos não podem ser criados com título ou mensagem duplicados.

Respostas não podem ser criadas com a mesma mensagem em um mesmo tópico.

As respostas podem ser marcadas como "solução", e um tópico não pode ter mais de uma solução.

Apenas o autor de um tópico/resposta ou um ADMIN pode editar ou excluir o conteúdo.

Paginação e Ordenação: A listagem de tópicos é paginada para um melhor desempenho.

Tratamento de Erros Global: A API retorna códigos de status HTTP semânticos (como 400 Bad Request, 404 Not Found, 409 Conflict) em caso de erros.

🔑 Endpoints da API A API pode ser testada via Swagger UI em http://localhost:8080/swagger-ui.html ou usando ferramentas como o Postman.

Método

Endpoint

Descrição

POST

/login

Autentica o usuário e retorna o token JWT.

POST

/topicos

Cria um novo tópico (requer autenticação).

GET

/topicos

Lista todos os tópicos paginados.

GET

/topicos/{id}

Detalha um tópico por ID.

PUT

/topicos/{id}

Atualiza um tópico (requer autenticação e ser o autor).

DELETE

/topicos/{id}

Exclui um tópico (requer autenticação e ser o autor).

POST

/respostas

Cria uma nova resposta (requer autenticação).

GET

/respostas

Lista todas as respostas paginadas.

GET

/respostas/{id}

Detalha uma resposta por ID.

PUT

/respostas/{id}

Atualiza uma resposta (requer autenticação e ser o autor).

DELETE

/respostas/{id}

Exclui uma resposta (requer autenticação e ser o autor).

⚙️ Como Rodar o Projeto Pré-requisitos: Certifique-se de ter o Java 17 e o Maven instalados.

Configurar o Banco de Dados:

Instale o PostgreSQL.

Crie um banco de dados chamado forumhub.

Ajuste as credenciais de acesso no arquivo application.properties.

Executar o Projeto:

Abra o projeto na sua IDE favorita (IntelliJ, VS Code, etc.).

Execute o arquivo principal ForumhubApplication.java.
