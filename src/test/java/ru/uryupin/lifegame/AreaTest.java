package ru.uryupin.lifegame;

import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;


public class AreaTest {

    private static final String IN_FILE = "C:\\Users\\conrg\\IdeaProjects\\stc_21_task_8\\in.txt";
    private static final String OUT_FILE = "C:\\Users\\conrg\\IdeaProjects\\stc_21_task_8\\out.txt";

    @Test
    public void AreaCreateTest() throws NoSuchFieldException, IllegalAccessException, InterruptedException, ExecutionException {

        Area area = new Area();
        String[] text;

        //char fill = Area.FILLED_CELL_SYMBOL;
        char empty = Area.EMPTY_CELL_SYMBOL;

        text = new String[5];
        text[0] = ".....";
        text[1] = ".....";
        text[2] = ".XXX.";
        text[3] = ".....";
        text[4] = ".....";
        saveFile(text);

        area.fileToStore(IN_FILE);
        area.calculateMove(1, 1);
        area.storeToFile(OUT_FILE);

        text = new String[0];
        saveFile(text);
        area.fileToStore(IN_FILE);
        area.calculateMove(1, 1);
        area.storeToFile(OUT_FILE);

        text = new String[1];
        text[0] = "X";
        saveFile(text);

        area.fileToStore(IN_FILE).calculateMove(1, 1);
        assertEquals(area.getFortune(0, 0), 0);

        text = new String[3];
        text[0] = "...";
        text[1] = "XXX";
        text[2] = "...";
        saveFile(text);
        area.fileToStore(IN_FILE);
        assertEquals(area.getFortune(1, 1), 1);

        text = new String[5]; // 10 x 5
        text[0] = "X.........";
        text[1] = "....X.....";
        text[2] = "...XXX...X";
        text[3] = "....X.....";
        text[4] = "XX.......X";
        saveFile(text);

        area.fileToStore(IN_FILE);

        Class clazz = area.getClass();
        Field field;

        field = clazz.getDeclaredField("width");
        field.setAccessible(true);
        assertEquals((int) field.get(area), 10);

        field = clazz.getDeclaredField("height");
        field.setAccessible(true);
        assertEquals((int) field.get(area), 5);

        field = clazz.getDeclaredField("firstStore");
        field.setAccessible(true);
        byte[] storeByte = (byte[]) field.get(area);

        int countByte = 0;
        for (String line : text) {
            byte[] lineByte = line.getBytes();
            for (byte b : lineByte) {
                assertEquals(storeByte[countByte], b == (int) empty ? (byte) 0 : (byte) 1);
                countByte++;
            }
        }

        // 4 соседа
        assertEquals(area.getFortune(4, 2), 0);
        // 3 соседа
        assertEquals(area.getFortune(4, 1), 1);
        // 2 соседа
        assertEquals(area.getFortune(1, 3), 0);
        // 1 сосед
        assertEquals(area.getFortune(4, 4), 0);
        // 0 соседей
        assertEquals(area.getFortune(1, 2), 0);

        // 0 соседей
        assertEquals(area.getFortune(6, 4), 0);
        // 1 сосед
        assertEquals(area.getFortune(0, 2), 0);
        // 3 соседа
        assertEquals(area.getFortune(9, 0), 1);
        // 3 соседа
        assertEquals(area.getFortune(1, 0), 1);
        // 3 соседа
        assertEquals(area.getFortune(9, 3), 1);

        text = new String[5]; // 10 x 5
        text[0] = ".....";
        text[1] = ".....";
        text[2] = ".000.";
        text[3] = ".....";
        text[4] = ".....";
        saveFile(text);

        area.fileToStore(IN_FILE).calculateMove(1, 1).storeToFile(OUT_FILE);
        area.fileToStore(IN_FILE).calculateMoveSingle(1).storeToFile(OUT_FILE);
    }

    @Test
    public void calculateStartsTest() {
        List<PairStarts> startList = Area.calculateStart(0, 0);
        assertEquals(startList.size(), 0);

        startList = Area.calculateStart(500, 500);

        int key = 0;
        for (PairStarts start : startList
        ) {
            assertEquals(start.getStart(), key);
            assertEquals(start.getOffset(), 1);
            key++;
        }

        startList = Area.calculateStart(500, 600);

        key = 0;
        for (PairStarts start : startList
        ) {
            assertEquals(start.getStart(), key);
            assertEquals(start.getOffset(), 1);
            key++;
        }

        startList = Area.calculateStart(500, 100);

        key = 0;
        for (PairStarts start : startList
        ) {
            assertEquals(start.getStart(), key);
            assertEquals(start.getOffset(), 5);
            key += 5;
        }

        startList = Area.calculateStart(100, 3);

        PairStarts pair = startList.get(0);
        assertEquals(pair.getStart(), 0);
        assertEquals(pair.getOffset(), 34);

        pair = startList.get(1);
        assertEquals(pair.getStart(), 34);
        assertEquals(pair.getOffset(), 33);

        pair = startList.get(2);
        assertEquals(pair.getStart(), 67);
        assertEquals(pair.getOffset(), 33);

        startList = Area.calculateStart(128, 3);

        pair = startList.get(0);
        assertEquals(pair.getStart(), 0);
        assertEquals(pair.getOffset(), 43);

        pair = startList.get(1);
        assertEquals(pair.getStart(), 43);
        assertEquals(pair.getOffset(), 43);

        pair = startList.get(2);
        assertEquals(pair.getStart(), 86);
        assertEquals(pair.getOffset(), 42);

    }

    @Test
    public void TimeLifeTest() throws InterruptedException, ExecutionException {
        String[] text;

        text = new String[1000];
        text[0] = ".....0.........................";
        text[1] = "......0...........................................";
        text[2] = "....000...........................................";
        text[3] = "..................................................";
        text[4] = "..........................................0.......";
        text[5] = "........................................0.0.......";
        text[6] = ".........................................00.......";
        text[7] = "..................................................";
        text[8] = "..................................................";
        text[9] = "..................................................";
        for (int i = 10; i < 1000; i++) {
            text[i] = "..................................................";
        }
        saveFile(text);

        int iteration = 10;
        long noThreadsTime = showCompare(0, iteration, 0);
        showCompare(100, iteration, noThreadsTime);
        showCompare(10, iteration, noThreadsTime);
        showCompare(5, iteration, noThreadsTime);
        showCompare(2, iteration, noThreadsTime);
        System.out.println("-----------------------------------------------");
        noThreadsTime = showCompare(0, 1, 0);
        showCompare(1, 1, noThreadsTime);
        System.out.println("-----------------------------------------------");
        iteration = 2000;
        noThreadsTime = showCompare(0, iteration, 0);
        showCompare(100, iteration, noThreadsTime);
        showCompare(10, iteration, noThreadsTime);
        showCompare(5, iteration, noThreadsTime);
        showCompare(2, iteration, noThreadsTime);
    }

    /**
     * calculates and displays the execution time of the operation
     *
     * @param th            Number of threads
     * @param iteration     Number of iterations
     * @param noThreadsTime Operation time
     * @return Operation time
     * @throws InterruptedException InterruptedException
     */
    private static long showCompare(int th, int iteration, long noThreadsTime) throws InterruptedException, ExecutionException {
        Area area = new Area();
        area.fileToStore(IN_FILE);
        long start = System.currentTimeMillis();
        if (th == 0) {
            area.calculateMoveSingle(iteration);
        } else {
            area.calculateMove(iteration, th);
        }
        long finish = System.currentTimeMillis();
        long timeConsumedMillis = finish - start;
        area.storeToFile(OUT_FILE);
        if (th == 0) {
            System.out.print("No treads, " + iteration + " iterations: ");
            System.out.print(timeConsumedMillis);
            System.out.println(" ms");
        } else {
            System.out.print(th + " threads, " + iteration + " iterations: ");
            System.out.print(timeConsumedMillis);
            System.out.print(" ms. ");
            double percent = 100 - ((double) timeConsumedMillis / (double) noThreadsTime) * 100;
            String word = percent >= 0 ? "Faster " : "Slower";
            System.out.println(word + String.format(" %.1f%%", Math.abs(percent)));
        }

        return timeConsumedMillis;
    }

    /**
     * Writes a template to the input file (for tests)
     *
     * @param text template
     */
    private void saveFile(String[] text) {
        try (FileWriter writer = new FileWriter(AreaTest.IN_FILE, false)) {
            for (String line : text
            ) {
                writer.write(line);
                writer.write("\n");
            }
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
