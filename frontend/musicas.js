const el = (id) => document.getElementById(id);

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

let listaMusicas = [];

async function fetchConfig() {
  const response = await fetch('config.json');
  return response.json();
}

function formatarTempo(s) {
  if (isNaN(s)) return "0:00";
  const m = Math.floor(s / 60);
  const sec = Math.floor(s % 60).toString().padStart(2, "0");
  return `${m}:${sec}`;
}

function formatarData(dataString) {
  if (!dataString) return "Data desconhecida";
  const [ano, mes, dia] = dataString.split("-");
  return `${dia}/${mes}/${ano}`;
}

function atualizarRastroSlider(slider) {
  if (!slider) return;
  const value = ((slider.value - slider.min) / (slider.max - slider.min)) * 100;
  slider.style.background = `linear-gradient(to right, var(--accent) 0%, var(--accent) ${value}%, var(--border-color) ${value}%, var(--border-color) 100%)`;
}

async function getCapaAlbum(musica) {
  try {
    const artista = encodeURIComponent(musica.artista?.nome || "Desconhecido");
    const album = encodeURIComponent(musica.album?.titulo || "Desconhecido");
    const titulo = encodeURIComponent(musica.titulo);

    const config = await fetchConfig();
    const url = `${config.API_URL}/musicas/capa/${artista}/${album}/${titulo}`
    const res = await fetch(url);

    if (!res.ok) {
      console.error(`Erro HTTP! status: ${res.status}`);
      return;
    }

    const arrayBuffer = await res.arrayBuffer();
    const blob = new Blob([arrayBuffer], { type: "audio/mpeg" });
    
    return URL.createObjectURL(blob);

  } catch (err) {
    console.error("Erro ao obter capa do álbum:", err);
    return;
  }
}

async function atualizarInfoMusica(musica) {
  if (!musica) return;

  const album = musica.album;
  const artista = musica.artista;
  const tituloMusica = musica.titulo || "Título Desconhecido";
  const tituloAlbum = album?.titulo || "Álbum Desconhecido";
  const nomeArtista = artista?.nome || "Artista Desconhecido";
  const dataLancamento = formatarData(album?.dataDeLancamento);
  const urlCapa = await getCapaAlbum(musica);

  elementos.infoMusica.textContent = tituloMusica;
  elementos.infoArtista.textContent = nomeArtista;
  elementos.infoAlbum.textContent = tituloAlbum;
  elementos.infoDataLancamento.textContent = dataLancamento;
  if (urlCapa) {
    elementos.capaAlbum.src = urlCapa;
  }
  atualizarFila();
}

function atualizarFila() {
  document.querySelectorAll(".track-item").forEach((item, index) => {
    item.classList.toggle("active", playerGlobal.musicaAtual?.id === listaMusicas[index]?.id);
  });
}

function atualizarBotaoPlay() {
  const playPath = "M7.5 5.625v12.75a.75.75 0 001.14.643l10.5-6.375a.75.75 0 000-1.286L8.64 4.982A.75.75 0 007.5 5.625z";
  const pausePath = "M6 6.75A.75.75 0 016.75 6h.75a.75.75 0 01.75.75v10.5a.75.75 0 01-.75.75h-.75a.75.75 0 01-.75-.75V6.75zM15.75 6a.75.75 0 00-.75.75v10.5a.75.75 0 00.75.75h.75a.75.75 0 00.75-.75V6.75a.75.75 0 00-.75-.75h-.75z";
  const pathElement = elementos.playPauseIcon.querySelector("path");
  if (!pathElement) return;

  if (playerGlobal.estaTocando) {
    pathElement.setAttribute("d", pausePath);
    elementos.playPauseIcon.style.transform = "none";
  } else {
    pathElement.setAttribute("d", playPath);
    elementos.playPauseIcon.style.transform = "translateX(2px)";
  }
}

function atualizarEstadoPlayer() {
  atualizarBotaoPlay();
  atualizarFila();
}

function alternarPlay() {
  if (!playerGlobal.audio.src && listaMusicas.length > 0) {
    playerGlobal.tocarMusica(listaMusicas[0], null, 0);
    return;
  }
  playerGlobal.alternarPlay();
  atualizarEstadoPlayer();
}

function tocarProxima() {
  playerGlobal.proximaMusica(listaMusicas);
}

function tocarAnterior() {
  playerGlobal.musicaAnterior(listaMusicas);
}

function configurarControlesPlayer() {
  if (elementos.volumeSlider) {
    elementos.volumeSlider.oninput = (e) => {
      playerGlobal.audio.volume = e.target.value;
      atualizarRastroSlider(elementos.volumeSlider);
    };
  }

  if (elementos.botaoPlay) elementos.botaoPlay.onclick = alternarPlay;
  if (elementos.botaoProximo) elementos.botaoProximo.onclick = tocarProxima;
  if (elementos.botaoAnterior) elementos.botaoAnterior.onclick = tocarAnterior;

  if (elementos.alternarModoEscuro) {
    elementos.alternarModoEscuro.onclick = () => {
      document.body.classList.toggle("dark-mode");
      localStorage.setItem("darkMode", document.body.classList.contains("dark-mode") ? "enabled" : "disabled");
    };
  }

  window.addEventListener("metadataLoaded", (e) => {
    elementos.tempoTotal.textContent = formatarTempo(e.detail.duration);
    atualizarRastroSlider(elementos.barraProgresso);
  });

  window.addEventListener("timeUpdated", (e) => {
    const { currentTime, duration } = e.detail;
    if (elementos.barraProgresso) {
      elementos.barraProgresso.value = (currentTime / duration) * 100 || 0;
      atualizarRastroSlider(elementos.barraProgresso);
      elementos.tempoAtual.textContent = formatarTempo(currentTime);
    }
  });

  window.addEventListener("trackEnded", tocarProxima);
  window.addEventListener("autoplayFailed", atualizarEstadoPlayer);

  if (elementos.barraProgresso) {
    elementos.barraProgresso.oninput = () => {
      if (playerGlobal.audio.duration) {
        playerGlobal.audio.currentTime =
          (elementos.barraProgresso.value / 100) * playerGlobal.audio.duration;
      }
      atualizarRastroSlider(elementos.barraProgresso);
    };
  }
}

async function buscarMusicas() {
  try {
    const config = await fetchConfig();
    const res = await fetch(`${config.API_URL}/musicas`);
    if (!res.ok) throw new Error(res.statusText);

    let data = await res.json();
    listaMusicas = data.map(m => m.musicaDTO || m);
    elementos.filaDesktop.innerHTML = "";
    atualizarRastroSlider(elementos.volumeSlider);

    if (!playerGlobal.musicaAtual && listaMusicas.length > 0) {
      atualizarInfoMusica(listaMusicas[0]);
      document.querySelector(".music-player").style.display = "flex";
    } else if (listaMusicas.length === 0) {
      document.querySelector(".music-player").style.display = "none";
    }

    if (playerGlobal.musicaAtual) {
        atualizarFila();
    }
    
    listaMusicas.forEach((musica, indice) => {
      const li = document.createElement("li");
      li.className = `track-item ${playerGlobal.musicaAtual?.id === musica.id ? "active" : ""}`;
      li.innerHTML = `
        <span class="track-title">${musica.titulo}</span>
        <span class="track-artist">${musica.artista?.nome}</span>
      `;
      li.onclick = () => {        
        playerGlobal.tocarMusica(musica, null, indice);
      };
      elementos.filaDesktop.appendChild(li);
    });
  } catch (err) {
    console.error("Erro ao buscar músicas:", err);
  }
}

document.addEventListener("DOMContentLoaded", async () => {
  const usuarioLogado = localStorage.getItem("usuarioLogado");
  if (!usuarioLogado) window.location.href = "login.html";

  const estadoSalvo = playerGlobal.restaurarEstado();
  if (estadoSalvo?.musicaAtual) {
    await atualizarInfoMusica(estadoSalvo.musicaAtual);
    document.querySelector(".music-player").style.display = "flex";
    if (elementos.volumeSlider) {
      elementos.volumeSlider.value = estadoSalvo.volume || 1;
      atualizarRastroSlider(elementos.volumeSlider);
    }
    atualizarEstadoPlayer();
  }

  await playerGlobal.restaurarSessao();
  await buscarMusicas();
  configurarControlesPlayer();

  window.addEventListener("estadoPlayerRestaurado", () => {
    const info = playerGlobal.obterInfo();
    if (info.musicaAtual) {
      atualizarInfoMusica(info.musicaAtual);
      atualizarEstadoPlayer();
    }
  });

  window.addEventListener("musicaAlterada", (e) => {
    const { musica } = e.detail;
    atualizarInfoMusica(musica);
    atualizarEstadoPlayer();
});
}); 