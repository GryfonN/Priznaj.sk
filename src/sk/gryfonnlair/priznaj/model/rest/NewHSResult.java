package sk.gryfonnlair.priznaj.model.rest;

import java.util.Collection;

import com.google.gson.annotations.SerializedName;

public class NewHSResult {

	@SerializedName("priznania_stredne")
	public Collection<HighSchoolAdmission> priznania_stredne;
}
