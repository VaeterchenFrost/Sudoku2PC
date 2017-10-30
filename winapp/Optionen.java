package winapp;

public class Optionen {
	private boolean zeigeMoegliche;
	private boolean zeigeFeldPaare;

	public Optionen() {
		zeigeMoegliche = false;
		zeigeFeldPaare = false;
	}

	public void setzeZeigeMoegliche(boolean bo) {
		zeigeMoegliche = bo;
	}

	public void setzeZeigeFeldPaare(boolean bo) {
		zeigeFeldPaare = bo;
	}

	public boolean istZeigeMoegliche() {
		return zeigeMoegliche;
	}

	public boolean istZeigeFeldPaare() {
		return zeigeFeldPaare;
	}
}
