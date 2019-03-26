public class Main {
    public static void main(String[] args) {
        Optimiser op = new Optimiser((x) -> x*x - 2*x - 2*Math.cos(x));
        //Optimiser op = new Optimiser((x) -> 200*x - x*x - 10000);
        op.setLog(true);

        try {
            op.Sven(-1,1);
            op.Dychotonomy_search(0.00001);

            op.Print_iterated_function(0.01);
            System.out.println(op.Parabols_search(-1,0.1,0.01));
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}