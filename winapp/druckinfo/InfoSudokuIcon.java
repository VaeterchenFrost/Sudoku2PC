package winapp.druckinfo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Map;

import javax.swing.Icon;

import sudoku.kern.feldmatrix.FeldNummer;
import sudoku.kern.info.FeldInfo;
import sudoku.kern.info.InfoSudoku;
import winapp.feld.FeldMaler;

/**
 * @author heroe
 * Das Bild eines InfoSudokus, das eine definierte Größe besitzt und sich zeichnen kann
 */
public class InfoSudokuIcon implements Icon {
	private InfoSudoku infoSudoku;
	private InfoSudokuMaler maler;
	private Dimension dimension;

	public InfoSudokuIcon(InfoSudoku infoSudoku, InfoSudokuMaler maler, Dimension dimension) {
		this.infoSudoku = infoSudoku;
		this.maler = maler;
		this.dimension = dimension;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		int titelHoehe = InfoSudokuIcon.gibTitelHoehe(this.dimension.height);
		Dimension dTitel = new Dimension(this.dimension.width, titelHoehe);
		InfoSudokuIcon.maleSudokuTitel(this.infoSudoku, g, dTitel, maler);

		// MalUrsprung unter den Titel stellen
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(0, titelHoehe);

		// Mal-Groesse für das Sudoku selbst erstellen
		Dimension dSudoku = new Dimension(this.dimension.width, this.dimension.height - titelHoehe);
		FeldMaler.maleRechteck(g2d, new Rectangle(dSudoku), Color.WHITE);
		maler.maleSudokuRahmen(g, dSudoku);
		for (Map.Entry<FeldNummer, FeldInfo> entry : this.infoSudoku.entrySet()) {
			FeldInfo feldInfo = entry.getValue();
			maler.maleFeld(g, dSudoku, feldInfo);
		}
	}

	public int getIconWidth() {
		return dimension.width;
	}

	public int getIconHeight() {
		return dimension.height;
	}

	public static int gibTitelHoehe(int sudokuHoehe) {
		int titelhoehe = (int) ((sudokuHoehe / 9) * 3 / 4);
		return titelhoehe;
	}

	public static void maleSudokuTitel(InfoSudoku infoSudoku, Graphics g, Dimension d, InfoSudokuMaler maler) {
		String titel1 = infoSudoku.gibTitel1();
		if (titel1 != null) {
			if (!titel1.isEmpty()) {
				String titel2 = infoSudoku.gibTitel2();
				boolean t2Existiert = titel2 != null;
				if (t2Existiert) {
					t2Existiert = !titel2.isEmpty();
				}
				if (!t2Existiert) {
					maler.maleTitel(g, new Rectangle(d), titel1);
				} else {
					maler.maleTitel(g, new Rectangle(d), titel1, titel2);
				}
			}
		}
	}
}
