/*
 * javalib k�nyvt�r
 * Csomag: extra
 * Console.java
 *
 * Angster Erzs�bet: OO tervez�s �s programoz�s, Java 1. k�tet
 * 2002.09.01.
 *
 * Beolvas�s a konzolr�l:
 *
 * String readLine()
 * String readLine(String str)
 * char readChar()
 * char readChar(String str)
 * int readInt()
 * int readInt(String str)
 * int readLong()
 * int readLong(String str)
 * double readDouble()
 * double readDouble(String str)
 * void pressEnter()
 */

package extra;
import java.io.*;

public class Console {
  // Az oszt�lyb�l nem lehet p�ld�nyt l�trehozni:
  private Console() {
  }

  /* Pufferez� karakterfolyam, melynek forr�shelye a konzol.
   * A readLine met�dus haszn�lja.
   */
  private static BufferedReader be =
     new BufferedReader(new InputStreamReader(System.in));

  // ---------------------------------------------------------
  // String beolvas�sa sor v�g�ig:
  public static String readLine() {
    String beString = "";
    try {
      beString = be.readLine();
    }
    catch (IOException e) {
    }
    return beString;
  }

  // ---------------------------------------------------------
  // String beolvas�sa sor v�g�ig, el�tte prompt:
  public static String readLine(String str) {
    System.out.print(str);
    return readLine();
  }

  // ---------------------------------------------------------
  // Karakter beolvas�sa:
  public static char readChar() {
    while(true) {
      try {
        return readLine().charAt(0);
      }
      catch(IndexOutOfBoundsException e) {
        System.out.println("Nem karakter! Ujra!");
      }
    }
  }

  // ---------------------------------------------------------
  // Karakter beolvas�sa, el�tte prompt:
  public static char readChar(String str) {
    System.out.print(str);
    return readChar();
  }

  // ---------------------------------------------------------
  // Eg�sz (int) beolvas�sa:
  public static int readInt() {
    while(true) {
      try {
        return Integer.parseInt(readLine().trim());
      }
      catch(NumberFormatException e) {
        System.out.println("Nem egesz! Ujra!");
      }
    }
  }

  // ---------------------------------------------------------
  // Eg�sz (int) beolvas�sa, el�tte prompt:
  public static int readInt(String str) {
    while(true) {
      System.out.print(str);
      try {
        return Integer.parseInt(readLine().trim());
      }
      catch(NumberFormatException e) {
        System.out.println("Nem egesz! Ujra!");
      }
    }
  }

  // ---------------------------------------------------------
  // Eg�sz (long) beolvas�sa:
  public static long readLong() {
    while(true) {
      try {
        return Long.parseLong(readLine().trim());
      }
      catch(NumberFormatException e) {
        System.out.println("Nem egesz! Ujra!");
      }
    }
  }

  // ---------------------------------------------------------
  // Eg�sz (long) beolvas�sa, el�tte prompt:
  public static long readLong(String str) {
    while(true) {
      System.out.print(str);
      try {
        return Long.parseLong(readLine().trim());
      }
      catch(NumberFormatException e) {
        System.out.println("Nem egesz! Ujra!");
      }
    }
  }

  // ---------------------------------------------------------
  // Val�s (double) beolvas�sa:
  public static double readDouble() {
    while(true) {
      try {
        return Double.parseDouble(readLine().trim());
      }
      catch(NumberFormatException e) {
        System.out.println("Nem valos! Ujra!");
      }
    }
  }

  // ---------------------------------------------------------
  // Val�s (double) beolvas�sa, el�tte prompt:
  public static double readDouble(String str) {
    while(true) {
      System.out.print(str);
      try {
        return Double.parseDouble(readLine().trim());
      }
      catch(NumberFormatException e) {
        System.out.println("Nem valos! Ujra!");
      }
    }
  }

  // ---------------------------------------------------------
  // V�r�s az ENTER lenyom�s�ra:
  public static void pressEnter() {
    System.out.print("<ENTER>");
    readLine();
  }
}
