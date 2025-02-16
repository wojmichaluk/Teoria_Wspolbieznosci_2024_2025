import java.util.ArrayList;
import java.util.List;

public class Simulationv6 {
    public static void main(String[] args) {
        int forksNumber = 20;

        Fork[] forks = new Fork[forksNumber];
        List<Philosopherv6> philosophers = new ArrayList<>(forksNumber);
        Waiter waiter = new Waiter(forksNumber);

        for (int i = 0; i < forksNumber; i++) {
            forks[i] = new Fork(i+1);
        }

        for (int i = 0; i < forksNumber; i++) {
            philosophers.add(new Philosopherv6(forks[i], forks[(i+1)%forksNumber], waiter, i+1, "phil_" + forksNumber + "/phil_n" + (i + 1) + ".csv"));
        }

        philosophers.forEach(Thread::start);
        philosophers.forEach(philosopherThread -> {
            try {
                philosopherThread.join();
            } catch (InterruptedException ie) {
                System.out.println(ie.getMessage());
            }
        });
    }
}
