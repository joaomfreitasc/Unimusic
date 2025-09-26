package br.unibh.sdm.unimusic_music.negocio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.unibh.sdm.unimusic_music.persistencia.MusicaRepository;
import br.unibh.sdm.unimusic_music.entidades.Musica;

public class FavoritoService {
    
    private static Logger LOGGER = LoggerFactory.getLogger(MusicaService.class);
    @Autowired
    private MusicaRepository repository;
}
