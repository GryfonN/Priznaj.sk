package sk.gryfonnlair.priznaj.control.social;

import sk.gryfonnlair.priznaj.model.specific.AdmissionType;
import android.content.Context;

public interface SocialNetworks {
	
	void shareFeed(Context context, String link);
	
	String getShareLink(final AdmissionType admissionType, final Integer gbCategory, final int admissionId);

	String getCommentLink(final AdmissionType admissionType, final Integer gbCategory, final int admissionId);
}
