package assignment3;

import java.util.Random;

public class Main {
    public static void main(String[] args)  {
        Block blockDepth3 = new Block(0,4);
        blockDepth3.updateSizeAndPosition(16, 0, 0);
        blockDepth3.printColoredBlock();
    }
}