package co.za.anele.dagacubesplayerapi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Currency;
import java.util.Locale;

@OpenAPIDefinition(
        info = @Info(
                title = "DagaCubes Player API",
                description = "A casino's game for storing player, balance and transactions",
                contact = @Contact(
                        name = "Anele. M Mdolomba",
                        email = "support@anelemdolomba.co.za",
                        url = "https://anelemdolomba.co.za/"
                ),
                version = "v1",
                license = @License(
                        name = "Apache 2.0",
                        url = "https://github.com/AneleMgciniMdolomba/dagacubes-player-api/LICENSE"
                )
        )
)
@SpringBootApplication
public class DagaCubesPlayerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DagaCubesPlayerApiApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return mapper;
    }

    @Bean
    public Locale locale() {
        return Locale.getDefault();
    }

    @Bean
    public Currency currency(Locale locale) {
        return Currency.getInstance(locale);
    }

}
