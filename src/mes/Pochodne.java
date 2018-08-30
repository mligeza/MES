package mes;

public class Pochodne {


        private double[][] dN_po_ksi;
        private double[][] dN_po_eta;

        public Pochodne () {
            double[] ksi = { ( - 1 / Math.pow( 3, - 1 ) ), ( 1 / Math.pow( 3, - 1 ) ), ( - 1 / Math.pow( 3, - 1 ) ), ( 1 / Math.pow( 3, - 1 ) ) };
            double[] eta = { ( - 1 / Math.pow( 3, - 1 ) ), ( - 1 / Math.pow( 3, - 1 ) ), ( 1 / Math.pow( 3, - 1 ) ), ( 1 / Math.pow( 3, - 1 ) ) };

            dN_po_ksi = new double[4][4];
            dN_po_eta = new double[4][4];
            for ( int i = 0; i < 4; i++ ) {
                dN_po_ksi[i][0] = N1_ksi( ksi[i] );
                dN_po_ksi[i][1] = N2_ksi( ksi[i] );
                dN_po_ksi[i][2] = N3_ksi( ksi[i] );
                dN_po_ksi[i][3] = N4_ksi( ksi[i] );

                dN_po_eta[i][0] = N1_eta( eta[i] );
                dN_po_eta[i][1] = N2_eta( eta[i] );
                dN_po_eta[i][2] = N3_eta( eta[i] );
                dN_po_eta[i][3] = N4_eta( eta[i] );
            }
        }

        //
        private double N1_ksi ( double eta ) {
            return ( - ( 1.0 / 4.0 ) * ( 1 - eta ) );
        }
        private double N1_eta ( double ksi ) {
            return ( - ( 1.0 / 4.0 ) * ( 1 - ksi ) );
        }

        //
        private double N2_ksi ( double eta ) {
            return ( ( 1.0 / 4.0 ) * ( 1 - eta ) );
        }
        private double N2_eta ( double ksi ) {
            return ( - ( 1.0 / 4.0 ) * ( 1 + ksi ) );
        }

        //
        private double N3_ksi ( double eta ) {
            return ( ( 1.0 / 4.0 ) * ( 1 + eta ) );
        }
        private double N3_eta ( double ksi ) {
            return ( ( 1.0 / 4.0 ) * ( 1 + ksi ) );
        }

        //
        private double N4_ksi ( double eta ) {
            return ( - ( 1.0 / 4.0 ) * ( 1 + eta ) );
        }
        private double N4_eta ( double ksi ) {
            return ( ( 1.0 / 4.0 ) * ( 1 - ksi ) );
        }


        public double[][] getdN_po_ksi () {
            return dN_po_ksi;
        }
        public double[][] getdN_po_eta () {
            return dN_po_eta;
        }
    }

