package winapp;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JFrame;

import sudoku.kern.info.InfoSudoku;
import winapp.druck.SudokuListe;
import winapp.druckinfo.InfoSudokuDruck;
import winapp.druckinfo.InfoSudokuMalFlaeche;
import winapp.druckinfo.InfoSudokuMaler;
import winapp.druckinfo.MalerEinfach;
import winapp.tools.MaximaleFensterGroesse;

/**
 * @author heroe
 * Fenster zur Darstellung von n Info-Sudokus
 */
@SuppressWarnings("serial")
public class InfoSudokuFrame extends JFrame {
	private Dimension fensterRahmen;

	public Dimension gibFensterRahmen() {
		return fensterRahmen;
	}

	private void init(Rectangle rectangle, SudokuListe infoSudokuVermittler) {
		this.setResizable(false);
		// this.toFront();
		String titel = infoSudokuVermittler.gibTitel();
		if (titel != null) {
			this.setTitle(titel);
		}

		InfoSudokuMalFlaeche malFlaeche = new InfoSudokuMalFlaeche(infoSudokuVermittler);
		this.add(malFlaeche);

		pack();
		fensterRahmen = getSize();
		fensterRahmen.width -= malFlaeche.getSize().width;
		fensterRahmen.height -= malFlaeche.getSize().height;

		// Jetzt erst nach pack() (!) Größe setzen
		setLocation(rectangle.getLocation());
		setSize(rectangle.getSize());
		this.setVisible(true);
	}

	private Rectangle gibMaxGroesse() {
		return MaximaleFensterGroesse.gibMaxGroesse();
	}

	/**
	 * Die Sudokus werden im Fenster mit maximaler Größe mit dem angewiesenen Maler dargestellt
	 * @param titel
	 * @param infoSudokus
	 * @param infoSudokuMaler
	 */
	public InfoSudokuFrame(String titel, InfoSudoku[] infoSudokus, InfoSudokuMaler infoSudokuMaler,
			boolean istFreiesSeitenformat) {
		super();

		InfoSudokuDruck infoSudokuVermittler = new InfoSudokuDruck(titel, infoSudokus, infoSudokuMaler,
				istFreiesSeitenformat);
		init(gibMaxGroesse(), infoSudokuVermittler);
	}

	/**
	 * Die Sudokus werden im Fenster mit maximaler Größe mit dem Standard-Maler dargestellt
	 * @param titel
	 * @param infoSudokus
	 */
	public InfoSudokuFrame(String titel, InfoSudoku[] infoSudokus, boolean istFreiesSeitenformat) {
		super();

		InfoSudokuMaler infoSudokuMaler = new MalerEinfach();
		InfoSudokuDruck infoSudokuVermittler = new InfoSudokuDruck(titel, infoSudokus, infoSudokuMaler,
				istFreiesSeitenformat);
		init(gibMaxGroesse(), infoSudokuVermittler);
	}

	/**
	 * Die Sudokus werden im Fenster mit maximaler Größe dargestellt
	 * @param infoSudokuVermittler
	 * @param rFrame
	 */
	public InfoSudokuFrame(SudokuListe infoSudokuVermittler) {
		super();
		init(gibMaxGroesse(), infoSudokuVermittler);
	}
}
