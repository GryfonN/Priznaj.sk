package sk.gryfonnlair.priznaj.view.send.tools;

import java.util.List;

import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.util.FontUtil;
import sk.gryfonnlair.priznaj.model.HighSchool;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


/**
 * ArrayAdapter pre spinner na stredne skoly , odosielanie stredne.priznaj.sk
 * 
 * @author petranik
 * 
 */
public class HighSchoolSpinnerAdapter extends ArrayAdapter<HighSchool> {

	private final LayoutInflater inflater;

	public HighSchoolSpinnerAdapter(final Context context, final int textViewResourceId, final List<HighSchool> objects) {
		super(context, textViewResourceId, objects);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
		final View view = getView(position, convertView, parent);
		view.setBackgroundColor(Color.BLACK);
		return view;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		final ViewHolder viewHolder;
		final HighSchool highSchool = getItem(position);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.activity_send_admission_spinner_view, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.highSchool = ((TextView) convertView.findViewById(R.id.activity_send_admission_spinner_view));
			FontUtil.setOswaldRegularFont(viewHolder.highSchool);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = ((ViewHolder) convertView.getTag());
		}

		viewHolder.highSchool.setText(highSchool.getHighSchoolName());
		return convertView;
	}

	static class ViewHolder {
		TextView highSchool;
	}

}
