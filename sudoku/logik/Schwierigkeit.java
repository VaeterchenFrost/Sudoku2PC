package sudoku.logik;

import java.util.EnumMap;

import sudoku.kern.exception.UnerwarteterInhalt;

public enum Schwierigkeit {
	LEICHT, MITTEL, SCHWER, KNACKIG, EXPERTE, LOGIREX;

	/**
	 * @return Welche Schwierigkeit welche <code>Klugheit<code> besitzt bzw. welche Logiken beherrscht. 
	 * @see Klugheit(LogikID maxLogik): Die Klugheit beherrscht maxLogik und alle kleineren Logiken von maxLogik!  
	 */
	private static EnumMap<Schwierigkeit, Klugheit> gibSchwierigkeitKlugheiten() {
		EnumMap<Schwierigkeit, Klugheit> typKlugheiten = new EnumMap<Schwierigkeit, Klugheit>(Schwierigkeit.class);

		typKlugheiten.put(LEICHT, new Klugheit(Logik_ID.ORTFEST1));
		typKlugheiten.put(MITTEL, new Klugheit(Logik_ID.ORTFEST2));
		typKlugheiten.put(SCHWER, new Klugheit(Logik_ID.KASTEN2));
		typKlugheiten.put(KNACKIG, new Klugheit(Logik_ID.FELD));
		typKlugheiten.put(EXPERTE, new Klugheit(Logik_ID.KREUZFLUEGEL));
		typKlugheiten.put(LOGIREX, new Klugheit((Logik_ID) null));

		return typKlugheiten;
	}

	/**
	 * @param klugheit
	 * @return null oder Schwierigkeit, die mit der übergebenen Klugheit zu bewältigen ist
	 */
	public static Schwierigkeit gibSchwierigkeit(Klugheit klugheit) {
		EnumMap<Schwierigkeit, Klugheit> typKlugheiten = gibSchwierigkeitKlugheiten();

		Schwierigkeit[] schwierigkeiten = Schwierigkeit.values();
		for (int i = schwierigkeiten.length - 1; i >= 0; i--) {
			Schwierigkeit schwierigkeit = schwierigkeiten[i];

			Klugheit schwierigkeitKlugheit = typKlugheiten.get(schwierigkeit);
			Logik_ID maxLogik = schwierigkeitKlugheit.gibGroessteLogik();

			if (klugheit.istSoKlug(maxLogik)) {
				return schwierigkeit;
			}
		}

		return null;
	}

	// /**
	// * @return Die Schwierigkeit, der die Logik der Logik_ID zugeordnet ist.
	// * Oder genauer und praktischer gesagt: Die kleinste Schwierigkeit, ab der die Logik der Logik_ID läuft.
	// */
	// public static Schwierigkeit gibSchwierigkeit(Logik_ID logik_ID) {
	// EnumMap<Schwierigkeit, Klugheit> typKlugheiten = gibSchwierigkeitKlugheiten();
	//
	// if (Logik_ID.XYZFLUEGEL.equals(logik_ID)) { // && Schwierigkeit.EXPERTE.equals(schwierigkeit))
	// int a = 0;
	// int b = a;
	// }
	//
	// Schwierigkeit[] schwierigkeiten = Schwierigkeit.values();
	// for (int i = 0; i < schwierigkeiten.length - 1; i++) {
	// Schwierigkeit schwierigkeit = schwierigkeiten[i];
	// Klugheit klugheit = typKlugheiten.get(schwierigkeit);
	//
	// if (klugheit.istSoKlug(logik_ID)) {
	// return schwierigkeit;
	// }
	// }
	//
	// throw new UnerwarteterInhalt("Es wurde keine Schwierigkeit gefunden für die (unbekannte ?) Logik_ID " + logik_ID);
	// }

	/**
	 * @param logik bei null kommt die kleinste Schwierigkeit zurück
	 * @return Die kleinste Schwierigkeit, die diese Logik beinhaltet.
	 * 				Oder genauer und praktischer gesagt: Die kleinste Schwierigkeit, ab der die Logik der Logik_ID läuft. 
	 */
	public static Schwierigkeit gibSchwierigkeit(Logik_ID logik) {
		if (logik == null) {
			return Schwierigkeit.gibExtremTyp(false);
		}

		EnumMap<Schwierigkeit, Klugheit> typKlugheiten = gibSchwierigkeitKlugheiten();
		Schwierigkeit[] schwierigkeiten = Schwierigkeit.values();

		// Mit allen Schwierigkeiten ab der leichtesten
		for (int iSchwierigkeit = 0; iSchwierigkeit < schwierigkeiten.length; iSchwierigkeit++) {
			Schwierigkeit schwierigkeit = schwierigkeiten[iSchwierigkeit];

			Klugheit schwierigkeitKlugheit = typKlugheiten.get(schwierigkeit);

			if (schwierigkeitKlugheit.istSoKlug(logik)) {
				return schwierigkeit;
			}
		}

		throw new UnerwarteterInhalt("Es wurde keine Schwierigkeit gefunden für die (unbekannte ?) Logik_ID " + logik);
		// return null;
	}

	public static Klugheit gibKlugheit(Schwierigkeit typ) {
		EnumMap<Schwierigkeit, Klugheit> typKlugheiten = gibSchwierigkeitKlugheiten();
		Klugheit klugheit = typKlugheiten.get(typ);
		return klugheit;
	}

	public static Schwierigkeit gibWert(String name) {
		Schwierigkeit typ = Schwierigkeit.valueOf(name.toUpperCase());
		return typ;
	}

	public static Schwierigkeit gibExtremTyp(boolean istMax) {
		Schwierigkeit[] typen = Schwierigkeit.values();

		if (istMax) {
			return typen[typen.length - 1];
		} else {
			return typen[0];
		}
	}

	public static String gibName(Schwierigkeit typ) {
		String name = "nix";
		if (typ != null) {
			String typName = typ.toString();
			String ende = typName.substring(1);
			name = typName.substring(0, 1) + ende.toLowerCase();
		}
		return name;
	}

	public static String[] gibAlleNamen() {
		Schwierigkeit[] typen = Schwierigkeit.values();
		String namen[] = new String[typen.length];
		for (int i = 0; i < typen.length; i++) {
			namen[i] = gibName(typen[i]);
		}
		return namen;
	}

	/**
	 * @param typ
	 * @return Gibt einen um eins leichteren Schwierigkeit zurück oder null
	 */
	public static Schwierigkeit gibLeichtere(Schwierigkeit typ) {
		if (typ == null) {
			return Schwierigkeit.values()[0];
		}

		int index = typ.ordinal();
		index--;
		if (index < 0) {
			return null;
		} else {
			Schwierigkeit[] typen = Schwierigkeit.values();
			return typen[index];
		}
	}

	/**
	 * @param typ
	 * @return Gibt einen um eins schwereren Schwierigkeit zurück oder null
	 */
	public static Schwierigkeit gibSchwerere(Schwierigkeit typ) {
		if (typ == null) {
			return Schwierigkeit.values()[0];
		}

		int index = typ.ordinal();
		index++;

		Schwierigkeit[] typen = Schwierigkeit.values();
		if (index >= typen.length) {
			return null;
		} else {
			return typen[index];
		}
	}

	/**
	 * @param typ
	 * @return typ rotiert
	 */
	public static Schwierigkeit rotiere(Schwierigkeit typ) {
		Schwierigkeit[] typen = Schwierigkeit.values();
		Schwierigkeit groesster = typen[typen.length - 1];
		if (groesster.equals(typ)) {
			return typen[0];
		}
		for (int i = 0; i < typen.length; i++) {
			Schwierigkeit iTyp = typen[i];
			if (iTyp.equals(typ)) {
				return typen[i + 1];
			}
		}
		return null; // Das kann nie sein!
	}

}
