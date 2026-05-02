let carrinho = [];
let produtos = [];

const iconesCategoria = {
    'Bebida': '☕',
    'Lanche': '🥐',
    'Doce': '🍰',
    'Salgado': '🥪',
    'Quente': '🔥',
    'Gelado': '❄️',
    'default': '🍽️'
};

// Mapeamento robusto para qualquer variação
function normalizarCategoria(valor) {
    if (!valor) return '';
    let str = String(valor).trim();

    // Remove acentos
    str = str.normalize('NFD').replace(/[\u0300-\u036f]/g, '');

    let lower = str.toLowerCase();

    const mapa = {
        'bebida': 'Bebida', 'bebidas': 'Bebida',
        'lanche': 'Lanche', 'lanches': 'Lanche',
        'doce': 'Doce', 'doces': 'Doce',
        'salgado': 'Salgado', 'salgados': 'Salgado'
    };

    return mapa[lower] || str;
}

async function carregarProdutos() {
    const container = document.getElementById('produtos-container');

    try {
        const response = await fetch('/api/produtos');
        if (!response.ok) throw new Error('Erro ' + response.status);

        const dados = await response.json();

        // Normaliza categorias
        produtos = dados.map(p => {
            let cat = normalizarCategoria(p.categoria);
            // Se categoria vazia, tenta usar tipo
            if (!cat && p.tipo) {
                cat = normalizarCategoria(p.tipo);
            }
            return { ...p, categoria: cat };
        });

        if (produtos.length === 0) {
            container.innerHTML = '<div class="loading">Nenhum produto disponível</div>';
            return;
        }

        renderizarProdutos(produtos);

    } catch (err) {
        console.error('Erro:', err);
        container.innerHTML = '<div class="loading">Erro ao carregar cardápio 😢</div>';
    }
}

function renderizarProdutos(lista) {
    const container = document.getElementById('produtos-container');

    if (lista.length === 0) {
        container.innerHTML = '<div class="loading">Nenhum produto encontrado nesta categoria 😢</div>';
        return;
    }

    container.innerHTML = lista.map(p => {
        let imagemHTML;
        if (p.imagem) {
            imagemHTML = `<img src="${p.imagem}" alt="${p.nome}" onerror="this.parentElement.innerHTML='${getIcone(p)}'">`;
        } else {
            imagemHTML = getIcone(p);
        }

        return `
        <div class="produto-card" data-categoria="${p.categoria}">
            <div class="produto-img">
                ${imagemHTML}
            </div>
            <div class="produto-info">
                <div class="produto-header">
                    <h3>${p.nome}</h3>
                    <span class="produto-badge">${p.tipo || p.categoria}</span>
                </div>
                <div class="produto-preco">R$ ${Number(p.preco).toFixed(2)}</div>
                <div class="produto-estoque">📦 ${p.estoque} unidades</div>
                <p class="produto-descricao">${p.descricao || ''}</p>
                <button class="btn-add" onclick="adicionarAoCarrinho(${p.id})">➕ Adicionar ao Carrinho</button>
            </div>
        </div>`;
    }).join('');
}

function getIcone(produto) {
    return iconesCategoria[produto.categoria] ||
        iconesCategoria[produto.tipo] ||
        iconesCategoria['default'];
}

function filtrarCategoria(categoria) {
    const btnClicado = event.target.closest('.cat-btn');
    if (!btnClicado) return;

    document.querySelectorAll('.cat-btn').forEach(btn => btn.classList.remove('active'));
    btnClicado.classList.add('active');

    if (!categoria) {
        renderizarProdutos(produtos);
    } else {
        const filtrados = produtos.filter(p => p.categoria === categoria);
        renderizarProdutos(filtrados);
    }
}

// ============== CARRINHO ==============
function adicionarAoCarrinho(id) {
    const produto = produtos.find(p => p.id === id);
    if (!produto) return;

    const itemExistente = carrinho.find(item => item.id === id);
    if (itemExistente) {
        itemExistente.quantidade++;
    } else {
        carrinho.push({
            id: produto.id,
            nome: produto.nome,
            preco: Number(produto.preco),
            quantidade: 1
        });
    }

    atualizarCarrinho();
    mostrarToast(produto.nome + ' adicionado!');
}

function toggleCarrinho() {
    const carrinhoEl = document.getElementById('carrinho');
    const overlay = document.getElementById('overlay');
    if (carrinhoEl.classList.contains('aberto')) {
        carrinhoEl.classList.remove('aberto');
        overlay.style.display = 'none';
    } else {
        carrinhoEl.classList.add('aberto');
        overlay.style.display = 'block';
    }
}

function atualizarCarrinho() {
    const itensEl = document.getElementById('carrinho-itens');
    const countEl = document.getElementById('cart-count');
    const totalEl = document.getElementById('total-valor');

    const totalItens = carrinho.reduce((s, item) => s + item.quantidade, 0);
    const totalValor = carrinho.reduce((s, item) => s + (item.preco * item.quantidade), 0);

    countEl.textContent = totalItens;
    totalEl.textContent = 'R$ ' + totalValor.toFixed(2);

    if (carrinho.length === 0) {
        itensEl.innerHTML = '<p class="vazio">Carrinho vazio</p>';
        return;
    }

    itensEl.innerHTML = carrinho.map(item => `
        <div class="carrinho-item">
            <div>
                <strong>${item.nome}</strong>
                <div>R$ ${item.preco.toFixed(2)} x ${item.quantidade}</div>
            </div>
            <div class="item-acoes">
                <button onclick="alterarQuantidade(${item.id}, -1)">-</button>
                <span>${item.quantidade}</span>
                <button onclick="alterarQuantidade(${item.id}, 1)">+</button>
                <button onclick="removerItem(${item.id})">🗑️</button>
            </div>
        </div>
    `).join('');
}

function alterarQuantidade(id, delta) {
    const item = carrinho.find(i => i.id === id);
    if (!item) return;
    item.quantidade += delta;
    if (item.quantidade <= 0) removerItem(id);
    else atualizarCarrinho();
}

function removerItem(id) {
    carrinho = carrinho.filter(i => i.id !== id);
    atualizarCarrinho();
}

function finalizarPedido() {
    if (carrinho.length === 0) {
        alert('Carrinho vazio!');
        return;
    }
    const resumoEl = document.getElementById('resumo-pedido');
    const total = carrinho.reduce((s, item) => s + (item.preco * item.quantidade), 0);
    resumoEl.innerHTML = `
        <h4>Itens do pedido:</h4>
        ${carrinho.map(item => `
            <div>${item.quantidade}x ${item.nome} - R$ ${(item.preco * item.quantidade).toFixed(2)}</div>
        `).join('')}
        <hr>
        <div><strong>Total: R$ ${total.toFixed(2)}</strong></div>
    `;
    document.getElementById('modal-pedido').style.display = 'flex';
}

function fecharModal() {
    document.getElementById('modal-pedido').style.display = 'none';
}

async function confirmarPedido() {
    const nome = document.getElementById('nome-cliente').value;
    const telefone = document.getElementById('telefone-cliente').value;
    if (!nome || !telefone) {
        alert('Preencha nome e telefone!');
        return;
    }
    const itens = carrinho.map(item => ({
        produtoId: item.id,
        quantidade: item.quantidade
    }));
    try {
        const response = await fetch('/api/pedidos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ clienteNome: nome, clienteTelefone: telefone, itens: itens })
        });
        if (response.ok) {
            const pedido = await response.json();
            alert('Pedido #' + pedido.id + ' confirmado!');
            carrinho = [];
            atualizarCarrinho();
            fecharModal();
        } else {
            alert('Erro ao confirmar pedido');
        }
    } catch (err) {
        alert('Erro de conexão');
    }
}

function mostrarToast(mensagem) {
    const toast = document.getElementById('toast');
    if (!toast) return;
    toast.textContent = mensagem;
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 3000);
}

document.addEventListener('DOMContentLoaded', function() {
    carregarProdutos();
});