package assignment3;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

public class Block {
    private int xCoord;
    private int yCoord;
    private int size; // height/width of the square
    private int level; // the root (outer most block) is at level 0
    private int maxDepth;
    private Color color;

    private Block[] children; // {UR, UL, LL, LR}


    public static Random gen = new Random(123);


    /*
     * These two constructors are here for testing purposes.
     */
    public Block() {
    }

    public Block(int x, int y, int size, int lvl, int maxD, Color c, Block[] subBlocks) {
        this.xCoord = x;
        this.yCoord = y;
        this.size = size;
        this.level = lvl;
        this.maxDepth = maxD;
        this.color = c;
        this.children = subBlocks;
    }


    /*
     * Creates a random block given its level and a max depth.
     *
     * xCoord, yCoord, size, and highlighted should not be initialized
     * (i.e. they will all be initialized by default)
     */
    public Block(int lvl, int maxDepth) {
        this.level = lvl;
        this.maxDepth = maxDepth;
        if (lvl > maxDepth) {
            throw new IllegalArgumentException("not legal");
        }
        if (lvl < maxDepth) {
            double randomNum = gen.nextDouble();
            if (randomNum < Math.exp(-0.25 * lvl)) {
                this.children = new Block[4];
                for (int i = 0; i < 4; i++) {
                    children[i] = new Block(lvl + 1, maxDepth);
                    children[i].level = lvl + 1;
                    children[i].maxDepth = maxDepth;        ///
                }
            } else {
                int colorIndex = gen.nextInt(4);
                this.color = GameColors.BLOCK_COLORS[colorIndex];
                this.children = new Block[0];
            }
        } else {
            int colorIndex = gen.nextInt(4);
            this.color = GameColors.BLOCK_COLORS[colorIndex];
            this.children = new Block[0];
        }
    }


    /*
     * Updates size and position for the block and all of its sub-blocks, while
     * ensuring consistency between the attributes and the relationship of the
     * blocks.
     *
     *  The size is the height and width of the block. (xCoord, yCoord) are the
     *  coordinates of the top left corner of the block.
     */
    public void updateSizeAndPosition(int size, int xCoord, int yCoord) {
        if (size == 0) {
            throw new IllegalArgumentException("Invalid size input");
        }
        if (size < 0) {
            throw new IllegalArgumentException("Invalid size input");
        }

        if (this.level == this.maxDepth && size % 1 != 0) {
            throw new IllegalArgumentException("Invalid size input");
        }

        if (size > 0 && this.level < this.maxDepth && size % 2 != 0) {
            throw new IllegalArgumentException("Invalid size input");
        }

        // Update this block's size and position
        this.size = size;
        this.xCoord = xCoord;
        this.yCoord = yCoord;

        // If this block has children, update their sizes and positions recursively
        if (this.children != null && this.children.length != 0) {
            int childSize = this.size / 2;
            this.children[0].updateSizeAndPosition(childSize, xCoord + childSize, yCoord);
            this.children[1].updateSizeAndPosition(childSize, xCoord, yCoord);
            this.children[2].updateSizeAndPosition(childSize, xCoord, yCoord + childSize);
            this.children[3].updateSizeAndPosition(childSize, xCoord + childSize, yCoord + childSize);
            if ((size % 1 != 0) || size < 0) {
                throw new IllegalArgumentException("Invalid size input");
            }
        }
    }

    /*

    private boolean isValidSize(int size, int maxDepth) {
        if (size == 0) {
            return false;
        }
        if (maxDepth == 0) {
            return size % 2 == 0;
        }
        return size % 2 == 0 && isValidSize(size / 2, maxDepth - 1);
    }

     */



    /*
     * Returns a List of blocks to be drawn to get a graphical representation of this block.
     *
     * This includes, for each undivided Block:
     * - one BlockToDraw in the color of the block
     * - another one in the FRAME_COLOR and stroke thickness 3
     *
     * Note that a stroke thickness equal to 0 indicates that the block should be filled with its color.
     *
     * The order in which the blocks to draw appear in the list does NOT matter.
     */
    public ArrayList<BlockToDraw> getBlocksToDraw() {
        ArrayList<BlockToDraw> blocksToDraw = new ArrayList<BlockToDraw>();

        if (this.children == null || this.children.length == 0) {
            blocksToDraw.add(new BlockToDraw(this.color, this.xCoord, this.yCoord, this.size, 0));
            blocksToDraw.add(new BlockToDraw(GameColors.FRAME_COLOR, this.xCoord, this.yCoord, this.size, 3));
            //System.out.println(this.color + " "+ this.xCoord + " " + this.yCoord + " " + this.size);
        } else {
            for (Block subBlock : this.children) {
                blocksToDraw.addAll(subBlock.getBlocksToDraw());
            }
        }

        return blocksToDraw;

    }

    /*
     * This method is provided and you should NOT modify it.
     */
    public BlockToDraw getHighlightedFrame() {
        return new BlockToDraw(GameColors.HIGHLIGHT_COLOR, this.xCoord, this.yCoord, this.size, 5);
    }

    /*
     * Return the Block within this Block that includes the given location
     * and is at the given level. If the level specified is lower than
     * the lowest block at the specified location, then return the block
     * at the location with the closest level value.
     *
     * The location is specified by its (x, y) coordinates. The lvl indicates
     * the level of the desired Block. Note that if a Block includes the location
     * (x, y), and that Block is subdivided, then one of its sub-Blocks will
     * contain the location (x, y) too. This is why we need lvl to identify
     * which Block should be returned.
     *
     * Input validation:
     * - this.level <= lvl <= maxDepth (if not throw exception)
     * - if (x,y) is not within this Block, return null.
     */
    public Block getSelectedBlock(int x, int y, int lvl) {

        if (lvl < this.level || lvl > maxDepth) {
            throw new IllegalArgumentException("Invalid");
        }

        // Check if the given position is within this block
        if (x < xCoord || x > xCoord + size || y < yCoord || y > yCoord + size) {
            return null;
        }

        // If this block is at the desired level, return it
        if (this.level == lvl) {
            return this;
        }

        // If this block is not subdivided, return it (closest block at the desired level)
        if (this.children == null || children.length == 0) {
            return this;
        }

        // Recursively search for the selected block in the children
        int childSize = size / 2;
        Block selectedBlock = null;
        int childIndex = -1;
        if (x >= xCoord + childSize) { // right half
            if (y >= yCoord + childSize) { // lower-right child
                childIndex = 3;
            } else { // upper-right child
                childIndex = 0;
            }
        } else { // left half
            if (y >= yCoord + childSize) { // lower-left child
                childIndex = 2;
            } else { // upper-left child
                childIndex = 1;
            }
        }
        if (childIndex != -1) {
            selectedBlock = children[childIndex].getSelectedBlock(x, y, lvl);
        }

        // If the selected block was not found in the children, return this block (closest block at the desired level)
        if (selectedBlock == null) {
            selectedBlock = this;
        }

        return selectedBlock;
    }


    /*
     * Swaps the child Blocks of this Block.
     * If input is 1, swap vertically. If 0, swap horizontally.
     * If this Block has no children, do nothing. The swap
     * should be propagate, effectively implementing a reflection
     * over the x-axis or over the y-axis.
     *
     */

    public void reflect(int direction) {

        if (direction != 0 && direction != 1) {
            throw new IllegalArgumentException("Invalid axis: " + direction);
        }
        if (children.length == 0) {
            // If this block has no children, it cannot be reflected
            return;
        }
        // Reflect the children array and update their positions
        Block[] newChildren = new Block[4];
        if (direction == 0) { // Reflect over x-axis
            newChildren[0] = children[3]; // 0  2
            newChildren[1] = children[2]; // 1  3
            newChildren[2] = children[1]; // 2  0
            newChildren[3] = children[0]; // 3  1
            for (Block child : newChildren) {
                child.reflect(direction);
                child.updateSizeAndPosition(child.size, child.xCoord, 2 * yCoord + size - child.yCoord - child.size);
            }
        } else { // Reflect over y-axis
            newChildren[0] = children[1];
            newChildren[1] = children[0];
            newChildren[2] = children[3];
            newChildren[3] = children[2];
            for (Block child : newChildren) {
                child.reflect(direction);
                child.updateSizeAndPosition(child.size, 2 * xCoord + size - child.xCoord - child.size, child.yCoord);
            }
        }
        children = newChildren;
    }

    /*
     * Rotate this Block and all its descendants.
     * If the input is 1, rotate clockwise. If 0, rotate
     * counterclockwise. If this Block has no children, do nothing.
     */
    public void rotate(int direction) {
        if (direction != 0 && direction != 1) {
            throw new IllegalArgumentException("Invalid direction.");
        }

        if (children.length == 0) {
            return; // nothing to rotate
        }

        // swap children in the correct order
        Block tmp;
        if (direction == 1) { // rotate counter clockwise
            tmp = children[0];
            children[0] = children[1];
            children[1] = children[2];
            children[2] = children[3];
            children[3] = tmp;
        } else { // rotate clockwise
            tmp = children[3];
            children[3] = children[2];
            children[2] = children[1];
            children[1] = children[0];
            children[0] = tmp;
        }

        // update the x and y coordinates of the children
        int childSize = size / 2;
        children[0].updateSizeAndPosition(childSize, xCoord + childSize, yCoord);
        children[1].updateSizeAndPosition(childSize, xCoord, yCoord);
        children[2].updateSizeAndPosition(childSize, xCoord, yCoord + childSize);
        children[3].updateSizeAndPosition(childSize, xCoord + childSize, yCoord + childSize);

        // propagate the rotation to the sub-blocks
        for (Block child : children) {
            child.rotate(direction);
        }
    }



    /*
     * Smash this Block.
     *
     * If this Block can be smashed,
     * randomly generate four new children Blocks for it.
     * (If it already had children Blocks, discard them.)
     * Ensure that the invariants of the Blocks remain satisfied.
     *
     * A Block can be smashed iff it is not the top-level Block
     * and it is not already at the level of the maximum depth.
     *
     * Return True if this Block was smashed and False otherwise.
     *
     */

    public boolean smash() {
        if (level == 0 || level == maxDepth) {
            return false;
        }
        children = new Block[4];
        for (int i = 0; i < 4; i++) {
            children[i] = new Block(level + 1, maxDepth);
            children[i].updateSizeAndPosition(size / 2, xCoord + (i % 2) * size / 2, yCoord + (i / 2) * size / 2);
        }
        return true;
    }


    /*
     * Return a two-dimensional array representing this Block as rows and columns of unit cells.
     *
     * Return and array arr where, arr[i] represents the unit cells in row i,
     * arr[i][j] is the color of unit cell in row i and column j.
     *
     * arr[0][0] is the color of the unit cell in the upper left corner of this Block.
     */


    public Color[][] flatten() {
        Color[][] result;
        if (children.length == 0) {
            // base case: no children
            if (level != maxDepth) {
                int size = (int) Math.pow(2, maxDepth - level);
                result = new Color[size][size];
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        result[i][j] = color;
                    }
                }
            } else {
                result = new Color[1][1];
                result[0][0] = color;
            }
        } else {
            // recursive case: has children
            int childSize = children[0].flatten().length;
            result = new Color[childSize * 2][childSize * 2];
            Color[][] ur = children[0].flatten();
            Color[][] ul = children[1].flatten();
            Color[][] ll = children[2].flatten();
            Color[][] lr = children[3].flatten();
            for (int i = 0; i < childSize; i++) {
                for (int j = 0; j < childSize; j++) {
                    result[i][j] = ul[i][j];
                    result[i][j + childSize] = ur[i][j];
                    result[i + childSize][j] = ll[i][j];
                    result[i + childSize][j + childSize] = lr[i][j];
                }
            }
        }
        return result;

    }







    // These two get methods have been provided. Do NOT modify them.
    public int getMaxDepth() {
        return this.maxDepth;
    }

    public int getLevel() {
        return this.level;
    }


    /*
     * The next 5 methods are needed to get a text representation of a block.
     * You can use them for debugging. You can modify these methods if you wish.
     */
    public String toString() {
        return String.format("pos=(%d,%d), size=%d, level=%d"
                , this.xCoord, this.yCoord, this.size, this.level);
    }

    public void printBlock() {
        this.printBlockIndented(0);
    }

    private void printBlockIndented(int indentation) {
        String indent = "";
        for (int i=0; i<indentation; i++) {
            indent += "\t";
        }

        if (this.children.length == 0) {
            // it's a leaf. Print the color!
            String colorInfo = GameColors.colorToString(this.color) + ", ";
            System.out.println(indent + colorInfo + this);
        } else {
            System.out.println(indent + this);
            for (Block b : this.children)
                b.printBlockIndented(indentation + 1);
        }
    }

    private static void coloredPrint(String message, Color color) {
        System.out.print(GameColors.colorToANSIColor(color));
        System.out.print(message);
        System.out.print(GameColors.colorToANSIColor(Color.WHITE));
    }

    public void printColoredBlock(){
        Color[][] colorArray = this.flatten();
        for (Color[] colors : colorArray) {
            for (Color value : colors) {
                String colorName = GameColors.colorToString(value).toUpperCase();
                if(colorName.length() == 0){
                    colorName = "\u2588";
                }else{
                    colorName = colorName.substring(0, 1);
                }
                coloredPrint(colorName, value);
            }
            System.out.println();
        }
    }

}
