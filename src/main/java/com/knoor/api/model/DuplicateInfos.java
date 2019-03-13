package com.knoor.api.model;

import java.io.Serializable;
import java.util.List;

public class DuplicateInfos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2175511483562824267L;

	private String hadith;
	private List<Long> uniqueIds;
	private int total;

	public DuplicateInfos() {

	}

	public List<Long> getUniqueIds() {
		return uniqueIds;
	}

	public void setUniqueIds(List<Long> uniqueIds) {
		this.uniqueIds = uniqueIds;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getHadith() {
		return hadith;
	}

	public void setHadith(String hadith) {
		this.hadith = hadith;
	}
}
