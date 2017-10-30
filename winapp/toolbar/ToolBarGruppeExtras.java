package winapp.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import sudoku.bedienung.BedienElement;
import sudoku.bedienung.SudokuBedienung;
import sudoku.kern.protokoll.ProtokollKursorInfo;
import winapp.druckinfo.extras.Drucke6;
import winapp.druckinfo.extras.GeschenkeDruck;
import winapp.statusbar.StatusBar;

@SuppressWarnings("serial")
public class ToolBarGruppeExtras {

	private class ButtonExtra extends JButton implements BedienElement {
		protected JFrame frame;
		protected String applikationsTitel;
		protected SudokuBedienung sudoku;
		protected StatusBar statusBar;

		public ButtonExtra(String titel, String toolTip, JFrame frame, String applikationsTitel, SudokuBedienung sudoku,
				StatusBar statusBar) {
			super(titel);
			this.setToolTipText(toolTip);
			this.frame = frame;
			this.applikationsTitel = applikationsTitel;
			this.sudoku = sudoku;
			this.statusBar = statusBar;
			this.sudoku.registriereBedienElement(this);
		}

		@Override
		public void sperre() {
			this.setEnabled(false);
		}

		@Override
		public void entsperre(boolean istVorgabenMin, ProtokollKursorInfo protokollKursorInfo) {
			this.setEnabled(true);
		}
	}

	// Geschenk
	public class ButtonGeschenk extends ButtonExtra implements ActionListener {
		private GeschenkeDruck geschenk;

		public ButtonGeschenk(JFrame frame, String applikationsTitel, SudokuBedienung sudoku, StatusBar statusBar) {
			super("Geschenk...", "Sudokus eines Verzeichnisses drucken", frame, applikationsTitel, sudoku, statusBar);
			geschenk = new GeschenkeDruck(frame);
			this.addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			geschenk.erstelle(frame, statusBar);
		}
	}

	// Drucke6
	public class ButtonDrucke6 extends ButtonExtra implements ActionListener {
		public ButtonDrucke6(JFrame frame, String applikationsTitel, SudokuBedienung sudoku, StatusBar statusBar) {
			super("Drucke 6...", "6 Sudokus einer gewünschten Schwierigkeit drucken", frame, applikationsTitel, sudoku,
					statusBar);
			this.addActionListener(this);
			Drucke6.vorbereiten();
		}

		public void actionPerformed(ActionEvent e) {
			// Typ über PopupMenü auswählen lassen
			Drucke6.erstelle(sudoku, this, statusBar);
		}
	}

	public ToolBarGruppeExtras(ToolBarLinks toolBar, ToolBarRechts toolBarRechts, JFrame frame, String titel,
			SudokuBedienung sudoku, StatusBar statusBar) {
		toolBar.add(new ButtonDrucke6(frame, titel, sudoku, statusBar));
		toolBar.add(new ButtonGeschenk(frame, titel, sudoku, statusBar));
	}

}
