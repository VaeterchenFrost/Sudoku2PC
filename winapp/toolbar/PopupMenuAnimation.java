package winapp.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import sudoku.bedienung.SudokuBedienung;
import sudoku.kern.animator.Animator;
import sudoku.kern.animator.Animator_DrehenLinks;
import sudoku.kern.animator.Animator_DrehenRechts;
import sudoku.kern.animator.Animator_SpiegelnMittelPunkt;
import sudoku.kern.animator.Animator_SpiegelnOben;
import sudoku.kern.animator.Animator_SpiegelnObenLinks;
import sudoku.kern.animator.Animator_SpiegelnObenRechts;
import sudoku.kern.animator.Animator_SpiegelnSeite;
import sudoku.kern.animator.Animator_VerschiebenHoch;
import sudoku.kern.animator.Animator_VerschiebenLinks;
import sudoku.kern.animator.Animator_VerschiebenRechts;
import sudoku.kern.animator.Animator_VerschiebenRunter;

@SuppressWarnings("serial")
public class PopupMenuAnimation extends JPopupMenu {
	private SudokuBedienung sudoku;

	private class MenuItemAnimation extends JMenuItem implements ActionListener {
		private Animator animator;

		public MenuItemAnimation(Animator animator) {
			super(animator.gibName());
			this.animator = animator;
			this.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			sudoku.animiere(this.animator);
		}
	}

	private void add(Animator animator) {
		this.add(new MenuItemAnimation(animator));
	}

	/**
	 * Erstellt das PopupMenu
	 */
	public PopupMenuAnimation(SudokuBedienung sudoku) {
		this.sudoku = sudoku;
		this.add(new Animator_DrehenLinks());
		this.add(new Animator_DrehenRechts());
		this.addSeparator();
		this.add(new Animator_VerschiebenLinks());
		this.add(new Animator_VerschiebenRechts());
		this.add(new Animator_VerschiebenHoch());
		this.add(new Animator_VerschiebenRunter());
		this.addSeparator();
		this.add(new Animator_SpiegelnSeite());
		this.add(new Animator_SpiegelnOben());
		this.add(new Animator_SpiegelnObenLinks());
		this.add(new Animator_SpiegelnObenRechts());
		this.add(new Animator_SpiegelnMittelPunkt());
	}
}
