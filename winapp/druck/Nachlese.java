package winapp.druck;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import sudoku.kern.exception.Exc;
import sudoku.tools.Verzeichnis;
import winapp.statusbar.StatusBar;

public class Nachlese {

	private static boolean speichernBilder(SudokuListe sudokus, String pfad) {
		String dateiTyp = "jpg";
		String pfadName;
		try {
			pfadName = Verzeichnis.gibUnterverzeichnis(pfad, "Druck-Bilder", true);
		} catch (Exc e1) {
			return false;
		}

		// Da ich nicht weiß wie ich dem Bild eine Wunschauflösung geben kann:
		// Diese Auflösung wurde (warum diese?) gespeichert:
		int aufloesungDpi = 2 * 72; // 144 dpi: pixel je Inch
		double aufloesungDpMM = aufloesungDpi / (10 * 2.54); // Auflösung pixel je mm
		Dimension a4 = Seitenformat.gibA4Hochformat();
		Dimension dimension = new Dimension((int) (aufloesungDpMM * a4.width), (int) (aufloesungDpMM * a4.height));

		// Seiten-Format ermitteln
		Seitenformat seitenformat = sudokus.gibSeitenformat(dimension);
		int anzahlSudokus = sudokus.gibAnzahl();
		int anzahlSeiten = seitenformat.gibAnzahlSeiten(anzahlSudokus);

		for (int iSeite = 0; iSeite < anzahlSeiten; iSeite++) {
			BufferedImage bi = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_RGB);
			Graphics g = bi.createGraphics();
			{ // Hintergrund weißen (jedenfalls für zeichnen in BufferedImage)
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, dimension.width, dimension.height);
			}

			SeitenMaler.maleSeite(sudokus, iSeite, g, Seitenformat.gibRand(dimension), dimension);

			try {
				String dateiName = String.format("%s\\%d.%s", pfadName, iSeite, dateiTyp);
				File outputfile = new File(dateiName);
				ImageIO.write(bi, dateiTyp, outputfile);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	private static boolean speichernSudokus(SudokuListe sudokus, String pfad) {
		String pfadName;
		try {
			pfadName = Verzeichnis.gibUnterverzeichnis(pfad, "Druck-Sudokus", true);
		} catch (Exc e) {
			e.printStackTrace();
			return false;
		}

		return sudokus.speichere(pfadName);
	}

	public static void erstelle(SudokuListe sudokus, StatusBar statusBar) {
		// Pfad zum Speichern der Nachlese
		String pfadName = Verzeichnis.gibAktuellesVerzeichnis();
		String info = String.format("Druck-Nachlese in %s", pfadName);

		statusBar.zeigeInfo(info);

		boolean okBilder = speichernBilder(sudokus, pfadName);
		boolean okSudokus = speichernSudokus(sudokus, pfadName);
		if (!okBilder | !okSudokus) {
			info = "Fehler bei " + info;
			statusBar.zeigeProblem(info);
		}
	}

}
