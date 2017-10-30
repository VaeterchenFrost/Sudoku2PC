package winapp.druckinfo.extras;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import sudoku.bedienung.SudokuBedienung;
import sudoku.logik.Schwierigkeit;
import sudoku.neu.pool.NeuTypOption;
import winapp.statusbar.StatusBar;

@SuppressWarnings("serial")
public class Drucke6PopupMenue extends JPopupMenu {

	private class OptionsMenue extends JMenu {

		private class MenuItemNeu extends JMenuItem implements ActionListener {
			private SudokuBedienung sudoku;
			private Schwierigkeit schwierigkeit;
			private NeuTypOption option;
			private StatusBar statusBar;

			public MenuItemNeu(SudokuBedienung sudoku, Schwierigkeit schwierigkeit, NeuTypOption option,
					StatusBar statusBar) {
				super(schwierigkeit.toString() + " + " + option.toString());
				this.sudoku = sudoku;
				this.schwierigkeit = schwierigkeit;
				this.option = option;
				this.statusBar = statusBar;
				// this.setToolTipText(neuTyp.gibToolTip());
				this.addActionListener(this);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Drucke6.erstelleUndDrucke(this.schwierigkeit, this.option, this.sudoku, this.statusBar);
			}
		} // private class MenuItemNeu

		OptionsMenue(SudokuBedienung sudoku, Schwierigkeit typ, StatusBar statusBar) {
			super(typ.toString());
			NeuTypOption[] optionen = NeuTypOption.values();

			for (NeuTypOption option : optionen) {
				MenuItemNeu item = new MenuItemNeu(sudoku, typ, option, statusBar);
				this.add(item);
			}
		}
	} // private class OptionsMenue
		// ---------------------------------------------------------------------------------------------------------------

	public Drucke6PopupMenue(SudokuBedienung sudoku, StatusBar statusBar) {
		Schwierigkeit[] typen = Schwierigkeit.values();

		for (Schwierigkeit neuTyp : typen) {
			this.add(new OptionsMenue(sudoku, neuTyp, statusBar));
		}
	}

}
