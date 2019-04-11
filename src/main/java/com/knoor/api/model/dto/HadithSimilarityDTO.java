package com.knoor.api.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter 
@NoArgsConstructor
@ToString
public class HadithSimilarityDTO {
	
	private long idOrigin,idTarget;
	private double similarity;

}
