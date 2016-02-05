package sk.gryfonnlair.priznaj.model;


public class Gender {

	private int genderId;
	private String genderName;

	public Gender(final int genderId, final String genderName) {
		this.genderId = genderId;
		this.genderName = genderName;
	}

	public int getGenderId() {
		return genderId;
	}

	public void setGenderId(final int genderId) {
		this.genderId = genderId;
	}

	public String getGenderName() {
		return genderName;
	}

	public void setGenderName(final String genderName) {
		this.genderName = genderName;
	}
}
