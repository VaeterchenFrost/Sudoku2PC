package logik;

import kern.feldmatrix.FeldNummerMitZahl;
import kern.feldmatrix.ZahlenListe;
import logik.bericht.GruppenLaeufeListe;
import logik.tipinfo.TipInfo;

class LogikErgebnis {
	final GruppenLaeufeListe gruppenLaeufeListe;
	final FeldNummerMitZahl eintrag;
	final ZahlenListe loeschZahlen;
	final TipInfo tipInfo;

	LogikErgebnis(GruppenLaeufeListe gruppenLaeufeListe, FeldNummerMitZahl eintrag, ZahlenListe loeschZahlen,
			TipInfo tipInfo) {
		super();
		this.gruppenLaeufeListe = gruppenLaeufeListe;
		this.eintrag = eintrag;
		this.loeschZahlen = loeschZahlen;
		this.tipInfo = tipInfo;
	}

	LogikErgebnis(GruppenLaeufeListe gruppenLaeufeListe) {
		super();
		this.gruppenLaeufeListe = gruppenLaeufeListe;
		this.eintrag = null;
		this.loeschZahlen = null;
		this.tipInfo = null;
	}

}
