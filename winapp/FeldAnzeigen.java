package winapp;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.JButton;

import sudoku.bedienung.SudokuBedienung;
import sudoku.kern.feldmatrix.FeldNummer;
import winapp.feld.FeldAnzeige;
import winapp.feld.FeldFarbe;
import winapp.statusbar.StatusBar;
import winapp.toolbar.ToolBarLinks;
import winapp.toolbar.ToolBarRechts;

public class FeldAnzeigen implements ComponentListener // Anpassung der FeldAnzeigen an die Applikationsgröße
{
	private ToolBarLinks toolBarLinks;
	private ToolBarRechts toolBarRechts;
	private StatusBar statusBar;
	private ArrayList<FeldAnzeige> feldAnzeigen;

	public FeldAnzeigen(Container container, ToolBarLinks toolBarLinks, ToolBarRechts toolBarRechts,
			StatusBar statusBar, SudokuBedienung sudoku, Optionen optionen) {
		container.setBackground(FeldFarbe.gibSudokuRahmen());
		this.toolBarLinks = toolBarLinks;
		this.toolBarRechts = toolBarRechts;
		this.statusBar = statusBar;
		this.feldAnzeigen = new ArrayList<FeldAnzeige>();

		for (int zeile = 1; zeile < 10; zeile++) {
			for (int spalte = 1; spalte < 10; spalte++) {
				FeldAnzeige b = new FeldAnzeige(new FeldNummer(spalte, zeile), this.feldAnzeigen, toolBarLinks, sudoku,
				// statusBar,
						optionen);
				container.add(b);
			}
		}

		// wer weiß, warum der letzte erschaffene Button spinnt
		{
			JButton b = new JButton("1234");
			b.setVisible(false);
			container.add(b);
		}

		container.addComponentListener(this);
	}

	public void anzeigen() {
		for (int i = 0; i < feldAnzeigen.size(); i++) {
			FeldAnzeige fa = feldAnzeigen.get(i);
			fa.zeigeSudokuFeld();
		}
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
	}

	@Override
	public void componentResized(ComponentEvent e) {
		Container container = (Container) e.getSource();
		int paneHoehe = container.getHeight();
		int paneBreite = container.getWidth();
		int toolBarLBreite = toolBarLinks.getWidth();
		if (!toolBarLinks.isVisible()) {
			toolBarLBreite = 0;
		}
		int toolBarRBreite = toolBarRechts.getWidth();
		if (!toolBarRechts.isVisible()) {
			toolBarRBreite = 0;
		}

		int sudokuX = toolBarLBreite;
		int sudokuY = 0;

		int nutzbarB = paneBreite - toolBarLBreite - toolBarRBreite;
		int nutzbarH = paneHoehe - statusBar.getHeight();

		// Sudoku-Felder zeigen
		for (int i = 0; i < feldAnzeigen.size(); i++) {
			FeldAnzeige b = feldAnzeigen.get(i);
			Point pos = new Point(sudokuX, sudokuY);
			Dimension dimension = new Dimension(nutzbarB, nutzbarH);
			b.parentResized(pos, dimension);
		}
	}

}
