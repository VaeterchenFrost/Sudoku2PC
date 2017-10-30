package neu;

import kern.animator.Animator;
import kern.animator.Animator_SpiegelnMittelPunkt;
import kern.exception.Exc;
import kern.feldmatrix.FeldMatrix;
import kern.feldmatrix.FeldNummer;
import kern.feldmatrix.FeldNummerListe;
import kern.info.FeldInfo;
import kern.info.InfoSudoku;
import kern.protokoll.Protokoll.Schrittweite;
import knacker.Ergebnis;
import knacker.Knacker.VersuchsEbenen;
import knacker.bericht.BerichtKnacker;
import logik.Klugheit;
import logik.Schwierigkeit;
import schwer.Analysator;
import schwer.SudokuSchwierigkeit;
import schwer.daten.LogikAnzahlen;

class Generator extends Generator1 {

	/**
	 * @param neuTyp
	 * @return Neues Sudoku des neuTyp oder null falls keines erstellbar war.
	 */
	public static GeneratorErgebnis gibNeuesSudoku(NeuTyp neuTyp) {
		// Animator symmetrierer = null;
		Animator symmetrierer = new Animator_SpiegelnMittelPunkt();
		if (istSystemOut()) {
			String sSymmetrierer = symmetrierer == null ? "" : " mit " + symmetrierer.getClass().getSimpleName();
			System.out.println(String.format("%s.gibNeuesSudoku() Start %s %s", Generator.class.getName(), neuTyp,
					sSymmetrierer));
		}

		// Erstellung eines neuen Sudoku
		Generator g = new Generator(neuTyp);
		GeneratorErgebnis ergebnis = g.gibNeues(symmetrierer);
		if (istSystemOut()) {
			String sErgebnis = ergebnis.infoSudoku == null ? "null" : " Sudoku mit "
					+ ergebnis.infoSudoku.gibAnzahlVorgaben() + " Vorgaben";
			System.out.println(String.format("%s.gibNeuesSudoku() Ergebnis=%s", Generator.class.getName(), sErgebnis));
		}

		return ergebnis;
	}

	/**
	 * Nur zur Dokumentation
	 */
	private static Zaehler nVersuchStarts = new Zaehler();

	/**
	 * Zum Loggen der genutzten Logiken 
	 */
	private static LogikAnzahlen erfolgreicheLogiken = new LogikAnzahlen();

	static void logErfolgreicheLogiken() {
		LogikAnzahlenSpeicher.logLogikAnzahlen(erfolgreicheLogiken);
		erfolgreicheLogiken = new LogikAnzahlen();
	}

	/**
	 * @param feldMatrix In diesen Feldern wird gelöscht
	 * @param vorgaben Aus diesen Feldern sollen anzahl Felder gelöscht werden
	 * @param anzahl zu löschender Vorgaben. Gilt auch bei symmetrierer != null.
	 * @param symmetrierer Falls != null soll das Sudoku symmetrisch aussehen.
	 * 					In diesem Fall sollte die Anzahl gerade sein: 
	 * 					Es werden jetzt jeweils 2 symmetrisch zueinander liegende Vorgaben gelöscht. 
	 * @return gelöschte Felder
	 * @throws Exc 
	 */
	static private FeldNummerListe loesche(FeldMatrix feldMatrix, FeldNummerListe vorgaben, int anzahl,
			Animator symmetrierer) throws Exc {
		if (istSystemOut() & istSystemOutAlles()) {
			System.out.println(String.format("lösche() n=%d", anzahl));
		}
		FeldNummerListe loeschFelder = Zufall.gibAuswahlFelder(vorgaben, anzahl, symmetrierer);

		for (FeldNummer feldNummer : loeschFelder) {
			feldMatrix.setzeVorgabe(feldNummer, 0);
		}
		return loeschFelder;
	}

	/**
	 * @param feldMatrix In einem dieser Felder wird gelöscht. 
	 * @param vorgaben Aus diesen Feldern soll 1 Vorgabe gelöscht werden
	 * @param symmetrierer Falls != null soll das Sudoku symmetrisch aussehen. 
	 * 					In diesem Fall werden 2 Vorgaben gelöscht, falls noch 2 Vorgaben zur Verfügung stehen! 
	 * @return FeldNummern der gelöschten Vorgabe(n)
	 * @throws Exc
	 */
	static private FeldNummerListe loesche1Vorgabe(FeldMatrix feldMatrix, FeldNummerListe vorgaben,
			Animator symmetrierer) throws Exc {
		int anzahlLoeschungen = symmetrierer == null ? 1 : 2;
		FeldNummerListe loeschFelder = Zufall.gibAuswahlFelder(vorgaben, anzahlLoeschungen, symmetrierer);
		for (FeldNummer feldNummer : loeschFelder) {
			feldMatrix.setzeVorgabe(feldNummer, 0);
		}
		return loeschFelder;
	}

	// Ende static
	// =================================================
	public class GeneratorErgebnis {
		public final NeuTyp neuTyp;
		public final InfoSudoku infoSudoku;
		public final int loesungsZeit;
		public final int laufNummer;

		/**
		 * @param infoSudoku Auch null falls kein Sudoku erstellt werden konnte.
		 * @param neuTyp Der Typ des erstellten Sudoku. Er muss nicht mit dem angeforderten Typ übereinstimmen!
		 * @param loesungsZeit
		 * @param laufNummer Nummer des Laufes, der erfolgreich dies Sudoku erzeugt hat.
		 */
		GeneratorErgebnis(InfoSudoku infoSudoku, NeuTyp neuTyp, int loesungsZeit, int laufNummer) {
			super();
			this.neuTyp = neuTyp;
			this.infoSudoku = infoSudoku;
			this.loesungsZeit = loesungsZeit;
			this.laufNummer = laufNummer;
		}

	}

	// =================================================
	private final NeuTyp neuTyp;

	private Generator(NeuTyp neuTyp) {
		super();
		this.neuTyp = neuTyp;
	}

	/**
	 * @param loesung
	 * @param symmetrierer Falls != null soll das Sudoku symmetrisch aussehen 
	 * @return null wenn die Bestimmtheit für eine Lösung nicht ausreicht,
	 * 					ansonsten gesetzte Felder, die im weiteren nicht gelöscht werden dürfen
	 * 					(ansonsten würde man die lineare Lösung wieder verlassen werden) 
	 * @throws Exc
	 */
	private FeldNummerListe sichereLineareLoesung(InfoSudoku loesung, Klugheit klugheit, Animator symmetrierer)
			throws Exc {
		if (istSystemOut() & istSystemOutAlles()) {
			System.out.println("sichereLineareLoesung()");
		}

		FeldNummerListe versuchStarts = null;
		this.gibKlugheit().setze(klugheit);
		Ergebnis ergebnis = null;

		do {
			this.gibProtokoll().reset();
			ergebnis = this.gibKnacker().loese(VersuchsEbenen.UNBEGRENZT, "sichereLineareLoesung", false);
			if (ergebnis.gibArt() != Ergebnis.Art.FERTIG) {
				this.gibProtokoll().gehe(Schrittweite.ALLES, false);
				if (istSystemOut() & istSystemOutAlles()) {
					System.out.println("sichereLineareLoesung(): Keine Lösung durch Knacker !!!");
				}
				return null;
			}

			nVersuchStarts.start();
			int ebeneNr = this.gibFeldmatrix().ebeneGibNummer();
			if (ebeneNr > 1) {
				// Die Versuch-Starts wieder als Vorgaben setzen, so dass keine Versuche mehr nötig sind
				versuchStarts = this.gibFeldmatrix().gibFelderVersuchStart();

				nVersuchStarts.inc(versuchStarts.size());
				this.gibProtokoll().gehe(Schrittweite.ALLES, false);
				for (FeldNummer feldNummer : versuchStarts) {
					FeldInfo vorgabe = loesung.get(feldNummer);
					this.gibFeldmatrix().setzeVorgabe(feldNummer, vorgabe.gibVorgabe());

					if (symmetrierer != null) {
						FeldNummer feldNummerSymmetrisch = symmetrierer.gibFeldNummer(feldNummer,
								FeldMatrix.feldNummerMax);
						if (!this.gibFeldmatrix().gibFeldInfo(feldNummerSymmetrisch).istVorgabe()) {
							FeldInfo vorgabe2 = loesung.get(feldNummerSymmetrisch);
							this.gibFeldmatrix().setzeVorgabe(feldNummerSymmetrisch, vorgabe2.gibVorgabe());
						}
					}
				}
			}

			// Kontrolle:
			ergebnis = this.gibKnacker().loese(VersuchsEbenen.KEINE, "sichereLineareLoesung", false);
			this.gibProtokoll().gehe(Schrittweite.ALLES, false);
		} while (ergebnis.gibArt() != Ergebnis.Art.FERTIG);

		if (istSystemOut() & istSystemOutAlles()) {
			System.out.println(String.format("sichereLineareLoesung(): %s nVersuche: %s", ergebnis, nVersuchStarts));
		}
		return versuchStarts;
	}

	/**
	 * Löscht aus den verbliebenen Vorgaben soweit möglich.
	 * Als erstes  wird die Hürde entsprechend NeuTyp.Klugheit eingebaut.
	 * @param tabu Vorgaben, die nicht gelöscht werden dürfen, um die eindeutige Lösung (ohne Versuche) nicht zu verlassen
	 * @param symmetrierer Falls != null soll das Sudoku symmetrisch aussehen 
	 * @return BerichtKnacker Beinhaltet die erreichte Schwierigkeit
	 * @throws Exc 
	 */
	private BerichtKnacker leeren(FeldNummerListe tabu, Animator symmetrierer) throws Exc {
		BerichtKnacker knackerBericht = null;

		if (istSystemOut() & istSystemOutAlles()) {
			System.out.println(String.format("leeren()"));
		}
		// Aus diesen Vorgaben soll gelöscht werden:
		FeldNummerListe basisVorgaben = new FeldNummerListe();
		InfoSudoku startVorgaben = this.gibFeldmatrix().gibVorgaben();
		basisVorgaben.addAll(startVorgaben.keySet());
		basisVorgaben.removeAll(tabu);

		// gewünschte einzusetzende Klugheit:
		Schwierigkeit wieSchwerSoll = neuTyp.gibWieSchwer();
		Klugheit klugheitNeu = Schwierigkeit.gibKlugheit(wieSchwerSoll);

		// Liste zum munteren Rauslöschen
		FeldNummerListe testVorgaben = new FeldNummerListe();
		testVorgaben.addAll(basisVorgaben);
		while (!testVorgaben.isEmpty()) {
			// Vorgabe zufällig auswählen + löschen
			FeldNummerListe loeschFelder = loesche1Vorgabe(this.gibFeldmatrix(), testVorgaben, symmetrierer);
			// Diese Vorgabe ist danach getestet
			testVorgaben.removeAll(loeschFelder);

			// Ist das Sudoku lösbar mit neuTyp-Klugheit ohne diese Vorgabe?
			this.gibKlugheit().setze(klugheitNeu);
			this.gibProtokoll().reset();
			Ergebnis ergebnis = this.gibKnacker().loese(VersuchsEbenen.KEINE, "leeren", false);
			this.gibProtokoll().gehe(Schrittweite.ALLES, false);
			if (ergebnis.gibArt() == Ergebnis.Art.FERTIG) {
				knackerBericht = this.gibKnacker().gibBericht();
			} else {
				// geht nicht: wieder reinsetzen in das Sudoku
				for (FeldNummer loeschFeld : loeschFelder) {
					FeldInfo vorgabe = startVorgaben.get(loeschFeld);
					this.gibFeldmatrix().setzeVorgabe(loeschFeld, vorgabe.gibVorgabe());
				}
			} // if
		} // while
		return knackerBericht;
	}

	/**
	 * @param symmetrierer Falls != null soll das Sudoku symmetrisch aussehen 
	 * @return Neues Sudoku des angeforderten this.neuTyp
	 * Schlimmstenfalls null
	 */
	private GeneratorErgebnis gibNeues(Animator symmetrierer) {
		InfoSudoku neuesSudoku = null;
		int loesungsZeit = 0;
		NeuTyp typDesNeuenSudoku = this.neuTyp;
		int laufNummer = 0;

		try {
			// gewünschte einzusetzende Klugheit wird nicht jedesmal zufällig erreicht
			int laufNummerMax = 50;
			for (laufNummer = 1; laufNummer < laufNummerMax; laufNummer++) {
				if (this.neuTyp.gibTyp() == NeuTyp.Typ.LEER) {
					neuesSudoku = new InfoSudoku();
					// Hier ist die Lösungszeit nicht relevant.
					break; // =========================>
				}

				this.gibFeldmatrix().reset();
				// Das Protokoll muss allerdings nach der wilden Löscherei der Vorgaben..
				// vor dessen Nutzung nochmals rückgesetzt werden!
				this.gibProtokoll().reset();
				FeldNummerListe alle = this.gibFeldmatrix().gibAlleFeldNummern();

				// 1. vollständig gefülltes Sudoku erstellen
				boolean istGefuellt = super.fuelleSudoku(alle);
				if (istGefuellt) {
					InfoSudoku loesung = this.gibFeldmatrix().gibVorgaben();
					if (this.neuTyp.gibTyp() == NeuTyp.Typ.VOLL) {
						neuesSudoku = loesung;
						// Hier ist die Lösungszeit nicht relevant.
						break; // =========================>
					}

					// 2. Aus dem vollständig mit Vorgaben gefüllten Sudoku Vorgaben entfernen
					// 2. a) großer Schwung bis die Eindeutigkeit infrage gestellt ist
					loesche(this.gibFeldmatrix(), alle, 40, symmetrierer);

					// 2. b) Falls keine lineare Lösung mehr existiert:
					// Die EbenenStarts des Knacker als Vorgaben setzen.
					// Diese Vorgaben sind jetzt schon mal tabu für das Löschen!
					Schwierigkeit wieSchwerSoll = neuTyp.gibWieSchwer();
					FeldNummerListe tabu = null;
					{
						Klugheit klugheit = new Klugheit(true);
						tabu = sichereLineareLoesung(loesung, klugheit, symmetrierer);
						if (tabu != null) {
							klugheit = Schwierigkeit.gibKlugheit(wieSchwerSoll);
							FeldNummerListe tabu2 = sichereLineareLoesung(loesung, klugheit, symmetrierer);
							if (tabu2 != null) {
								tabu.addAll(tabu2);
							}
						}
					}

					// Wenn noch eine sichere Lösung existiert:
					if (tabu != null) {
						// 2. c) Möglichst weit leeren von Vorgaben: Einzelne Felder löschen
						BerichtKnacker knackerBericht = leeren(tabu, symmetrierer);

						// 3. Schwierigkeit dieses Sudoku ermitteln
						SudokuSchwierigkeit wieSchwerIst = Analysator.gibSchwierigkeit(this.gibFeldmatrix()
								.gibVorgaben(), knackerBericht);
						Schwierigkeit wieSchwerIstTyp = wieSchwerIst.gibKlareWieSchwer();

						// MItschreiben (Speichern) erfolgreicher Logiken
						LogikAnzahlen erfolgreicheLogiken = wieSchwerIst.gibKlareErfolgreicheLogiken();
						Generator.erfolgreicheLogiken.add(erfolgreicheLogiken);

						// Sudoku vermerken
						boolean sudokuNotieren = true;
						if (neuesSudoku != null) {
							sudokuNotieren = (wieSchwerIstTyp.ordinal() > typDesNeuenSudoku.gibWieSchwer().ordinal());
						}
						if (sudokuNotieren) {
							neuesSudoku = this.gibFeldmatrix().gibVorgaben();
							loesungsZeit = wieSchwerIst.gibZeit();
							typDesNeuenSudoku = new NeuTyp(wieSchwerIstTyp);

							// 3. Wurde die geforderte Schwierigkei erreicht?
							if (wieSchwerSoll.equals(wieSchwerIstTyp)) {
								if (istSystemOut()) {
									System.out.println(String.format(
											"Generator.gibNeues(): Soll=%s Geklappt (Ist=%s)in Lauf=%d (Logiken %s)",
											neuTyp, wieSchwerIstTyp, laufNummer, erfolgreicheLogiken));
								}
								// Das Ziel ist erreicht!
								break; // for (int iLauf
							}
						} // if (sudokuNotieren){
					} // if (tabu != null){
				} // if (istGefuellt){
			} // for (int iLauf

		} catch (Exc e) {
			e.printStackTrace();
			if (istSystemOut()) {
				System.out.println(String.format("Generator.gibNeues(): Ende durch Ausnahme %s", e.getMessage()));
			}
		}

		if (istSystemOut()) {
			String erfolg = new String("mit Erfolg");
			if (!this.neuTyp.equals(typDesNeuenSudoku)) {
				erfolg += " aber nur mit Schwierigkeit " + Schwierigkeit.gibName(typDesNeuenSudoku.gibWieSchwer());
			}
			if (neuesSudoku == null) {
				erfolg = new String("------------------- erfolglos");
			}
			System.out.println(String.format("%s.gibNeuesSudoku() %s: Ende %s", Generator.class.getName(), neuTyp,
					erfolg));
		}

		GeneratorErgebnis ergebnis = new GeneratorErgebnis(neuesSudoku, typDesNeuenSudoku, loesungsZeit, laufNummer);
		return ergebnis;
	}

}
