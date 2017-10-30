package neu.pool;

import kern.info.InfoSudoku;
import logik.Schwierigkeit;
import neu.NeuTyp;

public interface Pool0 {
	/**
	 * Die Vorschrift zum Thema: Wie werden die Sudokus im Pool abgelegt?
	 */
	public enum AblageVorschrift {
		/**
		 * Es werden nur Sudokus mit ganzzahligen Lösungszeiten abgelegt. Es gibt keine Verdopplung. 
		 */
		HALB,
		/**
		 * Es werden Sudokus aller Lösungszeiten (einmal) abgelegt. 
		 */
		EINFACH,
		/**
		 * Je Lösungszeit werden maximal 2 Sudokus abgelegt.
		 */
		DOPPEL;
		/**
		 * @param schwierigkeit
		 * @return Die AblageVorschrift für die Schwierigkeit
		 * 			Diese Vorschrift ergibt sich aus der Menge an entstehenden Sudokus für eine Lösungszeit je Programmlaufzeit. 
		 */
		static public AblageVorschrift gibVorschrift(Schwierigkeit schwierigkeit) {
			switch (schwierigkeit) {
			case LEICHT:
				return AblageVorschrift.DOPPEL;
			case MITTEL:
				return AblageVorschrift.DOPPEL;
			case SCHWER:
				return AblageVorschrift.DOPPEL;
			case KNACKIG:
				return AblageVorschrift.EINFACH;
			case EXPERTE:
				return AblageVorschrift.DOPPEL;
			case LOGIREX:
				return AblageVorschrift.HALB;
			}
			// Falls die Schwierigkeit geändert, aber hier nicht angepasst wurde, läuft dafür hier dieser Standard:
			return AblageVorschrift.EINFACH;
		}
	};

	/**
	 * @param neuTyp Hier werden nicht angefordert: LEER oder VORLAGE.
	 * @param option
	 * @return Sudoku des angeforderten Typs 
	 * 	oder null wenn ein solches nicht zur Verfügung steht.
	 */
	public InfoSudoku gibSudoku(NeuTyp neuTyp, NeuTypOption option);

	/**
	 * @return Angeforderter Typ wenn ein Sudoku benötigt wird oder null.
	 * 		Hier kommen nicht zurück: LEER oder VORLAGE.
	 */
	public NeuTyp gibForderung();

	/**
	 * Setzt in den Pool das Sudoku zur Aufbewahrung
	 * @param neuTyp
	 * @param sudoku
	 * @return 
	 * 	- null wenn das Sudoku nicht im Pool abgelegt wurde.
	 *  - true wenn das Sudoku als 1. Sudoku mit dieser Lösungszeit im Pool abgelegt wurde.
	 *  - false wenn das Sudoku im Pool abgelegt wurde, aber NICHT als 1. Sudoku mit dieser Lösungszeit.  
	 */
	public Boolean setze(NeuTyp neuTyp, InfoSudoku sudoku, int loesungsZeit);

	/**
	 * @param anzahlEntstehungsIntervalle Die Anzahl der Zeit-Intervalle, für die eine Sudokuanzahl genannt werden soll.
	 * @return Info zum aktuellen Zustand des Pools
	 */
	public PoolInfo gibPoolInfo(int anzahlEntstehungsIntervalle);

	/**
	 * @param anzahlEntstehungsIntervalle Die Anzahl der Zeit-Intervalle, für die eine Sudokuanzahl genannt werden soll.
	 * @return Info zum aktuellen Zustand des Pools
	 */
	public PoolInfoEntnommene gibPoolInfoEntnommene();

	/**
	 * @param neuTyp
	 * @return Name des dem neuTyp entsprechenden Topfes im Pool.
	 */
	public String gibTopfName(NeuTyp neuTyp);
}
