package sudoku.neu.pool;

import java.time.LocalDateTime;

public class AnzahlJeZeit {
	public static int[] gibAnzahlen(AnzahlJeZeit[] haeufigkeiten) {
		int[] anzahlen = new int[haeufigkeiten.length];
		for (int i = 0; i < haeufigkeiten.length; i++) {
			anzahlen[i] = haeufigkeiten[i].anzahl;
		}
		return anzahlen;
	}

	// ==============================
	public final LocalDateTime bis;
	public final int anzahl;

	/**
	 * @param bis
	 * @param anzahl
	 */
	AnzahlJeZeit(LocalDateTime bis, int anzahl) {
		super();
		this.bis = bis;
		this.anzahl = anzahl;
	}

}
