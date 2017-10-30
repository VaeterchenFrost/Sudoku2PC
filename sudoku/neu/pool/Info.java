package neu.pool;

import java.time.LocalDateTime;
import java.util.Calendar;

public class Info {
	/**
	 * @param dateiZeit
	 * @return Das Datum der übergebenen (Datei-)Zeit. Genauer gesagt: LocalDateTime mit allen Zeitangaben == 0.
	 */
	public static LocalDateTime gibDatum(Long dateiZeit) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateiZeit);
		LocalDateTime datum = LocalDateTime.of(cal.get(Calendar.YEAR), (cal.get(Calendar.MONTH) + 1),
				cal.get(Calendar.DAY_OF_MONTH), 0, 0);
		return datum;
	}

	/**
	 * @param dateiZeit
	 * @return Datum und Zeit Sekunden-genau der übergebenen (Datei-)Zeit. 
	 */
	static LocalDateTime gibDatumMitZeit(Long dateiZeit) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateiZeit);
		LocalDateTime datum = LocalDateTime.of(cal.get(Calendar.YEAR), (cal.get(Calendar.MONTH) + 1),
				cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE),
				cal.get(Calendar.SECOND));
		return datum;
	}

	// ===============================================================
	/**
	 * Anzahl aller Sudokus, die diese Info repräsentiert
	 */
	private int anzahl;
	/**
	 * Größe auf Dateträger aller Sudokus, die diese Info repräsentiert
	 */
	private long groesse;
	/**
	 * Lösungszeit des leichtesten Sudoku
	 */
	private Integer leichtestes;
	/**
	 * Lösungszeit des schwersten Sudoku
	 */
	private Integer schwerstes;
	/**
	 * Vom File.lastModified
	 */
	private LocalDateTime aeltestes;
	/**
	 * Vom File.lastModified
	 */
	private LocalDateTime juengstes;

	/**
	 * 
	 */
	public Info() {
		super();
		this.anzahl = 0;
		this.groesse = 0;
		this.leichtestes = null;
		this.schwerstes = null;
		this.aeltestes = null;
		this.juengstes = null;
	}

	/**
	 * @param anzahl
	 * @param groesse
	 * @param leichtestes
	 * @param schwerstes
	 * @param aeltestes
	 * @param juengstes
	 */
	public Info(int anzahl, long groesse, Integer leichtestes, Integer schwerstes, LocalDateTime aeltestes,
			LocalDateTime juengstes) {
		super();
		this.anzahl = anzahl;
		this.groesse = groesse;
		this.leichtestes = leichtestes;
		this.schwerstes = schwerstes;
		this.aeltestes = aeltestes;
		this.juengstes = juengstes;
	}

	public int gibAnzahl() {
		return anzahl;
	}

	public long gibGroesse() {
		return groesse;
	}

	public Integer gibLeichtestes() {
		return leichtestes;
	}

	public Integer gibSchwerstes() {
		return schwerstes;
	}

	public LocalDateTime gibAeltestes() {
		return aeltestes;
	}

	public LocalDateTime gibJuengstes() {
		return juengstes;
	}

	/**
	 * @param info2 Fügt die Werte der Info2 zu meinen dazu. 
	 */
	public void add(Info info2) {
		this.anzahl += info2.anzahl;
		this.groesse += info2.groesse;

		if (info2.leichtestes != null) {
			if (leichtestes == null) {
				this.leichtestes = info2.leichtestes;
			} else {
				if (info2.leichtestes < leichtestes) {
					leichtestes = info2.leichtestes;
				}
			}
		}
		if (info2.schwerstes != null) {
			if (this.schwerstes == null) {
				this.schwerstes = info2.schwerstes;
			} else {
				if (info2.schwerstes > schwerstes) {
					schwerstes = info2.schwerstes;
				}
			}
		}

		if (info2.aeltestes != null) {
			if (this.aeltestes == null) {
				this.aeltestes = info2.aeltestes;
			} else {
				if (info2.aeltestes.isBefore(aeltestes)) {
					aeltestes = info2.aeltestes;
				}
			}
		}

		if (info2.juengstes != null) {
			if (this.juengstes == null) {
				this.juengstes = info2.juengstes;
			} else {
				if (info2.juengstes.isAfter(juengstes)) {
					juengstes = info2.juengstes;
				}
			}
		}
	}
}
