package com.knoor.api.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.knoor.api.exception.BusinessException;
import com.knoor.api.model.DuplicateInfos;
import com.knoor.api.model.HadithModel;

import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;

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

	// @SuppressWarnings("unchecked")
	// public List<DuplicateInfos> searchFullDuplicate(int start, int page) throws
	// BusinessException {
	//
	// try {
	// final List<DuplicateInfos> result = new LinkedList<>();
	// Block<Document> addResultBlock = new Block<Document>() {
	// @Override
	// public void apply(final Document d) {
	// long total = d.getLong("total");
	// List<Long> uniqueIds = (List<Long>) d.get("uniqueIds");
	// Document _id = (Document) d.get("_id");
	// String hadith = _id.getString("id");
	// DuplicateInfos duplicateInfos = new DuplicateInfos(hadith, uniqueIds, total);
	// LOG.info("###" + duplicateInfos.toString());
	// result.add(duplicateInfos);
	// }
	// };
	// com.mongodb.client.MongoCollection<Document> hadiths =
	// mongoTemplate.getCollection(collectionName);
	// hadiths.aggregate(
	// Arrays.asList(group(eq("id", "$hadith"), addToSet("uniqueIds", "$idHadith"),
	// sum("total", 1L)),
	// match(gt("total", 1L)), skip(start * page), limit(page),
	// sort(descending("total"))))
	// .allowDiskUse(true).forEach(addResultBlock);
	//
	// return result;
	// } catch (Exception e) {
	// throw new BusinessException(e);
	// }
	// }

	/**
	 * 
	 * @param start
	 * @param page
	 * @return
	 * @throws BusinessException
	 */
	public List<DuplicateInfos> getDuplicateHadith(long start, long page) throws BusinessException {
		MatchOperation matchOps = Aggregation.match(Criteria.where("total").gt(1));
		SortOperation sortOps = Aggregation.sort(new Sort(Sort.Direction.DESC, "total"));
		GroupOperation groupOps = Aggregation.group("hadith").last("hadith").as("hadith").addToSet("idHadith")
				.as("uniqueIds").count().as("total");

		LimitOperation limitOps = Aggregation.limit(page);
		SkipOperation skipOps = Aggregation.skip(start * page);

		ProjectionOperation projectOps = project("uniqueIds", "total").and("hadith").previousOperation();

		Aggregation aggregation = Aggregation.newAggregation(groupOps, matchOps, projectOps, limitOps, skipOps, sortOps)
				.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<DuplicateInfos> aggregationResults = mongoTemplate.aggregate(aggregation, HadithModel.class,
				DuplicateInfos.class);

		return aggregationResults.getMappedResults();

	}

	public static void main(String[] args) {
		InputStream is = HadithService.class.getResourceAsStream("text1.txt");
		InputStream is2 = HadithService.class.getResourceAsStream("text2.txt");
		
		String text1 = getText(is);
		
		
		String text2 = getText(is2);
		SimilarityStrategy strategy = new JaroWinklerStrategy();
		StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
		double score = service.score(text1, text2); 
		System.out.println("==> Similarity: "+score);
		
	}

	private static String getText(InputStream is) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sb.toString();

	}

}
