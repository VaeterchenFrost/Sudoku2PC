package sudoku.logik;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sudoku.Paar;
import sudoku.kern.exception.Exc;
import sudoku.kern.exception.UnerwarteterInhalt;
import sudoku.kern.feldmatrix.Feld;
import sudoku.kern.feldmatrix.FeldListe;
import sudoku.kern.feldmatrix.FeldMatrix;
import sudoku.kern.feldmatrix.FeldNummer;
import sudoku.kern.feldmatrix.FeldNummerListe;
import sudoku.kern.feldmatrix.FeldNummerMitZahl;
import sudoku.kern.feldmatrix.ZahlenListe;
import sudoku.logik.bericht.GruppenLaeufeListe;
import sudoku.logik.tipinfo.EinTipText;
import sudoku.logik.tipinfo.TipInfo;
import sudoku.logik.tipinfo.TipInfo0;

/**
 * @author heroe
 */
class Logik_XYZFluegel implements Logik__Interface {
	// ====================================================================
	static private FeldNummerListe gibMitspieler(Gruppe basisLinie, Kasten kastenDesFeld1) {
		FeldNummerListe mitspieler = new FeldNummerListe(basisLinie);
		FeldNummerListe mitspieler2 = new FeldNummerListe(kastenDesFeld1);
		mitspieler.add(mitspieler2);
		return mitspieler;
	}

	private class TipInfoXYZFluegel extends TipInfo0 {
		private Gruppe basisLinie;
		private Kasten kastenDesFeld1;
		private FeldNummer verbindungsFeld1;
		private FeldNummer basisFeld2;
		private FeldNummer feld3;
		private int loeschZahl;
		private FeldNummerListe loeschFelder;

		TipInfoXYZFluegel(Gruppe basisLinie, Kasten kastenDesFeld1, FeldNummer verbindungsFeld1, FeldNummer basisFeld2,
				FeldNummer feld3, int loeschZahl, FeldNummerListe loeschFelder) {
			super(Logik_ID.XYZFLUEGEL, gibMitspieler(basisLinie, kastenDesFeld1));
			this.basisLinie = basisLinie;
			this.kastenDesFeld1 = kastenDesFeld1;
			this.verbindungsFeld1 = verbindungsFeld1;
			this.basisFeld2 = basisFeld2;
			this.feld3 = feld3;
			this.loeschZahl = loeschZahl;
			this.loeschFelder = loeschFelder;
		}

		public EinTipText[] gibTip() {
			FeldNummerListe zahlenFeldNummern = gibAktiveFelder();
			String s1a = "In den 3 verknüpften Feldern " + zahlenFeldNummern.gibKette("+");
			String s1b = " sind insgesamt nur 3 Zahlen möglich.";
			EinTipText t1 = new EinTipText(s1a, s1b);

			String s2a = String.format("Feld%s und Feld%s liegen %s.", verbindungsFeld1, basisFeld2,
					basisLinie.gibInText(false));
			EinTipText t2a = new EinTipText(s2a, null);

			String s2b = String.format("Feld%s und Feld%s liegen %s.", verbindungsFeld1, feld3,
					kastenDesFeld1.gibInText(false));
			EinTipText t2b = new EinTipText(s2b, null);

			String s3 = String.format("Feld%s %s und %s verknüpft beide Gruppen.", verbindungsFeld1,
					basisLinie.gibInText(false), kastenDesFeld1.gibInText(false));
			EinTipText t3 = new EinTipText(s3, null);

			String s4 = String.format("Die Zahl %d ist unbedingt notwendig in einem der drei Felder.", loeschZahl);
			EinTipText t4 = new EinTipText(s4, null);

			String s5a = "in dem Feld";
			if (loeschFelder.size() > 1) {
				s5a = "in den Feldern";
			}
			String s5 = String.format("Die Zahl %d ist daher %s %s nicht möglich.", loeschZahl, s5a,
					loeschFelder.gibKette("+"));
			EinTipText t5 = new EinTipText(s5, null);

			EinTipText[] sArray = new EinTipText[] { t1, t2a, t2b, t3, t4, t5 };
			return sArray;
		}

		@Override
		public FeldNummerListe gibAktiveFelder() {
			FeldNummerListe nummern = new FeldNummerListe();
			nummern.add(verbindungsFeld1);
			nummern.add(basisFeld2);
			nummern.add(feld3);

			return nummern;
		}

		@Override
		public ZahlenListe gibLoeschZahlen() {
			ZahlenListe loeschZahlen = new ZahlenListe(loeschFelder, loeschZahl);
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

		/**
		 * @param ignorierTips
		 * @return true wenn dieser Tip einem der ignorierTips entspricht
		 */
		boolean ignorieren(List<TipInfo> ignorierTips) {
			if (ignorierTips == null) {
				return false;
			}
			if (ignorierTips.isEmpty()) {
				return false;
			}
			ZahlenListe loeschZahlen = gibLoeschZahlen();
			for (TipInfo tip : ignorierTips) {
				if (loeschZahlen.istGleicherInhalt(tip.gibLoeschZahlen())) {
					return true;
				}
			}
			return false;
		}
	}

	// ===========================================================
	private final ArbeitsLinien zeilen;
	private final ArbeitsLinien spalten;
	private final Map<KastenIndex, ArbeitsKasten> kastenMap;

	// private final SudokuLogik sudoku;

	/**
	 * Die Parameter sind bei Erstellung des Info-Objektes alle null!
	 * @param zeilen
	 * @param spalten
	 * @param kaesten
	 * @param sudoku
	 */
	public Logik_XYZFluegel(ArrayList<Gruppe> zeilen, ArrayList<Gruppe> spalten, ArrayList<Kasten> kaesten,
			SudokuLogik sudoku) {
		if (zeilen == null) {
			this.zeilen = null;
			this.spalten = null;
			this.kastenMap = null;
		} else {
			this.zeilen = new ArbeitsLinien(zeilen, true);
			this.spalten = new ArbeitsLinien(spalten, false);
			this.kastenMap = new HashMap<>();
			for (Kasten kasten : kaesten) {
				kastenMap.put(kasten.kastenIndex, new ArbeitsKasten(kasten));
			}
			// this.sudoku = sudoku;
		}
	}

	@Override
	public Logik_ID gibLogikID() {
		return Logik_ID.XYZFLUEGEL;
	}

	@Override
	public String gibKurzName() {
		return "XZ";
	}

	@Override
	public String gibName() {
		return "XYZ-Flügel";
	}

	@Override
	public String[] gibWo() {
		return new String[] {
				"3 Felder: 2 Felder davon auf 1er Zeile bzw. Spalte, 2 Felder davon in einem verknüpften Kasten.",
				"Die 3 Felder mit insgesamt 3 möglichen Zahlen. Eine Zahl in allen 3 Feldern. 2 Felder mit jeweils nur 2 Zahlen." };
	}

	@Override
	public String[] gibSituationAbstrakt() {
		return new String[] { "Weid ni" };
	}

	@Override
	public String[] gibSituation() {
		return new String[] {
				"Das Feld mit den 3 möglichen Zahlen verknüpft die Linie mit dem Kasten (Verbindungsfeld)." };
	}

	@Override
	public String[] gibErgebnis() {
		return new String[] {
				"Im Kasten ist die allen 3 Feldern gemeinsame Zahl auf den Feldern der Linie nicht möglich.",
				"(Außer im Verknüpfungsfeld.)" };
	}

	@Override
	public double gibKontrollZeit1() {
		return 60;
	}

	// =============================================================
	/**
	 * @param moegliche1 Die 3 möglichen Zahlen des Verbindungsfeldes.
	 * @param moegliche2 Die 2 möglichen Zahlen des einen möglichen Partnerfeldes.
	 * @return Die 2 Varianten der möglichen Zahlen des weiteren Partner-Feldes.
	 */
	static private Paar<ArrayList<Integer>, ArrayList<Integer>> gibFehlendeMoeglicheZahlen(
			ArrayList<Integer> moegliche1, ArrayList<Integer> moegliche2) {
		ArrayList<Integer> moeglicheA = new ArrayList<Integer>();
		ArrayList<Integer> moeglicheB = new ArrayList<Integer>();

		// 1. unbedingt notwendige Zahl einfügen
		for (int i = 0; i < moegliche1.size(); i++) {
			Integer zahl = moegliche1.get(i);
			if (!moegliche2.contains(zahl)) {
				moeglicheA.add(zahl);
				moeglicheB.add(zahl);
			}
		}

		// 2. Die beiden Varianten-Zahlen hinzufügen
		for (int i = 0; i < moegliche1.size(); i++) {
			Integer zahl = moegliche1.get(i);
			if (moegliche2.contains(zahl)) {
				if (moeglicheA.size() < 2) {
					moeglicheA.add(zahl);
				} else {
					moeglicheB.add(zahl);
				}
			}
		}

		moeglicheA = Feld.gibSortiert(moeglicheA);
		moeglicheB = Feld.gibSortiert(moeglicheB);

		Paar<ArrayList<Integer>, ArrayList<Integer>> fehlendeMoeglicheZahlen = new Paar<ArrayList<Integer>, ArrayList<Integer>>(
				moeglicheA, moeglicheB);
		return fehlendeMoeglicheZahlen;
	}

	/**
	 * Es wird vorausgesetzt, dass die beiden Felder insgesamt 3 mögliche Zahlen beinhalten, jedes Feld 2 davon. 
	 * @param feld1 Das Verbindungsfeld mit den 3 Zahlen
	 * @param feld2 Partnerfeld mit 2 Zahlen
	 * @param feld3 Partnerfeld mit 2 Zahlen
	 * @return Die in allen Feldern gemeinsame mögliche Zahl. 
	 */
	static private int gibLoeschZahl(Feld feld1, Feld feld2, Feld feld3) {
		for (Integer zahl : feld1.gibMoegliche()) {
			if (feld2.istMoeglich(zahl) & feld3.istMoeglich(zahl)) {
				return zahl;
			}
		}
		throw new UnerwarteterInhalt(feld1.gibMoeglicheAlsString() + " " + feld2.gibMoeglicheAlsString() + " "
				+ feld3.gibMoeglicheAlsString());
	}

	/**
	 * @param basisLinie Auf dieser Zeile bzw. Spalte sind feld1 und feld2 Mitglied
	 * @param feld1 Das Feld mit den 3 Zahlen. 
	 * @param feld2 Das weitere Feld auf der BasisLinie mit 2 möglichen Zahlen.
	 * @param fehlendeMoeglicheZahlen Die zwei Varianten der möglichen Zahlen eines 3. Feldes.
	 * @param arbeitsKasten In den ArbeitsFeldern dieses Kastens (des Feldes feld1) müsste das weitere 3. Feld liegen. 
	 * @return Die TipInfo wenn ein passendes 3. Feld zu feld1 und feld2 entsprechend dieser Logik gefunden wurde
	 * 					und dadurch zu löschende mögliche Zahlen erkannt wurden. 
	 */
	private TipInfoXYZFluegel gibErgebnis(ArbeitsLinien.Linie basisLinie, Feld feld1, Feld feld2,
			Paar<ArrayList<Integer>, ArrayList<Integer>> fehlendeMoeglicheZahlen, ArbeitsKasten arbeitsKasten) {

		if (arbeitsKasten.arbeitsFelder != null) {
			FeldListe arbeitsFelder = arbeitsKasten.arbeitsFelder.differenz(basisLinie.gruppe);

			if (arbeitsFelder.size() > 0) {
				for (int iArbeitsFelder = 0; iArbeitsFelder < arbeitsFelder.size(); iArbeitsFelder++) {
					Feld feld3 = arbeitsFelder.get(iArbeitsFelder);
					ArrayList<Integer> testMoegliche = feld3.gibMoegliche();
					boolean moegliche3OK = fehlendeMoeglicheZahlen.getKey().equals(testMoegliche);
					if (!moegliche3OK) {
						moegliche3OK = fehlendeMoeglicheZahlen.getValue().equals(testMoegliche);
					}

					if (moegliche3OK) {
						// Das sieht schon mal gut aus: Die Möglichen des Arbeitsfeldes stimmen.
						int loeschZahl = gibLoeschZahl(feld1, feld2, feld3);
						FeldNummerListe loeschFelder = Logik_XYFluegel.gibLoeschFelder(loeschZahl, feld1,
								basisLinie.gruppe, arbeitsKasten.kasten);
						if (!loeschFelder.isEmpty()) {
							// Hurra: erfolgreiche Logik
							TipInfoXYZFluegel tipInfo = new TipInfoXYZFluegel(basisLinie.gruppe, arbeitsKasten.kasten,
									feld1.gibFeldNummer(), feld2.gibFeldNummer(), feld3.gibFeldNummer(), loeschZahl,
									loeschFelder);
							return tipInfo;
						}
					}

				}
			}
		}
		// Nichts gefunden
		return null;
	}

	/**
	 * @param gruppenLaeufeListe
	 * @param basisLinien Benennen die Arbeitsfelder: Jedes der Felder besitzt 2 mögliche Zahlen.
	 * @param kastenMap Die Kästen greifbar über ihren Kasten-Index.
	 * @param senkrechte Senkrechte Linien zu den Arbeitslinien, ebenfalls mit den Arbeitsfeldern. 
	 * @param ignorierTips
	 * @return
	 */
	private TipInfo gibInfo(GruppenLaeufeListe gruppenLaeufeListe, ArbeitsLinien basisLinien,
			Map<KastenIndex, ArbeitsKasten> kastenMap, List<TipInfo> ignorierTips) {
		for (int basisLinieNr = 1; basisLinieNr <= FeldMatrix.feldNummerMax; basisLinieNr++) {
			ArbeitsLinien.Linie basisLinie = basisLinien.gibLinie(basisLinieNr);
			if (basisLinie != null) {
				if (basisLinie.arbeitsFelder != null) {
					if (basisLinie.arbeitsFelder.size() > 0) {
						// Die Basis-Linie besitzt mindestens 1 Feld mit 2 möglichen Zahlen.
						gruppenLaeufeListe.add(basisLinie.gruppe.gibTyp());
						Gruppe gruppe = basisLinie.gruppe;

						for (int iGruppenFeld = 0; iGruppenFeld < gruppe.size(); iGruppenFeld++) {
							Feld feld1 = gruppe.get(iGruppenFeld);
							ArrayList<Integer> moegliche1 = feld1.gibMoegliche();
							if (moegliche1.size() == 3) {
								// Dies Feld könnte das Verbindungsfeld zwischen Linie und Kasten sein
								FeldNummer feld1FeldNummer = feld1.gibFeldNummer();
								KastenIndex kastenIndex = Kasten.gibKastenIndex(feld1FeldNummer);
								ArbeitsKasten arbeitsKasten = kastenMap.get(kastenIndex);
								if (arbeitsKasten.arbeitsFelder != null) {
									if (!arbeitsKasten.arbeitsFelder.isEmpty()) {
										// Im Kasten gibt es Felder mit 2 möglichen Zahlen
										for (int iTestFeld = 0; iTestFeld < basisLinie.arbeitsFelder
												.size(); iTestFeld++) {
											Feld testFeld = basisLinie.arbeitsFelder.get(iTestFeld);
											// Liegt das TestFeld in einem anderen Kasten?
											KastenIndex testFeldKastenIndex = Kasten
													.gibKastenIndex(testFeld.gibFeldNummer());
											if (!kastenIndex.equals(testFeldKastenIndex)) {
												// arbeitsFeld und TestFeld liegen in unterschiedlichen Kästen
												ArrayList<Integer> moegliche2 = testFeld.gibMoegliche();
												if (moegliche1.containsAll(moegliche2)) {
													// Das TestFeld besitzt 2 der 3 möglichen Zahlen des Feldes feld1
													Paar<ArrayList<Integer>, ArrayList<Integer>> fehlendeMoeglicheZahlen = gibFehlendeMoeglicheZahlen(
															moegliche1, moegliche2);

													TipInfoXYZFluegel tipInfo = gibErgebnis(basisLinie, feld1, testFeld,
															fehlendeMoeglicheZahlen, arbeitsKasten);
													if (tipInfo != null) {
														if (!tipInfo.ignorieren(ignorierTips)) {
															return tipInfo;
														}
													}
												}
											}
										}
									}
								}

							}
						} // for (int iGruppenFeld
					}
				}
			}
		}

		return null;
	}

	@Override
	public LogikErgebnis laufen(boolean hatZeit, boolean istTip, List<TipInfo> ignorierTips) throws Exc {
		Logik_XYFluegel.setzeArbeitsFelder(zeilen);
		Logik_XYFluegel.setzeArbeitsFelder(spalten);
		Logik_XYFluegel.setzeArbeitsFelder(kastenMap);

		GruppenLaeufeListe gruppenLaeufeListe = new GruppenLaeufeListe(Gruppe.Typ.ZEILE);

		TipInfo tipInfo = gibInfo(gruppenLaeufeListe, zeilen, kastenMap, ignorierTips);
		if (tipInfo == null) {
			tipInfo = gibInfo(gruppenLaeufeListe, spalten, kastenMap, ignorierTips);
		}

		if (tipInfo != null) {
			LogikErgebnis logikErgebnis = new LogikErgebnis(gruppenLaeufeListe, null, tipInfo.gibLoeschZahlen(),
					tipInfo);
			return logikErgebnis;
		}
		return new LogikErgebnis(gruppenLaeufeListe);
	}
}
