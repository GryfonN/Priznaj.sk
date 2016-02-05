package sk.gryfonnlair.priznaj.view.send;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.rest.RestDroid;
import sk.gryfonnlair.priznaj.control.util.FontUtil;
import sk.gryfonnlair.priznaj.dao.DataAccessObjectImpl;
import sk.gryfonnlair.priznaj.model.County;
import sk.gryfonnlair.priznaj.model.HighSchool;
import sk.gryfonnlair.priznaj.model.Region;
import sk.gryfonnlair.priznaj.view.send.tools.CountySpinnerAdapter;
import sk.gryfonnlair.priznaj.view.send.tools.HighSchoolSpinnerAdapter;
import sk.gryfonnlair.priznaj.view.send.tools.RegionSpinnerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Fragment pre odosielanie STREDNE priznani, dedi od SenderFragment
 * 
 * @author gryfonn
 * 
 */
public class HighSchoolSenderFragment extends SenderFragment {

	private static final String KEY_ADMISSION = "HighSchoolSenderFragment_admission";
	private static final String KEY_SPINNER_REGION = "HighSchoolSenderFragment_region";
	private static final String KEY_SPINNER_COUNTY = "HighSchoolSenderFragment_county";
	private static final String KEY_SPINNER_HIGHSCHOOL = "HighSchoolSenderFragment_school";
	private static final String KEY_SPINNER_REGION_ID = "HighSchoolSenderFragment_region_id";
	private static final String KEY_SPINNER_COUNTY_ID = "HighSchoolSenderFragment_county_id";

	private Spinner regionSpinner;
	private Spinner countySpinner;
	private Spinner highSchoolSpinner;

	private String admissionForSave = "";
	private int regionForSave = 0;
	private int countyForSave = 0;
	private int highSchoolForSave = 0;
	private int regionIdForSave = 0;
	private int countyIdForSave = 0;
	private CountySpinnerAdapter countyAdapter;
	private HighSchoolSpinnerAdapter highSchoolAdapter;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.activity_send_highschool_admission, container, false);
		restoreFromBundle(savedInstanceState);
		collectViews(view);
		FontUtil.setOswaldRegularFont(labelTextView, admissionEditText, sendButton);
		return view;
	}

	@Override
	public void onResume() {
		addListeners();
		super.onResume();
	}

	@Override
	protected void collectViews(final View view) {
		formLayout = (LinearLayout) view.findViewById(R.id.activity_send_form);
		formLayout.setVisibility(formVisibilityForSave);
		thanksLayout = (RelativeLayout) view.findViewById(R.id.activity_send_thanks);
		thanksLayout.setVisibility(thanksVisibilityForSave);

		final TextView thx = (TextView) view.findViewById(R.id.activity_send_admission_thanks);
		if (thx != null) {
			FontUtil.setOswaldRegularFont(thx);
		}

		labelTextView = (TextView) view.findViewById(R.id.activity_send_admission_label);

		regionSpinner = (Spinner) view.findViewById(R.id.activity_send_admission_spinner_high_region);
		if (regionSpinner != null) {
			final List<Region> regionList = new ArrayList<Region>();
			regionList.add(0, new Region(0, getString(R.string.activity_send_admission_highschool_spinner_region_prompt)));
			regionList.addAll(DataAccessObjectImpl.INSTACE.getAllRegions());
			regionSpinner.setAdapter(new RegionSpinnerAdapter(getActivity(), 0, regionList));
			regionSpinner.setSelection(regionForSave, false);
		}
		countySpinner = (Spinner) view.findViewById(R.id.activity_send_admission_spinner_high_county);
		if (countySpinner != null) {
			final List<County> countyList = new ArrayList<County>();
			countyList.add(0, new County(0, getString(R.string.activity_send_admission_highschool_spinner_county_prompt), 0));
			if (regionForSave <= 0) {
				if (checkPostHoneycombVersion()) {
					countySpinner.setAlpha(0.5f);
				}
				countySpinner.setEnabled(false);
			} else {
				if (checkPostHoneycombVersion()) {
					countySpinner.setAlpha(1f);
				}
				countySpinner.setEnabled(true);
				countyList.addAll(DataAccessObjectImpl.INSTACE.getCountiesByRegion(regionIdForSave));
			}
			countyAdapter = new CountySpinnerAdapter(getActivity(), 0, countyList);
			countySpinner.setAdapter(countyAdapter);
			countySpinner.setSelection(countyForSave, false);
		}
		highSchoolSpinner = (Spinner) view.findViewById(R.id.activity_send_admission_spinner_high_school);
		if (highSchoolSpinner != null) {
			final List<HighSchool> highSchoolList = new ArrayList<HighSchool>();
			highSchoolList.add(0, new HighSchool(0, getString(R.string.activity_send_admission_highschool_spinner_highschool_prompt), 0));
			if (countyForSave <= 0) {
				if (checkPostHoneycombVersion()) {
					highSchoolSpinner.setAlpha(0.5f);
				}
				highSchoolSpinner.setEnabled(false);
			} else {
				if (checkPostHoneycombVersion()) {
					highSchoolSpinner.setAlpha(1f);
				}
				highSchoolSpinner.setEnabled(true);
				highSchoolList.addAll(DataAccessObjectImpl.INSTACE.getHighSchoolsByCounty(countyIdForSave));
			}
			highSchoolAdapter = new HighSchoolSpinnerAdapter(getActivity(), 0, highSchoolList);
			highSchoolSpinner.setAdapter(highSchoolAdapter);
			highSchoolSpinner.setSelection(highSchoolForSave);
		}

		admissionEditText = (EditText) view.findViewById(R.id.activity_send_admission_text);
		if (admissionEditText != null) {
			admissionEditText.setText((admissionForSave != null) ? admissionForSave : "");
		}
		sendButton = (Button) view.findViewById(R.id.activity_send_admission_button_send);
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		//preto cez premmenne lebo po otoceni nemam spinner ale hodnotu treba uchovat
		if (admissionEditText != null) {
			admissionForSave = admissionEditText.getText().toString();
		}
		outState.putString(KEY_ADMISSION, admissionForSave);

		if (regionSpinner != null) {
			regionForSave = regionSpinner.getSelectedItemPosition();
			regionIdForSave = ((Region) regionSpinner.getAdapter().getItem(regionForSave)).getRegionId();
		}
		outState.putInt(KEY_SPINNER_REGION, regionForSave);
		outState.putInt(KEY_SPINNER_REGION_ID, regionIdForSave);
		if (countySpinner != null) {
			countyForSave = countySpinner.getSelectedItemPosition();
			countyIdForSave = ((County) countySpinner.getAdapter().getItem(countyForSave)).getCountyId();
		}
		outState.putInt(KEY_SPINNER_COUNTY, countyForSave);
		outState.putInt(KEY_SPINNER_COUNTY_ID, countyIdForSave);
		if (highSchoolSpinner != null) {
			highSchoolForSave = highSchoolSpinner.getSelectedItemPosition();
		}
		outState.putInt(KEY_SPINNER_HIGHSCHOOL, highSchoolForSave);
		outState.putInt(KEY_FORM_VISIBILITY, formLayout.getVisibility());
		outState.putInt(KEY_THANKS_VISIBILITY, thanksLayout.getVisibility());
	}

	@Override
	protected void restoreFromBundle(final Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}
		admissionForSave = savedInstanceState.getString(KEY_ADMISSION);
		regionForSave = savedInstanceState.getInt(KEY_SPINNER_REGION, 0);
		regionIdForSave = savedInstanceState.getInt(KEY_SPINNER_REGION_ID, 0);
		countyForSave = savedInstanceState.getInt(KEY_SPINNER_COUNTY, 0);
		countyIdForSave = savedInstanceState.getInt(KEY_SPINNER_COUNTY_ID, 0);
		highSchoolForSave = savedInstanceState.getInt(KEY_SPINNER_HIGHSCHOOL, 0);
		formVisibilityForSave = savedInstanceState.getInt(KEY_FORM_VISIBILITY, View.VISIBLE);
		thanksVisibilityForSave = savedInstanceState.getInt(KEY_THANKS_VISIBILITY, View.GONE);
	}

	@Override
	protected void addListeners() {
		super.addListeners();
		if (regionSpinner != null) {
			regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(final AdapterView<?> parent, final View view, final int pos, final long id) {
					//ak prompt tak negenerujem dalsie.
					if (pos == 0) {
						if (checkPostHoneycombVersion()) {
							countySpinner.setAlpha(0.5f);
							highSchoolSpinner.setAlpha(0.5f);
						}
						countySpinner.setEnabled(false);
						countySpinner.setSelection(0);
						highSchoolSpinner.setEnabled(false);
						highSchoolSpinner.setSelection(0);
					} else {
						final Region region = (Region) parent.getItemAtPosition(pos);
						final List<County> countiesByRegion = new ArrayList<County>();
						countiesByRegion.add(0, new County(0, getString(R.string.activity_send_admission_highschool_spinner_county_prompt), 0));
						countiesByRegion.addAll(DataAccessObjectImpl.INSTACE.getCountiesByRegion(region.getRegionId()));
						countyAdapter = new CountySpinnerAdapter(getActivity(), 0, countiesByRegion);
						countySpinner.setAdapter(countyAdapter);
						if (checkPostHoneycombVersion()) {
							countySpinner.setAlpha(1f);
						}
						countySpinner.setEnabled(true);
					}
				}

				@Override
				public void onNothingSelected(final AdapterView<?> arg0) {
				}
			});
		}
		if (countySpinner != null) {
			countySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(final AdapterView<?> parent, final View view, final int pos, final long id) {
					//ak prompt tak negenerujem dalsie.
					if (pos == 0) {
						if (checkPostHoneycombVersion()) {
							highSchoolSpinner.setAlpha(0.5f);
						}
						highSchoolSpinner.setEnabled(false);
						highSchoolSpinner.setSelection(0);
					} else {
						final County county = (County) parent.getItemAtPosition(pos);
						final List<HighSchool> highSchoolsByCounty = new ArrayList<HighSchool>();
						highSchoolsByCounty.add(0, new HighSchool(0, getString(R.string.activity_send_admission_highschool_spinner_highschool_prompt), 0));
						highSchoolsByCounty.addAll(DataAccessObjectImpl.INSTACE.getHighSchoolsByCounty(county.getCountyId()));
						highSchoolAdapter = new HighSchoolSpinnerAdapter(getActivity(), 0, highSchoolsByCounty);
						highSchoolSpinner.setAdapter(highSchoolAdapter);
						if (checkPostHoneycombVersion()) {
							highSchoolSpinner.setAlpha(1f);
						}
						highSchoolSpinner.setEnabled(true);
					}
				}

				@Override
				public void onNothingSelected(final AdapterView<?> arg0) {
				}
			});
		}
	}

	@Override
	protected boolean checkDataForRequest() {
		if (regionSpinner.getSelectedItemPosition() < 1) {
			Toast.makeText(getActivity(), "Zvoľte kraj", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (countySpinner.getSelectedItemPosition() < 1) {
			Toast.makeText(getActivity(), "Zvoľte okres", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (highSchoolSpinner.getSelectedItemPosition() < 1) {
			Toast.makeText(getActivity(), "Zvoľte školu", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (admissionEditText.getText().toString() == null || admissionEditText.getText().toString().trim().length() < 1) {
			Toast.makeText(getActivity(), "Zadajte text priznania", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	protected String[] collectDataForRequest() {
		final String region = RestDroid.HEADER_REGION + ":" + Integer.toString(((Region) regionSpinner.getSelectedItem()).getRegionId());
		final String county = RestDroid.HEADER_COUNTY + ":" + Integer.toString(((County) countySpinner.getSelectedItem()).getCountyId());
		final String highschool = RestDroid.HEADER_HIGH_SCHOOL + ":" + Integer.toString(((HighSchool) highSchoolSpinner.getSelectedItem()).getHighSchoolId());

		return new String[] { RestDroid.HEADER_TYPE + ":1", region, county, highschool,
				RestDroid.HEADER_TEXT + ":" + admissionEditText.getText().toString().trim(),
				RestDroid.HEADER_DATETIME + ":" + String.valueOf(new Date().getTime()) };
	}

	/**
	 * Checknem verziu aby som mohol pouzit setAlpha na spinner
	 * 
	 * @return true ak mam novsiu ako API 11
	 */
	private boolean checkPostHoneycombVersion() {
		return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB;
	}
}
