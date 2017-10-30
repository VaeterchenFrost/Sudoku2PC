package sudoku.neu.pool;

import sudoku.neu.pool.Pool0.AblageVorschrift;

public class InfoTopf extends Info {
	public final AblageVorschrift ablageVorschrift;
	/**
	 * Anzahl der zweiten Sudokus mit gleicher Schwierigkeit 
	 */
	private int anzahlDoppel;

	public InfoTopf(AblageVorschrift ablageVorschrift) {
		super();
		this.ablageVorschrift = ablageVorschrift;
		this.anzahlDoppel = 0;
	}

	/**
	 * @param anzahl
	 * @param anzahlDoubel
	 * @param groesse
	 * @param leichtestes
	 * @param schwerstes
	 * @param aeltestes
	 * @param juengstes
	 */
	public InfoTopf(AblageVorschrift ablageVorschrift, int anzahl, int anzahlDoppel, long groesse, Integer leichtestes,
			Integer schwerstes, Long aeltestes, Long juengstes) {
		super(anzahl, groesse, leichtestes, schwerstes, Info.gibDatumMitZeit(aeltestes), Info
				.gibDatumMitZeit(juengstes));
		this.ablageVorschrift = ablageVorschrift;
		this.anzahlDoppel = anzahlDoppel;
	}

	public int gibAnzahlDoppel() {
		return anzahlDoppel;
	}

	/**
	 * @return Füllstand des Topfes in % der max. möglichen Anzahl Sudokus in diesem Topfe
	 */
	static private int gibFuellstand(int maxAnzahl, int anzahl) {
		int fuellstand = 0;
		if (anzahl < 20) {
			fuellstand = anzahl / 2;
		} else {
			if (anzahl <= maxAnzahl) {
				fuellstand = (anzahl * 100) / maxAnzahl;
			}
		}
		return fuellstand;
	}

	/**
	 * @return Füllstand der Basis-Sudokus des Topfes in % der max. möglichen Anzahl Bais-Sudokus in diesem Topfe
	 */
	public int gibFuellstand1() {
		int fuellstand = 0;

		if ((gibSchwerstes() != null) & (gibLeichtestes() != null)) {
			int maxAnzahl = gibSchwerstes() - gibLeichtestes() + 1;
			if (ablageVorschrift == AblageVorschrift.HALB) {
				maxAnzahl /= 2;
			}
			int meineAnzahl = gibAnzahl() - anzahlDoppel;
			fuellstand = gibFuellstand(maxAnzahl, meineAnzahl);
		}
		return fuellstand;
	}

	/**
	 * @return Füllstand der Doppel-Sudokus des Topfes in % der max. möglichen Anzahl Basis-Sudokus in diesem Topfe
	 */
	public int gibFuellstand2() {
		int fuellstand = 0;

		if ((gibSchwerstes() != null) & (gibLeichtestes() != null)) {
			int maxAnzahl = gibSchwerstes() - gibLeichtestes() + 1;
			fuellstand = gibFuellstand(maxAnzahl, anzahlDoppel);
		}
		return fuellstand;
	}

	/**
	 * @param topf2 Fügt die Werte der Info2 zu meinen dazu. Meine AblageVorschrift bleibt unverändert.
	 */
	public void add(InfoTopf info2) {
		super.add(info2);
		this.anzahlDoppel += info2.anzahlDoppel;
	}
}
