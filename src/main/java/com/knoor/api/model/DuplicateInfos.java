package com.knoor.api.model;

import java.io.Serializable;
import java.util.List;

import org.bson.types.ObjectId;

public class DuplicateInfos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2175511483562824267L;

	//private String id;
	private List<Long> uniqueIds;
	private long total;

	public DuplicateInfos() {

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

//	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}

	
	
	
	@Override
	public String toString() {
		return "DuplicateInfos [id=" + null + ", uniqueIds=" + uniqueIds + ", total=" + total + "]";
	}


	
}
