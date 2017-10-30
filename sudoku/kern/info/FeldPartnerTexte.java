package kern.info;

import java.util.ArrayList;

import kern.feldmatrix.FeldNummer;
import kern.feldmatrix.FeldNummerListe;
import kern.feldmatrix.ZahlenFeldNummern;

/**
 * Ein anderes Feld einer Gruppen ist mit einem Basis-Feld ein Feld-Paar,
 * wenn es in der Gruppe eine mögliche Zahl genau zweimal gibt: 
 * Das eine Exemplar dieser möglichen Zahl besitzt das Basis-Feld, das andere das andere Feld.
 * In der Sudokulösung MUSS das Basis-Feld oder das andere diese Zahl als Eintrag besitzen!
 * Die Feld-Paare sind der letzte Strohhalm des Knacker an den er sich klammert, 
 * um doch noch zu einer Lösung des Sudoku zu kommen, nachdem er bereits alle Felder 
 * mit genau zwei möglichen Zahlen durchprobiert hat. 
 * feldPaare != null sagt: Zu der möglichen Zahl X bildet das Basis-Feld
 * mit folgenden Feldern (FeldNummern) ein FeldPaar.  
 */
public class FeldPartnerTexte {
	static private String gibSpaltenBezug(int basisSpalte, int partnerSpalte) {
		if (basisSpalte < partnerSpalte) {
			return "rechts";
		}
		return "links";
	}

	static private String gibZeilenBezug(int basisZeile, int partnerZeile) {
		if (basisZeile < partnerZeile) {
			return "unten";
		}
		return "oben";
	}

	/**
	 * @param basisFeldNummer
	 * @param partner
	 * @return Text, der die Lage des partners bezogen auf das basisFeld benennt.
	 * 				Bemerkenswert ist vielleicht, dass es sich hier "nur" um den Lage-Bezug handelt 
	 * 				und nicht etwa um die Zuordnung zu einer Gruppe!
	 * 				Es kann also z.B. sein, dass ein Partner auf Basis eines Kastens mit dem BasisFeld auf einer Zeile liegt.
	 */
	static private String gibPartnerString(FeldNummer basisFeldNummer, FeldNummer partner) {
		if (basisFeldNummer.zeile == partner.gibZeile()) {
			String s = String.format("in dieser Zeile mit %s S%d ",
					gibSpaltenBezug(basisFeldNummer.spalte, partner.gibSpalte()), partner.gibSpalte());
			return s;
		}

		if (basisFeldNummer.spalte == partner.gibSpalte()) {
			String s = String.format("in dieser Spalte mit %s Z%d ",
					gibZeilenBezug(basisFeldNummer.zeile, partner.gibZeile()), partner.gibZeile());
			return s;
		}

		String s = String.format("hier im Kasten mit %s %s %s ",
				gibSpaltenBezug(basisFeldNummer.spalte, partner.gibSpalte()),
				gibZeilenBezug(basisFeldNummer.zeile, partner.gibZeile()), partner);
		return s;
	}

	/**
	 * @param basisFeldNummer
	 * @return Die Liste der Strings, einer für jeden Partner
	 */
	static public String[] gibPaareTexte(FeldNummer basisFeldNummer, ZahlenFeldNummern partner) {
		ArrayList<String> texte = new ArrayList<String>();
		int[] zahlen = partner.gibZahlen();
		if (zahlen != null) {
			for (int iZahl = 0; iZahl < zahlen.length; iZahl++) {
				int zahl = zahlen[iZahl];
				FeldNummerListe andereFelder = partner.gibFeldNummern(zahl);
				if (andereFelder != null) {
					String sZahlList = String.format("Mit Zahl %d bin ich Feld-Paar ", zahl);
					// sZahlList += "->";
					for (int i = 0; i < andereFelder.size(); i++) {
						FeldNummer andereFeldNummer = andereFelder.get(i);
						String sFeldNummer = gibPartnerString(basisFeldNummer, andereFeldNummer);
						if (i > 0) {
							sZahlList += " und ";
						}
						sZahlList += sFeldNummer;
					}
					texte.add(sZahlList);
				}
			} // for
		}
		String[] texteArray = texte.toArray(new String[texte.size()]);
		return texteArray;
	}

}
