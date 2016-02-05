package sk.gryfonnlair.priznaj.control.social;

import sk.gryfonnlair.priznaj.PriznajApplication;
import sk.gryfonnlair.priznaj.model.specific.AdmissionType;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


public enum SocialNetworksImpl implements SocialNetworks {

	INSTANCE;

	private static final String SHARE_SUBJECT = "Jedno zo skvelých priznaní android aplikácie Priznaj.sk";

	@Override
	public void shareFeed(final Context context, final String link) {
		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, SHARE_SUBJECT);
		intent.putExtra(android.content.Intent.EXTRA_TEXT, link);
		try {
			context.startActivity(android.content.Intent.createChooser(intent, "Zdieľať príspevok"));
		} catch (final Exception e)
		{
			Toast.makeText(context, "Zvolený typ zdieľania nepodporujeme.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public String getShareLink(final AdmissionType admissionType, final Integer gbCategory, final int admissionId) {
		final StringBuilder urlLink = new StringBuilder();
		if (admissionType == AdmissionType.GIRLS_BOYS) {
			if (gbCategory == 1) {
				urlLink.append("http://www.girls.priznaj.sk/");
			} else {
				urlLink.append("http://www.boys.priznaj.sk/");
			}
			urlLink.append("priznanie2.php?");
		} else if (admissionType == AdmissionType.HIGH_SCHOOL) {
			urlLink.append("http://www.stredne.priznaj.sk/");
			urlLink.append("priznanie_stredne.php?");
		} else {
			urlLink.append("http://www.priznaj.sk/");
			urlLink.append("priznanie.php?");
		}
		urlLink.append("id=").append(admissionId);
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "FacebookServiceImpl> getShareLink:" + urlLink.toString());
		}
		return urlLink.toString();
	}

	@Override
	public String getCommentLink(final AdmissionType admissionType, final Integer gbCategory, final int admissionId) {
		final StringBuilder urlLink = new StringBuilder("http://www.priznaj.sk/rest/comments.php");
		if (admissionType == AdmissionType.GIRLS_BOYS) {
			if (gbCategory == 1) {
				urlLink.append("?typ=3");
			} else {
				urlLink.append("?typ=4");
			}
		} else if (admissionType == AdmissionType.HIGH_SCHOOL) {
			urlLink.append("?typ=2");
		} else {
			urlLink.append("?typ=1");
		}
		urlLink.append("&id=").append(admissionId);
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "FacebookServiceImpl> getCommentLink:" + urlLink.toString());
		}
		return urlLink.toString();
	}

}
