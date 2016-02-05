package sk.gryfonnlair.priznaj.model.rest;

import java.util.Collection;

import com.google.gson.annotations.SerializedName;

public class NewUniResult {

	@SerializedName("priznania")
	public Collection<UniversityAdmission> priznania;
}
