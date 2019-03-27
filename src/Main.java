 class Main {
    public static void main(String ... args) {
        Optimiser op = new Optimiser((x) -> x*x - 2*x - 2*Math.cos(x));

        op.setLog(true);

        try {
            op.Parabols_search(50, 0.001, 0.0001);
            op.Print_iterated_function(0.01, -2,2);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
