package com.knoor.api.service.reactive;

import static com.mongodb.client.model.Accumulators.addToSet;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Sorts.descending;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.knoor.api.exception.BusinessException;
import com.knoor.api.model.DuplicateInfos;
import com.knoor.api.model.HadithModel;
import com.mongodb.Block;
import com.mongodb.reactivestreams.client.AggregatePublisher;
import com.mongodb.reactivestreams.client.MongoCollection;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HadithReactiveService {

	@Autowired
	ReactiveMongoTemplate reactiveMongoTemplate;
	
	private static final Logger LOG = LoggerFactory.getLogger(HadithReactiveService.class);

	@Value("${mlab.db.hadith.collection}")
	protected String collectionName;


	public Mono<HadithModel> reactiveFindById(String id) {
		return reactiveMongoTemplate.findById(id, HadithModel.class);
	}

	public Flux<HadithModel> reactiveFindAll() {
		return reactiveMongoTemplate.findAll(HadithModel.class);
	}

	public Mono<HadithModel> reactiveSave(Mono<HadithModel> account) {
		return reactiveMongoTemplate.save(account);
	}
	
	public Mono<ServerResponse> reactiveSearchDuplicate(ServerRequest request) throws BusinessException {
		try {
			final List<DuplicateInfos> result = new ArrayList<>();
			Block<Document> addResultBlock = new Block<Document>() {
		        @Override
		        public void apply(final Document d) {
		        	long total = d.getLong("total");
					List<Long> uniqueIds = (List<Long>) d.get("uniqueIds");
					Document _id = (Document) d.get("_id");
					String hadith = _id.getString("id");
					DuplicateInfos duplicateInfos = new DuplicateInfos(hadith, uniqueIds, total);
					result.add(duplicateInfos);
		        }
		    };
			MongoCollection<Document> hadiths = reactiveMongoTemplate.getCollection(collectionName);
			AggregatePublisher<Document> publisher =hadiths.aggregate(
					Arrays.asList(
							group(eq("id", "$hadith"), 
							addToSet("uniqueIds", "$idHadith"), 
							sum("total", 1L)),
							match(gt("total", 1L)), 
							sort(descending("total"))))
					.allowDiskUse(true);
			Subscriber s;
			Flux.just(publisher);
			return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromObject(result));

		} catch (Exception e) {
			throw new BusinessException(e);
		}
		
	}

	public Flux<DuplicateInfos> reactiveFullDuplicate() throws BusinessException{
		
		GroupOperation groupOps = Aggregation
				.group("hadith").last("hadith").as("hadith")
				.addToSet("idHadith").as("uniqueIds").count().as("total");
		
		MatchOperation countOps = Aggregation.match(new Criteria("total").gt(1));
		
		SortOperation sortOps =Aggregation.sort(new Sort(Sort.Direction.DESC, "total"));
		
		ProjectionOperation projectionOps = Aggregation.project("hadith","uniqueIds").and("total").previousOperation();
		         
		Aggregation agg = Aggregation.newAggregation(groupOps, countOps, projectionOps, sortOps)
				.withOptions(Aggregation.newAggregationOptions().
				        allowDiskUse(true).build());

		Flux<DuplicateInfos> result = reactiveMongoTemplate.aggregate(agg, HadithModel.class, DuplicateInfos.class);
		return result;

	}

	
	

}
