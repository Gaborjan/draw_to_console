public class Terem extends Kijelzo {
	static final private int uresSor=3; // vászon és első sor közötti sorok száma
	static final private int vaszonSor=3; //vászon helye
	static final private char FOGLALT='X';
	static final private char SZABAD='*';
	private char[][] helyek;
	private String filmCim;
	private String teremNev;
	private double jegyAr;
	private int foglalt;
	private int szabad;
	private double bevetel;
	final private char vaszon='\u2588';
	int teremSor=0; 
	int teremOszlop=0;
	
	public Terem(int sor, int[] szekekSzama, String tNev, double jAr, String fCim) {
		super(((sor*2)+3),(int) (szekekSzama[sor-1]*2.8)); //Létrehozunk egy "Kijelzot"
		teremSor=(sor*2)+3; //A sorok és oszlopok alapján hozzuk létre a Kijelző méreteit
		teremOszlop=(int) (szekekSzama[sor-1]*2.8);
		filmCim=fCim;
		jegyAr=jAr;
		teremNev=tNev;
		foglalt=0;
		szabad=0;
		bevetel=0;
		int seged, seged1;
		helyek = new char[sor][]; //Létrehozzuk a sorokat
		for (int i=0;i<szekekSzama.length;i++) // Létrehozzuk a sorokban az ülőhelyeket
			helyek[i]=new char[szekekSzama[i]];
		for (int i=0;i<helyek.length;i++) //Minden hely szabad
			for (int j=0;j<helyek[i].length;j++) {
				helyek[i][j]=SZABAD;
				szabad++;
			}	
		this.keret(teremSor, teremOszlop, 'D', false, teremNev+"  *  "+filmCim);
		this.sorRajzol(vaszonSor, 5, teremOszlop-10, vaszon); // Vetítő vászon kirajzolása
		for (int i=0;i<helyek.length;i++) { // Az ülőhelyek kirajzolása
			seged=(int) ((teremOszlop-(helyek[i].length)*2)/2); // A terem felétől balra lévő kezdő poz. soronként
			for (int j=0,o=0;j<helyek[i].length;j++,o=o+1)
				this.irXY(vaszonSor+uresSor+i, seged+j+o, helyek[i][j]+"");
		}
		seged=(int) ((teremOszlop-(helyek[sor-1].length)*2)/2); //Utolsó sor felétől balra lévő kezdő poz.
		seged1=1;
		for (int i=0 ;i<helyek[sor-1].length;i++) { //A utolsó sor alá kiírjuk a székek számát
			if (seged1==10) seged1=0;
			this.irXY(vaszonSor+uresSor+sor, seged+(i*2), seged1+"");
			seged1++;
		}
		seged1=1;
		for (int i=1 ;i<(helyek[sor-1].length/10)+1;i++) { // A székek száma alá egy segéd sor, tizes beosztás
			this.irXY(vaszonSor+uresSor+sor+1, seged+(i*20)-2, seged1+"");
			seged1++;
		}
		for (int i=1;i<=sor;i++) { //Sorok számozása bal és jobb oldalon
			this.irXY(vaszonSor+uresSor+i-1, 2, i+"");
			this.irXY(vaszonSor+uresSor+i-1, teremOszlop-3, i+"");
		}
		this.irXY(teremSor-2,3,"FOGLALT: "+foglalt);
		this.irXY(teremSor-2,18,"SZABAD: "+szabad);
		this.irXY(teremSor-4,3,"JEGY�R: "+String.format("%6.0f", jegyAr)+" Ft");	
	}
	
	public void foglal(int sor,int oszlop) {
		int seged;
		this.helyek[sor-1][oszlop-1]=FOGLALT; //megfelelő hely foglaltra állítása
		seged=(int) ((teremOszlop-(helyek[sor-1].length)*2)/2); //az adott sor első oszlopának kiszámítása
		this.irXY(vaszonSor+uresSor+sor-1, seged+(oszlop*2)-2, helyek[sor-1][oszlop-1]+""); //foglalt jel kiírása a megfelelő sor megfelel
		//oszlopába
		this.foglalt++; //foglalt helyek növelése
		this.bevetel=this.jegyAr*this.foglalt; //bevétel növelése
		this.szabad--;//szabad helyek csökkentése
		this.irXY(teremSor-2,3,"FOGLALT: "+foglalt); //foglalt és szabad értékek aktualizálása
		this.irXY(teremSor-2,18,"SZABAD: "+szabad);
	}
	
	public void helytorol(int sor,int oszlop) {
		int seged;
		this.helyek[sor-1][oszlop-1]=SZABAD; //megfelelő hely szabadra állítása
		seged=(int) ((teremOszlop-(helyek[sor-1].length)*2)/2); //az adott sor első oszlopának kiszámítása
		this.irXY(vaszonSor+uresSor+sor-1, seged+(oszlop*2)-2, helyek[sor-1][oszlop-1]+""); //szabad jel kiírása a megfelelő sor megfelelő
		//oszlopába
		this.foglalt--; //eggyel kevesebb foglalt hely van
		this.szabad++; //eggyel több szabad hely van
		this.irXY(teremSor-2,3,"FOGLALT: "+foglalt); //foglalt és szabad helyek aktualizálása
		this.irXY(teremSor-2,18,"SZABAD: "+szabad);
	}
	
	public double getBevetel() {
		return bevetel;
	}
} 

