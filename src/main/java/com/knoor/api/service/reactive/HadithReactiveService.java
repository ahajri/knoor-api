package com.knoor.api.service.reactive;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.knoor.api.model.DuplicateInfos;
import com.knoor.api.model.HadithModel;
import com.mongodb.AggregationOptions;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class HadithReactiveService {

	@Autowired
	ReactiveMongoTemplate reactiveMongoTemplate;

	public Mono<HadithModel> reactiveFindById(String id) {
		return reactiveMongoTemplate.findById(id, HadithModel.class);
	}

	public Flux<HadithModel> reactiveFindAll() {
		return reactiveMongoTemplate.findAll(HadithModel.class);
	}

	public Mono<HadithModel> reactiveSave(Mono<HadithModel> account) {
		return reactiveMongoTemplate.save(account);
	}

	public Flux<DuplicateInfos> findFullDuplicates() {
		
		GroupOperation groupOps = Aggregation
				.group("hadith").addToSet("id").as("uniqueIds").count().as("count");
		
		MatchOperation countOps = Aggregation.match(new Criteria("count").gt(1));
//		
//		ProjectionOperation projectStage = Aggregation.project("foo", "bar.baz");
//		         
		Aggregation agg 
		  = Aggregation.newAggregation(groupOps, countOps);
//		 
//		AggregationResults<DuplicateInfos> output 
//		  = reactiveMongoTemplate.aggregate(agg, HadithModel.class, DuplicateInfos.class);
//		
//		
//		
//		Aggregation agg = newAggregation(
//				pipelineOP1(), 
//				pipelineOP2(), 
//				pipelineOPn());
//
		Flux<DuplicateInfos> result = reactiveMongoTemplate.aggregate(agg, HadithModel.class, DuplicateInfos.class);
		return result;

	}

	private AggregationOperation pipelineOPn() {
		
		return null;
	}

	private AggregationOperation pipelineOP2() {
		// TODO Auto-generated method stub
		return null;
	}

	private AggregationOperation pipelineOP1() {
		// TODO Auto-generated method stub
		return null;
	}

}
