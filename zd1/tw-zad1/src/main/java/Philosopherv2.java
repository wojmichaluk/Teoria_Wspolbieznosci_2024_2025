import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class Philosopherv2 extends Thread {
    private final Fork left;
    private final Fork right;
    private final int id;
    private final File csvFile;

    Philosopherv2(Fork left, Fork right, int id, String csvPath) {
        this.left = left;
        this.right = right;
        this.id = id;
        this.csvFile = new File("src/main/resources/philv2/" + csvPath);

        csvFile.getParentFile().mkdirs();
        writeLineToCsv("Time");
    }

    private void think(String message) {
        System.out.println(message);
        try {
            Thread.sleep((long)(Math.random() * 1000));
        } catch(InterruptedException ie) {
            System.out.println(ie.getMessage());
        }
    }

    private void eat() {
        Instant start = Instant.now();
        while(left.isLifted() || right.isLifted()) {
            think("Filozof " + id + " nie może w tej turze podnieść obu widelców, więc wraca do myślenia... Hmm...");
        }
        left.lift();
        right.lift();
        long waitingTime = Duration.between(start, Instant.now()).toMillis();
        System.out.println("Filozof " + id + " podnosi oba widelce na raz (" + left.getId() + " i " + right.getId() + ")!");
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
            think("Filozof " + id + " myśli... Hmm...");
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
