package mes;

import java.io.FileNotFoundException;

public class Element {



    public Node[] ND; // wezly w elemencie
    public Surface POW[]; //powierzchnie elementu

    private int n_pow; // liczba zewnetrznych powierzchni
    private final int[] a_pow; // lokalne numery powierzchni kontaktowych elementu

    public int[] globalNodeID;

    GlobalData globalData;

    public Element(int i, int j, Node[] NDs) throws FileNotFoundException { //db - szerokosc 1 elementu, dh - wysokosc 1 elementu

        ND = new Node[4];
        POW = new Surface[4];
        globalNodeID = new int[4];
        globalData = GlobalData.getInstance();

        //wyznaczamy wspolrzedne wezlow w elemencie
        ND[0] = NDs[0];
        ND[1] = NDs[1];
        ND[2] = NDs[2];
        ND[3] = NDs[3];

        //wyznaczamy globalne id wezlow w elemencie
        globalNodeID[0] = globalData.getnH() * i + j;
        globalNodeID[1] = globalData.getnH() * (i + 1) + j;
        globalNodeID[2] = globalData.getnH() * (i + 1) + (j + 1);
        globalNodeID[3] = globalData.getnH() * i + (j + 1);

        //wezly na powierzchniach
        POW[0] = new Surface(ND[3], ND[0]);
        POW[1] = new Surface(ND[0], ND[1]);
        POW[2] = new Surface(ND[1], ND[2]);
        POW[3] = new Surface(ND[2], ND[3]);

        n_pow = 0;
        for (int k = 0; k < 4; k++) {
            if (POW[k].getNodes()[0].getStatus() == 1 && POW[k].getNodes()[1].getStatus() == 1) {
                n_pow++;
            }
        }
        a_pow = new int[n_pow];

        int counter = 0;
        for (int k = 0; k < 4; k++) {
            if (POW[k].getNodes()[0].getStatus() == 1 && POW[k].getNodes()[1].getStatus() == 1) {
                a_pow[counter++] = k;
            }
        }
    }


    public int getN_pow() {
        return n_pow;
    }

    public int[] getA_pow() {
        return a_pow;
    }

    public Node[] getND() {
        return ND;
    }
}
