import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class Philosopherv6 extends Thread {
    private final Fork left;
    private final Fork right;
    private final Waiter waiter;
    private final int id;
    private final File csvFile;

    Philosopherv6(Fork left, Fork right, Waiter waiter, int id, String csvPath) {
        this.left = left;
        this.right = right;
        this.waiter = waiter;
        this.id = id;
        this.csvFile = new File("src/main/resources/philv6/" + csvPath);

        csvFile.getParentFile().mkdirs();
        writeLineToCsv("Time");
    }

    private void think() {
        System.out.println("Filozof " + id + " myśli... Hmm...");
        try {
            Thread.sleep((long)(Math.random() * 1000));
        } catch(InterruptedException ie) {
            System.out.println(ie.getMessage());
        }
    }

    private void eat() {
        Instant start;
        long waitingTime;
        boolean ateOnCorridor;

        if(waiter.isDiningHallFull()) {
            ateOnCorridor = true;
            System.out.println("Filozof " + id + " został wyproszony do jadalni!");

            start = Instant.now();
            right.lift();
            System.out.println("Filozof " + id + " podnosi prawy widelec (" + right.getId() + ")!");

            left.lift();
            waitingTime = Duration.between(start, Instant.now()).toMillis();
            System.out.println("Filozof " + id + " podnosi lewy widelec (" + left.getId() + ")!");
        } else {
            ateOnCorridor = false;
            start = Instant.now();
            waiter.makeOrder();

            left.lift();
            System.out.println("Filozof " + id + " podnosi lewy widelec (" + left.getId() + ")!");

            right.lift();
            waitingTime = Duration.between(start, Instant.now()).toMillis();
            System.out.println("Filozof " + id + " podnosi prawy widelec (" + right.getId() + ")!");
        }

        System.out.println("Filozof " + id + " podniósł oba widelce, więc zabiera się za jedzenie!");
        try {
            Thread.sleep((long)(Math.random() * 2000));
        } catch(InterruptedException ie) {
            System.out.println(ie.getMessage());
        }

        System.out.println("Filozof " + id + " już zjadł! Mniam!");
        left.putDown();
        right.putDown();

        if (!ateOnCorridor) {
            waiter.askForReceipt();
        }
        writeLineToCsv(String.valueOf(waitingTime));
    }

    public void run() {
        while(true) {
            think();
            eat();
        }
    }

    private void writeLineToCsv(String line) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile, true))) {
            bw.write(line);
            bw.newLine();
        } catch(IOException ioe) {
            System.out.println("Błąd przy zapisywaniu do pliku");
        }
    }
}
