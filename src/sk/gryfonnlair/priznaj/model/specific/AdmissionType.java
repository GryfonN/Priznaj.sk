package sk.gryfonnlair.priznaj.model.specific;


/**
 * Pouzitie napriklad v FavoriteAdmission na uchovanie informacie o type
 * priznania
 * 
 * @author gryfonn
 * 
 */
public enum AdmissionType {

	GIRLS_BOYS(0), HIGH_SCHOOL(1), UNIVERSITY(2);

	private final int identifier;

	private AdmissionType(final int identifier) {
		this.identifier = identifier;
	}

	public int getIdentifier() {
		return identifier;
	}

	/**
	 * Vrati AdmissionType podla ID, ak je id mimo tak vrati null
	 * 
	 * @param identifier id pre type 0gb, 1hs, 2uni
	 * @return null ak zle id, inak vrati enum
	 */
	public static AdmissionType fromIdentifier(final int identifier) {
		for (final AdmissionType ids : values()) {
			if (ids.identifier == identifier) {
				return ids;
			}
		}
		return null;
	}

}
