package com.knoor.api.service.reactive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Precision;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
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
import org.springframework.stereotype.Service;

import com.knoor.api.enums.TXT_SIMILARITY;
import com.knoor.api.exception.BusinessException;
import com.knoor.api.model.DuplicateInfos;
import com.knoor.api.model.db.HadithModel;
import com.knoor.api.model.db.HadithSimilarityModel;
import com.knoor.api.model.dto.HadithSimilarityDTO;

import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HadithReactiveService {

	private static final Logger LOG = LoggerFactory.getLogger(HadithReactiveService.class);

	private static final List<String> allowedLetters = Arrays.asList("ا", "ب", "ت", "ث", "ج", "ح", "خ", "د", "ذ", "ر",
			"ز", "س", "ش", "ص", "ض", "ط", "ظ", "ع", "غ", "ف", "ق", "ك", "ل", "م", "ن", "ه", "و", "ي", "ء", "١", "٢",
			"٣", "٤", "٥", "٦", "٧", "٨", "٩", "٠", "ة", "أ", "إ", "آ", "ؤ", "ئ", "ى", "؟", "?");

	ReactiveMongoTemplate reactiveMongoTemplate;

	MongoTemplate mongoTemplate;

	@Autowired
	MongoOperations operations;

	@Autowired
	public HadithReactiveService(ReactiveMongoTemplate reactiveMongoTemplate, MongoTemplate mongoTemplate) {
		super();
		this.reactiveMongoTemplate = reactiveMongoTemplate;
		this.mongoTemplate = mongoTemplate;
	}

	@Value("${mlab.db.hadith.collection}")
	protected String collectionName;

	public Mono<HadithModel> reactiveFindByIdHadith(Long idHadith) {
		Query q = new Query(Criteria.where("id").is(idHadith));
		return reactiveMongoTemplate.findOne(q, HadithModel.class);
	}

	public Mono<HadithModel> reactiveFindById(ObjectId id) {
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

	/**
	 * 
	 * @return list of duplicate Hadiths
	 * @throws BusinessException
	 */
	public Flux<DuplicateInfos> reactiveSearchFullDuplicate() throws BusinessException {

		GroupOperation groupOps = Aggregation.group("cleanedHadith").last("cleanedHadith").as("cleanedHadith")
				.addToSet("id").as("uniqueIds").count().as("total");

		MatchOperation matchOps = Aggregation.match(new Criteria("total").gt(1));

		SortOperation sortOps = Aggregation.sort(new Sort(Sort.Direction.DESC, "total"));

		ProjectionOperation projectionOps = Aggregation.project("total", "uniqueIds").and("cleanedHadith")
				.previousOperation();

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
	public Flux<HadithSimilarityDTO> reactiveSearchSimilarity(long idOrigin, float similarity)
			throws BusinessException {
		Query q = new Query();
		q.addCriteria(Criteria.where("id").is(idOrigin));
		Mono<HadithModel> monoHadith = reactiveMongoTemplate.findOne(q, HadithModel.class);
		HadithModel hadith = monoHadith.block();
		String txt = hadith.getCleanedHadith();
		System.out.println("Origin===> " + txt);
		Term t = new Term(txt, Term.Type.PHRASE);

		TextCriteria criteria = TextCriteria.forDefaultLanguage().diacriticSensitive(false).matching(t);

		// Flux<HadithModel> fluxHadith = reactiveMongoTemplate.find(new
		// Query().addCriteria(Criteria.where("idHadith").ne(idOrigin)),
		// HadithModel.class);

		Flux<HadithModel> fluxHadith = reactiveMongoTemplate.find(new Query().addCriteria(criteria), HadithModel.class);

		fluxHadith.map(h -> h.getId()).collectList().block().forEach(h -> System.out.println(h + "\n\n"));

		return null;
	}

	/**
	 * 169319 74987
	 * 
	 * @param idOrigin: ID of Hadith
	 * @return {@link Flux} of {@link HadithSimilarityDTO}
	 * @throws BusinessException
	 */
	public Mono<List<HadithSimilarityModel>> batchSimilarity(long idOrigin, double similarityRate)
			throws BusinessException {
		Query q = new Query();
		q.addCriteria(Criteria.where("id").is(idOrigin));
		Mono<HadithModel> monoHadith = reactiveMongoTemplate.findOne(q, HadithModel.class);
		HadithModel hadith = monoHadith.block();
		String originTxt = hadith.getCleanedHadith();

		Query antiQ = new Query();
		antiQ.addCriteria(Criteria.where("id").ne(idOrigin));

		Flux<HadithModel> otherHadiths = reactiveMongoTemplate.find(antiQ, HadithModel.class);
		SimilarityStrategy strategy = new JaroWinklerStrategy();
		StringSimilarityService service = new StringSimilarityServiceImpl(strategy);

		Mono<List<HadithSimilarityModel>> result = otherHadiths.filter(h -> {
			double score = service.score(originTxt, h.getCleanedHadith());
			double presicision = Precision.round(score, 2);
			if (presicision >= similarityRate) {
				System.out.println(
						String.format("Similarity between %d and %d is %.2f", idOrigin, h.getId(), presicision));
			}
			return presicision >= similarityRate;
		}).map(h -> {
			HadithSimilarityModel similarityModel = new HadithSimilarityModel();
			double score = service.score(originTxt, h.getCleanedHadith());
			similarityModel.setIdOrigin(idOrigin);
			similarityModel.setIdTarget(h.getId());
			double precision = Precision.round(score, 2);
//			if (precision == 1) {
//				similarityModel.setSimilarity(TXT_SIMILARITY.SIMILAR_100);
//			} else if (precision >= similarityRate) {
//				similarityModel.setSimilarity(TXT_SIMILARITY.SIMILAR_GTE_90);
//			}
			similarityModel.setDistance(precision);
			return similarityModel;
		}).collectList();

		reactiveMongoTemplate.insertAll(result).subscribe();

		return result;
	}

//	public Mono<List<HadithSimilarityModel>> batchSimilarityBis(long idOrigin) throws BusinessException {
//		Query q = new Query();
//		q.addCriteria(Criteria.where("id").is(idOrigin));
//		Mono<HadithModel> monoHadith = reactiveMongoTemplate.findOne(q, HadithModel.class);
//		HadithModel hadith = monoHadith.block();
//		String originTxt = hadith.getHadith();
//		final List<String> originWords = getWords(originTxt);
//		System.out.println(String.format("{origin words : %s}", originWords.toString()));
//
//		Query antiQ = new Query();
//		antiQ.addCriteria(Criteria.where("id").ne(idOrigin));
//
//		Flux<HadithModel> otherHadiths = reactiveMongoTemplate.find(antiQ, HadithModel.class);
//
//		Mono<List<HadithSimilarityModel>> result = otherHadiths.map(h -> {
//			HadithSimilarityModel similarityModel = new HadithSimilarityModel();
//			List<String> targetWords = getWords(h.getCleanedHadith());
//			System.out.println(String.format("{target words : %s}", targetWords.toString()));
//			long totalWords = originWords.size() + targetWords.size();
//			System.out.println(String.format("{total words : %d}", totalWords));
//			Map<String, Double> originWordMap = new HashMap<>();
//			originWords.stream().forEach(w -> {
//				if (originWordMap.containsKey(w)) {
//					double count = originWordMap.get(w);
//					originWordMap.put(w, count + (1 / totalWords));
//				} else {
//					originWordMap.put(w, (new Double(1) / totalWords));
//				}
//			});
//			System.out.println(String.format("{origin map  words : %s}", originWordMap.toString()));
//			// get rate of every word
//
//			Map<String, Double> targetWordMap = new HashMap<>();
//
//			targetWords.stream().forEach(w -> {
//				if (targetWordMap.containsKey(w)) {
//					double count = targetWordMap.get(w);
//					targetWordMap.put(w, count + (1 / totalWords));
//				} else {
//					targetWordMap.put(w, (new Double(1) / totalWords));
//				}
//			});
//			System.out.println(String.format("{target map  words : %s}", targetWordMap.toString()));
//			//
//			long originSize = originWordMap.size();
//			long targetSize = targetWordMap.size();
//
//			final Map<String, Double> scoreMap = new HashMap<>();
//
//			if (originSize >= targetSize) {
//				originWordMap.entrySet().forEach(e -> {
//					String word = e.getKey();
//
//					double originRate = e.getValue();
//					if (targetWordMap.containsKey(word)) {
//						double targetRate = targetWordMap.get(word);
//						scoreMap.put(word, originRate + targetRate);
//					} else {
//						scoreMap.put(word, originRate);
//					}
//				});
//
//			} else {
//				targetWordMap.entrySet().forEach(e -> {
//					String word = e.getKey();
//					double targetRate = e.getValue();
//					if (originWordMap.containsKey(word)) {
//						double originRate = originWordMap.get(word);
//						if (targetRate > originRate) {
//							scoreMap.put(word, originRate / targetRate);
//						} else {
//							scoreMap.put(word, targetRate / originRate);
//						}
//
//					} else {
//						scoreMap.put(word, Double.valueOf(1));
//					}
//				});
//			}
//
//			double score = Precision.round(scoreMap.values().stream().reduce(0.0, (x, y) -> x + y) / totalWords, 2);
//			System.out.println("[Origin: " + idOrigin + ", Target: " + h.getId() + ", similarity: " + score);
//
//			similarityModel.setIdOrigin(idOrigin);
//			similarityModel.setIdTarget(h.getId());
//			double presicision = Precision.round(score, 2);
//			if (presicision == 1) {
//				similarityModel.setSimilarity(TXT_SIMILARITY.SIMILAR_100);
//			} else if (presicision >= 0.8) {
//				similarityModel.setSimilarity(TXT_SIMILARITY.SIMILAR_GTE_80);
//			}
//			similarityModel.setMethod("CUSTOM-JAVA");
//			return similarityModel;
//		}).collectList();
//
//		// reactiveMongoTemplate.insertAll(result).subscribe();
//
//		return result;
//	}

	public void refineText() throws BusinessException {
		List<HadithModel> hadiths = mongoTemplate.findAll(HadithModel.class);

		hadiths.stream().forEach(h -> {
			String refinedText = getCleanedText(h.getHadith(), h.getSanad()).trim();
			h.setCleanedHadith(refinedText);
			HadithModel savedHadith = mongoTemplate.save(h);
			LOG.debug(String.format("Hadith %d refined", savedHadith.getId()));
		});

	}

	public void batchSimilarity() throws BusinessException {

		Query query = new Query();

		List<HadithModel> hadiths = mongoTemplate.find(query, HadithModel.class);

		hadiths.stream().forEach(h -> {
			h.setCleanedHadith(getCleanedText(h.getHadith(), h.getSanad()).trim());
		});

		// FIXME mongoTemplate.remove(HadithSimilarityModel.class); int totalSize =
		long totalSize = hadiths.size();

		List<HadithSimilarityModel> similarities = new LinkedList<>();

		for (int i = 0; i < totalSize; i++) {
			String txt1 = hadiths.get(i).getHadith();
			for (int j = i + 1; j < totalSize; j++) {
				if (i == j) {
					continue;
				}
				String txt2 = hadiths.get(j).getHadith();
				HadithSimilarityModel similarityModel = new HadithSimilarityModel();
				SimilarityStrategy strategy = new JaroWinklerStrategy();
				StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
				double score = service.score(txt1, txt2);
				similarityModel.setIdOrigin(hadiths.get(i).getId());
				similarityModel.setIdTarget(hadiths.get(j).getId());
				similarityModel.setDistance(Precision.round(score, 2));
//				similarityModel.setSimilarity(TXT_SIMILARITY.SIMILAR_GTE_90);
				similarities.add(similarityModel);

				if (j == totalSize - 1) {
					mongoTemplate.insertAll(similarities);
					similarities.clear();
					System.out.println("====> insert & clear");
				}

			}
		}

	}

	private String getCleanedText(String txt, String sanad) {
		String txt1 = getCleanedText(txt);
		String sanad1 = StringUtils.EMPTY;
		// substring sanad
		if(StringUtils.isNotBlank(sanad)) {
			sanad1 = getCleanedText(sanad);
			txt1 = txt.replace(sanad1, "");
		}
		
		return txt1;
	}

	private String getCleanedText(String txt) {
		StringBuffer buffer = new StringBuffer();
		getWords(txt).stream().forEach(w -> {
			buffer.append(getCleanWord(w));
			buffer.append(" ");
		});
		return buffer.toString();
	}

	private String getCleanWord(String w) {
		char[] t = w.toCharArray();
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < t.length; i++) {
			if (allowedLetters.contains(String.valueOf(t[i]))) {
				buff.append(String.valueOf(t[i]));
			}
		}
		return buff.toString().trim();
	}

	private static List<String> getWords(String SInput) {
		ArrayList<String> all_Words_List = new ArrayList<String>();
		if (StringUtils.isBlank(SInput)) {
			return all_Words_List;
		}

		List<String> words = Arrays.asList(SInput.split(" "));
		return words;
//		StringBuilder stringBuffer = new StringBuilder(SInput);
//		
//		
//		String SWord = "";
//		for (int i = 0; i < stringBuffer.length(); i++) {
//			Character charAt = stringBuffer.charAt(i);
//			if (Character.isAlphabetic(charAt) || Character.isDigit(charAt)) {
//				SWord = SWord + charAt;
//			} else {
//				if (!SWord.isEmpty())
//					all_Words_List.add(new String(SWord));
//				SWord = "";
//			}
//		}
//		return all_Words_List;
	}

	public static void main(String[] args) {
		String text = "عن أبان بن أبي عياش عن سليم بن قيس الهلالي قال سمعت سلمان الفارسي يقول إذا كان يوم القيامة يؤتى بإبليس مزموما بزمام من نار ويؤتى بزفر مزموما بزمامين من نارفينطلق إليه إبليس فيصرخ ويقول ثكلتك أمك من أنت؟ أنا الذي فتنت الأولين والآخرين وأنا مزموم بزمام واحد وأنت مزموم بزمامينفيقول أنا الذي أمرت فأطعت وأمر الله فعصي";
		String sanad = "عن أبان بن أبي عياش";
		System.out.println(text.replace(sanad, ""));
	}
}
