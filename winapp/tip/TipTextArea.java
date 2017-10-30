package winapp.tip;

import java.awt.Font;

import javax.swing.JTextArea;

@SuppressWarnings("serial")
class TipTextArea extends JTextArea {
	private static int startFontGroesse = 16;
	private static int fontGroesseMin = 6;
	private static int fontGroesse = 0;

	// -----------------------------------------------------------------------
	TipTextArea(String text, int anzahlTextZeilen) {
		super(text);
		if (anzahlTextZeilen > 0) {
			setRows(anzahlTextZeilen);
			setColumns(3 * anzahlTextZeilen);
		}
		// setLineWrap(true);
		// setWrapStyleWord(true);
		setEditable(false);
		if (fontGroesse == 0) {
			fontGroesse = startFontGroesse;
		}
		setzeFontGroesse();
	}

	void setzeFontGroesse() {
		Font font = getFont();
		if (font == null) {
			return;
		}
		Font fNeu = font.deriveFont((float) fontGroesse);
		setFont(fNeu);
	}

	boolean istZoomTextMoeglich(int richtung, int kontrollBreite) {
		if (richtung < 0) {
			return fontGroesse > fontGroesseMin;
		} else { // vergrößern
			return this.getWidth() < kontrollBreite;
		}
	}

	void zoomText(int delta) {
		fontGroesse += delta;
		setzeFontGroesse();
		// System.out.println(String.format("TipTextArea0.zoomText() neue Fontgröße=%d", fontGroesse));
	}
}
