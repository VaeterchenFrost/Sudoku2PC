package schwer;

import java.util.ArrayList;

import kern.exception.Exc;
import kern.info.InfoSudoku;
import logik.Logik_ID;
import logik.Schwierigkeit;
import schwer.daten.AnalysatorKlare;
import schwer.daten.AnalysatorVersuch;
import schwer.daten.InfoKlareDetail;
import schwer.daten.InfoKlareZeit;
import schwer.daten.InfoUnbekannt;
import schwer.daten.InfoUnbestimmt;
import schwer.daten.InfoVersuche;
import schwer.daten.LogikAnzahlen;

/**
 * @author heroe
 * Alle Infos komplett zur Schwierigkeit eines Sudokus 
 * mit Zeit der logischen (Teil-)Lösung und den Versuchen
 */
public class SudokuSchwierigkeit {

	public static SudokuSchwierigkeit unbekannt() {
		return new SudokuSchwierigkeit();
	}

	public static SudokuSchwierigkeit unbestimmt(int nVorgaben) {
		return new SudokuSchwierigkeit(nVorgaben);
	}

	/**
	 * @param vorgaben
	 * @return null oder die Schwierigkeit (bei klaren Sudokus nie null)
	 */
	public static SudokuSchwierigkeit gibSchwierigkeit(InfoSudoku vorgaben) {
		SudokuSchwierigkeit schwierigkeit = null;
		try {
			schwierigkeit = Analysator.gibSchwierigkeit(vorgaben);
		} catch (Exc e) {
			e.printStackTrace();
		}
		return schwierigkeit;
	}

	// =============================================================
	private ArrayList<InfoKlareDetail> klareDetails;
	private InfoKlareZeit klareZeit;
	private ArrayList<InfoVersuche> versuchStarts;
	private AnzeigeInfo versucheOK;

	private SudokuSchwierigkeit() {
		super();
		init();
	}

	private void init() {
		this.klareDetails = new ArrayList<>();
		this.klareZeit = null;
		this.versuchStarts = null;
		this.versucheOK = new InfoUnbekannt();
	}

	private SudokuSchwierigkeit(int nVorgaben) {
		super();
		init();
		this.versucheOK = new InfoUnbestimmt(nVorgaben);
	}

	// public
	SudokuSchwierigkeit(knacker.bericht.BerichtKnacker bericht) {
		super();
		init();

		this.klareDetails = AnalysatorKlare.wandelUm(bericht);
		Schwierigkeit wieSchwer = this.gibKlareWieSchwer();
		this.klareZeit = new InfoKlareZeit(klareDetails, wieSchwer);
		this.versuchStarts = AnalysatorVersuch.gibVersuchsStarts(bericht);
		this.versucheOK = AnalysatorVersuch.gibVersucheOK(bericht);
	}

	public String gibName() {
		Schwierigkeit wieSchwer = this.gibKlareWieSchwer();

		String name = Schwierigkeit.gibName(wieSchwer);
		return name;
	}

	public ArrayList<InfoVersuche> gibVersuchStarts() {
		return versuchStarts;
	}

	public AnzeigeInfo gibAnzahlOKVersuche() {
		return versucheOK;
	}

	public InfoKlareZeit gibKlareZeit() {
		return klareZeit;
	}

	/**
	 * @return Die Zeit (ohne Versuche) in Minuten
	 */
	public int gibZeit() {
		if (klareZeit == null) {
			return 999;
		}
		return klareZeit.gibZeit();
	}

	/**
	 * @return Die Zeit (ohne Versuche) in Minuten gerastert
	 */
	public int gibAnzeigeZeit() {
		if (klareZeit == null) {
			return 999;
		}
		return klareZeit.gibAnzeigeZeit();
	}

	public ArrayList<InfoKlareDetail> gibDetails() {
		return klareDetails;
	}

	public LogikAnzahlen gibKlareErfolgreicheLogiken() {
		LogikAnzahlen logikAnzahlen = new LogikAnzahlen();

		for (InfoKlareDetail detail : klareDetails) {
			LogikAnzahlen detailLogikAnzahlen = detail.gibErfolgreicheLogiken();
			logikAnzahlen.add(detailLogikAnzahlen);
		}
		return logikAnzahlen;
	}

	/**
	 * @return Die Schwierigkeit, die für die (lineare) Lösung nötig war oder
	 * 					die kleinste Schwierigkeit wenn gar keine Logik benutzt wurde
	 */
	public Schwierigkeit gibKlareWieSchwer() {
		LogikAnzahlen logikAnzahlen = gibKlareErfolgreicheLogiken();

		int anzahlSumme = logikAnzahlen.gibAnzahlSumme();
		if (anzahlSumme == 0) {
			return Schwierigkeit.gibExtremTyp(false);
		}

		Logik_ID maxLogik = logikAnzahlen.gibGroessteLogik();
		Schwierigkeit schwierigkeit = Schwierigkeit.gibSchwierigkeit(maxLogik);

		if (schwierigkeit == null) {
			return Schwierigkeit.gibExtremTyp(false);
		}

		return schwierigkeit;
	}

	public void systemOut() {
		// + this.getClass().getPackage().getName() + "." + this.getClass().getName()" );
		for (int i = 0; i < klareDetails.size(); i++) {
			AnzeigeInfo lL = klareDetails.get(i);
			System.out.println(lL.gibAnzeigeText());
		}
		for (int i = 0; i < versuchStarts.size(); i++) {
			AnzeigeInfo lL = versuchStarts.get(i);
			System.out.println(lL.gibAnzeigeText());
		}
		System.out.println(klareZeit.gibAnzeigeText());
		System.out.println(versucheOK.gibAnzeigeText());
	}
}
