package sk.gryfonnlair.priznaj.view.send;

import sk.gryfonnlair.priznaj.PriznajApplication;
import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.rest.RestDroid;
import sk.gryfonnlair.priznaj.view.send.tools.OnSendTaskStart;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Rodic pre kazdy fragment v sendery g/b|vysoke|stredne <br>
 * <p>
 * Obsahuje spolocne widgety kt zdielaju vsetky fragmenty
 * 
 * @author gryfonn
 * 
 */
public abstract class SenderFragment extends Fragment {

	protected static final String KEY_FORM_VISIBILITY = "SenderFragment_formVisibility";
	protected static final String KEY_THANKS_VISIBILITY = "SenderFragment_thanksVisibility";

	protected int formVisibilityForSave = View.VISIBLE;
	protected int thanksVisibilityForSave = View.GONE;

	protected OnSendTaskStart onSendTaskStart;
	/**
	 * label hore v fragmente
	 */
	protected TextView labelTextView;
	/**
	 * EditText pre priznanie
	 */
	protected EditText admissionEditText;
	/**
	 * button na odoslanie priznania
	 */
	protected Button sendButton;
	protected LinearLayout formLayout;
	protected RelativeLayout thanksLayout;

	protected View.OnClickListener sendClick;

	/**
	 * Metoda pre nazbieranie views z layoutu<br>
	 * <p>
	 * neriesim null veci ratam s tym v neskorsej implementacii
	 * 
	 * @param view
	 */
	protected abstract void collectViews(View view);

	/**
	 * Meotda pre zber dat z bundlu v onCreate metode po collectnuti views
	 * 
	 * @param savedInstanceState
	 */
	protected abstract void restoreFromBundle(final Bundle savedInstanceState);

	/**
	 * Pozrie ci su konkretne widgety nasetovane a nie prazdne
	 * 
	 * @return <code>true</code> ak je vsetko v poriadku
	 */
	protected abstract boolean checkDataForRequest();

	/**
	 * Pozbiera data z widgetov a vytvori z nich pole Stringov v tvare
	 * <b>"header:value"</b>
	 * 
	 * @return
	 */
	protected abstract String[] collectDataForRequest();

	@Override
	public final void onAttach(final Activity activity) {
		super.onAttach(activity);
		try {
			onSendTaskStart = (OnSendTaskStart) activity;
		} catch (final Exception e) {
			if (PriznajApplication.D) {
				Log.e(PriznajApplication.DEBUG_TAG, "Framgnet sa nenapojil na SendAdmissionActivity" + e.getMessage().toString());
			}
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		sendClick = new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				if (!RestDroid.networkAvailable) {
					Toast.makeText(v.getContext(), "Internet je nedostupný", Toast.LENGTH_SHORT).show();
					sendButton.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.activity_send_shake));
					return;
				}
				if (!checkDataForRequest()) {
					sendButton.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.activity_send_shake));
					return;
				}
				if (onSendTaskStart != null) {
					onSendTaskStart.onSendTaskStart(collectDataForRequest());
					formLayout.setVisibility(View.GONE);
					thanksLayout.setVisibility(View.VISIBLE);
				}
			}

		};
		super.onCreate(savedInstanceState);
	}

	/**
	 * V tejto metode sa pridavaju listenery v aktivite, <b>Vzdy volat Super</b>
	 * lebo ten nasetuje onClicl na sendButton
	 */
	protected void addListeners() {
		if (sendButton != null) {
			sendButton.setOnClickListener(sendClick);
		}
	}

	@Override
	public void onDetach() {
		onSendTaskStart = null;
		super.onDetach();
	}

	@Override
	public void onDestroyView() {
		onSendTaskStart = null;
		super.onDestroyView();
	}

}
