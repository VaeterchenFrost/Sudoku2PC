package winapp.druck;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.RepaintManager;

import winapp.druckinfo.MalerEinfach;
import winapp.statusbar.StatusBar;

/**
 * @author Hendrick 
 * Druckt Frame aus.
 * In Zusammenhang mit Druck.SudokusPainter ist FrameDruck.maleSudoku() 
 * nur noch ein unbenutztes Beispiel für den Druck eines java.Frame !
 */
public class FrameDruck implements SudokuListe {
	// Ist true nur während des Druckens des Frames
	private static boolean varIstDruck = false;

	public static boolean istDruck() {
		return varIstDruck; // Private
	}

	private JFrame frame;

	FrameDruck(JFrame frame) {
		this.frame = frame;
	}

	@Override
	public int gibAnzahl() {
		return 2;
	}

	@Override
	public void maleSudokuTitel(int sudokuIndex, Graphics g, Dimension d) {
		Graphics2D g2d = (Graphics2D) g;
		String s = String.format("Hallöchenne % d", sudokuIndex);
		new MalerEinfach().maleTitel(g2d, new Rectangle(d), s, s);
	}

	@Override
	public void maleSudoku(int sudokuIndex, Graphics g, Dimension d) {
		Graphics2D g2d = (Graphics2D) g;
		double scaleX = d.getWidth() / this.frame.getWidth();
		double scaleY = d.getHeight() / this.frame.getHeight();
		try {
			g2d.scale(scaleX, scaleY);
			// Für die spezielle Malerei während des Druckens:
			RepaintManager currentManager = RepaintManager.currentManager(frame);
			currentManager.setDoubleBufferingEnabled(false);
			FrameDruck.varIstDruck = true;
			frame.paint(g2d);
			FrameDruck.varIstDruck = false;
			currentManager.setDoubleBufferingEnabled(true);
		} finally {
			g2d.scale(1 / scaleX, 1 / scaleY);
		}
	}

	public static void drucke(JFrame frame, StatusBar statusBar) {
		FrameDruck drucksudoku = new FrameDruck(frame);
		Druck.drucke(drucksudoku, statusBar);
	}

	@Override
	public String gibTitel() {
		return null;
	}

	@Override
	public boolean speichere(String verzeichnisName) {
		return true;
	}

	@Override
	public Seitenformat gibSeitenformat(Dimension dimension) {
		return Seitenformat.gibFormat(gibAnzahl(), false, dimension);
	}

}
