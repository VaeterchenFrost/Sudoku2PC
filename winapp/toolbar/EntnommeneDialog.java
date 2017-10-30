package winapp.toolbar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import sudoku.bedienung.SudokuBedienung;
import sudoku.kern.info.InfoSudoku;
import sudoku.logik.Schwierigkeit;
import sudoku.neu.pool.DateiPoolEntnahmeProtokoll;
import winapp.EintragsModus;
import winapp.druckinfo.InfoSudokuIcon;
import winapp.druckinfo.MalerEinfach;

//@SuppressWarnings("serial")
public class EntnommeneDialog extends JDialog implements ActionListener, ItemListener, ListSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -951563094252249497L;
	private static Schwierigkeit startSchwierigkeit = Schwierigkeit.SCHWER;

	/**
	 * @author heroe
	 * Diese Klasse gibt der JList die Componenten für die Darstellung der Infosudokus 
	 */
	private class InfoSudoku_ListRenderer extends DefaultListCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = -9015488707977122793L;
		private Dimension iconDimension;

		public InfoSudoku_ListRenderer(Dimension iconDimension) {
			super();
			this.iconDimension = iconDimension;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {

			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			InfoSudoku sudoku = (InfoSudoku) value;
			InfoSudokuIcon icon = new InfoSudokuIcon(sudoku, new MalerEinfach(), iconDimension);

			label.setIcon(icon);
			return label;
		}
	}

	// ==========================================================================

	private JFrame frame;
	private String applikationsTitel;
	private SudokuBedienung sudoku;
	private EintragsModus eintragsModus;
	private JButton buttonLaden;
	private JButton buttonAbbrechen;
	private JComboBox<String> cbWieSchwer;
	private JComboBox<Integer> cbZeit;
	private JList<InfoSudoku> sudokuListe;
	private JScrollPane scrollPane;

	/**
	 * @param frame
	 * @param sudoku In den wird das benannte Sudoku geladen
	 * @param eintragsModus Wird nach dem Laden eines Sudoku auf "Eintrag" gesetzt
	 */
	public EntnommeneDialog(JFrame frame, String applikationsTitel, SudokuBedienung sudoku, EintragsModus eintragsModus) {
		super(frame, true);
		this.frame = frame;
		this.applikationsTitel = applikationsTitel;
		this.sudoku = sudoku;
		this.eintragsModus = eintragsModus;
		this.setTitle("Ein bereits entnommenes Sudoku laden");

		this.setSize(frame.getWidth() / 2, frame.getHeight());
		Point frameLocation = frame.getLocation();
		this.setLocation(frameLocation.x + frame.getWidth() / 4, frameLocation.y);

		// Die Bedienleiste
		JPanel bedienPane = new JPanel(new BorderLayout());
		bedienPane.setBorder(BorderFactory.createLoweredBevelBorder());

		// Zum Abstand halten nach oben
		JPanel werteRahmen = new JPanel(new BorderLayout());
		erstelleAbstand(BorderLayout.NORTH, werteRahmen);

		// Die Werte-Definitions-Leiste
		JPanel wertePane = new JPanel(new BorderLayout());
		{
			cbWieSchwer = new JComboBox<String>(Schwierigkeit.gibAlleNamen());
			cbWieSchwer.setSelectedItem(Schwierigkeit.gibName(startSchwierigkeit));
			cbWieSchwer.addItemListener(this);
			erstelleComboBoxGruppe(" Schwierigkeit: ", cbWieSchwer, BorderLayout.NORTH, wertePane);
		}
		erstelleAbstand(BorderLayout.CENTER, wertePane);
		{
			cbZeit = new JComboBox<Integer>();
			cbZeit.addItemListener(this);
			erstelleComboBoxGruppe(" Lösungszeit: ", cbZeit, BorderLayout.SOUTH, wertePane);
		}
		werteRahmen.add(wertePane, BorderLayout.CENTER);

		bedienPane.add(werteRahmen, BorderLayout.NORTH);
		// Schiebt werteRahmen und die Button-Leiste nach oben/unten:
		bedienPane.add(new JPanel(), BorderLayout.CENTER);

		// Die Button-Leiste
		JPanel buttonPane = new JPanel(new BorderLayout());
		buttonAbbrechen = erstelleButton("Abbrechen", BorderLayout.CENTER, buttonPane);
		buttonLaden = erstelleButton("Laden", BorderLayout.SOUTH, buttonPane);
		buttonLaden.setEnabled(false);
		bedienPane.add(buttonPane, BorderLayout.SOUTH);

		getContentPane().add(bedienPane, BorderLayout.EAST);

		// Die Anzeige der Sudokus
		{
			sudokuListe = new JList<InfoSudoku>();

			int iconHoehe = this.getHeight() / 3; // * 9 / 10;
			int iconBreite = iconHoehe * 9 / 10;
			Dimension iconDimension = new Dimension(iconBreite, iconHoehe);
			sudokuListe.setCellRenderer(new InfoSudoku_ListRenderer(iconDimension));
			sudokuListe.addListSelectionListener(this);
			int hellesGrau = 240;
			sudokuListe.setBackground(new Color(hellesGrau, hellesGrau, hellesGrau));

			// Beides unbedingt meiden: Klappt wohl nicht wegen der Mischung mit BorderLayout
			// sudokuListe.setLayoutOrientation(JList.VERTICAL_WRAP);
			// sudokuListe.setPreferredSize(this.getSize());
			scrollPane = new JScrollPane(sudokuListe);
			scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());

			getContentPane().add(scrollPane);
		}

		ladeZeiten(startSchwierigkeit);
	}

	private void erstelleAbstand(String borderLayout, JPanel zielPane) {
		JPanel wPane = new JPanel();
		JLabel label = new JLabel(" ");
		wPane.add(label);

		zielPane.add(wPane, borderLayout);
	}

	private void erstelleComboBoxGruppe(String titel, JComponent comboBox, String borderLayout, JPanel zielPane) {
		JPanel wPane = new JPanel(new GridLayout());
		JLabel label = new JLabel(titel);
		wPane.add(label);

		wPane.add(comboBox);
		zielPane.add(wPane, borderLayout);
	}

	/** 
	 * Erstellt einen JButton mit dem text, setzt diesen in ein JPanel. 
	 * Dieses wird in buttonPane gesetzt mit borderLayout.
	 * @param text
	 * @param borderLayout
	 * @param buttonPane
	 */
	private JButton erstelleButton(String text, String borderLayout, JPanel buttonPane) {
		JPanel pane = new JPanel();
		JButton button = new JButton(text);
		button.addActionListener(this);
		pane.add(button);
		buttonPane.add(pane, borderLayout);
		return button;
	}

	private void ladeZeiten(Schwierigkeit typ) {
		cbZeit.removeAllItems();
		Integer zeiten[] = DateiPoolEntnahmeProtokoll.gibZeiten(typ);
		if (zeiten != null) {
			for (int i = 0; i < zeiten.length; i++) {
				cbZeit.addItem(zeiten[i]);
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == cbWieSchwer) {
			boolean istSelektiert = e.getStateChange() == ItemEvent.SELECTED;
			if (istSelektiert) {
				String itemText = (String) e.getItem();
				Schwierigkeit typ = Schwierigkeit.gibWert(itemText);

				InfoSudoku sudokus[] = {};
				sudokuListe.setListData(sudokus);

				ladeZeiten(typ);
			}
		} else if (e.getSource() == cbZeit) {
			boolean istSelektiert = e.getStateChange() == ItemEvent.SELECTED;
			if (istSelektiert) {
				String wieSchwerName = (String) cbWieSchwer.getSelectedItem();
				Schwierigkeit wieSchwer = Schwierigkeit.gibWert(wieSchwerName);
				Integer loesungsZeit = (Integer) e.getItem();

				InfoSudoku sudokus[] = DateiPoolEntnahmeProtokoll.gibEntnommene(wieSchwer, loesungsZeit);

				if (sudokus != null) {
					sudokuListe.setListData(sudokus);
					if (sudokus.length == 1) {
						sudokuListe.setSelectedIndex(0);
					}
				} else {
					InfoSudoku keineSudokus[] = {};
					sudokuListe.setListData(keineSudokus);
				}

				JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
				if (scrollBar != null) {
					scrollBar.setValue(0);
				}
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		InfoSudoku infoSudoku = sudokuListe.getSelectedValue();
		buttonLaden.setEnabled(infoSudoku != null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonAbbrechen) {
			setVisible(false);
			dispose();
		} else if (e.getSource() == buttonLaden) {
			InfoSudoku infoSudoku = sudokuListe.getSelectedValue();
			if (infoSudoku != null) {
				String sudokuName = infoSudoku.gibTitel1();
				sudoku.reset(infoSudoku, sudokuName);
				this.eintragsModus.setzeEintragsModus(EintragsModus.Modus.Eintrag);
				if (sudokuName != null) {
					sudokuName = this.applikationsTitel + sudokuName;
				} else {
					sudokuName = this.applikationsTitel;
				}
				this.frame.setTitle(sudokuName);
			}
			setVisible(false);
			dispose();
		}
	}
}
