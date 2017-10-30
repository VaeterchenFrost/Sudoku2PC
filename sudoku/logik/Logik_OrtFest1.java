package logik;

import java.util.ArrayList;
import java.util.List;

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

class Logik_OrtFest1 implements Logik__Interface {

	// =========================================================
	private class TipInfoOrtFest1 extends TipInfo0 {
		private FeldNummerMitZahl klareZahl;
		private Gruppe gruppe;

		private TipInfoOrtFest1(FeldNummerMitZahl klareZahl, Gruppe gruppe) {
			super(Logik_ID.ORTFEST1, new FeldNummerListe(gruppe));
			this.klareZahl = klareZahl;
			this.gruppe = gruppe;
		}

		public EinTipText[] gibTip() {
			String s1 = String.format("%s ist die Zahl %d", gruppe.gibInText(true), klareZahl.gibZahl());
			String s2 = String.format(" nur in dem einen Feld %s möglich.", klareZahl.gibFeldNummer());

			EinTipText[] sArray = new EinTipText[] { new EinTipText(s1, s2) };
			return sArray;
		}

		@Override
		public boolean istZahl() {
			return true;
		}

		@Override
		public FeldNummerMitZahl gibZahlFeld() {
			return klareZahl;
		}

		@Override
		public FeldNummerListe gibAktiveFelder() {
			FeldNummerListe feldNummerListe = new FeldNummerListe();
			feldNummerListe.add(klareZahl.gibFeldNummer());
			return feldNummerListe;
		}

		@Override
		public ZahlenListe gibLoeschZahlen() {
			return null;
		}
	}

	// =========================================================
	private ArrayList<Gruppe> gruppen;

	public Logik_OrtFest1(ArrayList<Gruppe> gruppen) {
		this.gruppen = gruppen;
	}

	@Override
	public Logik_ID gibLogikID() {
		return Logik_ID.ORTFEST1;
	}

	@Override
	public String gibKurzName() {
		return "O1";
	}

	@Override
	public String gibName() {
		return "Ort ist fest für 1 Zahl";
	}

	@Override
	public String[] gibWo() {
		return new String[] { "In einer Gruppe (Zeile, Spalte bzw. Kasten)" };
	}

	@Override
	public String[] gibSituationAbstrakt() {
		return new String[] { "Der Ort für 1 Zahl ist festgelegt." };
	}

	@Override
	public String[] gibSituation() {
		return new String[] { "Es ist 1 Zahl nur in 1 Feld möglich." };
	}

	@Override
	public String[] gibErgebnis() {
		return new String[] { "Diese Zahl wird ein Eintrag." };
	}

	@Override
	public LogikErgebnis laufen(boolean hatZeit, boolean istTip, List<TipInfo> ignorierTips) throws Exc {
		ArrayList<Gruppe> freieGruppen = Gruppe.gibFreieGruppen(gruppen, 1);
		if (!freieGruppen.isEmpty()) {
			GruppenLaeufeListe gruppenLaeufeListe = new GruppenLaeufeListe(freieGruppen.get(0).gibTyp());

			for (int i = 0; i < freieGruppen.size(); i++) {
				Gruppe gruppe = freieGruppen.get(i);
				gruppenLaeufeListe.add(gruppe.gibTyp());

				FeldNummerMitZahl klareZahl = null;
				for (int zahl = 1; zahl < 10; zahl++) {
					FeldListe zahlenFelder = gruppe.gibFelderDerMoeglichenZahl(zahl);

					if (1 == zahlenFelder.size()) {
						Feld feld = zahlenFelder.get(0);
						klareZahl = new FeldNummerMitZahl(feld.gibFeldNummer(), zahl);
						// Soll-Eintrag ist erkannt!
						break;
					}
				}

				if (klareZahl != null) {
					TipInfo tipInfo = null;
					if (istTip) {
						tipInfo = new TipInfoOrtFest1(klareZahl, gruppe);
					}

					LogikErgebnis ergebnis = new LogikErgebnis(gruppenLaeufeListe, klareZahl, null, tipInfo);
					return ergebnis;
				}
			} // for (int i = 0; i < freieGruppen

			return new LogikErgebnis(gruppenLaeufeListe);
		} // if ( ! freieGruppen.isEmpty()){

		return null;
	}

	@Override
	public double gibKontrollZeit1() {
		return 4.4;
	}

}
