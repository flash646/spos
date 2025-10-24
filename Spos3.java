import java.util.concurrent.Semaphore;

class ReaderWriter {

    // Semaphore to control access to the shared resource
    static Semaphore mutex = new Semaphore(1);       // For reader count update
    static Semaphore writeLock = new Semaphore(1);   // For writers
    static int readerCount = 0;

    // Reader thread
    static class Reader extends Thread {
        int readerId;

        Reader(int id) {
            this.readerId = id;
        }

        public void run() {
            try {
                // Entry section
                mutex.acquire();
                readerCount++;
                if (readerCount == 1) {
                    writeLock.acquire(); // First reader locks writers
                }
                mutex.release();

                // Critical section
                System.out.println("Reader " + readerId + " is reading.");

                Thread.sleep(1000); // Simulate reading time

                // Exit section
                mutex.acquire();
                readerCount--;
                if (readerCount == 0) {
                    writeLock.release(); // Last reader releases writer lock
                }
                mutex.release();

                System.out.println("Reader " + readerId + " has finished reading.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Writer thread
    static class Writer extends Thread {
        int writerId;

        Writer(int id) {
            this.writerId = id;
        }

        public void run() {
            try {
                // Entry section
                writeLock.acquire();

                // Critical section
                System.out.println("Writer " + writerId + " is writing.");

                Thread.sleep(1500); // Simulate writing time

                // Exit section
                System.out.println("Writer " + writerId + " has finished writing.");
                writeLock.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Main function
    public static void main(String[] args) {
        // Create multiple readers and writers
        Reader r1 = new Reader(1);
        Reader r2 = new Reader(2);
        Writer w1 = new Writer(1);
        Reader r3 = new Reader(3);
        Writer w2 = new Writer(2);

        // Start threads
        r1.start();
        w1.start();
        r2.start();
        r3.start();
        w2.start();
    }
}
