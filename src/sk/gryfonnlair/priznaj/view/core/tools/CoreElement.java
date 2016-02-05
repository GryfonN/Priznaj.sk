package sk.gryfonnlair.priznaj.view.core.tools;

import sk.gryfonnlair.priznaj.model.specific.CoreAdmission;


/**
 * Element pre adapter listviewvu priznani
 * 
 * @author gryfonn
 * 
 */
public class CoreElement {

	public final Integer imageViewResource;
	public final CoreAdmission coreAdmission;

	/**
	 * Tvorba priznania
	 * 
	 * @param coreAdmission
	 */
	public CoreElement(final CoreAdmission coreAdmission) {
		this.coreAdmission = coreAdmission;
		imageViewResource = null;
	}

	/**
	 * Tvorba reklamy
	 * 
	 * @param imageViewResource
	 */
	public CoreElement(final int imageViewResource) {
		coreAdmission = null;
		this.imageViewResource = imageViewResource;
	}
}
