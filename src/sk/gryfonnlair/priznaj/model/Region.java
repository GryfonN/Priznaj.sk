package sk.gryfonnlair.priznaj.model;

public class Region {

	private int regionId;
	private String regionName;

	public Region(final int regionId, final String regionName) {
		this.regionId = regionId;
		this.regionName = regionName;
	}

	public int getRegionId() {
		return regionId;
	}

	public void setRegionId(final int regionId) {
		this.regionId = regionId;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(final String regionName) {
		this.regionName = regionName;
	}

}
