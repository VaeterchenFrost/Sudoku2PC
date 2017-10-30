package neu.pool;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.EnumMap;

import logik.Schwierigkeit;
import neu.NeuTyp;
import neu.pool.Pool0.AblageVorschrift;
import tools.Verzeichnis;

class DateiPoolInfo {

	/**
	 * @return Alle Entnahmen aus dem Pool zeitlich geordnet: Auf Index 0 die ältesten 
	 */
	static InfoEntnommene[] gibInfoEntnommene() {
		String verzeichnisName = DateiPoolEntnahmeProtokoll.gibPfadName();
		File fVerzeichnis = new File(verzeichnisName);
		File[] fArray = fVerzeichnis.listFiles();
		Arrays.sort(fArray);

		ArrayList<InfoEntnommene> entnommene = new ArrayList<>();
		for (int iFile = 0; iFile < fArray.length; iFile++) {
			File file = fArray[iFile];
			String fname = file.getName();
			boolean istSudoku = Verzeichnis.istSudoku(fname);
			if (istSudoku) {
				Schwierigkeit schwierigkeit = DateiPoolEntnahmeProtokoll.gibSchwierigkeit(fname);
				long fGroesse = file.length();
				int fLoesungsZeit = DateiPoolEntnahmeProtokoll.gibLoesungsZeit(fname);
				LocalDateTime protokollZeit = DateiPoolEntnahmeProtokoll.gibProtokollZeit(file.getAbsolutePath());
				InfoEntnommene dateiInfo = new InfoEntnommene(schwierigkeit, 1, fGroesse, fLoesungsZeit, fLoesungsZeit,
						protokollZeit, protokollZeit);

				boolean istNeueEntnahme = true;
				if (!entnommene.isEmpty()) {
					InfoEntnommene letzteInfo = entnommene.get(entnommene.size() - 1);
					if (letzteInfo.istGleicheEntnahme(dateiInfo)) {
						istNeueEntnahme = false;
					}
				}

				if (istNeueEntnahme) {
					entnommene.add(dateiInfo);
				} else {
					entnommene.get(entnommene.size() - 1).add(dateiInfo);
				}
			} // if (istSudoku){
		} // for (int iFile = 0; iFile < fArray.length; iFile++) {

		InfoEntnommene[] entnommeneArray = new InfoEntnommene[entnommene.size()];

		entnommene.toArray(entnommeneArray);
		return entnommeneArray;
	}

	static EnumMap<Schwierigkeit, InfoTopf> gibInfoInhalt(Pool0 pool) {
		EnumMap<Schwierigkeit, InfoTopf> poolInfo = new EnumMap<Schwierigkeit, InfoTopf>(Schwierigkeit.class);
		Schwierigkeit[] wieSchwerArray = Schwierigkeit.values();

		for (int iSchwierigkeit = 0; iSchwierigkeit < wieSchwerArray.length; iSchwierigkeit++) {
			Schwierigkeit wieSchwer = wieSchwerArray[iSchwierigkeit];
			NeuTyp typ = new NeuTyp(wieSchwer);
			String verzeichnisName = pool.gibTopfName(typ);
			File fVerzeichnis = new File(verzeichnisName);
			File[] fArray = fVerzeichnis.listFiles();
			AblageVorschrift ablageVorschrift = Pool0.AblageVorschrift.gibVorschrift(wieSchwer);
			InfoTopf schwierigkeitInfo = new InfoTopf(ablageVorschrift);

			for (int iFile = 0; iFile < fArray.length; iFile++) {
				File file = fArray[iFile];
				String fname = file.getName();
				boolean istSudoku = Verzeichnis.istSudoku(fname);
				if (istSudoku) {
					boolean istDoppel = fname.indexOf(" ") > 0;
					long fGroesse = file.length();
					int fLoesungsZeit = Verzeichnis.gibSudokuLoesungsZeit(fname);
					long fModified = file.lastModified();
					InfoTopf dateiInfo = new InfoTopf(ablageVorschrift, 1, istDoppel ? 1 : 0, fGroesse, fLoesungsZeit,
							fLoesungsZeit, fModified, fModified);
					schwierigkeitInfo.add(dateiInfo);
				} // if (istSudoku){
			} // for (int iFile = 0; iFile < fArray.length; iFile++) {

			poolInfo.put(wieSchwer, schwierigkeitInfo);
		} // for (int iSchwierigkeit

		return poolInfo;
	}

	/**
	 * @param anzahlIntervalle
	 * @return Die Zeit-Intervalle über alle Dateien
	 */
	static private AnzahlJeZeit[] gibIntervalleAbgelegte(Pool0 pool, int anzahlIntervalle) {
		// Gesamtbereich der Zeiten des Ablegens ermitteln
		long zeitAlt = Long.MAX_VALUE;
		long zeitJung = Long.MIN_VALUE;
		Schwierigkeit[] wieSchwerArray = Schwierigkeit.values();
		for (int iSchwierigkeit = 0; iSchwierigkeit < wieSchwerArray.length; iSchwierigkeit++) {
			Schwierigkeit wieSchwer = wieSchwerArray[iSchwierigkeit];
			NeuTyp typ = new NeuTyp(wieSchwer);
			String verzeichnisName = pool.gibTopfName(typ);
			File fVerzeichnis = new File(verzeichnisName);
			File[] fArray = fVerzeichnis.listFiles();

			for (int iFile = 0; iFile < fArray.length; iFile++) {
				File file = fArray[iFile];
				String fname = file.getName();
				boolean istSudoku = Verzeichnis.istSudoku(fname);
				if (istSudoku) {
					long fModified = file.lastModified();
					if (fModified < zeitAlt) {
						zeitAlt = fModified;
					}
					if (fModified > zeitJung) {
						zeitJung = fModified;
					}
				}
			}
		}

		// Intervalle ermitteln
		AnzahlJeZeit[] intervalle = new AnzahlJeZeit[anzahlIntervalle];
		long zeitdelta = (zeitJung - zeitAlt) / anzahlIntervalle;

		for (int iIntervall = 0; iIntervall < intervalle.length; iIntervall++) {
			long zeitBis = zeitAlt + (1 + iIntervall) * zeitdelta;
			LocalDateTime datumBis = Info.gibDatum(zeitBis);
			if (iIntervall == intervalle.length - 1) {
				datumBis = datumBis.plusDays(1);
			}

			AnzahlJeZeit haeufigkeit = new AnzahlJeZeit(datumBis, 0);
			intervalle[iIntervall] = haeufigkeit;
		}

		return intervalle;
	}

	/**
	 * @param dateiZeit Datei-Zeit
	 * @param anzahlen In diesem Array der Anzahlen soll eine Anzahl um 1 erhöht werden.
	 * @param intervalle In einem der Intervalle liegt die Datei-Zeit. Sein Index ist derjenige, der bestimmt welche Anzahl erhöht wird.
	 */
	static private void setzeErstellung(long dateiZeit, int[] anzahlen, AnzahlJeZeit[] intervalle) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateiZeit);
		LocalDateTime zeitPunkt = LocalDateTime.of(cal.get(Calendar.YEAR), (cal.get(Calendar.MONTH) + 1),
				cal.get(Calendar.DAY_OF_MONTH), 0, 0);
		// Datumswerte zu 0
		cal.set(0, 0, 0);
		long zeitImTag = cal.getTimeInMillis();
		zeitPunkt.plusNanos(1000 * zeitImTag);

		for (int i = 0; i < intervalle.length; i++) {
			LocalDateTime bis = intervalle[i].bis;
			if (zeitPunkt.isEqual(bis) | zeitPunkt.isBefore(bis)) {
				anzahlen[i]++;
				break;
			}
		}
	}

	static EnumMap<Schwierigkeit, AnzahlJeZeit[]> gibEntstehung(Pool0 pool, int anzahlIntervalle) {
		EnumMap<Schwierigkeit, AnzahlJeZeit[]> poolInfo = new EnumMap<Schwierigkeit, AnzahlJeZeit[]>(
				Schwierigkeit.class);
		AnzahlJeZeit[] intervalle = gibIntervalleAbgelegte(pool, anzahlIntervalle);
		Schwierigkeit[] wieSchwerArray = Schwierigkeit.values();

		for (int iSchwierigkeit = 0; iSchwierigkeit < wieSchwerArray.length; iSchwierigkeit++) {
			Schwierigkeit wieSchwer = wieSchwerArray[iSchwierigkeit];
			NeuTyp typ = new NeuTyp(wieSchwer);
			String verzeichnisName = pool.gibTopfName(typ);
			File fVerzeichnis = new File(verzeichnisName);
			File[] fArray = fVerzeichnis.listFiles();
			int[] anzahlen = new int[anzahlIntervalle];

			for (int iFile = 0; iFile < fArray.length; iFile++) {
				File file = fArray[iFile];
				String fname = file.getName();
				boolean istSudoku = Verzeichnis.istSudoku(fname);
				if (istSudoku) {
					long fModified = file.lastModified();
					setzeErstellung(fModified, anzahlen, intervalle);
				} // if (istSudoku){
			} // for (int iFile = 0; iFile < fArray.length; iFile++) {

			// Häufigkeiten erstellen
			AnzahlJeZeit[] haeufigkeiten = new AnzahlJeZeit[anzahlIntervalle];
			for (int i = 0; i < haeufigkeiten.length; i++) {
				AnzahlJeZeit haeufigkeit = new AnzahlJeZeit(intervalle[i].bis, anzahlen[i]);
				haeufigkeiten[i] = haeufigkeit;
			}

			poolInfo.put(wieSchwer, haeufigkeiten);
		} // for (int iSchwierigkeit

		return poolInfo;
	}

}
