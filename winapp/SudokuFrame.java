package winapp;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import res.ResLoader;
import sudoku.bedienung.SudokuBedienung;
import sudoku.kern.exception.Exc;
import sudoku.kern.info.InfoSudoku;
import sudoku.logik.Logik_ID;
import sudoku.tools.Verzeichnis;
import winapp.statusbar.StatusBar;
import winapp.toolbar.ToolBarGruppeSudoku;
import winapp.toolbar.ToolBarLinks;
import winapp.toolbar.ToolBarRechts;

//TODO Sudoku speichert auch die "EintrÃ¤ge"
// Das geht, indem das Protokoll mit gespeichert und geladen wird.
// und sollte nur stattfinden bei Speichern bzw. Laden per Toolbar-Button.

//TODO Sudoku ablesen von irgendeinem Bild (Internet-Browser)
/** LÃ¶sungshinweise:
 * - Basiert auf Tesseract OCR engine. http://www.newocr.com
 * - http://www.free-ocr.com/
 * - http://www.sciweavers.org/free-online-ocr
 * - https://www.ocrgeek.com/
 * - http://www.online-code.net/ocr.html
 * - Microsoft Office Document Imaging 12.0 Type Library. 
 * 		Das kann man sich free saugen und stellt eine OCR-Klasse dar. Der Klasse Ã¼bergibt man einfach einen FileName und gut is ;)
 * - C++Code: http://www.planet-source-code.com/vb/scripts/ShowCode.asp?txtCodeId=5604&lngWId=3
 */

// TODO Mac-Installer: http://mac.softpedia.com/get/Utilities/Jar-Installer.shtml

// TODO Suchstrings fÃ¼r die Ablage im *.jar-File als Ressource:
// InputStream in = ResLoader.class.getResourceAsStream("img/meinBild.png");
// URL url = ResLoader.class.getResource("img/meinBild.png");
// Die Datei Configuration.properties liegt wie die Klasse ConfigTool im Package config

// TODO Problem-Meldung: Bedienerfreunlichen Text (nicht-technisch)
// + ToolTip: Was tun

// TODO Rekorder:
// - ev. gehen die Versuch-Buttons +-5 Schritte wenn kein Versuch vorliegt
//

// TODO Mehrfach-Sudoku
// - MenÃ¼ + Toolbar weg zur Seite:
// . Tollbar links: oben Eingabemodus + Klare + Knacken, Mitte Sudoku, unten Optionen
// - Wird das beste sein: Swing ersetzen durch awt
// - Alle FeldAnzeigen legen sich drunter ein Panel gleicher GrÃ¶ÃŸe, denn
// - FeldAnzeigen als Klasse extended Panel
// - Anzeige Spalte/Zeile mit Label

@SuppressWarnings("serial")
public class SudokuFrame extends JFrame {
	private static String titel = "Sudoku   ";

	private static JFrame mainFrame = null;

	private static StatusBar statusBar = null;

	private static ToolBarLinks toolBarLinks = null;
	private static ToolBarRechts toolBarRechts = null;

	public static JFrame gibMainFrame() {
		return mainFrame;
	}

	/**
	 * @return Die Breite und HÃ¶he des Bereiches um die Sudokuanzeige (FeldAnzeigen)
	 */
	public static Dimension gibSudokuRahmen() {
		int breiteToolBarL = toolBarLinks.getWidth();
		if (!toolBarLinks.isVisible()) {
			breiteToolBarL = 0;
		}
		int breiteToolBarR = toolBarRechts.getWidth();
		if (!toolBarRechts.isVisible()) {
			breiteToolBarR = 0;
		}
		int breite = breiteToolBarL + breiteToolBarR;
		int hoehe = statusBar.getHeight();
		return new Dimension(breite, hoehe);
	}

	public static void main(String args[]) throws Exc {
		winapp.tools.AusnahmeBehandlung ausnahmeBehandlung = new winapp.tools.AusnahmeBehandlung();
		ausnahmeBehandlung.einklinken();

		String pfadName = null;
		if (args.length == 1) {
			String dateiName = args[0];
			if (dateiName.endsWith(InfoSudoku.dateiErweiterung)) {
				pfadName = dateiName;
			}
		}
		new SudokuFrame(pfadName);
	}

	SudokuFrame(String pfadName) throws Exc {
		super();
		mainFrame = this;
		this.setTitle(titel);
		// Anwendung schlieÃŸen bei Haupt-Fenster-SchlieÃŸen lÃ¤uft nicht automatisch!
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		SudokuBedienung sudoku = new SudokuBedienung(new winapp.tools.AusnahmeBehandlung());
		SudokuFrame.statusBar = new StatusBar(sudoku, this.getContentPane());
		SudokuFrame.toolBarLinks = new ToolBarLinks(sudoku, statusBar, this.getContentPane());
		SudokuFrame.toolBarRechts = new ToolBarRechts(sudoku, statusBar, this.getContentPane());
		Optionen optionen = new Optionen();
		FeldAnzeigen feldAnzeigen = new FeldAnzeigen(this.getContentPane(), toolBarLinks, toolBarRechts, statusBar,
				sudoku, optionen);

		SudokuFrame.toolBarLinks.erschaffeGruppeSudoku(this, titel, toolBarLinks, sudoku, statusBar, toolBarRechts);
		SudokuFrame.toolBarLinks.erschaffeGruppeAnzeige(optionen, sudoku, feldAnzeigen);
		SudokuFrame.toolBarLinks.erschaffeGruppeExtras(this, titel, sudoku, statusBar, toolBarRechts);

		// Eigene Minimal-GrÃ¶ÃŸe setzen (auf Quadrat fÃ¼r das Ermitteln der nichtNutzbareHoehe!)
		int minGroesse = 300;
		this.setMinimumSize(new Dimension(minGroesse, minGroesse));
		// Die Components.. erhalten ihre reale GrÃ¶ÃŸe
		pack();

		SudokuFrame.toolBarLinks.aufpeppen();
		SudokuFrame.toolBarRechts.aufpeppen();
		// Die ToolBars erhalten ihre neue reale GrÃ¶ÃŸe
		pack();

		// Eigene GrÃ¶ÃŸe setzen
		Groesse.setzeMaximalGroesse();

		// Und jetzt einmal Veranlassen, dass sudoku alle Anzeigeelemente anspricht
		boolean bo = sudoku.istSoKlug(Logik_ID.ORTFEST1);
		sudoku.setzeLogik(Logik_ID.ORTFEST1, bo);

		if (pfadName != null) {
			String verzeichnis = Verzeichnis.gibVerzeichnis(pfadName);
			String dateiname = Verzeichnis.gibDateiname(pfadName);
			// System.out.println("SudokuFrame(String pfadName) " + pfadName);
			// System.out.println("SudokuFrame(String pfadName) " + verzeichnis);
			// System.out.println("SudokuFrame(String pfadName) " + dateiname);
			try {
				ToolBarGruppeSudoku.ladeDatei(verzeichnis, dateiname, sudoku, // SudokuBedienung
						SudokuFrame.toolBarLinks, // EintragsModus eintragsModus,
						this, SudokuFrame.titel);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
		this.setState(ICONIFIED);
		this.setVisible(true);
	}

	// -------------------------------------------------------------------------------------------------
	@Override
	public List<Image> getIconImages() {
		ArrayList<Image> list = new ArrayList<>();

		URL url = ResLoader.class.getResource("SudokuIcon.gif");
		ImageIcon imageIcon = new ImageIcon(url);
		list.add(imageIcon.getImage());
		// Das Bild wird, weil es Bestandteil der Projekt-Quellen ist, als Ressource erfolgreich bereitgestellt
		// und als Ikon der laufenden Anwendung dargestellt.

		// Allerdings bietet die Applikation nichts als Icon an fÃ¼r die Konfiguration des Symbols fÃ¼r eine VerknÃ¼pfung.
		// Dazu mÃ¼sste laut Hinweisen das Bild z.B. mit IvanView im richtigen Format gespeichert werden.
		// NÃ¤mlich im Format eines Symbols und mit der Dateierweiterung .ico.

		return list;
	}

	@Override
	protected void processEvent(AWTEvent e) {
		// System.out.println(e);
		super.processEvent(e);
	}

}
