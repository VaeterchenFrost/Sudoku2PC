package neu.pool;

import java.time.LocalDateTime;

import logik.Schwierigkeit;

/**
 * @author heroe
 * Die Info zu einer oder mehrerer Entnahmen aus dem Sudoku-Pool
 */
public class InfoEntnommene extends Info {
	public final Schwierigkeit schwierigkeit;

	public InfoEntnommene(Schwierigkeit schwierigkeit) {
		super();
		this.schwierigkeit = schwierigkeit;
	}

	public InfoEntnommene(Schwierigkeit schwierigkeit, int anzahl, long groesse, Integer leichtestes,
			Integer schwerstes, LocalDateTime aeltestes, LocalDateTime juengstes) {
		super(anzahl, groesse, leichtestes, schwerstes, aeltestes, juengstes);
		this.schwierigkeit = schwierigkeit;
	}

	public boolean istGleicheEntnahme(InfoEntnommene info) {
		boolean gleich = this.schwierigkeit.equals(info.schwierigkeit)
				& this.gibAeltestes().isEqual(info.gibAeltestes()) & this.gibJuengstes().isEqual(info.gibJuengstes());
		return gleich;
	}

}
