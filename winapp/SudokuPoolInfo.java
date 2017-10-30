package winapp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Transient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

import sudoku.logik.Schwierigkeit;
import sudoku.neu.pool.AnzahlJeZeit;
import sudoku.neu.pool.InfoEntnommene;
import sudoku.neu.pool.InfoTopf;
import sudoku.neu.pool.PoolInfo;
import sudoku.neu.pool.PoolInfoEntnommene;
import sudoku.neu.pool.PoolInfoEntnommene.TagesEntnahme;
import winapp.tools.MaximaleFensterGroesse;

@SuppressWarnings("serial")
public class SudokuPoolInfo extends JTable {
	static private int spaltenAnzahl = 10;

	static private String sSumme = "Summe";

	static private String[] gibAnzahlenTexte(String name, int[] anzahlen) {
		String[] texte = new String[spaltenAnzahl];
		texte[0] = name;
		for (int i = 0; i < anzahlen.length; i++) {
			int anzahl = anzahlen[i];
			texte[i + 1] = anzahl == 0 ? "" : gibSpaltenText(new Integer(anzahl).toString(), 3);
		}
		return texte;
	}

	/**
	 * @return Die Anzahl der Zeit-Intervalle, für die eine Sudokuanzahl genannt werden soll.
	 */
	static public int gibAnzahlEntstehungsIntervalle() {
		return spaltenAnzahl - 1;
	}

	static private String gibDatumText(LocalDateTime datum) {
		String sDatum = "";
		if (datum != null) {
			DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yy");
			sDatum = datum.format(df);
		}
		return sDatum;
	}

	static private String gibDatumTextEntnahme(LocalDateTime datum, boolean istObereGrenze) {
		String sDatum = gibDatumText(datum);
		sDatum = String.format("%s %s", istObereGrenze ? "Bis" : "Am", sDatum);
		return sDatum;
	}

	static private String[] gibUeberschrift(String titel) {
		String[] texte = new String[spaltenAnzahl];
		texte[0] = titel;
		for (int iText = 1; iText < texte.length; iText++) {
			texte[iText] = "";
		}
		return texte;
	}

	static private String gibSpaltenText(String text, int laenge) {
		String s = new String(text);
		while (s.length() < laenge) {
			s = "0" + s;
		}
		return s;
	}

	static private String[] gibSpaltenUeberschriftenZustand(String titel) {
		String[] texte = new String[] { titel, "Anzahl", "Doppel", "% voll1", "% voll2", "KB groß", "Leicht", "Schwer",
				"Ältestes", "Jüngstes" };
		return texte;
	}

	static private String[] gibSpaltenUeberschriftenEntnahme(TagesEntnahme[] entnahme, int anzahlAllerTage) {
		String[] texte = new String[spaltenAnzahl];
		for (int i = 0; i < texte.length; i++) {
			texte[i] = "";
		}

		if (entnahme.length > 0) {
			boolean istObereGrenze = entnahme.length < anzahlAllerTage;
			texte[1] = gibDatumTextEntnahme(entnahme[0].datum, istObereGrenze);
			for (int iEntnahme = 1; iEntnahme < entnahme.length; iEntnahme++) {
				texte[iEntnahme + 1] = gibDatumTextEntnahme(entnahme[iEntnahme].datum, false);
			}
		}

		return texte;
	}

	static private ArrayList<String[]> gibTexte(final EnumMap<Schwierigkeit, InfoTopf> poolInfo,
			boolean mitFuellstand) {
		ArrayList<String[]> texte = new ArrayList<>();

		Set<Entry<Schwierigkeit, InfoTopf>> entrySet = poolInfo.entrySet();
		Iterator<Entry<Schwierigkeit, InfoTopf>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry<Schwierigkeit, InfoTopf> next = iterator.next();
			Schwierigkeit schwierigkeit = next.getKey();
			String[] topfTexte = gibTopfTexte(Schwierigkeit.gibName(schwierigkeit), next.getValue(), mitFuellstand);
			texte.add(topfTexte);
		}
		return texte;
	}

	static private String[][] gibTexte(PoolInfo poolInfo) {
		ArrayList<String[]> verfuegbare = gibTexte(poolInfo.inhalt, true);
		InfoTopf summeVerfuegbare = PoolInfo.gibSumme(poolInfo.inhalt.values());
		String[] textSummeVerfuegbare = gibTopfTexte(sSumme, summeVerfuegbare, false);

		ArrayList<String[]> alleTexte = new ArrayList<>();

		alleTexte.add(gibSpaltenUeberschriftenZustand("Im Pool"));
		alleTexte.addAll(verfuegbare);
		alleTexte.add(textSummeVerfuegbare);
		alleTexte.add(gibUeberschrift(""));

		ArrayList<String[]> entstehung = gibTexteEntstehung(poolInfo.entstehung);
		alleTexte.addAll(entstehung);

		String[][] texteArray = new String[alleTexte.size()][];
		alleTexte.toArray(texteArray);
		return texteArray;
	}

	static private String[] gibTexte(TagesEntnahme[] infoJeTag, Schwierigkeit schwierigkeit) {
		String[] texte = new String[spaltenAnzahl];
		for (int i = 0; i < texte.length; i++) {
			texte[i] = "";
		}

		texte[0] = Schwierigkeit.gibName(schwierigkeit);
		for (int iInfo = 0; iInfo < infoJeTag.length; iInfo++) {
			TagesEntnahme tagesEntnahme = infoJeTag[iInfo];
			InfoEntnommene infoEntnommene = tagesEntnahme.entnahme.get(schwierigkeit);
			String text = "";
			if (infoEntnommene != null) {
				text = String.format("%02d (%d-%d)", infoEntnommene.gibAnzahl(), infoEntnommene.gibLeichtestes(),
						infoEntnommene.gibSchwerstes());
			}
			texte[iInfo + 1] = text;
		}
		return texte;
	}

	static private String[][] gibTexte(PoolInfoEntnommene poolInfo) {
		TagesEntnahme[] alleTage = poolInfo.gibTageweise();
		TagesEntnahme[] komprimiertTage = PoolInfoEntnommene.gibTageweise(alleTage,
				SudokuPoolInfo.gibAnzahlEntstehungsIntervalle());

		ArrayList<String[]> entnahmeTexte = new ArrayList<>();
		Schwierigkeit[] schwierigkeiten = Schwierigkeit.values();
		for (int iSchwierigkeit = 0; iSchwierigkeit < schwierigkeiten.length; iSchwierigkeit++) {
			Schwierigkeit schwierigkeit = schwierigkeiten[iSchwierigkeit];
			String[] schwierigkeitTexte = gibTexte(komprimiertTage, schwierigkeit);
			entnahmeTexte.add(schwierigkeitTexte);
		}

		ArrayList<String[]> alleTexte = new ArrayList<>();

		alleTexte.add(gibSpaltenUeberschriftenEntnahme(komprimiertTage, alleTage.length));
		alleTexte.addAll(entnahmeTexte);

		String[][] texteArray = new String[alleTexte.size()][];
		alleTexte.toArray(texteArray);
		return texteArray;
	}

	static private ArrayList<String[]> gibTexteEntstehung(final EnumMap<Schwierigkeit, AnzahlJeZeit[]> entstehung) {
		ArrayList<String[]> texte = new ArrayList<>();

		// Die Überschrift der zeiten
		String[] texteBis = null;

		Set<Entry<Schwierigkeit, AnzahlJeZeit[]>> entrySet = entstehung.entrySet();
		Iterator<Entry<Schwierigkeit, AnzahlJeZeit[]>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry<Schwierigkeit, AnzahlJeZeit[]> next = iterator.next();
			Schwierigkeit schwierigkeit = next.getKey();
			AnzahlJeZeit[] haeufigkeiten = next.getValue();
			if (texteBis == null) {
				// Überschriften
				texteBis = new String[1 + haeufigkeiten.length];
				texteBis[0] = "Erzeugt bis";
				for (int iHaeufigkeit = 0; iHaeufigkeit < haeufigkeiten.length; iHaeufigkeit++) {
					AnzahlJeZeit haeufigkeit = haeufigkeiten[iHaeufigkeit];
					DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yy");
					texteBis[iHaeufigkeit + 1] = haeufigkeit.bis.format(df);
				}
			}

			int[] anzahlen = AnzahlJeZeit.gibAnzahlen(haeufigkeiten);
			String[] anzahlTexte = gibAnzahlenTexte(Schwierigkeit.gibName(schwierigkeit), anzahlen);
			texte.add(anzahlTexte);
		}

		// Die Überschriften vorne ran
		texte.add(0, texteBis);
		texte.add(0, gibUeberschrift("Erzeugung"));

		// Die Summe
		Collection<AnzahlJeZeit[]> haeufigkeiten = entstehung.values();
		Iterator<AnzahlJeZeit[]> iterator2 = haeufigkeiten.iterator();
		int[] summe = null;
		while (iterator2.hasNext()) {
			AnzahlJeZeit[] next = iterator2.next();

			if (summe == null) {
				summe = new int[next.length];
				for (int i = 0; i < next.length; i++) {
					AnzahlJeZeit haeufigkeit = next[i];
					summe[i] = haeufigkeit.anzahl;
				}
			} else {
				for (int i = 0; i < next.length; i++) {
					AnzahlJeZeit haeufigkeit = next[i];
					summe[i] += haeufigkeit.anzahl;
				}
			}
		}

		String[] summeTexte = gibAnzahlenTexte(sSumme, summe);
		texte.add(summeTexte);

		return texte;
	}

	static private String[] gibTopfTexte(String name, InfoTopf topfInfo, boolean mitFuellstand) {
		String[] texte = new String[spaltenAnzahl];
		int i = 0;
		texte[i++] = name;
		texte[i++] = topfInfo.gibAnzahl() == 0 ? "" : gibSpaltenText(new Integer(topfInfo.gibAnzahl()).toString(), 3);
		texte[i++] = topfInfo.gibAnzahlDoppel() == 0 ? ""
				: gibSpaltenText(new Integer(topfInfo.gibAnzahlDoppel()).toString(), 3);
		if (mitFuellstand) {
			texte[i++] = topfInfo.gibFuellstand1() == 0 ? ""
					: gibSpaltenText(new Integer(topfInfo.gibFuellstand1()).toString(), 2);
			texte[i++] = topfInfo.gibFuellstand2() == 0 ? ""
					: gibSpaltenText(new Integer(topfInfo.gibFuellstand2()).toString(), 2);
		} else {
			texte[i++] = "";
			texte[i++] = "";
		}
		texte[i++] = topfInfo.gibGroesse() == 0 ? ""
				: gibSpaltenText(new Long(Math.round(topfInfo.gibGroesse() / 1000.0)).toString(), 3);
		texte[i++] = topfInfo.gibLeichtestes() == null ? "" : gibSpaltenText(topfInfo.gibLeichtestes().toString(), 2);
		texte[i++] = topfInfo.gibSchwerstes() == null ? "" : gibSpaltenText(topfInfo.gibSchwerstes().toString(), 3);
		texte[i++] = gibDatumText(topfInfo.gibAeltestes());
		texte[i++] = gibDatumText(topfInfo.gibJuengstes());
		return texte;
	}

	static private void zeige(String titel, SudokuPoolInfo table) {
		// Diese Variante bringt größeren Text nicht komplett auf's Trapez:
		// JOptionPane.showMessageDialog(SudokuFrame.gibMainFrame(), table, "Sudoku-Pool-Zustand",
		// JOptionPane.PLAIN_MESSAGE);

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
		String zeitString = now.format(df);

		final JDialog dlg = new JDialog((JFrame) null, titel + " " + zeitString, false);
		dlg.add(table);

		JButton buttonOK = new JButton("OK");
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dlg.dispose();
			}
		};
		buttonOK.addActionListener((ActionListener) actionListener);
		JPanel buttonPane = new JPanel();
		buttonPane.add(buttonOK);
		dlg.add(buttonPane, BorderLayout.SOUTH);
		dlg.pack();

		Rectangle maxRectangle = MaximaleFensterGroesse.gibMaxGroesse();
		int dlgHeight = table.getRowCount() * table.getRowHeight() + 5 * buttonOK.getHeight();
		Dimension dlgGroesse = new Dimension((maxRectangle.width * 4) / 5, dlgHeight);
		dlg.setLocation(
				new Point((maxRectangle.width - dlgGroesse.width) / 2, (maxRectangle.height - dlgGroesse.height)));
		dlg.setSize(dlgGroesse);

		dlg.setVisible(true);
	}

	static public void zeige(PoolInfo poolInfo) {
		String[][] texteArray = gibTexte(poolInfo);
		String[] spaltenUeberschriften = gibSpaltenUeberschriftenZustand("");
		SudokuPoolInfo table = new SudokuPoolInfo(texteArray, spaltenUeberschriften);
		zeige("Sudoku-Pool-Zustand", table);
	}

	static public void zeige(PoolInfoEntnommene poolInfoEntnommene) {
		String[][] texteArray = gibTexte(poolInfoEntnommene);
		SudokuPoolInfo table = new SudokuPoolInfo(texteArray, texteArray[0]);
		zeige("Sudoku-Pool-Entnahme", table);
	}

	// =========================================================
	private SudokuPoolInfo(String[][] texteArray, String[] spaltenUeberschriften) {
		super(texteArray, spaltenUeberschriften);
	}

	@Override
	@Transient
	public Font getFont() {
		Font font = super.getFont();
		if (font == null) {
			return null;
		}
		// label.setFont(new Font("Arial", Font.BOLD, 18));
		float fontGroesse = font.getSize2D();
		fontGroesse *= 2;
		Font fNeu = font.deriveFont((float) fontGroesse);
		return fNeu;
	}

	@Override
	public int getRowHeight() {
		int h = super.getRowHeight();
		h *= 2;
		return h;
	}

}
