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

	public List<HadithCount> getDuplicateHadith() throws BusinessException {

		/*******************/

		Aggregation agg1 = newAggregation(
				match(Criteria.where("total").gt(1)),
				group("hadith").count().as("total"), 
				project("total").and("hadith").previousOperation(),
				sort(Sort.Direction.DESC, "total")

		);
		AggregationResults<Object> gr = mongoTemplate
				.aggregate(agg1, HadithModel.class,	Object.class);
		LOG.info("#####000#######" + gr.getMappedResults().size());
		// Convert the aggregation result into a List
		AggregationResults<HadithCount> groupResults1 = mongoTemplate
				.aggregate(agg1, HadithModel.class,	HadithCount.class);
		List<HadithCount> result1 = groupResults1.getMappedResults();

		LOG.info("#####1#######" + result1.size());
		
		return result1;

		/**************/
		/*Criteria filterCriteria = Criteria.where("count").gt(1);
		Sort sort = new Sort(Sort.Direction.DESC, "count");

		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.group("hadith").addToSet("id").as("uniqueIds").count().as("count"),
				Aggregation.match(filterCriteria), Aggregation.sort(sort));

		AggregationResults<DuplicateInfos> aggregationResults = mongoTemplate.aggregate(aggregation, HadithModel.class,
				DuplicateInfos.class);

		List<DuplicateInfos> r = aggregationResults.getMappedResults();
		LOG.info("#######2#######" + r.size());*/

		/******************/
		/*Aggregation agg = newAggregation(group("hadith").addToSet("id").as("uniqueIds").count().as("count"),
				match(Criteria.where("count").gt(1)), project("count").and("uniqueIds").previousOperation(),
				sort(Sort.Direction.DESC, "count")

		);

		// Convert the aggregation result into a List
		AggregationResults<DuplicateInfos> groupResults = mongoTemplate.aggregate(agg, HadithModel.class,
				DuplicateInfos.class);
		List<DuplicateInfos> result = groupResults.getMappedResults();

		return result;*/

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
