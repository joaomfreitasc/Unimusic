function aplicarTema(tema) {
    document.body.classList.toggle('dark-mode', tema === 'dark');
}

function alternarTema() {
    const temaAtual = document.body.classList.contains('dark-mode') ? 'dark' : 'light';
    const novoTema = temaAtual === 'dark' ? 'light' : 'dark';
    aplicarTema(novoTema);
}

function salvarDadosUsuarioCache(usuario) {
    localStorage.setItem('usuarioLogado', JSON.stringify(usuario));
}

function obterDadosUsuarioCache() {
    const usuario = localStorage.getItem('usuarioLogado');
    return usuario ? JSON.parse(usuario) : null;
}

function limparDadosUsuarioCache() {
    localStorage.removeItem('usuarioLogado');
}

function mostrarPopUp(mensagem, tipo) {
    const corPrimaria = tipo === 'sucesso' ? '#1DB954' : '#E53E3E';
    
    const icone = tipo === 'sucesso' ? 
        '<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" width="24" height="24"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>' :
        '<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" width="24" height="24"><path stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m-9.303 3.376c-.866 1.5.021 3.375 1.763 3.375h14.586c1.742 0 2.629-1.875 1.763-3.375l-7.28-12.607a1.5 1.5 0 00-2.62 0l-7.28 12.607z" /><path stroke-linecap="round" stroke-linejoin="round" d="M12 15h.007v.008H12v-.008z" /></svg>';

    document.querySelectorAll('.unimusic-popup-modal').forEach(el => el.remove());

    const popUp = document.createElement('div');
    popUp.className = 'unimusic-popup-modal';
    
    popUp.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 9999;
        background-color: var(--bg-secondary, #141414);
        color: var(--text-primary, #ffffff);
        padding: 1rem 1.5rem;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.4);
        max-width: 350px;
        display: flex;
        align-items: center;
        gap: 1rem;
        border-left: 5px solid ${corPrimaria};
        transition: transform 0.3s ease-out, opacity 0.3s ease-out;
        transform: translateX(110%);
        opacity: 0;
    `;
    
    popUp.innerHTML = `
        <div style="color: ${corPrimaria};">${icone}</div>
        <p style="margin: 0; font-weight: 500;">${mensagem}</p>
    `;

    document.body.appendChild(popUp);

    requestAnimationFrame(() => {
        popUp.style.transform = 'translateX(0)';
        popUp.style.opacity = '1';
    });
    
    setTimeout(() => {
        popUp.style.transform = 'translateX(110%)';
        popUp.style.opacity = '0';
        setTimeout(() => popUp.remove(), 300);
    }, 4000);
}

async function login(nomeUsuario, senha) {
    try {
        const res = await fetch('http://localhost:8080/usuarios/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nomeUsuario: nomeUsuario, senha: senha })
        });

        if (res.ok) {
            const usuario = await res.json();
            salvarDadosUsuarioCache(usuario);
            mostrarPopUp('Login realizado com sucesso! Redirecionando...', 'sucesso');
            
            setTimeout(() => {
                window.location.href = 'index.html';
            }, 1000);
        } else {
            mostrarPopUp('Erro ao realizar login. Verifique suas credenciais.', 'erro');
            limparDadosUsuarioCache();
        }
    } catch (err) {
        console.error('Erro ao realizar login:', err);
        mostrarPopUp('Ocorreu um erro na conexão. Tente novamente mais tarde.', 'erro');
        limparDadosUsuarioCache();
    }
}

async function registrar(nomeUsuario, email, senha) {
    try {
        limparDadosUsuarioCache();

        const res = await fetch('http://localhost:8080/usuarios/registrar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nomeUsuario: nomeUsuario, email: email, senha: senha })
        });

        if (res.ok) {
            mostrarPopUp('Usuário registrado com sucesso! Faça login para continuar.', 'sucesso');
            
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 1000);
        } else {
            const mensagemErro = await res.text();
            mostrarPopUp(`Erro ao registrar: ${mensagemErro}`, 'erro');
        }
    } catch (err) {
        console.error('Erro ao registrar usuário:', err);
        mostrarPopUp('Ocorreu um erro ao tentar registrar. Tente novamente mais tarde.', 'erro');
    }
}

function deslogar() {
    limparDadosUsuarioCache();
    mostrarPopUp('Você foi deslogado com sucesso!', 'sucesso');
    
    setTimeout(() => {
        window.location.href = 'login.html';
    }, 1000);
}

document.addEventListener('DOMContentLoaded', () => {
    const botaoDarkToggle = document.getElementById('dark-mode-toggle');
    if (botaoDarkToggle) {
        botaoDarkToggle.onclick = alternarTema;
    }

    const botaoLogin = document.getElementById('botao-login');
    if (botaoLogin) {
        botaoLogin.onclick = () => {
            const nomeUsuario = document.getElementById('login-username').value;
            const senha = document.getElementById('login-password').value;
            login(nomeUsuario, senha);
        };
    }

    const botaoRegistrar = document.getElementById('botao-registrar');
    if (botaoRegistrar) {
        botaoRegistrar.onclick = () => {
            const nomeUsuario = document.getElementById('register-username').value;
            const email = document.getElementById('register-email').value;
            const senha = document.getElementById('register-password').value;
            registrar(nomeUsuario, email, senha);
        };
    }

    const tabLogin = document.getElementById('tab-login');
    const tabRegister = document.getElementById('tab-register');
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    
    if (tabLogin && tabRegister) { 
        tabLogin.addEventListener('click', () => {
            tabLogin.classList.add('active');
            tabRegister.classList.remove('active');
            loginForm.classList.add('active');
            registerForm.classList.remove('active');
        });

        tabRegister.addEventListener('click', () => {
            tabRegister.classList.add('active');
            tabLogin.classList.remove('active');
            registerForm.classList.add('active');
            loginForm.classList.remove('active');
        });
    }
});