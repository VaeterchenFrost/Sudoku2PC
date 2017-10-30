package kern.exception;

import kern.feldmatrix.FeldNummerMitZahl;

@SuppressWarnings("serial")
public class FehlendeMoeglicheZahl extends RuntimeException {

	public FehlendeMoeglicheZahl(FeldNummerMitZahl feldNummerMitZahl) {
		super(String.format("Fehlende mögliche Zahl %s", feldNummerMitZahl));
	}
}
