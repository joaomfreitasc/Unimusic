const el = (id) => document.getElementById(id);

document.addEventListener('DOMContentLoaded', () => {
    const usuarioLogado = localStorage.getItem('usuarioLogado');
    if (!usuarioLogado) {
        window.location.href = 'login.html';
    }
});

const elementos = {
    infoMusica: el("info-song"),
    infoArtista: el("info-artist"),
    botaoPlay: el("play-button"),
    botaoProximo: el("next-button"),
    botaoAnterior: el("previous-button"),
    barraProgresso: el("slider"),
    tempoAtual: el("time-current"),
    tempoTotal: el("time-total"),
    alternarModoEscuro: el("dark-mode-toggle"),
    filaDesktop: el("desktop-queue-view"),
    capaAlbum: el("album-art"),
    infoAlbum: el("info-album"),
    infoDataLancamento: el("info-release-date"),
    volumeSlider: el("volume-slider"),
    playPauseIcon: el("play-pause-icon"),
};

let audio = new Audio();
let musicaAtual = null;
let estaTocando = false;
let indiceAtual = 0;
let listaMusicas = [];

async function fetchConfig() {
    const response = await fetch('config.json');
    return response.json();
}

const formatarTempo = (s) => {
    if (isNaN(s)) return "0:00";
    const m = Math.floor(s / 60);
    const sec = Math.floor(s % 60).toString().padStart(2, "0");
    return `${m}:${sec}`;
};

function formatarData(dataString) {
    if (!dataString) return "Data desconhecida";
    const [ano, mes, dia] = dataString.split("-");
    return `${dia}/${mes}/${ano}`;
}

function atualizarRastroSlider(slider) {
    const value = (slider.value - slider.min) / (slider.max - slider.min) * 100;
    slider.style.background = `linear-gradient(to right, var(--accent) 0%, var(--accent) ${value}%, var(--border-color) ${value}%, var(--border-color) 100%)`;
}

async function buscarMusicas() {
    try {
        const config = await fetchConfig();
        const res = await fetch(`${config.API_URL}/musicas`);
        if (!res.ok) throw new Error(res.statusText);
        listaMusicas = await res.json();
        elementos.filaDesktop.innerHTML = "";

        atualizarRastroSlider(elementos.volumeSlider);


        if (listaMusicas.length > 0) {
            indiceAtual = 0;
            atualizarInfoMusica(listaMusicas[indiceAtual]);
            document.querySelector(".music-player").style.display = "flex";
        } else {
            document.querySelector(".music-player").style.display = "none";
        }


        listaMusicas.forEach((musica, indice) => {
            const li = document.createElement("li");
            li.className = `track-item ${indice === indiceAtual ? 'active' : ''}`;

            const titulo = document.createElement("span");
            titulo.className = "track-title";
            titulo.textContent = musica.titulo;

            const artista = document.createElement("span");
            artista.className = "track-artist";
            artista.textContent = musica.artista?.nome;

            li.appendChild(titulo);
            li.appendChild(artista);

            li.onclick = () => {
                document.querySelectorAll('.track-item').forEach(item => item.classList.remove('active'));
                li.classList.add('active');

                indiceAtual = indice;
                atualizarInfoMusica(musica);
                tocarMusica(musica);
            };
            elementos.filaDesktop.appendChild(li);
        });
    } catch (err) {
        console.error("Erro ao buscar músicas:", err);
        document.querySelector(".music-player").style.display = "flex";
    }
}

function atualizarInfoMusica(musica) {
    const album = musica.artista?.albums?.[0];
    const tituloAlbum = album?.titulo || "Álbum Desconhecido";
    const dataLancamento = formatarData(album?.dataDeLancamento);
    const urlCapa = album?.capaUrl || "https://via.placeholder.com/400x400?text=UniMusic";

    elementos.infoMusica.textContent = musica.titulo || "Título Desconhecido";
    elementos.infoArtista.textContent = musica.artista?.nome || "Artista Desconhecido";
    elementos.capaAlbum.src = urlCapa;
    elementos.infoAlbum.textContent = tituloAlbum;
    elementos.infoDataLancamento.textContent = dataLancamento;

    document.querySelectorAll('.track-item').forEach((item, index) => {
        item.classList.toggle('active', index === indiceAtual);
    });
}


async function tocarMusica(musica) {
    try {
        const artista = encodeURIComponent(musica.artista?.nome || "Desconhecido");
        const album = encodeURIComponent(musica.artista?.albums?.[0]?.titulo || "Desconhecido");
        const tituloMusica = encodeURIComponent(musica.titulo);

        const config = await fetchConfig();
        const url = `${config.API_URL}/musicas/stream/${artista}/${album}/${tituloMusica}.mp3`;

        const res = await fetch(url);
        if (!res.ok) {
            console.error(`Erro HTTP! status: ${res.status}`);
            return;
        }

        const arrayBuffer = await res.arrayBuffer();
        const blob = new Blob([arrayBuffer], { type: "audio/mpeg" });
        const urlAudio = URL.createObjectURL(blob);

        const volumeAnterior = audio.volume;

        if (audio.src) audio.pause();
        audio = new Audio(urlAudio);
        audio.volume = volumeAnterior;
        musicaAtual = musica;
        estaTocando = true;

        atualizarBotaoPlay();

        audio.play();

        audio.onloadedmetadata = () => {
            elementos.tempoTotal.textContent = formatarTempo(audio.duration);
            atualizarRastroSlider(elementos.barraProgresso); 
        };

        audio.ontimeupdate = () => {
            const { currentTime, duration } = audio;
            elementos.barraProgresso.value = (currentTime / duration) * 100 || 0;
            elementos.tempoAtual.textContent = formatarTempo(currentTime);
            atualizarRastroSlider(elementos.barraProgresso); 
        };

        audio.onended = tocarProxima;

        elementos.barraProgresso.oninput = () => {
            if (audio.duration) {
                audio.currentTime = (elementos.barraProgresso.value / 100) * audio.duration;
            }
            atualizarRastroSlider(elementos.barraProgresso); 
        };

    } catch (err) {
        console.error("Erro ao tocar música:", err);
    }
}

function alternarPlay() {
    if (!audio.src) {
        if (listaMusicas.length > 0) {
            atualizarInfoMusica(listaMusicas[indiceAtual]);
            tocarMusica(listaMusicas[indiceAtual]);
        }
        return;
    }
    if (estaTocando) audio.pause();
    else audio.play();
    estaTocando = !estaTocando;
    atualizarBotaoPlay();
}

function atualizarBotaoPlay() {
    const playIconPath = "M7.5 5.625v12.75a.75.75 0 001.14.643l10.5-6.375a.75.75 0 000-1.286L8.64 4.982A.75.75 0 007.5 5.625z";
    const pauseIconPath = "M6 6.75A.75.75 0 016.75 6h.75a.75.75 0 01.75.75v10.5a.75.75 0 01-.75.75h-.75a.75.75 0 01-.75-.75V6.75zM15.75 6a.75.75 0 00-.75.75v10.5a.75.75 0 00.75.75h.75a.75.75 0 00.75-.75V6.75a.75.75 0 00-.75-.75h-.75z"; // Novo path de Pause (Fill)

    const pathElement = elementos.playPauseIcon.querySelector("path");
    if (!pathElement) return;

    if (estaTocando) {
        pathElement.setAttribute("d", pauseIconPath);
        elementos.playPauseIcon.style.transform = 'none';
    } else {
        pathElement.setAttribute("d", playIconPath);
        elementos.playPauseIcon.style.transform = 'translateX(2px)';
    }
}


function tocarProxima() {
    if (indiceAtual < listaMusicas.length - 1) {
        indiceAtual++;
        const proximaMusica = listaMusicas[indiceAtual];
        atualizarInfoMusica(proximaMusica);
        tocarMusica(proximaMusica);
    } else {
        audio.pause();
        estaTocando = false;
        atualizarBotaoPlay();
    }
}

function tocarAnterior() {
    if (indiceAtual > 0) {
        indiceAtual--;
        const musicaAnterior = listaMusicas[indiceAtual];
        atualizarInfoMusica(musicaAnterior);
        tocarMusica(musicaAnterior);
    }
}

audio.volume = elementos.volumeSlider.value;

elementos.volumeSlider.oninput = (e) => {
    audio.volume = e.target.value;
    atualizarRastroSlider(elementos.volumeSlider); 
};

document.querySelector(".music-player").style.display = "none";

elementos.botaoPlay.onclick = alternarPlay;
elementos.alternarModoEscuro.onclick = () => {
    document.body.classList.toggle("dark-mode");
};

elementos.botaoProximo.onclick = tocarProxima;
elementos.botaoAnterior.onclick = tocarAnterior;

buscarMusicas();