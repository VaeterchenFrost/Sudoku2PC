package logik;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kern.exception.Exc;
import kern.exception.UnerwarteterInhalt;
import kern.feldmatrix.Feld;
import kern.feldmatrix.FeldListe;
import kern.feldmatrix.FeldMatrix;
import kern.feldmatrix.FeldNummer;
import kern.feldmatrix.FeldNummerListe;
import kern.feldmatrix.FeldNummerMitZahl;
import kern.feldmatrix.ZahlenListe;
import logik.bericht.GruppenLaeufeListe;
import logik.tipinfo.EinTipText;
import logik.tipinfo.TipInfo;
import logik.tipinfo.TipInfo0;

/**
 * @author heroe
 */
class Logik_XYFluegel implements Logik__Interface {
	// ====================================================================
	static private FeldNummerListe gibMitspieler(Gruppe basisLinie, Gruppe gruppe2, Kasten kastenDesFeld2,
			FeldNummerListe loeschFelder) {
		FeldNummerListe mitspieler = new FeldNummerListe(basisLinie);
		FeldNummerListe mitspieler2 = new FeldNummerListe(gruppe2);
		mitspieler.add(mitspieler2);
		mitspieler.add(loeschFelder);
		if (kastenDesFeld2 != null) {
			mitspieler.addAll(new FeldNummerListe(kastenDesFeld2));
		}
		return mitspieler;
	}

	private class TipInfoXYFluegel extends TipInfo0 {
		private Gruppe basisLinie;
		private Gruppe gruppe2;
		private FeldNummer verbindungsFeld;
		private FeldNummer basisFeld2;
		private FeldNummer feld3;
		private int loeschZahl;
		private FeldNummerListe loeschFelder;

		private TipInfoXYFluegel(Gruppe basisLinie, Gruppe gruppe2, FeldNummer verbindungsFeld, FeldNummer basisFeld2,
				Kasten kastenDesFeld2, FeldNummer feld3, int loeschZahl, FeldNummerListe loeschFelder) {
			super(Logik_ID.XYFLUEGEL, gibMitspieler(basisLinie, gruppe2, kastenDesFeld2, loeschFelder));
			this.basisLinie = basisLinie;
			this.gruppe2 = gruppe2;
			this.verbindungsFeld = verbindungsFeld;
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

			String s2a = String.format("Feld%s und Feld%s liegen %s.", verbindungsFeld, basisFeld2,
					basisLinie.gibInText(false));
			EinTipText t2a = new EinTipText(s2a, null);

			String s2b = String
					.format("Feld%s und Feld%s liegen %s.", verbindungsFeld, feld3, gruppe2.gibInText(false));
			EinTipText t2b = new EinTipText(s2b, null);

			String s3 = String.format("Feld%s %s und %s verknüpft beide Gruppen.", verbindungsFeld,
					basisLinie.gibInText(false), gruppe2.gibInText(false));
			EinTipText t3 = new EinTipText(s3, null);

			String s4 = String.format("Die Zahl %d ist unbedingt notwendig in einem der beiden Felder %s bzw. %s.",
					loeschZahl, basisFeld2, feld3);
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
			nummern.add(verbindungsFeld);
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
	public Logik_XYFluegel(ArrayList<Gruppe> zeilen, ArrayList<Gruppe> spalten, ArrayList<Kasten> kaesten,
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
		return Logik_ID.XYFLUEGEL;
	}

	@Override
	public String gibKurzName() {
		return "XY";
	}

	@Override
	public String gibName() {
		return "XY-Flügel";
	}

	@Override
	public String[] gibWo() {
		return new String[] { "Nur Felder mit 2 möglichen Zahlen.", "3 Felder mit insgesamt 3 möglichen Zahlen.",
				"Jede mögliche Zahl tritt innerhalb der 3 Felder genau zweimal auf.",
				"3 Felder: 2 Felder davon auf 1er Zeile bzw. Spalte, 2 Felder davon in einer verknüpften Gruppe." };
	}

	@Override
	public String[] gibSituationAbstrakt() {
		return new String[] { "Weid ni" };
	}

	@Override
	public String[] gibSituation() {
		return new String[] { "Die gemeinsame mögliche Zahl der beiden nicht verknüpfenden Felder",
				"verbietet diese Zahl im Kreuzungs-Feld dieser Felder." };
	}

	@Override
	public String[] gibErgebnis() {
		return new String[] {
				"Im Kreuzungsfeld der nicht verknüpfenden Felder ist deren gemeinsame Zahl nicht möglich.",
				"In dem Fall, die verknüpfte Gruppe ist ein Kasten, ist diese Zahl außerdem nicht möglich:",
				" - im Kasten auf der Linie des Verknüpfungsfeldes",
				" - und im Kasten des 2. Feldes dieser Linie auf der Linie des Kreuzungsfeldes." };
	}

	@Override
	public double gibKontrollZeit1() {
		return 60;
	}

	// =============================================================
	/**
	 * @param linien Ihre Arbeitsfelder werden gesetzt mit den Feldern, die 2 mögliche Zahlen besitzen
	 */
	static void setzeArbeitsFelder(ArbeitsLinien linien) {
		for (int linienNr = 1; linienNr <= FeldMatrix.feldNummerMax; linienNr++) {
			ArbeitsLinien.Linie linie = linien.gibLinie(linienNr);
			linie.arbeitsFelder = linie.gruppe.gibFreieFelderMit2Moeglichen();
		}
	}

	/**
	 * @param kastenMap Ihre Arbeitsfelder werden gesetzt mit den Feldern, die 2 mögliche Zahlen besitzen
	 */
	static void setzeArbeitsFelder(Map<KastenIndex, ArbeitsKasten> kastenMap) {
		Collection<ArbeitsKasten> arbeitskaesten = kastenMap.values();
		for (ArbeitsKasten arbeitsKasten : arbeitskaesten) {
			arbeitsKasten.arbeitsFelder = arbeitsKasten.kasten.gibFreieFelderMit2Moeglichen();
		}
	}

	/**
	 * @param feld1
	 * @param feld2
	 * @return != null falls die beiden Felder feld1 und feld2 gemeinsam 3 mögliche Zahlen beinhalten. 
	 * 					In diesem Fall werden die beiden möglichen Zahlen zurückgegeben, 
	 * 					die das dritte zu suchende Feld besitzen muss für diese Logik. 
	 * 					Die Reihenfolge der möglichen Zahlen in der Rückgabeliste entspricht der von BasisLogik.setzeMoegliche(),
	 * 					um einen direkten Vergleich der möglichen Zahlen per ...equals() zu ermöglichen.
	 */
	static private ArrayList<Integer> gibFehlendeMoeglicheZahlen(Feld feld1, Feld feld2) {
		ArrayList<Integer> moegliche1 = feld1.gibMoegliche();
		ArrayList<Integer> moegliche2 = feld2.gibMoegliche();
		ArrayList<Integer> moegliche = new ArrayList<Integer>();

		moegliche.addAll(moegliche1);
		for (Integer zahl2 : moegliche2) {
			if (!moegliche.contains(zahl2)) {
				moegliche.add(zahl2);
			}
		}
		if (moegliche.size() != 3) {
			return null;
		}

		ArrayList<Integer> fehlende = new ArrayList<Integer>();
		for (Integer zahl : moegliche) {
			if (!(moegliche1.contains(zahl) & moegliche2.contains(zahl))) {
				fehlende.add(zahl);
			}
		}

		// aufsteigende Sortierung der möglichen Zahlen garantieren:
		fehlende = Feld.gibSortiert(fehlende);
		return fehlende;
	}

	/**
	 * Es wird vorausgesetzt, dass die beiden Felder insgesamt 3 mögliche Zahlen beinhalten, jedes Feld 2 davon. 
	 * @param feld1
	 * @param feld2
	 * @return Die in den beiden Feldern gemeinsame mögliche Zahl. 
	 */
	static private int gibLoeschZahl(Feld feld1, Feld feld2) {
		for (Integer zahl : feld1.gibMoegliche()) {
			if (feld2.istMoeglich(zahl)) {
				return zahl;
			}
		}
		throw new UnerwarteterInhalt(feld1.gibMoeglicheAlsString() + " " + feld2.gibMoeglicheAlsString());
	}

	/**
	 * @param loeschZahl Die mögliche Zahl, die zu löschen wäre.
	 * @param feld Dies Feld soll von der Betrachtung ausgeschlossen werden. Kann auch null übergeben worden sein. 
	 * @param basisLinie Die Felder dieser Gruppe, die auch im kasten liegen, sollen betrachtet werden.
	 * @param kasten
	 * @return Leere Liste oder die Nummern der betrachteten Felder, die die loeschZahl als mögliche Zahl enthalten.
	 */
	static FeldNummerListe gibLoeschFelder(int loeschZahl, Feld feld, Gruppe basisLinie, Kasten kasten) {
		FeldListe schnittMenge = basisLinie.schnittMenge(kasten);
		if (feld != null) {
			schnittMenge.remove(feld);
		}

		FeldNummerListe loeschFelder = new FeldNummerListe();
		for (int iFeld = 0; iFeld < schnittMenge.size(); iFeld++) {
			Feld testFeld = schnittMenge.get(iFeld);
			if (testFeld.istMoeglich(loeschZahl)) {
				loeschFelder.add(testFeld.gibFeldNummer());
			}
		}
		return loeschFelder;
	}

	/**
	 * @param loeschZahl Die mögliche Zahl, die in dieser Konstellation zu löschen wäre.
	 * @param feld1 Das Feld auf der BasisLinie, liegt auch im Kasten kasten13
	 * @param feld2 Das Feld auf der BasisLinie, liegt auch im Kasten kasten2
	 * @param feld3 Das Feld liegt im Kasten kasten13. Es liegt NICHT auf der BasisLinie. 
	 * @param basisLinie Mit den Feldern 1 und 2
	 * @param kasten13 Mit den Feldern 1 und 3
	 * @param kasten2 Mit dem Feld 2
	 * @param parallele Die Parallelen zur BasisLinie
	 * @return Die Nummern der Felder, in denen die mögliche Zahl <loeschZahl> zu löschen ist (oder leere Liste).
	 * 					Das sind maximal:
	 * 						a) Die Felder, die sowohl auf der BasisLinie als auch im Kasten13 liegen außer feld1.
	 * 						b) Die Felder, die auf der zur BasisLinie durch feld3 definierten Parallelen und Kasten2 liegen.
	 */
	static private FeldNummerListe gibLoeschFelder(int loeschZahl, Feld feld1, Feld feld2, Feld feld3,
			Gruppe basisLinie, Kasten kasten13, Kasten kasten2, ArbeitsLinien parallele) {
		FeldNummerListe loeschFelder = gibLoeschFelder(loeschZahl, feld1, basisLinie, kasten13);

		ArbeitsLinien.Linie feld3Linie = parallele.gibLinie(feld3.gibFeldNummer());
		Gruppe feld3Gruppe = feld3Linie.gruppe;
		FeldNummerListe loeschFelder2 = gibLoeschFelder(loeschZahl, null, feld3Gruppe, kasten2);
		loeschFelder.addAll(loeschFelder2);
		return loeschFelder;
	}

	/**
	 * @param loeschZahl Die mögliche Zahl, die in dieser Konstellation zu löschen wäre.
	 * 					Das Feld des Kreuzungspunktes der beiden Felder feld1 und feld2 kann eventuell das Löschfeld sein.
	 * @param feld1 
	 * @param feld2 Dieses Feld liegt auf einer anderen linie als das feld1
	 * @param linien Die Arbeitslinien, in denen auch das Lösch-Feld zu finden ist.
	 * @return null oder die Nummer des Feldes, in dem die mögliche Zahl <loeschZahl> zu löschen ist.
	 * 					null kommt zurück, wenn die <loeschZahl> nicht als mögliche Zahl in dem Feld des Kreuzungspunktes der beiden Felder existiert.
	 */
	static private FeldNummer gibLoeschFeld(int loeschZahl, Feld feld1, Feld feld2, ArbeitsLinien linien) {
		FeldNummer loeschFeldNummer = null;
		if (linien.istZeilen()) {
			loeschFeldNummer = new FeldNummer(feld1.gibSpalte(), feld2.gibZeile());
		} else {
			loeschFeldNummer = new FeldNummer(feld2.gibSpalte(), feld1.gibZeile());
		}
		ArbeitsLinien.Linie linie = linien.gibLinie(loeschFeldNummer);
		Feld feld = linie.gruppe.gibFeld_SehrLahm(loeschFeldNummer);
		if (feld.istMoeglich(loeschZahl)) {
			return loeschFeldNummer;
		}
		return null;
	}

	/**
	 * @param basisLinie Auf dieser Zeile bzw. Spalte sind feld1 und feld2 Mitglied
	 * @param feld1 Eine Gruppe dieses Feldes (Kasten oder Senkrechte) soll die 3. Zahl beinhalten
	 * @param feld2 Zweites Feld auf der BasisLinie
	 * @param kastenMap 
	 * @param senkrechte
	 * @return Die TipInfo wenn ein passendes 3. Feld zu feld1 und feld2 entsprechend dieser Logik gefunden wurde
	 * 					und dadurch zu löschende mögliche Zahlen erkannt wurden. 
	 */
	private TipInfoXYFluegel gibErgebnis(ArbeitsLinien.Linie basisLinie, Feld feld1, Feld feld2,
			ArrayList<Integer> fehlendeMoeglicheZahlen, Map<KastenIndex, ArbeitsKasten> kastenMap,
			ArbeitsLinien senkrechte, ArbeitsLinien parallele) {

		// 1. Das 3. Feld suchen im Kasten des Feldes feld1, allerdings nicht in Feldern der basisLinie
		KastenIndex kastenIndex1 = Kasten.gibKastenIndex(feld1.gibFeldNummer());
		ArbeitsKasten kasten1 = kastenMap.get(kastenIndex1);

		if (kasten1.arbeitsFelder != null) {
			FeldListe arbeitsFelder = kasten1.arbeitsFelder.differenz(basisLinie.gruppe);

			if (arbeitsFelder.size() > 0) {
				for (int iArbeitsFelder = 0; iArbeitsFelder < arbeitsFelder.size(); iArbeitsFelder++) {
					Feld feld3 = arbeitsFelder.get(iArbeitsFelder);
					ArrayList<Integer> testMoegliche = feld3.gibMoegliche();
					if (fehlendeMoeglicheZahlen.equals(testMoegliche)) {
						// Das sieht schon mal gut aus: Die Möglichen des Arbeitsfeldes stimmen.
						int loeschZahl = gibLoeschZahl(feld2, feld3);
						ArbeitsKasten kasten2 = kastenMap.get(Kasten.gibKastenIndex(feld2.gibFeldNummer()));
						FeldNummerListe loeschFelder = gibLoeschFelder(loeschZahl, feld1, feld2, feld3,
								basisLinie.gruppe, kasten1.kasten, kasten2.kasten, parallele);
						if (!loeschFelder.isEmpty()) {
							// Hurra: erfolgreiche Logik
							TipInfoXYFluegel tipInfo = new TipInfoXYFluegel(basisLinie.gruppe, kasten1.kasten,
									feld1.gibFeldNummer(), feld2.gibFeldNummer(), kasten2.kasten,
									feld3.gibFeldNummer(), loeschZahl, loeschFelder);
							return tipInfo;
						}
					}

				}
			}
		}
		// 2. Das 3. Feld suchen in der Senkrechten des Feld1
		ArbeitsLinien.Linie senkrechte1 = senkrechte.gibLinie(feld1.gibFeldNummer());

		if (senkrechte1.arbeitsFelder != null) {
			FeldListe arbeitsFelder = senkrechte1.arbeitsFelder;

			if (arbeitsFelder.size() > 1) {
				for (int iArbeitsFelder = 0; iArbeitsFelder < arbeitsFelder.size(); iArbeitsFelder++) {
					Feld feld3 = arbeitsFelder.get(iArbeitsFelder);

					ArrayList<Integer> testMoegliche = feld3.gibMoegliche();
					if (fehlendeMoeglicheZahlen.equals(testMoegliche)) {
						// Das sieht schon mal gut aus: Die Möglichen des Arbeitsfeldes stimmen.
						int loeschZahl = gibLoeschZahl(feld2, feld3);
						FeldNummer loeschFeld = gibLoeschFeld(loeschZahl, feld2, feld3, parallele);
						if (loeschFeld != null) {
							// Hurra: erfolgreiche Logik
							FeldNummerListe loeschFelder = new FeldNummerListe();
							loeschFelder.add(loeschFeld);
							TipInfoXYFluegel tipInfo = new TipInfoXYFluegel(basisLinie.gruppe, senkrechte1.gruppe,
									feld1.gibFeldNummer(), feld2.gibFeldNummer(), null, feld3.gibFeldNummer(),
									loeschZahl, loeschFelder);
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
			Map<KastenIndex, ArbeitsKasten> kastenMap, ArbeitsLinien senkrechte, List<TipInfo> ignorierTips) {
		for (int basisLinieNr = 1; basisLinieNr <= FeldMatrix.feldNummerMax; basisLinieNr++) {
			ArbeitsLinien.Linie basisLinie = basisLinien.gibLinie(basisLinieNr);
			if (basisLinie != null) {
				if (basisLinie.arbeitsFelder != null) {
					if (basisLinie.arbeitsFelder.size() > 1) {
						// Die Basis-Linie besitzt mindestens 2 Felder mit 2 möglichen Zahlen.
						gruppenLaeufeListe.add(basisLinie.gruppe.gibTyp());
						for (int iArbeitsFeld = 0; iArbeitsFeld < basisLinie.arbeitsFelder.size() - 1; iArbeitsFeld++) {
							Feld arbeitsFeld = basisLinie.arbeitsFelder.get(iArbeitsFeld);
							KastenIndex arbeitsFeldKastenIndex = Kasten.gibKastenIndex(arbeitsFeld.gibFeldNummer());

							for (int iTestFeld = 0; iTestFeld < basisLinie.arbeitsFelder.size(); iTestFeld++) {
								Feld testFeld = basisLinie.arbeitsFelder.get(iTestFeld);
								// Liegt das TestFeld in einem anderen Kasten?
								KastenIndex testFeldKastenIndex = Kasten.gibKastenIndex(testFeld.gibFeldNummer());
								if (!arbeitsFeldKastenIndex.equals(testFeldKastenIndex)) {
									// arbeitsFeld und TestFeld liegen in unterschiedlichen Kästen
									ArrayList<Integer> fehlendeMoeglicheZahlen = gibFehlendeMoeglicheZahlen(
											arbeitsFeld, testFeld);
									if (fehlendeMoeglicheZahlen != null) {
										// Arbeits- und Test-Feld besitzen zusammen genau 3 mögliche Zahlen.
										TipInfoXYFluegel tipInfo = gibErgebnis(basisLinie, arbeitsFeld, testFeld,
												fehlendeMoeglicheZahlen, kastenMap, senkrechte, basisLinien);
										if (tipInfo == null) {
											tipInfo = gibErgebnis(basisLinie, testFeld, arbeitsFeld,
													fehlendeMoeglicheZahlen, kastenMap, senkrechte, basisLinien);
										}
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
			}
		}

		return null;
	}

	@Override
	public LogikErgebnis laufen(boolean hatZeit, boolean istTip, List<TipInfo> ignorierTips) throws Exc {
		setzeArbeitsFelder(zeilen);
		setzeArbeitsFelder(spalten);
		setzeArbeitsFelder(kastenMap);

		GruppenLaeufeListe gruppenLaeufeListe = new GruppenLaeufeListe(Gruppe.Typ.ZEILE);
		TipInfo tipInfo = gibInfo(gruppenLaeufeListe, zeilen, kastenMap, spalten, ignorierTips);
		if (tipInfo == null) {
			tipInfo = gibInfo(gruppenLaeufeListe, spalten, kastenMap, zeilen, ignorierTips);
		}

		if (tipInfo != null) {
			LogikErgebnis logikErgebnis = new LogikErgebnis(gruppenLaeufeListe, null, tipInfo.gibLoeschZahlen(),
					tipInfo);
			return logikErgebnis;
		}
		return new LogikErgebnis(gruppenLaeufeListe);
	}
}
