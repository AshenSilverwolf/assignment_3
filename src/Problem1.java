import java.util.ArrayList;

public class Problem1 {
    private final static int THREADS = 4;

    public static void main(String[] args) {
        long startTime, stopTime, elapsedTime;
        startTime = System.currentTimeMillis();
        ArrayList<Servant> thread = new ArrayList<>();

        Servant.createBag();

        // Create servant threads
        for (int i = 0; i < THREADS; i++) {
            Servant s = new Servant();
            s.setName("Servant-" + i);
            thread.add(s);
        }

        for (int i = 0; i < THREADS; i++) {
            thread.get(i).start();
        }

        for (int i = 0; i < THREADS; i++) {
            try {
                thread.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Total number of Thank You's written: " + Servant.numThankYous.get());
        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("Elapsed Time (ms): " + elapsedTime);
    }
}
