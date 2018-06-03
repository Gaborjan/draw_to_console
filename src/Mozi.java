import java.io.*;
import java.util.*;
import extra.Menu;
import extra.*;

public class Mozi {
	static Terem[] moziTermek;
	static final String FOMENUPONTOK[] = {"1. Fájlműveletek","2. Foglalás","3. Törlés","4. Teremállapot","0. Program vége"};
	static final String FAJLMUVELETEK[] = {"1. Inicializálás", "2. Foglalás mentés","3. Foglalás betöltés","0. FŐMENÜ"};
	static boolean inicializalasOk = false; //Akkor lesz igaz, ha a létrehozzuk a termeket a mozi_betolt eljárással;
	
	public static void main(String[] args) {
		int menuP=0;
		// !!!Csak a tesztelés idejére, ne kelljen mindig a menüből!!!!
		mozi_betolt();
		inicializalasOk=true;
		do {
			menuP=Menu.egyszeruMenu("Főmenü",FOMENUPONTOK, 5);
			switch (menuP) {
   			case 1:
   				{
   					do {
   						menuP=Menu.egyszeruMenu(FOMENUPONTOK[0].substring(3, FOMENUPONTOK[0].length()),FAJLMUVELETEK, 4);
   						switch (menuP) {
      						case 1:
      						{
      							if (!inicializalasOk ) {
         							mozi_betolt();
         							inicializalasOk=true;
         							System.out.println("Sikeres inicializálás. Betöltött termek:");
         							for (int i=0; i<moziTermek.length;i++)
         								System.out.println(moziTermek[i].getTeremNev());
         							extra.Console.pressEnter();
      							}
      							else
      							{
      								System.out.println("Már volt inicializálás!");
      								extra.Console.pressEnter();
      							}
      							break;
      						}
      						case 2:
      						{
      						
      							break;
      						}
      						case 3:break;
      						case 4:break;
      					} // fájlműveletek switch
   					} while (menuP!=0);
   					menuP=99;
   					break;
   				} // fájlműveletek case ág
   			case 2: // Foglalás
   			{	
   				if (inicializalasOk ) {
						String filmek[] = new String[moziTermek.length+1];
						int jDb; //Foglalni kívánt jegyek száma
						int jSor;// Foglalni kívánt sorok száma
						ArrayList<Integer> jegySzek = new ArrayList<Integer>(); // helyek a sorban
						int jH; // seged változó
						int k; // segedváltozó
						char fEredmeny; //segédváltozó, a foglalás megfelel, nem felel meg, megszakítás
						for (int i=0;i<filmek.length-1;i++)
							filmek[i]=(i+1)+". "+moziTermek[i].getFilmCim()+"___"+moziTermek[i].getTeremNev()+"___"+moziTermek[i].getJegyAr()+ " Ft";
   					filmek[moziTermek.length]="0. Foglalás megszakítása";
   					do {
   						menuP=Menu.egyszeruMenu("Foglalás, film választása",filmek, filmek.length);
   						if (menuP>0) {
   							moziTermek[menuP-1].kiir();
   							jDb=extra.Console.readInt("Hány jegyet kér? (0=Kilépés)");
   							if (jDb>0)
   								{
   									jSor=extra.Console.readInt("Hanyadik sorba kéri? (0=Kilépés)");
   									if (jSor>0)
   										{
   											System.out.println("Adja meg a helyek számát a sorban! (0=Kilépés)");
   											k=0;
   											do {
   												jH=Menu.egesz_Beolvas("Kérem a "+(k+1)+". jegy helyét: ", 0, moziTermek[menuP-1].getSorHelyDarab(jSor), "Hibás adat!");
   												if (jH!=0) {
   													k++;
   													jegySzek.add(jH);
   												}
   											} while (jH!=0 && k<jDb);
   											if (jH==0) {
   												menuP=0;
   												continue;
   											}
   											else
   											{
   												for (int i=1;i<=k;i++)
   													moziTermek[menuP-1].foglal(jSor, jegySzek.get(i-1));
   												moziTermek[menuP-1].kiir();
   												fEredmeny=extra.Console.readChar("Megfelelőek a helyek? <I>gen <N>em <K>ilépés-foglalás megszakítása: ");
   												if (Character.toUpperCase(fEredmeny)=='I') {
   													System.out.printf("Fizetendő összeg: %,6d Ft",jDb*moziTermek[menuP-1].getJegyAr());
   													System.out.println();
   													System.out.println("Fizetés renben? <I>gen/<N>em");
   												}
   												
   											}
   												
   										}
   									else
   									{
   										menuP=0;
   										continue;
   									}
   								}
   							else
   								{
   									menuP=0;
   								}
   						}
   					} while (menuP!=0);
   					System.out.println("---------------------");
						System.out.println("Foglalás megszakítva!");
						System.out.println("---------------------");
					}
					else
					{
						System.out.println("Még nem lettek létrehozva a termek, nem indítható foglalás!");
						extra.Console.pressEnter();
					}
   				menuP=99;
   				break;
   			} // Foglalás case ág
   			case 3:break;
   			case 4:
   			{
   				if (inicializalasOk) {
   					String[] teremNevek = new String[moziTermek.length+1];
   					for (int i=0;i<teremNevek.length-1;i++)
							teremNevek[i]=(i+1)+". "+moziTermek[i].getTeremNev();
   					teremNevek[moziTermek.length]="0. Vissza a FŐMENÜBE";
   					do {
   						menuP=Menu.egyszeruMenu(FOMENUPONTOK[3].substring(3, FOMENUPONTOK[3].length()),teremNevek, teremNevek.length);
   						if (menuP>0) {
   							moziTermek[menuP-1].kiir();
   							extra.Console.pressEnter();
   						}
   					} while (menuP!=0);
   					menuP=99;
   				}
   				else
   				{
   					System.out.println("Még nem lettek létrehozva a termek, nincs mit listázni!");
   					extra.Console.pressEnter();
   				}
   				break;
   			} //Teremállapot case ág
			} //fomenu switch
		} while (menuP!=0); 
		System.out.println("PROGRAM VÉGE");
		
	}

	static void mozi_betolt() {
		//A metódus egy megfelelő tartalmú fájlból beolvassa a szükséges adatokat, ami alapján
		//létrehozzuk a termeket. A fájl tartalmát nem ellenőrizzük, feltesszük, hogy az elvárt
		//módon van létrehozva.
		try {
			RandomAccessFile fajl; //Moziterem adatoknak
			RandomAccessFile fajl1; //Műsor adatoknak
			String egySor;
			int teremDb=0; //Mozitermek száma
			int t=0; //Hanyadik teremnél járunk?
			String seged[]; //String daraboláshoz segéd tömb
			int teremSorok[]; //Melyik teremben hány sor van?
			int teremSzekek[]; //Ebben a tömbben tároljuk melyik sorban hány szék van
			String filmCimek[]; //Ebben a tömbben tároljuk a filmek címeit
			int jegyArak[]; //Ebben a tömbben tároljuk melyik filmre mennyibe kerül a jegy
			fajl=new RandomAccessFile("mozi_adatok.csv","r");
			egySor=fajl.readLine(); // Az első magyarázó sor, nincs adat benne
			egySor=fajl.readLine(); // A mozi termeinek számát tartalmazó 2. sor
			seged=egySor.split(";");
			teremDb=Integer.parseInt(seged[0]); // teremDb=a mozitermek száma
			filmCimek= new String[teremDb];  
			jegyArak= new int[teremDb];
			t=0;
			//Betöltjük a filmek címeit és a jegyárakat, eltesszük 1-1 tömbbe
			try {
				fajl1=new RandomAccessFile("mozi_musor.csv","r");
				egySor=fajl1.readLine();
				while (egySor!=null) {
					seged=egySor.split(";");
					filmCimek[t]=seged[0];
					jegyArak[t]=Integer.parseInt(seged[1]);
					egySor=fajl1.readLine();
					t++;
				}
				fajl1.close();
			}
			catch (IOException e) {
				System.err.println("Hiba történt a fájlművelet közben! (-> Moziműsor adatok <-)");
			} 
			moziTermek= new Terem[teremDb]; // létrehozunk egy tömböt, amely elemei Terem típusúak 
			teremSorok= new int[teremDb]; // Tömb létrehozása a termek sorainak
			egySor=fajl.readLine(); //3. sor beolvasása, melyik teremben hány sor van
			seged=egySor.split(";");
			for (int i=0;i<teremDb;i++) {                //Eltesszük egy tömbbe melyik teremben hány sor van
				teremSorok[i]=Integer.parseInt(seged[i]);
			}
			t=0;
			egySor=fajl.readLine(); // első terem adatainak beolvasása
			while (egySor!=null) {
				seged=egySor.split(";");
				teremSzekek = new int[seged.length-1]; //Kell egy tömb, amiben eltároljuk melyik sorban hány szék van
				for (int i=0;i<seged.length-1;i++) // Feltöltjük a tömböt, amiben a székek számával (soronként)
					teremSzekek[i]=Integer.parseInt(seged[i]);
				//Minden adat megvan, létrehozzunk az adott termet
				moziTermek[t]= new Terem(teremSorok[t],teremSzekek,seged[seged.length-1],jegyArak[t],filmCimek[t]);
				egySor=fajl.readLine(); 
				t++;
			}
			fajl.close();
		}
		catch (IOException e) {
			System.err.println("Hiba történt a fájlművelet közben! (-> Moziterem adatok betöltése <-)");
		}
	}
}




/*mozi_betolt();
for (int i=0;i<moziTermek.length;i++)
	moziTermek[i].kiir();
System.out.println("VÉGE");*/
/*int[] terem1Szekek = {10,10,10,15,15,15,15,15,15,25};
int[] terem2Szekek = {10,10,10,20,20,20,30,30,40,40};
Terem Terem1 = new Terem(10,terem1Szekek,"JÁVOR PÁL TEREM",1990,"CSILLAGOK HÁBORÚJA I.");
Terem1.kiir();
Terem1.foglal(5, 5);
Terem1.foglal(1, 1);
Terem1.foglal(1, 10);
Terem1.foglal(10, 25);
Terem1.foglal(10, 24);
Terem1.foglal(10, 1);
Terem1.kiir();
Terem1.helytorol(1, 1);
Terem1.kiir();
System.out.println(String.format("%,6.0f",Terem1.getBevetel())+" Ft");
Terem Terem2 = new Terem(10,terem2Szekek,"JOHN WILLIAMS TEREM",1590,"VISSZA A JŐVŐBE III.");
Terem2.kiir();
Terem2.foglal(5, 5);
Terem2.foglal(1, 1);
Terem2.foglal(1, 10);
Terem2.foglal(10, 25);
Terem2.foglal(10, 24);
Terem2.foglal(10, 1);
Terem2.kiir();
Terem2.helytorol(1, 1);
Terem2.kiir();
System.out.println(String.format("%,6.0f",Terem2.getBevetel())+" Ft");*/