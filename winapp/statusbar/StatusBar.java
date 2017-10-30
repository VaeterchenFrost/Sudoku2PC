package winapp.statusbar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import sudoku.bedienung.AnzeigeElement;
import sudoku.bedienung.BedienElement;
import sudoku.bedienung.SudokuBedienung;
import sudoku.kern.protokoll.ProtokollKursorInfo;
import sudoku.schwer.SudokuSchwierigkeit;

@SuppressWarnings("serial")
public class StatusBar extends JStatusBar implements AnzeigeElement, BedienElement {
	private Color cfgTextMitText = Color.BLACK;
	private Color cbgTextMitText = Color.WHITE;
	private Color cbgOhneText = Color.GRAY;

	private Color cfgProblem = Color.WHITE;
	private Color cbgProblem = Color.RED;

	private JLabel labelTexte;
	private JLabel labelVorgaben;
	private JLabel labelFreieFelder;
	private SchwierigkeitsAnzeige cbSchwierigkeit;

	public StatusBar(SudokuBedienung sudoku, Container container) {
		labelTexte = new JLabel("", JLabel.LEFT);
		labelVorgaben = new JLabel("0");
		labelFreieFelder = new JLabel("0");
		cbSchwierigkeit = new SchwierigkeitsAnzeige();

		// Wirkt nicht:
		// labelTexte.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
		// labelKlugheit.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
		// labelVorgaben.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
		setzeBoldFont(labelTexte);
		setzeBoldFont(labelVorgaben);
		setzeBoldFont(labelFreieFelder);
		setzeBoldFont(cbSchwierigkeit);

		labelFreieFelder.setToolTipText("Anzahl der freien Felder im Sudoku");
		labelVorgaben.setToolTipText("Anzahl der Vorgaben im Sudoku");

		// Create the list of secondary components
		List<JComponent> secondaryComponents = new ArrayList<JComponent>();
		secondaryComponents.add(labelFreieFelder);
		secondaryComponents.add(labelVorgaben);
		secondaryComponents.add(cbSchwierigkeit);
		init(labelTexte, secondaryComponents);

		cbgOhneText = gibMainComponentPanel().getBackground();
		sudoku.registriereAnzeigeElement(this);
		sudoku.registriereBedienElement(this);
		container.add(this, BorderLayout.SOUTH);
	}

	private void setzeBoldFont(JComponent component) {
		Font boldFont = new Font(component.getFont().getName(), Font.BOLD, component.getFont().getSize());
		component.setFont(boldFont);
	}

	public void resetInfoAnzeige() {
		labelTexte.setText("");
		labelTexte.setForeground(cfgTextMitText);
		gibMainComponentPanel().setBackground(cbgOhneText);
	}

	public void zeigeInfo(String info) {
		labelTexte.setText(info);
		labelTexte.setForeground(cfgTextMitText);
		gibMainComponentPanel().setBackground(cbgTextMitText);
	}

	public void zeigeProblem(String problem) {
		labelTexte.setText(problem);
		labelTexte.setForeground(cfgProblem);
		gibMainComponentPanel().setBackground(cbgProblem);
	}

	public boolean istInfoAnzeigeLeer() {
		Color c = gibMainComponentPanel().getBackground();
		boolean bo = c.equals(cbgOhneText);
		return bo;
	}

	@Override
	public void zeige(SudokuBedienung sudoku) {
		// Problem
		String problem = sudoku.gibProblem();
		if (problem == null) {
			resetInfoAnzeige();
		} else {
			zeigeProblem(problem);
		}

		// Schwierigkeit
		SudokuSchwierigkeit schwierigkeit = sudoku.gibSchwierigkeit();
		if (schwierigkeit != null) {
			cbSchwierigkeit.setzeSchwierigkeit(schwierigkeit);
		}

		// Einträge-Anzahl anzeigen
		labelFreieFelder.setText(String.valueOf(sudoku.gibAnzahlFreierFelder()));

		// Vorgaben-Anzahl anzeigen
		int nVorgaben = sudoku.gibAnzahlVorgaben();
		labelVorgaben.setText(String.valueOf(nVorgaben));
	}

	@Override
	public void sperre() {
		cbSchwierigkeit.setEnabled(false);
	}

	@Override
	public void entsperre(boolean istVorgabenMin, ProtokollKursorInfo protokollKursorInfo) {
		cbSchwierigkeit.setEnabled(true);
	}

}
