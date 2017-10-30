package winapp;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JFrame;

import winapp.tools.MaximaleFensterGroesse;
//import java.awt.Font;
//import java.awt.FontMetrics;

public class Groesse {

	private static Dimension gibGroesse(Dimension maximum, Dimension sudokuRahmen) {
		int maxSudokuBreite = maximum.width - sudokuRahmen.width;
		int maxSudokuHoehe = maximum.height - sudokuRahmen.height;

		int diff = maxSudokuBreite - maxSudokuHoehe;

		// zu breit:
		if (diff > 0) {
			maxSudokuBreite -= diff;
		} else {
			if (diff < 0) {
				maxSudokuBreite += diff;
			}
		}
		int appBreite = maxSudokuBreite + sudokuRahmen.width;
		int appHoehe = maxSudokuHoehe + sudokuRahmen.height;
		return new Dimension(appBreite, appHoehe);
	}

	private static void setzeMaximalGroesse(Dimension maximum) {
		// Größe
		Dimension groesse = gibGroesse(maximum, SudokuFrame.gibSudokuRahmen());
		SudokuFrame.gibMainFrame().setSize(groesse);

		// Position
		JFrame mainFrame = SudokuFrame.gibMainFrame();
		Dimension frameSize = mainFrame.getSize();
		int posX = (maximum.width - frameSize.width) / 2;
		int posY = (maximum.height - frameSize.height) / 2;
		mainFrame.setLocation(posX, posY);
	}

	public static void setzeMaximalGroesse() {
		Rectangle maxRectangle = MaximaleFensterGroesse.gibMaxGroesse();
		setzeMaximalGroesse(new Dimension(maxRectangle.width, maxRectangle.height));
	}

	// Ist nicht nötig wegen MainFrame.pack()
	// public static void setzeStartGroesse(JFrame frame){
	// // Zugang zum Bildschirm holen
	// GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	// Rectangle maxRectangle = ge.getMaximumWindowBounds();
	//
	// // Start-Rechengröße ermitteln
	// int startHoehe = ( (maxRectangle.height * 9) / 10);
	// int startBreite = ( (maxRectangle.width * 9) / 10);
	//
	// // GraphicsDevice[] gds = ge.getScreenDevices();
	// // int gsLength = gds.length;
	// // GraphicsDevice gd = gds[0];
	// // boolean isScreen = gd.getType() == GraphicsDevice.TYPE_RASTER_SCREEN;
	// // GraphicsConfiguration gc = gd.getDefaultConfiguration();
	//
	// Font font = new Font("Arial", Font.BOLD, 16);
	// FontMetrics fontMetrics = frame.getFontMetrics(font);
	//
	// // nichtNutzbareHoehe ermitteln
	// int nichtNutzbareHoehe = 4 * fontMetrics.getHeight(); // Höhe von Titel, Menü, ToolBar, StatusBar
	//
	// setzeGroesse(startBreite, startHoehe, nichtNutzbareHoehe);
	// // Position in Bildschirmmitte anweisen
	// SudokuFrame.gibMainFrame().setLocationRelativeTo(null);
	// }
}
