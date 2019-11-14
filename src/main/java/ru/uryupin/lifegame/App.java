package ru.uryupin.lifegame;

import java.util.concurrent.ExecutionException;

public class App {

    private static final byte NUM_ARGS = 3;

    //private static final String IN_FILE = "C:\\Users\\conrg\\IdeaProjects\\stc_21_task_8\\in.txt";
    //private static final String OUT_FILE = "C:\\Users\\conrg\\IdeaProjects\\stc_21_task_8\\out.txt";


    public static void main(String[] args) throws InterruptedException, ExecutionException {

        if (args.length < NUM_ARGS) {
            System.out.println("Not enough parameters");
            System.exit(-1);
        }

        Area area = new Area();
        area.fileToStore(args[0]).
                calculateMove(Integer.parseInt(args[2]), 2).
                storeToFile(args[1]);

//        String[] text;
//        if (true) {
//            text = new String[1000];
//            text[0] = ".....0.........................";
//            text[1] = "......0...........................................";
//            text[2] = "....000...........................................";
//            text[3] = "..................................................";
//            text[4] = "..........................................0.......";
//            text[5] = "........................................0.0.......";
//            text[6] = ".........................................00.......";
//            text[7] = "..................................................";
//            text[8] = "..................................................";
//            text[9] = "..................................................";
//
//
//
//            for (int i = 10; i < 1000; i++) {
//                text[i] = "..................................................";
//            }
//            saveFile(text);
//        } else {
//            text = new String[5]; // 10 x 5
//            text[0] = ".....";
//            text[1] = ".....";
//            text[2] = ".000.";
//            text[3] = ".....";
//            text[4] = ".....";
//            saveFile(text);
//        }
//
//        int iteration = 10;
//        long noThreadsTime = showCompare(0, iteration, 0);
//        showCompare(100, iteration, noThreadsTime);
//        showCompare(10, iteration, noThreadsTime);
//        showCompare(5, iteration, noThreadsTime);
//        showCompare(2, iteration, noThreadsTime);
//        System.out.println("-----------------------------------------------");
//        noThreadsTime = showCompare(0, 1, 0);
//        showCompare(1, 1, noThreadsTime);
//        System.out.println("-----------------------------------------------");
//        iteration = 500;
//        noThreadsTime = showCompare(0, iteration, 0);
//        showCompare(100, iteration, noThreadsTime);
//        showCompare(10, iteration, noThreadsTime);
//        showCompare(5, iteration, noThreadsTime);
//        showCompare(2, iteration, noThreadsTime);
    }

//    /**
//     * calculates and displays the execution time of the operation
//     *
//     * @param th            Number of threads
//     * @param iteration     Number of iterations
//     * @param noThreadsTime Operation time
//     * @return Operation time
//     * @throws InterruptedException InterruptedException
//     */
//    private static long showCompare(int th, int iteration, long noThreadsTime) throws InterruptedException {
//        Area area = new Area();
//        area.fileToStore(IN_FILE);
//        long start = System.currentTimeMillis();
//        if (th == 0) {
//            area.calculateMoveSingle(iteration);
//        } else {
//            area.calculateMove(iteration, th);
//        }
//        long finish = System.currentTimeMillis();
//        long timeConsumedMillis = finish - start;
//        area.storeToFile(OUT_FILE);
//        if (th == 0) {
//            System.out.print("No treads, " + iteration + " iterations: ");
//            System.out.print(timeConsumedMillis);
//            System.out.println(" ms");
//        } else {
//            System.out.print(th + " threads, " + iteration + " iterations: ");
//            System.out.print(timeConsumedMillis);
//            System.out.print(" ms. ");
//            double percent = 100 - ((double) timeConsumedMillis / (double) noThreadsTime) * 100;
//            String word = percent >= 0 ? "Faster " : "Slower";
//            System.out.println(word + String.format(" %.1f%%", Math.abs(percent)));
//        }
//
//        return timeConsumedMillis;
//    }
//
//    /**
//     * Writes a template to the input file (for tests)
//     *
//     * @param text template
//     */
//    private static void saveFile(String[] text) {
//        try (FileWriter writer = new FileWriter(IN_FILE, false)) {
//            for (String line : text
//            ) {
//                writer.write(line);
//                writer.write("\n");
//            }
//            writer.flush();
//        } catch (IOException ex) {
//            System.out.println(ex.getMessage());
//        }
//    }
}
