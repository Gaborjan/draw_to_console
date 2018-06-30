/*Java programozási alapok gyakorlása céljából készült program.
 * Egy nagyon egyszerű jegyeladási rendszert szimulálunk. Mivel nem adatbáziskezelés a cél
 * számos egyszerűsítéssel élünk és feltételezzük, hogy szándékosan nem kerülnek helytelen adatok megadásra.
 * (A programban alap ellenőrzések és kivételkezelések vannak, fals adatokat általában nem fogad el.)
 * A jegyértékesítés mindig csak adott napra lehetséges, és feltesszük, hogy egy teremben, egy napon csak egy filmet játszanak.
 * Az értékesítés azzal indul, hogy fájlokban megadott adatok alapján létrehozzuk a termeket illetve ugyancsak
 * fájlból betöltjük a műsort, a jegyárakat - így létrejön az az adatszerkezet, ahová a foglalások rögzíthetőek.
 * Ezután a foglalás, illetve lemondás értelemszerűen, a szokásos módon lehetséges.
 * Bármikor le tudjuk kérdezni egy terem állapotát, ahol látjuk hol és mennyi foglalt hely van.
 * Az aktuális foglaltsági állapot elmenthető egy .csv fájlba, amit utána akár Excellel tovább szerkeszthetünk.
 * Egy foglaltsági állapot vissza is tölthető (előtte a műsorbetöltést meg kell csinálnuk, illetve ha már dolgoztunk
 * a programmal, akkor az aktuális adatok felülíródnak. Ez akkor praktikus, ha valami miatt fel kell függeszteni 
 * az értékesítést (pl. pénztárps ebédszünet, stb.), illetve ha csoportok foglalnak, akkor egyszerűbb Excelben a sok helyet
 * foglalttá tenni és betölteni.
 * A napi mentés a nap végén készül (ha már nem akarunk semmilyem más műveletet elvégezni), és egy fájba mentésre kerülnek
 * a foglalási adatok. Ez a fájl havonta gyűjti az adatokat, vagyis az aktuális napi adatok mindig a végére íródnak. Ennek a fájlnak az
 * adataiból különféle statisztikák kérhetőek le. (Nincs ellenőrzés arra vonatkozóan, hogy adott napon hány napi zárás készült.)
 * A termeknek és a filmek egyedi ID-val rendelkeznek, feltételezzük, hogy ezek megadása helyesen történik a terem- és műsoradatokat
 * tároló fájlokban. 
 * A fájlok tartalma, kötelezően csv formátumban mentendőek.
 * mozi_adatok.csv: 
 * 1. sor: magyarázat
 * 2. sor: a mozi termeinek száma
 * 3. sor: melyik teremben hány sor van
 * 4. sortól: annyi oszlop, ahány sor van, ezekben a székek száma soronként, a terem neve, a terem ID (egyszerű sorszám, feltesszük egyediek - nincs ellenőrzés)
 * mozi_musor.csv:
 * soronként: a film címe, a jegyár és a film ID -> minden filmenek egyedi ID-ja van, egyszerű sorszám, feltesszük egyediek - nincs ellenőrzés 
 * Feltételezzük, hogy annyi filmet adnak meg, ahány terem van, tehát termenként egy filmnek lennie kell.
 * Ha egy teremben nem játszanak semmit, akkor filmcímnek erre utaló szöveget kell megadni, ID-ja 0 (de ez sem kerül sehol ellenőrzésre). Ebben az esetben
 * felételezzük, hogy a kezelő ide nem rögzít foglalást. (Nem kerül ellenőrzésre.) 
 * Az extra csomag Angster Erzsébet "Objektumorientált tervezés és programozás Java I." c. kötetének melléklete - alap adatbekérő metódusok.
 * A Kijelzo csomag is felhasználásra kerül. 
 * Jánvári Gábor 2018.
 */
import java.io.*;
import java.text.DateFormat;
import java.util.*;

//import extra.Menu;
import extra.*;

public class Mozi {
	static Terem[] moziTermek; //Ebben a tömbben tároljuk a mozitermeket
	static final String FOMENUPONTOK[] = {"1. Fájlműveletek","2. Foglalás","3. Lemondás","4. Teremállapot","5. Napi bevétel","6. Havi statisztikák","0. Program vége"};
	static final String FAJLMUVELETEK[] = {"1. Műsorbetöltés", "2. Foglalások kimentése","3. Foglalások betöltése","4. Napi mentés","0. FŐMENÜ"};
	static final String STATISZTIKAK[] = {"1. Terem kihasználtság és bevétel","2. Filmnézettség és bevétel","3. Játszott filmek termek szerint","0. FŐMENÜ"};
	static final char HIBA_UZ_MINTA='*';
	static final char TAJ_UZ_MINTA='-';
	static boolean inicializalasOk = false; //Akkor lesz igaz, ha a létrehozzuk a termeket a mozi_betolt eljárással;
	static double kezKtg=0.1; // Lemondásnál ennyi kezelési költséget vonunk le a lemondott jegyek árából
	static boolean napiMentesVolt=false;
		
	public static void main(String[] args) {
		int menuP=0;
		char fBiztos; // Segédváltozó
	   // !!!Csak a tesztelés idejére, ne kelljen mindig a menüből!!!!
		mozi_betolt();
		inicializalasOk=true;
		do {
			menuP=Menu.egyszeruMenu("Főmenü",FOMENUPONTOK, 7);
			switch (menuP) {
   			case 1: //Főmenü/Fájlműveletek
   				{
   					do {
   						menuP=Menu.egyszeruMenu(FOMENUPONTOK[0].substring(3, FOMENUPONTOK[0].length()),FAJLMUVELETEK, 5);
   						switch (menuP) {
      						case 1: // Fájlműveletek/Műsorbetöltés
      						{
      							inicializalas();
      							break;
      						} //case 1 Műsorbetöltés
      						case 2: // Fájlműveletek/Foglalások kimentése
      						{
      							if (inicializalasOk) {
      								fajlbament();
      							}
      							else
      							{
      								hibaUzenet("Még nem történt meg a műsorbetöltés, a termek nincsenek létrehozva, nincs mit menteni!",true);
      							}
      							break;
      						} // case 2 Foglalások kimentése
      						case 3: {// Fájlműveletek/Foglalások betöltése
      							if (inicializalasOk) {
      							   hibaUzenet("FIGYELEM! \nHa ezt a funkciót választja, akkor az aktuális foglalási adatok \nfelülírásra kerülnek!",false);
      								do {
                                 fBiztos=extra.Console.readChar("Biztos benne? <I>gen / <N>em ");
                                 fBiztos=Character.toUpperCase(fBiztos);
                              } while (fBiztos!='N' && fBiztos!='I');
      								if (fBiztos=='I')
      									fajlbolbetolt();
      								else {
      								   tajUzenet("Az aktuális foglalási adatok nem kerültek felülírásra.",false);
      								}
      							}
      							else
      							{
      								hibaUzenet("Még nem történt meg a műsorbetöltés, a termek nincsenek létrehozva, nincs hová adatot tölteni!",true);
      							}
      							break; 
      						} //case 3 foglalások betöltése 
      						case 4: { //Fájlműveletek/Napi mentés
      						   if (inicializalasOk) {
                              napimentes();
                           }
                           else
                           {
                              hibaUzenet("Még nem történt meg a műsorbetöltés, a termek nincsenek létrehozva, nincs mit menteni!",true);
                           }
                           break;
      						} //case 4 napi mentés 
   						} // fájlműveletek switch
   					} while (menuP!=0); //Amíg a Fájlműveletekből nem lépünk ki
   					menuP=99; // Így nem lépünk ki a Főmenűből
   					break;
   				} // fájlműveletek case ág (FŐMENÜ)
   			case 2: // Főmenü/Foglalás
   			{	
   				if (inicializalasOk && !napiMentesVolt) {
   				   foglalas();
 					}
					else
					{
						if (!inicializalasOk) {  
							hibaUzenet("Még nem történt meg a műsorbetöltés, a termek nincsenek létrehozva, nem indítható foglalás!",true);
						}
						else
						{
						   hibaUzenet("Már volt napimentés, ezért újabb foglalás már nem lehetséges, az adatok lezárásra kerültek!",true);
						}
						extra.Console.pressEnter();
					}
   				menuP=99;
   				break;
   			} // Foglalás case 2 ág
   			case 3: // Főmenü/Lemondás
   			{
   			   if (inicializalasOk ) {
                  visszavet();
               }
               else
               {
                  hibaUzenet("Még nem történt meg a műsorbetöltés, a termek nincsenek létrehozva, nem lehet foglalást lemondani!",true);
               }
               menuP=99;
               break;
   			} // case 3, főmenü/lemondás
   			case 4: //Főmenü/Teremállapot
   			{
   				if (inicializalasOk) {
   				   terem_allapot();
   					menuP=99;
   				}
   				else
   				{
   					hibaUzenet("Még nem történt meg a műsorbetöltés, a termek nincsenek létrehozva,, nincs mit listázni!",true);
   				}
   				break;
   			} //Teremállapot case 4 ág
   			case 5: //Főmenű/Napi bevétel
   			{
   			   if (inicializalasOk) {
                  beveteli_adatok();
                  menuP=99;
               }
               else
               {
                  hibaUzenet("Még nem történt meg a műsorbetöltés, a termek nincsenek létrehozva, bevételi adatok nem jeleníthetőek meg!",true);
               }
               break;  
   			}// Bevételi adatok case 5 ág
   			case 6: //Főmenü/Statisztikák
   			{
   			   do {
   			      menuP=Menu.egyszeruMenu(FOMENUPONTOK[5].substring(3, FOMENUPONTOK[5].length()),STATISZTIKAK, 4);
   			      switch (menuP) {
                     case 1: // Statisztikak/Terem kihasználtság
                     {
                        terem_kihasznaltsag();
                        menuP=99;
                        break;
                      } // Terem kihasználtság case 2 ág
                     case 2: //Statisztikák/Filmnézettség 
                     {
                        film_nezettseg();
                        menuP=99;
                        break;
                     } // Filmnézettség case 2 ág
                     case 3: //Statisztikák/Játszott filmek termek szerint 
                     {
                        termekben_A_Filmek();
                        menuP=99;
                        break;
                     } // Filmnézettség case 2 ág
               } //Statisztikák fömenő switch
            } while (menuP!=0); //Amíg a Statisztikákból nem lépünk ki
            menuP=99; // Így nem lépünk ki a Főmenűből
            break;
   			}//Statisztikák case ág
			} //fomenu switch
		} while (menuP!=0); 
		System.out.println("PROGRAM VÉGE");
	} // main metódus
	
	
	// *** METÓDUSOK***
	
	
	static void inicializalas() {
	   //A metódus meghívja a mozi_betolt metódust, majd kiírja a betöltött termek adatait.
	   if (!inicializalasOk) {
	      if (mozi_betolt()) { // Ha a mozi_betolt sikeresen lefut, nem volt hiba
            inicializalasOk=true;
            tajUzenet("Sikeres műsorbetöltés. Betöltött termek:",false);
            for (int i=0; i<moziTermek.length;i++)
               System.out.println(moziTermek[i].getTeremNev());
            extra.Console.pressEnter();
         }
	   }
      else
      {
         hibaUzenet("Már volt műsorbetöltés!",true);
      }   
	} // inicializalas metódus
	
	static boolean mozi_betolt() {
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
		   int fID[]; //FilmID-kat tárolja
			fajl=new RandomAccessFile("mozi_adatok.csv","r");
			egySor=fajl.readLine(); // Az első magyarázó sor, nincs adat benne
			egySor=fajl.readLine(); // A mozi termeinek számát tartalmazó 2. sor
			seged=egySor.split(";");
			teremDb=Integer.parseInt(seged[0]); // teremDb=a mozitermek száma
			filmCimek= new String[teremDb];  
			jegyArak= new int[teremDb];
			fID = new int [teremDb];
			t=0;
			
			
			//Betöltjük a filmek címeit és a jegyárakat, eltesszük 1-1 tömbbe
			try {
				fajl1=new RandomAccessFile("mozi_musor.csv","r");
				egySor=fajl1.readLine();
				while (egySor!=null) {
					seged=egySor.split(";");
					filmCimek[t]=seged[0];
					jegyArak[t]=Integer.parseInt(seged[1]);
					fID[t]=Integer.parseInt(seged[2]);
					egySor=fajl1.readLine();
					t++;
				}
				fajl1.close();
			}
			catch (IOException e) {
			   hibaUzenet("Hiba történt a fájlművelet közben! (-> Moziműsor adatok <-)",true);
			   fajl.close();
			   return false;
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
				teremSzekek = new int[seged.length-2]; //Kell egy tömb, amiben eltároljuk melyik sorban hány szék van
				//Feltöltjük a tömböt, amiben a székek számával (soronként), utolsó két elem a terem neve és ID-ja ezért length-2
				for (int i=0;i<seged.length-2;i++) 
					teremSzekek[i]=Integer.parseInt(seged[i]);
				//Minden adat megvan, létrehozzunk egy adott termet
				moziTermek[t]= new Terem(teremSorok[t],teremSzekek,seged[seged.length-2],jegyArak[t],filmCimek[t],Integer.parseInt(seged[seged.length-1]),fID[t]); 
				egySor=fajl.readLine(); 
				t++;
			}
			fajl.close();
			return true;
		}
		catch (IOException e) {
			hibaUzenet("Hiba történt a fájlművelet közben! (-> Moziterem adatok betöltése <-)",true);
		   return false;
		}
	} // mozibetolt metódus
	
	static void foglalas() {
	   
	   int menuP=0;
	   String filmek[] = new String[moziTermek.length+1]; // a filmválasztás menüpontjait tároljuk ebben a tömbben -> teremnév, filmcím és jegyár 
      int jDb; //Foglalni kívánt jegyek száma
      int jSor;// Foglalni kívánt sorok száma
      ArrayList<Integer> jegySzek = new ArrayList<Integer>(); // foglalni kívánt helyek a sorban
      int jH; // seged változó
      int k; // segedváltozó
      char fEredmeny='N'; //segédváltozó, a foglalás megfelel, nem felel meg, megszakítás
      boolean sikeresFoglalas=false; // Ha a foglalás teljesen végigment, akkor lesz igazra állítva, egyébként hamis
      boolean foglalte; //Adatbekéréskor ellenőrizzük egy adott hely foglalt-e már esetleg
      
      for (int i=0;i<filmek.length-1;i++) // A filmválasztás menü menüpontjainak előállítása
         filmek[i]=(i+1)+". "+moziTermek[i].getFilmCim()+"___"+moziTermek[i].getTeremNev()+"___"+moziTermek[i].getJegyAr()+ " Ft";
      filmek[moziTermek.length]="0. Foglalás megszakítása"; // Kell egy kilépés menüpont is
      do { // Ciklus, míg ki nem lépünk filmválasztásból
         menuP=Menu.egyszeruMenu("Foglalás, film választása",filmek, filmek.length); //Melyik filmre akarunk foglalni?
         if (menuP>0) {
            while (fEredmeny=='N' && menuP>0) { //Ciklus, míg nem lépünk ki a film menüből vagy nem lett megszakítva valahol a foglalási folyamat
               moziTermek[menuP-1].kiir(); // A választott termet megjelenítjük a választás megkönnyítésére
               jDb=extra.Console.readInt("Hány jegyet kér? (0=Kilépés)");
               if (jDb>0)
                  {
                     jSor=Menu.egesz_Beolvas("Hányadik sorba kéri? (Max.: "+moziTermek[menuP-1].getTeremSor()+" Kilépés = '0') ",0,moziTermek[menuP-1].getTeremSor(),"Hibás adat!");
                     if (jSor>0)
                        {
                           System.out.println("Adja meg a helyek számát a sorban! (0=Kilépés)  "); 
                           k=0; //ebben tároljuk hányadik jegynél járunk
                           do { // ciklus amíg nem szakítja meg a foglalást illetve amíg nem kértük be az összes helyet
                              //Csak érvényes adatot fogadunk el, figyeljük hány hely van a sorban, de nem nézzük kétszer megadják-e ugyanazt a helyet
                              do {
                                 jH=Menu.egesz_Beolvas("Kérem a "+(k+1)+". jegy helyét (Max.:"+moziTermek[menuP-1].getSorHelyDarab(jSor)+"): ", 0, moziTermek[menuP-1].getSorHelyDarab(jSor), "Hibás adat!");
                                 if (jH!=0) { // Ha nem lépünk ki ellenőrizzuk, hogy a megadott hely esetleg foglalt-e már
                                    foglalte=moziTermek[menuP-1].getHelyFoglalt(jSor, jH);
                                    if (foglalte)
                                       System.out.println(" ***** A "+jSor+". SOR "+jH+" . HELYE MÁR FOGLALT! ***** ");
                                 }
                                 else foglalte=false; // 0-t kaptunk, így kilépünk a bekérő ciklusból
                              } while (foglalte);
                              if (jH!=0) {
                                 k++; // 
                                 jegySzek.add(jH); // eltároljuk a foglalt szék számát
                              }
                           } while (jH!=0 && k<jDb); 
                           if (jH==0) { //ha megszakítottuk a foglalást a ciklus elejére ugrunk, ahol ki is lépünk
                              menuP=0;
                              continue;
                           }
                           else // megkaptuk a foglalni kívánt helyeket
                           {
                              for (int i=1;i<=k;i++) //Csinálunk egy előfoglalást, hogy ellenőrizni tudjuk megfelelőek-e a helyek
                                 moziTermek[menuP-1].elofoglal(jSor, jegySzek.get(i-1));
                              moziTermek[menuP-1].kiir(); // Megadott helyeket megjelenítjük
                              do { // Rákérdezünk rendben vannak-e a helyek?
                                 fEredmeny=extra.Console.readChar("Megfelelőek a helyek? <I>gen <N>em <K>ilépés-foglalás megszakítása:  ");
                                 fEredmeny=Character.toUpperCase(fEredmeny);
                              } while (fEredmeny!='N' && fEredmeny!='I' && fEredmeny!='K');
                              if (fEredmeny=='N') { //Ha nem megfelelő a hely
                                 for (int i=1;i<=k;i++) // Az előfoglalást törölni kell, ismét bekérjük  majd a helyeket
                                    moziTermek[menuP-1].elofoglaltorol(jSor, jegySzek.get(i-1));
                                 jegySzek.clear(); // A tárolt helyeket törölni kell, mivel ismét be fogjuk kérni
                              }
                              else if (fEredmeny=='K') { // Kilépés a foglalási folyamatból
                                 for (int i=1;i<=k;i++)
                                    moziTermek[menuP-1].elofoglaltorol(jSor, jegySzek.get(i-1)); // Előfoglalás törlése   
                                 menuP=0; 
                                 continue; // Ciklus elejére ugrunk, ahol ki ki lépünk
                              }
                                 else // Megfelelőek a választott helyek
                                 {
                                    if (fEredmeny=='I') { // Jegyek árának kiírása
                                       System.out.printf("Fizetendő összeg: %,6d Ft",jDb*moziTermek[menuP-1].getJegyAr());
                                       System.out.println();
                                       do { // Itt történik meg a fizetés, majd rákérdezünk rendben van-e?
                                          fEredmeny=extra.Console.readChar("Fizetés rendben? <I>gen <N>em  ");
                                          fEredmeny=Character.toUpperCase(fEredmeny);
                                       } while (fEredmeny!='N' && fEredmeny!='I');
                                       if (fEredmeny=='N') { //Ha a fizetés nem volt sikeres, elofoglalást töröljük és tájékoztatást adunk
                                          for (int i=1;i<=k;i++)
                                             moziTermek[menuP-1].elofoglaltorol(jSor, jegySzek.get(i-1)); 
                                          hibaUzenet("Sikertelen fizetés!",true);
                                          menuP=0;
                                          continue; // Ciklus elejére ugrunk is ki is lépünk
                                       } 
                                       else { // Sikeres volt a fizetés
                                          for (int i=1;i<=k;i++) // Most már véglegesen lefoglaljuk a helyeket a teremben és tájékoztatást adunk
                                             moziTermek[menuP-1].foglal(jSor, jegySzek.get(i-1));
                                          sikeresFoglalas=true;
                                          tajUzenet("Sikeres foglalás!",false);
                                          System.out.println("Sor száma: "+jSor);
                                          System.out.println("Eladott helyek: "+jegySzek);
                                          do { // Ellenőrzésképpen kér-e teremállapotot?
                                             fEredmeny=extra.Console.readChar("Teremállapot? <I>gen <N>em  ");
                                             fEredmeny=Character.toUpperCase(fEredmeny);
                                          } while (fEredmeny!='N' && fEredmeny!='I');
                                          if (fEredmeny=='I') { // kér teremállapotot
                                             moziTermek[menuP-1].kiir();
                                             extra.Console.pressEnter();
                                          }
                                          menuP=0; // Ciklus elejére lépünk és ki is ugrunk
                                          continue;
                                       }  //sikeres fizetés 
                                    } // megfelelőek a választot helyek
                                 }  //megfelelőek a választott helyek 
                           } // megkaptuk a foglalni kívánt helyeket
                         } // megkaptuk hagyadik sorba kéri, nem szakította meg akkor a folyamatot
                     else // A sor bekérésnél 0-t kaptunk foglalás megszakítása
                     {
                        menuP=0; // Ciklus elejére ugrunk és kilépünk
                        continue;
                     }
                  } // Megkaptuk hány db jegyet kér
               else // A jegy darabszámának 0-t kaptunk, foglalás megszakítása
                  {
                     menuP=0; // Kilépés a ciklusból
                  }
            } //while, amíg foglalás nem sikeres, és nem kell kilépnünk a menüből
         }
      } while (menuP!=0); // Vissza a főmenübe, ha 0-t ad meg
      if (!sikeresFoglalas) { // A foglalás valahol meg lett szakítva a folyamat során, így ekkor tájékoztatást adunk
         hibaUzenet("Foglalás megszakítva!",true);
      }
	} // folglalas metódus
	
	static void terem_allapot() {
	   int menuP=0;
	   
	   String[] teremNevek = new String[moziTermek.length+1];
      for (int i=0;i<teremNevek.length-1;i++)
         teremNevek[i]=(i+1)+". "+moziTermek[i].getTeremNev()+"___"+moziTermek[i].getFilmCim();
      teremNevek[moziTermek.length]="0. Vissza a FŐMENÜBE";
      do {
         menuP=Menu.egyszeruMenu(FOMENUPONTOK[3].substring(3, FOMENUPONTOK[3].length()),teremNevek, teremNevek.length);
         if (menuP>0) {
            moziTermek[menuP-1].kiir();
            extra.Console.pressEnter();
         }
      } while (menuP!=0);
	} // teremallapot metódus
	
	static void visszavet() {
	   int menuP=0;
      String filmek[] = new String[moziTermek.length+1]; // a filmválasztás menüpontjait tároljuk ebben a tömbben -> teremnév, filmcím és jegyár 
      int lDb; //Lemondani kívánt jegyek száma
      int lSor; //Lemondani kívánt jegyek sora
      ArrayList<Integer> jegySzek = new ArrayList<Integer>(); // lemondani kívánt helyek a sorban
      char lEredmeny='N'; //segédváltozó, a lemondás sikeresen megtörtént
      boolean sikeresLemondas=false; // Ha a foglalás teljesen végigment, akkor lesz igazra állítva, egyébként hamis
      int k; //segédváltozó
      int lH; //segédváltozó
      for (int i=0;i<filmek.length-1;i++) // A filmválasztás menü menüpontjainak előállítása
         filmek[i]=(i+1)+". "+moziTermek[i].getFilmCim()+"___"+moziTermek[i].getTeremNev()+"___"+moziTermek[i].getJegyAr()+ " Ft";
      filmek[moziTermek.length]="0. Lemondás megszakítása"; // Kell egy kilépés menüpont is
      do { // Ciklus, míg ki nem lépünk filmválasztásból
         menuP=Menu.egyszeruMenu("Lemondás, film választása",filmek, filmek.length); //Melyik filmre volt a foglalás?
         if (menuP>0) {
            while (lEredmeny=='N' && menuP>0) { //Ciklus, míg nem lépünk ki a film menüből vagy nem lett megszakítva valahol a lemondási folyamat
               moziTermek[menuP-1].kiir(); // A választott termet megjelenítjük a választás megkönnyítésére
               lSor=Menu.egesz_Beolvas("Hányadik sorba szól a jegy? (Max.: "+moziTermek[menuP-1].getTeremSor()+" Kilépés = '0') ",0,moziTermek[menuP-1].getTeremSor(),"Hibás adat!");
               if (lSor!=0) { //Ha nem szakítjuk meg a lemondást
                  lDb=extra.Console.readInt("Hány darab jegyet mond le a "+lSor+". sorban? (0=Kilépés) ");
                  if (lDb!=0) {
                     System.out.println("Adja meg a helyek számát a sorban! (0=Kilépés)  "); 
                     k=0; //ebben tároljuk hányadik jegynél járunk
                     do { // ciklus amíg nem szakítja meg a lemondást illetve amíg nem kértük be az összes helyet az adott sorban
                        //Csak érvényes adatot fogadunk el, figyeljük hány hely van a sorban, de nem nézzük tényleg foglalt-e a hely, mert
                        //elvileg ilyen nem fordulhat elő, hiszen ott a jegy a kezünkben.
                        lH=Menu.egesz_Beolvas("Kérem a "+(k+1)+". jegy helyét (Max.:"+moziTermek[menuP-1].getSorHelyDarab(lSor)+"): ", 0, moziTermek[menuP-1].getSorHelyDarab(lSor), "Hibás adat!");
                        if (lH!=0) {
                           k++; // 
                           jegySzek.add(lH); // eltároljuk a lemondott szék számát
                        }
                     } while (lH!=0 && k<lDb); 
                     if (lH==0) { //ha megszakítottuk a lemondást a ciklus elejére ugrunk, ahol ki is lépünk
                        menuP=0;
                        continue;
                     }
                     else { // Megkaptunk minden adatot, jöhet a foglalás lemondása
                        tajUzenet("LEMONDÁSRA KERÜLŐ HELYEK:",false);
                        System.out.println("Sor száma: "+lSor);
                        System.out.println("Lemondott helyek: "+jegySzek);
                        System.out.printf("Visszajáró összeg %,6.0f Ft",((lDb*moziTermek[menuP-1].getJegyAr())*(1-kezKtg)));
                        System.out.println();
                        do { // Rákérdezünk rendben vannak-e a helyek?
                           lEredmeny=extra.Console.readChar("A megadott adatok helyesek? <I>gen <N>em <K>ilépés-lemondás megszakítása:  ");
                           lEredmeny=Character.toUpperCase(lEredmeny);
                        } while (lEredmeny!='N' && lEredmeny!='I' && lEredmeny!='K');
                        if (lEredmeny=='N') { //Nem megfelelő adatok, ismét be fogjuk kérni őket
                           jegySzek.clear();
                        }
                        else if (lEredmeny=='K') { // Kilépés, lemondás megszakítása, ciklus elejére ugrunk
                           menuP=0;
                           continue;
                        }
                        else { // A foglalás törölhető ténylegesen
                           sikeresLemondas=true;
                           for (int i=0;i<jegySzek.size();i++)
                              moziTermek[menuP-1].helytorol(lSor,jegySzek.get(i));
                           tajUzenet("LEMONDÁS SIKERES, HELYEK FELSZABADÍTVA!",false);
                           System.out.println("Sor száma: "+lSor);
                           System.out.println("Lemondott helyek: "+jegySzek);
                           do { // Ellenőrzésképpen kér-e teremállapotot?
                              lEredmeny=extra.Console.readChar("Teremállapot? <I>gen <N>em  ");
                              lEredmeny=Character.toUpperCase(lEredmeny);
                           } while (lEredmeny!='N' && lEredmeny!='I');
                           if (lEredmeny=='I') { // kér teremállapotot
                              moziTermek[menuP-1].kiir();
                              extra.Console.pressEnter();
                           }
                           menuP=0; // Ciklus elejére lépünk és ki is ugrunk
                           continue;
                        } //Sikeres foglalás törlés ág
                     } //Adatbekérés ág
                  } //Lemondaandó helyekdarabszáma ág
                  else { // Jegy darabszám megadásánál 0-t kaptunk, megszakítjuk a lemondást
                     menuP=0;
                     continue;
                  }
               } //Sor megadása ár
               else { //A sor bekérésnél 0-t kaptunk, a ciklus elejére ugrunk és kilépünk
                  menuP=0;
                  continue;
               }
            } // Ciklus vége, amíg nem lépünk ki a menüből, vagy nem szakítjuk meg a lemondást 
         } //Filmválasztó ág vége
      } //Ciklus vége, filmválasztás
      while (menuP!=0); // Vissza a főmenübe, ha 0-t ad meg
      if (!sikeresLemondas) { // A foglalás valahol meg lett szakítva a folyamat során, így ekkor tájékoztatást adunk
         tajUzenet("Lemondás megszakítva!",true);
      }
	} // visszavét metódus
	
	static void fajlbament() {
		// fajlbament metódus
		String egySor="";
		RandomAccessFile fajl;
		String fajlNev="";
		GregorianCalendar naptar = new GregorianCalendar();
		try {
			//Előállítjuk a fájl nevét, ami 'Foglalások_'+dátum+idő másodperces pontossággal
		   fajlNev="Foglalások_";
			fajlNev=fajlNev+naptar.get(Calendar.YEAR)+"_"+naptar.get(Calendar.MONTH)+"_"+naptar.get(Calendar.DAY_OF_MONTH)+"_";
			fajlNev=fajlNev+naptar.get(Calendar.HOUR_OF_DAY)+"_"+naptar.get(Calendar.MINUTE)+"_"+naptar.get(Calendar.SECOND);
			fajlNev=fajlNev+".csv";
			fajl=new RandomAccessFile(fajlNev, "rw");
			egySor="";
			fajl.writeBytes("#\n");//A termek adatait a kettőskereszt választja el egymástól
			for (int i=0;i<moziTermek.length;i++) { //A mozitermek mindegyikét feldolgozzuk
				fajl.writeBytes(moziTermek[i].getTeremNev()+"\n");
				fajl.writeBytes(moziTermek[i].getFilmCim()+"\n");
				egySor="";
				egySor=egySor+moziTermek[i].getTeremSor(); // Adott terem sorainak száma
				for (int j=1;j<=moziTermek[i].getTeremSor();j++) // Kiírjuk melyik sorban hány hely van
					egySor=egySor+";"+moziTermek[i].getSorHelyDarab(j);
				fajl.writeBytes(egySor+"\n");
				for (int k=1;k<=moziTermek[i].getTeremSor();k++) { //A ciklusban az egyes sorokban lévő helyeket írjuk ki
					egySor="";
					for (int j=1;j<=moziTermek[i].getSorHelyDarab(k);j++) { //A ciklus egy sort ír ki a fájlba
						if (moziTermek[i].getHelyFoglalt(k,j)) //Attól függően, hogy foglalt vagy üres, más karaktert írunk ki
							egySor=egySor+";"+Terem.FOGLALT;
						else
							egySor=egySor+";"+Terem.SZABAD;
					}//Egy sor kiírása ciklus
					fajl.writeBytes(egySor+"\n");
				}//Terem helyek kiírása ciklus
				fajl.writeBytes("Szabad helyek: "+moziTermek[i].getSzabad()+"\n");
				fajl.writeBytes("Foglalt helyek: "+moziTermek[i].getFoglalt()+"\n");
				fajl.writeBytes("Bevétel foglaltság alapján: "+moziTermek[i].getBevetel()+"\n");
				if (i<moziTermek.length-1) //Új terem következik, ezért elválasztót írunk ki, ha nincs több terem, a fájl végére ### kerül 
					fajl.writeBytes("#"+"\n");
				else
					fajl.writeBytes("###"+"\n");
			} // Mozitermeket feldololgozó ciklus
			fajl.close();
			tajUzenet("A foglalási adatok kiírása sikeresen megtörtént! A fájl neve: "+fajlNev,true);
		}
		catch (IOException e) {
			hibaUzenet("Hiba történ a fájlba írás közben!",true);
		}
	}
	
	static void fajlbolbetolt() {
	   //A metódusban feltételezzük, hogy a kiírt fájlban lévő adatok megfelelnek a termek méreteinek, vagyis ha megváltoztak a termek,
	   //akkor nem akarunk olyat adatokat betölteni, amelyek nem az aktuális terem szerintiek. Ilyen ellenőrzést nem végzünk.
	   //Feltesszük továbbá, hogy volt műsorbetöltés, azt külön kell megtenni.
	   
		String fNev=""; // A betöltendő fájl neve
		RandomAccessFile fajl;
		String egySor="";
		String [] helyAdat; //Egy sor helyeit tároljuk majd benne
		int szabad=0; // szabad helyek száma
		int foglalt=0; // foglalt helyek száma
				
		do { //ciklus kilépésig
			//fNev=extra.Console.readLine("Adja meg a betöltendő fájl nevét! (Kilépés=0)");
			fNev="FOGPL1.csv";
			try {
				if (!(fNev.equals("0"))) // Ha nem kilépés
					fajl= new RandomAccessFile(fNev.toString(),"r");
				else
					continue; //Ha 0-t adtak meg a ciklus végére ugrunk és ki is lépünk majd
				for (int i=0;i<moziTermek.length;i++) { //A termek mindegyikét feldolgozzuk
					egySor=fajl.readLine();//#
					egySor=fajl.readLine();//Teremnév
					//Az Excel telenyomja elválasztóval a fájlt mentés után, ezért csak az első elválasztóig lévő szöveget vesszük ki
					moziTermek[i].setTeremNev(egySor.substring(0, egySor.indexOf(';')));
					egySor=fajl.readLine();//FilmCím
					moziTermek[i].setFilmCim(egySor.substring(0, egySor.indexOf(';')));
					egySor=fajl.readLine();//Sor és oszlopok száma
					foglalt=0;
					szabad=0;
					for (int j=1;j<=moziTermek[i].getTeremSor();j++) { // Egyesével bekérjük a sorokat
						egySor=fajl.readLine();
						helyAdat = new String [moziTermek[i].getSorHelyDarab(j)]; //Létrehozzuk a tömböt annyi elemmel, ahány hely van a sorban
						helyAdat=egySor.split(";"); //szétszedjük a sort 
						for (int k=0;k<helyAdat.length;k++) { // Az egyes helyeket beállítjuk annak megfelelően, hogy foglaltak (X) vagy szabadok (*)
							if (helyAdat[k].equals(Terem.FOGLALT+"")) { // Foglalt esetben foglalunk
								moziTermek[i].foglal(j,k);
								foglalt++; //számoljuk a foglalt helyeket
							}
							else if (helyAdat[k].equals(Terem.SZABAD+"")) { // Szabad esetben a helyet felszabadítjuk 
								moziTermek[i].helytorol(j, k);
								szabad++; // számoljuk a szabad helyeket
							}
						} // helyek beállítása
					} // egy terem egy sorai
					//extra.Console.pressEnter();
					moziTermek[i].setFoglalt(foglalt); // külön beállítjuk mennyi foglalt és szabad hely volt a fájl adataiban
					moziTermek[i].setSzabad(szabad);
					moziTermek[i].helyAktualizal(); // szükség van még egy kiírás aktualizálásra is, különben a régi adatok a kijelzőn maradhatnak
					egySor=fajl.readLine(); //Szabad helyek
					egySor=fajl.readLine(); //Foglalt helyek
					egySor=fajl.readLine(); //Bevétel
				} // mozitermek darabszáma
				fajl.close();
				tajUzenet("A(z) "+fNev+" fájl betöltése sikeres!",true);
				fNev="0";
			}
			catch (IOException e ) {
				hibaUzenet("A megadott fájlt nem sikerült megnyitni!",true);
			}
		} while (!(fNev.equals("0")));
	}// fajlbolbetolt
	
	static void beveteli_adatok() {
	   int bsor = 8+moziTermek.length; // Ennyi sora lesz a bevétel képernyőnek
	   Kijelzo bevetel = new Kijelzo(bsor, 120); //Létrehozzunk a képernyőt
	   double osszBevetel=0; 
	   Calendar c = Calendar.getInstance();
	   DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.MEDIUM, Locale.getDefault());
	   
	   bevetel.keret(bsor, 115, 'D', false, " N A P I      B E V É T E L    "+df.format(c.getTime())+" ");
	   //Fejléc készítése
	   bevetel.irXY(2, 2, "Ssz.");
	   bevetel.oszlopRajzol(7, 2, Kijelzo.FS, moziTermek.length+2);
	   bevetel.irXY(2, 9, "Terem");
	   bevetel.oszlopRajzol(32, 2, Kijelzo.FS, moziTermek.length+2);
	   bevetel.irXY(2, 34, "Film");
	   bevetel.oszlopRajzol(60, 2, Kijelzo.FS, moziTermek.length+2);
	   bevetel.irXY(2, 66, "Jegyár");
	   bevetel.oszlopRajzol(73, 2, Kijelzo.FS, moziTermek.length+2);
	   bevetel.irXY(2, 75 , "Foglalt");
	   bevetel.oszlopRajzol(83, 2, Kijelzo.FS, moziTermek.length+2);
	   bevetel.irXY(2,85," Szabad");
	   bevetel.oszlopRajzol(93, 2, Kijelzo.FS, moziTermek.length+2);
	   bevetel.irXY(2, 105, "Bevétel");
	   bevetel.sorRajzol(3, 2, 110, '-');
	   for (int i=0;i<moziTermek.length;i++ ) { //Minden termet feldolgozunk
	      //Kírjuk a szükséges adatokat
	      bevetel.irXY(4+i, 2, String.format("%3d",(i+1))+".");
	      bevetel.irXY(4+i, 9, levag(moziTermek[i].getTeremNev(),20));
	      bevetel.irXY(4+i, 34, levag(moziTermek[i].getFilmCim(),25));
	      bevetel.irXY(4+i, 64, String.format("%,5d", moziTermek[i].getJegyAr())+" Ft");
	      bevetel.irXY(4+i, 75 , String.format("%,7d", moziTermek[i].getFoglalt()));
	      bevetel.irXY(4+i, 85, String.format("%,7d", moziTermek[i].getSzabad()));
	      bevetel.irXY(4+i, 101, String.format("%,8.0f", moziTermek[i].getBevetel())+" Ft"); 
	      osszBevetel+=moziTermek[i].getBevetel();
	   }
	   //Összesítés elkészítése
	   bevetel.sorRajzol(4+moziTermek.length, 2, 110, '-');
	   bevetel.irXY(4+moziTermek.length+1, 38, "N A P I     B E V É T E L     M I N D Ö S S Z E S E N : ");
	   bevetel.irXY(4+moziTermek.length+1,99,String.format("%,10.0f", osszBevetel)+" Ft");
	   bevetel.kiir();
	   extra.Console.pressEnter();
	}
	
		
	static void napimentes() {
	   //Létrehozunk egy Napimentes+dátum+időpont fájlt, amiben eltároljuk minden napra a TeremID, FilmID
	   //Teremnév, Filmnév, Eladott db, Üres hely, és Jegyár adatokat.
	   //Kérdés, hogy naponta egy-egy fájl készüljön, vagy legyen egy dátum mező inkább?->Collections?
	   //How filter arraylist in Java?
	   Calendar c = Calendar.getInstance();
	   DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.MEDIUM, Locale.getDefault());
	   String ev, honapSzoveg;
	   int honap=0,nap=0;
	   String fajlNev="";
	   String egySor;
	   String h="",n="";
	   char mehet;
	   hibaUzenet("FIGYELEM! A napi mentést csak akkor végezze el, ha már nem szeretne semmilyen műveletet végezni!", false);
	   do { 
         mehet=extra.Console.readChar("Biztos elvégzi a napi mentést? <I>gen <N>em ");
         mehet=Character.toUpperCase(mehet);
      } while (mehet!='N' && mehet!='I');
	   if (mehet=='I') {
   	   df.format(c.getTime());
   	   ev=c.get(Calendar.YEAR)+"";
   	   honap=c.get(Calendar.MONTH);
   	   nap=c.get(Calendar.DAY_OF_MONTH);
   	   honapSzoveg=c.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.getDefault());
   	   honap++;
   	   if (honap<=9) 
   	   	h="0"+String.valueOf(honap);
   	   else
   	   	h=String.valueOf(honap);
   	   if (nap<=9) 
   	   	n="0"+String.valueOf(nap);
   	   else
   	   	n=String.valueOf(nap);
   	   fajlNev=fajlNev.concat("NAPI_MENTÉS_"+ev+"_"+honapSzoveg.toUpperCase()+".csv");
   	   try {
   	   	RandomAccessFile fajl = new RandomAccessFile(fajlNev,"rw");
   	   	fajl.seek(fajl.length());
   	   	for (int i=0;i<moziTermek.length;i++) {
   	   		egySor=ev+h+n+";"+moziTermek[i].getTeremID()+";"+moziTermek[i].getTeremNev()+";"+moziTermek[i].getFilmID()+";"+moziTermek[i].getFilmCim()+
   	   				";"+moziTermek[i].getFoglalt()+";"+moziTermek[i].getSzabad()+";"+moziTermek[i].getJegyAr()+"\n";
   	   		fajl.writeBytes(egySor);
   	   		System.out.println(egySor);
   	   		egySor="";
   	   	}
   	   	fajl.close();
   	   	tajUzenet("A napimentés sikeresen elkészült!",true);
   	   	napiMentesVolt=true;
   	   }
   	   catch (IOException e ) {
   			hibaUzenet("Hiba történt fájlművelet közben!",true);
   		}
	   } //Biztos volt benne, hogy elvégzi a mentés
	   else {
	   	tajUzenet("A napi mentés nem történe meg.",true);
	   }
   	   	
	} // napimentes

	static void terem_kihasznaltsag() {
	   String egySor="";
	   String fajlNev="NAPI_MENTÉS_2018_JÚNIUS.csv"; //Tesztelés miatti
	   ArrayList<String> napiMentesSorok = new ArrayList<String>(); //A fájl sorait ebbe olvassuk be
      TreeMap<Integer, String[]> termek = new TreeMap<Integer, String[]>(); //Az integer a teremID, a String tömbbe pedig a terem nevét
      //a foglalt és a szabad helyeket tároljuk el
      String[] seged = new String[8]; // egy napi mentés sor elemit ide tördeljük szét
      int f,sz,t,j,ja; // segédváltozók
      String teremNev; //segeédváltozó kiíráshoz
      String evHo=""; // Melyik év melyik hónapról van szó, kiíráshoz kell
      int of=0,osz=0; //Osszes eladott, osszes ures hely
      int oh=0; //osszes hely
      int ob=0; // terem bevétele (foglalt*jegyár)
      int mb=0; //a termek bevételeinek összege
      
	   
	   try {
	      //fajlNev=extra.Console.readLine("Kérem a fájl nevét, amelyből a teremkihasználtságot előállítsam (napi mentés fájl): ");
	      RandomAccessFile fajl = new RandomAccessFile(fajlNev,"r");
	      egySor=fajl.readLine();
	      while (egySor!=null) { // Betöltjük a napimentés fájl összes sorát
            napiMentesSorok.add(egySor);
            egySor=fajl.readLine();
         } // while
         fajl.close();
	   } //try
      catch (IOException e ) {
         hibaUzenet("A megadott fájlt nem sikerült megnyitni!", true);
      }   
      seged=napiMentesSorok.get(0).split(";");
      evHo=seged[0].substring(0,4)+". "+honap(Integer.valueOf(seged[0].substring(5,6)));
      
      for (int i=0;i<napiMentesSorok.size();i++) { //A fájl sorokból kigyűjtjük a termeket és kezdőértékeket állítunk be
         seged=napiMentesSorok.get(i).split(";"); //Aktuális sor darabolása
         if (!termek.containsKey(Integer.valueOf(seged[1]))) { // Ha még nem volt ilyen terem ID
               termek.put(Integer.valueOf(seged[1]), new String[4]); // Új ID-jú terem, kulcs a teremID, 3 elemű tömb kell
               termek.get(Integer.valueOf(seged[1]))[0]=seged[2]; //A tömb első eleme a teremnév
               termek.get(Integer.valueOf(seged[1]))[1]="0"; //A tömb második eleme a foglalt helyek száma lesz
               termek.get(Integer.valueOf(seged[1]))[2]="0"; // A tömb harmadik eleme a szabad helyek száma lesz
               termek.get(Integer.valueOf(seged[1]))[3]="0"; // A tömb negyedik eleme terem bevétele lesz összesítve
         }
      }
      /* Kb. ilyen eredmény áll elő
        	1     [JÁVOR PÁL TEREM, 0, 5]
	      2     [JOHN WILLIAMS TEREM, 0, 0]
	      3     [KARÁDY KATALIN TEREM, 0, 0]
	      5     [KAMARATEREM, 0, 0]
	      8     [CHAPLIN TEREM, 0, 0]
        for (Map.Entry <Integer, String[]> elem: termek.entrySet()) 
       		System.out.println(elem.getKey()+"     "+Arrays.asList(elem.getValue()));
       */
      int bsor = 8+termek.size(); // Ennyi sora lesz a képernyőnek
      Kijelzo teremF = new Kijelzo(bsor, 100); //Létrehozzunk a képernyőt
  
      //Végigmegyünk ismét a sorokon és kigyűjtjük melyik teremben mennyi foglalt és szabad hely volt
      for (int i=0;i<napiMentesSorok.size();i++) {
         seged=napiMentesSorok.get(i).split(";");
         ja=Integer.valueOf(seged[7]); //jegyár ja-ba
         f=Integer.valueOf(seged[5]); // foglalt helyek int-ként
      	ob=f*ja; // terembevétel=foglalt hely * jegyár
      	sz=Integer.valueOf(seged[6]); // szabad helyek int-ként
      	t=Integer.valueOf(seged[1]); // a terem ID-ját is változóba tesszük, hgy áttekinthetőbb legyen a program
      	of+=f;
      	oh+=f;
      	f=f+Integer.parseInt(termek.get(t)[1]); // Foglalt helyek számát növeljük az eddig gyűjtöttel
      	osz+=sz;
      	oh+=sz;
      	sz=sz+Integer.parseInt(termek.get(t)[2]); // Szabad helyek számát növeljük az eddig gyűjtöttel
      	termek.get(t)[1]=String.valueOf(f); // visszaírjuk a termek megfelelő indexű és tömb elemű helyére a foglaltat sztringként
      	termek.get(t)[2]=String.valueOf(sz);// visszaírjuk a termek megfelelő indexű és tömb elemű helyére a szabadot sztringként
      	ob=ob+Integer.parseInt(termek.get(t)[3]); //az eddig tárolt bevételhez hosszáadjuk az aktuálisan számoltat
      	termek.get(t)[3]=String.valueOf(ob); // visszatesszuk stringként a bevétel értéket
      	
      } //for
      /* A fenti ciklus után ilyesféle eredmény születik:
    	1     [JÁVOR PÁL TEREM, 10, 1005]
      2     [JOHN WILLIAMS TEREM, 20, 1590]
      3     [KARÁDY KATALIN TEREM, 28, 672]
      5     [KAMARATEREM, 32, 346]
      8     [CHAPLIN TEREM, 12, 548]  
      */
      // for (Map.Entry <Integer, String[]> elem: termek.entrySet()) 
      //  System.out.println(elem.getKey()+"     "+Arrays.asList(elem.getValue()));
      // Előállítjuk a statisztika megjelenítésére szolgáló képernyőt
      teremF.keret(bsor, 97, 'D', false, " T E R E M    K I H A S Z N Á L T S Á G   "+evHo.toUpperCase()+" ");
      teremF.irXY(2, 3, "Terem név");
      teremF.oszlopRajzol(36, 2, Kijelzo.FS, termek.size()+4);
      teremF.irXY(2, 38, "Eladott");      
      teremF.oszlopRajzol(46, 2, Kijelzo.FS, termek.size()+4);
      teremF.irXY(2, 51, "Üres");
      teremF.oszlopRajzol(56, 2, Kijelzo.FS, termek.size()+4);
      teremF.irXY(2, 59, "Összes");
      teremF.oszlopRajzol(66, 2, Kijelzo.FS, termek.size()+4);
      teremF.irXY(2, 68, "Nézettség");
      teremF.oszlopRajzol(78, 2, Kijelzo.FS, termek.size()+4);
      teremF.irXY(2, 87, "Bevétel");
      teremF.sorRajzol(3, 3, 91, '-');
      j=0;
      //Végiglépkedünk a termek elemein és az adatok kitesszük a képernyőre
      for (Map.Entry <Integer, String[]> elem: termek.entrySet()) {
      	teremNev=elem.getValue()[0];
      	f=Integer.parseInt(elem.getValue()[1]); // Foglalt (eladott) helyek száma, integerré alakítva számoláshoz
      	sz=Integer.parseInt(elem.getValue()[2]); // Szabad helyek száma, int-é alakítva számoláshoz
      	ob=Integer.parseInt(elem.getValue()[3]);
      	mb+=ob;
      	teremF.irXY(4+j, 3, String.format("%-30s", levag(teremNev,30)));
      	teremF.irXY(4+j, 39, String.format("%,6d", f));
      	teremF.irXY(4+j, 49, String.format("%,6d",sz));
      	teremF.irXY(4+j, 59, String.format("%,6d", f+sz)); // Összes hely száma
      	teremF.irXY(4+j, 69, String.format("%,6.2f %%", ((f+0.0)/(f+sz))*100)); //Kihasználtság számítása/kiírása
      	teremF.irXY(4+j, 81, String.format("%,10d Ft", ob));
      	j++;
      } // for
      //Összesítő sor kiírása
      teremF.sorRajzol(4+j, 3, 91, '-');
      teremF.irXY(4+j+1, 3, "A mozi kihasználts./össz. bevétel:");
      teremF.irXY(4+j+1, 39, String.format("%,6d",of));
      teremF.irXY(4+j+1, 49, String.format("%,6d", osz));
      teremF.irXY(4+j+1, 59, String.format("%,6d", oh));
      teremF.irXY(4+j+1, 69, String.format("%, 6.2f %%",((of+0.0)/(of+osz))*100));
      teremF.irXY(4+j+1, 81, String.format("%,10d Ft", mb));
      teremF.kiir(); // A képernyő megjelenítése konzolon
      extra.Console.pressEnter(); 
	} // terem_kihasznaltsag metódus
	
	static void film_nezettseg() {
	   String egySor="";
      String fajlNev="NAPI_MENTÉS_2018_JÚNIUS.csv"; //Tesztelés miatti
      ArrayList<String> napiMentesSorok = new ArrayList<String>(); //A fájl sorait ebbe olvassuk be
      TreeMap<Integer, String[]> filmek = new TreeMap<Integer, String[]>(); //Az integer a filmID, a String tömbbe pedig a film cimét
      //a foglalt és a szabad helyeket tároljuk el
      String[] seged = new String[8]; // egy napi mentés sor elemit ide tördeljük szét
      int f,sz,t,j,ja; // segédváltozók
      String filmNev; //segeédváltozó kiíráshoz
      String evHo=""; // Melyik év melyik hónapról van szó, kiíráshoz kell
      int of=0,osz=0; //Osszes eladott, osszes ures hely
      int oh=0; //osszes hely
      int fb=0; // film bevétele (foglalt*jegyár)
      int fbo=0; //a filmek bevételeinek összege
      
      
      try {
         //fajlNev=extra.Console.readLine("Kérem a fájl nevét, amelyből a teremkihasználtságot előállítsam (napi mentés fájl): ");
         RandomAccessFile fajl = new RandomAccessFile(fajlNev,"r");
         egySor=fajl.readLine();
         while (egySor!=null) { // Betöltjük a napimentés fájl összes sorát
            napiMentesSorok.add(egySor);
            egySor=fajl.readLine();
         } // while
         fajl.close();
      } //try
      catch (IOException e ) {
         hibaUzenet("A megadott fájlt nem sikerült megnyitni!", true);
      }   
      seged=napiMentesSorok.get(0).split(";");
      evHo=seged[0].substring(0,4)+". "+honap(Integer.valueOf(seged[0].substring(5,6)));
      
      for (int i=0;i<napiMentesSorok.size();i++) { //A fájl sorokból kigyűjtjük a filmeket és kezdőértékeket állítunk be
         seged=napiMentesSorok.get(i).split(";"); //Aktuális sor darabolása
         if (!filmek.containsKey(Integer.valueOf(seged[3]))) { // Ha még nem volt ilyen film ID
               filmek.put(Integer.valueOf(seged[3]), new String[4]); // Új ID-jú film, kulcs a filmID, 3 elemű tömb kell
               filmek.get(Integer.valueOf(seged[3]))[0]=seged[4]; //A tömb első eleme a filcím
               filmek.get(Integer.valueOf(seged[3]))[1]="0"; //A tömb második eleme a foglalt helyek száma lesz
               filmek.get(Integer.valueOf(seged[3]))[2]="0"; // A tömb harmadik eleme a szabad helyek száma lesz
               filmek.get(Integer.valueOf(seged[3]))[3]="0"; // A tömb negyedik eleme terem bevétele lesz összesítve
         }
      }
    
      int bsor = 8+filmek.size(); // Ennyi sora lesz a képernyőnek
      Kijelzo teremF = new Kijelzo(bsor, 100); //Létrehozzunk a képernyőt
  
      //Végigmegyünk ismét a sorokon és kigyűjtjük melyik teremben mennyi foglalt és szabad hely volt
      for (int i=0;i<napiMentesSorok.size();i++) {
         seged=napiMentesSorok.get(i).split(";");
         ja=Integer.valueOf(seged[7]); //jegyár ja-ba
         f=Integer.valueOf(seged[5]); // foglalt helyek int-ként
         fb=f*ja; // filmbevétel=foglalt hely * jegyár
         sz=Integer.valueOf(seged[6]); // szabad helyek int-ként
         t=Integer.valueOf(seged[3]); // a film ID-ját is változóba tesszük, hgy áttekinthetőbb legyen a program
         of+=f;
         oh+=f;
         f=f+Integer.parseInt(filmek.get(t)[1]); // Foglalt helyek számát növeljük az eddig gyűjtöttel
         osz+=sz;
         oh+=sz;
         sz=sz+Integer.parseInt(filmek.get(t)[2]); // Szabad helyek számát növeljük az eddig gyűjtöttel
         filmek.get(t)[1]=String.valueOf(f); // visszaírjuk a termek megfelelő indexű és tömb elemű helyére a foglaltat sztringként
         filmek.get(t)[2]=String.valueOf(sz);// visszaírjuk a termek megfelelő indexű és tömb elemű helyére a szabadot sztringként
         fb=fb+Integer.parseInt(filmek.get(t)[3]); //az eddig tárolt bevételhez hosszáadjuk az aktuálisan számoltat
         filmek.get(t)[3]=String.valueOf(fb); // visszatesszuk stringként a bevétel értéket
       } //for
      
      // Előállítjuk a statisztika megjelenítésére szolgáló képernyőt
      teremF.keret(bsor, 97, 'D', false, " F I L M    N É Z E T T S É G   "+evHo.toUpperCase()+" ");
      teremF.irXY(2, 3, "Film cím");
      teremF.oszlopRajzol(36, 2, Kijelzo.FS, filmek.size()+4);
      teremF.irXY(2, 38, "Eladott");      
      teremF.oszlopRajzol(46, 2, Kijelzo.FS, filmek.size()+4);
      teremF.irXY(2, 51, "Üres");
      teremF.oszlopRajzol(56, 2, Kijelzo.FS, filmek.size()+4);
      teremF.irXY(2, 59, "Összes");
      teremF.oszlopRajzol(66, 2, Kijelzo.FS, filmek.size()+4);
      teremF.irXY(2, 68, "Nézettség");
      teremF.oszlopRajzol(78, 2, Kijelzo.FS, filmek.size()+4);
      teremF.irXY(2, 87, "Bevétel");
      teremF.sorRajzol(3, 3, 91, '-');
      j=0;
      //Végiglépkedünk a termek elemein és az adatok kitesszük a képernyőre
      for (Map.Entry <Integer, String[]> elem: filmek.entrySet()) {
         filmNev=elem.getValue()[0];
         f=Integer.parseInt(elem.getValue()[1]); // Foglalt (eladott) helyek száma, integerré alakítva számoláshoz
         sz=Integer.parseInt(elem.getValue()[2]); // Szabad helyek száma, int-é alakítva számoláshoz
         fb=Integer.parseInt(elem.getValue()[3]);
         fbo+=fb;
         teremF.irXY(4+j, 3, String.format("%-30s", levag(filmNev,32)));
         teremF.irXY(4+j, 39, String.format("%,6d", f));
         teremF.irXY(4+j, 49, String.format("%,6d",sz));
         teremF.irXY(4+j, 59, String.format("%,6d", f+sz)); // Összes hely száma
         teremF.irXY(4+j, 69, String.format("%,6.2f %%", ((f+0.0)/(f+sz))*100)); //Kihasználtság számítása/kiírása
         teremF.irXY(4+j, 81, String.format("%,10d Ft", fb));
         j++;
      } // for
      //Összesítő sor kiírása
      teremF.sorRajzol(4+j, 3, 91, '-');
      teremF.irXY(4+j+1, 3, "Film nézettség/össz. bevétel:");
      teremF.irXY(4+j+1, 39, String.format("%,6d",of));
      teremF.irXY(4+j+1, 49, String.format("%,6d", osz));
      teremF.irXY(4+j+1, 59, String.format("%,6d", oh));
      teremF.irXY(4+j+1, 69, String.format("%, 6.2f %%",((of+0.0)/(of+osz))*100));
      teremF.irXY(4+j+1, 81, String.format("%,10d Ft", fbo));
      teremF.kiir(); // A képernyő megjelenítése konzolon
      extra.Console.pressEnter(); 
	}
	
	static void termekben_A_Filmek() {
	   String egySor="";
      String fajlNev="NAPI_MENTÉS_2018_JÚNIUS.csv"; //Tesztelés miatti
      ArrayList<String> napiMentesSorok = new ArrayList<String>(); //A fájl sorait ebbe olvassuk be
      TreeMap<Integer, ArrayList<String>> termek = new TreeMap<Integer, ArrayList<String>>(); //Az integer a terem ID, list első eleme a teremnév, utána a film ID-k
      TreeMap<Integer, String> filmek = new TreeMap<Integer, String>(); //Az integer a filmID, a String a filmcím
      String[] seged = new String[8]; // egy napi mentés sor elemit ide tördeljük szét
      String evHo=""; // Melyik év melyik hónapról van szó, kiíráshoz kell
      int s;       //segédváltozó 
      
      try {
         //fajlNev=extra.Console.readLine("Kérem a fájl nevét, amelyből a lekérdezést előállítsam (napi mentés fájl): ");
         RandomAccessFile fajl = new RandomAccessFile(fajlNev,"r");
         egySor=fajl.readLine();
         while (egySor!=null) { // Betöltjük a napimentés fájl összes sorát
            napiMentesSorok.add(egySor);
            egySor=fajl.readLine();
         } // while
         fajl.close();
      } //try
      catch (IOException e ) {
         hibaUzenet("A megadott fájlt nem sikerült megnyitni!", true);
      }   
      if (napiMentesSorok.size()>0) { //Ha a fájl  nem volt üres
         //Előállítjuk a fejlécbe az év és hónapot
         seged=napiMentesSorok.get(0).split(";");
         evHo=seged[0].substring(0,4)+". "+honap(Integer.valueOf(seged[0].substring(5,6)));
         
         for (int i=0;i<napiMentesSorok.size();i++) { //A fájl sorokból kigyűjtjük a termeket 
            seged=napiMentesSorok.get(i).split(";"); //Aktuális sor darabolása
            if (!termek.containsKey(Integer.valueOf(seged[3]))) { // Ha még nem volt ilyen terem ID
                  termek.put(Integer.valueOf(seged[1]), new ArrayList<String>()); // Új ID-jú terem, kulcs a terem ID, és létrehozzuk a listát amiben a játszott filmek ID-ja lesz
                  termek.get(Integer.valueOf(seged[1])).add(seged[2]); //A lista első eleme a teremnév lesz, amit beleteszünk
            }
            if (!filmek.containsKey(Integer.valueOf(seged[3]))) //Ha az aktuális sorban lévő filmcímet még nem tettük el, akkor azt is eltesszük a filmek közé
               filmek.put(Integer.valueOf(seged[3]), seged[4]);
         } // terem és film begyűjtő ciklus
         
         //Ismét végig kell menni az adatokon és kikeresni melyik teremben melyik filmet játszották
         for (int i=0;i<napiMentesSorok.size();i++) {
            seged=napiMentesSorok.get(i).split(";");
            if (!termek.get(Integer.valueOf(seged[1])).contains(seged[3])) //Ha termekben lévő lista még nem tartalmmazza az aktuális sorban lévő film ID-t...
               termek.get(Integer.valueOf(seged[1])).add(seged[3]); //.. akkor ezt a film ID-t el kell tennünk a listába 
          } //
       
          int bsor = 8+termek.size()+filmek.size(); // Ennyi sora lesz a képernyőnek
          Kijelzo teremF = new Kijelzo(bsor, 100); //Létrehozzunk a képernyőt
          s=2; //A változó az aktuális kiírás sorát mutatja
          teremF.keret(bsor, 100, 'D', false, " J Á T S Z O T T    F I L M E K    T E R M E K     S Z E R I N T    "+evHo.toUpperCase()+" ");
          for (Map.Entry <Integer, ArrayList<String>> terem: termek.entrySet()) { // végigmegyünk a termeken 
             teremF.irXY(s, 3, terem.getValue().get(0)); // a terem nevét kiírjuk
             s++; //Új sor
             for (int i=0;i<terem.getValue().size()-1;i++) { //a terembem lévő arraylisten végig kell mennünk a film ID miatt
                //megkeressük a filmek között a terem arraylistjének i+1. eleme által tárolt film ID szerinti filmet
                //azért i+1, mert a 0-ban a terem neve volt tárolva.
                teremF.irXY(s, 10, "- "+levag(filmek.get(Integer.valueOf(terem.getValue().get(i+1))),60));
                s++; // Új sor
             } // adott teremhez megvannak a filmek a ciklus végére
             s++; // új sor
          } // termeken végigmenő ciklus
          teremF.kiir(); // A kijelző megjelenítése a konzolon
          extra.Console.pressEnter();
      } //Ha volt adat a fájlban
      else
      {
         hibaUzenet("A megadott fájl nem tartalmaz adatokat!",true);
      }
	}// termekben_A_Filmek metódus
	
	//A metódus a paraméterként kapott sztring 0. karakterétől számított db hosszúságú sztringet
	//ad vissza. Ha a sztring rövidebb, mint a megadott db, akkor az egész stzringet visszaadja.
	static String levag(String szoveg, int db) {
	   if (szoveg.length()<=db)
	      return szoveg;
	   else
	      return szoveg.substring(0,db-1);
	} //levag metódus
		
	//A metódus a paraméterként kapott sztringet kiírja, előtte egy HIBA_UZ_MINTA karakterből álló sor van, azelőtt
	//egy üres sor. Az üzenet mögött HIBA_UZ_MINTA álló sor van, utána egy üres sor
	//Ha enter igaz Enter leütésre vár
	static void hibaUzenet(String uzenet, boolean enter) {
		uzenet(uzenet,enter,HIBA_UZ_MINTA);
	}
	
	//A metódus a paraméterként kapott sztringet kiírja, előtte egy TAJ_UZ_MINTA karakterből álló sor van, azelőtt
   //egy üres sor. Az üzenet mögött TAJ_UZ_MINTA álló sor van, utána egy üres sor
   //Ha enter igaz Enter leütésre vár
	static void tajUzenet(String uzenet, boolean enter) {
	   uzenet(uzenet,enter,TAJ_UZ_MINTA);
	}
	
	static void uzenet(String uzenet, boolean enter, char minta) {
	   System.out.println();
      for (int i=1;i<=uzenet.length();i++)
         System.out.print(minta);
      System.out.println();
      System.out.println(uzenet);
      for (int i=1;i<=uzenet.length();i++)
         System.out.print(minta);
      System.out.println();
      System.out.println();
      if (enter)
         extra.Console.pressEnter();
	}
	//A paraméterként kapott szám alapján visszadja a hónap nevét
	static String honap(int h) {
		switch (h) {
			case 1: return "Január";
			case 2: return "Február";
			case 3: return "Március";
			case 4: return "Április";
			case 5: return "Május";
			case 6: return "Június";
			case 7: return "Július";
			case 8: return "Augusztus";
			case 9: return "Szeptember";
			case 10: return "Október";
			case 11: return "November";
			case 12: return "December";
			default: return "Hibás hónapsorszám!";
		}
	}
} // class Mozi
