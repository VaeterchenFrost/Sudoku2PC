package winapp.druckinfo.extras;

import sudoku.kern.info.InfoSudoku;
import sudoku.logik.Schwierigkeit;
import sudoku.schwer.SudokuSchwierigkeit;

/**
 * @author heroe
 * Ein InfoSudoku mit Attributen
 */
public class Gabe implements Comparable<Gabe> {
	public enum Figur {
		HERZ, KERZE, SCHNEEFLOCKE, SCHNEEMANN, STERN, ZWEIG, GLOCKE, MARIA, MOND, ZAHL, LOK, KEINE
	}

	private InfoSudoku infoSudoku;
	private Figur figur;
	private SudokuSchwierigkeit schwierigkeit;
	private String adressat;
	private String dateiname;

	/**
	 * @param infoSudoku
	 * @param schwierigkeit
	 */
	public Gabe(InfoSudoku infoSudoku, SudokuSchwierigkeit schwierigkeit) {
		super();
		this.infoSudoku = infoSudoku;
		this.figur = Figur.KEINE;
		this.schwierigkeit = schwierigkeit;
		this.adressat = new String();
		this.dateiname = new String();
	}

	/**
	 * @param infoSudoku
	 * @param figur
	 * @param schwierigkeit
	 * @param adressat
	 */
	public Gabe(InfoSudoku infoSudoku, Figur figur, SudokuSchwierigkeit schwierigkeit, String adressat, String dateiname) {
		super();
		this.infoSudoku = infoSudoku;
		this.figur = figur;
		this.schwierigkeit = schwierigkeit;
		this.adressat = adressat;
		this.dateiname = dateiname;
	}

	public InfoSudoku gibSudoku() {
		return infoSudoku;
	}

	public Figur gibFigur() {
		return figur;
	}

	public static Figur gibFigur(String name) {
		String nameGross = name.toUpperCase();
		for (Figur figur : Figur.values()) {
			if (nameGross.indexOf(figur.toString()) >= 0)
				return figur;
		}
		return Figur.KEINE;
	}

	public String gibAdressat() {
		return adressat;
	}

	public Schwierigkeit gibWieSchwer() {
		if (schwierigkeit == null) {
			return Schwierigkeit.SCHWER;
		}
		return schwierigkeit.gibKlareWieSchwer();
	}

	public int gibZeit() {
		return schwierigkeit.gibAnzeigeZeit();
	}

	private int getCompareResult(int me, int other) {
		if (me > other) {
			return 1;
		}
		if (me < other) {
			return -1;
		}
		return 0;
	}

	@Override
	public int compareTo(Gabe other) {
		if (this == other) {
			return 0;
		}
		if (other == null) {
			return 1;
		}
		if (getClass() != other.getClass()) {
			return 1;
		}
		// Figur
		boolean keineFigur = figur.equals(Figur.KEINE);
		boolean otherKeineFigur = other.figur.equals(Figur.KEINE);
		int istFigur = keineFigur ? 1 : 0;
		int istOtherFigur = otherKeineFigur ? 1 : 0;
		int cr = getCompareResult(istFigur, istOtherFigur);
		if (cr != 0) {
			return cr;
		}
		if (!keineFigur & !otherKeineFigur) {
			String sMein = dateiname.substring(0, 1);
			String sOther = other.dateiname.substring(0, 1);
			int iMein = 0;
			int iOther = 0;
			try {
				iMein = Integer.parseUnsignedInt(sMein);
				iOther = Integer.parseUnsignedInt(sOther);
				cr = getCompareResult(iMein, iOther);
				if (cr != 0) {
					return cr;
				}
			} catch (NumberFormatException e) {
			}
		}
		// Wie schwer
		cr = getCompareResult(schwierigkeit.gibKlareWieSchwer().ordinal(), other.schwierigkeit.gibKlareWieSchwer()
				.ordinal());
		if (cr != 0) {
			return cr;
		}
		// Zeit
		cr = getCompareResult(schwierigkeit.gibAnzeigeZeit(), other.schwierigkeit.gibAnzeigeZeit());
		return cr;
	}

}
