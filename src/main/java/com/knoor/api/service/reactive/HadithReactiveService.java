package com.knoor.api.service.reactive;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.knoor.api.exception.BusinessException;
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

	public Flux<DuplicateInfos> asyncFullDuplicates() throws BusinessException{
		
		GroupOperation groupOps = Aggregation
				.group("hadith").addToSet("id_hadith").as("uniqueIds").count().as("total");
		
		MatchOperation countOps = Aggregation.match(new Criteria("total").gt(1));
		
		Sort sort = new Sort(Sort.Direction.DESC, "total");
		SortOperation sortOps =Aggregation.sort(sort);
		         
		Aggregation agg = Aggregation.newAggregation(groupOps, countOps, sortOps)
				.withOptions(Aggregation.newAggregationOptions().
				        allowDiskUse(true).build());

		Flux<DuplicateInfos> result = reactiveMongoTemplate.aggregate(agg, HadithModel.class, DuplicateInfos.class);
		return result;

	}

	

}
