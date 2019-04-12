package com.knoor.api.model.db;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter 
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Document(collection = "hadith_similarity")
public class HadithSimilarityModel {
	
	private long idOrigin,idTarget;
	private double similarity;

}
