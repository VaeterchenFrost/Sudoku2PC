package winapp.statusbar;

import javax.swing.JOptionPane;

import sudoku.schwer.daten.InfoKlareDetail;
import winapp.SudokuFrame;

public class SchwierigkeitHilfe {

	public static void zeige() {
		String[] texte = InfoKlareDetail.gibAnzeigeTextBeschreibung();
		String text = "";
		for (int i = 0; i < texte.length; i++) {
			if (i != 0) {
				text += "\n";
			}
			text += texte[i];
		}
		JOptionPane.showMessageDialog(SudokuFrame.gibMainFrame(), text, "Schwierigkeit-Detail-Hilfe",
				JOptionPane.PLAIN_MESSAGE);
	}
}
