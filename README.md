☕ Sistema de Cafeteria
Sistema completo para gestão de cafeteria, desenvolvido com foco em usabilidade, organização e eficiência no dia a dia de um negócio real.
📋 Sobre o Projeto
Este sistema foi criado para atender às necessidades de uma cafeteria, oferecendo:

    🏪 Loja virtual para clientes visualizarem o cardápio
    📊 Dashboard administrativo com métricas de vendas
    📝 Gestão de produtos com controle de estoque
    👨‍🍳 Painel da cozinha para acompanhamento de pedidos

🖼️ Screenshots
🏪 Loja Virtual
Interface limpa e intuitiva para os clientes navegarem pelo cardápio, filtrar por categorias (Bebidas, Lanches, Doces) e adicionar itens ao carrinho.


<p align="center">
  <img src="https://github.com/LucasEspind0la/Cafeteria/blob/main/assets/Captura%20de%20tela%20de%202026-05-05%2021-43-20.png?raw=true" width="90%" alt="Loja Virtual" />
</p>


📊 Dashboard Administrativo
Painel com visão geral do negócio: vendas do dia, total de pedidos, estoque disponível, clientes atendidos e gráficos de análise (vendas por hora e top produtos).


<p align="center">
  <img src="https://github.com/LucasEspind0la/Cafeteria/blob/main/assets/Captura%20de%20tela%20de%202026-05-05%2021-44-27.png?raw=true" width="90%" alt="Dashboard" />
</p>


📝 Gestão de Produtos
CRUD completo para administrar o cardápio: adicionar, editar e excluir produtos com controle de nome, preço, categoria, tipo (quente/gelado) e quantidade em estoque.


<p align="center">
  <img src="https://github.com/LucasEspind0la/Cafeteria/blob/main/assets/Captura%20de%20tela%20de%202026-05-05%2021-45-26.png?raw=true" width="90%" alt="Gestão de Produtos" />
</p>


👨‍🍳 Painel da Cozinha
Visão em tempo real dos pedidos com status atualizado (Pendente → Em Preparo → Pronto). Interface otimizada para o fluxo de trabalho da cozinha.


<p align="center">
  <img src="https://github.com/LucasEspind0la/Cafeteria/blob/main/assets/Captura%20de%20tela%20de%202026-05-05%2021-45-32.png?raw=true" width="90%" alt="Painel da Cozinha" />
</p>


🚀 Tecnologias Utilizadas
<div align="left">
  <img src="https://img.shields.io/badge/Java-ED8B00?logo=openjdk&logoColor=white&style=for-the-badge" alt="Java"/>
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?logo=springboot&logoColor=white&style=for-the-badge" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/PostgreSQL-336791?logo=postgresql&logoColor=white&style=for-the-badge" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/HTML5-E34F26?logo=html5&logoColor=white&style=for-the-badge" alt="HTML5"/>
  <img src="https://img.shields.io/badge/CSS3-1572B6?logo=css3&logoColor=white&style=for-the-badge" alt="CSS3"/>
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?logo=javascript&logoColor=black&style=for-the-badge" alt="JavaScript"/>
  <img src="https://img.shields.io/badge/Git-F05032?logo=git&logoColor=white&style=for-the-badge" alt="Git"/>
</div>
⚙️ Funcionalidades
Table
Módulo	Funcionalidades
Loja	Cardápio com filtros, carrinho de compras, finalização de pedido
Dashboard	Métricas em tempo real, gráficos de vendas, histórico de pedidos
Gestão	CRUD de produtos, controle de estoque, categorização
Cozinha	Fila de pedidos, atualização de status, acompanhamento em tempo real
Notificações	Alertas de novos pedidos e status atualizados
📦 Estrutura do Banco de Dados

    Produtos: id, nome, descrição, preço, categoria, tipo, quantidade, imagem
    Pedidos: id, mesa, cliente, telefone, status, forma de pagamento, total
    Itens do Pedido: id, pedido_id, produto_id, quantidade, subtotal

🛠️ Como Executar
bash
Copy

# Clone o repositório
git clone https://github.com/LucasEspind0la/Cafeteria.git

# Configure o banco PostgreSQL
# Execute os scripts SQL em /database

# Inicie a aplicação
./mvnw spring-boot:run

# Acesse no navegador

📬 Contato
<p align="center">
  

  <a href="https://github.com/LucasEspind0la">
    <img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white" />
  </a>
</p>
<div align="center">
  <img src="https://komarev.com/ghpvc/?username=LucasEspind0la&label=Visualiza%C3%A7%C3%B5es&color=FF6B35&style=for-the-badge" alt="contador de visitas" />
</div>
