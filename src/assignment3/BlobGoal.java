package assignment3;

import java.awt.Color;

public class BlobGoal extends Goal{

    public BlobGoal(Color c) {
        super(c);
    }

    @Override
    public int score(Block board) {
        // flatten the tree representation of the board into a 2D array of Colors
        Color[][] unitCells = board.flatten();
        // initialize the visited array
        boolean[][] visited = new boolean[unitCells.length][unitCells[0].length];
        // initialize the maximum blob size to 0
        int maxBlobSize = 0;
        // iterate through the 2D array of unit cells
        for (int i = 0; i < unitCells.length; i++) {
            for (int j = 0; j < unitCells[0].length; j++) {
                // find out the size of the blob this cell is part of
                int blobSize = undiscoveredBlobSize(i, j, unitCells, visited);
                // if this cell is part of a blob of the target color and the blob is bigger than the current maximum,
                // update the maximum blob size
                if (blobSize > 0 && blobSize > maxBlobSize) {
                    maxBlobSize = blobSize;
                }
            }
        }
        // return the maximum blob size
        return maxBlobSize;
    }

    @Override
    public String description() {
        return "Create the largest connected blob of " + GameColors.colorToString(targetGoal)
                + " blocks, anywhere within the block";
    }


    public int undiscoveredBlobSize(int i, int j, Color[][] unitCells, boolean[][] visited) {

        if (i < 0 || i >= unitCells.length || j < 0 || j >= unitCells[0].length) {
            // out of bounds, so no blob here
            return 0;
        }
        if (visited[i][j]) {
            // already visited, so no need to explore further
            return 0;
        }
        if (!unitCells[i][j].equals(targetGoal)) {
            // not the target color, so no blob here
            return 0;
        }
        // mark as visited
        visited[i][j] = true;
        // count this cell as part of the blob
        int blobSize = 1;
        // recursively explore neighboring cells
        blobSize += undiscoveredBlobSize(i+1, j, unitCells, visited);
        blobSize += undiscoveredBlobSize(i-1, j, unitCells, visited);
        blobSize += undiscoveredBlobSize(i, j+1, unitCells, visited);
        blobSize += undiscoveredBlobSize(i, j-1, unitCells, visited);
        // return the total blob size
        return blobSize;

    }

}

