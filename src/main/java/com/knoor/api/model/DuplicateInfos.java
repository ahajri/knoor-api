package com.knoor.api.model;

import java.io.Serializable;
import java.util.List;

import org.bson.types.ObjectId;

public class DuplicateInfos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2175511483562824267L;

	private String id;
	private List<ObjectId> uniqueIds;
	private int total;

	public DuplicateInfos() {

	}

	public List<ObjectId> getUniqueIds() {
		return uniqueIds;
	}

	public void setUniqueIds(List<ObjectId> uniqueIds) {
		this.uniqueIds = uniqueIds;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	
	
	@Override
	public String toString() {
		return "DuplicateInfos [id=" + id + ", uniqueIds=" + uniqueIds + ", total=" + total + "]";
	}


	
}
