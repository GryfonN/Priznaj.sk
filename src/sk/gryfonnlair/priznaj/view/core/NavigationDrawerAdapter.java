package sk.gryfonnlair.priznaj.view.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.util.FontUtil;
import sk.gryfonnlair.priznaj.model.specific.AdmissionType;
import sk.gryfonnlair.priznaj.model.specific.NavigationDrawerChildItem;
import sk.gryfonnlair.priznaj.model.specific.NavigationDrawerItem;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;


public class NavigationDrawerAdapter extends BaseExpandableListAdapter {

	private final List<NavigationDrawerItem> groupsList;
	private final LayoutInflater inflater;
	private final OnGroupClickCallback groupClickCallback;
	private final Map<String, ?> newsCountsMap;

	public NavigationDrawerAdapter(final Context context, final List<NavigationDrawerItem> groupsList, final OnGroupClickCallback groupClickCallback) {
		this.groupsList = groupsList;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.groupClickCallback = groupClickCallback;
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		newsCountsMap = prefs.getAll();
	}

	@Override
	public Object getChild(final int groupPosition, final int childPosition) {
		return groupsList != null ? groupsList.get(groupPosition).getObjectsList().get(childPosition) : Collections.EMPTY_LIST;
	}

	@Override
	public long getChildId(final int groupPosition, final int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, final ViewGroup parent) {

		ViewHolder viewHolder = null;
		final NavigationDrawerChildItem childItem = (NavigationDrawerChildItem) getChild(groupPosition, childPosition);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.drawer_list_child_item, null);
			viewHolder = new ViewHolder(
					(TextView) convertView.findViewById(R.id.drawer_tvDrawerChildItemName),
					(TextView) convertView.findViewById(R.id.drawer_childNewAdmissionsCount)
					);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = ((ViewHolder) convertView.getTag());
		}
		final int count = computeChildCounts(newsCountsMap, groupPosition, childPosition);
		if (count > 0) {
			viewHolder.childAdmissionsCounts.setVisibility(View.VISIBLE);
			viewHolder.childAdmissionsCounts.setText(String.valueOf(count));
		} else {
			viewHolder.childAdmissionsCounts.setVisibility(View.GONE);
		}
		viewHolder.tvDrawerChildItemName.setText(childItem.getName() == null ? "" : childItem.getName());
		return convertView;
	}

	@Override
	public int getChildrenCount(final int groupPosition) {
		return groupsList != null ? groupsList.get(groupPosition).getObjectsList().size() : 0;
	}

	@Override
	public Object getGroup(final int groupPosition) {
		return groupsList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groupsList != null ? groupsList.size() : 0;
	}

	@Override
	public long getGroupId(final int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, final boolean isExpanded, final View convertView, final ViewGroup parent) {
		View returnView = convertView;

		ViewHolder viewHolder = null;
		final NavigationDrawerItem groupItem = ((NavigationDrawerItem) getGroup(groupPosition));

		if (returnView == null) {
			returnView = inflater.inflate(R.layout.drawer_list_item, null);
			viewHolder = new ViewHolder(
					(TextView) returnView.findViewById(R.id.drawer_GroupItemName),
					(LinearLayout) returnView.findViewById(R.id.drawer_expandCollapseGroupsImg),
					(ToggleButton) returnView.findViewById(R.id.drawer_expandCollapseGroupsButton),
					(LinearLayout) returnView.findViewById(R.id.drawer_layGroups),
					(TextView) returnView.findViewById(R.id.drawer_new_admissions_count)
					);
			returnView.setTag(viewHolder);
		} else {
			viewHolder = ((ViewHolder) returnView.getTag());
		}

		final int count = computeGroupCounts(newsCountsMap, groupPosition);
		if (count > 0) {
			viewHolder.groupAdmissionsCounts.setVisibility(View.VISIBLE);
			viewHolder.groupAdmissionsCounts.setText(String.valueOf(count));
		} else {
			viewHolder.groupAdmissionsCounts.setVisibility(View.GONE);
		}

		switch (groupPosition) {

			case 0:
				viewHolder.layDrawerGroup.setBackgroundResource(R.drawable.drawer_uni);
				viewHolder.groupButtonBackgroundView.setBackgroundResource(R.drawable.drawer_uni);
				break;
			case 1:
				viewHolder.layDrawerGroup.setBackgroundResource(R.drawable.drawer_hs);
				viewHolder.groupButtonBackgroundView.setBackgroundResource(R.drawable.drawer_hs);
				break;
			case 2:
				viewHolder.layDrawerGroup.setBackgroundResource(R.drawable.drawer_g);
				viewHolder.groupButtonBackgroundView.setBackgroundResource(R.drawable.drawer_g);
				break;
			case 3:
				viewHolder.layDrawerGroup.setBackgroundResource(R.drawable.drawer_b);
				viewHolder.groupButtonBackgroundView.setBackgroundResource(R.drawable.drawer_b);
				break;
			default:
				break;
		}

		if (groupPosition == 2 || groupPosition == 3) {
			viewHolder.groupToggleButton.setEnabled(false);
			viewHolder.groupToggleButton.setVisibility(View.GONE);
			viewHolder.groupButtonBackgroundView.setEnabled(false);
		} else {
			viewHolder.groupToggleButton.setEnabled(true);
			viewHolder.groupToggleButton.setVisibility(View.VISIBLE);
			viewHolder.groupButtonBackgroundView.setEnabled(true);
		}

		viewHolder.tvDrawerGroupItemName.setText(groupItem.getGroupName());
		viewHolder.layDrawerGroup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View view) {

				switch (groupPosition) {
					case 0:
						groupClickCallback.onGroupClick(AdmissionType.UNIVERSITY, null, groupItem.getGroupName());
						break;
					case 1:
						groupClickCallback.onGroupClick(AdmissionType.HIGH_SCHOOL, null, groupItem.getGroupName());
						break;
					case 2:
						groupClickCallback.onGroupClick(AdmissionType.GIRLS_BOYS, 1, groupItem.getGroupName());
						break;
					case 3:
						groupClickCallback.onGroupClick(AdmissionType.GIRLS_BOYS, 2, groupItem.getGroupName());
					default:
						break;
				}
			}
		});

		viewHolder.groupToggleButton.setChecked(isExpanded);
		viewHolder.groupToggleButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View view) {
				final ExpandableListView drawerList = ((ExpandableListView) parent);
				@SuppressWarnings("unused")
				//mohol som cez if ale v byte kode je tento vyraz 1 riadok ak by bol iff tak by boli 4minimalne = rozum od FourPee-ho
				final boolean b = isExpanded ? drawerList.collapseGroup(groupPosition) : drawerList.expandGroup(groupPosition);
			}
		});

		return returnView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(final int arg0, final int arg1) {
		return true;
	}

	/**
	 * Vypocita pre child news count
	 * 
	 * @param newsCountsMap mapa vytiahnuta z pref getALL
	 * @param groupPosition grupa
	 * @param childPosition child
	 * @return
	 */
	public static final int computeChildCounts(final Map<String, ?> newsCountsMap, final int groupPosition, final int childPosition) {
		Integer count = 0;
		switch (groupPosition) {
			case 0:
				//UNI
				switch (childPosition) {
				//skoly
					case 0:
						count += newsCountsMap.get("UPJŠ") instanceof Integer ? (Integer) newsCountsMap.get("UPJŠ") : 0;
						break;
					case 1:
						count += newsCountsMap.get("TUKE") instanceof Integer ? (Integer) newsCountsMap.get("TUKE") : 0;
						break;
					case 2:
						count += newsCountsMap.get("EUBA") instanceof Integer ? (Integer) newsCountsMap.get("EUBA") : 0;
						break;
					case 3:
						count += newsCountsMap.get("STU") instanceof Integer ? (Integer) newsCountsMap.get("STU") : 0;
						break;
					case 4:
						count += newsCountsMap.get("UK") instanceof Integer ? (Integer) newsCountsMap.get("UK") : 0;
						break;
					case 5:
						count += newsCountsMap.get("UNIPO") instanceof Integer ? (Integer) newsCountsMap.get("UNIPO") : 0;
						break;
					case 6:
						count += newsCountsMap.get("UKF") instanceof Integer ? (Integer) newsCountsMap.get("UKF") : 0;
						count += newsCountsMap.get("SPU") instanceof Integer ? (Integer) newsCountsMap.get("SPU") : 0;
						break;
					case 7:
						count += newsCountsMap.get("UNIZA") instanceof Integer ? (Integer) newsCountsMap.get("UNIZA") : 0;
						count += newsCountsMap.get("UCM") instanceof Integer ? (Integer) newsCountsMap.get("UCM") : 0;
						break;
					case 8:
						count += newsCountsMap.get("TRUNI") instanceof Integer ? (Integer) newsCountsMap.get("TRUNI") : 0;
						count += newsCountsMap.get("TNUNI") instanceof Integer ? (Integer) newsCountsMap.get("TNUNI") : 0;
						break;
					case 9:
						count += newsCountsMap.get("UVLF") instanceof Integer ? (Integer) newsCountsMap.get("UVLF") : 0;
						count += newsCountsMap.get("UMB") instanceof Integer ? (Integer) newsCountsMap.get("UMB") : 0;
						count += newsCountsMap.get("TUZVO") instanceof Integer ? (Integer) newsCountsMap.get("TUZVO") : 0;
						count += newsCountsMap.get("VŠMU") instanceof Integer ? (Integer) newsCountsMap.get("VŠMU") : 0;
						count += newsCountsMap.get("VŠVU") instanceof Integer ? (Integer) newsCountsMap.get("VŠVU") : 0;
						count += newsCountsMap.get("AU") instanceof Integer ? (Integer) newsCountsMap.get("AU") : 0;
						count += newsCountsMap.get("KU") instanceof Integer ? (Integer) newsCountsMap.get("KU") : 0;
						count += newsCountsMap.get("UJS") instanceof Integer ? (Integer) newsCountsMap.get("UJS") : 0;
						break;
					default:
						break;
				}
				break;
			case 1:
				//HS
				final String key = String.valueOf(childPosition + 1);
				count += newsCountsMap.get(key) instanceof Integer ? (Integer) newsCountsMap.get(key) : 0;
				break;
			default:
				break;
		}

		return count.intValue();
	}

	/**
	 * Vytiahne z pref sucet poctov noviniek pre danu groupu
	 * 
	 * @param newsCountsMap mapa vytiahnuta z pref getALL
	 * @param groupPosition
	 * @return 0 ak nic neni v mape, inak 1+
	 */
	public static final int computeGroupCounts(final Map<String, ?> newsCountsMap, final int groupPosition) {
		Integer count = 0;
		switch (groupPosition) {
			case 0:
				//UNI
				for (int i = 0; i <= 9; i++) {
					count += computeChildCounts(newsCountsMap, groupPosition, i);
				}
				break;
			case 1:
				//HS
				for (int i = 0; i <= 7; i++) {
					count += computeChildCounts(newsCountsMap, groupPosition, i);
				}
				break;
			case 2:
				//GIRLS
				count = newsCountsMap.get("G") instanceof Integer ? (Integer) newsCountsMap.get("G") : 0;
				break;
			case 3:
				//BOYS
				count = newsCountsMap.get("B") instanceof Integer ? (Integer) newsCountsMap.get("B") : 0;
				break;
			default:
				break;
		}
		return count.intValue();
	}

	static class ViewHolder {
		//child
		TextView tvDrawerChildItemName;
		TextView childAdmissionsCounts;
		//group
		TextView tvDrawerGroupItemName;
		LinearLayout groupButtonBackgroundView;
		ToggleButton groupToggleButton;
		TextView groupAdmissionsCounts;
		LinearLayout layDrawerGroup;

		public ViewHolder(final TextView tvDrawerChildItemName, final TextView childAdmissionsCounts) {
			this.tvDrawerChildItemName = tvDrawerChildItemName;
			this.childAdmissionsCounts = childAdmissionsCounts;
			FontUtil.setOswaldRegularFont(tvDrawerChildItemName, childAdmissionsCounts);
		}

		public ViewHolder(final TextView tvDrawerGroupItemName, final LinearLayout groupButtonBackgroundView,
				final ToggleButton groupToggleButton, final LinearLayout layDrawerGroup, final TextView groupAdmissionsCounts) {
			this.tvDrawerGroupItemName = tvDrawerGroupItemName;
			this.groupButtonBackgroundView = groupButtonBackgroundView;
			this.groupToggleButton = groupToggleButton;
			this.layDrawerGroup = layDrawerGroup;
			this.groupAdmissionsCounts = groupAdmissionsCounts;
			FontUtil.setOswaldRegularFont(tvDrawerGroupItemName, groupAdmissionsCounts);
		}

	}

	public static interface OnGroupClickCallback {

		void onGroupClick(AdmissionType group, Integer category, String title);

	}
}
