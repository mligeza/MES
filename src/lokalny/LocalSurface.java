package lokalny;

public class LocalSurface {

    public LocalNode[] ND;

    public double N[][];

    public LocalSurface(LocalNode node1, LocalNode node2) {
        ND = new LocalNode[2];

        ND[0] = node1;
        ND[1] = node2;

        N = new double[2][4];
    }
}