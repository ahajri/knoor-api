package com.knoor.api.model;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document(collection="hadith_similarity")
public class HadithSimilarityModel {
	
	@Id
	private ObjectId _id;
	
	private List<Long> idHadiths;
	
	private float similarity;

}
