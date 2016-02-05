package sk.gryfonnlair.priznaj.view.search.tools;

import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.util.FontUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Fragment pre ViewPager na vyber typu priznani na hladanie
 * 
 * @author gryfonn
 * 
 */
public class SearchTitleFragment extends Fragment {

	public static final String KEY_TITLE = "SearchTitleFragment_title";
	private String title;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		title = getArguments().getString(KEY_TITLE);
		if (title == null) {
			title = "NENI";
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.activity_search_title_fragment, container, false);
		final TextView titleTextView = (TextView) view.findViewById(R.id.activity_search_fragment_title);
		final TextView leftArrow = (TextView) view.findViewById(R.id.activity_search_fragment_left_arrow);
		final TextView rightArrow = (TextView) view.findViewById(R.id.activity_search_fragment_right_arrow);
		if ("GIRLS/BOYS.PRIZNAJ.SK".equalsIgnoreCase(title)) {
			leftArrow.setVisibility(View.INVISIBLE);
		}
		else if ("STREDNE.PRIZNAJ.SK".equalsIgnoreCase(title)) {
			rightArrow.setVisibility(View.INVISIBLE);
		}
		titleTextView.setText(title);
		FontUtil.setOswaldRegularFont(titleTextView, leftArrow, rightArrow);
		return view;
	}
}
