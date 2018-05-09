public class Terem extends Kijelzo {
	static final private int uresSor=3; // v�szon �s els� sor k�z�tti sorok sz�ma
	static final private int vaszonSor=3; //v�szon helye
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
		super(((sor*2)+3),(int) (szekekSzama[sor-1]*2.8)); //L�trehozunk egy "Kijelzot"
		teremSor=(sor*2)+3; //A sorok �s oszlopok alapj�n hozzuk l�tre a Kijelz� m�reteit
		teremOszlop=(int) (szekekSzama[sor-1]*2.8);
		filmCim=fCim;
		jegyAr=jAr;
		teremNev=tNev;
		foglalt=0;
		szabad=0;
		bevetel=0;
		int seged, seged1;
		helyek = new char[sor][]; //L�trehozzuk a sorokat
		for (int i=0;i<szekekSzama.length;i++) // L�trehozzuk a sorokban az �l�helyeket
			helyek[i]=new char[szekekSzama[i]];
		for (int i=0;i<helyek.length;i++) //Minden hely szabad
			for (int j=0;j<helyek[i].length;j++) {
				helyek[i][j]=SZABAD;
				szabad++;
			}	
		this.keret(teremSor, teremOszlop, 'D', false, teremNev+"  *  "+filmCim);
		this.sorRajzol(vaszonSor, 5, teremOszlop-10, vaszon); // Vet�t� v�szon kirajzol�sa
		for (int i=0;i<helyek.length;i++) { // Az �l�helyek kirajzol�sa
			seged=(int) ((teremOszlop-(helyek[i].length)*2)/2); // A terem fel�t�l balra l�v� kezd� poz. soronk�nt
			for (int j=0,o=0;j<helyek[i].length;j++,o=o+1)
				this.irXY(vaszonSor+uresSor+i, seged+j+o, helyek[i][j]+"");
		}
		seged=(int) ((teremOszlop-(helyek[sor-1].length)*2)/2); //Utols� sor fel�t�l balra l�v� kezd� poz.
		seged1=1;
		for (int i=0 ;i<helyek[sor-1].length;i++) { //A utols� sor al� ki�rjuk a sz�kek sz�m�t
			if (seged1==10) seged1=0;
			this.irXY(vaszonSor+uresSor+sor, seged+(i*2), seged1+"");
			seged1++;
		}
		seged1=1;
		for (int i=1 ;i<(helyek[sor-1].length/10)+1;i++) { // A sz�kek sz�ma al� egy seg�d sor, tizes beoszt�s
			this.irXY(vaszonSor+uresSor+sor+1, seged+(i*20)-2, seged1+"");
			seged1++;
		}
		for (int i=1;i<=sor;i++) { //Sorok sz�moz�sa bal �s jobb oldalon
			this.irXY(vaszonSor+uresSor+i-1, 2, i+"");
			this.irXY(vaszonSor+uresSor+i-1, teremOszlop-3, i+"");
		}
		this.irXY(teremSor-2,3,"FOGLALT: "+foglalt);
		this.irXY(teremSor-2,18,"SZABAD: "+szabad);
		this.irXY(teremSor-4,3,"JEGY�R: "+String.format("%6.0f", jegyAr)+" Ft");	
	}
	
	public void foglal(int sor,int oszlop) {
		int seged;
		this.helyek[sor-1][oszlop-1]=FOGLALT; //megfelel� hely foglaltra �ll�t�sa
		seged=(int) ((teremOszlop-(helyek[sor-1].length)*2)/2); //az adott sor els� oszlop�nak kisz�m�t�sa
		this.irXY(vaszonSor+uresSor+sor-1, seged+(oszlop*2)-2, helyek[sor-1][oszlop-1]+""); //foglalt jel ki�r�sa a megfelel sor megfelel�
		//oszlop�ba
		this.foglalt++; //foglalt helyek n�vel�se
		this.bevetel=this.jegyAr*this.foglalt; //bev�tel n�vel�se
		this.szabad--;//szabad helyek cs�kkent�se
		this.irXY(teremSor-2,3,"FOGLALT: "+foglalt); //foglalt �s szabaf �rt�kek aktualiz�l�sa
		this.irXY(teremSor-2,18,"SZABAD: "+szabad);
	}
	
	public void helytorol(int sor,int oszlop) {
		int seged;
		this.helyek[sor-1][oszlop-1]=SZABAD; //megfelel� hely szabadra �ll�t�sa
		seged=(int) ((teremOszlop-(helyek[sor-1].length)*2)/2); //az adott sor els� oszlop�nak kisz�m�t�sa
		this.irXY(vaszonSor+uresSor+sor-1, seged+(oszlop*2)-2, helyek[sor-1][oszlop-1]+""); //szabad jel ki�r�sa a megfelel sor megfelel�
		//oszlop�ba
		this.foglalt--; //eggyel kevesebb foglalt hely van
		this.szabad++; //eggyel t�bb szabad hely van
		this.irXY(teremSor-2,3,"FOGLALT: "+foglalt); //foglalt és szabad helyek aktualizálása
		this.irXY(teremSor-2,18,"SZABAD: "+szabad);
	}
	
	public double getBevetel() {
		return bevetel;
	}
} 

