package winapp.druckinfo;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import sudoku.kern.info.FeldInfo;
import sudoku.kern.info.InfoSudoku;

/**
 * @author heroe
 * 
 */
/**
 * @author heroe
 * Malt den Titel und die Felder eines Sudoku. Ein Feld wird als FeldInfo übergeben.
 */
public interface InfoSudokuMaler {
	/**
	 * Dem Maler wird vor maleTitel() und vor maleSudokuHintergrund() 
	 * das zu malende Sudoku und ihr Index im Sudoku-Array bekanntgegeben.  
	 * @param infoSudoku Das zu malende Sudoku
	 * @param sudokuIndex Der Index des Sudoku im Array der Sudokus
	 */
	public void registriereSudoku(InfoSudoku infoSudoku, int sudokuIndex);

	/**
	 * Malt den Rahmen der Sudofelder im Rechteck r. 
	 * Und zwar nicht einfach als Hintergrundfläche weil es dann wackler bei der Markierung möglicher Zahlen gibt.
	 * @param g
	 * @param dSudoku
	 */
	public void maleSudokuRahmen(Graphics g, Dimension dSudoku);

	/**
	 * Malt das Feld feldInfo im Rechteck r
	 * @param g
	 * @param dSudoku Dimension des Sudoku insgesamt
	 * @param feldInfo
	 * @param maleMoegliche
	 * @param moeglicheMarkierungSichtbarkeit 0.0=unsichtbar ..bis.. 1.0=sichbar
	 */
	public void maleFeld(Graphics g, Dimension dSudoku, FeldInfo feldInfo);

	/**
	 * Malt den Titel (zentriert) im Rechteck r
	 * @param g
	 * @param r
	 * @param titel
	 */
	public void maleTitel(Graphics g, Rectangle r, String titel);

	/**
	 * Malt den Titel (zentriert) im Rechteck r: titel1 links und titel2 rechts
	 * @param g
	 * @param r
	 * @param titel1
	 * @param titel2
	 */
	public void maleTitel(Graphics g, Rectangle r, String titel1, String titel2);

	/**
	 * @return true wenn dieser Malerdie Markierung möglicher Zahlen malt.
	 */
	public boolean istMalenMarkierungMoeglicherZahl();
}
