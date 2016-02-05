package sk.gryfonnlair.priznaj.model.specific;


public class NavigationDrawerChildItem {
	
	private final int id;
	private final String name;
	
	public NavigationDrawerChildItem(final int id, final String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
}
