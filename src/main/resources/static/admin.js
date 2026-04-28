// ===== CONFIGURAÇÃO =====
const API = 'http://localhost:8080/api/produtos';
let produtos = []; // Cache local

// ===== INICIALIZAÇÃO =====
document.addEventListener('DOMContentLoaded', () => {
    carregarProdutos();
});

// ===== NAVEGAÇÃO =====
function showSection(id) {
    document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
    document.getElementById('sec-' + id).classList.add('active');

    document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
    event.target.classList.add('active');
}

// ===== CARREGAR PRODUTOS =====
async function carregarProdutos() {
    mostrarLoading(true);

    try {
        const res = await fetch(API);
        if (!res.ok) throw new Error('Erro ao carregar');

        produtos = await res.json();
        renderizar(produtos);
        toast(`${produtos.length} produtos carregados`, 'sucesso');
    } catch (err) {
        toast('Erro: ' + err.message, 'erro');
        console.error(err);
    } finally {
        mostrarLoading(false);
    }
}

// ===== RENDERIZAR =====
function renderizar(lista) {
    const container = document.getElementById('lista-produtos');

    if (lista.length === 0) {
        container.innerHTML = '<div class="loading">Nenhum produto encontrado</div>';
        return;
    }

    // DocumentFragment = performance máxima
    const fragment = document.createDocumentFragment();

    lista.forEach(p => {
        const card = document.createElement('div');
        card.className = 'card';

        const estoqueClass = p.estoque <= 5 ? 'critico' : p.estoque <= 15 ? 'baixo' : '';

        card.innerHTML = `
            <div class="card-titulo">${escape(p.nome)}</div>
            <div class="card-tags">
                <span class="tag tag-tipo">${escape(p.tipo)}</span>
                <span class="tag tag-cat">${escape(p.categoria)}</span>
            </div>
            <div class="card-preco">R$ ${p.preco.toFixed(2)}</div>
            <div class="card-estoque">
                <span class="estoque-indicador ${estoqueClass}"></span>
                <span>Estoque: ${p.estoque} unidades</span>
            </div>
            <div class="card-desc">${escape(p.descricao) || 'Sem descrição'}</div>
            <div class="card-acoes">
                <button class="btn-editar" onclick="editar(${p.id})">✏️ Editar</button>
                <button class="btn-excluir" onclick="excluir(${p.id})">🗑️ Excluir</button>
            </div>
        `;

        fragment.appendChild(card);
    });

    container.innerHTML = '';
    container.appendChild(fragment);
}

// ===== FILTRAR =====
function filtrar() {
    const busca = document.getElementById('search').value.toLowerCase();
    const tipo = document.getElementById('filter-tipo').value;

    const filtrados = produtos.filter(p => {
        const matchBusca = !busca || p.nome.toLowerCase().includes(busca);
        const matchTipo = !tipo || p.tipo === tipo;
        return matchBusca && matchTipo;
    });

    renderizar(filtrados);
}

// ===== SALVAR (CREATE) =====
async function salvar(e) {
    e.preventDefault();

    const produto = {
        nome: document.getElementById('nome').value.trim(),
        tipo: document.getElementById('tipo').value,
        categoria: document.getElementById('categoria').value.trim(),
        preco: parseFloat(document.getElementById('preco').value),
        estoque: parseInt(document.getElementById('estoque').value),
        descricao: document.getElementById('descricao').value.trim()
    };

    try {
        const res = await fetch(API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(produto)
        });

        if (!res.ok) throw new Error('Erro ao salvar');

        toast('Produto cadastrado!', 'sucesso');
        document.getElementById('form-produto').reset();
        showSection('produtos');
        carregarProdutos();
    } catch (err) {
        toast('Erro: ' + err.message, 'erro');
    }
}

// ===== EXCLUIR (DELETE) =====
async function excluir(id) {
    if (!confirm('Tem certeza?')) return;

    try {
        const res = await fetch(`${API}/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Erro ao excluir');

        toast('Produto excluído!', 'sucesso');
        carregarProdutos();
    } catch (err) {
        toast('Erro: ' + err.message, 'erro');
    }
}

// ===== EDITAR (preenche formulário) =====
function editar(id) {
    const p = produtos.find(x => x.id === id);
    if (!p) return;

    document.getElementById('nome').value = p.nome;
    document.getElementById('tipo').value = p.tipo;
    document.getElementById('categoria').value = p.categoria;
    document.getElementById('preco').value = p.preco;
    document.getElementById('estoque').value = p.estoque;
    document.getElementById('descricao').value = p.descricao || '';

    showSection('cadastro');
    toast('Edite e salve o produto', 'sucesso');
}

// ===== UTILITÁRIOS =====
function mostrarLoading(mostrar) {
    document.getElementById('loading').style.display = mostrar ? 'block' : 'none';
}

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