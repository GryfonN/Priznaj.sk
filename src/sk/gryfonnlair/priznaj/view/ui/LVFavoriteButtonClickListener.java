package sk.gryfonnlair.priznaj.view.ui;

import sk.gryfonnlair.priznaj.control.AdmissionDroid;
import sk.gryfonnlair.priznaj.model.specific.CoreAdmission;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;


public class LVFavoriteButtonClickListener implements View.OnClickListener {

	private CoreAdmission coreAdmission;

	@Override
	public void onClick(final View v) {
		final ToggleButton toggleButton = (v instanceof ToggleButton) ? (ToggleButton) v : null;
		if (toggleButton != null) {
			final boolean result = AdmissionDroid.changeFavoriteOnAdmission(
					toggleButton.isChecked(),
					coreAdmission.type,
					coreAdmission.id);
			if (!result) {
				toggleButton.setChecked(!toggleButton.isChecked());
				Toast.makeText(v.getContext(), "Nepodarilo sa mi to.", Toast.LENGTH_SHORT).show();
			} else {
				coreAdmission.favorite = toggleButton.isChecked();
				Toast.makeText(v.getContext(), "Priznanie bolo " + (toggleButton.isChecked() ? "pridané do" : "odstranené z") + " obľúbených.",
						Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	public void resetClickListener(final CoreAdmission coreAdmission) {
		this.coreAdmission = coreAdmission;
	}
}