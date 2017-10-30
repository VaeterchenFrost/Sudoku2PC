package winapp.druck;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import winapp.statusbar.StatusBar;

/**
 * @author heroe
 * Druckt eine Liste von Sudokus. Diese muss in der Form winapp.druck.Sudokus vorliegen.
 */
/**
 * @author heroe
 *
 */
public class Druck {
	/**
	 * Die sind auszudrucken
	 */
	private static SudokuListe sudokus = null;

	/**
	 * @author Hendrick Druckt eine Seite
	 */
	private static class SudokusPainter implements Printable {
		public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
			// Seiten-Format ermitteln
			Dimension dSeite = new Dimension((int) pageFormat.getWidth(), (int) pageFormat.getHeight());
			Seitenformat seitenformat = sudokus.gibSeitenformat(dSeite);
			int anzahlSudokus = sudokus.gibAnzahl();
			int anzahlSeiten = seitenformat.gibAnzahlSeiten(anzahlSudokus);

			if (pageIndex == anzahlSeiten) { // Es sind alle erforderlichen Seiten ausgedruckt
				return Printable.NO_SUCH_PAGE;
			}
			// Rand relativ zur Seitenlänge
			// double randX = pageFormat.getImageableX() / pageFormat.getWidth();
			// double randY = pageFormat.getImageableY() / pageFormat.getHeight();

			SeitenMaler.maleSeite(sudokus, pageIndex, g, (int) pageFormat.getImageableX(), dSeite);

			/* tell the caller that this page is part of the printed document */
			return Printable.PAGE_EXISTS;
		}
	} // private static class SudokuPainter

	/**
	 * @param sudokus
	 * @param statusBar
	 * @return true wenn gedruckt wurde, ansonsten wurde der Druck nicht gestartet (abgebrochen).
	 */
	public static boolean drucke(SudokuListe sudokus, StatusBar statusBar) {
		Druck.sudokus = sudokus;
		Frame vorschau = new Vorschau(sudokus);

		PrinterJob pjob = PrinterJob.getPrinterJob();
		boolean istZuDrucken = pjob.printDialog();
		if (istZuDrucken) {
			pjob.setPrintable(new SudokusPainter());
			try {
				pjob.print();
			} catch (PrinterException e) {
				e.printStackTrace();
			}
		}
		vorschau.dispose();

		Nachlese.erstelle(sudokus, statusBar);
		return istZuDrucken;
	}

}
