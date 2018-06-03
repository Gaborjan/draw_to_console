public class Kijelzo {
	private static final int MAXOSZLOP	 = 200;
	private static final int MAXSOR		 = 50;
	private char			  tartalom[][]; // A kijelző "tartalma"
	private int				  aktSor		 = 0, aktOszlop = 0; // A képernyő kurzorának poziciója
	private int				  kijelzoSor = 0, kijelzoOszlop = 0; // Ennyi sorból és oszlopból áll a kijelző

	public Kijelzo() {
		
	}
	
	
	public Kijelzo(int sor, int oszlop) {
		if (sor > 0 && sor <= MAXSOR && oszlop > 0 && oszlop <= MAXOSZLOP) {
			tartalom = new char[sor][oszlop];
			kijelzoSor = sor;
			kijelzoOszlop = oszlop;
		} else {
			tartalom = new char[MAXSOR][MAXOSZLOP];
			kijelzoSor = MAXSOR;
			kijelzoOszlop = MAXOSZLOP;
		}
		for (int i = 0; i < kijelzoSor; i++)
			for (int j = 0; j < kijelzoOszlop; j++)
				tartalom[i][j] = ' ';
		aktSor = 0;
		aktOszlop = 0;

	}

	//A tárolt kijelző kiírása konzolra
	public void kiir() {
		for (int i = 0; i < kijelzoSor; i++) {
			for (int j = 0; j < kijelzoOszlop; j++)
				System.out.print(tartalom[i][j]);
			System.out.println();
		}
	}
	
	//A tárolt kijelző törlése = szóközzel feltöltése, kurzort a bal felső sarokba állítja
	public void torol() {
		for (int i = 0; i < kijelzoSor; i++)
			for (int j = 0; j < kijelzoOszlop; j++)
				tartalom[i][j] = ' ';
		aktSor = 0;
		aktOszlop = 0;
	}
	
	//A megadott sor, megadott oszlop poziciójába visszi a "kurzort"
	public void poz(int sor, int oszlop) {
		if (sor < kijelzoSor && sor >= 0)
			aktSor = sor;
		if (oszlop < kijelzoOszlop && oszlop >= 0)
			aktOszlop = oszlop;
	}

	//Az aktuális kurzor pozicíóba kiírja a megadott szöveget
	public void ir(String szoveg) {
		int o = aktOszlop;
		int i = 0;
		while (o < kijelzoOszlop && i < szoveg.length()) {
			tartalom[aktSor][o] = szoveg.charAt(i);
			o++;
			i++;
		}
	}

	//A megadott pozicióba kiírja az megadott szöveget, a kurzor pozicíója nem változik
	public void irXY(int sor, int oszlop, String szoveg) {
		int mSor = aktSor, mOszlop = aktOszlop; // kurzor helyének eltárolása
		poz(sor, oszlop);
		ir(szoveg);
		//kurzor helyének visszaállítása
		aktSor = mSor;
		aktOszlop = mOszlop;
	}

	/* Megrajzol egy adott hosszúságú sort, adott karakterből, megadott sor/oszloptól,  megadott hosszban
	 * a kurzor poziciója nem változik.*/
	public void sorRajzol(int sor, int oszlop, int hossz, char mibol) {
		for (int i = 0; i < hossz; i++)
			tartalom[sor][oszlop + i] = mibol;
	}

	/* Megrajzol egy adott hosszúságú oszlopot, adott karakterből, megadott sor/oszloptól,  megadott hosszban
	 * a kurzor poziciója nem változik.*/
	public void oszlopRajzol(int oszlop, int sor, char mibol, int hossz) {
		for (int i = 0; i < hossz; i++)
			tartalom[sor + i][oszlop] = mibol;
	}

	/* Keretet rajzol az aktuális kurzor poziciótól, kurzor pozicíója nem változik.
	 * sor,oszlop: a keretnek ennyi sor és oszlopa lesz
	 * tipus: 		S vagy D, egy vonalas, dupla vonalas keret
	 * arnyek: 		ha igaz akkor árnyékot is rajzol a keretnek
	 * fejléc: 		fejléc szöveg a keret tetejére
	 */
	public void keret(int sor, int oszlop, char tipus, boolean arnyek, String fejlec) {
		// Rajzoló karakterek definiálása
		final char BFS = '\u250c';
		final char JFS = '\u2510';
		final char VS = '\u2500';
		final char FS = '\u2502';
		final char BAS = '\u2514';
		final char JAS = '\u2518';
		final char BFD = '\u2554';
		final char JFD = '\u2557';
		final char VD = '\u2550';
		final char FD = '\u2551';
		final char BAD = '\u255A';
		final char JAD = '\u255D';
		final char ARNY = '\u2591';
		// Aktuális rajzoló karakterek, 'típus' paramétertől függően.
		char bfA = ' ', jfA = ' ', vA = ' ', fA = ' ', jaA = ' ', baA = ' ';
		// Rajzoló karakterek beállítása
		if (Character.toUpperCase(tipus) == 'S') {
			bfA = BFS;
			jfA = JFS;
			vA = VS;
			fA = FS;
			baA = BAS;
			jaA = JAS;
		} else if (Character.toUpperCase(tipus) == 'D') {
			bfA = BFD;
			jfA = JFD;
			vA = VD;
			fA = FD;
			baA = BAD;
			jaA = JAD;
		}
		//Kurzor pozició mentése
		int mSor, mOszlop;
		mSor = aktSor;
		mOszlop = aktOszlop;
		irXY(mSor, mOszlop, bfA + ""); // Bal felső sarok
		sorRajzol(mSor, mOszlop + 1, oszlop - 2, vA); // Első sor, keret teteje
		irXY(mSor, mOszlop + oszlop - 1, jfA + ""); // Jobb felső sarok
		oszlopRajzol(mOszlop, mSor + 1, fA, sor - 1);
		oszlopRajzol(mOszlop + oszlop - 1, mSor + 1, fA, sor - 1);
		if (arnyek)
			oszlopRajzol(mOszlop + oszlop, mSor + 1, ARNY, sor);
		irXY(mSor + sor - 1, mOszlop, baA + ""); // bal alsó sarok
		poz(mSor + sor - 1, mOszlop + 1);
		sorRajzol(mSor + sor - 1, mOszlop + 1, oszlop - 2, vA); // utolsó sor, keret alja
		if (arnyek) {// jobb alsó sarok
			irXY(mSor + sor - 1, mOszlop + oszlop - 1, jaA + ""); // Sarok
			irXY(mSor + sor - 1, mOszlop + oszlop, ARNY + ""); // Mögé az árnyék
		} else {
			irXY(mSor + sor - 1, mOszlop + oszlop - 1, jaA + "");
		}
		if (arnyek) { // Ha kell, árnyék rajzolás
			poz(mSor + sor, mOszlop + 2);
			sorRajzol(mSor + sor, mOszlop + 2, oszlop - 1, ARNY); // Ánryék megrajzolása
		}
		//Fejléc
		if (fejlec.length()>0) {
			if (Character.toUpperCase(tipus)=='S') 
				fejlec='\u2524'+fejlec+'\u251C';
			if (Character.toUpperCase(tipus)=='D') 
				fejlec='\u2561'+fejlec+'\u255E';
			irXY(mSor,(int) (mOszlop+((oszlop-fejlec.length())/2)),fejlec);
		}
		//Kurzor visszaállítása
		aktSor = mSor;
		aktOszlop = mOszlop;
		poz(aktSor, aktOszlop);
	}

	//Kurzor aktuális sora
	public int getAktSor() {
		return aktSor;
	}

	//Kurzor aktuális oszlopa
	public int getAktOszlop() {
		return aktOszlop;
	}
	
} //class Kijelzo