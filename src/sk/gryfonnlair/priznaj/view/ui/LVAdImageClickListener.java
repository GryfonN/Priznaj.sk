package sk.gryfonnlair.priznaj.view.ui;

import android.content.Intent;
import android.net.Uri;
import android.view.View;


public class LVAdImageClickListener implements View.OnClickListener {

	private Uri uri;

	@Override
	public void onClick(final View v) {
		v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, uri));
	}

	public void resetClickListener(final Uri uri) {
		this.uri = uri;
	}

}
