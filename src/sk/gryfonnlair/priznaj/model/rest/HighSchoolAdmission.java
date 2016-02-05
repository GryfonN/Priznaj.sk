package sk.gryfonnlair.priznaj.model.rest;

/**
 * Server side objekt v JSONe
 * 
 * @author gryfonn
 * 
 */
public class HighSchoolAdmission {

	public int id;
	public String text;
	public int skola;
	public long cas;

	public HighSchoolAdmission() {
	}

	@Override
	public String toString() {
		return new StringBuilder("HighSchoolAdmission ").append("id:").append(id).append(",skola:").append(skola).append(", cas:").append(cas).toString();
	}
}
