package winapp.druckinfo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import sudoku.kern.info.FeldInfo;
import sudoku.kern.info.InfoSudoku;
import winapp.feld.FeldMaler;

/**
 * @author heroe
 * Besitzt die Basis-Sudokus. Malt ein Feld mit Vergleich zum Feld im Basis-Sudoku.
 * Gibt sich selbst als Maler raus.
 */
public class MalerDifferenz extends MalerEinfach {

	private InfoSudoku[] basisSudokus;
	private InfoSudoku basisSudoku;

	public MalerDifferenz(InfoSudoku[] basisSudokus) {
		super();
		this.basisSudoku = null;
		this.basisSudokus = basisSudokus;
	}

	@Override
	public void maleFeld(Graphics g, Dimension dSudoku, FeldInfo feldInfo) {
		Rectangle rFeld = gibFeldRechteck(feldInfo.gibFeldNummer(), dSudoku);

		if (basisSudoku != null) {
			if (feldInfo != null) {
				if (feldInfo.istEintrag()) {
					FeldInfo feldInfoBasis = basisSudoku.get(feldInfo.gibFeldNummer());

					if (!feldInfo.istGleicherEintrag(feldInfoBasis)) {
						FeldMaler.maleRechteck(g, rFeld, Color.RED);
						FeldMaler.maleZahl(g, rFeld, Color.WHITE, feldInfo.gibEintrag());
						return;
					}
				}
			}
		}
		// Es gibt keine Differenz: Einfach malen
		super.maleFeld(g, dSudoku, feldInfo);
	}

	@Override
	public void registriereSudoku(InfoSudoku infoSudoku, int sudokuIndex) {
		basisSudoku = basisSudokus[sudokuIndex];
	}

}
