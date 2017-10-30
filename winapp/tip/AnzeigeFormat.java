package winapp.tip;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JComponent;

import winapp.druck.Seitenformat;

/**
 * @author heroe
 * Verwaltet den Anzeigeausschnitt der Sudokus: Zoomen, Rollen
 */
class AnzeigeFormat {
	class AnzeigeAusschnitt {
		AnzeigeAusschnitt(int offset, int ausschnitt) {
			super();
			this.offset = offset;
			this.ausschnitt = ausschnitt;
		}

		int offset;
		int ausschnitt;
	}

	// Anzahl der Anzeigeplätze bis zu der ein spezielles (kein freies) Format benutzt wird.
	// Dies ist auch das bevorzugte beim Start.
	private int spezialFormatMax;
	// Anzahl der Sudokus insgesamt
	private int nSudokus;
	// Gibt die Darstellungsgröße für das freie Format
	private JComponent sudokuFlaeche;
	// Anzahl der unsichtbaren Sudokus links (ab Anfang)
	private int offset;
	// Anzahl der sichtbaren Sudokus
	private int ausschnitt;

	AnzeigeFormat(int spezialFormatMax, int nSudokus, JComponent sudokuFlaeche) {
		super();
		this.sudokuFlaeche = sudokuFlaeche;
		this.spezialFormatMax = spezialFormatMax;
		setzeAndereDaten(nSudokus);
	}

	void setzeAndereDaten(int nSudokus) {
		this.offset = 0;
		this.nSudokus = nSudokus;
		this.ausschnitt = Math.min(nSudokus, spezialFormatMax);
	}

	AnzeigeAusschnitt gibAusschnitt() {
		return new AnzeigeAusschnitt(offset, ausschnitt);
	}

	boolean istMoeglichZoomMinus() {
		return ausschnitt < nSudokus;
	}

	boolean istMoeglichZoomPlus() {
		return ausschnitt > 1;
	}

	boolean istMoeglichRollenLinks() {
		return offset > 0;
	}

	boolean istMoeglichRollenRechts() {
		return offset + ausschnitt < nSudokus;
	}

	/**
	 * Das Format solange verändern bis sich die Sudokugröße ändert.
	 * @param richtung >0 Sudoku vergrößern, <0 verkleinern (also mehr Sudokus zeigen)
	 */
	private void zoomOrganisiereFreiesFormat(int richtung, int alterAusschnitt) {
		if (ausschnitt <= this.spezialFormatMax) {
			return; // Es handelt sich nicht um das Thema "Freies Format"
		}
		Dimension dMalFlaeche = this.sudokuFlaeche.getSize();
		Seitenformat altesFormat = Seitenformat.gibFormat(alterAusschnitt, true, dMalFlaeche);
		Rectangle rAlt = altesFormat.gibSudokuMalPlatz(0, dMalFlaeche);

		Seitenformat neuesFormat = Seitenformat.gibFormat(ausschnitt, true, dMalFlaeche);
		Rectangle rNeu = neuesFormat.gibSudokuMalPlatz(0, dMalFlaeche);

		if ((richtung < 0) & (ausschnitt == nSudokus)) {
			return; // Ziel erreicht
		}

		while (rAlt.getSize().equals(rNeu.getSize())) {
			ausschnitt += (-1 * richtung);
			neuesFormat = Seitenformat.gibFormat(ausschnitt, true, dMalFlaeche);
			rNeu = neuesFormat.gibSudokuMalPlatz(0, dMalFlaeche);
		}

		ausschnitt = neuesFormat.gibPlatzAnzahl();
		if (ausschnitt > nSudokus) {
			offset = 0;
			ausschnitt = nSudokus;
		}
		if (offset + ausschnitt > nSudokus) {
			offset = nSudokus - ausschnitt;
		}
	}

	/**
	 * @param richtung >0 Sudoku vergrößern, <0 verkleinern (also mehr Sudokus zeigen)
	 * @param bereich 0=Maximal, 1=um eines
	 */
	void zoom(int richtung, int bereich) {
		int alterAusschnitt = ausschnitt;
		if (richtung < 0) { // mehr zeigen
			if (bereich == 0) {
				offset = 0;
				ausschnitt = nSudokus;
			} else { // Eines mehr
				ausschnitt++;
				if (offset + ausschnitt > nSudokus) {
					offset--;
				}
				zoomOrganisiereFreiesFormat(richtung, alterAusschnitt);
			}
		} else { // weniger zeigen
			if (bereich == 0) {
				int aktuelleMitte = offset + ausschnitt / 2;
				ausschnitt = 1;
				offset = aktuelleMitte - 1;
			} else { // Eines weniger
				ausschnitt--;
				zoomOrganisiereFreiesFormat(richtung, alterAusschnitt);
			}
		} // if (richtung < 0
	}

	/**
	 * @param richtung >0 nach rechts zum Ende hin, <0 nach links zum Anfang hin
	 * @param bereich 0=Maximal, 1=eines weiter, 2=um den Ausschnitt weiter
	 */
	void rollen(int richtung, int bereich) {
		if (richtung < 0) { // nach links zum Anfang
			switch (bereich) {
			case 1:
				offset--;
				break;
			case 2:
				offset -= ausschnitt;
				if (offset < 0) {
					offset = 0;
				}
				break;
			default: // alles
				offset = 0;
				break;
			}
		} else { // nach rechts zum Ende hin
			switch (bereich) {
			case 1:
				offset++;
				break;
			case 2:
				offset += ausschnitt;
				break;
			default: // alles
				offset += 1000;
				break;
			}
			if (offset + ausschnitt > nSudokus) {
				offset = nSudokus - ausschnitt;
			}
		} // if (richtung < 0
	}
}
