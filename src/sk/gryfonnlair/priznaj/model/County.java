package sk.gryfonnlair.priznaj.model;

public class County {

	private int countyId;
	private String countyName;
	private int regionId;

	public County(final int countyId, final String countyName, final int regionId) {
		this.countyId = countyId;
		this.countyName = countyName;
		this.regionId = regionId;
	}

	public int getCountyId() {
		return countyId;
	}

	public void setCountyId(final int countyId) {
		this.countyId = countyId;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(final String countyName) {
		this.countyName = countyName;
	}

	public int getRegionId() {
		return regionId;
	}

	public void setRegionId(final int regionId) {
		this.regionId = regionId;
	}
}
