package com.knoor.api.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown=true)
public class DuplicateInfos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2175511483562824267L;

	private String cleanedHadith;
	private List<Long> uniqueIds;
	private long total;

	public DuplicateInfos() {

	}
	

	public DuplicateInfos(String cleanedHadith, List<Long> uniqueIds, long total) {
		super();
		this.cleanedHadith = cleanedHadith;
		this.uniqueIds = uniqueIds;
		this.total = total;
	}


	


	@Override
	public String toString() {
		return "DuplicateInfos [cleanedHadith=" + cleanedHadith + ", uniqueIds=" + uniqueIds + ", total=" + total + "]";
	}


	
}
