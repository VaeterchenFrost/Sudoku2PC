package winapp.druckinfo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import sudoku.kern.feldmatrix.FeldNummer;
import sudoku.kern.info.FeldInfo;
import sudoku.kern.info.InfoSudoku;
import winapp.feld.FeldAnzeige;
import winapp.feld.FeldFarbe;
import winapp.feld.FeldMaler;
import winapp.feld.FeldPosition;
import winapp.feld.PosUndLaenge;

/**
 * @author heroe
 * Malt Vorgabe schwarz auf grau (wie Feldanzeige), Eintrag blau auf weiß, leeres Feld weiß.
 */
public class MalerEinfach implements InfoSudokuMaler {
	// -----------------------------------------------------------------------------------------------
	/** Array mit den Rechtecken der Diagonalenfelder als Zwischenspeicher.
	 * 	Die Rechtecke auf Index 0 und 10 dienen der Rahmen-Errechnung.
	 */
	private class DiagonalenFeldRechtecke {
		Rectangle[] rDiagonale;
		Dimension dSudoku;

		DiagonalenFeldRechtecke(Rectangle[] rDiagonale, Dimension dSudoku) {
			this.rDiagonale = rDiagonale;
			this.dSudoku = dSudoku;
		}

		boolean istDimension(Dimension d) {
			return this.dSudoku.equals(d);
		}
	}

	// -----------------------------------------------------------------------------------------------

	private DiagonalenFeldRechtecke diagonalenFeldRechtecke;

	public MalerEinfach() {
		super();
		diagonalenFeldRechtecke = null;
	}

	private Rectangle gibFeldRechteck(FeldNummer feldNummer, Rectangle[] rDiagonale) {
		Rectangle rZeile = rDiagonale[feldNummer.gibZeile()];
		Rectangle rSpalte = rDiagonale[feldNummer.gibSpalte()];
		Rectangle rFeld = new Rectangle(rSpalte.x, rZeile.y, rSpalte.width, rZeile.height);
		return rFeld;
	}

	private ArrayList<Rectangle> gibRahmenRechtecke(Rectangle[] rDiagonale) {
		ArrayList<Rectangle> rRahmen = new ArrayList<>();

		Rectangle r0 = rDiagonale[0];
		Rectangle r10 = rDiagonale[10];

		Point posSudoku = new Point(r0.x + r0.width, r0.y + r0.height);
		Dimension dSudoku = new Dimension(r10.x - posSudoku.x - 1, r10.y - posSudoku.y - 1);

		for (int iRectangle = 0; iRectangle < rDiagonale.length - 1; iRectangle++) {
			Rectangle rD1 = rDiagonale[iRectangle];
			Rectangle rD2 = rDiagonale[iRectangle + 1];

			{
				Point posZeile = new Point(posSudoku.x, rD1.y + rD1.height);
				Dimension dZeile = new Dimension(dSudoku.width, rD2.y - posZeile.y);
				Rectangle rZeile = new Rectangle(posZeile, dZeile);
				rRahmen.add(rZeile);
			}

			Point posSpalte = new Point(rD1.x + rD1.width, posSudoku.y);
			Dimension dSpalte = new Dimension(rD2.x - posSpalte.x, dSudoku.height);
			Rectangle rSpalte = new Rectangle(posSpalte, dSpalte);
			rRahmen.add(rSpalte);
		}
		return rRahmen;
	}

	/**
	 * @param feldNummer
	 * @param sudokuHorizontal
	 * @param sudokuVertikal
	 * @return Das Rechteck des Feldes im Sudoku-Rechteck
	 */
	private Rectangle gibFeldRechteck(FeldNummer feldNummer, PosUndLaenge sudokuHorizontal,
			PosUndLaenge sudokuVertikal) {
		PosUndLaenge horizontalFeld = FeldPosition.gibPosUndLaenge(sudokuHorizontal, feldNummer.spalte);
		PosUndLaenge vertikalFeld = FeldPosition.gibPosUndLaenge(sudokuVertikal, feldNummer.zeile);
		Rectangle rFeld = new Rectangle(horizontalFeld.pos, vertikalFeld.pos, horizontalFeld.laenge,
				vertikalFeld.laenge);
		return rFeld;
	}

	/**
	 * @param dSudoku
	 * @return Array mit den Rechtecken der Diagonalenfelder.
	 * 					Die Rechtecke auf Index 0 und 10 dienen der Rahmen-Errechnung.
	 */
	private Rectangle[] gibDiagonalenRechtecke(Dimension dSudoku) {
		// Ist der Zwischenspeicher nutzbar?
		if (this.diagonalenFeldRechtecke != null) {
			if (this.diagonalenFeldRechtecke.istDimension(dSudoku)) {
				return this.diagonalenFeldRechtecke.rDiagonale;
			}
		}

		Rectangle[] rDiagonale = new Rectangle[11];

		PosUndLaenge sudokuHorizontal = new PosUndLaenge(0, dSudoku.width);
		PosUndLaenge sudokuVertikal = new PosUndLaenge(0, dSudoku.height);

		// Das Rechteck neben dem Sudoku links oben
		rDiagonale[0] = new Rectangle(-1, -1, 1, 1);

		for (int feldNr = 1; feldNr <= 9; feldNr++) {
			FeldNummer feldNummer = new FeldNummer(feldNr, feldNr);
			Rectangle rFeld = gibFeldRechteck(feldNummer, sudokuHorizontal, sudokuVertikal);
			rDiagonale[feldNr] = rFeld;
		}

		// Das Rechteck neben dem Sudoku rechts unten
		rDiagonale[10] = new Rectangle(dSudoku.width + 1, dSudoku.height + 1, 0, 0);

		// Ergebnis zwischenspeichern für das FeldMalen
		this.diagonalenFeldRechtecke = new DiagonalenFeldRechtecke(rDiagonale, dSudoku);

		return rDiagonale;
	}

	protected Rectangle gibFeldRechteck(FeldNummer feldNummer, Dimension dSudoku) {
		Rectangle[] rDiagonale = gibDiagonalenRechtecke(dSudoku);
		Rectangle rFeld = gibFeldRechteck(feldNummer, rDiagonale);
		return rFeld;
	}

	@Override
	public void maleFeld(Graphics g, Dimension dSudoku, FeldInfo feldInfo) {
		Rectangle rFeld = gibFeldRechteck(feldInfo.gibFeldNummer(), dSudoku);

		// if (feldInfo == null){
		// FeldMaler.maleRechteck(g, rFeld, FeldFarbe.hintergrund);
		// }
		FeldAnzeige.maleEinfach(feldInfo, g, rFeld);
	}

	@Override
	public void maleSudokuRahmen(Graphics g, Dimension dSudoku) {
		Rectangle[] rDiagonale = gibDiagonalenRechtecke(dSudoku);
		ArrayList<Rectangle> rRahmen = gibRahmenRechtecke(rDiagonale);

		for (Rectangle rR : rRahmen) {
			FeldMaler.maleRechteck(g, rR, FeldFarbe.gibSudokuRahmen());
		}
	}

	private static int maxTitelFontSize = 14;

	@Override
	public void maleTitel(Graphics g, Rectangle r, String titel) {
		FeldMaler.maleText(g, r, Color.WHITE, Color.BLACK, titel, maxTitelFontSize);
	}

	@Override
	public void maleTitel(Graphics g, Rectangle r, String titel1, String titel2) {
		FeldMaler.maleText(g, r, Color.WHITE, Color.BLACK, titel1, titel2, maxTitelFontSize);
	}

	@Override
	public void registriereSudoku(InfoSudoku infoSudoku, int sudokuIndex) {
	}

	@Override
	public boolean istMalenMarkierungMoeglicherZahl() {
		return false;
	}

}
