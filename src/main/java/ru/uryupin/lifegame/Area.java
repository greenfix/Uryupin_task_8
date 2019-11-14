package ru.uryupin.lifegame;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Area {

    static final char FILLED_CELL_SYMBOL = '0';
    static final char EMPTY_CELL_SYMBOL = '.';

    private byte[] firstStore;
    private byte[] lastStore;
    private byte[] tempStore;

    private int width;
    private int height;

    Area() {
    }

    byte[] getLastStore() {
        return lastStore;
    }

    int getWidth() {
        return width;
    }

    /**
     * Calculates a given number of moves (no threads)
     *
     * @param step of moves
     * @return this
     */
    Area calculateMoveSingle(int step) {

        for (int s = 1; s <= step; s++) {

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    lastStore[j + i * width] = getFortune(j, i);
                }
            }

            tempStore = firstStore;
            firstStore = lastStore;
            lastStore = tempStore;
        }

        return this;
    }

    /**
     * Calculates a given number of moves
     *
     * @param step    number of moves
     * @param threads number of threads
     * @return this
     */
    Area calculateMove(int step, int threads) throws InterruptedException {

        List<PairStarts> pairStarts = Area.calculateStart(height, threads);
        List<Thread> threadList = new ArrayList<>();

        for (int s = 1; s <= step; s++) {

            for (PairStarts pair : pairStarts) {
                threadList.add(new Thread(new ThreadBox(this, pair.getStart(), pair.getOffset())));
            }

            for (Thread threadEach : threadList
            ) {
                threadEach.start();
                threadEach.join();
            }

            threadList.clear();
            tempStore = firstStore;
            firstStore = lastStore;
            lastStore = tempStore;
        }

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

        if (height == 0 || threads == 0) {
            return resultList;
        }

        int stripLen;
        int stripLenLast;

        if (threads >= height) {

            for (int i = 0; i < height; i++) {
                resultList.add(new PairStarts(i, 1));
            }

        } else {

            stripLen = height / threads;
            stripLenLast = height % threads;
            int residual;
            int delta = 0;

            for (int i = 0; i < (threads); i++) {
                if (stripLenLast != 0) {
                    residual = 1;
                } else {
                    residual = 0;
                }
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
     * Сopy the contents of the processing buffer to the output file
     *
     * @param fileOutPath Full path to the output file
     */
    void storeToFile(String fileOutPath) {
        File file = new File(fileOutPath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    writer.write((firstStore[j + i * width] == 0) ? EMPTY_CELL_SYMBOL : FILLED_CELL_SYMBOL);
                }
                writer.write("\n");
            }
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
        getSizeArea(fileInPath);
        loadContent(fileInPath);
        return this;
    }

    /**
     * Determine the length and width of the processing matrix and store it in class variables
     *
     * @param fileInPath File input
     */
    private void getSizeArea(String fileInPath) {
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
            int emptyCode = EMPTY_CELL_SYMBOL;
            byte[] fullLine = new byte[width];
            byte[] shortLine;
            int lenShortLine;
            String line = reader.readLine();
            while (line != null) {
                shortLine = line.getBytes();
                lenShortLine = shortLine.length;
                for (int i = 0; i < width; i++) {
                    if (lenShortLine > i) {
                        fullLine[i] = shortLine[i];
                    } else {
                        fullLine[i] = (byte) emptyCode;
                    }
                }
                for (int i = 0; i < width; i++) {
                    firstStore[i + keyHeight * width] = (fullLine[i] == emptyCode) ? (byte) 0 : (byte) 1;
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
        for (int j = -1; j < 2; j++) {
            for (int i = -1; i < 2; i++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                testPlaceX = (x + i) % width;
                if (testPlaceX < 0) {
                    testPlaceX = width - 1;
                }
                testPlaceY = ((y + j) % height) * width;
                if (testPlaceY < 0) {
                    testPlaceY = (height - 1) * width;
                }
                neighbour += firstStore[testPlaceX + testPlaceY];
            }
        }
        if (firstStore[x + y * width] == 0) {
            if (neighbour == 3) {
                return 1;
            } else {
                return 0;
            }
        } else {
            if (neighbour > 3 || neighbour < 2) {
                return 0;
            } else {
                return 1;
            }
        }
    }
}