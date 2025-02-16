import java.util.concurrent.Semaphore;

public class Waiter {
    private final Semaphore semaphore;

    Waiter(int philosophers) {
        this.semaphore = new Semaphore(philosophers - 1);
    }

    public boolean isDiningHallFull() {
        return semaphore.availablePermits() == 0;
    }

    public void makeOrder() {
        try {
            semaphore.acquire();
        } catch(InterruptedException ie) {
            System.out.println(ie.getMessage());
        }
    }

    public void askForReceipt() {
        semaphore.release();
    }
}
