package com.knoor.api.model;

import java.io.Serializable;
import java.util.List;

public class DuplicateInfos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2175511483562824267L;

	private List<Integer> uniqueIds;
	private int count;

	public DuplicateInfos() {

	}


	public List<Integer> getUniqueIds() {
		return uniqueIds;
	}

	public void setUniqueIds(List<Integer> uniqueIds) {
		this.uniqueIds = uniqueIds;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
