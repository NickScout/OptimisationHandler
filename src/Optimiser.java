import java.util.function.Function;

public class Optimiser {
    private Function<Double, Double> function;
    private Double a = null;
    private Double b = null;
    boolean log;

    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public Optimiser(Function<Double, Double> function) {
        this.function = function;
        this.log = false;
    }

    public Function<Double, Double> getFunction() {
        return function;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public void setA(Double a) {
        this.a = a;
    }

    public void setB(Double b) {
        this.b = b;
    }

    // x - start dot; h - step
    public void Sven(double x, double h)
            throws  Exception
    {
        if (h <= 0) throw new Exception("Non-positive step!");

        if(log) System.out.println("\n\n\n###Defining A, B via Sven method###\n\n\n");

        //in order not to invoke function(x +- h) several times,
        //lets create variables for 'em
        Double f_min = function.apply(x - h);
        Double f = function.apply(x);
        Double f_max = function.apply(x + h);
        //check is function(x0) defined
        if (f == null) throw new Exception("Invalid start dot!");

        //alg says to do so
        double k = 1;


        if (f_min >= f && f >= f_max) {
            if (log) System.out.println("case 1: f_min <= f <= f_max");
            x += h;
        } else if (f_min <= f && f <= f_max) {
            if (log) System.out.println("case 2: f_min >= f >= f_max");
            x -= h;
            h *= -1;

        } else if (f_min <= f && f >= f_max) {
            if (log) System.out.println("case 3: f_min <= f >= f_max");
            a = x - h;
            b = x + h;
            if (log) System.out.println(String.format("\n\n\n###\nA = %f;\nB = %f###\n\n", a,b));
            return;
        } else {
            throw new Exception("Not unimodal function!");
        }

        if(log) System.out.println(String.format("Looking for [a,b] via Sven method\n" +
                "with x0 = %f, h = %f",x,h));

        //x[k+1]
        double x_new = x;
        //x[k-1]
        double x_old;
        //function(x[x+1])
        double f_new = f;

        do {
            f = f_new;
            x_old = x;
            x = x_new;
            h *=2;
            x_new = x + h;
            f_new = function.apply(x_new);

            k++;
            if (k == 255) throw new Exception("stop right here, criminal scum!");
            if (log) System.out.println(String.format("x[k-1] = %f; x[k] = %f, x[k+1] = %f;" +
                            "\nfunction(x[k+1]) = %f, function(x[k]) = %f)\n",
                    x_old, x, x_new, f_new, f));

        } while (f_new >= f);

        a = h > 0 ? x_old : x_new;
        b = h > 0 ? x_new : x_old;

        if (log) System.out.println(String.format("finallly,\na = %f;\nb = %f", a,b));

    }

    public double Dychotonomy_search(double eps)
            throws Exception {

        if (eps <= 0) throw new Exception("eps must be > 0");
        if (this.a.isNaN() || this.b.isNaN()) throw new Exception("could not specify [a,b] borders");

        double sigm = 0.5 * eps;

        double a = this.a, b = this.b;
        double x1;
        double x2;

        if (log) System.out.println(String.format("\n\n\n###Looking for local minimum via Dychotomy search###\n\n\n" +
                "At [%f ; %f] with eps = %f; sigma = %f;\n", a, b, eps, sigm));

        while (Math.abs(b - a) > eps) {

            x1 = (a + b - sigm) / 2;
            x2 = (a + b + sigm) / 2;

            if (log) System.out.println(String.format("x1 and x2 are set to %f ; %f", x1, x2));

            if (function.apply(x1) <= function.apply(x2)) {
                b = x2;
            } else {
                a = x1;
            }

            if (log) System.out.println(String.format("[a,b] trimmed to [%f ; %f]", a,b));
        }
        double res = (a + b)/2;
        if (log) System.out.println(String.format("\n\n\n###Found minimum x = %f ###\n\n", res));
        return res;
    }


    public double Fibonacci_search (double eps)
            throws Exception
    {
        if (eps <= 0) throw new Exception("eps must be > 0");
        if (this.a.isNaN() || this.b.isNaN()) throw new Exception("could not specify [a,b] borders");

        if (log) System.out.println(String.format("\n\n\n###Looking for local minimum via Fibonacci search###\n\n\n" +
                "At [%f ; %f] with eps = %f;\n", a, b, eps));


        //Byne formula to find n-th Fibonnacci num;
        Function<Integer, Double> Byne = (n) -> {
            double sqrt5 = Math.sqrt(5);
            return 1/sqrt5*(Math.pow((1 + sqrt5)/2,n) - Math.pow((1 - sqrt5)/2,n));
        };

        double a = this.a;
        double b = this.b;

        int n;
        //Fibonacci[n+2] >= (b-a)/eps
        //this needed to predict amount of iterations n
        double n_cond = (b - a)/eps;
        for (n = 0; Byne.apply(n) < n_cond; n++) { }
        n -= 2;
        if (log) System.out.println(String.format("Algorythm will need %d iterations\n" +
                "Here we go:", n));

        //x1 = a + Fibb(n)/Fibb(n + 2) * (b - a) due to alg;
        double x1 = a + (Byne.apply(n) / Byne.apply(n + 2)) * (b - a);
        //x2 = a + Fibb(n + 1)/Fibb(n + 2) * (b - a) = a + b - x1
        double x2 = a + b - x1;
        double res = 0;

        for (int i = 0; i <= n; i++) {

            if (function.apply(x1) <= function.apply(x2)) {
                b = x2;
                x2 = x1;
                res = x1;
                x1 = a + b - x2;
            } else {
                a = x1;
                x1 = x2;
                res = x2;
                x2 = a + b - x1;
            }
            if (log) System.out.println(String.format("i = %d, res = %f", i, res));
        }
        if (log) System.out.println(String.format("\n\n\n###Found minimum x = %f ###\n\n", res));

        return res;
    }

    public double Parabols_search(double x, double h, double eps) throws Exception {
        if (x == 0) throw new Exception("Zero x found!");
        if (eps <= 0) throw new Exception("eps must be > 0");

        while ((function.apply(x + h)-2* function.apply(x)+ function.apply(x - h))/(h*h)<=0) x+= 0.1;
        double x1 = x-0.5*h*(function.apply(x + h)- function.apply(x - h))/(function.apply(x + h)-2* function.apply(x)+ function.apply(x - h));
        if (log) System.out.println(String.format("initial x1 = %f", x1));
        while (Math.abs(x1-x)>eps) {
            x=x1;
            x1=x-0.5*h*(function.apply(x + h)- function.apply(x - h))/(function.apply(x + h)-2* function.apply(x)+ function.apply(x - h));
            if (log) System.out.println(String.format("iterated xk = %f", x1));
        }

        if (log) System.out.println(String.format("\n\n\n###Found minimum x = %f ###\n\n", x1));

        return x1;
    }

    
//    public double Parabols_search_as_book_says(double x, double h, double eps) throws Exception {
//        if (eps <= 0) throw new Exception("eps must be > 0");
//        if (this.a.isNaN() || this.b.isNaN()) throw new Exception("could not specify [a,b] borders");
//
//        double a = this.a, b = this.b, c = (a + b) / 2;
//        double delta_minus = function.apply(a) - function.apply(c);
//        double delta_plus = function.apply(b) - function.apply(c);
//        if (delta_minus < 0 || delta_plus < 0 || delta_minus + delta_plus <= 0)
//            throw new Exception("not a convex function!");
//        Function<Double, Double> parabola_equasion = (X) -> ((delta_plus / (b - c) + delta_minus / (c - a)) * ((X - c) * (X - b)) / (b - a) + (delta_plus / (b - c)) * (X - c) + function.apply(c));
//        double s = c + 0.5 * ((b - c) * (b - c) * delta_minus - (c - a) * (c - a) * delta_plus) / ((b - c) * delta_minus + (c - a) * delta_plus);
//        double t;
//        do {
//            if (s == c) {
//                t = (c + a)/2;
//            } else {
//                t = s;
//            }
//            s = c + 0.5 * ((b - c) * (b - c) * delta_minus - (c - a) * (c - a) * delta_plus) / ((b - c) * delta_minus + (c - a) * delta_plus);
//
//        } while (Math.abs(b - a) > eps);
//
//    }


    public void Print_iterated_function(double step) //iterates F and prints result
    {
        System.out.println("\n\n\n###Printing function output###\n\n");
        if (step == 0) {
            System.out.println("Printing cancelled due to zero step value");
            return;
        }

        for (double i = a; i <= b ; i+= step) {
            System.out.println(String.format("x = %f; function(x) = %f", i, function.apply(i)));
        }
    }
}

