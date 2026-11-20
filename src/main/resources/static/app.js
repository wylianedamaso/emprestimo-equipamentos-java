let token = localStorage.getItem('token');

const loginSection = document.getElementById('login-section');
const appSection = document.getElementById('app-section');
const userBar = document.getElementById('user-bar');
const userInfo = document.getElementById('user-info');
const alerta = document.getElementById('alerta');

function mostrarAlerta(mensagem, tipo = 'erro') {
    alerta.textContent = mensagem;
    alerta.className = `alerta alerta-${tipo}`;
    setTimeout(() => alerta.classList.add('hidden'), 4000);
}

async function api(url, options = {}) {
    const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const res = await fetch(url, { ...options, headers });
    if (res.status === 401) {
        sair();
        throw new Error('Sessão expirada ou credenciais inválidas.');
    }
    if (res.status === 403) {
        throw new Error('Você não tem permissão para esta ação.');
    }

    const texto = await res.text();
    const dados = texto ? JSON.parse(texto) : null;

    if (!res.ok) {
        const msg = dados?.message || Object.values(dados || {}).join(' | ') || 'Erro na requisição';
        throw new Error(msg);
    }
    return dados;
}

function atualizarTela() {
    if (token) {
        loginSection.classList.add('hidden');
        appSection.classList.remove('hidden');
        userBar.classList.remove('hidden');
        userInfo.textContent = `Logado como ${localStorage.getItem('usuario') || ''}`;
        carregarTudo();
    } else {
        loginSection.classList.remove('hidden');
        appSection.classList.add('hidden');
        userBar.classList.add('hidden');
    }
}

function sair() {
    token = null;
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
    atualizarTela();
}

document.getElementById('btn-logout').addEventListener('click', sair);

// Login
document.getElementById('form-login').addEventListener('submit', async (e) => {
    e.preventDefault();
    const usuario = document.getElementById('usuario').value;
    const senha = document.getElementById('senha').value;
    try {
        const res = await api('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ usuario, senha })
        });
        token = res.token;
        localStorage.setItem('token', token);
        localStorage.setItem('usuario', usuario);
        e.target.reset();
        atualizarTela();
    } catch (err) {
        mostrarAlerta(err.message);
    }
});

// Troca de abas
document.querySelectorAll('.tab').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.tab').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(c => c.classList.add('hidden'));
        btn.classList.add('active');
        document.getElementById(`tab-${btn.dataset.tab}`).classList.remove('hidden');
    });
});

// Listagens
async function carregarEquipamentos() {
    try {
        const lista = await api('/equipamentos');
        const tbody = document.getElementById('lista-equipamentos');
        tbody.innerHTML = lista.map(eq => `
            <tr>
                <td>${eq.id}</td>
                <td>${eq.patrimonio}</td>
                <td>${eq.nome}</td>
                <td>${eq.tipo}</td>
                <td><span class="badge ${eq.disponivel ? 'badge-ok' : 'badge-warn'}">
                    ${eq.disponivel ? 'Disponível' : 'Emprestado'}
                </span></td>
            </tr>
        `).join('');
    } catch (err) {
        mostrarAlerta(err.message);
    }
}

async function carregarColaboradores() {
    const tbody = document.getElementById('lista-colaboradores');
    try {
        const lista = await api('/colaboradores');
        tbody.innerHTML = lista.map(c => `
            <tr>
                <td>${c.id}</td>
                <td>${c.nome}</td>
                <td>${c.email}</td>
                <td>${c.cargo || '-'}</td>
            </tr>
        `).join('');
    } catch {
        tbody.innerHTML = '<tr><td colspan="4">Acesso restrito a administradores.</td></tr>';
    }
}

async function carregarEmprestimos() {
    try {
        const lista = await api('/emprestimos');
        const tbody = document.getElementById('lista-emprestimos');
        tbody.innerHTML = lista.map(emp => {
            let statusBadge = '<span class="badge badge-warn">Em aberto</span>';
            if (emp.dataDevolucao) {
                statusBadge = `<span class="badge badge-ok">Devolvido (${emp.dataDevolucao})</span>`;
            } else if (emp.atrasado) {
                statusBadge = '<span class="badge badge-err">Atrasado</span>';
            }

            const acao = emp.dataDevolucao
                ? '-'
                : `<button onclick="devolver(${emp.id})">Devolver</button>`;

            return `
                <tr>
                    <td>${emp.id}</td>
                    <td>${emp.equipamento.nome} (${emp.equipamento.patrimonio})</td>
                    <td>${emp.colaborador.nome}</td>
                    <td>${emp.dataEmprestimo}</td>
                    <td>${emp.dataPrevistaDevolucao}</td>
                    <td>${statusBadge}</td>
                    <td>${acao}</td>
                </tr>
            `;
        }).join('');
    } catch (err) {
        mostrarAlerta(err.message);
    }
}

function carregarTudo() {
    carregarEquipamentos();
    carregarColaboradores();
    carregarEmprestimos();
}

// Cadastros e ações
document.getElementById('form-equipamento').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        await api('/equipamentos', {
            method: 'POST',
            body: JSON.stringify({
                patrimonio: document.getElementById('eq-patrimonio').value,
                nome: document.getElementById('eq-nome').value,
                tipo: document.getElementById('eq-tipo').value
            })
        });
        e.target.reset();
        mostrarAlerta('Equipamento cadastrado!', 'sucesso');
        carregarEquipamentos();
    } catch (err) {
        mostrarAlerta(err.message);
    }
});

document.getElementById('form-colaborador').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        await api('/colaboradores', {
            method: 'POST',
            body: JSON.stringify({
                nome: document.getElementById('col-nome').value,
                email: document.getElementById('col-email').value,
                cargo: document.getElementById('col-cargo').value
            })
        });
        e.target.reset();
        mostrarAlerta('Colaborador cadastrado!', 'sucesso');
        carregarColaboradores();
    } catch (err) {
        mostrarAlerta(err.message);
    }
});

document.getElementById('form-emprestimo').addEventListener('submit', async (e) => {
    e.preventDefault();
    const eqId = document.getElementById('emp-equipamento-id').value;
    const colId = document.getElementById('emp-colaborador-id').value;
    try {
        await api(`/emprestimos?equipamentoId=${eqId}&colaboradorId=${colId}`, { method: 'POST' });
        e.target.reset();
        mostrarAlerta('Empréstimo registrado!', 'sucesso');
        carregarTudo();
    } catch (err) {
        mostrarAlerta(err.message);
    }
});

window.devolver = async (id) => {
    try {
        await api(`/emprestimos/${id}/devolucao`, { method: 'PUT' });
        mostrarAlerta('Equipamento devolvido!', 'sucesso');
        carregarTudo();
    } catch (err) {
        mostrarAlerta(err.message);
    }
};

// Alternância de tema (Dark Tech padrão / Light)
const btnTema = document.getElementById('btn-tema');
const temaSalvo = localStorage.getItem('tema') || 'dark';

function aplicarTema(tema) {
    document.documentElement.setAttribute('data-theme', tema);
    btnTema.textContent = tema === 'dark' ? '☀️' : '🌙';
    localStorage.setItem('tema', tema);
}

btnTema.addEventListener('click', () => {
    const atual = document.documentElement.getAttribute('data-theme');
    aplicarTema(atual === 'dark' ? 'light' : 'dark');
});

aplicarTema(temaSalvo);
atualizarTela();