package sk.gryfonnlair.priznaj.view.report;

import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.util.FontUtil;
import sk.gryfonnlair.priznaj.control.util.ScreenUtil;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * Report aktivita na hlasenie chyb/ napadov a pripomienok, v menu polozka
 * pripomienky<br>
 * <p>
 * Su tu len dakovne keci obrazok a button kt spusta intent s mailom
 * 
 * @author gryfonn
 * 
 */
public class ReportActivity extends ActionBarActivity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		ScreenUtil.setScreenOrientation(this);
		setContentView(R.layout.activity_report);

		final TextView label = (TextView) findViewById(R.id.activity_report_label);
		final TextView text = (TextView) findViewById(R.id.activity_report_text);
		final TextView text2 = (TextView) findViewById(R.id.activity_report_text2);
		final Button sendButton = (Button) findViewById(R.id.activity_report_button_send);
		final Button sendButton2 = (Button) findViewById(R.id.activity_report_button_send2);
		FontUtil.setOswaldRegularFont(label, text, text2, sendButton, sendButton2);

		sendButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.setType("plain/text");
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
						new String[] { getString(R.string.app_reporting_mail) });
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "PRIZNAJ.SK - ANDROID REPORTING");
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.app_reporting_hint_text));
				startActivity(Intent.createChooser(emailIntent, ""));
			}
		});

		sendButton2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=sk.gryfonnlair.priznaj"));
				startActivity(intent);
			}
		});
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
