# ☕ Sistema de Gestão para Cafeteria

![Java](https://img.shields.io/badge/Java-17/21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue?style=for-the-badge&logo=postgresql)
![Maven](https://img.shields.io/badge/Maven-Build-red?style=for-the-badge&logo=apachemaven)
![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)

> Um sistema full-stack completo para gestão de cardápio, pedidos e acompanhamento de vendas em tempo real, desenvolvido com foco em boas práticas de arquitetura e experiência do usuário.

## 📌 Sobre o Projeto

O **Sistema de Gestão para Cafeteria** foi desenvolvido para resolver a necessidade de digitalização de pequenos comércios. Ele integra uma API RESTful robusta no backend com uma interface web responsiva e intuitiva no frontend.

O sistema permite que clientes realizem pedidos online, enquanto administradores gerenciam o estoque, acompanham métricas de vendas através de dashboards interativos e controlam o fluxo de produção dos pedidos.

## 🚀 Funcionalidades Principais

### 👤 Área do Cliente
*   **Cardápio Interativo:** Visualização de produtos com filtragem por categoria (Bebidas, Lanches, Doces).
*   **Carrinho de Compras:** Adição, remoção e ajuste de quantidades em tempo real.
*   **Checkout Seguro:** Formulário de dados do cliente e resumo do pedido antes da confirmação.

### 🔐 Área Administrativa
*   **Dashboard Analítico:** Gráficos dinâmicos (Chart.js) mostrando faturamento diário, ticket médio e produtos mais vendidos.
*   **Gestão de Pedidos:** Tabela em tempo real para acompanhar status (Pendente, Pronto, Entregue) e atualizar o fluxo de trabalho.
*   **CRUD de Produtos:** Interface completa para cadastrar, editar e excluir itens do cardápio, controlando preços e estoque.

## 🛠️ Stack Tecnológica

| Categoria | Tecnologia |
| :--- | :--- |
| **Backend** | Java 17/21, Spring Boot 3.3.4, Spring Data JPA, Spring Validation |
| **Banco de Dados** | PostgreSQL (Relacional) |
| **Frontend** | HTML5, CSS3, JavaScript (ES6+), Chart.js |
| **Ferramentas** | Maven, Git, GitHub, Eclipse/STS |
| **Testes** | JUnit 5, Mockito, MockMvc |


## ⚙️ Como Executar o Projeto

### Pré-requisitos
*   [Java JDK 17 ou superior](https://www.oracle.com/java/technologies/downloads/)
*   [Maven](https://maven.apache.org/install.html)
*   [PostgreSQL](https://www.postgresql.org/download/) instalado e rodando

### Passo a Passo

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/LucasEspind0la/projeto-cafeteria.git
    cd projeto-cafeteria
    ```

2.  **Configuração do Banco de Dados:**
    Crie um banco de dados no PostgreSQL chamado `cafeteria_db`.
    Configure as credenciais no arquivo `src/main/resources/application.properties`:

    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/cafeteria_db
    spring.datasource.username=seu_usuario_postgres
    spring.datasource.password=sua_senha_postgres
    
    # Hibernate ddl auto (create, create-drop, validate, update)
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    ```

3.  **Executando a Aplicação:**
    Utilize o Maven wrapper ou o comando direto:
    ```bash
    mvn spring-boot:run
    ```


## 🧪 Testes

O projeto utiliza **JUnit 5** e **Mockito** para garantir a qualidade do código. Os testes focam na camada de Controller, simulando requisições HTTP.

Para executar os testes unitários:
```bash
mvn test
