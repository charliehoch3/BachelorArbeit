package de.tuberlin.ise.privacy.sar;

import com.opencsv.CSVWriter;
import net.suuft.libretranslate.Language;
import net.suuft.libretranslate.Translator;
import org.apache.commons.lang3.tuple.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class CSVConverter {


    public static void csvBuilder(Set<Pair<String, String>> tupelliste, String filename) {
        //Translator für LibreTranslate SetUp -> greift zu auf Localhost, nachdem LibreTranslate da installiert wurde
        Translator.setUrlApi("http://127.0.0.1:5000/translate");
        filename = filename.split("\\.")[0];


        //csv Datei wird erstellt + tupelarray wird erstellt
        CSVWriter csvWriter = null;
        try (FileWriter writer = new FileWriter(filename +"_datenauskunft.csv")) {
            csvWriter = new CSVWriter(writer);

            ArrayList<Pair<String, String>> csvTupel = new ArrayList<Pair<String, String>>();


            //tupelliste durchgehen und die Eltern auf ihr letztes Element reduzieren
            for (Pair<String, String> tupel : tupelliste) {
                String parentlist = tupel.getKey();
                String child = tupel.getValue();

                //parentString aufteilen in die einzelnen Eltern -> lonelyParents
                String[] lonelyParents = parentlist.split("_");

                if(lonelyParents.length == 2){
                    csvTupel.add(Pair.of(filename, child));
                } else {
                    csvTupel.add(Pair.of(lonelyParents[lonelyParents.length - 1], child));
                }

                //add translated Tuple
                //String translatedparent = Translator.translate(Language.ENGLISH, Language.GERMAN, lonelyParents[lonelyParents.length - 1]);
                //String translatedchild = Translator.translate(Language.ENGLISH, Language.GERMAN, child);

                //csvTupel.add(Pair.of(translatedparent, translatedchild));

            }

            //csvDatei befüllen
            csvWriter.writeNext(new String[]{"table", "column"});
            for (Pair<String, String> tupel : csvTupel) {
                csvWriter.writeNext(new String[]{tupel.getKey(), tupel.getValue()});
            }
            System.out.println("Fertig mit der csv!");
            csvWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

