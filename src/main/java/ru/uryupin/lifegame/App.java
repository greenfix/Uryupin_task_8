package ru.uryupin.lifegame;

public class App {

    private static final byte NUM_ARGS = 3;

    public static void main(String[] args) {

          if (args.length < NUM_ARGS) {
            System.out.println("Not enough parameters");
            System.exit(-1);
        }

        Area area = new Area();
        area.fileToStore(args[0]).
                calculateMove(Integer.parseInt(args[2]), 5).
                storeToFile(args[1]);
    }
}
