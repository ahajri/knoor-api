package com.knoor.api.model.db;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Document(collection = "hadith_similarity")
@JsonIgnoreProperties
public class HadithSimilarityModel {
	
	private long idOrigin,idTarget;
	double distance;

}
