package winapp.toolbar;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import sudoku.bedienung.BedienElement;
import sudoku.bedienung.SudokuBedienung;
import sudoku.kern.exception.EndeDurchAusnahme;
import sudoku.kern.info.InfoSudoku;
import sudoku.kern.protokoll.ProtokollKursorInfo;
import sudoku.tip.TipBericht;
import sudoku.tip.TipKomprimierer;
import sudoku.varianz.Loesungen;
import winapp.EintragsModus;
import winapp.FeldAnzeigen;
import winapp.Optionen;
import winapp.SudokuFrame;
import winapp.druck.Seitenformat;
import winapp.druckinfo.InfoSudokuDruck;
import winapp.druckinfo.MalerDifferenz;
import winapp.statusbar.StatusBar;
import winapp.tip.TipAnzeige;
import winapp.toolbar.ToolBarGruppeAnzeige.CheckBoxMoegliche;

@SuppressWarnings("serial")
public class ToolBarLinks extends ToolBar0 implements EintragsModus {

	/**
	 * @author Hendrick
	 * Der Button entsperrt sich wenn ausreichend Vorgaben existieren
	 */
	private class ButtonSudoku extends JButton implements BedienElement {
		public ButtonSudoku(String titel, SudokuBedienung sudoku) {
			super(titel);
			sudoku.registriereBedienElement(this);
		}

		@Override
		public void sperre() {
			this.setEnabled(false);
		}

		@Override
		public void entsperre(boolean istVorgabenMin, ProtokollKursorInfo protokollKursorInfo) {
			this.setEnabled(istVorgabenMin);
		}
	}

	// GibTip
	private class ButtonGibTip extends ButtonSudoku implements ActionListener {
		ButtonGibTip(SudokuBedienung sudoku) {
			super("Gib Tip", sudoku);
			setToolTipText("Anforderung eines Tips: Wie komme ich zur nächsten zu setzenden Zahl?");
			this.addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				TipBericht tipBericht = new TipBericht();
				sudoku.setzeTip(tipBericht);
				TipBericht komprimierterBericht = null;
				komprimierterBericht = TipKomprimierer.gibKomprimiert(sudoku, tipBericht);
				TipAnzeige.zeigeTip(SudokuFrame.gibMainFrame(), tipBericht, komprimierterBericht);
			} catch (Exception exception) {
				// statusBar.zeigeProblem(exception.getMessage());
				exception.printStackTrace();
				throw (new EndeDurchAusnahme(exception));
			}
		}
	}

	// GibTipZahl
	private class ButtonGibTipZahl extends ButtonSudoku implements ActionListener {
		ButtonGibTipZahl(SudokuBedienung sudoku) {
			super("Gib Zahl", sudoku);
			setToolTipText("Anforderung der nächsten zu setzenden Zahl (mit allen zur Verfügung stehenden Mitteln)");
			this.addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				sudoku.setzeTipZahl();
			} catch (Exception exception) {
				// statusBar.zeigeProblem(exception.getMessage());
				exception.printStackTrace();
				throw (new EndeDurchAusnahme(exception));
			}
		}
	}

	private static JRadioButton rbEintragsModusVorgabe;
	private static JRadioButton rbEintragsModusEintrag;

	public ToolBarLinks(SudokuBedienung aSudoku, StatusBar aStatusBar, Container container) {
		super(aSudoku, aStatusBar);
		container.add(this, BorderLayout.WEST);

		// Gruppe Setzen
		this.erschaffeGruppenTitel(" Setzen ", "Modus des Zahlen-Setzens: Als Vorgaben oder als Eintrag");
		{
			rbEintragsModusVorgabe = new JRadioButton(this.gibEintragsModusString(Modus.Vorgabe), true);
			rbEintragsModusVorgabe.setToolTipText("Vorgaben setzen");
			this.add(rbEintragsModusVorgabe);

			rbEintragsModusEintrag = new JRadioButton(this.gibEintragsModusString(Modus.Eintrag), false);
			rbEintragsModusEintrag.setToolTipText("Einträge setzen");
			this.add(rbEintragsModusEintrag);
			ButtonGroup group = new ButtonGroup();
			group.add(rbEintragsModusVorgabe);
			group.add(rbEintragsModusEintrag);
		}

		this.addSeparator();
		this.addSeparator();
		this.addSeparator();

		// Gruppe Lösen
		this.erschaffeGruppenTitel(" Lösen ", "Lösen des Sudoku");
		this.addSeparator();
		{

			// { // Das Setzen eines errechneten Eintrags macht richtig mit Markierung ButtonGibTipZahl!
			// JButton bSetzeKlare1 = new ButtonSudoku("1 Eintrag", sudoku);
			// bSetzeKlare1
			// .setToolTipText("Das nächste Feld, das mit nur einer möglichen Zahl erkannt wird, erhält diese Zahl als Eintrag");
			// bSetzeKlare1.addActionListener(new ActionListener() {
			// public void actionPerformed(ActionEvent e) {
			// sudoku.setzeEintrag();
			// }
			// });
			// this.add(bSetzeKlare1);
			// }

			{
				JButton bSetzeKlareAlle = new ButtonSudoku("Einträge", sudoku);
				String s1 = "Solange ein Feld mit nur 1 möglichen Zahl erkannt wird, ";
				String s2 = "erhält dieses Feld die Zahl als Eintrag";
				bSetzeKlareAlle.setToolTipText(String.format("<html>%s<br>%s</html>", s1, s2));
				bSetzeKlareAlle.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						sudoku.setzeEintraegeAufKlare();
					}
				});
				this.add(bSetzeKlareAlle);
			}

			// GibTip
			{
				JButton b = new ButtonGibTip(sudoku);
				this.add(b);
			}

			// GibTipZahl
			{
				ButtonGibTipZahl bGibTipZahl = new ButtonGibTipZahl(sudoku);
				this.add(bGibTipZahl);
			}

			// Knacke
			{
				JButton bKnacke = new ButtonSudoku("Knacke", sudoku);
				bKnacke.setToolTipText(
						"Es wird versucht, das Sudoku mit allen zur Verfügung stehenden Mitteln zu lösen");
				bKnacke.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						sudoku.knacke();
					}
				});

				this.add(bKnacke);
			}
			// Anzahl der Lösungen des Sudoku
			{
				JButton bVarianz = new ButtonSudoku("Varianz", sudoku);
				bVarianz.setToolTipText("Die Anzahl der Lösungen des Sudoku wird ermittelt");
				bVarianz.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int nDruckSeitenPlaetze = Seitenformat.gibMaxSudokuAnzahlA4();
						Loesungen loesungen = sudoku.ermittleLoesungen(nDruckSeitenPlaetze);

						if (loesungen == null) {
							JOptionPane.showMessageDialog(null, "Fehler");
						} else {
							String nLoesungen = String.format("Anzahl Lösungen = %d", loesungen.gibAnzahl());
							if (loesungen.gibAnzahl() == nDruckSeitenPlaetze) {
								nLoesungen = String.format("Anzahl Lösungen = %d oder mehr", loesungen.gibAnzahl());
							}
							String meldung = String.format("%s. %nSollen alle Varianten gedruckt werden?", nLoesungen);
							int antwort = JOptionPane.showConfirmDialog(null, meldung, "Varianz des Sudoku",
									JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
							if (antwort == JOptionPane.YES_OPTION) {
								// Ausdrucken:
								InfoSudoku[] infoSudokus = loesungen.gibInfoSudokusMitDifferenzTitel();
								InfoSudoku[] basisSudokus = loesungen.gibBasisSudokus();

								InfoSudokuDruck.drucke("Varianz", infoSudokus, new MalerDifferenz(basisSudokus), false,
										statusBar);
							}
						}
					}
				});

				this.add(bVarianz);
			}
		}
	}

	public void erschaffeGruppeAnzeige(Optionen optionen, SudokuBedienung sudoku, FeldAnzeigen feldAnzeigen) {
		this.addSeparator();
		this.addSeparator();
		this.addSeparator();
		this.erschaffeGruppenTitel(" Anzeige ", "Anzeige-Optionen");
		this.addSeparator();
		new ToolBarGruppeAnzeige(this, optionen, sudoku, feldAnzeigen);
	}

	public void erschaffeGruppeSudoku(JFrame frame, String titel, EintragsModus eintragsModus, SudokuBedienung sudoku,
			StatusBar statusBar, ToolBarRechts toolBarRechts) {
		this.addSeparator();
		this.addSeparator();
		this.addSeparator();
		this.erschaffeGruppenTitel(" Sudoku ", "Kennt man sonst als Menü 'Datei'");
		this.addSeparator();
		new ToolBarGruppeSudoku(this, toolBarRechts, frame, titel, eintragsModus, sudoku, statusBar);
	}

	@Override
	public Modus gibEintragsModus() {
		if (rbEintragsModusVorgabe.isSelected()) {
			return Modus.Vorgabe;
		} else {
			return Modus.Eintrag;
		}
	}

	@Override
	public void setzeEintragsModus(EintragsModus.Modus modus) {
		if (0 == Modus.Vorgabe.compareTo(modus)) {
			rbEintragsModusVorgabe.setSelected(true);
			rbEintragsModusEintrag.setSelected(false);
		} else if (0 == Modus.Eintrag.compareTo(modus)) {
			rbEintragsModusVorgabe.setSelected(false);
			rbEintragsModusEintrag.setSelected(true);
		}
	}

	@Override
	public String gibEintragsModusString(Modus modus) {
		if (0 == Modus.Vorgabe.compareTo(modus)) {
			return new String("Vorgabe");
		} else if (0 == Modus.Eintrag.compareTo(modus)) {
			return new String("Eintrag");
		}
		return new String("Error in " + this.getClass().getName() + ".gibString()");
	}

	public void erschaffeGruppeExtras(JFrame frame, String titel, SudokuBedienung sudoku, StatusBar statusBar,
			ToolBarRechts toolBarRechts) {
		this.addSeparator();
		this.addSeparator();
		this.addSeparator();
		this.erschaffeGruppenTitel(" Extras ", "");
		this.addSeparator();
		new ToolBarGruppeExtras(this, toolBarRechts, frame, "asljfhjlsa", sudoku, statusBar);
	}

	public boolean istMoeglicheZeigen() {
		boolean istZeigen = false;
		for (int i = 0; i < this.getComponentCount(); i++) {
			Component component = this.getComponent(i);
			if (component instanceof CheckBoxMoegliche) {
				CheckBoxMoegliche cbMoegliche = (CheckBoxMoegliche) component;
				istZeigen = cbMoegliche.gibOption();
			}
		}
		return istZeigen;
	}
}
