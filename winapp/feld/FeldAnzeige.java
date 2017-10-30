package winapp.feld;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JButton;

import sudoku.bedienung.AnzeigeElement;
import sudoku.bedienung.SudokuBedienung;
import sudoku.kern.EintragsEbenen;
import sudoku.kern.feldmatrix.Eintrag;
import sudoku.kern.feldmatrix.FeldNummer;
import sudoku.kern.feldmatrix.ZahlenFeldNummern;
import sudoku.kern.info.FeldInfo;
import sudoku.kern.info.MoeglicheZahl;
import winapp.EintragsModus;
import winapp.Optionen;
import winapp.druck.FrameDruck;
import winapp.tools.ToolTip;

/**
 * @author Hendrick
 * Ist die Repräsentation eines Sudoku-Feldes an der Oberfläche
 */
@SuppressWarnings("serial")
public class FeldAnzeige extends JButton implements AnzeigeElement {
	/**
	 * @param istLinks
	 * @return Platz für die Zusatz-Infos zum Eintrag: Versuchsebene bzw. Tip
	 */
	private static Rectangle gibEintragInfoPlatz(boolean istLinks, Rectangle feldRechteck) {
		int teiler = 4;
		int breite = feldRechteck.width / teiler;
		int breite2 = breite / 6;
		int hoehe = feldRechteck.height / teiler;
		Point point = new Point(breite2, (teiler - 1) * hoehe - breite2);
		if (!istLinks) {
			point = new Point((teiler - 1) * breite - breite2, point.y);
		}
		point.translate(feldRechteck.x, feldRechteck.y);
		Dimension dimension = new Dimension(breite, hoehe);
		Rectangle r = new Rectangle(point, dimension);
		return r;
	}

	private static void maleEintrag(FeldInfo feldInfo, Graphics g, Rectangle rectangle) {
		Eintrag eintragsObjekt = feldInfo.gibEintragObjekt();
		int ebene = eintragsObjekt.gibEbene();
		boolean istEbenenStart = eintragsObjekt.istEbenenStart();
		// Feld-Hintergrund

		Color cHintergrund = FeldFarbe.gibHintergrund(ebene, istEbenenStart);
		FeldMaler.maleRechteck(g, rectangle, cHintergrund);

		// Tip
		boolean istTipZahl = feldInfo.istEintragAlsTipZahl();
		if (istTipZahl) {
			Rectangle rTip = gibEintragInfoPlatz(true, rectangle);
			FeldMaler.maleOval(g, rTip, FeldFarbe.tip);
		}

		int stdEbene = EintragsEbenen.gibStandardEbene1();
		boolean istVersuchN = ebene > stdEbene + 1;
		if (istVersuchN) {
			Rectangle rInfo = gibEintragInfoPlatz(false, rectangle);
			// Color cHintergrund = FeldFarbe.gibVersuchN();
			Color cZahl = FeldFarbe.ebeneVersuchN;
			// Maler.maleZahlAufOval(g, rInfo, cHintergrund, cZahl, ebene - stdEbene);
			FeldMaler.maleZahl(g, rInfo, cZahl, ebene - stdEbene);
		}

		int zahl = feldInfo.gibEintrag();
		Color cZahl = FeldFarbe.gibVordergrund(ebene, istEbenenStart);
		FeldMaler.maleZahl(g, rectangle, cZahl, zahl);
	}

	private static void maleMoegliche(FeldInfo feldInfo, Graphics g, Rectangle rFeld, boolean maleMoegliche,
			boolean zeigeFeldPaare) {
		FeldMaler.maleRechteck(g, rFeld, FeldFarbe.hintergrund);

		if (!maleMoegliche) {
			return;
		}

		ArrayList<MoeglicheZahl> moegliche = feldInfo.gibMoeglicheZahlen();
		if (moegliche == null) {
			return;
		}

		// Speziellen Feld-Hintergrund malen
		Color cHintergrundFeld = FeldFarbe.hintergrund;
		if (moegliche.size() == 0) {
			cHintergrundFeld = FeldFarbe.hintergrundProblem;
		} else {
			Boolean markierung = feldInfo.gibMarkierung();
			if (markierung != null) {
				if (markierung.equals(Boolean.TRUE)) {
					// Aktives Feld im Tip
					cHintergrundFeld = FeldFarbe.hintergrundMarkierung;
				}
			}
		}
		FeldMaler.maleRechteck(g, rFeld, cHintergrundFeld);
		if (moegliche.size() == 0) {
			return; // ===========================================>
		}

		// Die möglichen Zahlen
		ArrayList<Rectangle> zahlenPlaetze = FeldMaler.gibZahlenPlaetze(rFeld, moegliche.size());
		for (int iZahl = 0; iZahl < moegliche.size(); iZahl++) {
			Rectangle rPlatz = zahlenPlaetze.get(iZahl);
			MoeglicheZahl mZahl = moegliche.get(iZahl);
			Color cHintergrund = cHintergrundFeld;
			// FeldPaare verändern den Hintergrund (nur) der Zahl.
			if (feldInfo.istFeldPaar() && zeigeFeldPaare) {
				ZahlenFeldNummern feldPaare = feldInfo.gibFeldParter();

				if (feldPaare.istVorhanden(mZahl.gibZahl())) {
					cHintergrund = FeldFarbe.hintergrundPaar;
				}
			}
			if (mZahl.istMarkiert()) {
				FeldMaler.maleZahlAufOval(g, rPlatz, FeldFarbe.moeglichMarkiertHintergrund,
						FeldFarbe.moeglichMarkiertZahl, mZahl.gibZahl());
			} else {
				FeldMaler.maleZahlAufRechteck(g, rPlatz, cHintergrund, FeldFarbe.moegliche, mZahl.gibZahl());
			}
		}
	}

	/**
	 * Malt Vorgabe schwarz auf grau (wie Feldanzeige), Eintrag blau auf weiß, leeres Feld weiß.
	 * @param feldInfo
	 * @param g
	 * @param rectangle
	 */
	public static void maleEinfach(FeldInfo feldInfo, Graphics g, Rectangle rectangle) {
		if (feldInfo.istVorgabe()) {
			FeldMaler.maleZahlAufRechteck(g, rectangle, FeldFarbe.vorgabeHintergrund, FeldFarbe.vorgabe,
					feldInfo.gibVorgabe());
			return;
		}

		FeldMaler.maleRechteck(g, rectangle, FeldFarbe.hintergrund);
		if (feldInfo.istEintrag()) {
			FeldMaler.maleZahl(g, rectangle, FeldFarbe.eintrag, feldInfo.gibEintrag());
		}
	}

	/**
	 * In dieser Methode darf kein return Verwendung finden, 
	 * denn an ihrem Ende wird die Passivmarkierung des Feldes gemalt!
	 * @param feldInfo
	 * @param g
	 * @param rectangle des Feldes
	 * @param maleMoegliche bei true werden die möglichen Zahlen gemalt
	 * @param zeigeFeldPaare bei true werden die Feldpaare der möglichen Zahlen gemalt
	 * @param moeglicheMarkierungSichtbarkeit Sichtbarkeit einer markierten möglichen Zahl:
	 * 					0.0=Zahl ist unsichtbar, 
	 * 					0.5=Läßt den Hintergrund zur Hälfte durchschimmern, 
	 * 					1.0=Zahl verdeckt komplett den Hintergrund
	 */
	public static void male(FeldInfo feldInfo, Graphics g, Rectangle rectangle, boolean maleMoegliche,
			boolean zeigeFeldPaare) {
		if (feldInfo.istVorgabe()) {
			FeldMaler.maleZahlAufRechteck(g, rectangle, FeldFarbe.vorgabeHintergrund, FeldFarbe.vorgabe,
					feldInfo.gibVorgabe());
		}
		if (feldInfo.istEintrag()) {
			maleEintrag(feldInfo, g, rectangle);
		}
		if ((!feldInfo.istVorgabe()) && (!feldInfo.istEintrag())) {
			maleMoegliche(feldInfo, g, rectangle, maleMoegliche, zeigeFeldPaare);
		}

		// Feld-Markierung:
		Boolean markierung = feldInfo.gibMarkierung();
		if (markierung == null) {
			return;
		}
		if (markierung.equals(Boolean.TRUE)) {
			// Diese Markierung wird beim Malen der Möglichen realisiert.
			return;
		}

		FeldMaler.maleSchleier(g, rectangle, Color.BLACK);
	}

	// ==========================================================================================
	// static-member sind allen SudokuButton identisch
	// sollten auch bei Mehrfachsudoku gehen !
	private SudokuBedienung sudoku;
	private Optionen optionen;
	// private StatusBar statusBar;

	// Auf dem Feld des Sudoku arbeite ich:
	private FeldNummer feldNummer;

	private final String stdToolTip;
	private TipZahlBlinker tipZahlBlinker;
	private MalZustand malZustand;

	// private boolean istGedrueckt;
	public void setzeGedrueckt(boolean istGedrueckt) {
		// this.istGedrueckt = istGedrueckt;
	}

	// -------------------------------------------------------------------------
	public FeldAnzeige(FeldNummer feldNummer, ArrayList<FeldAnzeige> feldAnzeigen, EintragsModus aEintragsModus,
			SudokuBedienung aSudoku, Optionen optionen) {
		super();
		feldAnzeigen.add(this);
		this.sudoku = aSudoku;
		this.optionen = optionen;
		this.feldNummer = feldNummer;
		this.stdToolTip = String.format(" Ich bin Feld %s (%s)", feldNummer, feldNummer.gibBeschreibung());
		this.setToolTipText(stdToolTip);
		this.tipZahlBlinker = null;
		this.malZustand = null;

		// Den Behandler für die Maus-Ereignisse erstellen und bei mir einklinken
		new FeldMaus(this, aEintragsModus, this.optionen);
		// Den Behandler für die Tastatur-Ereignisse erstellen und bei mir einklinken
		new FeldTasten(this, feldAnzeigen, aEintragsModus);

		this.setToolTipText(stdToolTip);
		// this.addKeyListener(l)

		sudoku.registriereAnzeigeElement(this);
	}

	/**
	 * @return auch null
	 */
	public FeldInfo gibFeldInfo() {
		return sudoku.gibFeldInfo(feldNummer);
	}

	public FeldNummer gibFeldNummer() {
		return feldNummer;
	}

	/**
	 * Die FeldAnzeige wird (neu) positioniert und es wird seine (neu) Größe gestellt
	 * @param sudokuBreite
	 * @param sudokuHohe
	 */
	public void parentResized(Point pos, Dimension dimension) {
		PosUndLaenge horizontalSudoku = new PosUndLaenge(pos.x, dimension.width);
		PosUndLaenge horizontalFeld = FeldPosition.gibPosUndLaenge(horizontalSudoku, feldNummer.spalte);

		PosUndLaenge vertikalSudoku = new PosUndLaenge(pos.y, dimension.height);
		PosUndLaenge vertikalFeld = FeldPosition.gibPosUndLaenge(vertikalSudoku, feldNummer.zeile);

		this.setLocation(horizontalFeld.pos, vertikalFeld.pos);
		this.setSize(horizontalFeld.laenge, vertikalFeld.laenge);
		this.repaint();
	}

	public void zeigeSudokuFeld() {
		FeldInfo feldInfo = gibFeldInfo();
		if (feldInfo != null) {
			MalZustand neuerMalZustand = new MalZustand(feldInfo, optionen.istZeigeMoegliche(),
					optionen.istZeigeFeldPaare());
			boolean istMalen = neuerMalZustand.istMalen(this.malZustand);
			if (istMalen) {
				this.malZustand = neuerMalZustand;
				boolean istTip = feldInfo.istEintragAlsTipZahl();
				if (istTip) {
					if (tipZahlBlinker == null) {
						tipZahlBlinker = new TipZahlBlinker(this);
					}
				} else {
					tipZahlBlinker = null;
				}
				this.repaint();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sudoku.GUIVermittler.SudokuAnzeige#zeige(sudoku.GUIVermittler.Sudoku) Zeigt den Inhalt des Button entsprechend des sudoku an
	 */
	@Override
	public void zeige(SudokuBedienung sudoku) {
		zeigeSudokuFeld();
	}

	/**
	 * Setzt im sudoku die Vorgabe: Entweder wird sie gelöscht oder sie wird
	 * gesetzt sie falls die Zahl in den Möglichen des Feldes enthalten ist
	 * 
	 * @param zahl
	 */
	public void setzeVorgabe(int zahl) {
		tipZahlBlinker = null;
		// Vorgabe löschen:
		if (zahl == 0) {
			sudoku.setzeVorgabe(feldNummer, zahl);
			return;
		}

		FeldInfo feldInfo = gibFeldInfo();
		if (feldInfo != null) {
			if (feldInfo.istMoeglich(zahl)) {
				sudoku.setzeVorgabe(feldNummer, zahl);
			}
		}
	}

	/**
	 * Setzt im sudoku den Eintrag: Entweder wird er gelöscht oder setzt ihn
	 * falls: - das Feld nicht schob eine Vorgabe besitzt und - die Zahl in den
	 * Möglichen des Feldes enthalten ist
	 * 
	 * @param zahl
	 * @return
	 */
	public void setzeEintrag(int zahl) {
		tipZahlBlinker = null;
		FeldInfo feldInfo = gibFeldInfo();
		if (feldInfo != null) {
			// Unsinn: Nicht bei vorhandener Vorgabe
			if (feldInfo.istVorgabe())
				return;

			// Eintrag löschen
			if (zahl == 0) {
				if (feldInfo.istEintrag()) {
					sudoku.setzeEintrag(feldNummer, 0);
				}
				return;
			}

			// Eintrag setzen

			// Aber nicht wenn keine Möglichen da sind
			if (feldInfo.gibMoegliche().isEmpty()) {
				return; // false;
			}

			// Aber nicht wenn sie nicht möglich ist
			if (!feldInfo.istMoeglich(zahl)) {
				return; // false;
			}
			sudoku.setzeEintrag(feldNummer, zahl);
		}
		return; // true;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Rectangle rFeld = new Rectangle(this.getSize());

		// Rahmen bei Selektiert oder Focus
		if ((this.isSelected() | this.isFocusOwner()) & !FrameDruck.istDruck()) {
			// boolean erhoeht = this.istGedrueckt;
			int hoehe = this.getHeight();
			g.setColor(FeldFarbe.rahmen);
			// g.fill3DRect(0, 0, this.getWidth(), hoehe, erhoeht);
			g.fillRect(0, 0, this.getWidth(), hoehe);
			int rahmenBreite = hoehe / 20;
			g.clipRect(rahmenBreite, rahmenBreite, this.getWidth() - 2 * rahmenBreite, hoehe - 2 * rahmenBreite);

			rFeld = new Rectangle(rahmenBreite, rahmenBreite, this.getWidth() - 2 * rahmenBreite,
					hoehe - 2 * rahmenBreite);
		}

		FeldInfo feldInfo = this.malZustand.gibFeldInfo();
		boolean maleMoegliche = this.optionen.istZeigeMoegliche() & sudoku.istMoeglichLogik();
		boolean zeigeFeldPaare = this.optionen.istZeigeFeldPaare();
		FeldAnzeige.male(feldInfo, g, rFeld, maleMoegliche, zeigeFeldPaare);

		// ToolTipText
		this.setToolTipText(stdToolTip);
		if (maleMoegliche & zeigeFeldPaare & feldInfo.istFeldPaar()) {
			String[] paareTexte = feldInfo.gibFeldPartnerTexte();
			String[] toolTipTexte = new String[paareTexte.length + 1];
			toolTipTexte[0] = stdToolTip;
			for (int i = 0; i < paareTexte.length; i++) {
				toolTipTexte[i + 1] = paareTexte[i];
			}
			this.setToolTipText(ToolTip.gibToolTip(toolTipTexte));
		}

		// TipBlinker
		boolean istTip = feldInfo.istEintrag() & feldInfo.istEintragAlsTipZahl();
		if (istTip) {
			if (tipZahlBlinker != null) {
				if (tipZahlBlinker.istBlink()) {
					FeldMaler.maleRechteck(g, rFeld, Color.WHITE);
				}
			}
		}
	}

}
