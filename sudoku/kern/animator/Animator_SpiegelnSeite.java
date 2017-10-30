package kern.animator;

import kern.feldmatrix.FeldNummer;

public class Animator_SpiegelnSeite implements Animator {
	@Override
	public FeldNummer gibFeldNummer(FeldNummer feldNummer, int nummerMax) {
		int neueSpalte = nummerMax + 1 - feldNummer.gibSpalte();
		int neueZeile = feldNummer.gibZeile();
		FeldNummer neueFeldNummer = new FeldNummer(neueSpalte, neueZeile);
		return neueFeldNummer;
	}

	@Override
	public String gibName() {
		return "Spiegeln an der Seite";
	}
}
