package winapp.tools;

import javax.swing.JOptionPane;

public class AusnahmeBehandlung extends sudoku.tools.AusnahmeBehandlung {

	/**
	 * Erstellung der Ausnahmebehandlung, die die Ausnahme auch in einem Dialog anzeigt.
	 */
	public AusnahmeBehandlung() {
		super();
	}

	/**
	 * Die Ableitung kann die Infos zur Ausnahme anzeigen:
	 * @param throwable
	 * @param textAusnahme
	 * @param logDateiName
	 */
	protected void zeigeAusnahme(Throwable throwable, String textAusnahme, String logDateiName) {
		String text = new String("Es ist eine Ausnahme aufgetreten: Das Programm wird beendet!");
		text += sCR;
		text += sCR;
		text += textAusnahme;
		text += " in";
		text += gibStackInfo(throwable, false);
		text += sCR;
		text += sCR;
		text += "Alle Details in ";
		text += logDateiName;

		JOptionPane.showMessageDialog(null, text, "Sudoku-Ausnahme", JOptionPane.ERROR_MESSAGE);
	}

}
