package com.knoor.api.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DuplicateInfos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2175511483562824267L;

	private String hadith;
	private List<Long> uniqueIds;
	private long total;

	public DuplicateInfos() {

	}
	

	public DuplicateInfos(String hadith, List<Long> uniqueIds, long total) {
		super();
		this.hadith = hadith;
		this.uniqueIds = uniqueIds;
		this.total = total;
	}


	public List<Long> getUniqueIds() {
		return uniqueIds;
	}

	public void setUniqueIds(List<Long> uniqueIds) {
		this.uniqueIds = uniqueIds;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	
	
	public String getHadith() {
		return hadith;
	}


	public void setHadith(String hadith) {
		this.hadith = hadith;
	}


	@Override
	public String toString() {
		return "DuplicateInfos [hadith=" + hadith + ", uniqueIds=" + uniqueIds + ", total=" + total + "]";
	}


	
}
