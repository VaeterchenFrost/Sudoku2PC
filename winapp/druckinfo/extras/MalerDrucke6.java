package winapp.druckinfo.extras;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import sudoku.kern.info.FeldInfo;
import sudoku.kern.info.InfoSudoku;
import sudoku.neu.Zufall;
import winapp.druckinfo.MalerEinfach;
import winapp.feld.FeldAnzeige;
import winapp.feld.FeldMaler;

public class MalerDrucke6 extends MalerEinfach {
	private static Color[] farben = { Color.RED, Color.GREEN, Color.BLUE };
	private int iFarbe;

	public MalerDrucke6() {
		this.iFarbe = 0;
	}

	@Override
	public void registriereSudoku(InfoSudoku infoSudoku, int sudokuIndex) {
		iFarbe = Zufall.gib(farben.length);
		iFarbe = sudokuIndex / 2;
	}

	@Override
	public void maleFeld(Graphics g, Dimension dSudoku, FeldInfo feldInfo) {
		Rectangle rFeld = gibFeldRechteck(feldInfo.gibFeldNummer(), dSudoku);

		if (feldInfo.istVorgabe()) {
			int min = 200;
			int rot = Math.max(min, farben[iFarbe].getRed());
			int gruen = Math.max(min, farben[iFarbe].getGreen());
			int blau = Math.max(min, farben[iFarbe].getBlue());
			Color hintergrund = new Color(rot, gruen, blau);

			FeldMaler.maleRechteck(g, rFeld, hintergrund);
			FeldMaler.maleZahl(g, rFeld, Color.BLACK, feldInfo.gibVorgabe());
			return;
		}
		FeldAnzeige.maleEinfach(feldInfo, g, rFeld);
	}

}
