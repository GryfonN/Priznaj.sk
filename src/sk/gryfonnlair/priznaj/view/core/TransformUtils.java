package sk.gryfonnlair.priznaj.view.core;

public class TransformUtils {

	public static String getGroupNameById(final int identifier) {
		switch (identifier) {
			case 1:
			return "Vysoké školy";
			case 2:
			return "Stredné školy";
			case 3:
			return "Girls";
			case 4:
			return "Boys";
		default:
			break;
		}
		return null;
	}
}
