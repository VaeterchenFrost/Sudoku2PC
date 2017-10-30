package winapp.feld;

import java.awt.Color;

public class FeldFarbe {
	// Hintergrund (Rahmen) des Sudoku
	private static final Color sudokuRahmen = Color.DARK_GRAY; // .brighter(); //new Color(100,100,100);

	// Stärke des Schleiers, der zur passiven Feld-Markierung benutzt wird:
	// 0.0 (keine Verschleierung) bis 1.0 (Schleier verdeckt den FeldInhalt ganz)
	static final float schleierStaerke = 0.5f;

	// Mögliche
	// Mögliche-Zahl
	static final Color moegliche = Color.LIGHT_GRAY.darker(); // BLACK;
	static final Color moeglichMarkiertZahl = Color.WHITE;
	static final Color moeglichMarkiertHintergrund = Color.BLACK;

	// Vorgabe
	static final Color vorgabe = Color.DARK_GRAY;
	// public static final Color vorgabeHintergrund = new Color(235,235,235);
	static final Color vorgabeHintergrund = Color.LIGHT_GRAY;

	// Hintergründe
	static final Color hintergrund = Color.WHITE;
	static final Color hintergrundProblem = Color.RED;
	static final Color hintergrundMarkierung = new Color(255, 255, 200);
	static final Color hintergrundPaar = new Color(220, 220, 255);

	static final Color rahmen = new Color(0, 150, 150);

	// Tip
	static final Color tip = new Color(255, 255, 180);

	// Eintrag auf einer der Ebenen
	static final Color eintrag = new Color(55, 55, 255);
	private static final Color eintragVersuch1 = new Color(255, 0, 0);
	private static final Color eintragVersuchN = new Color(255, 140, 140);
	static final Color ebeneVersuchN = Color.DARK_GRAY;

	public static Color gibHintergrund(int ebene, boolean istEbenenStart) {
		if (!istEbenenStart) {
			return hintergrund;
		}
		return gibVordergrund(ebene);
	}

	public static Color gibVordergrund(int ebene, boolean istEbenenStart) {
		if (istEbenenStart) {
			return hintergrund;
		}
		return gibVordergrund(ebene);
	}

	public static Color gibVersuchN() {
		return eintragVersuchN;
	}

	private static Color gibVordergrund(int ebene) {
		switch (ebene) {
		case 0: // Was ist jetzt los ?
			return hintergrund;
		case 1:
			return eintrag;
		case 2:
			return eintragVersuch1;
		case 3:
		default:
			return eintragVersuchN;
		}
	}

	public static Color gibSudokuRahmen() {
		return sudokuRahmen;
	}

	// public static Color gibInvers(Color color){
	// return new Color(255-color.getRed(), 255-color.getGreen(), 255-color.getBlue());
	// }
}
