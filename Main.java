import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (in.hasNext()) {
            Polynomial exp = new Polynomial(in.nextLine());
            //System.out.println(exp.toString());
            System.out.println(exp.derivative().toString());
        }
    }
}
