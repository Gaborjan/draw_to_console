/*Moziterem rajzolására használjuk a kijelzőt, egy kijelző=egy moziterem.*/

public class Terem extends Kijelzo {
	static final private int URESSOR=3; // vászon és első sor közötti sorok száma
	static final private int VASZONSOR=3; //vászon helye
	static final char FOGLALT='X';
	static final char ELOFOGLALT='░';
	static final char SZABAD='*';
	static final private double MERETARANY=2.8; //Arányszám a leghosszabb sor alapján a terem méretéhez
	static final private char VASZON='\u2588'; // Vászon karaktere
	
	private char[][] helyek; //A teremben lévő helyeket tároljuk sor, soronként a székek
	private String filmCim;
	private String teremNev;
	private int jegyAr;
	private int foglalt; // Foglalt helyek száma
	private int szabad; // Szabad helyek száma
	private double bevetel; // A foglaltság alapján a bevétel
	private int teremSor=0; //Terem sorainak a száma ~ kijelző méret sor
	private int teremOszlop=0; // Terem oszlopainak a száma ~ kijelző méret oszlop
	
	//Konstruktor
	public Terem(int sor, int[] szekekSzama, String tNev, int jAr, String fCim) {
		super(((sor*2)+7),(int) (szekekSzama[sor-1]*MERETARANY)); //Létrehozunk egy "Kijelzot"
		if (sor>=10) teremSor=(sor*2)+3;//A sorok és oszlopok alapján hozzuk létre a Kijelző méreteit
		else 
			teremSor=(sor*2)+7; 
		teremOszlop=(int) (szekekSzama[sor-1]*MERETARANY);
		filmCim=fCim;
		jegyAr=jAr;
		teremNev=tNev;
		foglalt=0;
		szabad=0;
		bevetel=0;
		helyek = new char[sor][]; //Létrehozzuk a sorokat
		
		int seged, seged1;
		
		// Létrehozzuk a sorokban az ülőhelyeket
		for (int i=0;i<szekekSzama.length;i++) 
			helyek[i]=new char[szekekSzama[i]];
		//Minden hely szabad
		for (int i=0;i<helyek.length;i++) 
			for (int j=0;j<helyek[i].length;j++) {
				helyek[i][j]=SZABAD;
				szabad++;
			}	
		//Elkezdjük megrajzolni a termet, persze csak virtuálisan
		this.keret(teremSor, teremOszlop, 'D', false, teremNev+"  *  "+filmCim); //Keret fejléccel
		this.sorRajzol(VASZONSOR, 5, teremOszlop-10, VASZON); // Vetítő vászon kirajzolása
		// Az ülőhelyek megrajzolása
		for (int i=0;i<helyek.length;i++) { 
			seged=(int) ((teremOszlop-(helyek[i].length)*2)/2); // A terem felétől balra lévő kezdő poz. soronként
			for (int j=0;j<helyek[i].length;j++)
				this.irXY(VASZONSOR+URESSOR+i, seged+(j*2), helyek[i][j]+"");
		}
		seged=(int) ((teremOszlop-(helyek[sor-1].length)*2)/2); //Utolsó sor felétől balra lévő kezdő poz.
		seged1=1;
		//A utolsó sor alá kiírjuk a székek számát
		for (int i=0 ;i<helyek[sor-1].length;i++) { 
			if (seged1==10) seged1=0;
			this.irXY(VASZONSOR+URESSOR+sor, seged+(i*2), seged1+"");
			seged1++;
		}
		seged1=1;
		// A székek száma alá egy segéd sor, tizes beosztás
		for (int i=1 ;i<(helyek[sor-1].length/10)+1;i++) { 
			this.irXY(VASZONSOR+URESSOR+sor+1, seged+(i*20)-2, seged1+"");
			seged1++;
		}
		//Sorok számozása bal és jobb oldalon
		for (int i=1;i<=sor;i++) { 
			this.irXY(VASZONSOR+URESSOR+i-1, 2, i+"");
			this.irXY(VASZONSOR+URESSOR+i-1, teremOszlop-3, i+"");
		}
		this.irXY(teremSor-2,3,"FOGLALT: "+foglalt);
		this.irXY(teremSor-2,18,"SZABAD: "+szabad);
		this.irXY(teremSor-4,3,"JEGYÁR: "+String.format("%6d", jegyAr)+" Ft");	
	}
	
	public void foglal(int sor,int oszlop) {
		int seged;
		this.helyek[sor-1][oszlop-1]=FOGLALT; //megfelelő hely foglaltra állítása
		seged=(int) ((teremOszlop-(helyek[sor-1].length)*2)/2); //az adott sor első oszlopának kiszámítása
		this.irXY(VASZONSOR+URESSOR+sor-1, seged+(oszlop*2)-2, helyek[sor-1][oszlop-1]+""); //foglalt jel kiírása a megfelelő sor megfelelő
		//oszlopába
		this.foglalt++; //foglalt helyek növelése
		this.bevetel=this.jegyAr*this.foglalt; //bevétel növelése
		this.szabad--;//szabad helyek csökkentése
		this.irXY(teremSor-2,3,"FOGLALT: "+foglalt); //foglalt és szabad értékek aktualizálása
		this.irXY(teremSor-2,18,"SZABAD: "+szabad);
	}
	
	//Foglaláskor a lefoglalt helyek mutatására előfoglalást csinálunk.
	public void elofoglal(int sor, int oszlop) {
	   int seged;
      this.helyek[sor-1][oszlop-1]=ELOFOGLALT; //megfelelő hely foglaltra állítása
      seged=(int) ((teremOszlop-(helyek[sor-1].length)*2)/2); //az adott sor első oszlopának kiszámítása
      this.irXY(VASZONSOR+URESSOR+sor-1, seged+(oszlop*2)-2, helyek[sor-1][oszlop-1]+""); //foglalt jel kiírása a megfelelő sor megfelelő
      //oszlopába
	}
	
	//Folglaláskor, ha nem megfelelőek a helyek, akkor az előfoglalást törölni kell
	public void elofoglaltorol(int sor, int oszlop) {
	   int seged;
      this.helyek[sor-1][oszlop-1]=SZABAD; //megfelelő hely szabadra állítása
      seged=(int) ((teremOszlop-(helyek[sor-1].length)*2)/2); //az adott sor első oszlopának kiszámítása
      this.irXY(VASZONSOR+URESSOR+sor-1, seged+(oszlop*2)-2, helyek[sor-1][oszlop-1]+""); //szabad jel kiírása a megfelelő sor megfelelő
      //oszlopába
	}
	
	public void helytorol(int sor,int oszlop) {
		int seged;
		this.helyek[sor-1][oszlop-1]=SZABAD; //megfelelő hely szabadra állítása
		seged=(int) ((teremOszlop-(helyek[sor-1].length)*2)/2); //az adott sor első oszlopának kiszámítása
		this.irXY(VASZONSOR+URESSOR+sor-1, seged+(oszlop*2)-2, helyek[sor-1][oszlop-1]+""); //szabad jel kiírása a megfelelő sor megfelelő
		//oszlopába
		this.foglalt--; //eggyel kevesebb foglalt hely van
		this.szabad++; //eggyel több szabad hely van
		this.irXY(teremSor-2,3,"FOGLALT: "+foglalt); //foglalt és szabad helyek aktualizálása
		this.irXY(teremSor-2,18,"SZABAD: "+szabad);
	}

	public String getTeremNev() {
		return teremNev;
	}

	public int getJegyAr() {
		return jegyAr;
	}

	public int getFoglalt() {
		return foglalt;
	}

	public int getSzabad() {
		return szabad;
	}
	
	public double getBevetel() {
		return bevetel;
	}
	
	public String getFilmCim() {
		return filmCim;
	}
	
	//Visszaadja hány hely van a megadott sorban
	public int getSorHelyDarab(int sor) {
		return helyek[sor-1].length;  
	}
	
	//Egy adott sor, oszlop-ról megmondja foglalt vagy szabad.
	//Ha Foglalt, a függvény értéke true, ha szabad akkor false.
	public boolean getHelyFoglalt(int sor, int oszlop) {
	   return this.helyek[sor-1][oszlop-1]==FOGLALT; 
	}
	
	//Terem sorainak számát adja vissza
   public int getTeremSor() {
      return helyek.length;
   }
	
   	
} 

