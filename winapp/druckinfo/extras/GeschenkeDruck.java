package winapp.druckinfo.extras;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import sudoku.kern.EintragsEbenen;
import sudoku.kern.exception.Exc;
import sudoku.kern.feldmatrix.FeldMatrix;
import sudoku.kern.info.InfoSudoku;
import sudoku.logik.Schwierigkeit;
import sudoku.schwer.Analysator;
import sudoku.schwer.SudokuSchwierigkeit;
import sudoku.tools.Verzeichnis;
import winapp.druckinfo.InfoSudokuDruck;
import winapp.druckinfo.extras.Gabe.Figur;
import winapp.statusbar.StatusBar;
import winapp.toolbar.DateiDialog;

/**
 * @author heroe
 * Lädt alle Sudokus eines Verzeichnisses. Dies ist die Bereitstellung für einen Druck.
 */
public class GeschenkeDruck {
	private boolean istSystemOut = false;
	private DateiDialog dialog;

	public GeschenkeDruck(JFrame frame) {
		dialog = new DateiDialog(frame, false);
	}

	private Gabe gibGabe(String fileName) {
		try {
			InfoSudoku vorgaben = InfoSudoku.lade(fileName);
			FeldMatrix fm = new FeldMatrix(new EintragsEbenen());
			fm.reset(vorgaben);

			InfoSudoku infoSudoku = fm.gibFeldInfos();
			Figur figur = Gabe.gibFigur(fileName);
			SudokuSchwierigkeit schwierigkeit = Analysator.gibSchwierigkeit(vorgaben);

			String adressat = Verzeichnis.gibLetztesUnterverzeichnis(fileName);
			String dateiname = Verzeichnis.gibDateiname(fileName);
			Gabe gabe = new Gabe(infoSudoku, figur, schwierigkeit, adressat, dateiname);
			return gabe;
		} catch (IOException | Exc e1) {
			e1.printStackTrace();
			return null;
		}
	}

	private void setzeSudokuTitel(int gabeIndex, Gabe gabe) {
		String wieSchwerName = Schwierigkeit.gibName(gabe.gibWieSchwer());
		int zeit = gabe.gibZeit();
		String adressat = gabe.gibAdressat();
		String[] gruesse = { "XXX", "ich wünsche dir ein ", "herzerwärmendes", "wunderschönes Fest", "und viel Spaß",
				"beim Sudokuen!" };
		String gruss = gruesse[gabeIndex];
		if (gruss == "XXX") {
			gruss = adressat + ",";
		}

		String titel2 = String.format("(%s %d)", wieSchwerName, zeit);
		gabe.gibSudoku().setzeTitel(gruss, titel2);
	}

	public void erstelle(Frame frame, StatusBar statusBar) {
		frame.setTitle("Geschenk");
		dialog.setVisible(true);
		if (dialog.getFile() == null) {
			// JOptionPane.showMessageDialog(null, "Kein Geschenk!");
			return;
		}

		String dirName = dialog.getDirectory();
		File dir = new File(dirName);
		File[] fileList = dir.listFiles();

		if (fileList.length < 1) {
			JOptionPane.showMessageDialog(null, "Es ist kein Geschenk da!");
			return;
		}

		if (fileList.length != 6) {
			JOptionPane.showMessageDialog(null, "Es dürfen nur genau 6 Sudokus sein als Geschenk!");
			return;
		}

		frame.setTitle("Geschenk " + dirName);
		ArrayList<Gabe> gaben = new ArrayList<>(fileList.length);
		if (istSystemOut) {
			System.out.println("Geschenk.erstelle()");
		}
		for (File f : fileList) {
			if (istSystemOut) {
				System.out.println(f.getName());
			}
			Gabe gabe = gibGabe(dirName + f.getName());
			if (gabe != null) {
				gaben.add(gabe);
			}
		}
		if (gaben.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Kein Geschenk!");
			return;
		}
		// gaben sortieren
		Collections.sort(gaben);

		// In die InfoSudokus ihre Titel setzen
		for (int iGabe = 0; iGabe < gaben.size(); iGabe++) {
			setzeSudokuTitel(iGabe, gaben.get(iGabe));
		}

		// Druck-InfoSudokus und deren Figuren bereitstellen
		InfoSudoku[] druckSudokus = new InfoSudoku[gaben.size()];
		Figur[] figuren = new Figur[gaben.size()];
		for (int iGabe = 0; iGabe < gaben.size(); iGabe++) {
			Gabe gabe = gaben.get(iGabe);
			druckSudokus[iGabe] = gabe.gibSudoku();
			figuren[iGabe] = gabe.gibFigur();
		}

		// Druck
		InfoSudokuDruck.drucke(Verzeichnis.gibLetztesUnterverzeichnis(dirName), druckSudokus,
				new MalerGeschenk(figuren), false, statusBar);
	}

}
