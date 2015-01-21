package figures.creation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class BetterTabbedThing extends JPanel {
	private static final long serialVersionUID = -6327889270988969173L;

	Component center = null;
	int selected = -1, num = 0;

	ArrayList<JButton> tabs = new ArrayList<JButton>();

	public BetterTabbedThing() {
		setLayout(new GridBagLayout());
	}

	public void addSTab(String str, JComponent c) {
		addSTab(str, c, 1, null);
	}

	public void addSTab(String str, JComponent c, int side) {
		addSTab(str, c, side, null);
	}

	public void addSTab(String str, JComponent c, int side, ActionListener a) {
		JButton b = new JButton(str);

		b.setForeground(Color.LIGHT_GRAY);
		b.setFont(new Font("SANS_SERIF", Font.BOLD, 15));
		b.setBackground(Color.DARK_GRAY.darker());
		b.setFocusable(false);
		b.addActionListener(new ClickSpy(c, num++));

		if (a != null)
			b.addActionListener(a);

		addSTab(b, c, side, false);
	}

	public void enable(boolean e) {
		for (JButton b : tabs) {
			b.setEnabled(e);
			b.setBackground(e ? Color.DARK_GRAY.darker() : Color.GRAY);
		}
	}

	public void addSTab(JButton b, JComponent c, int side, boolean q) {
		b.addActionListener(new ClickSpy(c, num++));

		if (q) {
			b.setForeground(Color.WHITE);
			b.setFont(new Font("SANS_SERIF", Font.BOLD, 15));
		}

		tabs.add(b);

		GridBagConstraints vars = new GridBagConstraints();
		vars.anchor = side > 0 ? GridBagConstraints.LINE_START : GridBagConstraints.LINE_END;
		vars.weightx = 0.0001f;
		vars.weighty = 0.5;
		vars.gridx = side > 0 ? side - 1 : 10 + side;
		vars.gridy = -1;
		vars.fill = GridBagConstraints.BOTH;
		add(b, vars);
	}

	private class ClickSpy implements ActionListener {
		Component comp;
		int index;

		public ClickSpy(Component c, int i) {
			this.comp = c;
			this.index = i;
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			if (center != null)
				BetterTabbedThing.this.remove(center);

			GridBagConstraints vars = new GridBagConstraints();
			vars.gridheight = 100;
			vars.anchor = GridBagConstraints.CENTER;
			vars.fill = GridBagConstraints.HORIZONTAL;
			vars.weightx = 1;
			vars.gridy = -1;
			vars.gridx = 5;

			if (comp != null)
				BetterTabbedThing.this.add(comp, vars);
			BetterTabbedThing.this.selected = index;
			center = comp;
			invalidate();
			revalidate();
			repaint();
		}
	}
}
