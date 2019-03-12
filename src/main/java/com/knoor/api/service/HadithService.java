package com.knoor.api.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import  org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.knoor.api.exception.BusinessException;
import com.knoor.api.model.DuplicateInfos;
import com.knoor.api.model.HadithCount;
import com.knoor.api.model.HadithModel;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;


@Service
public class HadithService {
	
	private static final Logger LOG = LoggerFactory.getLogger(HadithService.class);
	
	@Value("${mlab.db.hadith.collection}")
	protected String collectionName;

	@Autowired
	MongoTemplate mongoTemplate;

	public HadithModel findById(String id) {
		return mongoTemplate.findById(id, HadithModel.class);
	}

	public List<HadithModel> findAll() {
		return mongoTemplate.findAll(HadithModel.class);
	}

	public HadithModel save(HadithModel model) {
		return mongoTemplate.save(model);
	}

	public List<DuplicateInfos> findFullDuplicates() {
		Block<Document> printBlock = new Block<Document>() {
	        @Override
	        public void apply(final Document document) {
	            LOG.info(document.toJson());
	        }
	    };
	    
	 MongoCollection<Document> hadithCollection =   mongoTemplate.getDb().getCollection(collectionName);
	 hadithCollection.aggregate(
		      Arrays.asList(
		          Aggregates.project(
		              Projections.fields(
		                    Projections.excludeId(),
		                    Projections.include("name"),
		                    Projections.computed(
		                            "firstCategory",
		                            new Document("$arrayElemAt", Arrays.asList("$categories", 0))
		                    )
		              )
		          )
		      )
		).forEach(printBlock);
		return null;

	}
	
	public List<DuplicateInfos> getDuplicateHadith() throws BusinessException{
		
	
		
		
		Aggregation agg = newAggregation(
			group("hadith").addToSet("id").as("uniqueIds")
			//match(Criteria.where("count").gt(1)),
			//project("count").and("uniqueIds").previousOperation(),
			//sort(Sort.Direction.DESC, "count")
				
		);

		//Convert the aggregation result into a List
		AggregationResults<DuplicateInfos> groupResults 
			= mongoTemplate.aggregate(agg, HadithModel.class, DuplicateInfos.class);
		List<DuplicateInfos> result = groupResults.getMappedResults();
		
		return result;
		
	}
	
	public List<HadithCount> getDuplicateCount() throws BusinessException {
		List<HadithCount> result = null;
		
		try {
			Aggregation agg = newAggregation(
				group("hadith").count().as("count"),
				match(Criteria.where("count").gt(1)),
				project("hadith").and("count").previousOperation(),
				sort(Sort.Direction.DESC, "count")
					
			);

			//Convert the aggregation result into a List
			AggregationResults<HadithCount> groupResults 
				= mongoTemplate.aggregate(agg, HadithModel.class, HadithCount.class);
			 result = groupResults.getMappedResults();
		} catch (Exception e) {
			throw new BusinessException(e);
		}
		
		return result;
		
	}

}
