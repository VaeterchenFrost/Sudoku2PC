package sudoku.logik;

import java.util.ArrayList;
import java.util.List;

import sudoku.kern.exception.Exc;
import sudoku.kern.feldmatrix.FeldNummer;
import sudoku.kern.feldmatrix.FeldNummerListe;
import sudoku.kern.feldmatrix.FeldNummerMitZahl;
import sudoku.kern.feldmatrix.ZahlenListe;
import sudoku.logik.bericht.GruppenLaeufeListe;
import sudoku.logik.tipinfo.EinTipText;
import sudoku.logik.tipinfo.TipInfo;
import sudoku.logik.tipinfo.TipInfo0;

abstract class LogikKastenN implements Logik__Interface {
	/**
	 * @param gruppen
	 * @param freieFelderMin Anzahl Felder, die mindestens je Gruppe frei sein sollen
	 * @return
	 */
	static private ArrayList<Kasten> gibFreieKaesten(ArrayList<Kasten> kaesten, int freieFelderMin) {
		ArrayList<Kasten> freieKaesten = new ArrayList<>();

		for (int i = 0; i < kaesten.size(); i++) {
			Kasten kasten = kaesten.get(i);
			int anzahlFreieFelder = kasten.gibAnzahlFreieFelder();
			if (anzahlFreieFelder >= freieFelderMin) {
				freieKaesten.add(kasten);
			}
		}
		return freieKaesten;
	}

	// =========================================================
	protected class KastenErgebnis {

		private boolean istSpalte;
		private int loeschZahl;
		private FeldNummerListe loeschFelder;
		private FeldNummerListe nachbarUrsacheFelder;

		public KastenErgebnis(boolean istSpalte, int loeschZahl, FeldNummerListe geloeschtInFelder,
				FeldNummerListe nachbarUrsacheFelder) {
			super();
			this.istSpalte = istSpalte;
			this.loeschZahl = loeschZahl;
			this.loeschFelder = geloeschtInFelder;
			this.nachbarUrsacheFelder = nachbarUrsacheFelder;
		}

		public boolean istSpalte() {
			return istSpalte;
		}

		public int gibLoeschZahl() {
			return loeschZahl;
		}

		public FeldNummerListe gibLoeschFelder() {
			return loeschFelder;
		}

		public FeldNummerListe gibUrsacheFelder() {
			return nachbarUrsacheFelder;
		}

		public void addloeschFelder(FeldNummerListe felder) {
			loeschFelder.addAll(felder);
		}

		public void addUrsacheFelder(FeldNummerListe felder) {
			nachbarUrsacheFelder.addAll(felder);
		}
	}

	// =========================================================
	private class TipInfoKasten extends TipInfo0 {
		String textInGruppe;
		KastenErgebnis ergebnis;

		/**
		 * @param logik
		 * @param mitSpieler Sind diejenigen der Kastenlogik
		 * @param textInGruppe beschreibt den Kasten in dem mögliche Zahlen gelöscht wurden
		 * @param ergebnis
		 * @param infoSudoku
		 */
		private TipInfoKasten(Logik_ID logik, FeldNummerListe mitSpieler, String textInGruppe, KastenErgebnis ergebnis) {
			super(logik, mitSpieler);
			this.textInGruppe = textInGruppe;
			this.ergebnis = ergebnis;
		}

		private ArrayList<EinTipText> gibTipKasten1(KastenErgebnis ergebnis1) {
			FeldNummerListe ursachen = ergebnis1.gibUrsacheFelder();
			KastenIndex kastenIndex = Kasten.gibKastenIndex(ursachen.get(0));

			String kastenUrsache = Kasten.gibNameVomKastenIndex(kastenIndex);
			String sUrsache = ursachen.gibLinienName();
			String geloeschtIn = ergebnis1.gibLoeschFelder().gibKette("+");

			String s1 = String.format("%s wird die %d gelöscht in %s. Grund:", this.textInGruppe,
					ergebnis1.gibLoeschZahl(), geloeschtIn);
			String s2 = String.format("Der %s benötigt die %d in %s.", kastenUrsache, ergebnis1.gibLoeschZahl(),
					sUrsache);
			ArrayList<EinTipText> texte = new ArrayList<>();
			texte.add(new EinTipText(s1, s2));
			return texte;
		}

		private ArrayList<EinTipText> gibTipKasten2(KastenErgebnis ergebnis1) {
			// Ursache - Linien - Namen erstellen
			FeldNummerListe ursachen = ergebnis1.gibUrsacheFelder();
			ArrayList<Integer> linien = new ArrayList<>();

			if (ergebnis1.istSpalte()) {
				for (FeldNummer feldNummer : ursachen) {
					if (!linien.contains(feldNummer.spalte)) {
						linien.add(feldNummer.spalte);
					}
				}
			} else {
				// Zeile
				for (FeldNummer feldNummer : ursachen) {
					if (!linien.contains(feldNummer.zeile)) {
						linien.add(feldNummer.zeile);
					}
				}
			}
			String sLinien = new String("");
			for (int i = 0; i < linien.size(); i++) {
				if (i > 0) {
					sLinien += " und ";
				}
				sLinien += linien.get(i);
			}

			// Nachbar - Kästen - Namen erstellen
			FeldNummerListe geloeschtIn = ergebnis1.gibLoeschFelder();
			KastenIndex kastenIndex = Kasten.gibKastenIndex(geloeschtIn.get(0));
			KastenIndex[] nachbarnKastenIndizees = Kasten.gibNachbarn(kastenIndex, ergebnis1.istSpalte());
			String sNachbar1 = Kasten.gibNameVomKastenIndex(nachbarnKastenIndizees[0]);
			String sNachbar2 = Kasten.gibNameVomKastenIndex(nachbarnKastenIndizees[1]);
			String inText = ergebnis1.istSpalte() ? "in den Spalten" : "in den Zeilen";

			String s1a = String.format("%s wird die %d gelöscht", this.textInGruppe, ergebnis1.gibLoeschZahl());
			String s1b = String.format(" %s %s.", inText, sLinien);
			EinTipText s1 = new EinTipText(s1a, s1b);

			String s2a = String.format("Grund: %s und %s ", sNachbar1, sNachbar2);
			String s2b = String.format("benötigen die %d %s %s.", ergebnis1.gibLoeschZahl(), inText, sLinien);
			EinTipText s2 = new EinTipText(s2a, s2b);

			ArrayList<EinTipText> texte = new ArrayList<>();
			texte.add(s1);
			texte.add(s2);
			return texte;
		}

		@Override
		public EinTipText[] gibTip() {
			ArrayList<EinTipText> texte = new ArrayList<>();

			FeldNummerListe geloeschte = ergebnis.gibLoeschFelder();
			if (!geloeschte.isEmpty()) {
				if (this.logik == Logik_ID.KASTEN1) {
					texte = gibTipKasten1(ergebnis);
				} else {
					texte = gibTipKasten2(ergebnis);
				}
			}

			EinTipText[] texteArr = texte.toArray(new EinTipText[texte.size()]);
			return texteArr;
		}

		@Override
		public FeldNummerListe gibAktiveFelder() {
			FeldNummerListe feldNummerListe = new FeldNummerListe();

			feldNummerListe.add(ergebnis.gibLoeschFelder());

			return feldNummerListe;
		}

		@Override
		public ZahlenListe gibLoeschZahlen() {
			ZahlenListe loeschZahlen = new ZahlenListe();

			FeldNummerListe felderNummern = ergebnis.gibLoeschFelder();
			int geloeschteZahl = ergebnis.gibLoeschZahl();

			for (FeldNummer feldNummer : felderNummern) {
				FeldNummerMitZahl feldNummerMitZahl = new FeldNummerMitZahl(feldNummer, geloeschteZahl);
				loeschZahlen.add(feldNummerMitZahl);
			}

			return loeschZahlen;
		}

		@Override
		public boolean istZahl() {
			return false;
		}

		@Override
		public FeldNummerMitZahl gibZahlFeld() {
			return null;
		}

	}

	// =========================================================
	private ArrayList<Kasten> kaesten;

	public LogikKastenN(ArrayList<Kasten> kaesten) {
		this.kaesten = kaesten;
	}

	private boolean istErgebnisIgnorieren(List<TipInfo> ignorierTips, TipInfoKasten tipInfoKasten) {
		if (ignorierTips == null) {
			return false;
		}

		for (TipInfo tipInfo : ignorierTips) {
			TipInfoKasten ignorierTip = (TipInfoKasten) tipInfo;
			boolean gleicheMitspieler = ignorierTip.gibMitSpieler().istGleicherInhalt(tipInfoKasten.gibMitSpieler());
			boolean gleicheLoeschZahlen = ignorierTip.gibLoeschZahlen().istGleicherInhalt(
					tipInfoKasten.gibLoeschZahlen());
			if (gleicheMitspieler & gleicheLoeschZahlen) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Die Logik laufen lassen für den Kasten.
	 * @return Wenn die Logik erfolgreich gelaufen ist, d.h. Lösch-Zahlen erkannt wurden, dies Ergebnis, sonst null
	 */
	protected abstract KastenErgebnis laufen(Kasten kasten) throws Exc;

	@Override
	public LogikErgebnis laufen(boolean hatZeit, boolean istTip, List<TipInfo> ignorierTips) throws Exc {
		ArrayList<Kasten> freiekaesten = gibFreieKaesten(kaesten, 2);

		if (!freiekaesten.isEmpty()) {
			GruppenLaeufeListe gruppenLaeufeListe = new GruppenLaeufeListe(freiekaesten.get(0).gibTyp());

			for (int iKasten = 0; iKasten < freiekaesten.size(); iKasten++) {
				Kasten kasten = freiekaesten.get(iKasten);
				gruppenLaeufeListe.add(kasten.gibTyp());

				// Die Logik laufen lassen
				KastenErgebnis kastenErgebnis = laufen(kasten);

				if (kastenErgebnis != null) {
					// Dann war die Logik aktiv

					// lösch-Zahlen bereitstellen
					int loeschZahl = kastenErgebnis.gibLoeschZahl();
					FeldNummerListe loeschFelder = kastenErgebnis.gibLoeschFelder();
					ZahlenListe loeschZahlen = new ZahlenListe(loeschFelder, loeschZahl);

					boolean ergebnisIgnorieren = false;
					TipInfoKasten tipInfo = null;
					if (istTip) {
						FeldNummerListe mitSpieler = new FeldNummerListe(kasten);
						mitSpieler.add(kastenErgebnis.gibUrsacheFelder());

						tipInfo = new TipInfoKasten(gibLogikID(), mitSpieler, kasten.gibInText(true), kastenErgebnis);
						ergebnisIgnorieren = istErgebnisIgnorieren(ignorierTips, tipInfo);
					}

					if (!ergebnisIgnorieren) {
						LogikErgebnis logikErgebnis = new LogikErgebnis(gruppenLaeufeListe, null, loeschZahlen, tipInfo);
						return logikErgebnis;
					}
				} // if (kastenErgebnis != null){
			} // for(i

			return new LogikErgebnis(gruppenLaeufeListe);
		} // if ( ! freieKaesten.isEmpty()){

		return null;
	}

}
