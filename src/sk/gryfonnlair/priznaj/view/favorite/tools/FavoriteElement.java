package sk.gryfonnlair.priznaj.view.favorite.tools;

import sk.gryfonnlair.priznaj.model.specific.FavoriteAdmission;


/**
 * Element pre adapter v favorite aktivite
 * 
 * @author gryfonn
 * 
 */
public class FavoriteElement {

	public final Integer imageViewResource;
	public final FavoriteAdmission favoriteAdmission;

	/**
	 * Tvorim priznanie
	 * 
	 * @param fa
	 */
	public FavoriteElement(final FavoriteAdmission fa) {
		imageViewResource = null;
		favoriteAdmission = fa;
	}

	/**
	 * Tvorba Reklamy
	 * 
	 * @param addImageResource
	 */
	public FavoriteElement(final int addImageResource) {
		imageViewResource = addImageResource;
		favoriteAdmission = null;
	}

}
