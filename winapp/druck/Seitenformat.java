package winapp.druck;

import java.awt.Dimension;
import java.awt.Rectangle;

import winapp.druckinfo.InfoSudokuIcon;

/**
 * @author heroe
 * Das Format einer Druckseite.
 * Zwischenraum ist 1/9 eines Sudokus.
 */
public class Seitenformat {
	private static Dimension a4 = new Dimension(210, 297); // mm
	// Rand relativ zur Gesamtgröße (zur kleineren Seitenlänge)
	private static double rand = 1.3 / 100.0;

	// Rand relativ zur Gesamthöhe
	// private static double randY = 4.0 / a4.height;

	/**
	 * @return A4-Hochformat in mm
	 */
	public static Dimension gibA4Hochformat() {
		return a4;
	}

	/**
	 * @param dimension Gesamtgröße
	 * @return Rand in der Einheit der Dimension
	 */
	public static int gibRand(Dimension dimension) {
		int l = Math.min(dimension.width, dimension.height);
		return (int) (rand * l);
	}

	/**
	 * @return Die maximale Anzahl Sudokus auf einer A$-Hochformat-Seite (im nicht-freien vordefinierten Format)
	 */
	public static int gibMaxSudokuAnzahlA4() {
		Seitenformat seitenformat = gibA4FormatFuerEineSeite(100);
		int anzahlPlaetze = seitenformat.gibPlatzAnzahl();
		return anzahlPlaetze;
	}

	/**
	 * @param sudokuAnzahl
	 * @return Das passende Seitenformat für die sudokuAnzahl auf A4-Hochformat.
	 * 				Das Format ist begrenzt auf 4x6.
	 */
	private static Seitenformat gibA4FormatFuerEineSeite(int sudokuAnzahl) {
		Seitenformat[] formate = { new Seitenformat(1, 1), new Seitenformat(1, 2), new Seitenformat(2, 2),
				new Seitenformat(2, 3), new Seitenformat(3, 4), new Seitenformat(3, 5), new Seitenformat(4, 5),
				new Seitenformat(4, 6) };
		for (int i = 0; i < formate.length; i++) {
			if (formate[i].gibPlatzAnzahl() >= sudokuAnzahl) {
				return formate[i];
			}
		}
		return formate[formate.length - 1];
	}

	/**
	 * @param sudokuAnzahl
	 * @return Das passende Seitenformat für die sudokuAnzahl
	 */
	private static Seitenformat gibA4Format(int sudokuAnzahl) {
		Seitenformat seitenformat = gibA4FormatFuerEineSeite(sudokuAnzahl);
		int platzJeSeite = seitenformat.gibPlatzAnzahl();
		if (platzJeSeite < sudokuAnzahl) {
			// Für die mehreren Seiten das beste Format nehmen
			int nSeiten = sudokuAnzahl / platzJeSeite;
			if ((sudokuAnzahl % platzJeSeite) > 0) {
				nSeiten++;
			}
			int nJeSeite = sudokuAnzahl / nSeiten;
			if ((sudokuAnzahl % nSeiten) > 0) {
				nJeSeite++;
			}
			seitenformat = gibA4FormatFuerEineSeite(nJeSeite);
		}
		return seitenformat;
	}

	/**
	 * @param sudokuAnzahl
	 * @param dimension des Malbereiches insgesamt
	 * @return Ein optimales Seitenformat für die Parameter
	 */
	private static Seitenformat gibFreiesFormat(int sudokuAnzahl, Dimension dimension) {
		double seitenVerhaeltnis = dimension.getWidth() / dimension.getHeight();
		Seitenformat seitenformat = new Seitenformat(1, 1);
		while (true) {
			int nSudoku = seitenformat.gibPlatzAnzahl();
			if (nSudoku >= sudokuAnzahl) {
				break;
			}
			double seitenVerhaeltnisPlaetze = seitenformat.gibSeitenVerhaeltnis();
			Seitenformat alternative = null;
			if (seitenVerhaeltnis > seitenVerhaeltnisPlaetze) {
				alternative = new Seitenformat(seitenformat.spalten, seitenformat.zeilen + 1);
				seitenformat.spalten++;
			} else {
				alternative = new Seitenformat(seitenformat.spalten + 1, seitenformat.zeilen);
				seitenformat.zeilen++;
			}

			if (alternative.gibPlatzAnzahl() >= sudokuAnzahl) {
				// Prüfen ob die Alternative besser ist
				Dimension dAlternative = alternative.gibSudokuDimension(dimension);
				Dimension dSeitenformat = seitenformat.gibSudokuDimension(dimension);
				if (dAlternative.width > dSeitenformat.width) {
					seitenformat = alternative;
				}
			}
		} // while true

		return seitenformat;
	}

	/**
	 * @param sudokuAnzahl
	 * @param istFrei: Bei false eines der vordefinierten A4-Hochformat-Seitenformate, 
	 * 					bei true ein optimal an dimension angepasstes Seitenformat 
	 * @param dimension
	 * @return Seitenformat für die Parameter
	 */
	public static Seitenformat gibFormat(int sudokuAnzahl, boolean istFrei, Dimension dimension) {
		if (istFrei) {
			return gibFreiesFormat(sudokuAnzahl, dimension);
		} else {
			return gibA4Format(sudokuAnzahl);
		}
	}

	// ==================================================================
	private int spalten;
	private int zeilen;

	/**
	 * @param spalten Anzahl
	 * @param zeilen Anzahl
	 */
	private Seitenformat(int spalten, int zeilen) {
		super();
		this.spalten = spalten;
		this.zeilen = zeilen;
	}

	/**
	 * @return Die Anzahl (Sudoku-) Plätze auf einer Seite
	 */
	public int gibPlatzAnzahl() {
		return spalten * zeilen;
	}

	public int gibAnzahlSeiten(int anzahlSudokus) {
		int anzahlSudokusJeSeite = this.gibPlatzAnzahl();
		int anzahlSeiten = anzahlSudokus / anzahlSudokusJeSeite;
		if (anzahlSudokus % anzahlSudokusJeSeite > 0) {
			anzahlSeiten++;
		}
		return anzahlSeiten;
	}

	/**
	 * @param seitenIndex 0-basiert
	 * @return Den 0-basierten Index des ersten Sudokus der Seite[seitenIndex]
	 */
	public int gibSudokuIndex1(int seitenIndex) {
		return gibPlatzAnzahl() * seitenIndex;
	}

	/**
	 * @return Die Kastenanzahl für Breite und Höhe: 
	 * Ein Sudoku besitzt in jeder Richtung 9 Kästen.
	 * Drüber ist stets eine Kastenhöhe reserviert für den Titel.
	 * Zwischen den Sudoku in der Breite ist ein Kasten Abstand.
	 */
	private Dimension gibKastenanzahl() {
		int nKastenX = 10 * spalten - 1;
		int nKastenY = 10 * zeilen;
		return new Dimension(nKastenX, nKastenY);
	}

	/**
	 * @param malBereich Gesamter Malbereich (einer Seite)
	 * @return Breite bzw. Höhe des Sudokus in Malbereich-Einheiten
	 */
	private Dimension gibSudokuDimension(Dimension malBereich) {
		Dimension dKasten = gibKastenanzahl();
		double kastenSeitenverhaeltnis = (1.0 * dKasten.getWidth()) / dKasten.getHeight();
		double seitenverhaeltnis = malBereich.getWidth() / malBereich.getHeight();
		double kastenLaenge = 0;
		if (kastenSeitenverhaeltnis >= seitenverhaeltnis) {
			// Breite zur Skalierung benutzen
			kastenLaenge = malBereich.getWidth() / dKasten.getWidth();
		} else {
			// Höhe zur Skalierung benutzen
			kastenLaenge = malBereich.getHeight() / dKasten.getHeight();
		}

		double maxKastenLaenge = 3.0 * malBereich.getWidth() / 5;
		if (kastenLaenge > maxKastenLaenge) {
			kastenLaenge = maxKastenLaenge;
		}

		int sudokuLaenge = (int) (9 * kastenLaenge);

		return new Dimension(sudokuLaenge, sudokuLaenge);
	}

	/**
	 * @param malBereich
	 * @param dSudoku1 Größe eines Sudoku
	 * @return Den Rand des gesamten Sudoku-Bereiches, der nötig ist, um die Sudokus mittig (zentriert) anzuzeigen
	 */
	private Dimension gibRand(Dimension malBereich, Dimension dSudoku1) {
		Dimension dKasten = gibKastenanzahl();
		int breite = (int) (dKasten.getWidth() * dSudoku1.getWidth() / 9);
		int hoehe = (int) (dKasten.getHeight() * dSudoku1.getHeight() / 9);
		int randX = (int) ((malBereich.getWidth() - breite) / 2);
		int randY = (int) ((malBereich.getHeight() - hoehe) / 2);
		return new Dimension(randX, randY);
	}

	/**
	 * @param iPlatz Platz-Index auf der Seite: Läuft zeilenweise von links nach rechts.
	 * @param malBereich Gesamter Malbereich (einer Seite)
	 * @return Die Mal-Position des Sudokus
	 */
	public Rectangle gibSudokuMalPlatz(int iPlatz, Dimension malBereich) {
		Dimension dSudoku = gibSudokuDimension(malBereich);
		Dimension dRand = gibRand(malBereich, dSudoku);

		int iZeile = iPlatz / spalten;
		int iSpalte = iPlatz - iZeile * spalten;
		int y = dSudoku.height / 9 + (dSudoku.height + dSudoku.height / 9) * iZeile;
		int x = (dSudoku.width + dSudoku.width / 9) * iSpalte;

		Rectangle r = new Rectangle(x + dRand.width, y + dRand.height, dSudoku.width, dSudoku.height);
		return r;
	}

	/**
	 * @param rSudoku Der Malbereich des einen Sudoku
	 * @return Der Malbereich des dazugehörigen Titels
	 */
	public Rectangle gibTitelMalPlatz(Rectangle rSudoku) {
		int titelhoehe = InfoSudokuIcon.gibTitelHoehe(rSudoku.height);
		Rectangle rTitel = new Rectangle(rSudoku.x, rSudoku.y - titelhoehe, rSudoku.width, titelhoehe);
		return rTitel;
	}

	/**
	 * @return Das Seitenverhaeltnis Breite / Höhe
	 */
	private double gibSeitenVerhaeltnis() {
		Dimension dKasten = gibKastenanzahl();
		return dKasten.getWidth() / dKasten.getHeight();
	}
}
