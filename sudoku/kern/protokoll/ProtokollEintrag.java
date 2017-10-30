package kern.protokoll;

import kern.animator.Animator;
import kern.exception.Exc;
import kern.feldmatrix.Eintrag;
import kern.feldmatrix.FeldMatrix;
import kern.feldmatrix.FeldNummer;

public class ProtokollEintrag {
	private FeldNummer feldNummer;
	/**
	 * Zustand des Feldes vor dem Eintrag
	 */
	final Eintrag eintragAlt;
	/**
	 * Zustand des Feldes jetzt
	 */
	final Eintrag eintragNeu;

	public ProtokollEintrag(FeldNummer feldNummer, Eintrag eintragAlt, Eintrag eintragNeu) {
		this.feldNummer = feldNummer;
		this.eintragAlt = eintragAlt;
		this.eintragNeu = eintragNeu;
	}

	public FeldNummer gibFeldNummer() {
		return feldNummer;
	}

	/**
	 * @return Im Eintrag muss genau eine FeldInfo einen eintrag besitzen. Von ihm wird die Ebene genommen.
	 * @throws ExcProtokoll 
	 */
	public int gibEintragsEbene() throws Exc {
		Eintrag eintrag = eintragAlt;
		if (eintrag == null) {
			eintrag = eintragNeu;
		}

		if (eintrag == null) {
			throw Exc.protokollEintragOhneEintrag(this);
		}

		int ebene = eintrag.gibEbene();
		return ebene;
	}

	public void animiere(Animator animator) {
		feldNummer = animator.gibFeldNummer(feldNummer, FeldMatrix.feldNummerMax);
	}

}
