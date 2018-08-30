package mes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import lokalny.LocalElement;


public class GlobalData {

    private double H, B; //wysokosc, szerokosc
    private int nH, nB; //liczba wezlow po wysokosci i po szerokosci

    private int nh; //liczba wezlow
    private int ne; //liczba elementow


    private double dB; // odlegosc miedzy wezlami
    private double dH; // odlegosc miedzy wezlami


    private double t_begin; //temperatura poczatkowa
    private double tau; //czas procesu
    private double dtau; //poczatkowa wartosc przyrostu czasu
    private double t_otoczenia; //temperatura otoczenia
    private double alfa; //wspolczynnik wymiany ciepla
    private double c; //cieplo wlasciwe
    private double k; //wspolczynnik przewodzenia ciepla
    private double ro; //gestosc

    private LocalElement localElement;
    private double[][] H_current;
    private double[] P_current;
    private double[][] H_global;
    private double[] P_global;

    private static GlobalData globalData;

    public GlobalData() throws FileNotFoundException
    {

        Scanner scanner = new Scanner(new File("data.txt"));
        scanner.hasNextDouble();

        this.H = scanner.nextDouble();             scanner.findInLine(";");
        this.B = scanner.nextDouble();             scanner.findInLine(";");
        this.nH = scanner.nextInt();               scanner.findInLine(";");
        this.nB = scanner.nextInt();               scanner.findInLine(";");
        this.t_begin = scanner.nextDouble();       scanner.findInLine(";");
        this.tau = scanner.nextDouble();           scanner.findInLine(";");
        this.dtau = scanner.nextDouble();          scanner.findInLine(";");
        this.t_otoczenia = scanner.nextDouble();   scanner.findInLine(";");
        this.alfa = scanner.nextDouble();          scanner.findInLine(";");
        this.c = scanner.nextDouble();             scanner.findInLine(";");
        this.k = scanner.nextDouble();             scanner.findInLine(";");
        this.ro = scanner.nextDouble();            scanner.close();

        dB = B / ( nB - 1 );  // odlegosc miedzy wezlami
        dH = H / ( nH - 1 );  // odlegosc miedzy wezlami



        ne = (nH - 1) * (nB - 1); // liczba elementów w siatce
        nh = nH * nB;            // liczba wezłów w siatce

        localElement = LocalElement.getInstance();
        H_current = new double[4][4];
        P_current = new double[4];
        H_global = new double[nh][nh];
        P_global = new double[nh];

    }

    public void compute() throws FileNotFoundException {

        for (int i = 0; i < nh; i++) {
            for (int j = 0; j < nh; j++) {
                H_global[i][j] = 0;
            }
            P_global[i] = 0;
        }

        Grid grid = Grid.getInstance();
        Jakobian jakobian;
        double[] dNdx = new double[4];
        double[] dNdy = new double[4];
        double[] x = new double[4];
        double[] y = new double[4];
        double[] temp_0 = new double[4];
        double t0p, cij;
        int id;
        double detj = 0;

        for (int el_nr = 0; el_nr < ne; el_nr++) {

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    H_current[i][j] = 0;
                }
                P_current[i] = 0;
            }

            for (int i = 0; i < 4; i++) {
                id = grid.elements[el_nr].globalNodeID[i];
                x[i] = grid.nodes[id].getX();
                y[i] = grid.nodes[id].getY();
                temp_0[i] = grid.nodes[id].getT();
            }

            for (int punktCalkowania = 0; punktCalkowania < 4; punktCalkowania++) // 4 - liczba punktow calkowania po powierzchni w elemencie
            {
                jakobian = new Jakobian(punktCalkowania, x, y);
                t0p = 0;

                for (int j = 0; j < 4; j++)  // 4 - liczba wezlow w wykorzystywanym elemencie skonczonym
                {
                    dNdx[j] = 1.0 / jakobian.getDet() * (jakobian.getJ_inverted()[0][0] * localElement.getdN_dKsi()[punktCalkowania][j]
                            + jakobian.getJ_inverted()[0][1] * localElement.getdN_dEta()[punktCalkowania][j]);

                    dNdy[j] = 1.0 / jakobian.getDet() * (jakobian.getJ_inverted()[1][0] * localElement.getdN_dKsi()[punktCalkowania][j]
                            + jakobian.getJ_inverted()[1][1] * localElement.getdN_dEta()[punktCalkowania][j]);

                    t0p += temp_0[j] * localElement.getN()[punktCalkowania][j];
                }



                detj = Math.abs(jakobian.getDet());
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        cij = c * ro * localElement.getN()[punktCalkowania][i] * localElement.getN()[punktCalkowania][j] * detj;
                        H_current[i][j] += k * (dNdx[i] * dNdx[j] + dNdy[i] * dNdy[j]) * detj + cij / dtau;
                        P_current[i] += cij / dtau * t0p;
                    }
                }
            }




            //warunki brzegowe
            for (int ipow = 0; ipow < grid.elements[el_nr].getN_pow(); ipow++) {
                id = grid.elements[el_nr].getA_pow()[ipow];
                switch (id) {
                    case 0:
                        detj = Math.sqrt(Math.pow(grid.elements[el_nr].ND[3].getX() - grid.elements[el_nr].ND[0].getX(), 2)
                                + Math.pow(grid.elements[el_nr].ND[3].getY() - grid.elements[el_nr].ND[0].getY(), 2)) / 2.0;
                        break;
                    case 1:
                        detj = Math.sqrt(Math.pow(grid.elements[el_nr].ND[0].getX() - grid.elements[el_nr].ND[1].getX(), 2)
                                + Math.pow(grid.elements[el_nr].ND[0].getY() - grid.elements[el_nr].ND[1].getY(), 2)) / 2.0;
                        break;
                    case 2:
                        detj = Math.sqrt(Math.pow(grid.elements[el_nr].ND[1].getX() - grid.elements[el_nr].ND[2].getX(), 2)
                                + Math.pow(grid.elements[el_nr].ND[1].getY() - grid.elements[el_nr].ND[2].getY(), 2)) / 2.0;
                        break;
                    case 3:
                        detj = Math.sqrt(Math.pow(grid.elements[el_nr].ND[2].getX() - grid.elements[el_nr].ND[3].getX(), 2)
                                + Math.pow(grid.elements[el_nr].ND[2].getY() - grid.elements[el_nr].ND[3].getY(), 2)) / 2.0;
                        break;
                }

                for (int p = 0; p < 2; p++)
                {
                    for (int n = 0; n < 4; n++)
                    {
                        for (int i = 0; i < 4; i++)
                        {
                            H_current[n][i] += alfa * localElement.getPOW()[id].N[p][n] * localElement.getPOW()[id].N[p][i] * detj;
                        }
                        P_current[n] += alfa * t_otoczenia * localElement.getPOW()[id].N[p][n] * detj;
                    }
                }
            }


            //agregacja
            for (int i = 0; i < 4; i++)
            {
                for (int j = 0; j < 4; j++)
                {
                    H_global[grid.elements[el_nr].globalNodeID[i]][grid.elements[el_nr].globalNodeID[j]] += H_current[i][j];
                }
                P_global[grid.elements[el_nr].globalNodeID[i]] += P_current[i];
            }


            /*if (el_nr == ne - 1) {
                for (int i = 0; i < nh; i++) {
                    for (int j = 0; j < nh; j++) {
                        System.out.printf("%.2f\t", H_global[i][j]);
                    }
                    System.out.println("");
                }
            }*/
        }
    }

    public static GlobalData getInstance() throws FileNotFoundException {
        if (globalData == null) {
            globalData = new GlobalData();
        }
        return globalData;
    }

    public double getH() {
        return H;
    }

    public double getB() {
        return B;
    }

    public double getdB() {
        return dB;
    }

    public void setdB(double dB) {
        this.dB = dB;
    }

    public double getdH() {
        return dH;
    }

    public void setdH(double dH) {
        this.dH = dH;
    }

    public int getnH() {
        return nH;
    }

    public int getnB() {
        return nB;
    }

    public int getNh() {
        return nh;
    }

    public int getNe() {
        return ne;
    }

    public double getT_begin() {
        return t_begin;
    }

    public double getTau() {
        return tau;
    }

    public double getDtau() {
        return dtau;
    }

    public double getT_otoczenia() {
        return t_otoczenia;
    }

    public double getAlfa() {
        return alfa;
    }

    public double getC() {
        return c;
    }

    public double getK() {
        return k;
    }

    public double getRo() {
        return ro;
    }

    public LocalElement getEl_lok() {
        return localElement;
    }

    public double[][] getH_current() {
        return H_current;
    }

    public double[] getP_current() {
        return P_current;
    }

    public double[][] getH_global() {
        return H_global;
    }

    public double[] getP_global() {
        return P_global;
    }

    public void setDtau(double dtau) {
        this.dtau = dtau;
    }
}