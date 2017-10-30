package winapp.feld;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import sudoku.kern.exception.Exc;
import sudoku.kern.feldmatrix.FeldMatrix;
import sudoku.kern.feldmatrix.FeldNummer;
import winapp.EintragsModus;

public class FeldTasten extends KeyAdapter // implements KeyListener
{
	private FeldAnzeige feld;
	private ArrayList<FeldAnzeige> feldAnzeigen;
	private EintragsModus eintragsModus;

	public FeldTasten(FeldAnzeige feld, ArrayList<FeldAnzeige> feldAnzeigen, EintragsModus eintragsModus) {
		this.feld = feld;
		this.feldAnzeigen = feldAnzeigen;
		this.eintragsModus = eintragsModus;

		// Mich als Behandler für die Tastatur-Ereignisse beim Feld einklinken
		feld.addKeyListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent) Implementation der abstrakten Methoden des Interfaces KeyListener: Zahl als Vorgabe oder Eintrag setzen
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		int keyCode = e.getKeyCode();
		int zahl = keyCode - KeyEvent.VK_NUMPAD0;
		if ((zahl >= 0) && (zahl <= 9)) {
			keyZahl(zahl);
		}
		if (keyCode == KeyEvent.VK_DELETE) {
			keyZahl(0);
		}
		if ((keyCode >= KeyEvent.VK_LEFT) && (keyCode <= KeyEvent.VK_DOWN)) {
			keyBewegen(keyCode);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		super.keyTyped(e);
		char c = e.getKeyChar();
		int i = c - '0';
		if ((i >= 0) && (i <= 9)) {
			keyZahl(i);
		}
	}

	private void keyBewegen(int keyCode) {
		FeldNummer focusFeldNummer = null;
		FeldNummer feldNummer = feld.gibFeldNummer();
		switch (keyCode) {
		case KeyEvent.VK_LEFT:
			focusFeldNummer = new FeldNummer(feldNummer.spalte - 1, feldNummer.zeile);
			break;
		case KeyEvent.VK_RIGHT:
			focusFeldNummer = new FeldNummer(feldNummer.spalte + 1, feldNummer.zeile);
			break;
		case KeyEvent.VK_UP:
			focusFeldNummer = new FeldNummer(feldNummer.spalte, feldNummer.zeile - 1);
			break;
		case KeyEvent.VK_DOWN:
			focusFeldNummer = new FeldNummer(feldNummer.spalte, feldNummer.zeile + 1);
			break;
		}
		try {
			FeldMatrix.kontrolliereFeldNummer(focusFeldNummer);
			for (int i = 0; i < feldAnzeigen.size(); i++) {
				FeldAnzeige fa = feldAnzeigen.get(i);
				if (focusFeldNummer.equals(fa.gibFeldNummer())) {
					fa.requestFocusInWindow();
				}
			}
		} catch (Exc e) {
			e.printStackTrace();
		}
	}

	private void keyZahl(int zahl) {
		if (EintragsModus.Modus.Eintrag == eintragsModus.gibEintragsModus()) {
			feld.setzeEintrag(zahl);
		} else if (EintragsModus.Modus.Vorgabe == eintragsModus.gibEintragsModus()) {
			feld.setzeVorgabe(zahl);
		}
	}

}
