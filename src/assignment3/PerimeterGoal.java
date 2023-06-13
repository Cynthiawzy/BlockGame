package assignment3;

import java.awt.Color;

public class PerimeterGoal extends Goal{

    public PerimeterGoal(Color c) {
        super(c);
    }

    @Override
    public int score(Block board) {
        Color[][] flattenedBoard = board.flatten();
        int score = 0;
        int numRows = flattenedBoard.length;
        int numCols = flattenedBoard[0].length;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (i == 0 || i == numRows - 1 || j == 0 || j == numCols - 1) {
                    // Cell is on the perimeter
                    if (flattenedBoard[i][j] == targetGoal) {
                        // Check if cell is in a corner
                        if ((i == 0 || i == numRows - 1) && (j == 0 || j == numCols - 1)) {
                            score += 2;
                        } else {
                            score += 1;
                        }
                    }
                }
            }
        }

        return score;
    }

    @Override
    public String description() {
        return "Place the highest number of " + GameColors.colorToString(targetGoal)
                + " unit cells along the outer perimeter of the board. Corner cell count twice toward the final score!";
    }

}
