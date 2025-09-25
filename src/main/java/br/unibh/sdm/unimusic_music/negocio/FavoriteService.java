package br.unibh.sdm.unimusic_music.negocio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.unibh.sdm.unimusic_music.persistencia.MusicRepository;
import br.unibh.sdm.unimusic_music.entidades.Music;

public class FavoriteService {
    
    private static Logger LOGGER = LoggerFactory.getLogger(MusicService.class);
    @Autowired
    private MusicRepository repository;
}
