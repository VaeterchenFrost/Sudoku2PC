package winapp.druckinfo;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.SystemColor;

import javax.swing.JPanel;

import winapp.druck.SeitenMaler;
import winapp.druck.Seitenformat;
import winapp.druck.SudokuListe;

@SuppressWarnings("serial")
public class InfoSudokuMalFlaeche extends JPanel {

	// --------------------------------------------------------------------------------------------
	// private class MoeglicheMarkierer implements ActionListener{
	// private InfoSudokuMalFlaeche maler;
	// private boolean istMarkierung;
	// private float sichtbarkeit;
	// private Timer timer;
	//
	// MoeglicheMarkierer(InfoSudokuMalFlaeche maler, boolean istMarkierung){
	// this.maler = maler;
	// this.istMarkierung = istMarkierung;
	// sichtbarkeit = 1.0f;
	// this.timer = new Timer(1400, this);
	// if (this.istMarkierung){
	// this.timer.start();
	// }
	// }
	//
	// float gibSichtbarkeit(){
	// return sichtbarkeit;
	// }
	//
	// @Override
	// public void actionPerformed(ActionEvent arg0) {
	// float fMin = 0.4f;
	// if (sichtbarkeit == 1.0f){ sichtbarkeit = fMin;
	// }
	// else{ sichtbarkeit = 1.0f;
	// }
	// this.maler.repaint();
	// }
	// } // private class MoeglicheMarkierer
	// --------------------------------------------------------------------------------------------

	private SudokuListe sudokus;

	// private MoeglicheMarkierer moeglicheMarkierer;

	public InfoSudokuMalFlaeche(SudokuListe sudokus) {
		this.sudokus = sudokus;
		// Die markierten möglichen Zahlen pumpen lassen per Timer geht nicht richtig:
		// Bisweilen fällt das Bild ruckartig zusammen. Deshalb erfolgt diese Markierung einfach statisch.
		// this.moeglicheMarkierer = new MoeglicheMarkierer(this, false); //sudokus.istMarkierungMoeglicherZahl());
		this.setBackground(SystemColor.window);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension dimension = this.getSize();
		SeitenMaler.maleSeite(sudokus, 0, g, Seitenformat.gibRand(dimension), dimension);
		// this.moeglicheMarkierer.gibSichtbarkeit());
	}
}
