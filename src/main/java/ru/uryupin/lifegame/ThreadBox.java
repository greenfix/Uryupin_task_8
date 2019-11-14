package ru.uryupin.lifegame;

public class ThreadBox implements Runnable {

    private Area area;
    private int from;
    private int offset;

    ThreadBox(Area area, int from, int offset) {
        this.area = area;
        this.from = from;
        this.offset = offset;
    }

    @Override
    public void run() {
        int width = area.getWidth();
        byte[] lastStore = area.getLastStore();
        for (int i = from; i < (from + offset); i++) {
            for (int j = 0; j < width; j++) {
                lastStore[j + i * width] = area.getFortune(j, i);
            }
        }
    }
}
