package sudoku.neu;

import sudoku.kern.exception.Exc;
import sudoku.kern.info.InfoSudoku;
import sudoku.neu.pool.DateiPool;
import sudoku.neu.pool.NeuTypOption;
import sudoku.neu.pool.Pool0;
import sudoku.neu.pool.PoolInfo;
import sudoku.neu.pool.PoolInfoEntnommene;

public class SudokuPool {
	static GeneratorStatistik generatorStatistik = null;

	static public void setzeGeneratorStatistik(GeneratorStatistik sudokuGeneratorStatistik) {
		generatorStatistik = sudokuGeneratorStatistik;
	}

	// ============================================
	private Pool0 pool;
	private GeneratorThread generatorThread;

	/**
	 * @param externeAusnahmeBehandlung falls != null wird im internen Thread 
	 * 				diese Ausnahmebehandlung für nicht gefangene Ausnahmen eingeklinkt, 
	 * 				ansonsten die Standardbehandlung des genannten Typs. 
	 * @throws Exc
	 */
	public SudokuPool(sudoku.tools.AusnahmeBehandlung externeAusnahmeBehandlung) throws Exc {
		// this.pool = new Pool();
		this.pool = new DateiPool();
		this.generatorThread = new GeneratorThread(pool, externeAusnahmeBehandlung);
	}

	/**
	 * Setzt in den Pool das Sudoku zur Aufbewahrung
	 * @param neuTyp
	 * @param sudoku
	 * @param loesungsZeit
	 */
	public void setze(NeuTyp neuTyp, InfoSudoku sudoku, int loesungsZeit) {
		this.pool.setze(neuTyp, sudoku, loesungsZeit);
	}

	/**
	 * @param neuTyp
	 * @return Sudoku oder null wenn keines des angeforderten Typs zur Verfügung steht
	 */
	public InfoSudoku gibSudoku(NeuTyp neuTyp, NeuTypOption option) {
		InfoSudoku sudoku = pool.gibSudoku(neuTyp, option);
		return sudoku;
	}

	@Override
	protected void finalize() throws Throwable {
		// Thread beenden!
		this.generatorThread.interrupt();
		super.finalize();
	}

	public PoolInfo gibPoolInfo(int anzahlEntstehungsIntervalle) {
		PoolInfo poolInfo = this.pool.gibPoolInfo(anzahlEntstehungsIntervalle);
		return poolInfo;
	}

	public PoolInfoEntnommene gibPoolInfoEntnommene() {
		PoolInfoEntnommene poolInfo = this.pool.gibPoolInfoEntnommene();
		return poolInfo;
	}

	public String gibTopfName(NeuTyp neuTyp) {
		String s = this.pool.gibTopfName(neuTyp);
		return s;
	}
}
