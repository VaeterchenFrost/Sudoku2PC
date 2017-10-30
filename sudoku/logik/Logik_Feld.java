package logik;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kern.exception.Exc;
import kern.feldmatrix.Feld;
import kern.feldmatrix.FeldListe;
import kern.feldmatrix.FeldNummerListe;
import kern.feldmatrix.FeldNummerMitZahl;
import kern.feldmatrix.ZahlenListe;
import logik.bericht.GruppenLaeufeListe;
import logik.tipinfo.EinTipText;
import logik.tipinfo.TipInfo;
import logik.tipinfo.TipInfo0;

class Logik_Feld implements Logik__Interface {

	/**
	 * @param feld
	 * @param alleFelder
	 * @return Alle Feldnummern der Felder, die Mitglied der 3 Gruppen des Feldes sind:
	 * 					Zeile, Spalte, Kasten
	 */
	static private FeldNummerListe gibGruppenFeldNummern(Feld feld, FeldListe alleFelder) {
		Gruppe gruppe = new Gruppe(Gruppe.Typ.MIX, feld.gibFeldNummer(), false, alleFelder);
		FeldNummerListe gruppenFeldNummern = new FeldNummerListe(gruppe);
		return gruppenFeldNummern;
	}

	/**
	 * @param alleFelder
	 * @return Die Gruppen-FeldNummern je Feld
	 */
	static private Map<Feld, FeldNummerListe> gibGruppenFeldNummern(FeldListe alleFelder) {
		Map<Feld, FeldNummerListe> map = new HashMap<>();
		for (Feld feld : alleFelder) {
			FeldNummerListe feldNummern = gibGruppenFeldNummern(feld, alleFelder);
			map.put(feld, feldNummern);
		}
		return map;
	}

	// =========================================================
	private class TipInfoFeld extends TipInfo0 {
		private FeldNummerMitZahl feldNummerMitZahl;

		private TipInfoFeld(FeldNummerListe mitSpieler, FeldNummerMitZahl feldNummerMitZahl) {
			super(Logik_ID.FELD, mitSpieler);
			this.feldNummerMitZahl = feldNummerMitZahl;
		}

		// private String gibInText(FeldNummer feldNummer){
		// String s = feldNummer == null ? "" : feldNummer.toString();
		// return "Im Feld " + s;
		// }

		public EinTipText[] gibTip() {
			String s1 = String.format("Im Feld %s ist einzig die Zahl %d möglich.",
					this.feldNummerMitZahl.gibFeldNummer(), feldNummerMitZahl.gibZahl());
			EinTipText[] sArray = new EinTipText[] { new EinTipText(s1, null) };
			return sArray;
		}

		@Override
		public FeldNummerListe gibAktiveFelder() {
			FeldNummerListe aktiveFelder = new FeldNummerListe();
			FeldNummerMitZahl f = gibZahlFeld();
			if (f != null) {
				aktiveFelder.add(f.gibFeldNummer());
			}
			return aktiveFelder;
		}

		@Override
		public ZahlenListe gibLoeschZahlen() {
			return null;
		}

		@Override
		public boolean istZahl() {
			return this.feldNummerMitZahl != null;
		}

		@Override
		public FeldNummerMitZahl gibZahlFeld() {
			return this.feldNummerMitZahl;
		}

	}

	// =========================================================
	private FeldListe feldListe;
	private Map<Feld, FeldNummerListe> gruppenFeldNummern;

	public Logik_Feld(FeldListe feldListe) {
		this.feldListe = feldListe;
		if (feldListe != null) {
			this.gruppenFeldNummern = gibGruppenFeldNummern(feldListe);
		}
	}

	@Override
	public Logik_ID gibLogikID() {
		return Logik_ID.FELD;
	}

	@Override
	public String gibKurzName() {
		return "Fe";
	}

	@Override
	public String gibName() {
		return "In 1 Feld steht 1 Zahl fest";
	}

	@Override
	public String[] gibWo() {
		return new String[] { "In einem Feld" };
	}

	@Override
	public String[] gibSituationAbstrakt() {
		return new String[] { "In 1 Feld ist 1 Zahl festgelegt aufgrund der Belegung seiner Gruppen (Zeile, Spalte und Kasten)." };
	}

	@Override
	public String[] gibSituation() {
		return new String[] { "In 1 Feld ist nur 1 Zahl möglich aufgrund der Belegung seiner Gruppen (Zeile, Spalte und Kasten)." };
	}

	@Override
	public String[] gibErgebnis() {
		return new String[] { "Diese Zahl wird ein Eintrag." };
	}

	@Override
	public LogikErgebnis laufen(boolean hatZeit, boolean istTip, List<TipInfo> ignorierTips) throws Exc {
		FeldListe freieFelder = feldListe.gibFreieFelder();
		if (!freieFelder.isEmpty()) {

			GruppenLaeufeListe gruppenLaeufeListe = new GruppenLaeufeListe(null);

			for (int iFeld = 0; iFeld < freieFelder.size(); iFeld++) {
				Feld feld = freieFelder.get(iFeld);
				gruppenLaeufeListe.add(null);

				if (feld.istKlar()) {
					int zahl = feld.gibMoegliche().get(0);
					FeldNummerMitZahl sollEintrag = new FeldNummerMitZahl(feld.gibFeldNummer(), zahl);

					FeldNummerListe mitSpieler = gruppenFeldNummern.get(feld);

					TipInfo tipInfo = null;
					if (istTip) {
						tipInfo = new TipInfoFeld(mitSpieler, sollEintrag);
					}

					LogikErgebnis ergebnis = new LogikErgebnis(gruppenLaeufeListe, sollEintrag, null, tipInfo);
					return ergebnis;
				} // if (feldInfo != null){
			} // for (int iFeldNummer

			return new LogikErgebnis(gruppenLaeufeListe);
		} // if ( ! freieFelder.isEmpty()){

		return null;
	}

	@Override
	public double gibKontrollZeit1() {
		return 8;
	}

}
