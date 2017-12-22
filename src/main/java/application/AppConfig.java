package application;

import application.services.game.GameTools;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


@Configuration
public class AppConfig {

    @Bean
    @Qualifier("mymapper")
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    @Scope("singleton")
    public ScheduledExecutorService getScheduledExecutorService() {
        return Executors.newScheduledThreadPool(GameTools.EXECUTOR_THREADS_COUNT);
    }
}
