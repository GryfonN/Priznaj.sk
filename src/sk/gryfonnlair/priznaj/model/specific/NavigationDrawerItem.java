package sk.gryfonnlair.priznaj.model.specific;

import java.util.List;


public class NavigationDrawerItem {

	private String groupName;
	private final List<NavigationDrawerChildItem> objectsList;

	public NavigationDrawerItem(final String groupName, final List<NavigationDrawerChildItem> objectsList) {
		this.groupName = groupName;
		this.objectsList = objectsList;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(final String groupName) {
		this.groupName = groupName;
	}

	public List<NavigationDrawerChildItem> getObjectsList() {
		return objectsList;
	}

}
