/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gnuplotons;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucasrc
 */
public class GnuplotONS {

    protected static String dir = "./";
    protected static String[] raClasses;
    protected static ArrayList<String> header; 
    protected static TreeSet<Double> loads;
    
    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Usage: JsonReader <ra1> [ra2] [ra3] [ra4] ...");
            System.exit(0);
        }
        raClasses = new String[args.length];
        header = new ArrayList<>();
        loads = new TreeSet<>();
        System.arraycopy(args, 0, raClasses, 0, args.length);
        final File file = new File(args[0]+".txt");
        
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            boolean flag = false;
            while ((line = br.readLine()) != null) {
                if(!flag) {//the header
                    String[] div = line.split("\t");
                    for (String metric : div) {
                        if(metric.equals("#load")) {
                            metric = "Load";
                            header.add(metric);
                        } else {
                            if(!metric.contains("_ic"))
                                header.add(metric);
                        }
                    }
                    flag = true;
                } else {//the loads
                    String[] div = line.split("\t");
                    loads.add(Double.parseDouble(div[0]));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GnuplotONS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GnuplotONS.class.getName()).log(Level.SEVERE, null, ex);
        }
        Graphic g = new Graphic();
        g.execute();
    }
    
}
