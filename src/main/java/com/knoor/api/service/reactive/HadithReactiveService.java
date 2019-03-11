package com.knoor.api.service.reactive;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
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

	public List<DuplicateInfos> findFullDuplicates() {
		
//		MatchOperation matchStage = Aggregation
//				.group(new String[]{"hadith"}).match(new Criteria("foo").is("bar"));
//		
//		ProjectionOperation projectStage = Aggregation.project("foo", "bar.baz");
//		         
//		Aggregation aggregation 
//		  = Aggregation.newAggregation(matchStage, projectStage);
//		 
//		AggregationResults<DuplicateInfos> output 
//		  = reactiveMongoTemplate.aggregate(aggregation, "foobar", DuplicateInfos.class);
//		
//		
//		
//		Aggregation agg = newAggregation(
//				pipelineOP1(), 
//				pipelineOP2(), 
//				pipelineOPn());
//
//		Flux<List<DuplicateInfos>> results = reactiveMongoTemplate.aggregate(agg, "hadiths", List.class);
//		List<DuplicateInfos> mappedResult = results.getMappedResults();
		return null;

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
