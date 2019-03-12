package com.knoor.api.model;

public class HadithCount {
	
	private String _id;
	private String hadith;

	private long total;

	public String getHadith() {
		return hadith;
	}

	public void setHadith(String hadith) {
		this.hadith = hadith;
	}

	public long getTotal() {
		return total;
	}
	
	
	
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public void setTotal(long total) {
		this.total = total;
	}

}
