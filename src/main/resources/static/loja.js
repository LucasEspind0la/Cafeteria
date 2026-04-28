// ===== CONFIGURAÇÃO =====
const API_PRODUTOS = 'http://localhost:8080/api/produtos';
const API_PEDIDOS = 'http://localhost:8080/api/pedidos';

let produtos = [];
let carrinho = [];

// ===== INICIALIZAÇÃO =====
document.addEventListener('DOMContentLoaded', () => {
    carregarCardapio();
});

// ===== CARREGAR CARDÁPIO =====
async function carregarCardapio() {
    const container = document.getElementById('produtos-container');
    container.innerHTML = '<div class="loading">Carregando cardápio...</div>';

    try {
        const res = await fetch(API_PRODUTOS);
        produtos = await res.json();
        renderizarCardapio(produtos);
    } catch (err) {
        container.innerHTML = '<div class="loading">Erro ao carregar cardápio 😢</div>';
    }
}

// ===== RENDERIZAR =====
function renderizarCardapio(lista) {
    const container = document.getElementById('produtos-container');

    if (lista.length === 0) {
        container.innerHTML = '<div class="loading">Nenhum produto disponível</div>';
        return;
    }

    const fragment = document.createDocumentFragment();

    lista.forEach(p => {
        const card = document.createElement('div');
        card.className = 'produto-loja';

        // Emoji baseado no tipo
        const emoji = p.tipo === 'Bebida' ? '☕' : p.tipo === 'Lanche' ? '🥐' : '🍰';

        card.innerHTML = `
            <div class="produto-imagem">${emoji}</div>
            <div class="produto-info">
                <div class="produto-nome">${escape(p.nome)}</div>
                <div class="produto-desc">${escape(p.descricao) || p.tipo + ' - ' + p.categoria}</div>
                <div class="produto-preco">R$ ${p.preco.toFixed(2)}</div>
                <div class="produto-acoes">
                    <div class="quantidade">
                        <button onclick="ajustarQtd(${p.id}, -1)">−</button>
                        <span id="qtd-${p.id}">1</span>
                        <button onclick="ajustarQtd(${p.id}, 1)">+</button>
                    </div>
                    <button class="btn-add" onclick="adicionarAoCarrinho(${p.id})">
                        Adicionar
                    </button>
                </div>
            </div>
        `;

        fragment.appendChild(card);
    });

    container.innerHTML = '';
    container.appendChild(fragment);
}

// ===== FILTRAR CATEGORIA =====
function filtrarCategoria(categoria) {
    document.querySelectorAll('.cat-btn').forEach(b => b.classList.remove('active'));
    event.target.classList.add('active');

    if (!categoria) {
        renderizarCardapio(produtos);
        return;
    }

    const filtrados = produtos.filter(p => p.tipo === categoria);
    renderizarCardapio(filtrados);
}

// ===== QUANTIDADE =====
function ajustarQtd(id, delta) {
    const span = document.getElementById(`qtd-${id}`);
    let qtd = parseInt(span.textContent) + delta;
    if (qtd < 1) qtd = 1;
    if (qtd > 10) qtd = 10;
    span.textContent = qtd;
}

// ===== CARRINHO =====
function adicionarAoCarrinho(id) {
    const produto = produtos.find(p => p.id === id);
    const qtd = parseInt(document.getElementById(`qtd-${id}`).textContent);

    const existente = carrinho.find(item => item.produto.id === id);
    if (existente) {
        existente.quantidade += qtd;
    } else {
        carrinho.push({ produto: produto, quantidade: qtd });
    }

    atualizarCarrinho();
    toast(`${produto.nome} adicionado!`, 'sucesso');

    // Reset quantidade
    document.getElementById(`qtd-${id}`).textContent = '1';
}

function removerDoCarrinho(id) {
    carrinho = carrinho.filter(item => item.produto.id !== id);
    atualizarCarrinho();
}

function atualizarCarrinho() {
    const count = document.getElementById('cart-count');
    const itensDiv = document.getElementById('carrinho-itens');
    const totalSpan = document.getElementById('total-valor');

    const totalItens = carrinho.reduce((sum, item) => sum + item.quantidade, 0);
    count.textContent = totalItens;

    const total = carrinho.reduce((sum, item) =>
        sum + (item.produto.preco * item.quantidade), 0);
    totalSpan.textContent = `R$ ${total.toFixed(2)}`;

    if (carrinho.length === 0) {
        itensDiv.innerHTML = '<p class="vazio">Carrinho vazio</p>';
        return;
    }

    itensDiv.innerHTML = carrinho.map(item => `
        <div class="item-carrinho">
            <div class="item-info">
                <h4>${escape(item.produto.nome)}</h4>
                <p>${item.quantidade}x R$ ${item.produto.preco.toFixed(2)}</p>
            </div>
            <div class="item-preco">R$ ${(item.produto.preco * item.quantidade).toFixed(2)}</div>
            <button class="btn-remover" onclick="removerDoCarrinho(${item.produto.id})">×</button>
        </div>
    `).join('');
}

function toggleCarrinho() {
    document.getElementById('carrinho').classList.toggle('aberto');
    document.getElementById('overlay').classList.toggle('ativo');
}

// ===== FINALIZAR PEDIDO =====
function finalizarPedido() {
    if (carrinho.length === 0) {
        toast('Adicione itens ao carrinho primeiro!', 'erro');
        return;
    }

    const resumo = document.getElementById('resumo-pedido');
    const total = carrinho.reduce((sum, item) =>
        sum + (item.produto.preco * item.quantidade), 0);

    resumo.innerHTML = `
        <h4>Itens:</h4>
        ${carrinho.map(item => `
            <p>${item.quantidade}x ${escape(item.produto.nome)} - R$ ${(item.produto.preco * item.quantidade).toFixed(2)}</p>
        `).join('')}
        <hr>
        <p><strong>Total: R$ ${total.toFixed(2)}</strong></p>
    `;

    document.getElementById('modal-pedido').classList.add('ativo');
}

function fecharModal() {
    document.getElementById('modal-pedido').classList.remove('ativo');
}

async function confirmarPedido() {
    const nome = document.getElementById('nome-cliente').value.trim();
    const telefone = document.getElementById('telefone-cliente').value.trim();

    if (!nome || !telefone) {
        toast('Preencha todos os campos!', 'erro');
        return;
    }

    const pedido = {
        clienteNome: nome,
        clienteTelefone: telefone,
        itens: carrinho.map(item => ({
            produtoId: item.produto.id,
            quantidade: item.quantidade
        }))
    };

    try {
        const res = await fetch(API_PEDIDOS, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(pedido)
        });

        if (!res.ok) throw new Error('Erro ao criar pedido');

        const resultado = await res.json();

        toast(`Pedido #${resultado.id} confirmado!`, 'sucesso');
        carrinho = [];
        atualizarCarrinho();
        fecharModal();
        toggleCarrinho();

        // Limpar formulário
        document.getElementById('nome-cliente').value = '';
        document.getElementById('telefone-cliente').value = '';

    } catch (err) {
        toast('Erro: ' + err.message, 'erro');
    }
}

// ===== UTILITÁRIOS =====
function toast(msg, tipo) {
    const t = document.getElementById('toast');
    t.textContent = msg;
    t.className = `toast ${tipo} show`;
    setTimeout(() => t.classList.remove('show'), 3000);
}

function escape(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}