const createBtn = document.getElementById("create-btn");
const playlistNameInput = document.getElementById("playlist-name");
const playlistsContainer = document.getElementById("playlists");

let playlists = {};
let listaMusicas = [];

document.addEventListener("DOMContentLoaded", async () => {
  const estadoSalvo = playerGlobal.restaurarEstado();
  if (estadoSalvo && estadoSalvo.musicaAtual) {
    atualizarInfoPlayer(estadoSalvo.musicaAtual);
    const volumeSlider = document.getElementById("volume-slider");
    if (volumeSlider) {
      volumeSlider.value = estadoSalvo.volume || 1;
      atualizarRastroSlider(volumeSlider);
    }
    atualizarEstadoPlayer();
  }

  await playerGlobal.restaurarSessao();

  carregarMusicas();
  carregarPlaylists();
  configurarControlesPlayer();

  window.addEventListener("musicaAlterada", (e) => {
    const { musica } = e.detail;
    atualizarInfoPlayer(musica);
    atualizarEstadoPlayer();
  });
});

// Configurar controles do player
function configurarControlesPlayer() {
  const playButton = document.getElementById("play-button");
  const nextButton = document.getElementById("next-button");
  const prevButton = document.getElementById("previous-button");
  const volumeSlider = document.getElementById("volume-slider");
  const slider = document.getElementById("slider");

  if (playButton) {
    playButton.onclick = () => {
      if (playerGlobal.audio.src) {
        playerGlobal.alternarPlay();
      }
      atualizarEstadoPlayer();
    };
  }

  if (volumeSlider) {
    volumeSlider.oninput = (e) => {
      playerGlobal.audio.volume = e.target.value;
      atualizarRastroSlider(volumeSlider);
    };
  }

  if (nextButton) {
    nextButton.onclick = () => {
      playerGlobal.proximaMusica(playlistAtualLista);
      atualizarEstadoPlayer();
    };
  }

  if (prevButton) {
    prevButton.onclick = () => {
      playerGlobal.musicaAnterior(playlistAtualLista);
      atualizarEstadoPlayer();
    };
  }

  window.addEventListener("metadataLoaded", (e) => {
    const tempoTotal = document.getElementById("time-total");
    if (tempoTotal) tempoTotal.textContent = formatarTempo(e.detail.duration);
    atualizarRastroSlider(slider);
  });

  window.addEventListener("timeUpdated", (e) => {
    const { currentTime, duration } = e.detail;
    if (slider) {
      slider.value = (currentTime / duration) * 100 || 0;
      atualizarRastroSlider(slider);
    }
    const tempoAtual = document.getElementById("time-current");
    if (tempoAtual) tempoAtual.textContent = formatarTempo(currentTime);
  });

  window.addEventListener("trackEnded", () => {
    if (playerGlobal.playlistAtual) {
      playerGlobal.proximaMusica(playlists[playerGlobal.playlistAtual]);
      const info = playerGlobal.obterInfo();
      if (info.musicaAtual) {
        atualizarInfoPlayer(info.musicaAtual);
        atualizarEstadoPlayer();
      }
    }
  });

  window.addEventListener("autoplayFailed", atualizarEstadoPlayer);

  if (slider) {
    slider.oninput = () => {
      if (playerGlobal.audio.duration) {
        playerGlobal.audio.currentTime =
          (slider.value / 100) * playerGlobal.audio.duration;
      }
      atualizarRastroSlider(slider);
    };
  }
}

// Atualizar estado quando voltar à página
window.addEventListener("focus", () => {
  atualizarEstadoPlayer();
});

let playlistAtualLista = [];

// Buscar músicas do servidor
async function carregarMusicas() {
  try {
    const res = await fetch("http://localhost:8080/musicas");
    if (!res.ok) throw new Error(res.statusText);
    listaMusicas = await res.json();
  } catch (err) {
    console.error("Erro ao buscar músicas:", err);
  }
}

// Carregar playlists do localStorage
function carregarPlaylists() {
  const saved = localStorage.getItem("playlists");
  playlists = saved ? JSON.parse(saved) : {};
  renderizarPlaylists();
}

// Salvar playlists no localStorage
function salvarPlaylists() {
  localStorage.setItem("playlists", JSON.stringify(playlists));
}

// Renderizar todas as playlists
function renderizarPlaylists() {
  playlistsContainer.innerHTML = "";

  if (Object.keys(playlists).length === 0) {
    playlistsContainer.innerHTML =
      "<p style='text-align: center; color: var(--text-secondary);'>Nenhuma playlist criada</p>";
    return;
  }

  Object.keys(playlists).forEach((nome) => {
    const playlistDiv = document.createElement("div");
    playlistDiv.className = "playlist";

    const titulo = document.createElement("h3");
    titulo.textContent = nome;
    titulo.style.marginBottom = "1rem";

    const botaoPlayPlaylist = document.createElement("button");
    botaoPlayPlaylist.textContent = "▶ Tocar Playlist";
    botaoPlayPlaylist.onclick = () => tocarPlaylist(nome);
    botaoPlayPlaylist.style.cssText =
      "margin-bottom: 1rem; padding: 0.6rem 1rem; background-color: var(--accent); border: none; border-radius: 6px; cursor: pointer; color: var(--bg-primary); font-weight: 600; width: 100%;";
    botaoPlayPlaylist.onmouseover = () =>
      (botaoPlayPlaylist.style.backgroundColor = "#1ed760");
    botaoPlayPlaylist.onmouseout = () =>
      (botaoPlayPlaylist.style.backgroundColor = "var(--accent)");

    const musicas = playlists[nome];
    const listaMusicas = document.createElement("ul");
    listaMusicas.className = "track-list";

    if (musicas.length === 0) {
      const item = document.createElement("li");
      item.textContent = "Nenhuma música adicionada";
      item.style.color = "var(--text-secondary)";
      listaMusicas.appendChild(item);
    } else {
      musicas.forEach((musica, index) => {
        const item = document.createElement("li");
        item.className = "track-item";
        item.innerHTML = `
                    <span class="track-title">${musica.titulo}</span>
                    <span class="track-artist">${musica.artista?.nome || "Artista desconhecido"}</span>
                    <div style="margin-top: 0.5rem; display: flex; gap: 0.5rem;">
                        <button onclick="removerMusicaPlaylist('${nome}', ${index})" style="padding: 0.3rem 0.6rem; font-size: 0.8rem; background-color: #E53E3E; color: white; border: none; border-radius: 4px; cursor: pointer;">Remover</button>
                    </div>
                `;
        listaMusicas.appendChild(item);
      });
    }

    const botaoAdicionar = document.createElement("button");
    botaoAdicionar.textContent = "+ Adicionar música";
    botaoAdicionar.onclick = () => abrirModalAdicionarMusica(nome);
    botaoAdicionar.style.cssText =
      "margin-top: 1rem; padding: 0.6rem 1rem; background-color: var(--bg-tertiary); border: 1px solid var(--border-color); border-radius: 6px; cursor: pointer; color: var(--text-primary); font-weight: 500; width: 100%;";

    const botaoRemover = document.createElement("button");
    botaoRemover.textContent = "🗑️ Deletar playlist";
    botaoRemover.onclick = () => removerPlaylist(nome);
    botaoRemover.style.cssText =
      "margin-top: 0.5rem; padding: 0.6rem 1rem; background-color: #E53E3E; border: none; border-radius: 6px; cursor: pointer; color: white; font-weight: 500;";

    playlistDiv.appendChild(titulo);
    playlistDiv.appendChild(botaoPlayPlaylist);
    playlistDiv.appendChild(listaMusicas);
    playlistDiv.appendChild(botaoAdicionar);
    playlistDiv.appendChild(botaoRemover);

    playlistsContainer.appendChild(playlistDiv);
  });
}

// Função deslogar
function deslogar() {
  localStorage.removeItem("usuarioLogado");
  window.location.href = "login.html";
}

// Abrir modal para adicionar música
function abrirModalAdicionarMusica(nomePlaylist) {
  if (listaMusicas.length === 0) {
    alert("Nenhuma música disponível para adicionar");
    return;
  }

  let musicasOptions =
    "<select id='musica-select' style='width: 100%; padding: 0.5rem; margin: 1rem 0; border: 1px solid var(--border-color); border-radius: 4px;'>";
  listaMusicas.forEach((musica) => {
    const label = `${musica.titulo} - ${musica.artista?.nome || "Desconhecido"}`;
    musicasOptions += `<option value='${JSON.stringify(musica)}'>${label}</option>`;
  });
  musicasOptions += "</select>";

  const modal = document.createElement("div");
  modal.style.cssText =
    "position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.7); display: flex; justify-content: center; align-items: center; z-index: 1000;";

  modal.innerHTML = `
        <div style="background: var(--bg-secondary); padding: 2rem; border-radius: 8px; max-width: 400px; width: 90%;">
            <h3 style="margin-bottom: 1rem;">Adicionar música à playlist</h3>
            ${musicasOptions}
            <div style="display: flex; gap: 1rem; margin-top: 1.5rem;">
                <button onclick="this.closest('div').parentElement.remove()" style="flex: 1; padding: 0.6rem; background-color: var(--bg-tertiary); border: 1px solid var(--border-color); border-radius: 4px; cursor: pointer;">Cancelar</button>
                <button onclick="confirmarAdicaoMusica('${nomePlaylist}')" style="flex: 1; padding: 0.6rem; background-color: var(--accent); border: none; border-radius: 4px; cursor: pointer; color: var(--bg-primary); font-weight: 500;">Adicionar</button>
            </div>
        </div>
    `;

  document.body.appendChild(modal);
}

// Confirmar adição de música
function confirmarAdicaoMusica(nomePlaylist) {
  const selectElement = document.getElementById("musica-select");
  if (!selectElement) return;

  const musicaSelecionada = JSON.parse(selectElement.value);

  // Verificar se a música já está na playlist
  const jaExiste = playlists[nomePlaylist].some(
    (m) => m.id === musicaSelecionada.id,
  );
  if (jaExiste) {
    alert("Esta música já está nesta playlist");
    return;
  }

  playlists[nomePlaylist].push(musicaSelecionada);
  salvarPlaylists();
  renderizarPlaylists();

  // Fechar modal
  document
    .querySelector('[style*="position: fixed"][style*="z-index: 1000"]')
    .remove();
}

// Remover música da playlist
function removerMusicaPlaylist(nomePlaylist, index) {
  playlists[nomePlaylist].splice(index, 1);
  salvarPlaylists();
  renderizarPlaylists();
}

// Remover playlist inteira
function removerPlaylist(nome) {
  if (confirm(`Tem certeza que deseja deletar a playlist "${nome}"?`)) {
    delete playlists[nome];
    salvarPlaylists();
    renderizarPlaylists();
  }
}

// Atualizar informações da música no player
function atualizarInfoPlayer(musica) {
  const album = musica.artista?.albums?.[0];
  const tituloAlbum = album?.titulo || "Álbum Desconhecido";
  const dataLancamento = formatarData(album?.dataDeLancamento);

  // Se capaUrl não tiver http, adiciona a URL base do servidor
  let urlCapa =
    album?.capaUrl || "https://via.placeholder.com/400x400?text=UniMusic";
  if (urlCapa && !urlCapa.startsWith("http")) {
    urlCapa = "http://localhost:8080/" + urlCapa;
  }

  const infoSong = document.getElementById("info-song");
  const infoArtist = document.getElementById("info-artist");
  const capaAlbum = document.getElementById("album-art");
  const infoAlbum = document.getElementById("info-album");
  const infoRelease = document.getElementById("info-release-date");

  if (infoSong) infoSong.textContent = musica.titulo || "Título Desconhecido";
  if (infoArtist)
    infoArtist.textContent = musica.artista?.nome || "Artista Desconhecido";

  if (capaAlbum) {
    capaAlbum.src = urlCapa;
    // Se a capa falhar em carregar, usa o placeholder
    capaAlbum.onerror = function () {
      this.src = "https://via.placeholder.com/400x400?text=UniMusic";
    };
  }

  if (infoAlbum) infoAlbum.textContent = tituloAlbum;
  if (infoRelease) infoRelease.textContent = dataLancamento;
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

function formatarTempo(s) {
  if (isNaN(s)) return "0:00";
  const m = Math.floor(s / 60);
  const sec = Math.floor(s % 60)
    .toString()
    .padStart(2, "0");
  return `${m}:${sec}`;
}

// Tocar toda a playlist em sequência
async function tocarPlaylist(nomePlaylist) {
  if (!playlists[nomePlaylist] || playlists[nomePlaylist].length === 0) {
    alert("Esta playlist está vazia");
    return;
  }

  playlistAtualLista = playlists[nomePlaylist];
  playerGlobal.tocarMusica(playlists[nomePlaylist][0], nomePlaylist, 0);
  atualizarInfoPlayer(playlists[nomePlaylist][0]);
  atualizarEstadoPlayer();
}

// Tocar música da playlist
async function tocarMusica(nomePlaylist, indice) {
  try {
    playlistAtualLista = playlists[nomePlaylist];
    const musica = playlists[nomePlaylist][indice];

    playerGlobal.tocarMusica(musica, nomePlaylist, indice);
    atualizarInfoPlayer(musica);
    atualizarEstadoPlayer();
  } catch (err) {
    console.error("Erro ao tocar música:", err);
    alert("Erro ao tocar a música");
  }
}

function atualizarBotaoPlay() {
  const playPauseIcon = document.getElementById("play-pause-icon");
  if (!playPauseIcon) return;

  const playIconPath =
    "M7.5 5.625v12.75a.75.75 0 001.14.643l10.5-6.375a.75.75 0 000-1.286L8.64 4.982A.75.75 0 007.5 5.625z";
  const pauseIconPath =
    "M6 6.75A.75.75 0 016.75 6h.75a.75.75 0 01.75.75v10.5a.75.75 0 01-.75.75h-.75a.75.75 0 01-.75-.75V6.75zM15.75 6a.75.75 0 00-.75.75v10.5a.75.75 0 00.75.75h.75a.75.75 0 00.75-.75V6.75a.75.75 0 00-.75-.75h-.75z";

  const pathElement = playPauseIcon.querySelector("path");
  if (!pathElement) return;

  if (playerGlobal.estaTocando) {
    pathElement.setAttribute("d", pauseIconPath);
    playPauseIcon.style.transform = "none";
  } else {
    pathElement.setAttribute("d", playIconPath);
    playPauseIcon.style.transform = "translateX(2px)";
  }
}

function atualizarEstadoPlayer() {
  atualizarBotaoPlay();
}

// Criar nova playlist
createBtn.addEventListener("click", () => {
  const nome = playlistNameInput.value.trim();

  if (!nome) {
    alert("Digite um nome para a playlist");
    return;
  }

  if (playlists[nome]) {
    alert("Playlist com este nome já existe");
    return;
  }

  playlists[nome] = [];
  salvarPlaylists();
  renderizarPlaylists();
  playlistNameInput.value = "";
});

// Dark mode toggle (mesmo do resto do projeto)
const darkModeToggle = document.getElementById("dark-mode-toggle");
if (darkModeToggle) {
  darkModeToggle.onclick = () => {
    document.body.classList.toggle("dark-mode");
  };
}
