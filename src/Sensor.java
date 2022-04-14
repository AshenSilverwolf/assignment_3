import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Sensor extends Thread {
    static final Random random = new Random();
    private final static int HOURS_TO_RUN = 5; // How many hours to collect readings
    private final static int WAIT_TIME = 1; // To simulate waiting a minute (WAIT_TIME measured in milliseconds)
    static ArrayList<LockFreeListDoubles<Double>> readings = new ArrayList<>();
    static ArrayList<LockFreeListDoubles<Double>> tenMinuteReadings = new ArrayList<>();
    static ArrayList<ArrayList<Double>> tenMinuteDiff = new ArrayList<>();

    public static void setupList() {
        for (int i = 0; i < HOURS_TO_RUN; i++) {
            readings.add(new LockFreeListDoubles<Double>());
        }

        for (int i = 0; i < HOURS_TO_RUN; i++) {
            for (int j = 0; j < 6; j++) {
                tenMinuteReadings.add(new LockFreeListDoubles<Double>());
            }
            tenMinuteDiff.add(new ArrayList<Double>());
        }
    }

    protected double randomReading() {
		double rangeMin = -100;
		double rangeMax = 70;
		double reading = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
		reading = Math.round(reading * 100);
		reading = reading / 100;
        return reading;
    }

    public void run() {
        for (int i = 0; i < HOURS_TO_RUN; i++) {
            for (int j = 0; j < 6; j++) { // Every minute
                for (int k = 0; k < 10; k++) { // Every minute
                    Double reading = randomReading();
                    readings.get(i).add(reading);
                    tenMinuteReadings.get(j).add(reading);
                    try {
                        // Wait a minute
                        Thread.sleep(WAIT_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static class LeadSensor extends Sensor {
        protected void createHourlyReport(int hour) {
            LockFreeListDoubles<Double> hourlyReadings = readings.get(hour);
            double[] highs = hourlyReadings.fiveHigest(8 * 60 - 10);
            double[] lows = hourlyReadings.fiveLowest();
            ArrayList<Double> list = tenMinuteDiff.get(hour);

			System.out.println("--- HOURLY REPORT: " + (hour+1) + " ---");

            System.out.print("Top 5 Highest Temps: ");
            for (int i = 0; i < 5; i++) {
                System.out.print(highs[i] + " ");
				if (i < 4) {
					System.out.print("| ");
				}
            }

            System.out.println();

            System.out.print("Top 5 Lowest Temps: ");
            for (int i = 0; i < 5; i++) {
                System.out.print(lows[i] + " ");
				if (i < 4) {
					System.out.print("| ");
				}
            }

            System.out.println();

            double high = Collections.max(list);
            int index = list.indexOf(high);
            System.out.println("Largest Difference: " + high + " between " + index * 10 + " and " + (index + 1) * 10 + " minutes.\n");
        }

        protected void createTenMinuteReport(int hours, int tenMinutes) {
            LockFreeListDoubles<Double> minutelyReadings = tenMinuteReadings.get(tenMinutes);
            double[] highs = minutelyReadings.fiveHigest(8 * 60 - 5);
            double[] lows = minutelyReadings.fiveLowest();
            ArrayList<Double> list = tenMinuteDiff.get(hours);

            // Calculate the difference between the highest and lowest readings
            list.add(Math.floor((highs[4] - lows[0]) * 1000) / 1000);
        }

        public void run() {
            for (int i = 0; i < HOURS_TO_RUN; i++) {
                for (int j = 0; j < 6; j++) { // Every minute
                    for (int k = 0; k < 10; k++) { // Every minute
                        double reading = randomReading();
                        readings.get(i).add(reading);
                        tenMinuteReadings.get(j).add(reading);
                        // Wait a minute
                        try {
                            Thread.sleep(WAIT_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    createTenMinuteReport(i, j);
                }
                createHourlyReport(i);
            }
        }
    }
}
