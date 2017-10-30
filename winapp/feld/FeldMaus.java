package winapp.feld;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import sudoku.kern.info.FeldInfo;
import winapp.EintragsModus;
import winapp.Optionen;

/**
 * @author Hendrick 
 * Behandelt die Maus-Ereignisse einer FeldAnzeige: 
 * Rechte Maustaste: Zeige das PoupuMenü
 * Linke Maustaste: Setze Eintrag
 */
public class FeldMaus extends MouseAdapter {
	private FeldAnzeige feld;
	private EintragsModus eintragsModus;
	private Optionen optionen;

	public FeldMaus(FeldAnzeige feld, EintragsModus eintragsModus, Optionen optionen) {
		this.feld = feld;
		this.eintragsModus = eintragsModus;
		this.optionen = optionen;

		// Mich als Behandler für die Maus-Ereignisse beim Feld einklinken
		feld.addMouseListener(this);
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		feld.setzeGedrueckt(true);
		handleMouse(e);
	}

	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		feld.setzeGedrueckt(false);
		handleMouse(e);
	}

	private void handleMouse(MouseEvent e) {
		if (e.isPopupTrigger()) {
			PopupMenuZahlen.erstellen(feld, eintragsModus, optionen, new Point(e.getX(), e.getY()));
		} else {
			// Eventuell mit dem linken MausButton einen Eintrag setzen
			if ((e.getID() == MouseEvent.MOUSE_RELEASED) && (e.getButton() == MouseEvent.BUTTON1)
					&& (EintragsModus.Modus.Eintrag == eintragsModus.gibEintragsModus())
					&& (optionen.istZeigeMoegliche())) {
				FeldInfo feldInfo = feld.gibFeldInfo();

				if (feldInfo.istEintrag()) {
					feld.setzeEintrag(0);
				} else {
					ArrayList<Integer> moegliche = feldInfo.gibMoegliche();
					if (moegliche.size() == 1) {
						feld.setzeEintrag(moegliche.get(0));
					}
				}
			}
		}
	}
}
