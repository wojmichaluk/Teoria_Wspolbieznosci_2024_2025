import java.util.concurrent.Semaphore;

public class Fork {
    private final Semaphore semaphore = new Semaphore(1);
    private final int id;

    Fork(int id) {
        this.id = id;
    }
    public boolean isLifted() {
        return semaphore.availablePermits() == 0;
    }

    public void lift() {
        try {
            semaphore.acquire();
        } catch(InterruptedException ie) {
            System.out.println(ie.getMessage());
        }
    }

    public void putDown() {
        semaphore.release();
    }

    public int getId() {
        return id;
    }
}
