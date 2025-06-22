/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package dgreger.iki_kviz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author svenk
 */
public class Iki_kviz {

    public static void main(String[] args) {
        String inputPutanja = "Iki - pitanja.txt";
        String outputPutanja = "dump.sql";
        AtomicInteger pitanjeSifra = new AtomicInteger(1);
        AtomicInteger odgovorSifra = new AtomicInteger(1);
        
        try(
                BufferedReader br = new BufferedReader(new FileReader(inputPutanja));
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputPutanja))
                ) {
            String linija;
            int rednibroj = 0;
            String pitanjeTekst = "";
            String[] odgovori = new String[4];
            char tocanOdgovor = ' ';
            int odgovorCounter = 0;
            
            while ((linija = br.readLine()) != null) {                
                linija = linija.trim();
                if (linija.matches("^\\d+\\..*")) {
                    pitanjeTekst = linija.substring(linija.indexOf('.') + 1).trim();
                    odgovorCounter = 0;
                    rednibroj++;
                }
                else if(linija.matches("^[a-d]\\).*")){
                    odgovori[odgovorCounter] = linija.substring(2).trim();
                    odgovorCounter++;
                }
                else if(linija.startsWith("To?an odgovor:") || linija.startsWith("Točan odgovor:")){
                    tocanOdgovor = linija.charAt(linija.length() - 1);
                    
                    bw.write("insert into pitanja (sifra, tekst) values (" + pitanjeSifra.get() + ", '" + escape(pitanjeTekst) + "');\n");
                    
                    for (int i = 0; i < 4; i++) {
                        char oznaka = (char) ('a' + i);
                        boolean jeTocan = (oznaka == tocanOdgovor);
                        bw.write("insert into odgovori (sifra, tekst, je_tocan, pitanje_sifra) values ("
                            + odgovorSifra.getAndIncrement() + ", '"
                            + escape(odgovori[i]) + "', "
                            + (jeTocan ? 1 : 0) + ", "
                            + pitanjeSifra.get() + ");\n");
                    }
                    
                    pitanjeSifra.getAndIncrement();
                }
            }
            
            System.out.println("Dump file generiran:" + " " + outputPutanja);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String escape(String text) {
        return text.replace("'", "''");
    }
}
