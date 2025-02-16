import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class Philosopherv4 extends Thread {
    private final Fork left;
    private final Fork right;
    private final int id;
    private final File csvFile;

    Philosopherv4(Fork left, Fork right, int id, String csvPath) {
        this.left = left;
        this.right = right;
        this.id = id;
        this.csvFile = new File("src/main/resources/philv4/" + csvPath);

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
        double prob = Math.random();

        if (prob < 0.5) {
            start = Instant.now();
            right.lift();
            System.out.println("Filozof " + id + " podnosi prawy widelec (" + right.getId() + ")!");

            left.lift();
            waitingTime = Duration.between(start, Instant.now()).toMillis();
            System.out.println("Filozof " + id + " podnosi lewy widelec (" + left.getId() + ")!");
        } else {
            start = Instant.now();
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


