package mes;

import ukladyRownan.UkladyRownanLiniowych;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {


        GlobalData globalData = GlobalData.getInstance();
        Grid grid = Grid.getInstance();
        double[] t;


        grid.showNodes();
        System.out.println();

       // grid.showElements();


        System.out.println();
        System.out.println();


        for (int itau = 0; itau < globalData.getTau(); itau++) {
            globalData.compute();
            t = UkladyRownanLiniowych.gaussElimination(globalData.getNh(), globalData.getH_global(), globalData.getP_global());


            for (int i = 0; i < globalData.getNh(); i++) {
                grid.nodes[i].setT(t[i]);
            }
        }

        int count = 0;
        for (int i = 0; i < globalData.getnB(); i++) {
            for (int j = 0; j < globalData.getnH(); j++) {
                System.out.printf("%.14f\t", grid.nodes[count++].getT());
            }
            System.out.println("");
        }
        System.out.println("\n\n");
    }
}
