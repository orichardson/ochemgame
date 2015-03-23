package figures.creation;

import java.awt.Color;
import java.util.HashMap;

import utils.Methods;

public class ColorScheme extends HashMap<String, Color> {
	private static final long serialVersionUID = -3934146221762512792L;
	
	public static ColorScheme createActive() {
		ColorScheme cs = new ColorScheme();
		
		cs.put("SELECTED", new Color(255, 150, 0));
		cs.put("SELECTED_TRANSLUCENT", new Color(255, 150, 0, 100));
		cs.put("UNSELECTED", Color.GRAY);
		cs.put("CLOSE_POINT", Color.WHITE);
		cs.put("FAR_POINT", Color.DARK_GRAY);
		cs.put("BACKGROUND", Color.BLACK);
		
		return cs;
	}
	
	public static ColorScheme createFaded(double prc) {
		ColorScheme cs = new ColorScheme();
		
		cs.put("SELECTED", Methods.colorMeld(new Color(140, 70, 70), new Color(0, 0, 0,
				0), prc));
		cs.put("SELECTED_TRANSLUCENT", new Color(255, 150, 0, 100));
		cs.put("UNSELECTED", Methods.colorMeld(new Color(70, 70, 140), new Color(0, 0, 0,
				0), prc));
		cs.put("CLOSE_POINT", Color.WHITE);
		cs.put("FAR_POINT", Color.DARK_GRAY);
		cs.put("BACKGROUND", Color.BLACK);
		
		return cs;
	}
}
