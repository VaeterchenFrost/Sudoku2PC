package winapp.feld;

public class FeldPosition {
	/**
	 * @param sudoku Position und Länge für das gesamte Sudoku
	 * @param feldNummer ab oben links ab1 bis 9
	 * @return Mal-Position und Mallänge für das Feld
	 */
	public static PosUndLaenge gibPosUndLaenge(PosUndLaenge sudoku, int feldNummer) {
		int feldAnzahl = 9;

		// Die Längen der Rahmenelemente im Verhältnis zueinander
		int lKlein0 = 1;
		int lGross0 = 2;
		int lRand0 = 2;
		int lRahmen0 = 2 * lRand0 + 2 * lGross0 + 6 * lKlein0;

		// 9.4 Einheiten: 9*Feld und 1*Rahmenelemente
		int lFeld = (int) (sudoku.laenge / (feldAnzahl + 0.4));
		// Der Rahmen ist der Rest, den die Felder übrig lassen
		int lRahmen = sudoku.laenge - feldAnzahl * lFeld;

		if (lRahmen < lRahmen0) {
			// Nicht genug Rahmenpixel: Jedem Feld ein Pixel klauen
			lFeld--;
			lRahmen += feldAnzahl;
		}

		int lRahmeneinheit = lRahmen / lRahmen0;
		int lRest = lRahmen % lRahmen0;
		int lRest2 = lRest / 4; // Es gibt 2*großen Abstand und die 2* Rand: aufteilen auf sie
		int lRest3 = lRest % 4; // wird auf die Ränder aufgeteilt
		lKlein0 *= lRahmeneinheit;
		lGross0 = lRahmeneinheit * lGross0 + lRest2;
		lRand0 = lRahmeneinheit * lRand0 + lRest2 + lRest3 / 2;

		// Rechts von jedem Feld wird ein Abstand lKein gerechnet, deshalb:
		lGross0 -= lKlein0;

		int pos = sudoku.pos + lRand0 + (feldNummer - 1) * lKlein0 + ((feldNummer - 1) / 3) * lGross0
				+ (feldNummer - 1) * lFeld;

		return new PosUndLaenge(pos, lFeld);
	}

}
