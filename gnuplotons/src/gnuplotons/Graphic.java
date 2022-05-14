/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gnuplotons;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucasrc
 */
public class Graphic {
    
    private boolean flag = false;

    public Graphic() {
        File file = new File(GnuplotONS.dir + "/gnuplot.gnuplot");
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            int index = 2;
            for (int i = 1; i < GnuplotONS.header.size(); i++) {
                createGraphicPage(bw, GnuplotONS.header.get(i), index, index + 1);
                index += 2;
            }
            for (String raClass : GnuplotONS.raClasses) {
                createModulationPage(bw, raClass);
            }
            
            bw.close();
            fw.close();

        } catch (IOException ex) {
            Logger.getLogger(Graphic.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

        }
    }

    void execute() {
        Runtime r = Runtime.getRuntime();
        Process process;
        BufferedReader br;
        try {
            process = r.exec(new String[]{"sh", "-c", "cd " + GnuplotONS.dir + " && gnuplot gnuplot.gnuplot"});
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String saida = null;
            while ((saida = br.readLine()) != null) {
                System.out.println(saida); //saida do comando
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(Graphic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createModulationPage(BufferedWriter bw, String raClass) {
        ArrayList<String> modulationMetrics = new ArrayList<>();
        for (String metric : GnuplotONS.header) {
            if(modulationMetric(metric)) {
                modulationMetrics.add(metric);
            }
        }
        double size = -0.5 + (2.5*GnuplotONS.loads.size());
        try {
            bw.write("reset\n"
                    + "#set title \"Modulation used ratio (%) vs. Traffic Load\"\n"
                    + "set xlabel \"Traffic Load (Erlang)\"\n"
                    + "set ylabel \"Modulation used ratio (%) for " + raClass.replaceAll("_", "-") + "\"\n"
                    + "#set xrange [-0.5:"+ size +"]\n"
                    + "set yrange [0:100]\n"
                    + "set boxwidth 0.75 absolute \n"
                    + "set style fill solid 1.0 border -1 \n"
                    + "set style histogram errorbars gap 1 lw 1\n"
                    + "set style data histogram \n"
                    + "set key box width 1 #center top horizontal\n"
                    + "set ytics 10\n"
                    + "plot \\");
            bw.newLine();
            
            boolean flag = true;
            for (String modulationMetric : modulationMetrics) {
                printModulation(bw, raClass, modulationMetric, flag);
                flag = false;
            }
            
            bw.write("\n####################################################################################");
            bw.newLine();
            bw.newLine();
            
        } catch (IOException ex) {
            Logger.getLogger(Graphic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void createGraphicPage(BufferedWriter bw, String metric, int index1, int index2) {
        if(modulationMetric(metric)) {
            return;
        }
        int pt = 1, lt = 1;
        try {
            bw.write("reset\n"
                    + "#set title \"" + getTitleMetric(metric) + " vs. Traffic Load\"\n"
                    + "set xlabel \"Traffic Load (Erlang)\"\n"
                    + "set ylabel \"" + getTitleMetric(metric) + "\"\n"
                    + "set size 1,1\n"
                    + "set xrange [" + getLoads() + "]\n"
                    + "#set yrange [0:25]\n"
                    + "set autoscale y\n"
                    + "set style data linespoints\n"
                    + "set grid\n"
                    + "#set ytics 5\n"
                    + "#set xtics 50,50,710\n"
                    + "#set mytics 2\n"
                    + "#set mytics 1\n"
                    + "#set logscale y 2\n"
                    + "set key reverse Left box width 1 left #outside #horizontal\n"
                    + "plot \\");
            bw.newLine();
            
            for (String raClass : GnuplotONS.raClasses) {
                printRA(bw, raClass, metric, index1, index2, pt, lt);
                pt++;
                lt++;
                if(pt == 17) {
                    pt = 1;
                }
                if(lt == 9) {
                    lt = 1;
                }
            }
            if(!flag) {
                bw.write("\nset terminal pdf #size 1000,1000\n"
                        + "set output 'graphs.pdf'\n"
                        + "replot\n"
                        + "\n####################################################################################");
                bw.newLine();
                bw.newLine();
                flag = true;
            } else {
                bw.write("\n####################################################################################");
                bw.newLine();
                bw.newLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(Graphic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String getTitleMetric(String metric) {
        switch (metric) {
            case "BR":
                return "Blocking Ratio (BR)";
            case "BBR":
                return "Bandwidth Blocking Ratio (BBR)";
            case "LP":
                return "Number of LPs created";
            case "TR":
                return "Average of Transponders per request";
            case "VH":
                return "Average Virtual Hops per request";
            case "PH":
                return "Average of Physical Hops per request";
            case "Spec":
                return "Spectrum Available ratio";
            case "Frag":
                return "External Fragmentation ratio";
            case "PC":
                return "Average Power Consumption per LP (W)";
            case "TP":
                return "Power Consumption (GWh)";
            case "EE":
                return "Energy Efficiency (Kbits/Joule)";
            case "LPGrooming":
                return "LPs groomed (%)";
            case "JFI_BR":
                return "Jain's Fairness index (BR)";
            case "JFI_BBR":
                return "Jain's Fairness index (BBR)";
            case "BPSK":
                return "Modulation used ratio (%) BPSK";
            case "QPSK":
                return "Modulation used ratio (%) QPSK";
            case "8QAM":
                return "Modulation used ratio (%) 8QAM";
            case "16QAM":
                return "Modulation used ratio (%) 16QAM";
            case "32QAM":
                return "Modulation used ratio (%) 32QAM";
            case "64QAM":
                return "Modulation used ratio (%) 64QAM";
            case "128QAM":
                return "Modulation used ratio (%) 128QAM";
            case "256QAM":
                return "Modulation used ratio (%) 256QAM";
            default:
                return metric;
        }
    }

    private String getLoads() {
        String s = "";
        s += (GnuplotONS.loads.first() - 10);
        s += ":";
        s += (GnuplotONS.loads.last() + 10);
        return s;
    }
    
    private void printRA(BufferedWriter bw, String raClass, String metric, int index1, int index2, int pt, int lt) {
        try {
            switch (metric) {
                case "PC":
                    bw.write("'" + raClass + ".txt' using 1:($" + index1 + "/1000000) title '" + raClass.replaceAll("_", "-") + " ' pt " + pt + " lt " + lt + " ps 1.0 lw 1.5, \\\n");
                    bw.write("'" + raClass + ".txt' using 1:($" + index1 + "/1000000):($" + index2 + "/1000000) notitle with yerrorbars pt " + pt + " lt " + lt + " ps 0.0 lw 1.5, \\\n");
                    break;
                case "TC":
                    bw.write("'" + raClass + ".txt' using 1:($" + index1 + "/1000000000) title '" + raClass.replaceAll("_", "-") + " ' pt " + pt + " lt " + lt + " ps 1.0 lw 1.5, \\\n");
                    bw.write("'" + raClass + ".txt' using 1:($" + index1 + "/1000000000):($" + index2 + "/1000000000) notitle with yerrorbars pt " + pt + " lt " + lt + " ps 0.0 lw 1.5, \\\n");
                    break;
                case "EE":
                    bw.write("'" + raClass + ".txt' using 1:($" + index1 + "*1000) title '" + raClass.replaceAll("_", "-") + " ' pt " + pt + " lt " + lt + " ps 1.0 lw 1.5, \\\n");
                    bw.write("'" + raClass + ".txt' using 1:($" + index1 + "*1000):($" + index2 + "*1000) notitle with yerrorbars pt " + pt + " lt " + lt + " ps 0.0 lw 1.5, \\\n");
                    break;
                default:
                    bw.write("'" + raClass + ".txt' using 1:" + index1 + " title '" + raClass.replaceAll("_", "-") + " ' pt " + pt + " lt " + lt + " ps 1.0 lw 1.5, \\\n");
                    bw.write("'" + raClass + ".txt' using 1:" + index1 + ":" + index2 + " notitle with yerrorbars pt " + pt + " lt " + lt + " ps 0.0 lw 1.5, \\\n");
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(Graphic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean modulationMetric(String metric) {
        switch (metric) {
            case "BPSK":
                return true;
            case "QPSK":
                return true;
            case "8QAM":
                return true;
            case "16QAM":
                return true;
            case "32QAM":
                return true;
            case "64QAM":
                return true;
            case "128QAM":
                return true;
            case "256QAM":
                return true;
            default:
                return false;
        }
    }

    private void printModulation(BufferedWriter bw, String raClass, String modulationMetric, boolean flag) {
        int index = getMetricIndex(modulationMetric);
        try {
            if(flag) {
                bw.write("'" + raClass + ".txt' using " + index + ":"+ (index + 1) +":xtic(1) t '" + modulationMetric + " ', \\\n");        
            } else {
                bw.write("'' using " + index + ":"+ (index + 1)+" t '" + modulationMetric + " ', \\\n");        
            }
        } catch (IOException ex) {
            Logger.getLogger(Graphic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int getMetricIndex(String modulationMetric) {
        int index = 2;
        for (int i = 1; i < GnuplotONS.header.size(); i++) {
            if(GnuplotONS.header.get(i).equals(modulationMetric)) {
                return index;
            }
            index += 2;
        }
        return index;
    }
    /**
    * pt 
    * 1 linhaHorizontal 
    * 2 um X 
    * 3 um * 
    * 4 quadrado 
    * 5 quadrado preenchido 
    * 6 bolinha 
    * 7 bolinha preenchida 
    * 8 triangulo 
    * 9 triangulo preenchido 
    * 10 triangulo pra baixo 
    * 11 triangulo pra baixo preenchido
    * 12 losangulo 
    * 13 losangulo preenchido 
    * 14 pentagono 
    * 15 pentagono preenchido 
    * 16 cruz
    *
    * lt 
    * 1 roxo 
    * 2 verde 
    * 3 azul 
    * 4 laranja 
    * 5 amarelo 
    * 6 azul escuro 
    * 7 vermelho 
    * 8 preto
    */
}
