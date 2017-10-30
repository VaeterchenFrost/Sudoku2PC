package winapp.druckinfo;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;
import java.util.Map;

import sudoku.kern.feldmatrix.FeldNummer;
import sudoku.kern.info.FeldInfo;
import sudoku.kern.info.InfoSudoku;
import winapp.druck.Druck;
import winapp.druck.Seitenformat;
import winapp.druck.SudokuListe;
import winapp.statusbar.StatusBar;

public class InfoSudokuDruck implements SudokuListe {

	public InfoSudokuDruck(String vorschauTitel, InfoSudoku[] infoSudokus, InfoSudokuMaler maler,
			boolean istFreiesSeitenformat) {
		super();
		this.vorschauTitel = vorschauTitel;
		this.infoSudokus = infoSudokus;
		this.maler = maler;
		this.istFreiesSeitenformat = istFreiesSeitenformat;
	}

	private String vorschauTitel;
	private InfoSudoku[] infoSudokus;
	private InfoSudokuMaler maler;
	private boolean istFreiesSeitenformat;

	@Override
	public int gibAnzahl() {
		return infoSudokus.length;
	}

	@Override
	public void maleSudokuTitel(int sudokuIndex, Graphics g, Dimension d) {
		InfoSudoku infoSudoku = infoSudokus[sudokuIndex];
		maler.registriereSudoku(infoSudoku, sudokuIndex);
		InfoSudokuIcon.maleSudokuTitel(infoSudoku, g, d, maler);
	}

	@Override
	public void maleSudoku(int sudokuIndex, Graphics g, Dimension d) {
		try {
			// double scale = d.getWidth() / this.frame.getWidth();
			// g2d.scale(scale, scale);
			InfoSudoku infoSudoku = infoSudokus[sudokuIndex];
			maler.registriereSudoku(infoSudoku, sudokuIndex);

			maler.maleSudokuRahmen(g, d);

			for (Map.Entry<FeldNummer, FeldInfo> entry : infoSudoku.entrySet()) {
				FeldInfo feldInfo = entry.getValue();
				maler.maleFeld(g, d, feldInfo);
			}
		} finally {
			// g2d.scale(1/scale, 1/scale);
		}
	}

	@Override
	public String gibTitel() {
		return vorschauTitel;
	}

	@Override
	public boolean speichere(String verzeichnisName) {
		for (int i = 0; i < this.infoSudokus.length; i++) {
			InfoSudoku infoSudoku = infoSudokus[i];
			String dateiName = String.format("%s\\%d", verzeichnisName, i);
			try {
				infoSudoku.speichern(dateiName);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * @param vorschauTitel
	 * @param sudokuListe
	 * @param maler
	 * @param istFreiesSeitenformat
	 * @param statusBar
	 * @return true wenn gedruckt wurde.
	 */
	public static boolean drucke(String vorschauTitel, InfoSudoku[] sudokuListe, InfoSudokuMaler maler,
			boolean istFreiesSeitenformat, StatusBar statusBar) {
		InfoSudokuDruck sudokuDrucker = new InfoSudokuDruck(vorschauTitel, sudokuListe, maler, istFreiesSeitenformat);
		boolean warDruck = Druck.drucke(sudokuDrucker, statusBar);
		return warDruck;
	}

	@Override
	public Seitenformat gibSeitenformat(Dimension dimension) {
		return Seitenformat.gibFormat(gibAnzahl(), istFreiesSeitenformat, dimension);
	}

}
