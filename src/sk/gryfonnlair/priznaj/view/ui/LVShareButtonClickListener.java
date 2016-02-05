package sk.gryfonnlair.priznaj.view.ui;

import sk.gryfonnlair.priznaj.control.social.SocialNetworksImpl;
import sk.gryfonnlair.priznaj.model.specific.AdmissionType;
import android.view.View;


public class LVShareButtonClickListener implements View.OnClickListener {

	private int admissionId;
	/**
	 * ak sa nejedna o typ priznania GIRLS_BOYS tak je NULL, inak je to 1
	 * (GIRLS) alebo 2 (BOYS)
	 */
	private Integer gbCategory;
	private AdmissionType admissionType;

	@Override
	public void onClick(final View v) {
		final String link = SocialNetworksImpl.INSTANCE.getShareLink(admissionType, gbCategory, admissionId);
		SocialNetworksImpl.INSTANCE.shareFeed(v.getContext(), link);
	}

	public void resetClickListener(final int admissionId, final Integer gbCategory, final AdmissionType admissionType) {
		this.admissionId = admissionId;
		this.admissionType = admissionType;
		this.gbCategory = gbCategory;
	}
}
