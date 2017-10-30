package winapp.feld;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * @author heroe
 * Setzt im Konstruktor einen Verschleierungsgrad 0.0 .. bis.. 1.0, 
 * der mit reset() rückgesetzt wird nach dem Malen mit diesem Verschleierungsgrad. 
 */
class Verschleierer {
	private Composite original;
	private Graphics2D g2d;

	Verschleierer(Graphics g, float schleierStaerke) {
		g2d = (Graphics2D) g;
		original = g2d.getComposite();
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, schleierStaerke);
		g2d.setComposite(ac);
	}

	void reset() {
		g2d.setComposite(original);
	}
}
