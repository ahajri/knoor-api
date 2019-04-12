package com.knoor.api;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableMongoAuditing
@EnableReactiveMongoRepositories
@SpringBootApplication(scanBasePackages = {"com.knoor"})
@EnableAsync
public class KnoorApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(KnoorApiApplication.class, args);
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
