package winapp.toolbar;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.border.Border;

import sudoku.bedienung.SudokuBedienung;
import winapp.statusbar.StatusBar;

@SuppressWarnings("serial")
public class ToolBar0 extends JToolBar {
	private static String gibLeerzeichenText(int nLeerZeichen) {
		String s = "";
		for (int i = 0; i < nLeerZeichen; i++) {
			s += " ";
		}
		return s;
	}

	protected SudokuBedienung sudoku;
	protected StatusBar statusBar;

	private class ToolBar0Button extends JButton {
		public final int nLeerZeichen;

		ToolBar0Button(int nLeerZeichen) {
			super(ToolBar0.gibLeerzeichenText(nLeerZeichen));
			this.nLeerZeichen = nLeerZeichen;
		}
	}

	public ToolBar0(SudokuBedienung sudoku, StatusBar statusBar) {
		super(VERTICAL);
		this.sudoku = sudoku;
		this.statusBar = statusBar;

		this.add(new ToolBar0Button(0));
		this.add(new ToolBar0Button(100));
	}

	protected void erschaffeGruppenTitel(String text, String toolTip) {
		JButton l = new JButton(text);
		l.setToolTipText(toolTip);
		l.setBackground(Color.LIGHT_GRAY);
		Border border = BorderFactory.createLineBorder(Color.black);
		l.setBorder(border);
		l.setHorizontalAlignment(CENTER);
		this.add(l);
	}

	public void aufpeppen() {
		// Alle Elemente des ToolBar auflisten
		ArrayList<Component> components = new ArrayList<>();
		int sollBreite = 0;
		ToolBar0Button b0 = null;
		ToolBar0Button bn = null;

		for (int j = 0; j < this.getComponentCount(); j++) {
			Component c = this.getComponentAtIndex(j);
			if (c instanceof ToolBar0Button) {
				ToolBar0Button b = (ToolBar0Button) c;
				if (b.nLeerZeichen == 0) {
					b0 = b;
				} else {
					bn = b;
				}
			} else {
				components.add(c);
				int cBreite = c.getWidth();
				if (cBreite > sollBreite) {
					sollBreite = cBreite;
				}
			}
		}
		// Alle Elemente des ToolBar von diesem entfernen
		this.removeAll();

		// Ermittlung der Breite, die 1 Leerzeichen erzeugt
		final double breite1 = ((double) (bn.getWidth() - b0.getWidth())) / bn.nLeerZeichen;

		// Alle Elemente im ToolBar wieder einfügen mit der hübschen Breite
		for (int i = 0; i < components.size(); i++) {
			Component component = components.get(i);
			if (component instanceof JButton) {
				JButton b = ((JButton) component);
				int cBreite = component.getWidth();
				if (cBreite < sollBreite) {
					double diffBreite = (double) sollBreite - (double) cBreite;
					int nLeerZeichen = (int) (diffBreite / breite1);
					String cText = b.getText();
					boolean istVorn = true;
					for (int j = 0; j < nLeerZeichen; j++) {
						if (istVorn) {
							cText = " " + cText;
						} else {
							cText += " ";
						}
						istVorn = !istVorn;
					}
					b.setText(cText);
				}
			}
			this.add(component);
		}
	}
}
