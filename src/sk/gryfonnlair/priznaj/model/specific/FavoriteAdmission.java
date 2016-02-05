package sk.gryfonnlair.priznaj.model.specific;



/**
 * Objekt favorite adaptera drziaci priznanie
 * 
 * @author gryfonn
 * 
 */
public class FavoriteAdmission {

	public final int id;
	public final int originId;
	public final AdmissionType admissionType;
	public final String text;
	/**
	 * <b>Setnut string kt sa ma zobrazit v layoute, prekonertovat pri tvorbe na
	 * final</b>
	 */
	public final String categoryText;

	/**
	 * 
	 * @param id
	 * @param originId
	 * @param admissiontype integer0,1,2
	 * @param text
	 * @param dateTime
	 * @param categoryText string kt sa zobrazi v layoute, stredna
	 *        skola/g/b/vyska
	 */
	public FavoriteAdmission(final int id, final int originId, final AdmissionType admissionType, final String text,
			final String categoryText) {
		this.id = id;
		this.originId = originId;
		this.admissionType = admissionType;
		this.text = text;
		this.categoryText = categoryText;
	}
}
