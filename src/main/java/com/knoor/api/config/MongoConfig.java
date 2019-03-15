package com.knoor.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.mongodb.reactivestreams.client.MongoClient;

@Configuration
public class MongoConfig {

	@Value("${mlab.db.name}")
	protected String dbName;
	
	@Value("${mlab.db.baseUrl}")
	protected String baseUrl;

	@Autowired
	MongoClient reactiveMongoClient;
	
	
	@Autowired
	com.mongodb.MongoClient mongoClient;

	@Bean
	public  WebClient webClient() {
	    return WebClient.builder().clientConnector(new ReactorClientHttpConnector()).baseUrl(baseUrl).build();
	}

	@Bean
	public ReactiveMongoTemplate reactiveMongoTemplate() {
		return new ReactiveMongoTemplate(reactiveMongoClient, dbName);
	}

	@Bean
	public MongoTemplate mongoTemplate() {
		return new MongoTemplate(mongoClient, dbName);
	}

}
