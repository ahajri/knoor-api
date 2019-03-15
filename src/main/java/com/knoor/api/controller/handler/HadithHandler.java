package com.knoor.api.controller.handler;

import static com.mongodb.client.model.Accumulators.addToSet;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Sorts.descending;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.knoor.api.model.DuplicateInfos;
import com.mongodb.Block;

import reactor.core.publisher.Mono;

@Component
public class HadithHandler {
	
	@Value("${mlab.db.hadith.collection}")
	protected String collectionName;

	@Autowired
	MongoTemplate mongoTemplate;
	
	private static final Logger LOG = LoggerFactory.getLogger(HadithHandler.class); 

	/**
	 * 
	 * @param request
	 * @return
	 */
	public Mono<ServerResponse> handleFullDuplicate(ServerRequest request) {
		final List<DuplicateInfos> result = new LinkedList<>();
		Block<Document> addResultBlock = new Block<Document>() {
	        @Override
	        public void apply(final Document d) {
	        	long total = d.getLong("total");
				List<Long> uniqueIds = (List<Long>) d.get("uniqueIds");
				Document _id = (Document) d.get("_id");
				String hadith = _id.getString("id");
				DuplicateInfos duplicateInfos = new DuplicateInfos(hadith, uniqueIds, total);
				LOG.info("###"+duplicateInfos.toString());
				result.add(duplicateInfos);
	        }
	    };
		com.mongodb.client.MongoCollection<Document> hadiths = mongoTemplate.getCollection(collectionName);
		hadiths.aggregate(
				Arrays.asList(
						group(eq("id", "$hadith"), 
						addToSet("uniqueIds", "$idHadith"), 
						sum("total", 1L)),
						match(gt("total", 1L)), 
						sort(descending("total"))))
				.allowDiskUse(true).batchSize(100).forEach(addResultBlock);
		

		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromObject(result));
	}
}
