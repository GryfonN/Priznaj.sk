package sk.gryfonnlair.priznaj.view.tutorial;

import sk.gryfonnlair.priznaj.PriznajApplication;
import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.util.ScreenUtil;
import sk.gryfonnlair.priznaj.view.ui.LinePageIndicator;
import sk.gryfonnlair.priznaj.view.ui.ZoomOutPageTransformer;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;


/**
 * Aktivita tutorialu s viewpagerom
 * 
 * @author gryfonn
 * 
 */
public class TutorialActivity extends FragmentActivity {

	@Override
	protected void onCreate(final Bundle bundle) {
		super.onCreate(bundle);
		ScreenUtil.setScreenOrientation(this);
		setContentView(R.layout.activity_tutorial);

		final ViewPager viewPager = (ViewPager) findViewById(R.id.activity_tutorial_viewpager);
		final TutorialPagerAdapter tutorialPagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(tutorialPagerAdapter);
		viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
		final LinePageIndicator linePageIndicator = (LinePageIndicator) findViewById(R.id.activity_tutorial_viewpager_indicator);
		linePageIndicator.setViewPager(viewPager);
		linePageIndicator.setCurrentItem(0);
	}

	@Override
	public void finish() {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (!prefs.getBoolean(PriznajApplication.PREF_TUTORIAL_COMPLETE, false)) {
			prefs.edit().putBoolean(PriznajApplication.PREF_TUTORIAL_COMPLETE, true).commit();
		}
		super.finish();
	};

	public static class TutorialPagerAdapter extends FragmentPagerAdapter {

		private final Fragment[] sendFragments = new Fragment[] {
				//Intro 0
				new TutorialFragment(),
				//Core 1
				new TutorialFragment(),

				// Social 2
				new TutorialFragment(),

				//Send 3
				new TutorialFragment(),
				//Favorite 4
				new TutorialFragment(),
				//Search 5
				new TutorialFragment(),
				//Offline 6
				new TutorialFragment() };


		public TutorialPagerAdapter(final FragmentManager fm) {
			super(fm);
			final Bundle bundleIntro = new Bundle();
			bundleIntro.putInt(TutorialFragment.KEY_IMAGE, R.drawable.tutorial_screen_intro);
			bundleIntro.putInt(TutorialFragment.KEY_LABEL, R.string.tutorial_screen_intro_label);
			bundleIntro.putInt(TutorialFragment.KEY_TEXT, R.string.tutorial_screen_intro_text);
			sendFragments[0].setArguments(bundleIntro);
			final Bundle bundleCore = new Bundle();
			bundleCore.putInt(TutorialFragment.KEY_IMAGE, R.drawable.tutorial_screen_core);
			bundleCore.putInt(TutorialFragment.KEY_LABEL, R.string.tutorial_screen_core_label);
			bundleCore.putInt(TutorialFragment.KEY_TEXT, R.string.tutorial_screen_core_text);
			sendFragments[1].setArguments(bundleCore);
			final Bundle bundleSocial = new Bundle();
			bundleSocial.putInt(TutorialFragment.KEY_IMAGE, R.drawable.tutorial_screen_social);
			bundleSocial.putInt(TutorialFragment.KEY_LABEL, R.string.tutorial_screen_social_label);
			bundleSocial.putInt(TutorialFragment.KEY_TEXT, R.string.tutorial_screen_social_text);
			sendFragments[2].setArguments(bundleSocial);
			final Bundle bundleSend = new Bundle();
			bundleSend.putInt(TutorialFragment.KEY_IMAGE, R.drawable.tutorial_screen_send);
			bundleSend.putInt(TutorialFragment.KEY_LABEL, R.string.tutorial_screen_send_label);
			bundleSend.putInt(TutorialFragment.KEY_TEXT, R.string.tutorial_screen_send_text);
			sendFragments[3].setArguments(bundleSend);
			final Bundle bundleFavorite = new Bundle();
			bundleFavorite.putInt(TutorialFragment.KEY_IMAGE, R.drawable.tutorial_screen_favorite);
			bundleFavorite.putInt(TutorialFragment.KEY_LABEL, R.string.tutorial_screen_favorite_label);
			bundleFavorite.putInt(TutorialFragment.KEY_TEXT, R.string.tutorial_screen_favorite_text);
			sendFragments[4].setArguments(bundleFavorite);
			final Bundle bundleSearch = new Bundle();
			bundleSearch.putInt(TutorialFragment.KEY_IMAGE, R.drawable.tutorial_screen_search);
			bundleSearch.putInt(TutorialFragment.KEY_LABEL, R.string.tutorial_screen_search_label);
			bundleSearch.putInt(TutorialFragment.KEY_TEXT, R.string.tutorial_screen_search_text);
			sendFragments[5].setArguments(bundleSearch);

			final Bundle bundleFinish = new Bundle();
			bundleFinish.putInt(TutorialFragment.KEY_IMAGE, R.drawable.tutorial_screen_offline);
			bundleFinish.putInt(TutorialFragment.KEY_LABEL, R.string.tutorial_screen_offline_label);
			bundleFinish.putInt(TutorialFragment.KEY_TEXT, R.string.tutorial_screen_offline_text);
			bundleFinish.putBoolean(TutorialFragment.KEY_FINISH, true);
			sendFragments[6].setArguments(bundleFinish);
		}

		@Override
		public Fragment getItem(final int position) {
			return sendFragments[position];
		}

		@Override
		public int getCount() {
			return sendFragments.length;
		}

	}

}
