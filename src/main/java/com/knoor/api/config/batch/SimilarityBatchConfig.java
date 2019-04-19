//package com.knoor.api.config.batch;
//
//import java.util.HashMap;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.batch.item.data.MongoItemReader;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.domain.Sort.Direction;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.scheduling.annotation.EnableScheduling;
//
//import com.knoor.api.model.db.HadithModel;
//
//@Configuration
//@EnableBatchProcessing
//@EnableScheduling
//public class SimilarityBatchConfig {
//
//	@Autowired
//	private JobBuilderFactory jobBuilderFactory;
//	
//	@Autowired
//	private StepBuilderFactory stepBuilderFactory;
//
//	@Autowired
//	private ReactiveMongoTemplate reactiveMongoTemplate;
//	
//	@Autowired
//	private MongoTemplate mongoTemplate;
//
//	@Bean
//	public Job readReport() throws Exception {
//		return jobBuilderFactory.get("readReport").flow(step1()).end().build();
//	}
//
//	@Bean
//	public Step step1() throws Exception {
//		return stepBuilderFactory.get("step1").<HadithModel, HadithModel>chunk(10).reader(reader()).writer(writer()).build();
//	}
//
//	private ItemWriter<? super HadithModel> writer() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Bean
//	public MongoItemReader<HadithModel> reader() {
//		MongoItemReader<HadithModel> reader = new MongoItemReader<>();
//		reader.setTemplate(mongoTemplate);
//		reader.setSort(new HashMap<String, Sort.Direction>() {
//			{
//				put("id", Direction.DESC);
//			}
//		});
//		reader.setTargetType(HadithModel.class);
//		
//		Query q  =new Query() ;
//		
//		reader.setQuery(q);
//		return reader;
//	}
//
//}
