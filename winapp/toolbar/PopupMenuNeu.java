package winapp.toolbar;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import sudoku.neu.NeuTyp;
import sudoku.neu.NeuTyp.Typ;
import sudoku.schwer.daten.Schwierigkeiten;
import winapp.toolbar.ToolBarGruppeSudoku.ButtonNeu;
import winapp.tools.ToolTip;

@SuppressWarnings("serial")
public class PopupMenuNeu extends JPopupMenu {
	static private String gibToolTip(final NeuTyp neuTyp) {
		String toolTip = null;
		switch (neuTyp.gibTyp()) {
		case LEER: {
			String[] toolTipArray = { "Leeres Sudoku zum Abschreiben:", "  - Um hier einen Tip zu bekommen zur Lösung",
					"  - Zum Ermitteln der Schwierigkeit", "Maus-Rechtsklick: Bringt die Pool-Info-Entnahme" };
			toolTip = ToolTip.gibToolTip(toolTipArray);
		}
			break;
		case VOLL: {
			String[] toolTipArray = { "Sudoku komplett gefüllt mit Vorgaben: Zum Selberbasteln eines Geschenk-Sudoku",
					"  - mit schöner Form (Diagonale, Kreuz, Spirale...)", "  - mit bestimmter Schwierigkeit",
					"Maus-Rechtsklick: Bringt die Pool-Info" };
			toolTip = ToolTip.gibToolTip(toolTipArray);
		}
			break;
		case VORLAGE: {
			String[] toolTipArray = { "Sudoku in genau der Form eines Vorlage-Sudoku" };
			toolTip = ToolTip.gibToolTip(toolTipArray);
		}
			break;
		case SCHWER:
			String s = String.format("Maus-Rechtsklick: Pool-Info wird abgelegt in %s/%s", neuTyp.gibName(),
					Schwierigkeiten.gibStartDateiName());
			toolTip = ToolTip.gibToolTip(new String[] { s });
			break;
		default:
			;
		}
		return toolTip;
	}

	// ============================================================
	private class MenuItemNeu extends JMenuItem { // implements ActionListener{
		private ButtonNeu parent;
		private NeuTyp neuTyp;

		public MenuItemNeu(ButtonNeu parent, NeuTyp neuTyp) {
			super(neuTyp.gibName());
			this.parent = parent;
			this.neuTyp = neuTyp;
			this.setToolTipText(gibToolTip(neuTyp));
			// this.addActionListener(this);
		}

		// @Override
		// public void actionPerformed(ActionEvent e) {
		// this.parent.erstelleNeuesSudoku(this.neuTyp);
		// }

		@Override
		protected void processMouseEvent(MouseEvent e) {
			if (e.getID() == MouseEvent.MOUSE_RELEASED) {
				if (e.getButton() == MouseEvent.BUTTON3) { // rechte Maustaste
					this.parent.sonderFunktion(neuTyp);
				} else {
					this.parent.erstelleNeuesSudoku(this.neuTyp);
				}
			}
			super.processMouseEvent(e);
		}

	}

	// ============================================================
	/**
	 * Erstellt das PopupMenu
	 * 
	 */
	public PopupMenuNeu(ButtonNeu parent) {
		ArrayList<NeuTyp> neuTypen = NeuTyp.gibAlleTypen();

		for (NeuTyp neuTyp : neuTypen) {
			MenuItemNeu item = new MenuItemNeu(parent, neuTyp);
			this.add(item);
			if (neuTyp.gibTyp() == Typ.VORLAGE) {
				this.addSeparator();
			}
		}
	}
}
