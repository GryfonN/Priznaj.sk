package sk.gryfonnlair.priznaj.model.rest;

import java.util.Collection;

import com.google.gson.annotations.SerializedName;


/**
 * GSON objekt pre deserializaciu init resultu
 * 
 * @author gryfonn
 * 
 */
public class InitResult {

	@SerializedName("priznania")
	public Collection<UniversityAdmission> priznania;
	@SerializedName("priznania2")
	public Collection<GenderAdmission> priznania2;
	@SerializedName("priznania_stredne")
	public Collection<HighSchoolAdmission> priznania_stredne;

}
