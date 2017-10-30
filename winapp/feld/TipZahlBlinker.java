package winapp.feld;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class TipZahlBlinker implements ActionListener {
	private FeldAnzeige feldAnzeige;
	private Timer timer;
	// Anzahl der Anzeige-Läufe
	private int nAnzeige;

	public TipZahlBlinker(FeldAnzeige feldAnzeige) {
		this.feldAnzeige = feldAnzeige;
		this.nAnzeige = 1;
		this.timer = new Timer(100, this);
		this.timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		nAnzeige++;
		feldAnzeige.repaint();
		if (nAnzeige == 5) {
			timer.stop();
		}
	}

	/**
	 * @return true wenn die Spezial-Blink-Anzeige erforderlich ist 
	 */
	public boolean istBlink() {
		return (((nAnzeige % 2) == 0) ? true : false);
	}

	protected void finalize() throws Throwable {
		this.timer.stop();
	}

}
