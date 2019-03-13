package com.knoor.api.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.knoor.api.exception.BusinessException;
import com.knoor.api.model.DuplicateInfos;
import com.knoor.api.model.HadithCount;
import com.knoor.api.model.HadithModel;

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

	public List<DuplicateInfos> getDuplicateHadith() throws BusinessException {

		
		Criteria filterCriteria = Criteria.where("total").gt(1);
		Sort sort = new Sort(Sort.Direction.DESC, "total");

		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.group("hadith").addToSet("id_hadith").as("uniqueIds").count().as("total"),
				Aggregation.match(filterCriteria), 
				Aggregation.sort(sort))
					.withOptions(Aggregation.newAggregationOptions().
				        allowDiskUse(true).build());
		
		AggregationResults<DuplicateInfos> aggregationResults = mongoTemplate
				.aggregate(aggregation, HadithModel.class,
				DuplicateInfos.class);

		List<DuplicateInfos> result = aggregationResults.getMappedResults();
		result.stream().forEach(i -> LOG.info("###Ids:::::>"+i.toString()));
		LOG.info("#######2#######" + result.size());
		
		
		return result;
		

	}

	
	public List<HadithCount> getDuplicateCount() throws BusinessException {
		List<HadithCount> result = null;

		try {
			Aggregation agg = newAggregation(group("hadith").count().as("total"), 
					match(Criteria.where("total").gt(1)),
					project("hadith").and("total").previousOperation(), 
					sort(Sort.Direction.DESC, "count")

			);

			// Convert the aggregation result into a List
			AggregationResults<HadithCount> groupResults = mongoTemplate.aggregate(agg, HadithModel.class,
					HadithCount.class);
			result = groupResults.getMappedResults();
		} catch (Exception e) {
			throw new BusinessException(e);
		}

		return result;

	}

}
