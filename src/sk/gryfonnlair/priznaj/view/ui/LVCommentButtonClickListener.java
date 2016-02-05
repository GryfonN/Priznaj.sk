package sk.gryfonnlair.priznaj.view.ui;

import sk.gryfonnlair.priznaj.control.rest.RestDroid;
import sk.gryfonnlair.priznaj.control.social.SocialNetworksImpl;
import sk.gryfonnlair.priznaj.model.specific.AdmissionType;
import sk.gryfonnlair.priznaj.view.comment.FacebookCommentActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class LVCommentButtonClickListener implements View.OnClickListener {

	static final String MSG_NO_INTERNET_CONNECTION = "Pre komentovanie je potrebný internet";

	private int admissionId;
	/**
	 * ak sa nejedna o typ priznania GIRLS_BOYS tak je NULL, inak je to 1
	 * (GIRLS) alebo 2 (BOYS)
	 */
	private Integer gbCategory;
	private AdmissionType admissionType;


	@Override
	public void onClick(final View v) {
		if (!RestDroid.networkAvailable) {
			Toast.makeText(v.getContext(), MSG_NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
			return;
		}
		final String link = SocialNetworksImpl.INSTANCE.getCommentLink(admissionType, gbCategory, admissionId);
		postCommentParams(v.getContext(), putCommentParams(link));
	}

	public void resetClickListener(final int admissionId, final Integer gbCategory, final AdmissionType admissionType) {
		this.admissionId = admissionId;
		this.admissionType = admissionType;
		this.gbCategory = gbCategory;
	}

	private Bundle putCommentParams(final String link) {
		final Bundle commentParams = new Bundle();
		commentParams.putString("link", link);
		return commentParams;
	}

	private void postCommentParams(final Context context, final Bundle value) {
		final Intent intent = new Intent(context, FacebookCommentActivity.class);
		intent.putExtra("admissionToComment", value);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

}
