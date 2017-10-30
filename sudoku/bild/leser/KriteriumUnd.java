package bild.leser;

class KriteriumUnd implements KriteriumBildInfo {
	private String name;
	private KriteriumBildInfo[] kriterien;

	KriteriumUnd(String name, KriteriumBildInfo[] kriterien) {
		super();
		this.name = name;
		this.kriterien = kriterien;
	}

	KriteriumUnd(String name) {
		super();
		this.name = name;
		this.kriterien = null;
	}

	void setzeKriterien(KriteriumBildInfo[] kriterien) {
		this.kriterien = kriterien;
	}

	// private void systemOut(float erfuellungsGrad, Set<Float> erfuellungsGradJeKriterium){
	// if (ZahlLeser.istSystemOut()){
	// System.out.println(String.format("%s Das Kriterium UND ist zu %1.1f%% erfüllt.", getClass().getName(), erfuellungsGrad));
	// // for(KriteriumErgebnis kriteriumErgebnis: erfuellungsGradJeKriterium){
	// // System.out.println(String.format("%1.1f%% %s", kriteriumErgebnis.erfuellungsGrad, kriteriumErgebnis.kriterium));
	// // }
	// }
	// }

	public float gibErfuellungsGrad(ZahlBildInfo zahlBildInfo, boolean istSystemOutZahl) {
		float durchschnitt = 0;
		for (KriteriumBildInfo kriterium : kriterien) {
			float erfuellungsGrad = kriterium.gibErfuellungsGrad(zahlBildInfo, istSystemOutZahl);
			durchschnitt += erfuellungsGrad;
		}

		durchschnitt /= kriterien.length;

		if (istSystemOutZahl) {
			System.out.println(String.format("Erfüllt=%1.1f%% %s %s", durchschnitt, getClass().getSimpleName(),
					this.name));
		}

		return durchschnitt;
	}

	@Override
	public String gibName() {
		return name;
	}
}
