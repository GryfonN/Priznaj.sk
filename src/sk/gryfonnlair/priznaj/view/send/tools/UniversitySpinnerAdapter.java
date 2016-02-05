package sk.gryfonnlair.priznaj.view.send.tools;

import java.util.List;

import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.util.FontUtil;
import sk.gryfonnlair.priznaj.model.University;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


/**
 * Adapter pre spinner na posielanie uni priznani
 * 
 * @author petranik
 * 
 */
public class UniversitySpinnerAdapter extends ArrayAdapter<University> {

	private final LayoutInflater inflater;

	public UniversitySpinnerAdapter(final Context context, final int textViewResourceId, final List<University> objects) {
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
		final University university = getItem(position);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.activity_send_admission_spinner_view, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.universityName = ((TextView) convertView.findViewById(R.id.activity_send_admission_spinner_view));
			FontUtil.setOswaldRegularFont(viewHolder.universityName);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = ((ViewHolder) convertView.getTag());
		}

		viewHolder.universityName.setText(university.getUniversityName());
		return convertView;
	}

	static class ViewHolder {
		TextView universityName;
	}

}
