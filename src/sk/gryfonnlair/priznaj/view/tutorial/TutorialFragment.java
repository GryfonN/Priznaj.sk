package sk.gryfonnlair.priznaj.view.tutorial;

import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.util.FontUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Fragment pre tutorial activity, vsetky su rovnake len sa im nasetuje spravny
 * obrazok label a text
 * 
 * @author gryfonn
 * 
 */
public class TutorialFragment extends Fragment {

	public static final String KEY_IMAGE = "TutorialFragment_image";
	public static final String KEY_LABEL = "TutorialFragment_label";
	public static final String KEY_TEXT = "TutorialFragment_text";
	public static final String KEY_FINISH = "TutorialFragment_button";

	private int imageDrawableResource;
	private int labelStringResource;
	private int textStringResource;
	private boolean buttonFinishVisible;

	private View.OnClickListener finishClick;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		imageDrawableResource = getArguments().getInt(KEY_IMAGE, 0);
		labelStringResource = getArguments().getInt(KEY_LABEL, 0);
		textStringResource = getArguments().getInt(KEY_TEXT, 0);
		buttonFinishVisible = getArguments().getBoolean(KEY_FINISH, false);
		if (buttonFinishVisible) {
			finishClick = new View.OnClickListener() {

				@Override
				public void onClick(final View v) {
					TutorialFragment.this.getActivity().finish();
				}
			};
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.activity_tutorial_fragment, container, false);
		final ImageView image = (ImageView) view.findViewById(R.id.activty_tutorial_fragment_image);
		final TextView label = (TextView) view.findViewById(R.id.activty_tutorial_fragment_label);
		final TextView text = (TextView) view.findViewById(R.id.activty_tutorial_fragment_text);
		Button button = null;
		if (buttonFinishVisible) {
			button = (Button) view.findViewById(R.id.activty_tutorial_fragment_button_finish);
			button.setVisibility(View.VISIBLE);
			button.setOnClickListener(finishClick);
			final LinearLayout buttonTabletBorder = (LinearLayout) view.findViewById(R.id.activty_tutorial_fragment_button_finish_border);
			if (buttonTabletBorder != null) {
				buttonTabletBorder.setVisibility(View.VISIBLE);
			}
		}

		label.setText(labelStringResource);
		image.setImageResource(imageDrawableResource);
		text.setText(textStringResource);

		FontUtil.setOswaldRegularFont(label, text, button);
		return view;
	}
}
