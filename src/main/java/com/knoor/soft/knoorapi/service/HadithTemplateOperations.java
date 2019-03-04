package com.knoor.soft.knoorapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;

import com.knoor.soft.knoorapi.model.HadithModel;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HadithTemplateOperations {

	@Autowired
	ReactiveMongoTemplate template;

	public Mono<HadithModel> findById(String id) {
		return template.findById(id, HadithModel.class);
	}

	public Flux<HadithModel> findAll() {
		return template.findAll(HadithModel.class);
	}

	public Mono<HadithModel> save(Mono<HadithModel> account) {
		return template.save(account);
	}

}
