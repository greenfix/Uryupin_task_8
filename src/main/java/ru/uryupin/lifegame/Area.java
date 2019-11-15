package ru.uryupin.lifegame;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Area {

    private static final char FILLED_CELL_SYMBOL = '0';
    static final char EMPTY_CELL_SYMBOL = '.';

    private byte[] firstStore;
    private byte[] lastStore;
    private byte[] tempStore;

    private int width;
    private int height;

    byte[] getLastStore() {

        return lastStore;
    }

    int getWidth() {

        return width;
    }

    int getHeight() {

        return height;
    }

    /**
     * Calculates a given number of moves (no threads)
     *
     * @param step of moves
     * @return this
     */
    Area calculateMoveSingle(int step) {
        IntStream.range(0, step).forEach(s -> {
            IntStream.range(0, height).forEach(i -> IntStream.range(0, width).
                    forEach(j -> lastStore[j + i * width] = getFortune(j, i)));
            tempStore = firstStore;
            firstStore = lastStore;
            lastStore = tempStore;
        });
        return this;
    }

    /**
     * Calculates a given number of moves
     *
     * @param step    number of moves
     * @param threads number of threads
     * @return this
     */
    Area calculateMove(int step, int threads) {
        List<PairStarts> pairStarts = Area.calculateStart(height, threads);
        List<Future> futureList = new ArrayList<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(threads);
        IntStream.range(0, step).forEach(s -> {
            pairStarts.forEach((x) ->
                    futureList.add(threadPool.submit(new ThreadBox(this, x.getStart(), x.getOffset()))));
            futureList.forEach(x -> {
                try {
                    x.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
            futureList.clear();
            tempStore = firstStore;
            firstStore = lastStore;
            lastStore = tempStore;
        });
        threadPool.shutdown();

        return this;
    }

    /**
     * Calculates processing areas for threads
     *
     * @param height  Start of strip
     * @param threads The width of the strip
     * @return Processing areas for threads
     */
    static List<PairStarts> calculateStart(int height, int threads) {
        List<PairStarts> resultList = new ArrayList<>();
        int stripLen;
        int stripLenLast;
        if ((height | threads) == 0) {
            return resultList;
        }
        if (threads >= height) {
            resultList = IntStream.range(0, height).
                    mapToObj(i -> new PairStarts(i, 1)).collect(Collectors.toList());
        } else {
            stripLen = height / threads;
            stripLenLast = height % threads;
            int residual;
            int delta = 0;
            for (int i = 0; i < (threads); i++) {
                residual = (stripLenLast != 0) ? 1 : 0;
                resultList.add(new PairStarts(stripLen * i + delta, stripLen + residual));
                if (stripLenLast != 0) {
                    delta += 1;
                    stripLenLast--;
                }
            }
        }

        return resultList;
    }

    /**
     * Save the contents of the processing buffer to the output file
     *
     * @param fileOutPath Full path to the output file
     */
    void storeToFile(String fileOutPath) {
        File file = new File(fileOutPath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            IntStream.range(0, height).forEach(i -> {
                IntStream.range(0, width).forEach(j -> {
                    try {
                        writer.write((firstStore[j + i * width] == 0) ? EMPTY_CELL_SYMBOL : FILLED_CELL_SYMBOL);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                try {
                    writer.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            System.out.println("Output input error");
            System.exit(-1);
        }

    }

    /**
     * Copy the contents of the input file to the processing buffer
     *
     * @param fileInPath Full path to the input file
     * @return this
     */
    Area fileToStore(String fileInPath) {
        determineSizeArea(fileInPath);
        loadContent(fileInPath);

        return this;
    }

    /**
     * Determine the length and width of the processing matrix and store it in class variables
     *
     * @param fileInPath File input
     */
    private void determineSizeArea(String fileInPath) {
        File file = new File(fileInPath);
        width = 0;
        height = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                width = Math.max(width, line.length());
                ++height;
                line = reader.readLine();
            }
            firstStore = new byte[width * height];
            lastStore = new byte[width * height];
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found");
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("Output input error");
            System.exit(-1);
        }
    }

    /**
     * Fill the processing matrix with data from the file
     *
     * @param fileInPath File input
     */
    private void loadContent(String fileInPath) {
        File file = new File(fileInPath);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int keyHeight = 0;
            byte[] shortLine;
            int lenShortLine;
            String line = reader.readLine();
            while (line != null) {
                shortLine = line.getBytes();
                lenShortLine = shortLine.length;
                for (int i = 0; i < width; i++) {
                    firstStore[i + keyHeight * width] = (((lenShortLine > i) ? shortLine[i] :
                            EMPTY_CELL_SYMBOL) == EMPTY_CELL_SYMBOL) ? 0 : (byte) 1;
                }
                keyHeight++;
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found");
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("Output input error");
            System.exit(-1);
        }
    }

    /**
     * Detects cell condition
     *
     * @param x x position
     * @param y y position
     * @return 1 - if cell alive or born, 0 - otherwise
     */
    byte getFortune(int x, int y) {
        byte neighbour = 0;
        int testPlaceX;
        int testPlaceY;

        for (int j1 = -1; j1 < 2; j1++)
            for (int i1 = -1; i1 < 2; i1++) {

                if ((i1 | j1) == 0) {
                    continue;
                }

                testPlaceX = (x + i1) % width;
                testPlaceY = ((y + j1) % height) * width;

                if (testPlaceX < 0) {
                    testPlaceX = width - 1;
                }

                if (testPlaceY < 0) {
                    testPlaceY = (height - 1) * width;
                }

                neighbour += firstStore[testPlaceX + testPlaceY];
            }

        if (firstStore[x + y * width] == 0) {
            if (neighbour == 3) {
                return 1;
            } else {
                return 0;
            }
        } else {
            if (neighbour < 2 || neighbour > 3) {
                return 0;
            } else {
                return 1;
            }
        }
    }
}