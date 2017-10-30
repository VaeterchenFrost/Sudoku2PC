package winapp.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import sudoku.bedienung.BedienElement;
import sudoku.bedienung.SudokuBedienung;
import sudoku.kern.protokoll.ProtokollKursorInfo;
import winapp.FeldAnzeigen;
import winapp.Groesse;
import winapp.Optionen;

public class ToolBarGruppeAnzeige {

	@SuppressWarnings("serial")
	private abstract class CheckBoxOption extends JCheckBox implements ActionListener, BedienElement {
		protected Optionen optionen;
		protected FeldAnzeigen feldAnzeigen;

		protected abstract boolean gibOption();

		protected abstract void setzeOption(boolean setzen);

		CheckBoxOption(String titel, String toolTip, Optionen optionen, SudokuBedienung sudoku,
				FeldAnzeigen feldAnzeigen) {
			super(titel);
			this.optionen = optionen;
			this.setToolTipText(toolTip);
			this.setSelected(gibOption());
			this.feldAnzeigen = feldAnzeigen;
			this.addActionListener(this);
			sudoku.registriereBedienElement(this);
		}

		public void actionPerformed(ActionEvent e) {
			setzeOption(this.isSelected());
			this.feldAnzeigen.anzeigen();
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

	@SuppressWarnings("serial")
	class CheckBoxMoegliche extends CheckBoxOption {
		CheckBoxMoegliche(Optionen optionen, SudokuBedienung sudoku, FeldAnzeigen feldAnzeigen) {
			super("Mögliche", "Mögliche Zahlen (Einträge) zeigen", optionen, sudoku, feldAnzeigen);
		}

		@Override
		protected boolean gibOption() {
			return optionen.istZeigeMoegliche();
		}

		@Override
		protected void setzeOption(boolean setzen) {
			optionen.setzeZeigeMoegliche(setzen);
		}
	}

	@SuppressWarnings("serial")
	private class CheckBoxFeldPaare extends CheckBoxOption {
		CheckBoxFeldPaare(Optionen optionen, SudokuBedienung sudoku, FeldAnzeigen feldAnzeigen) {
			super("Feldpaare",
					"Feld-Paare zeigen. (FeldPaar: Eine Zahl ist nur in zwei Feldern von Zeile/Spalte/Kasten möglich)",
					optionen, sudoku, feldAnzeigen);
		}

		@Override
		protected boolean gibOption() {
			return optionen.istZeigeFeldPaare();
		}

		@Override
		protected void setzeOption(boolean setzen) {
			optionen.setzeZeigeFeldPaare(setzen);
		}
	}

	public ToolBarGruppeAnzeige(ToolBarLinks toolBar, Optionen optionen, SudokuBedienung sudoku,
			FeldAnzeigen feldAnzeigen) {
		// Maximale Größe
		{
			JButton bMax = new JButton("Maximal");
			bMax.setToolTipText("Maximal große Sudoku-Darstellung");
			toolBar.add(bMax);
			bMax.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Groesse.setzeMaximalGroesse();
				}
			});
		}
		// Mögliche
		toolBar.add(new CheckBoxMoegliche(optionen, sudoku, feldAnzeigen));
		// FeldPaare
		toolBar.add(new CheckBoxFeldPaare(optionen, sudoku, feldAnzeigen));
	}

}
