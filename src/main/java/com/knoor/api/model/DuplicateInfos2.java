package com.knoor.api.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DuplicateInfos2 implements Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 528972792446232369L;
	
	private String hadith;
	private List<String> uniqueIds;
	private long total;

	public DuplicateInfos2() {

	}
	

	public DuplicateInfos2(String hadith, List<String> uniqueIds, long total) {
		super();
		this.hadith = hadith;
		this.uniqueIds = uniqueIds;
		this.total = total;
	}


	public List<String> getUniqueIds() {
		return uniqueIds;
	}

	public void setUniqueIds(List<String> uniqueIds) {
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
