package kern.feldmatrix;

import java.util.ArrayList;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@SuppressWarnings("serial")
public class FeldNummerListe extends ArrayList<FeldNummer> {

	public FeldNummerListe() {
	}

	public FeldNummerListe(FeldNummerListe src) {
		for (int i = 0; i < src.size(); i++) {
			FeldNummer feldNummer = new FeldNummer(src.get(i));
			this.add(feldNummer);
		}
	}

	public FeldNummerListe(FeldListe felder) {
		for (int i = 0; i < felder.size(); i++) {
			Feld feld = felder.get(i);
			FeldNummer iFeld = feld.gibFeldNummer();
			this.add(new FeldNummer(iFeld));
		}
	}

	public FeldNummerListe(ZahlenListe zahlenFelder) {
		for (int i = 0; i < zahlenFelder.size(); i++) {
			FeldNummerMitZahl feld = zahlenFelder.get(i);
			FeldNummer iFeld = feld.gibFeldNummer();
			this.add(new FeldNummer(iFeld));
		}
	}

	/**
	 * @param felder Alle diese Feldnummern füge ich zu mir hinzu.
	 */
	public void add(FeldNummerListe felder) {
		this.addAll(felder);
	}

	/**
	 * @param verbindung Mit diesem Text werden die einzelnen Nummern miteinander verbunden
	 * @return
	 */
	public String gibKette(String verbindung) {
		String s = new String("");

		for (int iNummer = 0; iNummer < this.size(); iNummer++) {
			if (s.length() > 0) {
				s += verbindung;
			}
			s += this.get(iNummer);
		}

		return s;
	}

	@Override
	public String toString() {
		String s = new String("[");
		for (FeldNummer feldNummer : this) {
			s += " " + feldNummer;
		}
		s += "]";
		return s;
	}

	/**
	 * @return Eine Zeilen-Nummer wenn alle Felder in derselben Zeile liegen, sonst 0
	 */
	public int gibZeile() {
		if (this.size() == 0) {
			return 0;
		}
		int zeile = this.get(0).gibZeile();
		for (int i = 1; i < this.size(); i++) {
			if (this.get(i).gibZeile() != zeile) {
				return 0;
			}
		}
		return zeile;
	}

	/**
	 * @return Eine Spalten-Nummer zurück wenn alle Felder in derselben Spalte liegen, sonst 0
	 */
	public int gibSpalte() {
		if (this.size() == 0) {
			return 0;
		}
		int spalte = this.get(0).gibSpalte();
		for (int i = 1; i < this.size(); i++) {
			if (this.get(i).gibSpalte() != spalte) {
				return 0;
			}
		}
		return spalte;
	}

	/**
	 * @return null wenn die Felder nicht auf einer Spalte bzw. Zeile liegen
	 * 			ansonsten den Namen "Spalte n" bzw. "Zeile n"
	 */
	public String gibLinienName() {
		int spalte = gibSpalte();
		int zeile = gibZeile();

		if (spalte > 0) {
			return String.format("Spalte %d", spalte);
		}
		if (zeile > 0) {
			return String.format("Zeile %d", zeile);
		}
		return null;
	}

	/**
	 * @param andere
	 * @return true wenn diese Liste und die andere gleiche Länge und gleiche Elemente besitzen.
	 * 							Die Elemente dürfen in unterschiedlicher Reihenfolge stehen
	 */
	public boolean istGleicherInhalt(FeldNummerListe andere) {
		if (andere == null) {
			return false;
		}
		if (this.size() != andere.size()) {
			return false;
		}
		for (FeldNummer andereFeldNummer : andere) {
			if (!this.contains(andereFeldNummer)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Differenzmenge:
	 * @param andere
	 * @return Alle (meine) FeldNummern, die nicht in andere genannt sind. 
	 */
	FeldNummerListe differenz(FeldNummerListe andere) {
		FeldNummerListe ergebnis = new FeldNummerListe();
		for (FeldNummer feldNummer : this) {
			if (!andere.contains(feldNummer)) {
				ergebnis.add(feldNummer);
			}
		}
		return ergebnis;
	}

	/**
	 * @param andere
	 * @return Alle FeldNummern, die sowohl in mir als auch in andere (gleichzeitig) sind. 
	 */
	FeldNummerListe schnittMenge(FeldNummerListe andere) {
		throw new NotImplementedException();
	}
}
