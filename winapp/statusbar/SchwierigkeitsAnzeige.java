package winapp.statusbar;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JComboBox;

import sudoku.schwer.AnzeigeInfo;
import sudoku.schwer.SudokuSchwierigkeit;
import sudoku.schwer.daten.InfoKlareDetail;
import sudoku.schwer.daten.InfoKlareZeit;
import sudoku.schwer.daten.InfoVersuche;

@SuppressWarnings("serial")
public class SchwierigkeitsAnzeige extends JComboBox<String> implements ItemListener {
	private static boolean istSystemOut = false; // true;

	public SchwierigkeitsAnzeige() {
		super();
		this.setMaximumRowCount(30);
		// this.setFont();
		setzeSchwierigkeit(SudokuSchwierigkeit.unbekannt());
		this.addItemListener(this);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if ((!this.isVisible()) || (!this.isValid())) {
			return;
		}
		if (this.getItemCount() < 2) {
			return;
		}
		boolean istSelektiert = e.getStateChange() == ItemEvent.SELECTED;
		String itemText = (String) e.getItem();
		String itemTextLast = this.getItemAt(this.getItemCount() - 1);
		if ((itemText == null) || (itemTextLast == null)) {
			return;
		}
		boolean istLetzter = itemTextLast.equals(itemText);
		if (istSelektiert && (!istLetzter)) {
			SchwierigkeitHilfe.zeige();
		}
	}

	public void setzeSchwierigkeit(SudokuSchwierigkeit schwierigkeit) {
		this.removeAllItems();

		ArrayList<AnzeigeInfo> anzeigeInfos = erstelleAnzeigeInfos(schwierigkeit);

		if (istSystemOut) {
			System.out.println("SchwierigkeitsAnzeige.erstelleAnzeigeInfos()");
		}

		// Die letzte AnzeigeInfo oder null wenn es gar keine solche gibt:
		AnzeigeInfo summe = null;
		for (int i = 0; i < anzeigeInfos.size(); i++) {
			AnzeigeInfo lauf = anzeigeInfos.get(i);
			summe = lauf;
			this.addItem(lauf.gibAnzeigeText());
			if (istSystemOut) {
				System.out.println(lauf.gibAnzeigeText());
			}
		}

		if (summe != null) {
			this.setToolTipText(summe.gibToolTip());
			// this.setToolTipText(ToolTip.gibToolTip(InfoKlareDetail.gibAnzeigeTextBeschreibung()));
		}
		this.setSelectedIndex(this.getItemCount() - 1);
	}

	private ArrayList<AnzeigeInfo> erstelleAnzeigeInfos(SudokuSchwierigkeit schwierigkeit) {
		ArrayList<AnzeigeInfo> anzeigeInfos = new ArrayList<>();
		{
			ArrayList<InfoKlareDetail> klareDetails = schwierigkeit.gibDetails();
			if (!klareDetails.isEmpty()) {
				for (int i = 0; i < klareDetails.size(); i++) {
					InfoKlareDetail infoKlareDetail = klareDetails.get(i);
					anzeigeInfos.add(infoKlareDetail);
				}
				InfoKlareZeit klareZeit = schwierigkeit.gibKlareZeit();
				if (klareZeit != null) {
					anzeigeInfos.add(klareZeit);
				}
			}
		}

		AnzeigeInfo versucheOK = schwierigkeit.gibAnzahlOKVersuche();
		if (versucheOK != null) {
			ArrayList<InfoVersuche> versuchsStarts = schwierigkeit.gibVersuchStarts();
			if (versuchsStarts != null) {
				for (int i = 0; i < versuchsStarts.size(); i++) {
					anzeigeInfos.add(versuchsStarts.get(i));
				}
			}

			anzeigeInfos.add(versucheOK);
		}
		return anzeigeInfos;
	}

}
