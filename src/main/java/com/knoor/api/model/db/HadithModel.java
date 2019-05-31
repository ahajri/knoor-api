package com.knoor.api.model.db;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Document(collection = "hadith")
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true, value = { "_class" })
public class HadithModel {

	@Id
	private ObjectId _id;
	@Indexed(unique = true)
	private Integer id;
	private Integer book_id;
	private Integer bookVol;
	private Integer numHadith;
	private String titrePri;
	private String titreSec1;
	private String titreSec2;
	private String hadith;
	@TextIndexed
	private String cleanedHadith;
	private String maasoum1;
	private String maasoum2;
	private String sanad;
	private String rawi;
	private Integer suite;
	private Integer validated;
	private Integer id_maasoum1_id;
	private Integer id_maasoum2_id;
	private Integer id_rawi_id;
	private Integer id_madmoun_id;
	private Integer id_khousousiat_id;
	
	

}
