package sk.gryfonnlair.priznaj.view.send;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.rest.RestDroid;
import sk.gryfonnlair.priznaj.control.util.FontUtil;
import sk.gryfonnlair.priznaj.dao.DataAccessObjectImpl;
import sk.gryfonnlair.priznaj.model.University;
import sk.gryfonnlair.priznaj.view.send.tools.UniversitySpinnerAdapter;
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
 * Fragment pre odosielanie UNIVERSITY priznani, dedi od SenderFragment
 * 
 * @author gryfonn
 * 
 */
public class UniversitySenderFragment extends SenderFragment {

	private static final String KEY_ADMISSION = "UniversitySenderFragment_admission";
	private static final String KEY_SPINNER_UNI = "UniversitySenderFragment_uni";

	private Spinner uniSpinner;

	private String admissionForSave = "";
	private int uniForSave = 0;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.activity_send_university_admission, container, false);
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

		uniSpinner = (Spinner) view.findViewById(R.id.activity_send_admission_spinner_uni);
		if (uniSpinner != null) {
			final List<University> universityList = new ArrayList<University>();
			universityList.add(0, new University(0, getString(R.string.activity_send_admission_university_spinner_uni_prompt), ""));
			universityList.addAll(DataAccessObjectImpl.INSTACE.getAllUniversities());
			final UniversitySpinnerAdapter adater =
					new UniversitySpinnerAdapter(getActivity(), 0, universityList);
			uniSpinner.setAdapter(adater);
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

		if (uniSpinner != null) {
			uniForSave = uniSpinner.getSelectedItemPosition();
		}
		outState.putInt(KEY_SPINNER_UNI, uniForSave);
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
		uniForSave = savedInstanceState.getInt(KEY_SPINNER_UNI, 1);
		if (uniSpinner != null) {
			uniSpinner.setSelection(uniForSave);
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
		if (uniSpinner.getSelectedItemPosition() < 1) {
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
		final String university = new StringBuilder(RestDroid.HEADER_UNIVERSITY).append(':')
				.append(Integer.toString(((University) uniSpinner.getSelectedItem()).getUniversityId())).toString();

		return new String[] { RestDroid.HEADER_TYPE + ":2", university, RestDroid.HEADER_TEXT + ":" + admissionEditText.getText().toString().trim(),
				RestDroid.HEADER_DATETIME + ":" + String.valueOf(new Date().getTime()) };
	}
}
