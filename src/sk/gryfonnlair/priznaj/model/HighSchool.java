package sk.gryfonnlair.priznaj.model;


public class HighSchool {

	private int highSchoolId;
	private String highSchoolName;
	private int countyId;

	public HighSchool(final int highSchoolId, final String highSchoolName, final int countyId) {
		this.highSchoolId = highSchoolId;
		this.highSchoolName = highSchoolName;
		setCountyId(countyId);
	}

	public int getHighSchoolId() {
		return highSchoolId;
	}

	public void setHighSchoolId(final int highSchoolId) {
		this.highSchoolId = highSchoolId;
	}

	public String getHighSchoolName() {
		return highSchoolName;
	}

	public void setHighSchoolName(final String highSchoolName) {
		this.highSchoolName = highSchoolName;
	}

	public int getCountyId() {
		return countyId;
	}

	public void setCountyId(final int countyId) {
		this.countyId = countyId;
	}

}
