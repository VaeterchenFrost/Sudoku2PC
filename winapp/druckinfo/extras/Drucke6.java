package winapp.druckinfo.extras;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import sudoku.bedienung.SudokuBedienung;
import sudoku.kern.exception.Exc;
import sudoku.kern.info.InfoSudoku;
import sudoku.logik.Schwierigkeit;
import sudoku.neu.NeuTyp;
import sudoku.neu.pool.NeuTypOption;
import sudoku.schwer.Analysator;
import sudoku.schwer.SudokuSchwierigkeit;
import winapp.SudokuFrame;
import winapp.druckinfo.InfoSudokuDruck;
import winapp.statusbar.StatusBar;

public class Drucke6 {
	private static int nDruck = 6;

	static public void vorbereiten() {
		// Pool.setzeMaxForderung(nSudokus);
	}

	// static String gibName(Schwierigkeit.Typ typ){
	// String s = typ.toString();
	// return s.toLowerCase();
	// }
	//
	static public void erstelle(SudokuBedienung sudoku, JButton button, StatusBar statusBar) {
		Drucke6PopupMenue popupMenu = new Drucke6PopupMenue(sudoku, statusBar);
		int posX = button.getWidth() / 2;
		int posY = button.getHeight() / 2;
		popupMenu.show(button, posX, posY);
	}

	static public void erstelleUndDrucke(Schwierigkeit typ, NeuTypOption option, SudokuBedienung sudokuGeber,
			StatusBar statusBar) {
		ArrayList<InfoSudoku> sudokuList = new ArrayList<InfoSudoku>(nDruck);
		ArrayList<Integer> zeitList = new ArrayList<Integer>();
		for (int i = 0; i < nDruck; i++) {
			InfoSudoku infoSudoku = sudokuGeber.gibNeues(new NeuTyp(typ), option, null, null);
			if (infoSudoku != null) {
				SudokuSchwierigkeit schwierigkeit = null;
				try {
					schwierigkeit = Analysator.gibSchwierigkeit(infoSudoku);
				} catch (Exc e) {
					e.printStackTrace();
					return;
				}

				String wieSchwerName = schwierigkeit.gibName();
				Integer anzeigeZeit = schwierigkeit.gibAnzeigeZeit();
				infoSudoku.setzeTitel(wieSchwerName, anzeigeZeit.toString());
				sudokuList.add(infoSudoku);
				Integer zeit = schwierigkeit.gibZeit();
				zeitList.add(zeit);
			} // if (infoSudoku != null){
		} // for .. nDruck

		if (sudokuList.size() < nDruck) {
			String sProblem = String.format(
					"Z.Zt. sind nicht ausreichend Sudokus des Typs '%s' verfügbar. Bitte kurz warten.",
					Schwierigkeit.gibName(typ));
			JOptionPane.showMessageDialog(SudokuFrame.gibMainFrame(), sProblem, "Drucke 6", JOptionPane.PLAIN_MESSAGE);
			return;
		}

		// Die 6(!) Sudokus zum Drucken bereitstellen
		ArrayList<InfoSudoku> druckSudokuList = InfoSudoku.sortiereNachTitel2Zeit(sudokuList);

		// Die 6(!) Sudokus zum Drucken als Array bereitstellen
		InfoSudoku[] druckSudokus = new InfoSudoku[druckSudokuList.size()];
		druckSudokuList.toArray(druckSudokus);

		// Druck
		boolean warDruck = InfoSudokuDruck.drucke("", druckSudokus, new MalerDrucke6(), false, statusBar);
		if (!warDruck) {
			// Versuchen, die ungenutzten Sudokus zurückzulegen in den Sudoku-Pool
			for (int i = 0; i < nDruck; i++) {
				InfoSudoku infoSudoku = sudokuList.get(i);
				Integer zeit = zeitList.get(i);
				sudokuGeber.setze(new NeuTyp(typ), infoSudoku, zeit);
			}

		}
	}

}
