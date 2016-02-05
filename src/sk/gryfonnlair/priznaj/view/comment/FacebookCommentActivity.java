package sk.gryfonnlair.priznaj.view.comment;

import sk.gryfonnlair.priznaj.R;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.webkit.WebView;


public class FacebookCommentActivity extends ActionBarActivity {

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.facebook_comment_webview);

		final String link = getIntent().getBundleExtra("admissionToComment").getString("link");
		final WebView webView = ((WebView) findViewById(R.id.facebookCommentWebView));
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(link);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;

			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
