# ☕ Projeto Cafeteria - Sistema de Gestão e Pedidos

![Java](https://img.shields.io/badge/Java-17/21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue?style=for-the-badge&logo=postgresql)
![Maven](https://img.shields.io/badge/Maven-Build-red?style=for-the-badge&logo=apachemaven)

Sistema completo para gestão de uma cafeteria, desenvolvido com **Spring Boot** no backend e **HTML/CSS/JS** no frontend. O projeto permite o cadastro de produtos, realização de pedidos pelos clientes, acompanhamento em tempo real via dashboard administrativo e finalização de pagamentos.

## 🚀 Funcionalidades

*   **Loja do Cliente:** Visualização do cardápio, filtragem por categorias (Bebidas, Lanches, Doces) e carrinho de compras interativo.
*   **Gestão de Produtos (Admin):** Interface para cadastrar, editar e excluir itens do cardápio, controlando preços e estoque.
*   **Dashboard Administrativo:** Painel com gráficos dinâmicos (Chart.js) mostrando vendas do dia, produtos mais vendidos e status dos pedidos.
*   **Checkout e Pagamento:** Fluxo de finalização de pedido com resumo da compra e simulação de pagamento.
*   **API RESTful:** Endpoints completos para integração entre o frontend e o banco de dados PostgreSQL.

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Função |
| :--- | :--- |
| **Java 17/21** | Linguagem principal |
| **Spring Boot 3.3.4** | Framework Backend |
| **Spring Data JPA** | Persistência de dados |
| **PostgreSQL** | Banco de Dados Relacional |
| **Maven** | Gerenciador de Dependências |
| **JUnit 5 / Mockito** | Testes Unitários |
| **HTML5 / CSS3 / JS** | Frontend Responsivo |
| **Chart.js** | Gráficos no Dashboard |

## 📸 Screenshots do Projeto

### 🏪 Loja do Cliente
*Interface amigável para o cliente escolher seus produtos.*
![Loja Cliente](img/loja.png)

### 📊 Dashboard Admin
*Acompanhamento de métricas e status dos pedidos em tempo real.*
![Dashboard Admin](img/dashboard.png)

### 📦 Gestão de Produtos
*Tela administrativa para controle do cardápio.*
![Gestão Produtos](img/gestao-produtos.png)

### 💳 Checkout
*Finalização segura do pedido.*
![Checkout](img/checkout.png)

## ⚙️ Como Executar o Projeto

### Pré-requisitos
*   Java JDK 17 ou superior
*   Maven instalado
*   PostgreSQL configurado

### Passo a Passo

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/LucasEspind0la/projeto-cafeteria.git
    cd projeto-cafeteria
    ```

2.  **Configure o Banco de Dados:**
    Crie um banco no PostgreSQL chamado `cafeteria_db` e ajuste as credenciais no arquivo `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/cafeteria_db
    spring.datasource.username=seu_usuario
    spring.datasource.password=sua_senha
    spring.jpa.hibernate.ddl-auto=update
    ```

3.  **Execute a aplicação:**
    ```bash
    mvn spring-boot:run
    ```

4.  **Acesse no navegador:**
    *   Loja: `http://localhost:8080/index.html`
    *   Dashboard Admin: `http://localhost:8080/admin-pedidos.html`
    *   Gestão de Produtos: `http://localhost:8080/admin-produtos.html`

## 🧪 Testes

O projeto conta com testes unitários para a camada de Controller utilizando **MockMvc** e **Mockito**.
Para rodar os testes:
```bash
mvn test
