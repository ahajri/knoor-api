package com.knoor.api.model.db;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Document(collection = "hadith")
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true, value = { "_class" })
public class HadithModel {

	@Id
	private ObjectId _id;
	@Indexed(unique = true)
	private int id;
	private int book_id;
	private int bookVol;
	private int numHadith;
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
	private int suite;
	private int validated;
	private int id_maasoum1_id;
	private int id_maasoum2_id;
	private int id_rawi_id;
	private String id_madmoun_id;
	private String id_khousousiat_id;
	
	

}
