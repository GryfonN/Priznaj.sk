package sk.gryfonnlair.priznaj.view.send;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.rest.RestDroid;
import sk.gryfonnlair.priznaj.control.util.FontUtil;
import sk.gryfonnlair.priznaj.dao.DataAccessObjectImpl;
import sk.gryfonnlair.priznaj.model.Gender;
import sk.gryfonnlair.priznaj.view.send.tools.GenderSpinnerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Fragment pre odosielanie G/B priznani, dedi od SenderFragment
 * 
 * @author gryfonn
 * 
 */
public class GirlsBoysSenderFragment extends SenderFragment {

	private static final String KEY_ADMISSION = "GirlsBoysSenderFragment_admission";
	private static final String KEY_SPINNER_GENDER = "GirlsBoysSenderFragment_gender";

	private Spinner genderSpinner;

	private String admissionForSave = "";
	private int genderForSave = 0;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.activity_send_girlsboys_admission, container, false);
		collectViews(view);
		addListeners();
		restoreFromBundle(savedInstanceState);
		FontUtil.setOswaldRegularFont(labelTextView, admissionEditText, sendButton);
		return view;
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

		genderSpinner = (Spinner) view.findViewById(R.id.activity_send_admission_spinner_gb);
		if (genderSpinner != null) {
			final List<Gender> genderList = new ArrayList<Gender>();
			genderList.add(0, new Gender(0, getString(R.string.activity_send_admission_girlsboys_spinner_gender_prompt)));
			genderList.addAll(DataAccessObjectImpl.INSTACE.getAllGenders());
			final GenderSpinnerAdapter adater = new GenderSpinnerAdapter(getActivity(), 0, genderList);
			genderSpinner.setAdapter(adater);
		}
		admissionEditText = (EditText) view.findViewById(R.id.activity_send_admission_text);
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

		if (genderSpinner != null) {
			genderForSave = genderSpinner.getSelectedItemPosition();
		}
		outState.putInt(KEY_SPINNER_GENDER, genderForSave);
		outState.putInt(KEY_FORM_VISIBILITY, formLayout.getVisibility());
		outState.putInt(KEY_THANKS_VISIBILITY, thanksLayout.getVisibility());
	}

	@Override
	protected void restoreFromBundle(final Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}
		admissionForSave = savedInstanceState.getString(KEY_ADMISSION);
		if (admissionEditText != null) {
			admissionEditText.setText((admissionForSave != null) ? admissionForSave : "");
		}
		genderForSave = savedInstanceState.getInt(KEY_SPINNER_GENDER, 1);
		if (genderSpinner != null) {
			genderSpinner.setSelection(genderForSave);
		}
		formVisibilityForSave = savedInstanceState.getInt(KEY_FORM_VISIBILITY, View.VISIBLE);
		if (formLayout != null) {
			formLayout.setVisibility(formVisibilityForSave);
		}
		thanksVisibilityForSave = savedInstanceState.getInt(KEY_THANKS_VISIBILITY, View.GONE);
		if (thanksLayout != null) {
			thanksLayout.setVisibility(thanksVisibilityForSave);
		}
	}

	@Override
	protected boolean checkDataForRequest() {
		if (genderSpinner.getSelectedItemPosition() < 1) {
			Toast.makeText(getActivity(), "Zvoľte pohlavie", Toast.LENGTH_SHORT).show();
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
		final String gender = RestDroid.HEADER_GENDER + ":" + Integer.toString(((Gender) genderSpinner.getSelectedItem()).getGenderId());

		return new String[] { RestDroid.HEADER_TYPE + ":0", gender, RestDroid.HEADER_TEXT + ":" + admissionEditText.getText().toString().trim(),
				RestDroid.HEADER_DATETIME + ":" + String.valueOf(new Date().getTime()) };
	}
}
