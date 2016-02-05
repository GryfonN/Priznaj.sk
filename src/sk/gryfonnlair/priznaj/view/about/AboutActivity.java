package sk.gryfonnlair.priznaj.view.about;

import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.util.FontUtil;
import sk.gryfonnlair.priznaj.control.util.ScreenUtil;
import sk.gryfonnlair.priznaj.view.tutorial.TutorialActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Aktivita pre informacie o autoroch, priznaj.sk, plus niekde button na
 * spustenie sprievodcu aplikáciou
 * 
 * @author petranik
 * 
 */
public class AboutActivity extends ActionBarActivity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		ScreenUtil.setScreenOrientation(this);
		setContentView(R.layout.activity_about);

		final Button tutorialButton = (Button) findViewById(R.id.activity_about_button_tutorial);
		if (tutorialButton != null) {
			tutorialButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(final View v) {
					startActivity(new Intent(AboutActivity.this, TutorialActivity.class));
				}
			});
		}

		final TextView labelHello = (TextView) findViewById(R.id.activity_about_label_hello);
		final TextView text1 = (TextView) findViewById(R.id.activity_about_text1);
		final TextView text2 = (TextView) findViewById(R.id.activity_about_text2);
		final TextView text3 = (TextView) findViewById(R.id.activity_about_text3);
		final TextView labelDevelopers = (TextView) findViewById(R.id.activity_about_label_developer);
		FontUtil.setOswaldRegularFont(tutorialButton, labelHello, text1, text2, text3, labelDevelopers);

		final ImageView bannerGryfonnLair = (ImageView) findViewById(R.id.activity_about_banner_gryfonnlair);
		bannerGryfonnLair.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.setType("plain/text");
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
						new String[] { getString(R.string.app_reporting_mail) });
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "PRIZNAJ.SK - ANDROID CUSTOMER");
				startActivity(Intent.createChooser(emailIntent, ""));
			}
		});
		final ImageView bannerDiffusion = (ImageView) findViewById(R.id.activity_about_banner_diffusion);
		bannerDiffusion.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.diffusion.sk/"));
				startActivity(browserIntent);
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
