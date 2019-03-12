package com.knoor.api.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="hadiths")
public class HadithModel {

	@Id
	private ObjectId _id;
	private long id;
	private long book_id;
	private int bookVol;
	private int numHadith;
	private String titrePri;
	private String titreSec1;
	private String titreSec2;
	private String hadith;
	private String maasoum1;
	private String maasoum2;
	private String sanad;
	private String rawi;
	private boolean suite;
	private boolean validated;
	private int id_maasoum1_id;
	private int id_maasoum2_id;

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getBook_id() {
		return book_id;
	}

	public void setBook_id(long book_id) {
		this.book_id = book_id;
	}

	public int getBookVol() {
		return bookVol;
	}

	public void setBookVol(int bookVol) {
		this.bookVol = bookVol;
	}

	public int getNumHadith() {
		return numHadith;
	}

	public void setNumHadith(int numHadith) {
		this.numHadith = numHadith;
	}

	public String getTitrePri() {
		return titrePri;
	}

	public void setTitrePri(String titrePri) {
		this.titrePri = titrePri;
	}

	public String getTitreSec1() {
		return titreSec1;
	}

	public void setTitreSec1(String titreSec1) {
		this.titreSec1 = titreSec1;
	}

	public String getTitreSec2() {
		return titreSec2;
	}

	public void setTitreSec2(String titreSec2) {
		this.titreSec2 = titreSec2;
	}

	public String getHadith() {
		return hadith;
	}

	public void setHadith(String hadith) {
		this.hadith = hadith;
	}

	public String getMaasoum1() {
		return maasoum1;
	}

	public void setMaasoum1(String maasoum1) {
		this.maasoum1 = maasoum1;
	}

	public String getMaasoum2() {
		return maasoum2;
	}

	public void setMaasoum2(String maasoum2) {
		this.maasoum2 = maasoum2;
	}

	public String getSanad() {
		return sanad;
	}

	public void setSanad(String sanad) {
		this.sanad = sanad;
	}

	public String getRawi() {
		return rawi;
	}

	public void setRawi(String rawi) {
		this.rawi = rawi;
	}

	public boolean isSuite() {
		return suite;
	}

	public void setSuite(boolean suite) {
		this.suite = suite;
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public int getId_maasoum1_id() {
		return id_maasoum1_id;
	}

	public void setId_maasoum1_id(int id_maasoum1_id) {
		this.id_maasoum1_id = id_maasoum1_id;
	}

	public int getId_maasoum2_id() {
		return id_maasoum2_id;
	}

	public void setId_maasoum2_id(int id_maasoum2_id) {
		this.id_maasoum2_id = id_maasoum2_id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HadithModel other = (HadithModel) obj;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		if (id != other.id)
			return false;
		return true;
	}
	
	

}
