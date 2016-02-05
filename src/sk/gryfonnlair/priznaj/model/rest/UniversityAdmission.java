package sk.gryfonnlair.priznaj.model.rest;

/**
 * Servr side objekt v JSONe
 * 
 * @author gryfonn
 * 
 */
public class UniversityAdmission {
	public int id;
	public String text;
	public int kategoria;
	public String univerzita;
	public long cas;

	public UniversityAdmission() {
	}

	@Override
	public String toString() {
		return new StringBuilder("UniversityAdmission ").append("id:").append(id).append(",kategoria").append(kategoria).append("uni:").append(univerzita)
				.append(",cas").append(cas).toString();
	}
}
