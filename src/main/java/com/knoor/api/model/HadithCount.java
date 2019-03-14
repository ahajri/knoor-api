package com.knoor.api.model;

public class HadithCount {
	
	private String id;
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
	
	
	
	public String getId() {
		return id;
	}

	public void set_id(String id) {
		this.id = id;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "HadithCount [id=" + id + ", hadith=" + hadith + ", total=" + total + "]";
	}
	
	

}
