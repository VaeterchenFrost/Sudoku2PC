package neu.pool;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;

import kern.exception.Exc;
import kern.exception.UnerwarteterNeuTyp;
import kern.info.InfoSudoku;
import logik.Schwierigkeit;
import neu.NeuTyp;
import neu.Zufall;
import tools.Verzeichnis;

/**
 * @author heroe
 * Der Pool zur Aufbewahrung von Sudokus in Form von InfoSudoku-Dateien
 */
public class DateiPool implements Pool0 {
	// ================================================================
	private class TypPlusVerzeichnis {
		NeuTyp neuTyp;
		String verzeichnisPfad;

		TypPlusVerzeichnis(NeuTyp neuTyp, String verzeichnisPfad) {
			super();
			this.neuTyp = neuTyp;
			this.verzeichnisPfad = verzeichnisPfad;
		}
	}

	// ================================================================
	private static boolean istSystemOut = false;
	private static String poolVerzeichnisName = "SudokuPool\\";
	// Anzahl vorzuhaltende Sudokus vom Typ VOLL
	private static int anzahlVolle = 10;

	// Anzahl vorzuhaltende Sudokus vom Typ SCHWER für eine Minute
	private static int anzahlJeMinute = 2;

	/**
	 * Säubert das Topf-Verzeichnis entsprechend AblageVorschrift.
	 * Nicht-Sudokus werden ebenfalls gelöscht.
	 * @param topfVerzeichnis 
	 * @param ablageVorschrift
	 */
	static private void clean(String topfVerzeichnis, AblageVorschrift ablageVorschrift) {
		File fVerzeichnis = new File(topfVerzeichnis);
		File[] fArray = fVerzeichnis.listFiles();

		for (int iFile = 0; iFile < fArray.length; iFile++) {
			File file = fArray[iFile];
			String fname = file.getName();
			boolean istSudoku = Verzeichnis.istSudoku(fname);
			if (istSudoku) {
				boolean istDoppel = fname.indexOf(" ") > 0;
				int fLoesungsZeit = Verzeichnis.gibSudokuLoesungsZeit(fname);
				boolean istGeradeZeit = (fLoesungsZeit % 2 == 0);

				switch (ablageVorschrift) {
				case HALB:
					if (!istGeradeZeit | istDoppel) {
						file.delete();
					}
					break;
				case EINFACH:
					if (istDoppel) {
						file.delete();
					}
					break;
				case DOPPEL:
					break;
				}
			} else {
				// Keine Sudoku-Datei:
				file.delete();
			}
		}
	}

	static private String gibDateiName(String verzeichnisName, int loesungsZeit) {
		String fName = gibDateiName(verzeichnisName, Integer.toUnsignedString(loesungsZeit));
		return fName;
	}

	static private String gibDateiName(String verzeichnisName, String zeitName) {
		String fName = String.format("%s%s%s", verzeichnisName, zeitName, InfoSudoku.dateiErweiterung);
		return fName;
	}

	/**
	 * @param verzeichnisName
	 * @param loesungsZeit
	 * @return Den durch eine weitere Nummer ergänzten Dateinamen 
	 * wenn noch nicht anzahlJeMinute Dateien für die Lösungszeit existieren, 
	 * sonst null
	 */
	static private String gibDateiNameAlternative(String verzeichnisName, int loesungsZeit) {
		String dateiName = null;
		for (int i = 1; i < anzahlJeMinute; i++) {
			String zeitName = String.format("%d %d", loesungsZeit, i);
			String fName = gibDateiName(verzeichnisName, zeitName);
			File f = new File(fName);
			if (!f.exists()) {
				dateiName = fName;
				break;
			}
		}
		return dateiName;
	}

	/**
	 * @return alle zu erstellenden Typen
	 */
	static private ArrayList<NeuTyp> gibErstellungsTypen() {
		ArrayList<NeuTyp> neuTypen = new ArrayList<>();

		neuTypen.add(new NeuTyp(Typ.VOLL));
		for (Schwierigkeit typ : Schwierigkeit.values()) {
			neuTypen.add(new NeuTyp(typ));
		}
		return neuTypen;
	}

	static private File gibFile(String verzeichnisName, String[] dateiNamen, NeuTypOption option) {
		int iFile = 0;
		if (option.equals(NeuTypOption.MIX)) {
			iFile = Zufall.gib(dateiNamen.length);
		} else {
			Verzeichnis.sortiereNachLoesungsZeit(dateiNamen);
			switch (option) {
			case KURZ:
				iFile = 0;
				break;
			case NORMAL:
				iFile = dateiNamen.length / 2;
				break;
			case LANGWIERIG:
				iFile = dateiNamen.length - 1;
				break;
			default:
				iFile = 0;
			}
		}
		String dateiName = dateiNamen[iFile];
		File f = new File(verzeichnisName + dateiName);
		return f;
	}

	static private int gibLoesungsZeit(File f) {
		String name = f.getName();
		int zeit = Verzeichnis.gibSudokuLoesungsZeit(name);
		return zeit;
	}

	private static void systemout(NeuTyp neuTyp, boolean istEntnommen, boolean istErfolgreich, String bemerkung) {
		if (!istSystemOut) {
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String uhrzeit = sdf.format(new Date());
		String sErstellt = istEntnommen ? "entnommen" : "reingesetzt";
		String sErfolg = istErfolgreich ? "Ja" : "Nööööööööööööööö";
		String s = String.format("DateiPool.systemout(): %s %s Typ=%s Erfolg=%s (%s)", uhrzeit, sErstellt,
				neuTyp.gibName(), sErfolg, bemerkung);
		System.out.println(s);
	}

	// ================================================================
	/**
	 * Komplette Pfadangaben
	 */
	private ArrayList<TypPlusVerzeichnis> verzeichnisse;

	private Schwierigkeit letzteForderung;

	public DateiPool() throws Exc {
		this.letzteForderung = Schwierigkeit.LEICHT;
		this.verzeichnisse = new ArrayList<>();

		try {
			// Verzeichnisse zum Speichern garantieren
			String aktuellerPfad = Verzeichnis.gibAktuellesVerzeichnis();
			String poolPfadName = Verzeichnis.gibUnterverzeichnis(aktuellerPfad, poolVerzeichnisName, false);
			ArrayList<NeuTyp> alleTypen = DateiPool.gibErstellungsTypen();
			for (NeuTyp neuTyp : alleTypen) {
				String neuTypName = neuTyp.gibName();
				String pfadName = Verzeichnis.gibUnterverzeichnis(poolPfadName, neuTypName, false) + "\\";
				verzeichnisse.add(new TypPlusVerzeichnis(neuTyp, pfadName));
			}
			DateiPoolEntnahmeProtokoll.sichereVerzeichnis(poolPfadName);

			clean();
		} catch (Exception e) {
			throw Exc.ausnahme("DateiPool: " + e.getMessage());
		}
	}

	private void clean() {
		Schwierigkeit[] schwierigkeiten = Schwierigkeit.values();
		for (int i = 0; i < schwierigkeiten.length; i++) {
			Schwierigkeit schwierigkeit = schwierigkeiten[i];
			String topfVerzeichnis = gibTopfName(new NeuTyp(schwierigkeit));
			AblageVorschrift ablageVorschrift = Pool0.AblageVorschrift.gibVorschrift(schwierigkeit);
			clean(topfVerzeichnis, ablageVorschrift);
		}
	}

	public NeuTyp gibForderung() {
		NeuTyp voll = new NeuTyp(NeuTyp.Typ.VOLL);
		String vollVerzeichnisName = gibTopfName(voll);
		File[] fArray = null;
		synchronized (this) {
			File f = new File(vollVerzeichnisName);
			fArray = f.listFiles();
		}
		if (fArray.length < anzahlVolle) {
			return new NeuTyp(NeuTyp.Typ.VOLL);
		}

		// Schwierigkeiten
		letzteForderung = Schwierigkeit.rotiere(letzteForderung);
		return new NeuTyp(letzteForderung);
	}

	private String gibNeuenDateinamenVollTyp() {
		String dateiname = null;
		NeuTyp vollTyp = new NeuTyp(Typ.VOLL);
		String verzeichnisName = gibTopfName(vollTyp);

		for (int i = 0; i < anzahlVolle * 2; i++) {
			String fName = gibDateiName(verzeichnisName, i);
			File f = new File(fName);
			if (!f.exists()) {
				dateiname = fName;
				break;
			}
		}
		return dateiname;
	}

	@Override
	public PoolInfo gibPoolInfo(int anzahlEntstehungsIntervalle) {
		PoolInfo poolInfo = null;
		synchronized (this) {
			final EnumMap<Schwierigkeit, InfoTopf> verfuegbare = DateiPoolInfo.gibInfoInhalt(this);
			final EnumMap<Schwierigkeit, AnzahlJeZeit[]> entstehung = DateiPoolInfo.gibEntstehung(this,
					anzahlEntstehungsIntervalle);
			poolInfo = new PoolInfo(verfuegbare, entstehung);
		}
		return poolInfo;
	}

	@Override
	public PoolInfoEntnommene gibPoolInfoEntnommene() {
		InfoEntnommene[] entnommene = null;
		synchronized (this) {
			entnommene = DateiPoolInfo.gibInfoEntnommene();
		}
		PoolInfoEntnommene poolInfo = new PoolInfoEntnommene(entnommene);
		return poolInfo;
	}

	// =================================================
	/**
	 * @param neuTyp
	 * @return Vorgaben des angeforderten Typs. Leere Vorgaben bei nicht zu erstellendem Typ
	 * @see gibErstellungsTypen()
	 */
	public InfoSudoku gibSudoku(NeuTyp neuTyp, NeuTypOption option) {
		InfoSudoku infoSudoku = null;
		if (new NeuTyp(Typ.LEER).equals(neuTyp)) {
			infoSudoku = new InfoSudoku();
		} else {
			String verzeichnisName = gibTopfName(neuTyp);
			synchronized (this) {
				String[] dateiNamen = Verzeichnis.gibSudokuNamen(verzeichnisName);

				int nFiles = dateiNamen.length;
				if (nFiles > 0) {
					File f = gibFile(verzeichnisName, dateiNamen, option);
					try {
						String dateiName = f.getAbsolutePath();
						infoSudoku = InfoSudoku.lade(dateiName);

						boolean dateiLoeschen = true;
						boolean entnahmeProtokollieren = (neuTyp.gibTyp() == NeuTyp.Typ.SCHWER);
						if (entnahmeProtokollieren) {
							dateiLoeschen = !DateiPoolEntnahmeProtokoll.protokolliere(neuTyp.gibWieSchwer(),
									gibLoesungsZeit(f), f);
						}
						if (dateiLoeschen) {
							f.delete();
						}
					} catch (IOException | Exc e) {
						e.printStackTrace();
					}
				} // if (nFiles > 0)
			} // synchronized

			if (infoSudoku != null) {
				DateiPoolEntnahmeProtokoll.kontrolliereAnzahl();
			}
		} // if (NeuTyp.Typ.LEER.equals(neuTyp))

		String bemerkung = "";
		if (infoSudoku == null) {
			bemerkung = "infoSudoku == null";
		}
		systemout(neuTyp, true, infoSudoku != null, bemerkung);

		return infoSudoku;
	}

	@Override
	public String gibTopfName(NeuTyp neuTyp) {
		for (TypPlusVerzeichnis verzeichnis : verzeichnisse) {
			if (verzeichnis.neuTyp.equals(neuTyp)) {
				return verzeichnis.verzeichnisPfad;
			}
		}
		throw new UnerwarteterNeuTyp(neuTyp.gibName());
	}

	private String gibZustand() {
		String zustandsInfo = new String();
		ArrayList<NeuTyp> alleTypen = DateiPool.gibErstellungsTypen();
		for (NeuTyp neuTyp : alleTypen) {
			String verzeichnisName = gibTopfName(neuTyp);
			File fVerzeichnis = new File(verzeichnisName);
			File[] fArray = fVerzeichnis.listFiles();
			int nFiles = fArray.length;
			String typZustand = String.format(" %s=%d", neuTyp.gibName(), nFiles);
			zustandsInfo += typZustand;
		}
		return zustandsInfo;
	}

	public Boolean setze(NeuTyp neuTyp, InfoSudoku sudoku, int loesungsZeit) {
		if (neuTyp == null) {
			throw new UnerwarteterNeuTyp("null");
		}

		boolean istErstesDerLoesungsZeit = true;
		synchronized (this) {
			String dateiName = null;

			if (new NeuTyp(Typ.VOLL).equals(neuTyp)) {
				dateiName = gibNeuenDateinamenVollTyp();
			} else {
				// Bearbeitung der Töpfe mit Schwierigkeiten
				Schwierigkeit schwierigkeit = neuTyp.gibWieSchwer();

				// Nur gerade Lösungszeiten speichern?
				AblageVorschrift ablageVorschrift = Pool0.AblageVorschrift.gibVorschrift(schwierigkeit);
				if (ablageVorschrift == AblageVorschrift.HALB) {
					if (loesungsZeit % 2 != 0) {
						return null;
					}
				}

				String verzeichnisName = gibTopfName(neuTyp);
				dateiName = gibDateiName(verzeichnisName, loesungsZeit);
				File f = new File(dateiName);
				if (f.exists()) {
					// Je Lösungszeit eine Datei zur Verdopplung speichern?
					if (ablageVorschrift != AblageVorschrift.DOPPEL) {
						return null;
					}

					dateiName = gibDateiNameAlternative(verzeichnisName, loesungsZeit);
					if (dateiName == null) {
						String bemerkung = String
								.format("Dateien für die Lösungszeit %d existieren alle", loesungsZeit);
						systemout(neuTyp, false, false, bemerkung);
						return null;
					}
					istErstesDerLoesungsZeit = false;
				}
			}

			try {
				speichern(dateiName);
				systemout(neuTyp, false, true, "");
				if (istSystemOut) {
					String s = String.format("DateiPool.systemout(): %s", gibZustand());
					System.out.println(s);
				}
			} catch (IOException e) {
				systemout(neuTyp, false, false, e.getMessage());
				e.printStackTrace();
			}
		} // synchronized
		return new Boolean(istErstesDerLoesungsZeit);
	}
}
