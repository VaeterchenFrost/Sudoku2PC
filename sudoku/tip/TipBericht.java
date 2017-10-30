package sudoku.tip;

import java.util.ArrayList;
import java.util.List;

import sudoku.kern.feldmatrix.FeldNummerListe;
import sudoku.kern.feldmatrix.FeldNummerMitZahl;
import sudoku.kern.info.InfoSudoku;
import sudoku.logik.Logik_ID;
import sudoku.logik.Schwierigkeit;
import sudoku.logik.tipinfo.TipInfo;
import sudoku.logik.tipinfo.TipKurier;

/**
 * @author heroe
 *
 */
public class TipBericht implements TipKurier {
	private ArrayList<TipInfo> infos;
	private ArrayList<TipInfo> ignorierTips;

	public TipBericht() {
		init(null);
	}

	public TipBericht(ArrayList<TipInfo> ignorierTips) {
		init(ignorierTips);
	}

	public void add(TipInfo info) {
		this.infos.add(info);
	}

	private void init(ArrayList<TipInfo> ignorierTips) {
		this.infos = new ArrayList<>();
		this.ignorierTips = ignorierTips;
	}

	public ArrayList<TipInfo> gibInfos() {
		return infos;
	}

	public FeldNummerListe gibMitSpieler() {
		FeldNummerListe mitSpieler = new FeldNummerListe();
		for (TipInfo tipInfo : infos) {
			FeldNummerListe mitSpieler1 = tipInfo.gibMitSpieler();
			if (mitSpieler1 != null) {
				mitSpieler.addAll(mitSpieler1);
			}
		}
		return mitSpieler;
	}

	/**
	 * @return Das Feld mit der (ersten) erkannten Zahl oder null wenn keine Tip-Zahl gefunden wurde.
	 * Da der letzte Tip nur die Zusammenfassung darstellt, wird er nicht berücksichtigt!
	 */
	public FeldNummerMitZahl gibZahlFeld() {
		for (int iTip = 0; iTip < infos.size() - 1; iTip++) {
			TipInfo tipInfo = infos.get(iTip);
			FeldNummerMitZahl zahlFeld = tipInfo.gibZahlFeld();
			if (zahlFeld != null) {
				return zahlFeld;
			}
		}
		return null;
	}

	public ArrayList<InfoSudoku> gibSudokus() {
		ArrayList<InfoSudoku> infoSudokus = new ArrayList<InfoSudoku>();
		int sudokuNummer = 0;
		for (int iTip = 0; iTip < infos.size(); iTip++) {
			TipInfo tipInfo = infos.get(iTip);
			InfoSudoku infoSudoku = tipInfo.gibSudoku();
			if (infoSudoku != null) {
				String sNummer = new Integer(sudokuNummer).toString();
				infoSudoku.setzeTitel(sNummer);
				sudokuNummer++;
				infoSudokus.add(infoSudoku);
			}
		}
		return infoSudokus;
	}

	/**
	 * @return Alle für die Komprimierung interessanten TipInfos. 
	 * Ausser dem letzten "echten" Tip, denn dieser ist nötig für den Tip und dem Tip mit TipZahl.
	 * Der letzte Tip ist kein "echter" Tip, denn er stellt ja nur die Zusammenfassung dar!
	 */
	TipInfo[] gibKontrollInfos() {
		ArrayList<TipInfo> kontrollInfos = new ArrayList<>();

		for (int iTip = 0; iTip < infos.size() - 2; iTip++) {
			TipInfo tipInfo = infos.get(iTip);
			FeldNummerMitZahl zahlFeld = tipInfo.gibZahlFeld();
			if (zahlFeld == null) {
				// Dann bezeichnet dieser Tip Löschzahlen
				kontrollInfos.add(tipInfo);
			}
		}

		TipInfo[] infoArray = new TipInfo[kontrollInfos.size()];
		kontrollInfos.toArray(infoArray);
		return infoArray;
	}

	private boolean existiertZahl() {
		for (TipInfo tipInfo : infos) {
			if (tipInfo.istZahl()) {
				return true;
			}
		}
		return false;
	}

	boolean istKomprimierbar() {
		if (this.infos.isEmpty()) {
			return false;
		}
		if (this.infos.size() < 3) {
			// StartSudoku + 1 Tip ist Minimum
			return false;
		}
		if (!this.existiertZahl()) {
			// Erfolgloser Tip
			return false;
		}
		// if (this.istLogiRex()) {
		// return false;
		// }
		return true;
	}

	boolean istKomprimiertZu(TipBericht basisBericht) {
		if (!basisBericht.istKomprimierbar()) {
			return false;
		}
		FeldNummerMitZahl basisZahl = basisBericht.gibZahlFeld();
		FeldNummerMitZahl dieseZahl = this.gibZahlFeld();
		if (!basisZahl.equals(dieseZahl)) {
			return false;
		}
		if (this.infos.size() >= basisBericht.infos.size()) {
			return false;
		}
		return true;
	}

	public List<TipInfo> gibIgnorierTips(Logik_ID logikID) {
		if (ignorierTips == null) {
			return null;
		}

		List<TipInfo> ignorierTipsDerLogik = new ArrayList<>();
		for (TipInfo tipInfo : ignorierTips) {
			if (tipInfo.gibLogik() == logikID) {
				ignorierTipsDerLogik.add(tipInfo);
			}
		}
		return ignorierTipsDerLogik;
	}

	public boolean istSchwierigkeitLOGIREX() {
		for (TipInfo tipInfo : infos) {
			Logik_ID logik_ID = tipInfo.gibLogik();
			Schwierigkeit schwierigkeit = Schwierigkeit.gibSchwierigkeit(logik_ID);

			if (Schwierigkeit.LOGIREX.equals(schwierigkeit)) {
				return true;
			}
		}
		return false;
	}
}
