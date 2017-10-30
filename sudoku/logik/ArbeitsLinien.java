package logik;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kern.feldmatrix.Feld;
import kern.feldmatrix.FeldListe;
import kern.feldmatrix.FeldNummer;

/**
 * @author heroe
 * Die Klasse beinhaltet die Felder entweder von Zeilen oder von Spalten
 */
class ArbeitsLinien {

	/**
	 * @author heroe
	 * Die Klasse beinhaltet die Felder entweder einer Zeile oder einer Spalte
	 */
	class Linie {
		/**
		 * Die Gruppe, zu denen die Felder gehören: Hier können die Gruppen-Infos abgefragt werden.
		 */
		Gruppe gruppe;
		/**
		 * Alle oder eine Auswahl der Felder der Gruppe, auch null.
		 */
		FeldListe arbeitsFelder;

		Linie(Gruppe gruppe) {
			this.gruppe = gruppe;
			arbeitsFelder = null;
		}

		/**
		 * Setzt die Felder der Gruppe, die speziell für diese Linie greifbar sein sollen.
		 * @param feldListe
		 */
		void setzeFelder(FeldListe feldListe) {
			this.arbeitsFelder = feldListe;
		}

		/**
		 * @param feldNummer
		 * @return null oder Felder ohne das mit der genannten Feldnummer.
		 */
		FeldListe gibFelderOhne(FeldNummer feldNummer) {
			FeldListe returnLinie = new FeldListe();
			for (int i = 0; i < arbeitsFelder.size(); i++) {
				Feld feld = arbeitsFelder.get(i);
				if (!feldNummer.equals(feld.gibFeldNummer())) {
					returnLinie.add(feld);
				}
			}
			return returnLinie;
		}
	}

	// ===============================================================
	/**
	 * true wenn die Linien Zeilen sind, false wenn die Linien Spalten sind.
	 */
	private final boolean istZeilen;

	/**
	 * Die Zuordnung <Zeilen- bzw. Spalten-Nr. zu Zeile bzw. Spalte>
	 */
	private final Map<Integer, Linie> linien;

	/**
	 * @param linien sind weder null noch leer. 
	 * @param istZeilen Die Linien sind entweder Zeilen oder Spalten.
	 */
	ArbeitsLinien(ArrayList<Gruppe> gruppen, boolean istZeilen) {
		this.istZeilen = istZeilen;
		this.linien = new HashMap<Integer, Linie>();
		for (int i = 0; i < gruppen.size(); i++) {
			Gruppe gruppe = gruppen.get(i);
			if (!gruppe.isEmpty()) {
				FeldNummer feldNummer = gruppe.get(0).gibFeldNummer();
				Integer key = feldNummer.gibZeile();
				if (!istZeilen) {
					key = feldNummer.gibSpalte();
				}
				Linie linie = new Linie(gruppe);
				this.linien.put(key, linie);
			}
		}
	}

	Linie gibLinie(int linienNummer) {
		Linie linie = linien.get(linienNummer);
		return linie;

	}

	/**
	 * @param feldNummer Bei istZeilen=true wird die Zeile von Feldnummer benutzt, sonst die Spalte.
	 * @return null oder die Felder der Linie, die durch feldnummer benannt ist.
	 * 					Das Feld der feldnummer selbst befindet sich nicht in der Rückgabe-Feldliste.
	 * 					Die Rückgabe-Feldliste kann leer sein .
	 */
	Linie gibLinie(FeldNummer feldNummer) {
		Integer key = null;
		if (istZeilen) {
			key = feldNummer.gibZeile();
		} else {
			key = feldNummer.gibSpalte();
		}
		Linie linie = linien.get(key);
		return linie;
	}

	boolean istZeilen() {
		return this.istZeilen;
	}
}
