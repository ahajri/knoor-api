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
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Term;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.knoor.api.exception.BusinessException;
import com.knoor.api.model.DuplicateInfos;
import com.knoor.api.model.db.HadithModel;
import com.knoor.api.model.db.HadithSimilarityModel;
import com.knoor.api.model.dto.HadithSimilarityDTO;
import com.mongodb.Block;
import com.mongodb.reactivestreams.client.AggregatePublisher;
import com.mongodb.reactivestreams.client.MongoCollection;

import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;
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
	
	public Mono<Long> getTotalCount() {
		return reactiveMongoTemplate.count(new Query(), HadithModel.class);
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
			AggregatePublisher<Document> publisher = hadiths.aggregate(
					Arrays.asList(group(eq("id", "$hadith"), addToSet("uniqueIds", "$idHadith"), sum("total", 1L)),
							match(gt("total", 1L)), sort(descending("total"))))
					.allowDiskUse(true);
			Subscriber s;
			Flux.just(publisher);
			return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromObject(result));

		} catch (Exception e) {
			throw new BusinessException(e);
		}

	}

	/**
	 * 
	 * @return list of duplicate Hadiths
	 * @throws BusinessException
	 */
	public Flux<DuplicateInfos> reactiveSearchFullDuplicate() throws BusinessException {

		GroupOperation groupOps = Aggregation.group("hadith").last("hadith").as("hadith").addToSet("idHadith")
				.as("uniqueIds").count().as("total");

		MatchOperation matchOps = Aggregation.match(new Criteria("total").gt(1));

		SortOperation sortOps = Aggregation.sort(new Sort(Sort.Direction.DESC, "total"));

		ProjectionOperation projectionOps = Aggregation.project("total", "uniqueIds").and("hadith").previousOperation();

		Aggregation agg = Aggregation.newAggregation(groupOps, matchOps, projectionOps, sortOps)
				.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		Flux<DuplicateInfos> result = reactiveMongoTemplate.aggregate(agg, HadithModel.class, DuplicateInfos.class);
		
		return result;

	}
	
	
	
	
	
	

	/**
	 * 
	 * @param idOrigin
	 * @param similarity
	 * @return Flux::Similarity
	 * @throws BusinessException
	 */
	public Flux<HadithSimilarityDTO> reactiveSearchSimilarity(long idOrigin, float similarity) throws BusinessException {
		Query q = new Query();
		q.addCriteria(Criteria.where("idHadith").is(idOrigin));
		Mono<HadithModel> monoHadith = reactiveMongoTemplate.findOne(q, HadithModel.class);
		HadithModel hadith =  monoHadith.block();
		String txt = hadith.getHadith();
		System.out.println("Origin===> "+txt);
		Term t = new Term(txt,Term.Type.PHRASE);
		
		
		TextCriteria criteria = TextCriteria.forDefaultLanguage().diacriticSensitive(false).matchingPhrase(txt); 
		
//		Flux<HadithModel> fluxHadith = reactiveMongoTemplate.find(new Query().addCriteria(Criteria.where("idHadith").ne(idOrigin)), HadithModel.class);
		
		Flux<HadithModel> fluxHadith = reactiveMongoTemplate
				.find(new Query().addCriteria(criteria), HadithModel.class);
		
	 fluxHadith.map(h -> h.getIdHadith()).collectList().block().forEach(h -> System.out.println(h+"\n\n"));
		
		
		return null;
	}
	
	
	/**
	 * 
	 * @param idOrigin: ID of Hadith
	 * @return {@link Flux} of {@link HadithSimilarityDTO}
	 * @throws BusinessException
	 */
	public Mono<List<HadithSimilarityModel>> batchSimilarity(long idOrigin) throws BusinessException {
		Query q = new Query();
		q.addCriteria(Criteria.where("idHadith").is(idOrigin));
		Mono<HadithModel> monoHadith = reactiveMongoTemplate.findOne(q, HadithModel.class);
		HadithModel hadith =  monoHadith.block();
		String originTxt = hadith.getHadith();
		LOG.debug("Origin Hadith ID ===> "+hadith.getIdHadith());
		
		Query antiQ = new Query();
		antiQ.addCriteria(Criteria.where("idHadith").ne(idOrigin));
		
		Flux<HadithModel> otherHadiths =reactiveMongoTemplate.find(antiQ,HadithModel.class);
		
		Mono<List<HadithSimilarityModel>> result = otherHadiths.map(h -> {
			HadithSimilarityModel similarityModel = new HadithSimilarityModel();
			SimilarityStrategy strategy = new JaroWinklerStrategy();
			StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
			double score = service.score(originTxt, h.getHadith()); 
			similarityModel.setIdOrigin(idOrigin);
			similarityModel.setIdTarget(h.getIdHadith());
			similarityModel.setSimilarity(score);
			System.out.println(similarityModel.toString());
			return similarityModel;
		}).collectList();//.block().forEach(h -> System.out.println(h.toString()+"\n\n"));
		
		reactiveMongoTemplate.insertAll(result);
		
		return result;
	}

}
