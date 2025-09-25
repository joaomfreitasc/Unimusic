package br.unibh.sdm.unimusic_music;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

import br.unibh.sdm.unimusic_music.persistencia.DynamoDBConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;


@SpringBootApplication
@Import({DynamoDBConfig.class})
@OpenAPIDefinition(info = @Info(title = "UniMusic API", version = "1.0", description = "API de streaming de Música", 
	license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"),
	contact = @Contact(name = "Suporte da Empresa XPTO", email = "suporte@empresa.com"), 
	termsOfService = "http://empresa.com/termos_uso_api")	)
public class App 
{
    private static final Logger log = LoggerFactory.getLogger(App.class);
    public static void main( String[] args ) {
        log.info("Starting...");
        System.setProperty("server.servlet.context-path", "/music-api");
        new SpringApplicationBuilder(App.class).web(WebApplicationType.SERVLET).run(args);

    }
}
