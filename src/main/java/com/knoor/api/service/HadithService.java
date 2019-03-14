package com.knoor.api.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.knoor.api.exception.BusinessException;
import com.knoor.api.model.DuplicateInfos;
import com.knoor.api.model.HadithCount;
import com.knoor.api.model.HadithModel;
import com.mongodb.Block;
/***/
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;

import java.util.Arrays;
import java.util.LinkedList;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Accumulators.addToSet;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Sorts.descending;

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

	public List<DuplicateInfos> searchFullDuplicate() throws BusinessException {

		try {
			final List<DuplicateInfos> result = new LinkedList<>();
			com.mongodb.client.MongoCollection<Document> hadiths = mongoTemplate.getCollection(collectionName);
			MongoCursor<Document> cursor =hadiths.aggregate(
					Arrays.asList(
							group(eq("id", "$hadith"), 
							addToSet("uniqueIds", "$idHadith"), 
							sum("total", 1L)),
							match(gt("total", 1L)), 
							sort(descending("total"))))
					.allowDiskUse(true).iterator();
			cursor.forEachRemaining(d -> {
				long total = d.getLong("total");
				List<Long> uniqueIds = (List<Long>) d.get("uniqueIds");
				Document _id = (Document) d.get("_id");
				String hadith = _id.getString("id");
				DuplicateInfos duplicateInfos = new DuplicateInfos(hadith, uniqueIds, total);
				result.add(duplicateInfos);
			});
			

			result.stream().forEach(d-> LOG.info("####"+d.toString()));
			return result;
		} catch (Exception e) {
			throw new BusinessException(e);
		}
	}

	// FIXME
	public List<DuplicateInfos> getDuplicateHadith() throws BusinessException {
		// Arrays.asList(group(eq("id", "$hadith"), addToSet("uniqueIds", "$id"),
		// sum("total", 1L)), match(gt("total", 1L)), sort(descending("total")))
		MatchOperation matchOps = Aggregation.match(Criteria.where("total").gt(1));
		SortOperation sortOps = Aggregation.sort(new Sort(Sort.Direction.DESC, "total"));
		GroupOperation groupOps = Aggregation.group("hadith").addToSet("idHadith").as("uniqueIds").count().as("total");

		ProjectionOperation projectOps = project("uniqueIds").and("total").previousOperation();

		Aggregation aggregation = Aggregation.newAggregation(groupOps, matchOps, projectOps, sortOps)
				.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<DuplicateInfos> aggregationResults = mongoTemplate.aggregate(aggregation, HadithModel.class,
				DuplicateInfos.class);

		List<DuplicateInfos> result = aggregationResults.getMappedResults();
		result.stream().forEach(i -> LOG.info("###Ids:::::>" + i.toString()));
		LOG.info("#######2#######" + result.size());

		return result;

	}

	public List<HadithCount> getDuplicateCount() throws BusinessException {
		List<HadithCount> result = null;

		try {
			Aggregation agg = newAggregation(
					org.springframework.data.mongodb.core.aggregation.Aggregation.group("hadith").count().as("total"),
					match(Criteria.where("total").gt(1)), project("hadith").and("total").previousOperation(),
					sort(Sort.Direction.DESC, "total"))
							.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

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
