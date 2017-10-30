package winapp.feld;

import java.util.ArrayList;

import sudoku.kern.info.FeldInfo;

public class MalZustand {
	private FeldInfo feldInfo;
	private boolean istZeigeMoegliche;
	private boolean istZeigeFeldPaare;

	public MalZustand(FeldInfo feldInfo, boolean istZeigeMoegliche, boolean istZeigeFeldPaare) {
		super();
		this.feldInfo = feldInfo;
		this.istZeigeMoegliche = istZeigeMoegliche;
		this.istZeigeFeldPaare = istZeigeFeldPaare;
	}

	public FeldInfo gibFeldInfo() {
		return this.feldInfo;
	}

	public boolean istZeigeMoegliche() {
		return this.istZeigeMoegliche;
	}

	public boolean istZeigeFeldPaare() {
		return this.istZeigeFeldPaare;
	}

	private boolean istGleich(ArrayList<Integer> moegliche1, ArrayList<Integer> moegliche2) {
		if (moegliche1.size() != moegliche2.size()) {
			return false;
		}
		for (int i = 0; i < moegliche1.size(); i++) {
			if (moegliche1.get(i) != moegliche2.get(i)) {
				return false;
			}
		}
		return true;
	}

	public boolean istMalen(MalZustand anderer) {
		if (anderer == null) {
			return true;
		}
		if (this.feldInfo.gibVorgabe() != anderer.feldInfo.gibVorgabe()) {
			return true;
		}
		if (this.feldInfo.gibEintragObjekt() == null) {
			if (anderer.feldInfo.gibEintragObjekt() != null) {
				return true;
			}
		} else {
			if (!this.feldInfo.gibEintragObjekt().equals(anderer.feldInfo.gibEintragObjekt())) {
				return true;
			}
		}
		if (this.istZeigeMoegliche != anderer.istZeigeMoegliche) {
			return true;
		}
		if (this.istZeigeFeldPaare != anderer.istZeigeFeldPaare) {
			return true;
		}
		if (!istGleich(this.feldInfo.gibMoegliche(), anderer.feldInfo.gibMoegliche())) {
			return true;
		}
		if (this.feldInfo.gibMarkierung() == null) {
			if (anderer.feldInfo.gibMarkierung() != null) {
				return true;
			}
		} else {
			if (!this.feldInfo.gibMarkierung().equals(anderer.feldInfo.gibMarkierung())) {
				return true;
			}
		}
		return false;
	}

}
