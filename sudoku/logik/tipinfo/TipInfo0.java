package logik.tipinfo;

import kern.feldmatrix.FeldNummerListe;
import kern.info.InfoSudoku;
import logik.Logik_ID;
import logik.SudokuLogik;

public abstract class TipInfo0 implements TipInfo {
	final protected Logik_ID logik;
	final protected FeldNummerListe mitSpieler;
	private InfoSudoku infoSudoku;

	public TipInfo0(Logik_ID logik, FeldNummerListe mitSpieler) {
		this.logik = logik;
		this.mitSpieler = mitSpieler;
		this.infoSudoku = null;
	}

	public void setzeSudoku(InfoSudoku infoSudoku) {
		this.infoSudoku = infoSudoku;
	}

	public Logik_ID gibLogik() {
		return logik;
	}

	public FeldNummerListe gibMitSpieler() {
		return mitSpieler;
	}

	public InfoSudoku gibSudoku() {
		return infoSudoku;
	}

	public String gibUeberschrift(int tipNummer) {
		String klugheitNameKurz = SudokuLogik.gibNameKurz(logik);
		String s = String.format("%d. Tip durch Logik %s:", tipNummer, klugheitNameKurz);
		return s;
	}

}
