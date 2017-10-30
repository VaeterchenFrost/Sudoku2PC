package winapp.toolbar;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import sudoku.bedienung.BedienElement;
import sudoku.bedienung.SudokuBedienung;
import sudoku.bild.Bild;
import sudoku.bild.SudokuSucher;
import sudoku.kern.exception.Exc;
import sudoku.kern.info.InfoSudoku;
import sudoku.kern.protokoll.ProtokollKursorInfo;
import sudoku.neu.NeuTyp;
import sudoku.neu.pool.NeuTypOption;
import sudoku.neu.pool.PoolInfo;
import sudoku.neu.pool.PoolInfoEntnommene;
import sudoku.schwer.SudokuSchwierigkeit;
import winapp.EintragsModus;
import winapp.SudokuFrame;
import winapp.SudokuPoolInfo;
import winapp.druckinfo.InfoSudokuDruck;
import winapp.druckinfo.MalerStandard;
import winapp.statusbar.StatusBar;
import winapp.tools.FortschrittAnzeige;
import winapp.tools.ToolTip;

@SuppressWarnings("serial")
public class ToolBarGruppeSudoku {

	// Basis-Klasse
	// ---------------------------------------------------------------------------------------------------
	private class ButtonSudoku extends JButton implements BedienElement {
		protected JFrame frame;
		protected String applikationsTitel;
		protected EintragsModus eintragsModus;
		protected SudokuBedienung sudoku;
		protected StatusBar statusBar;

		public ButtonSudoku(String titel, String toolTip, JFrame frame, String applikationsTitel,
				EintragsModus eintragsModus, SudokuBedienung sudoku, StatusBar statusBar) {
			super(titel);
			this.setToolTipText(toolTip);
			this.frame = frame;
			this.applikationsTitel = applikationsTitel;
			this.eintragsModus = eintragsModus;
			this.sudoku = sudoku;
			this.statusBar = statusBar;
			this.sudoku.registriereBedienElement(this);
		}

		@Override
		public void sperre() {
			this.setEnabled(false);
			// this.repaint();
		}

		@Override
		public void entsperre(boolean istVorgabenMin, ProtokollKursorInfo protokollKursorInfo) {
			this.setEnabled(true);
		}
	}

	// Entnommenes
	// ---------------------------------------------------------------------------------------------------
	public class ButtonEntnommenes extends ButtonSudoku implements ActionListener {
		// private DateiDialog dialog;

		public ButtonEntnommenes(JFrame frame, String applikationsTitel, EintragsModus eintragsModus,
				SudokuBedienung sudoku, StatusBar statusBar) {
			super("Entnommen...", "Ein zuvor entnommenes Sudoku laden", frame, applikationsTitel, eintragsModus, sudoku,
					statusBar);
			this.addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			EntnommeneDialog dialog = new EntnommeneDialog(frame, applikationsTitel, sudoku, eintragsModus);
			dialog.setVisible(true);
		}
	}

	// Neu
	// ---------------------------------------------------------------------------------------------------
	public class ButtonNeu extends ButtonSudoku implements ActionListener {
		// Das (ständig neu erstellte) PopupMenü
		private PopupMenuNeu popupMenu;
		private DateiDialog dialog;

		public ButtonNeu(JFrame frame, String applikationsTitel, EintragsModus eintragsModus, SudokuBedienung sudoku,
				StatusBar statusBar) {
			super("Neu...", ToolTip.gibToolTip(new String[] { "Neues Sudoku, neues Glück" }), frame, applikationsTitel,
					eintragsModus, sudoku, statusBar);
			popupMenu = new PopupMenuNeu(this);
			dialog = new DateiDialog(frame, false);
			this.addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			// Typ über PopupMenü auswählen lassen
			int posX = this.getWidth() / 2;
			int posY = this.getHeight() / 2;
			popupMenu.show(this, posX, posY);
		}

		/**
		 * Realisiert die Sonderfunktion des Maus-Rechtsklick auf das PopupMenü.
		 * @param neuTyp 
		 */
		void sonderFunktion(NeuTyp neuTyp) {
			switch (neuTyp.gibTyp()) {
			case LEER:
				PoolInfoEntnommene poolInfoEntnommene = this.sudoku.gibSudokuPoolInfoEntnommene();
				SudokuPoolInfo.zeige(poolInfoEntnommene);
				break;
			case VOLL:
				PoolInfo poolInfo = this.sudoku.gibSudokuPoolInfo(SudokuPoolInfo.gibAnzahlEntstehungsIntervalle());
				SudokuPoolInfo.zeige(poolInfo);
				break;
			case SCHWER:
				FortschrittAnzeige fortschrittAnzeige = new FortschrittAnzeige(frame);
				sudoku.schreibeSchwierigkeiten(neuTyp.gibWieSchwer(), fortschrittAnzeige);
				break;
			default:
				;
			}
		}

		void erstelleNeuesSudoku(NeuTyp neuTyp) {
			frame.setTitle(applikationsTitel);

			InfoSudoku vorlage = null;
			FortschrittAnzeige fortschrittAnzeige = null;
			if (neuTyp.gibTyp() == NeuTyp.Typ.VORLAGE) {
				fortschrittAnzeige = new FortschrittAnzeige(frame);
				dialog.setVisible(true);
				String fn = dialog.getFile();
				if (fn == null) {
					return;
				} else {
					try {
						vorlage = InfoSudoku.lade(dialog.getDirectory() + fn);
						vorlage.setzeTitel(fn);
					} catch (IOException | Exc e1) {
						e1.printStackTrace();
						statusBar.zeigeProblem(e1.getMessage());
						return;
					}
				} // if (fn != null)
			} // if (neuTyp.gibTyp() == NeuTyp.Typ.VORLAGE){

			InfoSudoku neuesSudoku = sudoku.gibNeues(neuTyp, NeuTypOption.MIX, vorlage, fortschrittAnzeige);
			if (neuesSudoku != null) {
				if (neuesSudoku.gibTitel1() != null) {
					frame.setTitle(applikationsTitel + " " + neuesSudoku.gibTitel1());
				}
				sudoku.reset(neuesSudoku, "");
				if (neuTyp.gibTyp() != NeuTyp.Typ.LEER) {
					// if ((neuTyp.gibTyp() == NeuTyp.Typ.SCHWER) || (neuTyp.gibTyp() == NeuTyp.Typ.VORLAGE)){
					eintragsModus.setzeEintragsModus(EintragsModus.Modus.Eintrag);
				} else {
					eintragsModus.setzeEintragsModus(EintragsModus.Modus.Vorgabe);
				}
			} else {
				String sProblem = String.format("Z.Zt. ist kein Sudoku des Typs '%s' verfügbar. Bitte kurz warten.",
						neuTyp.gibName());
				JOptionPane.showMessageDialog(SudokuFrame.gibMainFrame(), sProblem, "Neues Sudoku",
						JOptionPane.PLAIN_MESSAGE);
			}
		}

	} // private class ButtonDateiLaden

	// Datei laden / speichern
	// ---------------------------------------------------------------------------------------------------
	private abstract class ButtonDatei extends ButtonSudoku implements ActionListener {
		protected DateiDialog dialog;

		/**
		 * Führt die Aktion auf die Datei aus
		 * @param nameVerzeichnis direkt verkettbar mit nameDatei
		 * @param nameDatei direkt verkettbar mit nameVerzeichnis
		 * @return Sudoku in Textform wenn die Aktion erfolgreich ausgeführt wurde oder null
		 */
		protected abstract void aktion(String nameVerzeichnis, String nameDatei);

		public ButtonDatei(boolean istSpeichern, JFrame frame, String applikationsTitel, EintragsModus eintragsModus,
				SudokuBedienung sudoku, StatusBar statusBar) {
			super(istSpeichern ? "Speichern..." : "Laden...",
					istSpeichern ? "Sudoku in einer Sudoku-Text-Datei" + InfoSudoku.dateiErweiterung + " speichern"
							: ToolTip.gibToolTip(new String[] {
									"Sudoku aus einer Sudoku-Text-Datei" + InfoSudoku.dateiErweiterung + " laden" }),
					frame, applikationsTitel, eintragsModus, sudoku, statusBar);
			dialog = new DateiDialog(frame, istSpeichern);
			this.addActionListener(this);
		}

		public void actionPerformed(ActionEvent arg0) {
			dialog.setVisible(true);
			String fn = dialog.getFile();
			if (fn != null) {
				aktion(dialog.getDirectory(), fn);
			} // if (fn != null)
		} // actionPerformed
	}

	// Laden
	// ---------------------------------------------------------------------------------------------------
	static public void ladeDatei(String nameVerzeichnis, String nameDatei, SudokuBedienung sudoku,
			EintragsModus eintragsModus, JFrame frame, String standardApplikationsTitel) throws IOException, Exc {
		sudoku.reset(nameVerzeichnis, nameDatei);
		eintragsModus.setzeEintragsModus(EintragsModus.Modus.Eintrag);
		frame.setTitle(standardApplikationsTitel + nameDatei);
	}

	private class ButtonDateiLaden extends ButtonDatei {
		public ButtonDateiLaden(JFrame frame, String applikationsTitel, EintragsModus eintragsModus,
				SudokuBedienung sudoku, StatusBar statusBar) {
			super(false, frame, applikationsTitel, eintragsModus, sudoku, statusBar);
		}

		protected void aktion(String nameVerzeichnis, String nameDatei) {
			try {
				ladeDatei(nameVerzeichnis, nameDatei, sudoku, eintragsModus, frame, applikationsTitel);
			} catch (IOException | Exc e1) {
				e1.printStackTrace();
				String s = String.format("%s ist kein Sudoku!", nameDatei);
				statusBar.zeigeProblem(s);
				return;
			}
		}

	} // private class ButtonDateiLaden

	// Speichern
	// ---------------------------------------------------------------------------------------------------
	private class ButtonDateiSpeichern extends ButtonDatei {
		public ButtonDateiSpeichern(JFrame frame, String applikationsTitel, EintragsModus eintragsModus,
				SudokuBedienung sudoku, StatusBar statusBar) {
			super(true, frame, applikationsTitel, eintragsModus, sudoku, statusBar);
		}

		protected void aktion(String nameVerzeichnis, String nameDatei) {
			try {
				sudoku.speichern(nameVerzeichnis, nameDatei);
				sudoku.setzeName(nameDatei);
				// sudoku.setzeSchwierigkeit(); ist hier sinnlos
				frame.setTitle(applikationsTitel + nameDatei);
			} catch (IOException e1) {
				e1.printStackTrace();
				statusBar.zeigeProblem(e1.getMessage());
			}
		}
	}

	// Drucken
	// ---------------------------------------------------------------------------------------------------
	private class ButtonDrucken extends ButtonSudoku implements ActionListener {
		private class MalerDruck extends MalerStandard {
			private boolean druckMaleMoegliche;

			MalerDruck(boolean maleMoegliche) {
				super(false);
				this.druckMaleMoegliche = maleMoegliche;
			}

			@Override
			public void registriereSudoku(InfoSudoku infoSudoku, int sudokuIndex) {
				if (sudokuIndex == 0) {
					maleMoegliche = druckMaleMoegliche;
				} else {
					maleMoegliche = false;
				}
			}
		}

		private ToolBarLinks toolBarLinks;

		// private ToolBarRechts toolBarRechts;
		public ButtonDrucken(JFrame frame, String applikationsTitel, EintragsModus eintragsModus,
				SudokuBedienung sudoku, StatusBar statusBar, ToolBarLinks toolBarLinks) { // , ToolBarRechts toolBarRechts) {
			super("Drucken...", "Sudoku drucken", frame, applikationsTitel, eintragsModus, sudoku, statusBar);
			this.toolBarLinks = toolBarLinks;
			// this.toolBarRechts = toolBarRechts;
			this.addActionListener(this);
		}

		// private void zeigeToolBars(boolean zeigen){
		// this.toolBarLinks.setVisible(zeigen);
		// this.toolBarRechts.setVisible(zeigen);
		// }
		public void actionPerformed(ActionEvent arg0) {
			InfoSudoku schnappschuss = this.sudoku.gibSchnappschuss();
			// Sudoku-Name mittdrucken
			String titel = frame.getTitle();
			if (titel != null) {
				schnappschuss.setzeTitel(titel, " "); // Zwei Titel damit der Titel klein erscheint.
			}

			InfoSudoku vorgaben = this.sudoku.gibVorgaben();
			SudokuSchwierigkeit schwierigkeit = SudokuSchwierigkeit.gibSchwierigkeit(vorgaben);
			String wieSchwerName = schwierigkeit.gibName();
			Integer zeit = schwierigkeit.gibAnzeigeZeit();
			// Schwierigkeit und Lösungszeit mitdrucken
			vorgaben.setzeTitel(wieSchwerName, zeit.toString());

			// Drucken
			InfoSudoku[] druckSudokus = new InfoSudoku[2];
			druckSudokus[0] = schnappschuss;
			druckSudokus[1] = vorgaben;
			boolean istZeigenMoegliche = this.toolBarLinks.istMoeglicheZeigen();
			InfoSudokuDruck.drucke("", druckSudokus, new MalerDruck(istZeigenMoegliche), false, statusBar);
			// FrameDruck.drucke(frame, this.statusBar);
		}
	}

	// ---------------------------------------------------------------------------------------------------
	public class ButtonAnimation extends ButtonSudoku implements ActionListener {
		public ButtonAnimation(SudokuBedienung sudoku) {
			super("Animation...", "Sudoku ohne Einträge animieren", null, // frame,
					null, // applikationsTitel,
					null, // eintragsModus,
					sudoku, null);// , statusBar);
			this.addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			PopupMenuAnimation popupMenu = new PopupMenuAnimation(sudoku);
			int posX = this.getWidth() / 2;
			int posY = this.getHeight() / 2;
			popupMenu.show(this, posX, posY);
		}
	}

	// Wie schwer
	// ---------------------------------------------------------------------------------------------------
	private class ButtonWieSchwer extends ButtonSudoku implements ActionListener {
		public ButtonWieSchwer(JFrame frame, String applikationsTitel, EintragsModus eintragsModus,
				SudokuBedienung sudoku, StatusBar statusBar) {
			super("Wie schwer ?", "Aktualisiert die Schwierigkeitsangabe", frame, applikationsTitel, eintragsModus,
					sudoku, statusBar);
			this.addActionListener(this);
		}

		public void actionPerformed(ActionEvent arg0) {
			sudoku.setzeSchwierigkeit();
		}
	}

	// Sudoku im Monitor erkennen
	// ---------------------------------------------------------------------------------------------------
	private class ButtonMonitor extends ButtonSudoku implements ActionListener {
		public ButtonMonitor(JFrame frame, String applikationsTitel, EintragsModus eintragsModus,
				SudokuBedienung sudoku, StatusBar statusBar) {
			super("Monitor ?", "Sucht ein Sudoku auf dem Monitor", frame, applikationsTitel, eintragsModus, sudoku,
					statusBar);
			this.addActionListener(this);
		}

		/**
		 * @param owner
		 * @return Ein Schnappschuß des Monitorbildes
		 */
		private BufferedImage gibMonitorBild() {
			this.frame.setVisible(false);
			// Es ist erstaunlich, dass mit sleep ein Abwarten auf das vollständige Unsichtbar-Machen des Programm-Fensters möglich ist!?
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}

			BufferedImage image = null;
			try {
				image = Bild.gibMonitorBild();
			} catch (AWTException e) {
				image = null;
				e.printStackTrace();
			}

			image = Bild.gibBildKopie(image);
			this.frame.setVisible(true);
			return image;
		}

		private class BildAnzeige extends JFrame {
			private class MalPanel extends JPanel {
				@Override
				public void paint(Graphics g) {
					super.paint(g);
					if (image == null) {
						return;
					}

					Image imagePaint = image.getScaledInstance(this.getWidth(), this.getHeight(),
							BufferedImage.SCALE_SMOOTH);
					boolean b = g.drawImage(imagePaint, 0, 0, null);
					if (!b) {
						System.out.println(this.getClass().getName() + ": Bild-Malen klappt nicht");
					}
				}
			};

			private BufferedImage image;
			private MalPanel malPanel;

			public BildAnzeige(Frame owner, BufferedImage image) {
				super("Screenshot");
				this.image = image;

				// malPanel als Malfläche erstellen
				malPanel = new MalPanel();
				this.getContentPane().add(malPanel, BorderLayout.CENTER);

				// Fenster positionieren
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				this.setSize(screenSize.width, screenSize.height);
				this.setLocation(0, 0);
				setVisible(true);
			}
		}

		// ====================================================

		public void actionPerformed(ActionEvent arg0) {
			BufferedImage image = gibMonitorBild();
			// Es ist erstaunlich, dass mit sleep ein Abwarten auf das ... Fensters möglich ist!?
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}

			SudokuSucher prozess = new SudokuSucher(image);
			prozess.prozess();

			InfoSudoku[] sudokus = prozess.gibSudokus();
			int nSudokus = 0;
			if (sudokus != null) {
				nSudokus = sudokus.length;
			}

			switch (nSudokus) {
			case 0:
				JOptionPane.showMessageDialog(frame, "Es wurden keine Sudokus gefunden", "Sudoku vom Monitor lesen",
						JOptionPane.PLAIN_MESSAGE);
				break;
			case 1:
				this.sudoku.reset(sudokus[0], "Vom Monitor gelesenes Sudoku");
				break;
			default:
				this.sudoku.reset(sudokus[0], "Vom Monitor gelesenes Sudoku");
				JOptionPane.showMessageDialog(frame, String.format("Es wurden %d Sudokus gefunden", sudokus.length),
						"Sudoku vom Monitor lesen", JOptionPane.PLAIN_MESSAGE);
				break;
			}

			// Ergebnisbild zeigen
			boolean zeigeBild = false;
			if (zeigeBild) {
				BufferedImage sudokuImage = prozess.gibImage();
				if (sudokuImage != null) {
					// BildAnzeige bildAnzeige =
					new BildAnzeige(frame, sudokuImage);
				}
			}
		}
	}

	// =============================================================
	public ToolBarGruppeSudoku(ToolBarLinks toolBar, ToolBarRechts toolBarRechts, JFrame frame, String titel,
			EintragsModus eintragsModus, SudokuBedienung sudoku, StatusBar statusBar) {
		toolBar.add(new ButtonNeu(frame, titel, eintragsModus, sudoku, statusBar));
		toolBar.add(new ButtonDateiLaden(frame, titel, eintragsModus, sudoku, statusBar));
		toolBar.add(new ButtonEntnommenes(frame, titel, eintragsModus, sudoku, statusBar));
		toolBar.add(new ButtonDateiSpeichern(frame, titel, eintragsModus, sudoku, statusBar));
		toolBar.add(new ButtonDrucken(frame, titel, eintragsModus, sudoku, statusBar, toolBar)); // , toolBarRechts));
		toolBar.add(new ButtonAnimation(sudoku));
		toolBar.add(new ButtonWieSchwer(frame, titel, eintragsModus, sudoku, statusBar));
		toolBar.add(new ButtonMonitor(frame, titel, eintragsModus, sudoku, statusBar));
	}
}
