package sk.gryfonnlair.priznaj.model;


public class University {

	private int universityId;
	private String universityName;
	private String universityImageResource;

	public University(final int universityId, final String universityName, final String universityImageResource) {
		this.universityId = universityId;
		this.universityName = universityName;
		this.universityImageResource = universityImageResource;
	}

	public int getUniversityId() {
		return universityId;
	}

	public void setUniversityId(final int universityId) {
		this.universityId = universityId;
	}

	public String getUniversityName() {
		return universityName;
	}

	public void setUniversityName(final String universityName) {
		this.universityName = universityName;
	}

	public String getUniversityImageResource() {
		return universityImageResource;
	}

	public void setUniversityImageResource(final String universityImageResource) {
		this.universityImageResource = universityImageResource;
	}
}
