package br.unibh.sdm.unimusic_music;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.text.ParseException;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import br.unibh.sdm.unimusic_music.entidades.Music;
import br.unibh.sdm.unimusic_music.persistencia.MusicRepository;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PropertyPlaceholderAutoConfiguration.class, MusicTests.DynamoDBConfig.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MusicTests {
    
    private static Logger LOGGER = LoggerFactory.getLogger(MusicTests.class);

    @Configuration
	@EnableDynamoDBRepositories(basePackageClasses = MusicTests.class)
	public static class DynamoDBConfig {

		@Value("${amazon.aws.accesskey}")
		private String amazonAWSAccessKey;

		@Value("${amazon.aws.secretkey}")
		private String amazonAWSSecretKey;

		public AWSCredentialsProvider amazonAWSCredentialsProvider() {
			return new AWSStaticCredentialsProvider(amazonAWSCredentials());
		}

		@Bean
		public AWSCredentials amazonAWSCredentials() {
			return new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
		}

		@Bean
		public AmazonDynamoDB amazonDynamoDB() {
			return AmazonDynamoDBClientBuilder.standard().withCredentials(amazonAWSCredentialsProvider())
					.withRegion(Regions.US_EAST_1).build();
		}
	}

    @Autowired
    private MusicRepository repository;

    @Test
    public void testCreatMusic() throws ParseException {
        repository.deleteAll();
        LOGGER.info("Creating Objects...");
        Music m1 = new Music("Musica1", "Artista1", "Album1", "Genero1", 1, "music1\\com");
        Music m2 = new Music("Musica2", "Artista2", "Album2", "Genero2", 2, "music2\\com");
        Music m3 = new Music("Musica3", "Artista3", "Album3", "Genero3", 3, "music3\\com");
        Music m4 = new Music("Musica4", "Artista4", "Album4", "Genero4", 4, "music4\\com");
        Music m5 = new Music("Musica5", "Artista5", "Album5", "Genero5", 5, "music5\\com");
        repository.save(m1);
        repository.save(m2);
        repository.save(m3);
        repository.save(m4);
        repository.save(m5);
        Iterable<Music> lista = repository.findAll();
        assertNotNull(lista.iterator());
        for (Music music : lista) {
            LOGGER.info(music.toString());
        }
        LOGGER.info("Searching a object");
        assertEquals(repository.count(), 5);
        LOGGER.info("Encontrado: {} itens", repository.count());
    }

    @Test
    public void testDelete() throws ParseException {
        LOGGER.info("Deleting objects..");
        List<Music> result = repository.findByTitulo("Musica4");
        for (Music music : result) {
            LOGGER.info("Deleting Music titulo = " + music.getTitulo());
            repository.delete(music);
        }
        result = repository.findByTitulo("Musica4");
        assertEquals(result.size(), 0);
        LOGGER.info("Successfully deleted.");
    }

    @Test
    public void testDeleteAll() throws ParseException {
        LOGGER.info("Deleting all objects...");
        repository.deleteAll();
        assertEquals(repository.count(), 0);
        LOGGER.info("Sucessfully deleted.");;
    }
}
