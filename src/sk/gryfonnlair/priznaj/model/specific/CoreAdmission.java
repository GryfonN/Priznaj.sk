package sk.gryfonnlair.priznaj.model.specific;

/**
 * Objekt search adaptera drziaci priznanie
 * 
 * @author gryfonn
 * 
 */
public class CoreAdmission {

	public final int id;
	public final AdmissionType type;
	public final String text;
	/**
	 * <b>Setnut string kt sa ma zobrazit v layoute, prekonertovat pri tvorbe na
	 * final</b>
	 */
	public final String category;
	/**
	 * 0/1 v DB , prevara na boolean droid
	 */
	public boolean favorite;

	/**
	 * 
	 * @param id
	 * @param type
	 * @param text
	 * @param date
	 * @param category custom text kt sa zobrazi dole, generovat v droide napr
	 */
	public CoreAdmission(final int id, final AdmissionType type, final String text, final String category, final boolean favorite) {
		this.id = id;
		this.type = type;
		this.text = text;
		this.category = category;
		this.favorite = favorite;
	}
}
