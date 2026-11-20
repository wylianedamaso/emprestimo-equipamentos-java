# Empréstimo de Equipamentos — API REST + Front-end

Sistema completo para gerenciamento de empréstimos de equipamentos corporativos a colaboradores, desenvolvido em **Java** com **Spring Boot**, autenticação **JWT**, testes automatizados, **CI com GitHub Actions** e interface web minimalista (Dark Tech / Light).

---

## Tecnologias Utilizadas

* **Back-end:** Java 25, Spring Boot, Spring Data JPA, Spring Validation
* **Segurança:** Spring Security com autenticação via Token JWT
* **Banco de Dados:** H2 Database
* **Testes:** JUnit 5 e MockMvc (16 testes unitários e de integração)
* **Documentação e Monitoramento:** Swagger (OpenAPI 3) e Spring Boot Actuator
* **CI/CD e Infra:** GitHub Actions, Docker e Render
* **Front-end:** HTML5, CSS3 (Dark Tech Neon / Modo Claro) e JavaScript (Fetch API)

---

## Regras de Negócio Implementadas

* Cadastro de equipamentos com validação de número de patrimônio único e disponibilidade inicial automática (`true`).
* Cadastro de colaboradores com validação de e-mail único.
* Registro de empréstimo apenas para equipamentos disponíveis e colaboradores sem pendências em atraso.
* Prazo padrão de devolução de **14 dias** a partir da data do empréstimo.
* Registro de devolução atualizando automaticamente a data de entrega e liberando o equipamento para novos empréstimos.
* Tratamento global de exceções (`@RestControllerAdvice`) com status HTTP semânticos (`400`, `401`, `403`, `404`, `409`).

---

## Credenciais de Teste

O sistema conta com dois perfis de acesso configurados em memória:

| Perfil | Usuário | Senha | Permissões |
| :--- | :--- | :--- | :--- |
| **Administrador** | `admin` | `admin123` | Acesso total (equipamentos, colaboradores e empréstimos) |
| **Colaborador** | `colaborador` | `colab123` | Consulta de equipamentos/empréstimos, novo empréstimo e devolução |

---

## Principais Endpoints

### Autenticação
* `POST /auth/login` — Autentica o usuário e retorna o token JWT.

### Equipamentos
* `GET /equipamentos` — Lista todos os equipamentos.
* `POST /equipamentos` — Cadastra um novo equipamento *(apenas ADMIN)*.

### Colaboradores
* `GET /colaboradores` — Lista todos os colaboradores *(apenas ADMIN)*.
* `POST /colaboradores` — Cadastra um novo colaborador *(apenas ADMIN)*.

### Empréstimos
* `GET /emprestimos` — Lista todos os empréstimos.
* `POST /emprestimos?equipamentoId={id}&colaboradorId={id}` — Registra um novo empréstimo.
* `PUT /emprestimos/{id}/devolucao` — Registra a devolução do equipamento.

### Documentação e Saúde
* `GET /swagger-ui/index.html` — Documentação interativa da API.
* `GET /actuator/health` — Status de saúde da aplicação.

---

## Como Executar Localmente

1. Clone o repositório:

```bash
git clone [https://github.com/wylianedamaso/emprestimo-equipamentos-java.git](https://github.com/wylianedamaso/emprestimo-equipamentos-java.git)
cd emprestimo-equipamentos-java
```

2. Execute a aplicação com o Maven Wrapper:

```bash
./mvnw spring-boot:run
```

*(No Windows PowerShell, use `.\mvnw.cmd spring-boot:run`.)*

3. Acesse no navegador:
    - **Interface Web:** `http://localhost:8080`
    - **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`

4. Para rodar os testes automatizados:

```bash
./mvnw test
```

---

## Deploy

- **Aplicação em produção (Render):** *(adicionarei o link após o deploy)*

---

## Autora

Desenvolvido por **Wyliane Damaso**.