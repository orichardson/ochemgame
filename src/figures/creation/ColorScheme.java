
package figures.creation;

import java.awt.Color;
import java.util.HashMap;

import utils.Methods;

public class ColorScheme extends HashMap<String, Color> {
	private static final long serialVersionUID = -3934146221762512792L;

	public static ColorScheme createActive() {
		ColorScheme cs = new ColorScheme();

		cs.put("BACKGROUND", Color.BLACK);
		cs.put("SELECTED", new Color(255, 150, 0));
		cs.put("SELECTED_TRANSLUCENT", new Color(255, 150, 0, 100));
		cs.put("UNSELECTED", Color.GRAY);
		cs.put("CLOSE_POINT", Color.WHITE);
		cs.put("FAR_POINT", Color.DARK_GRAY);

		return cs;
	}

	public static ColorScheme createFaded(double prc) {
		ColorScheme cs = new ColorScheme();

		Color back = Color.black;
		Color fore = Color.WHITE;
		Color selected = Methods.colorMeld(new Color(180, 70, 70), new Color(0, 0, 0, 100), prc);
		Color unselected = Methods.colorMeld(new Color(70, 70, 180), new Color(0, 0, 0, 100), prc);

		cs.put("SELECTED", selected);
		cs.put("SELECTED_TRANSLUCENT", Methods.getColor(selected, selected.getAlpha() / 2));
		cs.put("UNSELECTED", unselected);
		cs.put("CLOSE_POINT", unselected);
		cs.put("FAR_POINT", unselected);
		cs.put("BACKGROUND", back);

		return cs;
	}
}
