package winapp.feld;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import sudoku.kern.info.FeldInfo;
import winapp.EintragsModus;
import winapp.Optionen;

@SuppressWarnings("serial")
public class PopupMenuZahlen extends JPopupMenu implements ActionListener // Erhält die PopupMenu-Ereignisse
{
	// Das (ständig neu erstellte) PopupMenü
	private static PopupMenuZahlen popupMenu;
	private static EintragsModus eintragsModus;
	private static FeldAnzeige feld;

	private static void erstelleMenuItem(int zahl) {
		JMenuItem menuItem = new JMenuItem(String.valueOf(zahl));
		menuItem.addActionListener(popupMenu);
		popupMenu.add(menuItem);
	}

	/**
	 * Erstellt das PopupMenu: Entweder für Modus Eintrag oder Modus Vorgaben
	 */
	public static void erstellen(FeldAnzeige aFeld, EintragsModus aEintragsModus, Optionen optionen,
			Point componentPos) {
		popupMenu = new PopupMenuZahlen();
		eintragsModus = aEintragsModus;
		feld = aFeld;
		FeldInfo feldInfo = feld.gibFeldInfo();

		boolean istBelegt = feldInfo.istEintrag() || feldInfo.istVorgabe();
		boolean feldBesitztEinenEintragDiesesModus = false;
		if (EintragsModus.Modus.Eintrag == eintragsModus.gibEintragsModus()) {
			feldBesitztEinenEintragDiesesModus = feldInfo.istEintrag();
		}
		boolean istModusVorgabe = EintragsModus.Modus.Vorgabe == eintragsModus.gibEintragsModus();
		if (istModusVorgabe) {
			feldBesitztEinenEintragDiesesModus = feldInfo.istVorgabe();
		}
		Integer resetVorgabe = null;
		if (istModusVorgabe) {
			resetVorgabe = feldInfo.gibResetVorgabe();
		}

		// Titel
		JMenuItem menuItemTitel = new JMenuItem(
				aEintragsModus.gibEintragsModusString(aEintragsModus.gibEintragsModus()));
		popupMenu.add(menuItemTitel);

		// Löschen
		if (istBelegt) {
			if (feldBesitztEinenEintragDiesesModus) {
				erstelleMenuItem(0);
			}
		} else {
			if (resetVorgabe != null) {
				erstelleMenuItem(resetVorgabe.intValue());
			}
			// SetzeMöglichkeiten
			ArrayList<Integer> weitereZahlen = new ArrayList<>();
			if (optionen.istZeigeMoegliche()) {
				weitereZahlen.addAll(feldInfo.gibMoegliche());
			} else {
				for (int i = 1; i < 10; i++) {
					weitereZahlen.add(i);
				}
			}

			if (resetVorgabe != null) {
				weitereZahlen.remove(resetVorgabe);
			}

			if (!weitereZahlen.isEmpty()) {
				popupMenu.addSeparator();
			}

			for (int i = 0; i < weitereZahlen.size(); i++) {
				erstelleMenuItem(weitereZahlen.get(i));
			}
		}

		popupMenu.show(feld, componentPos.x, componentPos.y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent) Implementation der abstrakten Methode des Interfaces ActionListener: Behandelt die Aktionen
	 * des PopupMenüs
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// Die Behandlung der PopupMenü-Ereignisse
		if (e.getSource() instanceof JMenuItem) {
			String cmd = e.getActionCommand();
			int zahl = Integer.valueOf(cmd);
			// ---------------------------- Eintrag
			if (EintragsModus.Modus.Eintrag == eintragsModus.gibEintragsModus()) {
				feld.setzeEintrag(zahl);
			}
			// ---------------------------- Vorgabe
			else if (EintragsModus.Modus.Vorgabe == eintragsModus.gibEintragsModus()) {
				feld.setzeVorgabe(zahl);
			}
		}
	}

}
