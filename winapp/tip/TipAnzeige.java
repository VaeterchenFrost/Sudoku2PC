package winapp.tip;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sudoku.kern.info.InfoSudoku;
import sudoku.tip.Tip;
import sudoku.tip.TipBericht;
import sudoku.tip.TipDetailTexte;
import winapp.druckinfo.InfoSudokuDruck;
import winapp.druckinfo.InfoSudokuMalFlaeche;
import winapp.druckinfo.InfoSudokuMaler;
import winapp.druckinfo.MalerStandard;
import winapp.tip.AnzeigeFormat.AnzeigeAusschnitt;
import winapp.tools.MaximaleFensterGroesse;

/**
 * @author heroe
 * Im Konstruktor wird das Hauptfenster unsichtbar gemacht, in dispose() wieder sichtbar.
 */
@SuppressWarnings("serial")
public class TipAnzeige extends JFrame implements ActionListener {
	public static void zeigeTip(JFrame frame, TipBericht tipBericht, TipBericht komprimierterBericht) {
		Tip tip = new Tip(tipBericht);

		Tip tipKomprimiert = null;
		if (komprimierterBericht != null) {
			tipKomprimiert = new Tip(komprimierterBericht);
		}
		new TipAnzeige(frame, tip, tipKomprimiert);
	}

	private static int spezialFormatMax = 3;
	// ---------------------------------------------------------------------------

	JFrame parent;

	Tip tip;
	Tip tipKomprimiert;
	Tip tipInDerAnzeige;

	InfoSudoku[] infoSudokus;
	TipDetailTexte[] tipDetailTexte;

	TexteFrame texteFrame;
	AnzeigeFormat anzeigeZustand;

	JPanel sudokuPane;
	JPanel textePane;

	JPanel buttonPane;
	JButton buttonOK;

	JButton buttonTipKomprimiert;
	JButton buttonTip;

	JButton buttonTextZoomMinus1;
	JButton buttonTextZoomPlus1;

	JButton buttonZoomMinusAlles;
	JButton buttonZoomMinus1;
	JButton buttonZoomPlus1;
	JButton buttonZoomPlusAlles;

	JButton buttonSchrittMinusAlles;
	JButton buttonSchrittMinusSeite;
	JButton buttonSchrittMinus1;
	JButton buttonSchrittPlus1;
	JButton buttonSchrittPlusSeite;
	JButton buttonSchrittPlusAlles;

	public TipAnzeige(JFrame frame, Tip tip, Tip tipKomprimiert) {
		super("Sudoku Tip");
		this.parent = frame;
		this.tip = tip;
		this.tipKomprimiert = tipKomprimiert;

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Die Button-Leiste
		buttonPane = new JPanel(new GridLayout());
		buttonPane.setBorder(BorderFactory.createLoweredBevelBorder());
		{
			JPanel buttonTipPane = new JPanel();
			buttonTipKomprimiert = erstelleButton(buttonTipPane, "Nötige");
			erstelleLabel(buttonTipPane, "Tip");
			buttonTip = erstelleButton(buttonTipPane, "Alle");
			buttonPane.add(buttonTipPane);
		}
		{
			JPanel buttonTextZoomPane = new JPanel();
			buttonTextZoomMinus1 = erstelleButton(buttonTextZoomPane, "-");
			erstelleLabel(buttonTextZoomPane, "Text-Zoom");
			buttonTextZoomPlus1 = erstelleButton(buttonTextZoomPane, "+");
			buttonPane.add(buttonTextZoomPane);
		}
		{
			JPanel buttonZoomPane = new JPanel();
			buttonZoomMinusAlles = erstelleButton(buttonZoomPane, "---");
			buttonZoomMinus1 = erstelleButton(buttonZoomPane, "-");
			erstelleLabel(buttonZoomPane, "Zoom");
			buttonZoomPlus1 = erstelleButton(buttonZoomPane, "+");
			buttonZoomPlusAlles = erstelleButton(buttonZoomPane, "+++");
			buttonPane.add(buttonZoomPane);
		}
		{
			JPanel buttonSchrittPane = new JPanel();
			buttonSchrittMinusAlles = erstelleButton(buttonSchrittPane, "<<<");
			buttonSchrittMinusSeite = erstelleButton(buttonSchrittPane, "<<");
			buttonSchrittMinus1 = erstelleButton(buttonSchrittPane, "<");
			erstelleLabel(buttonSchrittPane, "Schritt");
			buttonSchrittPlus1 = erstelleButton(buttonSchrittPane, ">");
			buttonSchrittPlusSeite = erstelleButton(buttonSchrittPane, ">>");
			buttonSchrittPlusAlles = erstelleButton(buttonSchrittPane, ">>>");
			buttonPane.add(buttonSchrittPane);
		}
		{
			JPanel buttonOKPane = new JPanel();
			buttonOK = erstelleButton(buttonOKPane, "OK");
			buttonPane.add(buttonOKPane);
		}
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		// Die Sudoku-Anzeigen-Panele
		JPanel datenPane = new JPanel(new BorderLayout());
		sudokuPane = new JPanel(new GridLayout(), true);
		datenPane.add(sudokuPane);
		textePane = new JPanel(new GridLayout());
		datenPane.add(textePane, BorderLayout.SOUTH);
		getContentPane().add(datenPane);

		tipInDerAnzeige = tip;
		if (tipKomprimiert != null) {
			tipInDerAnzeige = tipKomprimiert;
		}
		this.infoSudokus = tipInDerAnzeige.gibInfoSudokus();
		this.tipDetailTexte = tipInDerAnzeige.gibTexte();

		this.anzeigeZustand = new AnzeigeFormat(spezialFormatMax, infoSudokus.length, datenPane);

		pack();

		this.texteFrame = new TexteFrame(gibAnzahlTipTexte(), gibFensterTexte(), buttonPane.getHeight());
		stelleAufAnzeigeAusschnitt();

		pack();

		// Hauptfenster unsichtbar machen
		parent.setVisible(false);
		setzeGroesse();
		setVisible(true);
	}

	private void wechsleTip() {
		if (tipInDerAnzeige == tip) {
			tipInDerAnzeige = tipKomprimiert;
		} else {
			tipInDerAnzeige = tip;
		}
		this.infoSudokus = tipInDerAnzeige.gibInfoSudokus();
		this.tipDetailTexte = tipInDerAnzeige.gibTexte();

		this.anzeigeZustand.setzeAndereDaten(this.infoSudokus.length);

		// pack();

		this.texteFrame.setVisible(false);
		this.texteFrame.dispose();
		this.texteFrame = new TexteFrame(gibAnzahlTipTexte(), gibFensterTexte(), this.buttonPane.getHeight());
		stelleAufAnzeigeAusschnitt();

		pack();
	}

	/**
	 * @return Gesamtanzahl der Zeilen bei  tipDetailText.gibTexteInWenigenZeilen()
	 */
	private int gibAnzahlTipTexte() {
		int n = 0;
		for (TipDetailTexte texte : tipDetailTexte) {
			n += texte.gibTexteAnzahl();
		}
		return n;
	}

	private String gibFensterTexte() {
		String s = new String();
		for (int i = 0; i < tipDetailTexte.length; i++) {
			if (i > 0) {
				s += "\n";
			}
			s += " ";
			s += tipDetailTexte[i].gibTexteInWenigenZeilen();
		}
		return s;
	}

	private JLabel erstelleLabel(JPanel pane, String text) {
		JLabel label = new JLabel(text);
		pane.add(label);
		return label;
	}

	private JButton erstelleButton(JPanel pane, String text) {
		JButton button = new JButton(text);
		button.addActionListener(this);
		pane.add(button);
		return button;
	}

	private void setzeGroesse() {
		Rectangle rMax = MaximaleFensterGroesse.gibMaxGroesse();
		setMinimumSize(rMax.getSize());
		setSize(rMax.getSize());
	}

	@Override
	public void dispose() {
		this.texteFrame.dispose();
		// Hauptfenster wieder sichtbar machen
		parent.setVisible(true);
		super.dispose();
	}

	private void enableButtons() {
		buttonTipKomprimiert.setEnabled((tipKomprimiert != null) & (tipInDerAnzeige == tip));
		buttonTip.setEnabled(tipInDerAnzeige == tipKomprimiert);

		buttonTextZoomMinus1.setEnabled(istZoomTextMoeglich(-1));
		buttonTextZoomPlus1.setEnabled(istZoomTextMoeglich(+1));

		boolean istMoeglichZoomMinus = this.anzeigeZustand.istMoeglichZoomMinus();
		buttonZoomMinusAlles.setEnabled(istMoeglichZoomMinus);
		buttonZoomMinus1.setEnabled(istMoeglichZoomMinus);

		boolean istMoeglichZoomPlus = this.anzeigeZustand.istMoeglichZoomPlus();
		buttonZoomPlus1.setEnabled(istMoeglichZoomPlus);
		buttonZoomPlusAlles.setEnabled(istMoeglichZoomPlus);

		boolean istMoeglichSchrittMinus = this.anzeigeZustand.istMoeglichRollenLinks();
		buttonSchrittMinusAlles.setEnabled(istMoeglichSchrittMinus);
		buttonSchrittMinusSeite.setEnabled(istMoeglichSchrittMinus);
		buttonSchrittMinus1.setEnabled(istMoeglichSchrittMinus);

		boolean istMoeglichSchrittPlus = this.anzeigeZustand.istMoeglichRollenRechts();
		buttonSchrittPlus1.setEnabled(istMoeglichSchrittPlus);
		buttonSchrittPlusSeite.setEnabled(istMoeglichSchrittPlus);
		buttonSchrittPlusAlles.setEnabled(istMoeglichSchrittPlus);
	}

	/**
	 * @param richtung >0 Sudoku vergrößern, <0 verkleinern (also mehr Sudokus zeigen)
	 * @param bereich 0=Maximal, 1=um eines
	 */
	private void zoom(int richtung, int bereich) {
		this.anzeigeZustand.zoom(richtung, bereich);
		stelleAufAnzeigeAusschnitt();
	}

	/**
	 * @param richtung >0 nach rechts zum Ende hin, <0 nach links zum Anfang hin
	 * @param bereich 0=Maximal, 1=eines weiter, 2=um den Ausschnitt weiter
	 */
	private void rollen(int richtung, int bereich) {
		this.anzeigeZustand.rollen(richtung, bereich);
		stelleAufAnzeigeAusschnitt();
	}

	private void textZoom(int delta) {
		texteFrame.zoomText(delta);
		int nTextAreas = this.textePane.getComponentCount();
		for (int i = 0; i < nTextAreas; i++) {
			TipTextPanel textArea = (TipTextPanel) this.textePane.getComponent(i);
			textArea.setzeFontGroesse();
		}

		enableButtons();
	}

	private boolean istZoomTextMoeglich(int richtung) {
		if (texteFrame.isVisible()) {
			return texteFrame.istZoomTextMoeglich(richtung);
		}
		int nTextAreas = this.textePane.getComponentCount();
		if (nTextAreas <= 0) {
			return false;
		}
		TipTextPanel textArea = (TipTextPanel) this.textePane.getComponent(0);
		boolean istMoeglich = textArea.istZoomTextMoeglich(richtung);
		return istMoeglich;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonOK) {
			setVisible(false);
			dispose();
		} else if (e.getSource() == buttonTipKomprimiert) {
			wechsleTip();
		} else if (e.getSource() == buttonTip) {
			wechsleTip();
		}

		else if (e.getSource() == buttonTextZoomMinus1) {
			textZoom(-2);
		} else if (e.getSource() == buttonTextZoomPlus1) {
			textZoom(+2);
		}

		else if (e.getSource() == buttonZoomMinusAlles) {
			zoom(-1, 0);
		} else if (e.getSource() == buttonZoomMinus1) {
			zoom(-1, 1);
		} else if (e.getSource() == buttonZoomPlus1) {
			zoom(+1, 1);
		} else if (e.getSource() == buttonZoomPlusAlles) {
			zoom(+1, 0);
		}

		else if (e.getSource() == buttonSchrittMinusAlles) {
			rollen(-1, 0);
		} else if (e.getSource() == buttonSchrittMinusSeite) {
			rollen(-1, 2);
		} else if (e.getSource() == buttonSchrittMinus1) {
			rollen(-1, 1);
		} else if (e.getSource() == buttonSchrittPlus1) {
			rollen(+1, 1);
		} else if (e.getSource() == buttonSchrittPlusSeite) {
			rollen(+1, 2);
		} else if (e.getSource() == buttonSchrittPlusAlles) {
			rollen(+1, 0);
		}
	}

	private void stelleAufAnzeigeAusschnitt() {
		this.textePane.removeAll();
		sudokuPane.removeAll();
		// Wenn dem Frame sein zentrales Anzeigeelement genommen wird, verkleinert er sich.
		// Deshalb: erst alle neuen Sudoku-Anzeigen hinzufügen vor dem löschen der alten:
		// Nützte leider auch nichts! Aber setMinimumSize!!!
		// int nAlteSudokuAnzeigen = sudokuPane.getComponentCount();

		AnzeigeAusschnitt anzeigeAusschnitt = this.anzeigeZustand.gibAusschnitt();

		boolean istFreiesSeitenformat = anzeigeAusschnitt.ausschnitt > spezialFormatMax;
		InfoSudokuMaler maler = new MalerStandard(true);

		int iSudokuFirst = anzeigeAusschnitt.offset;
		int iSudokuLast = anzeigeAusschnitt.offset + anzeigeAusschnitt.ausschnitt - 1;

		if (istFreiesSeitenformat) {
			InfoSudoku[] sudokuArray = new InfoSudoku[anzeigeAusschnitt.ausschnitt];
			int iS = 0;
			for (int iSudoku = iSudokuFirst; iSudoku <= iSudokuLast; iSudoku++) {
				sudokuArray[iS] = infoSudokus[iSudoku];
				iS++;
			}
			InfoSudokuDruck infoSudokuDruck = new InfoSudokuDruck("Sudoku Tip", sudokuArray, maler,
					istFreiesSeitenformat);
			InfoSudokuMalFlaeche malFlaeche = new InfoSudokuMalFlaeche(infoSudokuDruck);
			sudokuPane.add(malFlaeche);
			this.texteFrame.zeigen();
		} else { // kein freies Seitenformat
			for (int iSudoku = iSudokuFirst; iSudoku <= iSudokuLast; iSudoku++) {
				// Sudoku
				InfoSudoku[] sudokuArray = new InfoSudoku[] { infoSudokus[iSudoku] };
				InfoSudokuDruck infoSudokuDruck = new InfoSudokuDruck("Sudoku Tip", sudokuArray, maler,
						istFreiesSeitenformat);
				InfoSudokuMalFlaeche malFlaeche = new InfoSudokuMalFlaeche(infoSudokuDruck);
				sudokuPane.add(malFlaeche);

				// Texte
				this.texteFrame.setVisible(false);
				String sudokuText = this.tipDetailTexte[iSudoku].gibTexteSchmal();
				TipTextPanel tipTextArea = new TipTextPanel(sudokuText);
				this.textePane.add(tipTextArea);
				// pack();
			}
		} // if (istFreiesSeitenformat)

		pack();
		setzeGroesse();
		enableButtons();
	}

}
