package winapp.druckinfo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import sudoku.kern.info.FeldInfo;
import winapp.feld.FeldAnzeige;
import winapp.feld.FeldMaler;

/**
 * @author heroe
 * Malt das Feld genauso wie die FeldAnzeige
 */
public class MalerStandard extends MalerEinfach {
	protected boolean maleMoegliche;

	public MalerStandard(boolean maleMoegliche) {
		this.maleMoegliche = maleMoegliche;
	}

	@Override
	public void maleTitel(Graphics g, Rectangle r, String titel) {
		FeldMaler.maleText(g, r, Color.WHITE, Color.BLACK, titel, 0);
	}

	@Override
	public void maleFeld(Graphics g, Dimension dSudoku, FeldInfo feldInfo) {
		Rectangle rFeld = gibFeldRechteck(feldInfo.gibFeldNummer(), dSudoku);

		FeldAnzeige.male(feldInfo, g, rFeld, maleMoegliche, true);
	}

	@Override
	public boolean istMalenMarkierungMoeglicherZahl() {
		return maleMoegliche;
	}

}
