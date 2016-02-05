package sk.gryfonnlair.priznaj.view.core;

import static sk.gryfonnlair.priznaj.view.core.TransformUtils.getGroupNameById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import sk.gryfonnlair.priznaj.PriznajApplication;
import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.AdmissionDroid;
import sk.gryfonnlair.priznaj.control.rest.RestDroid;
import sk.gryfonnlair.priznaj.control.util.ScreenUtil;
import sk.gryfonnlair.priznaj.dao.DataAccessObjectImpl;
import sk.gryfonnlair.priznaj.model.specific.AdmissionType;
import sk.gryfonnlair.priznaj.model.specific.NavigationDrawerChildItem;
import sk.gryfonnlair.priznaj.model.specific.NavigationDrawerItem;
import sk.gryfonnlair.priznaj.view.about.AboutActivity;
import sk.gryfonnlair.priznaj.view.favorite.FavoriteAdmissionActivity;
import sk.gryfonnlair.priznaj.view.report.ReportActivity;
import sk.gryfonnlair.priznaj.view.search.SearchAdmissionActivity;
import sk.gryfonnlair.priznaj.view.send.SendAdmissionActivity;
import sk.gryfonnlair.priznaj.view.tutorial.TutorialActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.Toast;


/**
 * 
 * 
 * @author Martin Kiss - DIFFUSION
 * 
 */
public class MainActivity extends ActionBarActivity {

	private static final String MSG_PRESS_ONE_MORE_TIME = "Stlačte späť ešte raz pre ukončenie";

	private static final long PERIOD = 2000;
	private DrawerLayout drawerLayout;
	private ExpandableListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	/**
	 * Temp aby sa nazov uchoval po otvoreny a zatvoreni
	 */
	private CharSequence mTitle;
	private long lastPressedTime;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ScreenUtil.setScreenOrientation(this);
		setContentView(R.layout.activity_main);

		drawerList = ((ExpandableListView) findViewById(R.id.left_drawer));
		drawerList.setBackgroundResource(RestDroid.networkAvailable ? R.drawable.drawer_bg_w_net : R.drawable.drawer_bg_wo_net);

		mTitle = getTitle();
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ExpandableListView) findViewById(R.id.left_drawer);
		drawerList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		drawerList.setAdapter(new NavigationDrawerAdapter(
				this, getNavigationDrawerItems(), new NavigationDrawerAdapter.OnGroupClickCallback() {

					@Override
					public void onGroupClick(final AdmissionType group, final Integer category, final String title) {
						selectItemFromDrawer(group, category);
						setTitle(title);
					}
				}
				));
		drawerList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

			@Override
			public boolean onChildClick(final ExpandableListView parent, final View v,
					final int groupPosition, final int childPosition, final long id) {
				final NavigationDrawerItem group = ((NavigationDrawerItem) drawerList.getItemAtPosition(groupPosition));
				final NavigationDrawerChildItem child = group.getObjectsList().get(childPosition);

				switch (groupPosition) {
					case 0:
						selectItemFromDrawer(AdmissionType.UNIVERSITY, child.getId());
						setTitle(child.getName());
						break;
					case 1:
						selectItemFromDrawer(AdmissionType.HIGH_SCHOOL, child.getId());
						setTitle(child.getName());

						break;
					default:
						break;
				}

				return true;
			}
		}
				);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			@Override
			public void onDrawerClosed(final View view) {
				getSupportActionBar().setTitle(mTitle);
				drawerList.setBackgroundResource(RestDroid.networkAvailable ? R.drawable.drawer_bg_w_net : R.drawable.drawer_bg_wo_net);
			}

			@Override
			public void onDrawerOpened(final View drawerView) {
				getSupportActionBar().setTitle(mTitle);
				drawerList.setBackgroundResource(RestDroid.networkAvailable ? R.drawable.drawer_bg_w_net : R.drawable.drawer_bg_wo_net);
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Po spusteni core sa zisti ci nebol absolvovany tutorial ak nie zapne ho
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(PriznajApplication.getContext());
		if (!prefs.getBoolean(PriznajApplication.PREF_TUTORIAL_COMPLETE, false)) {
			startActivity(new Intent(this, TutorialActivity.class));
			return;
		}

		//spytam sa ci existuje fragment (stary sa zachoval aj s premmenymi) a ak nie tak
		//vytvorim novy, pricom ako keby selectnem grupu Vysoke skoly a vlozim ho
		final FragmentManager fm = getSupportFragmentManager();
		final AdmissionFragment admissionFragment = (AdmissionFragment) fm.findFragmentByTag(AdmissionFragment.FRAGMENT_TAG);
		if (admissionFragment == null) {
			selectItemFromDrawer(AdmissionType.UNIVERSITY, null);
		}
		if (PriznajApplication.D) {
			PriznajApplication.logHeap(this.getClass());
		}
	}

	@Override
	protected void onPause() {
		if (PriznajApplication.D) {
			PriznajApplication.logHeap(this.getClass());
		}
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if (drawerLayout.isDrawerOpen(drawerList)) {
					drawerLayout.closeDrawer(drawerList);
				} else {
					drawerLayout.openDrawer(drawerList);
				}
				return true;

			case R.id.action_sendAdmission:
				final Intent sendAdmissionIntent = new Intent(MainActivity.this, SendAdmissionActivity.class);
				sendAdmissionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(sendAdmissionIntent);
				return true;

			case R.id.action_favorite:
				startActivity(new Intent(this, FavoriteAdmissionActivity.class));
				return true;

			case R.id.action_search:
				startActivity(new Intent(this, SearchAdmissionActivity.class));
				break;

			case R.id.action_about:
				startActivity(new Intent(this, AboutActivity.class));
				return true;

			case R.id.action_report:
				startActivity(new Intent(this, ReportActivity.class));
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
		return false;
	}

	/**
	 * Tuto metodu zavola click drawera, a posle do nej po logickych vypoctoch
	 * spravne data pre fragment logiku
	 * 
	 * @param groupType typ priznania
	 * @param childId id z db kategorie ktore pojde do where klauzuly, alebo
	 *        <code>null</code> ak sa kliklo na grupu
	 */
	public void selectItemFromDrawer(final AdmissionType groupType, final Integer childId) {
		AdmissionDroid.limitX = 0;
		final AdmissionFragment admissionFragment = new AdmissionFragment();
		final Bundle bundleForFragment = new Bundle();
		bundleForFragment.putInt(AdmissionFragment.ARGUMETNS_KEY_ADMISSION_TYPE, groupType.getIdentifier());
		bundleForFragment.putInt(AdmissionFragment.ARGUMETNS_KEY_DRAWER_CHILD_ID, childId == null ? -1 : childId);
		admissionFragment.setArguments(bundleForFragment);
		final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.content_frame, admissionFragment, AdmissionFragment.FRAGMENT_TAG).commit();
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "MainActivity>replaceFragment: type=" + groupType +
					", kategoria=" + (childId != null ? Integer.toString(childId) : "null"));
		}
		drawerLayout.closeDrawer(drawerList);
	}

	@Override
	public void setTitle(final CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onPostCreate(final Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_MENU:
				if (drawerLayout.isDrawerOpen(drawerList)) {
					drawerLayout.closeDrawer(drawerList);
				} else {
					drawerLayout.openDrawer(drawerList);
				}
				break;
			case KeyEvent.KEYCODE_BACK:
				switch (event.getAction()) {
					case KeyEvent.ACTION_DOWN:
						if (event.getDownTime() - lastPressedTime < PERIOD) {
							finish();
						} else {
							Toast.makeText(getApplicationContext(), MSG_PRESS_ONE_MORE_TIME, Toast.LENGTH_SHORT).show();
							lastPressedTime = event.getEventTime();
						}
						return true;
				}
			default:
				break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private List<NavigationDrawerItem> getNavigationDrawerItems() {
		final HashMap<String, List<NavigationDrawerChildItem>> multimap = DataAccessObjectImpl.INSTACE.getNavigationDrawerChildren();
		final List<NavigationDrawerItem> groupList = new ArrayList<NavigationDrawerItem>(1);

		groupList.add(new NavigationDrawerItem(getGroupNameById(1), multimap.get(getGroupNameById(1))));
		groupList.add(new NavigationDrawerItem(getGroupNameById(2), multimap.get(getGroupNameById(2))));
		groupList.add(new NavigationDrawerItem(getGroupNameById(3), Collections.<NavigationDrawerChildItem> emptyList()));
		groupList.add(new NavigationDrawerItem(getGroupNameById(4), Collections.<NavigationDrawerChildItem> emptyList()));
		return groupList;
	}

}
