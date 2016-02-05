package sk.gryfonnlair.priznaj.model.rest;

/**
 * Server side objekt v JSONe
 * 
 * @author gryfonn
 * 
 */
public class GenderAdmission {

	public int id;
	public String text;
	public int kategoria;
	public long cas;

	public GenderAdmission() {
	}

	@Override
	public String toString() {
		return new StringBuilder("GenderAdmission ").append(" id:").append(id).append(",kategoria:").append(kategoria).append(",cas:").append(cas).toString();
	}
}
