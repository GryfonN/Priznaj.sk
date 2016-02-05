package sk.gryfonnlair.priznaj.view.send;

import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.rest.RestDroid;
import sk.gryfonnlair.priznaj.control.util.ApiVersionUtil;
import sk.gryfonnlair.priznaj.control.util.ScreenUtil;
import sk.gryfonnlair.priznaj.view.send.tools.OnSendTaskStart;
import sk.gryfonnlair.priznaj.view.send.tools.ScrollableViewPager;
import sk.gryfonnlair.priznaj.view.ui.LinePageIndicator;
import sk.gryfonnlair.priznaj.view.ui.PProgressDialog;
import sk.gryfonnlair.priznaj.view.ui.ZoomOutPageTransformer;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


/**
 * FragmentActivita na posielanie priznania, spustenie cez actionbar/menu<br>
 * <p>
 * vuyzivam v nom viewpager indikator torchu podla seba(len line indikator)
 * http://www.androidviews.net/2012/12/viewpagerindicator/
 * 
 * @author gryfonn
 * 
 */
public final class SendAdmissionActivity extends ActionBarActivity implements OnSendTaskStart {

	static final int VIEW_PAGER_MAX_PAGES = 2;
	static final String MSG_ADMISSION_SEND_SUCCESS = "Priznanie odoslané";
	static final String MSG_ADMISSION_SEND_FAILED = "Priznanie sa nepodarilo odoslať";

	public PProgressDialog progresDialog;
	SendAsyncTask sendAsyncTask;

	@Override
	protected void onCreate(final Bundle bundle) {
		super.onCreate(bundle);
		ScreenUtil.setScreenOrientation(this);
		setContentView(R.layout.activity_send_admission);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setCustomView(R.layout.activity_send_action_bar);
		final ImageButton helpButton = (ImageButton) actionBar.getCustomView().findViewById(R.id.activity_send_info_button);
		helpButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				startActivity(new Intent(SendAdmissionActivity.this, RulesActivity.class));
			}
		});

		progresDialog = new PProgressDialog(this);
		sendAsyncTask = (SendAsyncTask) getLastCustomNonConfigurationInstance();
		if (sendAsyncTask != null) {
			sendAsyncTask.connectActivity(this);
		}
		final SendPagerAdapter sendPagerAdapter = new SendPagerAdapter(getSupportFragmentManager());
		final ScrollableViewPager scrollableViewPager = (ScrollableViewPager) findViewById(R.id.activity_send_admission_viewpager);
		scrollableViewPager.setOffscreenPageLimit(VIEW_PAGER_MAX_PAGES);
		scrollableViewPager.setAdapter(sendPagerAdapter);
		scrollableViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

		final LinePageIndicator linePageIndicator = (LinePageIndicator) findViewById(R.id.activity_send_admission_viewpager_indicator);
		linePageIndicator.setViewPager(scrollableViewPager);
		linePageIndicator.setCurrentItem(1);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
				&& ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL
				|| (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL)) {
			scrollableViewPager.setScrollEnabled(false);
		} else {
			scrollableViewPager.setScrollEnabled(true);
		}
	}

	@Override
	protected void onResume() {
		if (sendAsyncTask != null && !sendAsyncTask.ready && !progresDialog.isShowing()) {
			progresDialog.show();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		progresDialog.cancel();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (sendAsyncTask != null) {
			sendAsyncTask.disconnectActivity();
		}
		super.onDestroy();
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

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		return sendAsyncTask;
	}

	@Override
	public void onSendTaskStart(final String[] atributes) {
		sendAsyncTask = new SendAsyncTask(this);
		if (ApiVersionUtil.hasHoneycomb()) {
			sendAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, atributes);
		}
		else {
			sendAsyncTask.execute(atributes);
		}
	}

	static class SendAsyncTask extends AsyncTask<String, Void, Byte> {

		public boolean ready = true;

		SendAdmissionActivity activity;

		public SendAsyncTask(final SendAdmissionActivity activity) {
			this.activity = activity;
		}

		@Override
		protected void onPreExecute() {
			ready = false;
			activity.progresDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Byte doInBackground(final String... headers) {
			return RestDroid.sendAdmission(headers);
		}

		@Override
		protected void onPostExecute(final Byte result) {
			activity.progresDialog.cancel();
			if (result != null && result == 1) {
				Toast.makeText(activity, MSG_ADMISSION_SEND_SUCCESS, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(activity, MSG_ADMISSION_SEND_FAILED, Toast.LENGTH_SHORT).show();
			}
			ready = true;
			activity = null;
		}

		@Override
		protected void onCancelled() {
			activity.progresDialog.cancel();
			Toast.makeText(activity, MSG_ADMISSION_SEND_FAILED, Toast.LENGTH_SHORT).show();
			ready = true;
			activity = null;
			super.onCancelled();
		}

		public void connectActivity(final SendAdmissionActivity activity) {
			this.activity = activity;
		}

		public void disconnectActivity() {
			activity = null;
		}
	}

	static class SendPagerAdapter extends FragmentStatePagerAdapter {

		public SendPagerAdapter(final FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(final int position) {
			return position == 0 ? new GirlsBoysSenderFragment() : position == 2 ? new HighSchoolSenderFragment() : new UniversitySenderFragment();
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public void destroyItem(final View collection, final int position, final Object o) {
			View view = (View) o;
			((ViewPager) collection).removeView(view);
			view = null;
		}
	}
}
