package ru.uryupin.lifegame;

import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class AreaTest {

    private static String path = new File(new File(".").getAbsolutePath()).getAbsolutePath();

    private static final String IN_FILE = path + "\\src\\test\\resources\\in.txt";
    private static final String OUT_FILE = path + "\\src\\test\\resources\\out.txt";

    private static final int AREA_WIDTH = 500;
    private static final int AREA_HEIGHT = 500;

    //    @Test
    public void AreaCreateTest() throws NoSuchFieldException, IllegalAccessException {

        Area area = new Area();
        String[] text;

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

    //    @Test
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

//    @Test
    public void MultiTest() throws InterruptedException {
        String[] text;
        text = new String[AREA_HEIGHT];
        text[0] = "...............................................";
        text[1] = "..................................................";
        text[2] = "..................................................";
        text[3] = "..................................................";
        text[4] = "..................................................";
        text[5] =  ".......00000000...................................";
        text[6] =  ".......0.0000.0..................................";
        text[7] =  ".......00000000.....................................";
        text[8] =  "....................................................";
        text[9] =  "...........................0....00000000.........";
        text[10] = "..........................00....0.0000.0...........";
        text[11] = "..........................0.0...00000000...............";
//    text[0] = "...............................................";
//        text[1] = ".....00.00.00.00.00.00.00.........................";
//        text[2] = ".....00.00.00.00.00.00.00.........................";
//        text[3] = ".................................................";
//        text[4] = ".....00.00.00.00.00.00.00...........................";
//        text[5] = ".....00.00.00.00.00.00.00...........................";
//        text[6] = "...........0......................................";
//        text[7] = ".....00.00.00.00.00.00.00...........................";
//        text[8] = ".....00.00.00.00.00.00.00...............................";
        text[12] = new String(new char[AREA_WIDTH]).replace('\0', Area.EMPTY_CELL_SYMBOL);
        for (int i = 13; i < AREA_HEIGHT; i++) {
            text[i] = ".";
        }
        saveFile(text);

        Area area = new Area();
        for (int i = 0; i < 1000; i++) {
            area.fileToStore(IN_FILE);
            area.calculateMove(1, 2);
            area.storeToFile(IN_FILE);
            area.storeToFile(OUT_FILE);
            TimeUnit.MILLISECONDS.sleep(500);
        }
    }

    @Test
    public void TimeLifeTest() {
        String[] text;

        text = new String[AREA_HEIGHT];
        text[0] = ".....0.........................";
        text[1] = "......0..............0............................";
        text[2] = "....000...............0...........................";
        text[3] = "....................000.........................";
        text[4] = "..........................................0.......";
        text[5] = "........................................0.0.......";
        text[6] = ".........................................00.......";
        text[7] = ".";
        text[8] = ".";
        text[9] = new String(new char[AREA_WIDTH]).replace('\0', Area.EMPTY_CELL_SYMBOL);
        for (int i = 10; i < AREA_HEIGHT; i++) {
            text[i] = ".";
        }
        saveFile(text);

        Area area = new Area().fileToStore(IN_FILE);
        System.out.println("Area dimensions: " + area.getWidth() + " X " + area.getHeight());

        long noThreadsTime = showCompare(0, 1, 0);
        showCompare(1, 1, noThreadsTime);
        showCompare(1, 10, noThreadsTime);
        showCompare(1, 100, noThreadsTime);
        System.out.println("-----------------------------------------------");
        int iteration = 10;
        noThreadsTime = showCompare(0, iteration, 0);
        showCompare(100, iteration, noThreadsTime);
        showCompare(10, iteration, noThreadsTime);
        showCompare(5, iteration, noThreadsTime);
        showCompare(2, iteration, noThreadsTime);
        System.out.println("-----------------------------------------------");
        iteration = 1000;
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
     */
    private static long showCompare(int th, int iteration, long noThreadsTime) {
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
            Arrays.stream(text).forEach(line -> {
                try {
                    writer.write(line + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
