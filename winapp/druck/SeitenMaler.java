package winapp.druck;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public class SeitenMaler {

	/**
	 * Malt eine Seite
	 * @param g
	 * @param randX Rand relativ zur Seitenlänge
	 * @param randY Rand relativ zur Seitenlänge
	 * @param dSeite Größe der Seite
	 * @param pageIndex Index der zu malenden Seite
	 * @param seitenformat entweder ein mitgegebenes oder: 
	 * 					Bei null wird für die Sudoku-Anzahl und dSeite ein optimales Format erzeugt 
	 * @param moeglicheMarkierungSichtbarkeit 0.0=unsichtbar ..bis.. 1.0=voll sichtbar 
	 */
	public static void maleSeite(SudokuListe sudokus, int pageIndex, Graphics g, int rand, Dimension dSeite) {

		Graphics2D g2d = (Graphics2D) g;

		// int iRandX = (int) (dSeite.getWidth() * randX);
		// int iRandY = (int) (dSeite.getHeight() * randY);
		Dimension dMalBereich = new Dimension(dSeite.width - 2 * rand, dSeite.height - 2 * rand);

		// Mal-Koordinatensystem 0,0 auf Malbereich der Seite stellen
		g2d.translate(rand, rand);
		// Ist die Summe der Koordinaten-Ursprung-Verstellungen (ab jetzt)
		Point ursprung = new Point(0, 0);

		Seitenformat seitenformat = sudokus.gibSeitenformat(dMalBereich);
		int indexSudoku0 = seitenformat.gibSudokuIndex1(pageIndex);
		int anzahlPlaetze = seitenformat.gibPlatzAnzahl();
		int indexSudokuLast = indexSudoku0 + anzahlPlaetze - 1;
		if (indexSudokuLast > (sudokus.gibAnzahl() - 1)) {
			indexSudokuLast = sudokus.gibAnzahl() - 1;
		}

		// Jedes Sudoku malen
		for (int iPlatz = 0; iPlatz < anzahlPlaetze; iPlatz++) {
			int indexSudoku = indexSudoku0 + iPlatz;
			if (indexSudoku > indexSudokuLast) {
				break;
			}

			Rectangle rSudoku = seitenformat.gibSudokuMalPlatz(iPlatz, dMalBereich);
			{
				// Titel malen ------------------------------------------------------------
				Rectangle rTitel = seitenformat.gibTitelMalPlatz(rSudoku);

				// Mal-Koordinatensystem 0,0 auf Malstelle setzen
				int x = rTitel.x - ursprung.x;
				int y = rTitel.y - ursprung.y;
				ursprung.move(rTitel.x, rTitel.y);
				g2d.translate(x, y);
				sudokus.maleSudokuTitel(indexSudoku, g2d, rTitel.getSize());
			}

			// Sudoku malen ------------------------------------------------------------
			// Mal-Koordinatensystem 0,0 auf Malstelle setzen
			int x = rSudoku.x - ursprung.x;
			int y = rSudoku.y - ursprung.y;
			ursprung.move(rSudoku.x, rSudoku.y);
			g2d.translate(x, y);

			sudokus.maleSudoku(indexSudoku, g2d, rSudoku.getSize());
		} // for (int iPlatz
	} // public int male

}
