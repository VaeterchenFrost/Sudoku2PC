package winapp.druck;

import java.awt.Dimension;

import winapp.InfoSudokuFrame;

/**
 * @author heroe
 * Diese Druckvorschau unterstellt das Seitenformat A4-Hochformat
 */
@SuppressWarnings("serial")
public class Vorschau extends InfoSudokuFrame {

	public Vorschau(SudokuListe sudokus) {
		super(sudokus);
		Dimension dFenster = this.getSize();
		Dimension dFensterRahmen = gibFensterRahmen();
		Dimension a4 = Seitenformat.gibA4Hochformat();
		int hoeheMalFlaeche = dFenster.height - dFensterRahmen.height;
		int breiteMalFlaeche = (int) (hoeheMalFlaeche * (1.0 * a4.width / a4.height));
		int breiteFenster = breiteMalFlaeche + dFensterRahmen.width;

		setLocation(dFenster.width - breiteFenster, 0);
		setSize(breiteFenster, dFenster.height);
	}

}
