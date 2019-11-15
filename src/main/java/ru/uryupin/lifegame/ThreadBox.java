package ru.uryupin.lifegame;

import java.util.stream.IntStream;

class ThreadBox implements Runnable {

    private final Area area;
    private final int from;
    private final int offset;

    ThreadBox(Area area, int from, int offset) {
        this.area = area;
        this.from = from;
        this.offset = offset;
    }

    @Override
    public void run() {
        int width = area.getWidth();
        byte[] lastStore = area.getLastStore();
        IntStream.range(from, from + offset).
                forEach(i -> IntStream.range(0, width).
                        forEach(j -> lastStore[j + i * width] = area.getFortune(j, i)));
    }
}
