const createBtn = document.getElementById("create-btn");
const playlistNameInput = document.getElementById("playlist-name");
const playlistsContainer = document.getElementById("playlists");
const darkModeToggle = document.getElementById("dark-mode-toggle");
const addMusicaBtn = document.getElementById('add-musica-btn');
const musicaSelectContainer = document.getElementById('musica-select-container');

let listaPlaylists = [];
let listaMusicas = [];
let playlistAtualLista = [];
let playlistIdSelecionada = null;

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
  const sec = Math.floor(s % 60).toString().padStart(2, "0");
  return `${m}:${sec}`;
}

async function fetchConfig() {
  const response = await fetch('config.json');
  return response.json();
}

async function getCapaAlbum(musica) {
  try {
    const artista = encodeURIComponent(musica.artista?.nome || "Desconhecido");
    const album = encodeURIComponent(musica.album?.titulo || "Desconhecido");
    const titulo = encodeURIComponent(musica.titulo);
    const config = await fetchConfig();
    const url = `${config.API_URL}/musicas/capa/${artista}/${album}/${titulo}`
    const res = await fetch(url);
    if (!res.ok) return;
    const arrayBuffer = await res.arrayBuffer();
    const blob = new Blob([arrayBuffer], { type: "audio/mpeg" });
    return URL.createObjectURL(blob);
  } catch {
    return;
  }
}

async function atualizarInfoPlayer(musica) {
  const album = musica.album;
  const tituloAlbum = album?.titulo || "Álbum Desconhecido";
  const dataLancamento = formatarData(album?.dataDeLancamento);
  const infoSong = document.getElementById("info-song");
  const infoArtist = document.getElementById("info-artist");
  const infoAlbum = document.getElementById("info-album");
  const infoRelease = document.getElementById("info-release-date");
  const capaAlbum = document.getElementById("album-art");
  if (infoSong) infoSong.textContent = musica.titulo || "Título Desconhecido";
  if (infoArtist) infoArtist.textContent = musica.artista?.nome || "Artista Desconhecido";
  if (infoAlbum) infoAlbum.textContent = tituloAlbum;
  if (infoRelease) infoRelease.textContent = dataLancamento;
  const urlCapa = await getCapaAlbum(musica);
  if (capaAlbum && urlCapa) capaAlbum.src = urlCapa;
}

function atualizarBotaoPlay() {
  const playPauseIcon = document.getElementById("play-pause-icon");
  if (!playPauseIcon) return;
  const playIconPath = "M7.5 5.625v12.75a.75.75 0 001.14.643l10.5-6.375a.75.75 0 000-1.286L8.64 4.982A.75.75 0 007.5 5.625z";
  const pauseIconPath = "M6 6.75A.75.75 0 016.75 6h.75a.75.75 0 01.75.75v10.5a.75.75 0 01-.75.75h-.75a.75.75 0 01-.75-.75V6.75zM15.75 6a.75.75 0 00-.75.75v10.5a.75.75 0 00.75.75h.75a.75.75 0 00.75-.75V6.75a.75.75 0 00-.75-.75h-.75z";
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

function configurarControlesPlayer() {
  const playButton = document.getElementById("play-button");
  const nextButton = document.getElementById("next-button");
  const prevButton = document.getElementById("previous-button");
  const volumeSlider = document.getElementById("volume-slider");
  const slider = document.getElementById("slider");
  if (playButton) playButton.onclick = () => { if (playerGlobal.audio.src) playerGlobal.alternarPlay(); atualizarEstadoPlayer(); };
  if (volumeSlider) volumeSlider.oninput = (e) => { playerGlobal.audio.volume = e.target.value; atualizarRastroSlider(volumeSlider); };
  if (nextButton) nextButton.onclick = () => { const lista = listaPlaylists.find(p => p.nome === playerGlobal.playlistAtual)?.musicas || playlistAtualLista; playerGlobal.proximaMusica(lista); atualizarEstadoPlayer(); };
  if (prevButton) prevButton.onclick = () => { const lista = listaPlaylists.find(p => p.nome === playerGlobal.playlistAtual)?.musicas || playlistAtualLista; playerGlobal.musicaAnterior(lista); atualizarEstadoPlayer(); };
  window.addEventListener("metadataLoaded", (e) => { const tempoTotal = document.getElementById("time-total"); if (tempoTotal) tempoTotal.textContent = formatarTempo(e.detail.duration); atualizarRastroSlider(slider); });
  window.addEventListener("timeUpdated", (e) => { const { currentTime, duration } = e.detail; if (slider) { slider.value = (currentTime / duration) * 100 || 0; atualizarRastroSlider(slider); } const tempoAtual = document.getElementById("time-current"); if (tempoAtual) tempoAtual.textContent = formatarTempo(currentTime); });
  window.addEventListener("trackEnded", () => { if (playerGlobal.playlistAtual) { const playlist = listaPlaylists.find(p => p.nome === playerGlobal.playlistAtual); if (playlist) { playerGlobal.proximaMusica(playlist.musicas || []); const info = playerGlobal.obterInfo(); if (info.musicaAtual) { atualizarInfoPlayer(info.musicaAtual); atualizarEstadoPlayer(); } } } });
  window.addEventListener("autoplayFailed", atualizarEstadoPlayer);
  if (slider) slider.oninput = () => { if (playerGlobal.audio.duration) playerGlobal.audio.currentTime = (slider.value / 100) * playerGlobal.audio.duration; atualizarRastroSlider(slider); };
}

async function adicionarMusicaAPlaylist(playlistId, musicaSelecionada) {
  const config = await fetchConfig();
  const url = `${config.API_PLAYLIST_URL}/playlist-api/playlist/${playlistId}/musica`;
  const musicaDto = { musicaId: musicaSelecionada.id, titulo: musicaSelecionada.titulo, artistaNome: musicaSelecionada.artista?.nome || 'Desconhecido' };
  try {
    const res = await fetch(url, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(musicaDto) });
    if (!res.ok) { const erroBody = await res.text(); throw new Error(`Erro ${res.status}: ${res.statusText} - ${erroBody}`); }
    mostrarPopUp("Música adicionada com sucesso!", "sucesso");
    carregarPlaylists();
  } catch (err) {
    console.error("Erro ao adicionar música:", err);
    mostrarPopUp(`Falha ao adicionar música: ${err.message}`, "erro");
  }
}

async function carregarMusicas() {
  try {
    const res = await fetch("http://localhost:8080/musicas");
    if (!res.ok) throw new Error(res.statusText);
    listaMusicas = await res.json();
  } catch (err) {
    console.error("Erro ao buscar músicas:", err);
  }
}

async function getMusica(id) {
  try {
    const res = await fetch("http://localhost:8080/musicas/" + id);
    if (!res.ok) throw new Error(res.statusText);
    return await res.json();
  } catch (err) {
    console.error("Erro ao buscar música:", err);
  }
}

async function carregarPlaylists() {
  try {
    const usuarioLogadoString = localStorage.getItem("usuarioLogado");
    const usuarioLogado = usuarioLogadoString ? JSON.parse(usuarioLogadoString) : null;
    if (!usuarioLogado || !usuarioLogado.id) throw new Error("Usuário não logado ou ID de usuário ausente.");
    const res = await fetch("http://localhost:8081/playlist-api/playlist/usuario/" + usuarioLogado.id);
    if (!res.ok) throw new Error(res.statusText);
    listaPlaylists = await res.json();
  } catch (err) {
    console.error("Erro ao buscar playlists:", err);
  }
  renderizarPlaylists();
}

async function salvarPlaylist(nomePlaylist) {
  try {
    const usuarioLogadoString = localStorage.getItem("usuarioLogado");
    const usuarioLogado = usuarioLogadoString ? JSON.parse(usuarioLogadoString) : null;
    if (!usuarioLogado || !usuarioLogado.id) throw new Error("Usuário não logado ou ID de usuário ausente.");
    const res = await fetch("http://localhost:8081/playlist-api/playlist", { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ nome: nomePlaylist, usuarioId: usuarioLogado.id }) });
    if (!res.ok) throw new Error(res.statusText);
    await carregarPlaylists();
  } catch (err) {
    console.error("Erro ao registrar playlist:", err);
  }
}

async function deletarPlaylist(id) {
  try {
    const res = await fetch("http://localhost:8081/playlist-api/playlist/" + id, { method: 'DELETE', headers: { 'Content-Type': 'application/json' } });
    if (!res.ok) throw new Error(res.statusText);
    await carregarPlaylists();
  } catch (err) {
    console.error("Erro ao deletar playlist:", err);
  }
}

function removerPlaylist(nome, id) {
  if (confirm(`Tem certeza que deseja deletar a playlist "${nome}"?`)) deletarPlaylist(id);
}

async function removerMusicaPlaylist(playlistId, musicaId) {
  const config = await fetchConfig();
  const url = `${config.API_PLAYLIST_URL}/playlist-api/playlist/${playlistId}/musica/${musicaId}`;
  try {
    const res = await fetch(url, { method: 'DELETE', headers: { 'Content-Type': 'application/json' } });
    if (!res.ok) { const erroBody = await res.text(); throw new Error(`Erro ${res.status}: ${res.statusText} - ${erroBody}`); }
    mostrarPopUp("Música removida com sucesso!", "sucesso");
    carregarPlaylists();
  } catch (err) {
    console.error("Erro ao remover música:", err);
    mostrarPopUp(`Falha ao remover música: ${err.message}`, "erro");
  }
}

function renderizarMusicas(musicas, playlist) {
  const lista = document.createElement("ul");
  lista.className = "track-list";
  musicas.forEach((musica, index) => {
    const item = document.createElement("li");
    item.className = "track-item";
    item.onclick = () => tocarMusica(playlist.nome, index);
    item.innerHTML = `
      <span class="track-title">${musica.titulo}</span>
      <span class="track-artist">${musica.artistaNome || musica.artista?.nome || "Artista desconhecido"}</span>
      <div style="margin-top: 0.5rem; display: flex; gap: 0.5rem;">
          <button onclick="event.stopPropagation(); removerMusicaPlaylist('${playlist.id}', '${musica.id}')" style="padding: 0.3rem 0.6rem; font-size: 0.8rem; background-color: #E53E3E; color: white; border: none; border-radius: 4px; cursor: pointer;">Remover</button>
      </div>
    `;
    lista.appendChild(item);
  });
  return lista;
}

function renderizarPlaylists() {
  playlistsContainer.innerHTML = "";
  if (listaPlaylists.length === 0) {
    playlistsContainer.innerHTML = "<p style='text-align: center; color: var(--text-secondary);'>Nenhuma playlist criada</p>";
    return;
  }
  listaPlaylists.forEach((playlist) => {
    const playlistDiv = document.createElement("div");
    playlistDiv.className = "playlist";
    const titulo = document.createElement("h3");
    titulo.textContent = playlist.nome;
    titulo.style.marginBottom = "1rem";
    const botaoPlayPlaylist = document.createElement("button");
    botaoPlayPlaylist.textContent = "▶ Tocar Playlist";
    botaoPlayPlaylist.onclick = () => tocarPlaylist(playlist.nome);
    botaoPlayPlaylist.style.cssText = "margin-bottom: 1rem; padding: 0.6rem 1rem; background-color: var(--accent); border: none; border-radius: 6px; cursor: pointer; color: var(--bg-primary); font-weight: 600; width: 100%;";
    botaoPlayPlaylist.onmouseover = () => (botaoPlayPlaylist.style.backgroundColor = "#1ed760");
    botaoPlayPlaylist.onmouseout = () => (botaoPlayPlaylist.style.backgroundColor = "var(--accent)");
    let listaMusicasElement = document.createElement('ul');
    if (playlist.musicas && playlist.musicas.length > 0) listaMusicasElement = renderizarMusicas(playlist.musicas, playlist);
    const botaoAdicionar = document.createElement("button");
    botaoAdicionar.textContent = "+ Adicionar música";
    botaoAdicionar.onclick = () => abrirModalAdicionarMusica(playlist);
    botaoAdicionar.style.cssText = "margin-top: 1rem; padding: 0.6rem 1rem; background-color: var(--bg-tertiary); border: 1px solid var(--border-color); border-radius: 6px; cursor: pointer; color: var(--text-primary); font-weight: 500; width: 100%;";
    const botaoRemover = document.createElement("button");
    botaoRemover.textContent = "🗑️ Deletar playlist";
    botaoRemover.onclick = () => removerPlaylist(playlist.nome, playlist.id);
    botaoRemover.style.cssText = "margin-top: 0.5rem; padding: 0.6rem 1rem; background-color: #E53E3E; border: none; border-radius: 6px; cursor: pointer; color: white; font-weight: 500; width: 100%;";
    playlistDiv.appendChild(titulo);
    playlistDiv.appendChild(botaoPlayPlaylist);
    playlistDiv.appendChild(listaMusicasElement);
    playlistDiv.appendChild(botaoAdicionar);
    playlistDiv.appendChild(botaoRemover);
    playlistsContainer.appendChild(playlistDiv);
  });
}

function abrirModalAdicionarMusica(playlist) {
  if (listaMusicas.length === 0) { alert("Nenhuma música disponível para adicionar"); return; }
  playlistIdSelecionada = playlist.id;
  let musicasOptions = "<select id='musica-select' style='width: 100%; padding: 0.5rem; margin: 1rem 0; border: 1px solid var(--border-color); border-radius: 4px;'>";
  listaMusicas.forEach((musica) => {
    const musicaDto = musica.musicaDTO;
    const label = `${musicaDto.titulo} - ${musicaDto.artista?.nome || "Desconhecido"}`;
    musicasOptions += `<option value='${JSON.stringify(musicaDto)}'>${label}</option>`;
  });
  musicasOptions += "</select>";
  const modal = document.createElement("div");
  modal.style.cssText = "position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.7); display: flex; justify-content: center; align-items: center; z-index: 1000;";
  modal.innerHTML = `
    <div style="background: var(--bg-secondary); padding: 2rem; border-radius: 8px; max-width: 400px; width: 90%;">
        <h3 style="margin-bottom: 1rem;">Adicionar música à playlist: ${playlist.nome}</h3>
        ${musicasOptions}
        <div style="display: flex; gap: 1rem; margin-top: 1.5rem;">
            <button onclick="this.closest('div').parentElement.remove()" style="flex: 1; padding: 0.6rem; background-color: var(--bg-tertiary); border: 1px solid var(--border-color); border-radius: 4px; cursor: pointer;">Cancelar</button>
            <button onclick="confirmarAdicaoMusica('${playlist.id}')" style="flex: 1; padding: 0.6rem; background-color: var(--accent); border: none; border-radius: 4px; cursor: pointer; color: var(--bg-primary); font-weight: 500;">Adicionar</button>
        </div>
    </div>
  `;
  document.body.appendChild(modal);
}

function confirmarAdicaoMusica(playlistId) {
  const selectElement = document.getElementById("musica-select");
  if (!selectElement) return;
  
  const musicaSelecionada = JSON.parse(selectElement.value);
  const playlist = listaPlaylists.find(p => p.id === playlistId);

  if (playlist) {
    const jaExiste = playlist.musicas.some((m) => m.id === musicaSelecionada.id);
    if (jaExiste) { alert("Esta música já está nesta playlist"); return; }
    adicionarMusicaAPlaylist(playlistId, musicaSelecionada);
  }
  document.querySelector('[style*="position: fixed"][style*="z-index: 1000"]').remove();
}

async function tocarPlaylist(nomePlaylist) {
  const playlist = listaPlaylists.find(p => p.nome === nomePlaylist);
  if (!playlist || !playlist.musicas || playlist.musicas.length === 0) { alert("Esta playlist está vazia"); return; }
  playlistAtualLista = playlist.musicas;
  playerGlobal.tocarMusica(playlist.musicas[0], nomePlaylist, 0);
  atualizarInfoPlayer(playlist.musicas[0]);
  atualizarEstadoPlayer();
}

async function tocarMusica(nomePlaylist, indice) {
  try {
    const playlist = listaPlaylists.find(p => p.nome === nomePlaylist);
    if (!playlist || !playlist.musicas || playlist.musicas.length === 0) return;
    playlistAtualLista = playlist.musicas;
    const musica = await getMusica(playlist.musicas[indice].id);
    playerGlobal.tocarMusica(musica.musicaDTO, nomePlaylist, indice);
    atualizarInfoPlayer(musica.musicaDTO);
    atualizarEstadoPlayer();
  } catch {
    alert("Erro ao tocar a música !");
  }
}

window.addEventListener("focus", () => { atualizarEstadoPlayer(); });

createBtn.addEventListener("click", () => {
  const nome = playlistNameInput.value.trim();
  if (!nome) {
    alert("Digite um nome para a playlist");
    return;
  }

  salvarPlaylist(nome);
  playlistNameInput.value = "";
});

if (darkModeToggle) {
  darkModeToggle.onclick = () => {
    document.body.classList.toggle("dark-mode");
    localStorage.setItem("darkMode", document.body.classList.contains("dark-mode") ? "enabled" : "disabled");
  };
}

document.addEventListener("DOMContentLoaded", async () => {
  const estadoSalvo = playerGlobal.restaurarEstado();
  if (estadoSalvo && estadoSalvo.musicaAtual) {
    await atualizarInfoPlayer(estadoSalvo.musicaAtual);
    const volumeSlider = document.getElementById("volume-slider");
    if (volumeSlider) {
      volumeSlider.value = estadoSalvo.volume || 1;
      atualizarRastroSlider(volumeSlider);
    }
    atualizarEstadoPlayer();
  }

  if (localStorage.getItem("darkMode") === "enabled") {
    document.body.classList.toggle("dark-mode");
  }

  await playerGlobal.restaurarSessao();

  await carregarMusicas();
  await carregarPlaylists();
  configurarControlesPlayer();

  window.addEventListener("musicaAlterada", (e) => {
    const { musica, playlist } = e.detail;
    atualizarInfoPlayer(musica);
    atualizarEstadoPlayer();
    if (playlist) {
      const p = listaPlaylists.find(pl => pl.nome === playlist);
      playlistAtualLista = p ? p.musicas : [];
    }
  });
});
