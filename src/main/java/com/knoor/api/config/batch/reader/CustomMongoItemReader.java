package com.knoor.api.config.batch.reader;

import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;

import com.knoor.api.model.db.HadithModel;
import com.knoor.api.model.db.HadithSimilarityModel;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CustomMongoItemReader implements ItemReader<Flux<HadithModel>> {
	
	@Autowired
	ReactiveMongoTemplate reactiveMongoTemplate;

	@Override
	public Flux<HadithModel> read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		Flux<HadithSimilarityModel> flux =  reactiveMongoTemplate.findAll(HadithSimilarityModel.class);
		Mono<List<HadithSimilarityModel>> result = flux.collectList();
		return null;
	}

	

}
