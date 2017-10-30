package winapp.druckinfo.extras;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import sudoku.kern.feldmatrix.FeldNummer;
import sudoku.kern.feldmatrix.FeldNummerListe;
import sudoku.kern.info.FeldInfo;
import sudoku.kern.info.InfoSudoku;
import winapp.druckinfo.MalerEinfach;
import winapp.druckinfo.extras.Gabe.Figur;
import winapp.feld.FeldMaler;

/**
 * @author heroe
 * Malt ein Sudoku mit einer bekannten Figur speziell, sonst wie MalerEinfach. 
 */
public class MalerGeschenk extends MalerEinfach {
	private static int bandLaengeMin = 12;
	/**
	 * Das ist das Sudoku, das aktuell gemalt wird.
	 */
	private InfoSudoku infoSudoku;
	private Figur figur;
	private Figur[] figuren;

	public MalerGeschenk(Figur[] figuren) {
		super();
		this.infoSudoku = null;
		this.figur = null;
		this.figuren = figuren;
	}

	private boolean istFreiesFeldInDerFigur(FeldNummer feldNummer, boolean mitNachbarn) {
		if (feldNummer.gibSpalte() == 1)
			return false;
		if (feldNummer.gibSpalte() == 9)
			return false;
		if (feldNummer.gibZeile() == 1)
			return false;
		if (feldNummer.gibZeile() == 9)
			return false;

		FeldNummerListe zeileFelder = new FeldNummerListe();
		// Zeile nach links
		for (int spalte = feldNummer.gibSpalte(); spalte > 0; spalte--) {
			FeldNummer suchFeld = new FeldNummer(spalte, feldNummer.gibZeile());
			FeldInfo feldInfo = infoSudoku.get(suchFeld);
			if (feldInfo.istVorgabe()) {
				break; // bin drin
			}
			if (spalte == 1) {
				// Der Rand ist erreicht: Also ist definitiv außerhalb der
				// Figur.
				return false;
			}
			zeileFelder.add(suchFeld);
		}
		// Zeile nach rechts
		for (int spalte = feldNummer.gibSpalte(); spalte <= 9; spalte++) {
			FeldNummer suchFeld = new FeldNummer(spalte, feldNummer.gibZeile());
			FeldInfo feldInfo = infoSudoku.get(suchFeld);
			if (feldInfo.istVorgabe()) {
				break; // bin drin
			}
			if (spalte == 9) {
				// Der Rand ist erreicht: Also ist definitiv außerhalb der
				// Figur.
				return false;
			}
			zeileFelder.add(suchFeld);
		}

		FeldNummerListe spaltenFelder = new FeldNummerListe();
		// Spalte nach oben
		for (int zeile = feldNummer.gibZeile(); zeile > 0; zeile--) {
			FeldNummer suchFeld = new FeldNummer(feldNummer.gibSpalte(), zeile);
			FeldInfo feldInfo = infoSudoku.get(suchFeld);
			if (feldInfo.istVorgabe()) {
				break; // bin drin
			}
			if (zeile == 1) {
				// Der Rand ist erreicht: Also ist definitiv außerhalb der
				// Figur.
				return false;
			}
			spaltenFelder.add(suchFeld);
		}
		// Spalte nach unten
		for (int zeile = feldNummer.gibZeile(); zeile <= 9; zeile++) {
			FeldNummer suchFeld = new FeldNummer(feldNummer.gibSpalte(), zeile);
			FeldInfo feldInfo = infoSudoku.get(suchFeld);
			if (feldInfo.istVorgabe()) {
				break; // bin drin
			}
			if (zeile == 9) {
				// Der Rand ist erreicht: Also ist definitiv außerhalb der
				// Figur.
				return false;
			}
			spaltenFelder.add(suchFeld);
		}

		// Freie Nachbarfelder testen
		if (mitNachbarn) {
			for (FeldNummer feldNummerSuche : zeileFelder) {
				if (!istFreiesFeldInDerFigur(feldNummerSuche, false)) {
					return false;
				}
			}
			for (FeldNummer feldNummerSuche : spaltenFelder) {
				if (!istFreiesFeldInDerFigur(feldNummerSuche, false)) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean istFreiesFeldInDerFigur(FeldNummer feldNummer) {
		return istFreiesFeldInDerFigur(feldNummer, true);
	}

	/**
	 * Malt das Feld: Entweder Vorgabe oder freies Feld im VorgabenRechteck oder freies Feld
	 * @param g
	 * @param r
	 * @param feldInfo
	 * @param cZahlHintergrund
	 * @param cZahl
	 * @param cFreiInnen
	 * @param cFrei
	 */
	private void maleFlaeche(Graphics g, Rectangle r, FeldInfo feldInfo, Color cZahlHintergrund, Color cZahl,
			Color cFreiInnen, Color cFrei) {
		if (feldInfo.istVorgabe()) {
			FeldMaler.maleRechteck(g, r, cZahlHintergrund);
			FeldMaler.maleZahl(g, r, cZahl, feldInfo.gibVorgabe());
			return;
		}
		// Freies Feld: innen in der Figur oder außen
		boolean istFigur = istFreiesFeldInDerFigur(feldInfo.gibFeldNummer());
		if (istFigur) {
			FeldMaler.maleRechteck(g, r, cFreiInnen);
		} else {
			FeldMaler.maleRechteck(g, r, cFrei);
		}
	}

	/**
	 * Malt das Feld: Entweder Vorgabe oder freies Feld
	 * @param g
	 * @param r
	 * @param feldInfo
	 * @param cZahlHintergrund
	 * @param cZahl
	 * @param cFrei
	 */
	private void faerbeVorgaben(Graphics g, Rectangle r, FeldInfo feldInfo, Color cZahlHintergrund, Color cZahl,
			Color cFrei) {
		if (feldInfo.istVorgabe()) {
			FeldMaler.maleRechteck(g, r, cZahlHintergrund);
			FeldMaler.maleZahl(g, r, cZahl, feldInfo.gibVorgabe());
			return;
		}
		// Freies Feld:
		FeldMaler.maleRechteck(g, r, cFrei);
	}

	/**
	 * Malt das Feld: Entweder Vorgabe innerhalb eines Vorgabenbandes oder einfach Vorgabe oder freies Feld
	 * @param g
	 * @param r
	 * @param feldInfo
	 * @param cBandHintergrund
	 * @param cBandZahl
	 * @param cAussenZahlHintergrund
	 * @param cAussenZahl
	 * @param cFrei
	 */
	private void faerbeVorgabenBand(Graphics g, Rectangle r, FeldInfo feldInfo, Color cBandHintergrund, Color cBandZahl,
			Color cAussenZahlHintergrund, Color cAussenZahl, Color cFrei) {

		if (istVorgabenBand(feldInfo.gibFeldNummer())) {
			FeldMaler.maleRechteck(g, r, cBandHintergrund);
			FeldMaler.maleZahl(g, r, cBandZahl, feldInfo.gibVorgabe());
		} else { // kein Band
			if (feldInfo.istVorgabe()) {
				FeldMaler.maleRechteck(g, r, cAussenZahlHintergrund);
				FeldMaler.maleZahl(g, r, cAussenZahl, feldInfo.gibVorgabe());
			} else {
				// Freies Feld:
				FeldMaler.maleRechteck(g, r, cFrei);
			}
		}
	}

	/**
	 * @param r Rechteck: x,y ist links oben. 
	 * 					Rechte Spalte des Rechtecks ist r.x + r.width -1
	 * 					Untere Zeile des Rechtecks ist r.y + r.height -1
	 * @param feldNummer
	 * @return true wenn feldNummer auf das Rechteck zeigt
	 */
	private boolean feldIn(Rectangle r, FeldNummer feldNummer) {
		if (feldNummer.spalte < r.x) {
			return false;
		}
		if (feldNummer.spalte > (r.x + r.width - 1)) {
			return false;
		}
		if (feldNummer.zeile < r.y) {
			return false;
		}
		if (feldNummer.zeile > (r.y + r.height - 1)) {
			return false;
		}
		return true;
	}

	private boolean istGueltig(int feldNummerZahl) {
		return ((feldNummerZahl > 0) & (feldNummerZahl < 10));
	}

	private boolean istVorgabenBand(FeldNummerListe band, FeldNummerListe bandEnden) {
		FeldNummerListe nachbarn = new FeldNummerListe();

		for (FeldNummer feldNummer : bandEnden) {
			for (int dZeile = -1; dZeile < 2; dZeile++) {
				int zeile = feldNummer.gibZeile() + dZeile;
				if (istGueltig(zeile)) {
					for (int dSpalte = -1; dSpalte < 2; dSpalte++) {
						if (!((dZeile == 0) & (dSpalte == 0))) {
							int spalte = feldNummer.gibSpalte() + dSpalte;
							if (istGueltig(spalte)) {
								FeldNummer suchFeld = new FeldNummer(spalte, zeile);
								FeldInfo feldInfo = infoSudoku.get(suchFeld);
								if (feldInfo.istVorgabe()) {
									if ((!band.contains(suchFeld)) & (!nachbarn.contains(suchFeld))) {
										nachbarn.add(suchFeld);
									}
								}
							}
						}
					}
				} // if (istGueltig(zeile))
			} // for(dZeile
		} // for(FeldNummer feldNummer: bandEnden){
		if (!nachbarn.isEmpty()) {
			band.add(nachbarn);
			if (band.size() >= MalerGeschenk.bandLaengeMin) {
				return true;
			}
			return istVorgabenBand(band, nachbarn);
		}
		return false;
	}

	private boolean istVorgabenBand(FeldNummer feldNummer) {
		FeldInfo feldInfo = infoSudoku.get(feldNummer);
		if (!feldInfo.istVorgabe()) {
			return false;
		}

		FeldNummerListe band = new FeldNummerListe();
		band.add(feldNummer);
		FeldNummerListe bandEnden = new FeldNummerListe();
		bandEnden.add(feldNummer);

		return istVorgabenBand(band, bandEnden);
	}

	@Override
	public void maleFeld(Graphics g, Dimension dSudoku, FeldInfo feldInfo) {
		Rectangle rFeld = gibFeldRechteck(feldInfo.gibFeldNummer(), dSudoku);

		Color cHellesGelb = new Color(255, 255, 60);
		Color cHellesBlau = new Color(220, 220, 255);
		Color cHellesGrau = new Color(220, 220, 220);
		Color cMittleresBlau = new Color(150, 150, 255);
		Color cMittleresRot = new Color(255, 100, 100);
		Color cSattesGruen = new Color(0, 125, 62);
		Color cSattesBraun = new Color(125, 62, 0);
		switch (figur) {
		case HERZ:
			maleFlaeche(g, rFeld, feldInfo, Color.RED.brighter(), Color.WHITE, cMittleresRot, Color.WHITE);
			break;
		case KERZE: {
			Color cHintergrund = Color.WHITE;
			if (feldIn(new Rectangle(4, 1, 3, 4), feldInfo.gibFeldNummer())) {
				// Flamme gelb
				// maleFlaeche(g, r, feldInfo, Color.YELLOW.brighter(), Color.BLUE.brighter(), cHintergrund);
				maleFlaeche(g, rFeld, feldInfo, cHellesGelb, Color.DARK_GRAY, cHellesGelb, cHintergrund);
				break;
			}
			if (feldIn(new Rectangle(3, 5, 5, 5), feldInfo.gibFeldNummer())) {
				// Kerze rot
				maleFlaeche(g, rFeld, feldInfo, Color.RED.brighter(), Color.WHITE, cMittleresRot, cHintergrund);
				break;
			}
			faerbeVorgaben(g, rFeld, feldInfo, cSattesGruen, Color.WHITE, cHintergrund);
		}
			break;
		case SCHNEEFLOCKE:
			// faerbeVorgaben(g, r, feldInfo, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.WHITE);
			faerbeVorgaben(g, rFeld, feldInfo, cHellesGrau, Color.DARK_GRAY, Color.WHITE);
			break;
		case SCHNEEMANN:
			faerbeVorgaben(g, rFeld, feldInfo, Color.DARK_GRAY, Color.WHITE, Color.WHITE);
			break;
		case STERN:
			if (feldIn(new Rectangle(2, 2, 7, 7), feldInfo.gibFeldNummer())) {
				faerbeVorgaben(g, rFeld, feldInfo, Color.RED, Color.WHITE, Color.WHITE);
			} else {
				faerbeVorgaben(g, rFeld, feldInfo, Color.WHITE, Color.DARK_GRAY, Color.WHITE);
			}
			break;
		case ZWEIG:
			faerbeVorgaben(g, rFeld, feldInfo, cSattesGruen, Color.WHITE, Color.WHITE);
			break;
		case GLOCKE:
			// maleFlaeche(g, r, feldInfo, Color.RED.brighter(), Color.WHITE, Color.WHITE);
			if (feldIn(new Rectangle(1, 3, 9, 7), feldInfo.gibFeldNummer())) { // Glocke
				maleFlaeche(g, rFeld, feldInfo, cHellesGelb, Color.DARK_GRAY, cHellesGelb, Color.WHITE);
			} else { // oben
				faerbeVorgaben(g, rFeld, feldInfo, Color.RED, Color.WHITE, Color.WHITE);
			}
			break;
		case MARIA: {
			Color cHintergrund = Color.WHITE;
			if (feldIn(new Rectangle(4, 3, 6, 7), feldInfo.gibFeldNummer())) { // Maria
				maleFlaeche(g, rFeld, feldInfo, Color.BLUE.brighter(), Color.WHITE, cMittleresBlau, cHintergrund);
			}
			if (feldIn(new Rectangle(4, 1, 6, 2), feldInfo.gibFeldNummer())) { // Sterne rechts
				faerbeVorgaben(g, rFeld, feldInfo, cHellesGelb, Color.DARK_GRAY, cHintergrund);
			}
			if (feldIn(new Rectangle(1, 1, 3, 7), feldInfo.gibFeldNummer())) { // Sterne links
				faerbeVorgaben(g, rFeld, feldInfo, cHellesGelb, Color.DARK_GRAY, cHintergrund);
			}
			if (feldIn(new Rectangle(1, 8, 5, 2), feldInfo.gibFeldNummer())) { // Wiege
				faerbeVorgaben(g, rFeld, feldInfo, cSattesBraun.brighter(), Color.WHITE, cHintergrund);
			}
		}
			break;
		case MOND:
			faerbeVorgaben(g, rFeld, feldInfo, cHellesGelb, Color.DARK_GRAY, Color.WHITE);
			break;
		case ZAHL:
			faerbeVorgabenBand(g, rFeld, feldInfo, Color.RED, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY,
					Color.WHITE);
			break;
		case LOK: {
			Color cHintergrund = Color.WHITE;
			if (feldIn(new Rectangle(1, 1, 4, 6), feldInfo.gibFeldNummer())) { // Vögel
				faerbeVorgaben(g, rFeld, feldInfo, Color.DARK_GRAY, Color.WHITE, cHintergrund);
			}
			if (feldIn(new Rectangle(5, 1, 5, 5), feldInfo.gibFeldNummer())) { // Rauch
				faerbeVorgaben(g, rFeld, feldInfo, cHellesBlau, Color.BLACK, cHintergrund);
			}
			if (feldIn(new Rectangle(4, 6, 6, 4), feldInfo.gibFeldNummer())) { // Lok
				faerbeVorgaben(g, rFeld, feldInfo, cSattesGruen, Color.WHITE, cHintergrund);
			}
			if (feldIn(new Rectangle(1, 9, 9, 1), feldInfo.gibFeldNummer())) { // Räder
				faerbeVorgaben(g, rFeld, feldInfo, cHellesGelb, Color.DARK_GRAY, cHintergrund);
			}
		}
			break;

		default:
			super.maleFeld(g, dSudoku, feldInfo);
			break;
		}
	}

	@Override
	public void registriereSudoku(InfoSudoku infoSudoku, int sudokuIndex) {
		this.infoSudoku = infoSudoku;
		this.figur = figuren[sudokuIndex];
	}

}
