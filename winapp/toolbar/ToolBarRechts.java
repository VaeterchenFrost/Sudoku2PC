package winapp.toolbar;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import sudoku.bedienung.AnzeigeElement;
import sudoku.bedienung.BedienElement;
import sudoku.bedienung.SudokuBedienung;
import sudoku.kern.exception.EndeDurchAusnahme;
import sudoku.kern.protokoll.Protokoll;
import sudoku.kern.protokoll.Protokoll.Schrittweite;
import sudoku.kern.protokoll.ProtokollKursorInfo;
import sudoku.logik.Klugheit;
import sudoku.logik.Logik_ID;
import sudoku.logik.Logik__Infos;
import sudoku.logik.Schwierigkeit;
import sudoku.logik.SudokuLogik;
import winapp.SudokuFrame;
import winapp.statusbar.StatusBar;
import winapp.tools.ToolTip;

@SuppressWarnings("serial")
public class ToolBarRechts extends ToolBar0 implements BedienElement {

	private class ButtonKlugheitPlusMinus extends JButton implements ActionListener, BedienElement, AnzeigeElement {
		private boolean istPlus;
		private SudokuBedienung sudoku;

		ButtonKlugheitPlusMinus(boolean istPlus, SudokuBedienung sudoku) {
			super(istPlus ? "++" : "--");
			this.istPlus = istPlus;
			this.sudoku = sudoku;
			this.setToolTipText("Setze die Klugheit für eine Schwierigkeit " + (istPlus ? "größer" : "kleiner"));
			this.addActionListener(this);
			sudoku.registriereBedienElement(this);
			sudoku.registriereAnzeigeElement(this);
		}

		public void actionPerformed(ActionEvent e) {
			Klugheit klugheit = sudoku.gibKlugheit();

			Schwierigkeit wieSchwerKlug = Schwierigkeit.gibSchwierigkeit(klugheit);
			Schwierigkeit neuWieSchwerKlug = null;
			if (istPlus) {
				neuWieSchwerKlug = Schwierigkeit.gibSchwerere(wieSchwerKlug);
			} else {
				neuWieSchwerKlug = Schwierigkeit.gibLeichtere(wieSchwerKlug);
			}
			if (neuWieSchwerKlug != null) {
				Klugheit neueKlugkeit = Schwierigkeit.gibKlugheit(neuWieSchwerKlug);
				sudoku.setzeKlugheit(neueKlugkeit);
			}
		}

		@Override
		public void sperre() {
			this.setEnabled(false);
		}

		@Override
		public void entsperre(boolean istVorgabenMin, ProtokollKursorInfo protokollKursorInfo) {
			this.setEnabled(true);
		}

		@Override
		public void zeige(SudokuBedienung sudoku) {
			Klugheit klugheit = sudoku.gibKlugheit();
			Schwierigkeit wieSchwerKlug = Schwierigkeit.gibSchwierigkeit(klugheit);
			Schwierigkeit extremTyp = Schwierigkeit.gibExtremTyp(this.istPlus);
			this.setEnabled(wieSchwerKlug != extremTyp);
		}
	}

	private class LabelWieSchwerKlug extends JLabel implements AnzeigeElement {
		/**
		 * @param text
		 */
		LabelWieSchwerKlug(SudokuBedienung sudoku) {
			super("Haija");
			this.setHorizontalAlignment(CENTER);

			sudoku.registriereAnzeigeElement(this);
			zeige(sudoku);
		}

		@Override
		public void zeige(SudokuBedienung sudoku) {
			Klugheit klugheit = sudoku.gibKlugheit();
			Schwierigkeit wieSchwerKlug = Schwierigkeit.gibSchwierigkeit(klugheit);
			this.setText(Schwierigkeit.gibName(wieSchwerKlug));
		}
	}

	private class CheckBoxLogik extends JCheckBox implements ActionListener, AnzeigeElement, BedienElement {
		private Logik_ID logik;
		private SudokuBedienung sudoku;

		CheckBoxLogik(Logik_ID logik, SudokuBedienung sudoku) {
			super(SudokuLogik.gibNameKurz(logik), sudoku.istSoKlug(logik));
			this.logik = logik;
			this.sudoku = sudoku;

			Logik__Infos beschreibung = SudokuLogik.gibLogikInfos(logik);
			String toolTip = beschreibung.gibKurzName() + ":  " + beschreibung.gibName()
					+ " (Maus-Rechtsklick bringt die Beschreibung)";
			this.setToolTipText(toolTip);

			sudoku.registriereAnzeigeElement(this);
			this.addActionListener(this);
			sudoku.registriereBedienElement(this);
		}

		public void actionPerformed(ActionEvent e) {
			boolean bo = this.isSelected();
			sudoku.setzeLogik(logik, bo);
		}

		private ArrayList<String> gibListe(String[] texte) {
			ArrayList<String> texteListe = new ArrayList<>();
			for (int i = 0; i < texte.length; i++) {
				texteListe.add(texte[i]);
			}
			return texteListe;
		}

		@Override
		protected void processMouseEvent(MouseEvent e) {
			if ((e.getID() == MouseEvent.MOUSE_RELEASED) & (e.getButton() == MouseEvent.BUTTON3)) // rechte Maustaste
			{
				Logik__Infos logikTexte = SudokuLogik.gibLogikInfos(logik);
				ArrayList<String> texteListe = new ArrayList<>();
				texteListe.add(logikTexte.gibName());
				texteListe.add("");
				texteListe.addAll(gibListe(logikTexte.gibWo()));
				texteListe.add("");
				texteListe.addAll(gibListe(logikTexte.gibSituation()));
				texteListe.add("Oder abstakter:");
				texteListe.addAll(gibListe(logikTexte.gibSituationAbstrakt()));
				texteListe.add("");
				texteListe.addAll(gibListe(logikTexte.gibErgebnis()));

				String[] texteArray = new String[texteListe.size()];
				texteListe.toArray(texteArray);
				String beschreibung = ToolTip.gibToolTip(texteArray);
				String text = "Was bedeutet Logik '" + this.getText() + "'?\n\n" + beschreibung;

				JOptionPane.showMessageDialog(SudokuFrame.gibMainFrame(), text, "Logik Erläuterung",
						JOptionPane.PLAIN_MESSAGE);

			}
			super.processMouseEvent(e);
		}

		@Override
		public void zeige(SudokuBedienung sudoku) {
			boolean bo = sudoku.istSoKlug(this.logik);
			this.setSelected(bo);
		}

		@Override
		public void sperre() {
			this.setEnabled(false);
		}

		@Override
		public void entsperre(boolean istVorgabenMin, ProtokollKursorInfo protokollKursorInfo) {
			this.setEnabled(true);
		}
	}

	private JLabel lProtokollSchrittMin;
	private JButton bProtokollMinusAlle;
	private JButton bProtokollMinusEbene;
	private JButton bProtokollMinusSchritt;
	private JLabel lProtokollSchrittAktuell;
	private JButton bProtokollPlusSchritt;
	private JButton bProtokollPlusEbene;
	private JButton bProtokollPlusAlle;
	private JLabel lProtokollSchrittMax;

	public ToolBarRechts(SudokuBedienung aSudoku, StatusBar aStatusBar, Container container) {
		super(aSudoku, aStatusBar);
		container.add(this, BorderLayout.EAST);
		sudoku.registriereBedienElement(this);

		{
			// Klugheit
			{
				String[] toolTip = { "Die zum Lösen des Sudoku eingesetzte Klugheit.",
						"Die geringste nötige Klugheit ist hier nicht einstellbar, denn sie wird stets angewandt:",
						"Ein Feld betrachtet alle Zahlen als möglich, die noch nicht in Zeile/Spalte/Kasten gesetzt sind" };
				erschaffeGruppenTitel(" Klug ", ToolTip.gibToolTip(toolTip));
				this.addSeparator();

				LabelWieSchwerKlug labelWieSchwerKlug = new LabelWieSchwerKlug(sudoku);
				ButtonKlugheitPlusMinus bMinus = new ButtonKlugheitPlusMinus(false, sudoku); // , labelWieSchwerKlug);
				ButtonKlugheitPlusMinus bPlus = new ButtonKlugheitPlusMinus(true, sudoku); // , labelWieSchwerKlug);
				this.add(bMinus);
				this.add(labelWieSchwerKlug);
				this.add(bPlus);
			}

			{
				Logik_ID[] logikArray = Logik_ID.values();

				for (int iLogik = 0; iLogik < logikArray.length; iLogik++) {
					Logik_ID logik = logikArray[iLogik];
					CheckBoxLogik menuItem = new CheckBoxLogik(logik, sudoku);
					this.add(menuItem);
				}
			}
		}

		this.addSeparator();
		this.addSeparator();
		this.addSeparator();
		this.addSeparator();
		this.addSeparator();
		this.addSeparator();

		// Protokoll
		{
			erschaffeGruppenTitel(" Rec ",
					"Im (auch teilweise) gelösten Sudoku kann durch die Vergangenheit gegangen werden.");
			this.addSeparator();

			lProtokollSchrittMax = erschaffeProtokollLabel(gibProtokollText(789, 8));
			lProtokollSchrittMax
					.setToolTipText(gibProtokollToolTip("Nummer des jüngsten (letzten) Protokoll-Vermerkes"));
			this.add(lProtokollSchrittMax);
			this.add(bProtokollPlusAlle = new JButton(">>>"));
			bProtokollPlusAlle.setToolTipText(gibProtokollToolTip("Gehe zum jüngsten (letzten) Protokoll-Vermerk"));
			bProtokollPlusAlle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gehe(Protokoll.Schrittweite.ALLES, true); // vorwaerts)
				}
			});
			this.add(bProtokollPlusEbene = new JButton(" >> "));
			bProtokollPlusEbene.setToolTipText(gibProtokollToolTip("Gehe zum nächsten Versuch"));
			bProtokollPlusEbene.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gehe(Protokoll.Schrittweite.VERSUCH, true); // vorwaerts)
				}
			});
			this.add(bProtokollPlusSchritt = new JButton("  >  "));
			bProtokollPlusSchritt.setToolTipText(gibProtokollToolTip("Gehe einen Protokoll-Schritt vorwärts"));
			bProtokollPlusSchritt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gehe(Protokoll.Schrittweite.EINTRAG, true); // vorwaerts)
				}
			});
			// this.addSeparator();
			lProtokollSchrittAktuell = erschaffeProtokollLabel(gibProtokollText(0, 0));
			lProtokollSchrittAktuell.setToolTipText(gibProtokollToolTip("Nummer des aktuellen Protokoll-Schrittes"));
			this.add(lProtokollSchrittAktuell);
			// this.addSeparator();
			this.add(bProtokollMinusSchritt = new JButton("  <  "));
			bProtokollMinusSchritt.setToolTipText(gibProtokollToolTip("Gehe einen Protokoll-Schritt zurück"));
			bProtokollMinusSchritt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gehe(Protokoll.Schrittweite.EINTRAG, false); // vorwaerts)
				}
			});
			this.add(bProtokollMinusEbene = new JButton(" << "));
			bProtokollMinusEbene.setToolTipText(gibProtokollToolTip("Gehe zum vorigen Versuch"));
			bProtokollMinusEbene.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gehe(Protokoll.Schrittweite.VERSUCH, false); // vorwaerts)
				}
			});
			this.add(bProtokollMinusAlle = new JButton("<<<"));
			bProtokollMinusAlle.setToolTipText(gibProtokollToolTip("Gehe zum ältesten (ersten) Protokoll-Vermerk"));
			bProtokollMinusAlle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gehe(Protokoll.Schrittweite.ALLES, false);
				}
			});
			lProtokollSchrittMin = erschaffeProtokollLabel(gibProtokollText(0, 0));
			lProtokollSchrittMin
					.setToolTipText(gibProtokollToolTip("Nummer des ältesten (ersten) Protokoll-Vermerkes"));
			this.add(lProtokollSchrittMin);
		}
	}

	private String gibProtokollToolTip(String text) {
		String s1 = "Rekorder";
		String s = String.format("<html>%s<br>%s</html>", s1, text);
		return s;
	}

	private void gehe(Schrittweite schrittweite, boolean vorwaerts) {
		try {
			sudoku.gehe(schrittweite, vorwaerts);
		} catch (Exception exception) {
			// statusBar.zeigeProblem(exception.getMessage());
			exception.printStackTrace();
			throw (new EndeDurchAusnahme(exception));
		}
	}

	private JLabel erschaffeProtokollLabel(String text) {
		JLabel l = new JLabel(text);
		// Border border = BorderFactory.createLineBorder(Color.black);
		// l.setBorder(border);
		l.setHorizontalAlignment(CENTER);
		return l;
	}

	private String gibProtokollText(int Schritt, int Ebene) {
		// String s = String.format(" %4d(%1d) ", Schritt, Ebene);
		String s = String.format(" %4d ", Schritt);
		return s;
	}

	@Override
	public void sperre() {
		bProtokollMinusAlle.setEnabled(false);
		bProtokollMinusEbene.setEnabled(false);
		bProtokollMinusSchritt.setEnabled(false);
		bProtokollPlusSchritt.setEnabled(false);
		bProtokollPlusEbene.setEnabled(false);
		bProtokollPlusAlle.setEnabled(false);
	}

	@Override
	public void entsperre(boolean istVorgabenMin, ProtokollKursorInfo protokollKursorInfo) {
		String s = this.gibProtokollText(protokollKursorInfo.gibEintrag().min, protokollKursorInfo.gibEbene().min);
		lProtokollSchrittMin.setText(s);
		s = this.gibProtokollText(protokollKursorInfo.gibKursorEintrag(), protokollKursorInfo.gibKursorEbene());
		lProtokollSchrittAktuell.setText(s);
		s = this.gibProtokollText(protokollKursorInfo.gibEintrag().max, protokollKursorInfo.gibEbene().max);
		lProtokollSchrittMax.setText(s);

		{
			boolean istErlaubtMinus = protokollKursorInfo.istRueckwaertsMoeglich();
			bProtokollMinusAlle.setEnabled(istErlaubtMinus);
			bProtokollMinusEbene.setEnabled(istErlaubtMinus);
			bProtokollMinusSchritt.setEnabled(istErlaubtMinus);
		}

		boolean istErlaubtPlus = protokollKursorInfo.istVorwaertsMoeglich();
		bProtokollPlusSchritt.setEnabled(istErlaubtPlus);
		bProtokollPlusEbene.setEnabled(istErlaubtPlus);
		bProtokollPlusAlle.setEnabled(istErlaubtPlus);
	}

}
//
// toolBar.addSeparator();
// JLabel label = new JLabel("Font");
// toolBar.add(label);
//
// toolBar.addSeparator();
// JComboBox combo = new JComboBox(fonts);
// combo.addActionListener(new ActionListener() {
// public void actionPerformed(ActionEvent e) {
// try {
// pane.getStyledDocument().insertString(
// 0,
// "Font ["
// + ((JComboBox) e.getSource())
// .getSelectedItem() + "] chosen!\n",
// null);
// } catch (Exception ex) {
// ex.printStackTrace();
// }
// }
// });
// toolBar.add(combo);
