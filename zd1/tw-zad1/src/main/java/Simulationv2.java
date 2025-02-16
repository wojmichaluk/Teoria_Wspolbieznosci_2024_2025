import java.util.ArrayList;
import java.util.List;

public class Simulationv2 {
    public static void main(String[] args) {
        int forksNumber = 20;

        Fork[] forks = new Fork[forksNumber];
        List<Philosopherv2> philosophers = new ArrayList<>(forksNumber);

        for (int i = 0; i < forksNumber; i++) {
            forks[i] = new Fork(i+1);
        }

        for (int i = 0; i < forksNumber; i++) {
            philosophers.add(new Philosopherv2(forks[i], forks[(i+1)%forksNumber], i+1, "phil_" + forksNumber + "/phil_n" + (i + 1) + ".csv"));
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
