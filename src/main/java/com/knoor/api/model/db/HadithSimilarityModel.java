package com.knoor.api.model.db;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.knoor.api.enums.TXT_SIMILARITY;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter 
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Document(collection = "hadith_similarity")
@JsonIgnoreProperties
public class HadithSimilarityModel {
	
	private long idOrigin,idTarget;
	private TXT_SIMILARITY  similarity;
	double similarityRate;
	private String method;

}
