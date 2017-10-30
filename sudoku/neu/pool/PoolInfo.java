package sudoku.neu.pool;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;

import sudoku.logik.Schwierigkeit;

/**
 * @author heroe
 * Die PoolInfo beinhaltet zwei Informationen zum Sudoku-Pool:
 * 1. Inhalt: Aktueller Zustand des Pools
 * 2. Entstehung: Wie ist der aktuelle Pool-Zustand zeitlich entstanden?
 */
public class PoolInfo {
	static public InfoTopf gibSumme(Collection<InfoTopf> toepfe) {
		InfoTopf summe = new InfoTopf(null);
		Iterator<InfoTopf> iterator = toepfe.iterator();

		while (iterator.hasNext()) {
			InfoTopf next = iterator.next();
			summe.add(next);
		}
		return summe;
	}

	// ====================================================
	public final EnumMap<Schwierigkeit, InfoTopf> inhalt;
	public final EnumMap<Schwierigkeit, AnzahlJeZeit[]> entstehung;

	PoolInfo(EnumMap<Schwierigkeit, InfoTopf> inhalt, EnumMap<Schwierigkeit, AnzahlJeZeit[]> entstehung) {
		super();
		this.inhalt = inhalt;
		this.entstehung = entstehung;
	}
}
