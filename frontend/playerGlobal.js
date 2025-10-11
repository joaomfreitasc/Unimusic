class PlayerGlobal {
  constructor() {
    this.audio = null;
    this.musicaAtual = null;
    this.playlistAtual = null;
    this.indiceAtual = 0;
    this.estaTocando = false;
    this.listaMusicas = [];
    this.lastSaveTime = 0;
    this.isRestoring = false;
  }

  inicializar() {
    if (!this.audio) {
      this.audio = new Audio();
      this.attachEventListeners();
    }
  }

  attachEventListeners() {
    this.audio.onloadedmetadata = () => {
      window.dispatchEvent(
        new CustomEvent("metadataLoaded", {
          detail: { duration: this.audio.duration },
        }),
      );

      if (this.isRestoring) {
        const estadoSalvo = this.obterEstadoSalvo();
        if (estadoSalvo) {
          this.audio.currentTime = estadoSalvo.tempoAtual || 0;
          if (estadoSalvo.estaTocando) {
            this.audio.play().catch((e) => {
              console.warn("Autoplay bloqueado pelo navegador.", e);
              this.estaTocando = false;
              this.salvarEstado();
              window.dispatchEvent(new CustomEvent("autoplayFailed"));
            });
          }
        }
        this.isRestoring = false;
      }
    };

    this.audio.ontimeupdate = () => {
      if (this.audio.currentTime - this.lastSaveTime > 2) {
        this.salvarEstado();
        this.lastSaveTime = this.audio.currentTime;
      }
      window.dispatchEvent(
        new CustomEvent("timeUpdated", {
          detail: {
            currentTime: this.audio.currentTime,
            duration: this.audio.duration,
          },
        }),
      );
    };

    this.audio.onended = () => {
      window.dispatchEvent(new Event("trackEnded"));
    };
  }

  salvarEstado() {
    const estado = {
      musicaAtual: this.musicaAtual,
      playlistAtual: this.playlistAtual,
      indiceAtual: this.indiceAtual,
      estaTocando: this.estaTocando,
      tempoAtual: this.audio?.currentTime || 0,
      volume: this.audio?.volume || 1,
    };
    localStorage.setItem("playerEstado", JSON.stringify(estado));
  }

  obterEstadoSalvo() {
    const estadoSalvo = localStorage.getItem("playerEstado");
    return estadoSalvo ? JSON.parse(estadoSalvo) : null;
  }

  restaurarEstado() {
    const estado = this.obterEstadoSalvo();
    if (estado) {
      this.musicaAtual = estado.musicaAtual;
      this.playlistAtual = estado.playlistAtual;
      this.indiceAtual = estado.indiceAtual;
      this.estaTocando = estado.estaTocando;
      if (this.audio) {
        this.audio.volume = estado.volume || 1;
      }
    }
    return estado;
  }

  // Função para restaurar a música ao carregar a página
  async restaurarSessao() {
    const estadoSalvo = this.restaurarEstado();
    if (!estadoSalvo || !estadoSalvo.musicaAtual) {
      return;
    }

    this.isRestoring = true;

    try {
      const musica = estadoSalvo.musicaAtual;
      const artista = encodeURIComponent(
        musica.artista?.nome || "Desconhecido",
      );
      const album = encodeURIComponent(
        musica.artista?.albums?.[0]?.titulo || "Desconhecido",
      );
      const titulo = encodeURIComponent(musica.titulo);
      const url = `http://localhost:8080/musicas/stream/${artista}/${album}/${titulo}.mp3`;

      const res = await fetch(url);
      if (!res.ok) throw new Error(`Erro HTTP: ${res.status}`);

      const arrayBuffer = await res.arrayBuffer();
      const blob = new Blob([arrayBuffer], { type: "audio/mpeg" });
      this.audio.src = URL.createObjectURL(blob);

      this.audio.onloadedmetadata = () => {
        this.audio.currentTime = estadoSalvo.tempoAtual || 0;
        if (this.estaTocando) {
          this.audio.play().catch((e) => {
            console.warn("Autoplay bloqueado pelo navegador.", e);
            this.estaTocando = false; // Garante que o estado reflita a realidade
            this.salvarEstado();
          });
        }
        // Dispara um evento para que as páginas saibam que o player foi restaurado
        window.dispatchEvent(new CustomEvent("estadoPlayerRestaurado"));
      };
    } catch (err) {
      console.error("Não foi possível restaurar a música:", err);
      this.isRestoring = false;
    }
  }

  async tocarMusica(musica, playlist = null, indice = 0) {
    try {
      this.musicaAtual = musica;
      this.playlistAtual = playlist;
      this.indiceAtual = indice;

      const artista = encodeURIComponent(
        musica.artista?.nome || "Desconhecido",
      );
      const album = encodeURIComponent(
        musica.artista?.albums?.[0]?.titulo || "Desconhecido",
      );
      const titulo = encodeURIComponent(musica.titulo);

      const url = `http://localhost:8080/musicas/stream/${artista}/${album}/${titulo}.mp3`;

      const res = await fetch(url);
      if (!res.ok) {
        console.error(`Erro HTTP! status: ${res.status}`);
        return;
      }

      const arrayBuffer = await res.arrayBuffer();
      const blob = new Blob([arrayBuffer], { type: "audio/mpeg" });
      const urlAudio = URL.createObjectURL(blob);

      this.audio.src = urlAudio;
      this.audio.play();
      this.estaTocando = true;
      this.salvarEstado();

      window.dispatchEvent(
        new CustomEvent("musicaAlterada", {
          detail: { musica, playlist, indice },
        }),
      );
    } catch (err) {
      console.error("Erro ao tocar música:", err);
    }
  }

  pausar() {
    if (this.audio) {
      this.audio.pause();
      this.estaTocando = false;
      this.salvarEstado();
    }
  }

  retomar() {
    if (this.audio && this.audio.src) {
      this.audio.play();
      this.estaTocando = true;
      this.salvarEstado();
    }
  }

  alternarPlay() {
    if (!this.audio || !this.audio.src) return;

    if (this.estaTocando) {
      this.pausar();
    } else {
      this.retomar();
    }
  }

  proximaMusica(listaMusicas) {
    if (this.playlistAtual) {
      if (this.indiceAtual < this.playlistAtual.length - 1) {
        this.indiceAtual++;
        this.tocarMusica(
          listaMusicas[this.indiceAtual],
          this.playlistAtual,
          this.indiceAtual,
        );
      }
    } else {
      const indice = listaMusicas.findIndex(
        (m) => m.id === this.musicaAtual?.id,
      );
      if (indice !== -1 && indice < listaMusicas.length - 1) {
        const proximaMusica = listaMusicas[indice + 1];
        this.tocarMusica(proximaMusica, null, indice + 1);
      }
    }
  }

  musicaAnterior(listaMusicas) {
    if (this.playlistAtual) {
      if (this.indiceAtual > 0) {
        this.indiceAtual--;
        this.tocarMusica(
          this.playlistAtual[this.indiceAtual],
          this.playlistAtual,
          this.indiceAtual,
        );
      }
    } else {
      const indice = listaMusicas.findIndex(
        (m) => m.id === this.musicaAtual?.id,
      );
      if (indice > 0) {
        const musicaAnterior = listaMusicas[indice - 1];
        this.tocarMusica(musicaAnterior, null, indice - 1);
      }
    }
  }

  obterInfo() {
    return {
      musicaAtual: this.musicaAtual,
      playlistAtual: this.playlistAtual,
      indiceAtual: this.indiceAtual,
      estaTocando: this.estaTocando,
      tempoAtual: this.audio?.currentTime || 0,
      duracao: this.audio?.duration || 0,
    };
  }
}

const playerGlobal = new PlayerGlobal();

document.addEventListener("DOMContentLoaded", () => {
  playerGlobal.inicializar();
});