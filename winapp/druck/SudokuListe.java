package winapp.druck;

import java.awt.Dimension;
import java.awt.Graphics;

/**
 * @author heroe
 * So muß sich eine Sudokuliste dem Drucker darstellen
 */
public interface SudokuListe {
	/**
	 * @return Die Anzahl der Sudokus
	 */
	public int gibAnzahl();

	/**
	 * @return Den Titel für die Druck-Voschau
	 */
	public String gibTitel();

	/**
	 * Den Sudoku-Titel in Dimension d malen. 
	 * Titel-Links-Oben ist als Koordinatenursprung gestellt.
	 * Es muss ein g.scale() intern rückgängig gemacht werden wenn es intern benutzt wird!
	 * @param sudokuIndex 0-basiert
	 * @param g Malfläche...
	 * @param d So groß soll der Sudoku-Titel dargestellt werden.
	 */
	public void maleSudokuTitel(int sudokuIndex, Graphics g, Dimension d);

	/**
	 * Das Sudoku in Dimension d malen. 
	 * Sudoku-Links-Oben ist als Koordinatenursprung gestellt.
	 * Es muss ein g.scale() intern rückgängig gemacht werden wenn es intern benutzt wird!
	 * @param sudokuIndex 0-basiert
	 * @param g Malfläche...
	 * @param d So groß soll das Sudoku dargestellt werden. 
	 * @param moeglicheMarkierungSichtbarkeit 0.0=unsichtbar ..bis.. 1.0=voll sichtbar 
	 */
	public void maleSudoku(int sudokuIndex, Graphics g, Dimension d);

	/**
	 * @param verzeichnisName vollständiger Pfad, in dem die Sudokus gespeichert werden sollen
	 * @return true wenn alle Sudokus erfolgreich im Verzeichnis gespeichert wurden, sonst false
	 */
	public boolean speichere(String verzeichnisName);

	/**
	 * @param dimension
	 * @return Das Seitenformat, in dem die Darstellung der Sudokus auf einer Seite erfolgen soll
	 */
	public Seitenformat gibSeitenformat(Dimension dimension);
}
