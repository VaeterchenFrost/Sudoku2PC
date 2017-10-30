package winapp.feld;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import sudoku.kern.feldmatrix.FeldNummer;

public class FeldMaler {
	/**
	 * @param g
	 * @param dimension
	 * @param maxFontSize wenn != 0 wird die Textgröße hierauf beschränkt
	 * @return
	 */
	private static float gibFontSize(Graphics g, Dimension dimension, int maxFontSize) {
		if (dimension.height == 0) {
			return 12;
		}

		Font font = g.getFont();
		// großer Font zum rechnen:
		float fontSizeTest = 100;
		Font fTest = font.deriveFont(fontSizeTest);
		FontMetrics fm = g.getFontMetrics(fTest);
		float hoeheZahlTest = fm.getAscent();
		// Wie oft passt die Zahl dieses Fonts in hoeheSoll?
		float hoeheSoll = dimension.height;
		float n = hoeheSoll / hoeheZahlTest;

		float fontSize = fontSizeTest * n;
		if ((fontSize > maxFontSize) & (maxFontSize > 0)) {
			fontSize = maxFontSize;
		}
		return fontSize;
	}

	private static void setzeFontSize(Graphics g, Dimension dimension, int maxFontSize) {
		Font font = g.getFont();
		float fontSize = gibFontSize(g, dimension, maxFontSize);

		g.setFont(font.deriveFont(fontSize));
	}

	public static void maleZahl(Graphics g, Rectangle r, Color cZahl, int zahl) {
		String sZahl = String.valueOf(zahl);
		g.setColor(cZahl);

		FeldMaler.setzeFontSize(g, r.getSize(), 0);
		FontMetrics fm = g.getFontMetrics();
		int sZahlBreite = fm.charWidth(sZahl.charAt(0));
		int x = r.x + (r.width - sZahlBreite) / 2;
		int y = r.y + r.height - ((fm.getDescent() + fm.getLeading()) / 2);

		g.drawString(sZahl, x, y);
	}

	public static void maleZahlAlsSchleier(Graphics g, Rectangle r, Color cZahl, int zahl, float schleierStaerke) {
		Verschleierer verschleierer = new Verschleierer(g, schleierStaerke);
		maleZahl(g, r, cZahl, zahl);
		verschleierer.reset();
	}

	public static void maleRechteck(Graphics g, Rectangle r, Color color) {
		g.setColor(color);
		g.fillRect(r.x, r.y, r.width, r.height);
	}

	public static void maleOval(Graphics g, Rectangle r, Color color) {
		g.setColor(Color.BLACK);
		g.fillOval(r.x, r.y, r.width, r.height);
		g.setColor(color);
		int umRandung = r.width / 20;
		if (umRandung < 2) {
			umRandung = 2;
		}
		g.fillOval(r.x + umRandung, r.y + umRandung, r.width - 2 * umRandung, r.height - 2 * umRandung);
	}

	/**
	 * @param g
	 * @param r x,y=Ecke links oben, width,height=Weiten nach rechtsunten
	 * @param cHintergrund
	 * @param cZahl
	 * @param zahl
	 */
	public static void maleZahlAufRechteck(Graphics g, Rectangle r, Color cHintergrund, Color cZahl, int zahl) {
		maleRechteck(g, r, cHintergrund);
		maleZahl(g, r, cZahl, zahl);
	}

	public static void maleZahlAufOval(Graphics g, Rectangle r, Color cHintergrund, Color cZahl, int zahl) {
		maleOval(g, r, cHintergrund);
		maleZahl(g, r, cZahl, zahl);
	}

	/**
	 * @param g
	 * @param r
	 * @param cHintergrund
	 * @param cText
	 * @param text
	 * @param maxFontSize Wenn > 0 wird die Textgröße hierauf beschränkt. 
	 * 			(Es wird ansonsten der Text so groß wie r hergibt gemalt.)
	 */
	public static void maleText(Graphics g, Rectangle r, Color cHintergrund, Color cText, String text1, String text2,
			int maxFontSize) {
		g.setColor(cHintergrund);
		g.fillRect(r.x, r.y, r.width, r.height);

		g.setColor(cText);

		FeldMaler.setzeFontSize(g, r.getSize(), maxFontSize);
		Font font = g.getFont();
		g.setFont(font.deriveFont(Font.ITALIC));

		FontMetrics fm = g.getFontMetrics();
		int y = r.height - fm.getDescent(); // ( (fm.getDescent() + fm.getLeading()) / 2);

		if (text2 == null) {
			// Horizontal zentrierter Text
			int x = 0;
			int sTextBreite = fm.stringWidth(text1);
			if (r.width > sTextBreite) {
				x = (r.width - sTextBreite) / 2;
			}
			g.drawString(text1, x, y);
		} else {
			// text1 links und text2 rechts
			g.drawString(text1, 0, y);
			int posText2 = r.width - fm.stringWidth(text2);
			g.drawString(text2, posText2, y);
		}
	}

	/**
	 * @param g
	 * @param r
	 * @param cHintergrund
	 * @param cText
	 * @param text
	 * @param maxFontSize Wenn > 0 wird die Textgröße hierauf beschränkt. 
	 * 			(Es wird ansonsten der Text so groß wie r hergibt gemalt.)
	 */
	public static void maleText(Graphics g, Rectangle r, Color cHintergrund, Color cText, String text1, int maxFontSize) {
		maleText(g, r, cHintergrund, cText, text1, null, maxFontSize);
	}

	/**
	 * Gibt die Position des Platzes im Rechteck mit der Ausdehnung dimension.
	 * @param platz fortlaufend ab 1
	 * @param laengePlatz Breite und Höhe eines Platzes
	 * @param nSpalten Anzahl der Darstellungs-Spalten
	 * @param nZeilen der Darstellungs-Zeilen
	 * @param dFeld Gesamt-Breite und -Höhe
	 * @return Position in derselben Einheit, die laenge hat (Pixel)
	 */
	private static Point gibPos(int platz, int laengePlatz, int nSpalten, int nZeilen, Dimension dFeld) {
		int iPlatz = platz - 1;

		int iZeile = iPlatz / nSpalten;
		int iSpalte = iPlatz - iZeile * nSpalten;
		int x = laengePlatz * iSpalte;
		int y = laengePlatz * iZeile;

		if (nZeilen < nSpalten) {
			y += laengePlatz / 2;
		}
		Point p = new Point(x, y);
		return p;

	}

	/**
	 * @param rFeld
	 * @param nZahlen
	 * @return Die Rechtecke für die Darstellung von nZahlen im Rechteck rectangle
	 * bezogen auf die linke obere Ecke mit den Koordinaten (0,0).
	 */
	public static ArrayList<Rectangle> gibZahlenPlaetze(Rectangle rFeld, int nZahlen) {
		FeldNummer feldNummer = null;
		switch (nZahlen) {
		case 1:
			feldNummer = new FeldNummer(1, 1);
			break;
		case 2:
			feldNummer = new FeldNummer(2, 1);
			break;
		case 3:
			feldNummer = new FeldNummer(2, 2);
			break;
		case 4:
			feldNummer = new FeldNummer(2, 2);
			break;
		case 5:
			feldNummer = new FeldNummer(3, 2);
			break;
		case 6:
			feldNummer = new FeldNummer(3, 2);
			break;
		case 7:
			feldNummer = new FeldNummer(3, 3);
			break;
		case 8:
			feldNummer = new FeldNummer(3, 3);
			break;
		default:
			feldNummer = new FeldNummer(3, 3);
			break;
		} // switch (nZahlen)

		ArrayList<Rectangle> plaetze = new ArrayList<>();
		int hoehe = rFeld.height / (feldNummer.spalte);
		int breite = hoehe;
		Dimension platzDimension = new Dimension(breite, hoehe);

		for (int platz = 1; platz <= nZahlen; platz++) {
			Point platzPoint = gibPos(platz, hoehe, feldNummer.spalte, feldNummer.zeile, rFeld.getSize());
			platzPoint.translate(rFeld.x, rFeld.y);
			Rectangle platzRechteck = new Rectangle(platzPoint, platzDimension);
			plaetze.add(platzRechteck);
		}
		return plaetze;
	}

	public static void maleSchleier(Graphics g, Rectangle r, Color color) {
		Verschleierer verschleierer = new Verschleierer(g, FeldFarbe.schleierStaerke);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(color);
		g2d.fill(r);
		verschleierer.reset();
	}
}
