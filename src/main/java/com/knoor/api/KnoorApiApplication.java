package com.knoor.api;

import java.util.concurrent.Executor;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.knoor.api.client.HadithWebClient;

@EnableMongoAuditing
@EnableReactiveMongoRepositories
@SpringBootApplication
@EnableAsync
public class KnoorApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(KnoorApiApplication.class, args);
		HadithWebClient hadithWebClient = new HadithWebClient();
		LoggerFactory.getLogger(KnoorApiApplication.class).info("######"+hadithWebClient.getResult());
	}
	
	
	@Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("KNoorLookup-");
        executor.initialize();
        return executor;
    }

}
