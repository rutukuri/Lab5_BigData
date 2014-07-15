package com.jforeach.mazeman;

import java.io.Serializable;

import android.content.Context;

public class Maze implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int UP = 0, DOWN = 1, RIGHT = 2, LEFT = 3;

	private boolean[][] verticalLines;
	private boolean[][] horizontalLines;
	private int sizeX, sizeY; // stores the width and height of the maze
	private int currentX, currentY; // stores the current location of the ball
	private int finalX, finalY; // stores the ending of the maze
	private boolean gameComplete;
	

	public int getMazeWidth() {
		return sizeX;
	}

	public int getMazeHeight() {
		return sizeY;
	}

	public boolean move(int direction) {
		boolean moved = false;
		if (direction == UP) {
			System.out.println("The values Direction = " + direction+" and in Up"+" CurrentX="+currentX+" CurrentY="+currentY);
			if (currentY != 0 && !horizontalLines[currentY - 1][currentX]) {
				currentY--;
				moved = true;
				System.out.println("Let's see now go up");
			}
			else{
				System.out.println("The Current values in X="+currentX+" and Y="+currentY);
				currentX=0;
				currentY=1;
				moved=true;
			}
		}
		if (direction == DOWN) {
			System.out.println("The values Direction = " + direction+" and in DOWN");
			if (currentY != sizeY - 1 && !horizontalLines[currentY][currentX]) {
				currentY++;
				moved = true;
				System.out.println("Let's goi down");
			}
		}
		if (direction == RIGHT) {
			System.out.println("The values Direction = " + direction+" and in Roght");
			if (currentX != sizeX - 1 && !verticalLines[currentY][currentX]) {
				currentX++;
				moved = true;
				System.out.println("Let's see now we are on the right side");
			}
			else{
				System.out.println("The Current values in X="+currentX+" and Y="+currentY);
				currentX=1;
				currentY=0;;
				moved=true;
			}
		}
		if (direction == LEFT) {
			System.out.println("The vvalues of Direction are" + direction
					+ " , Lft= " + LEFT);
			if (currentX != 0 && !verticalLines[currentY][currentX - 1]) {
				currentX--;
				moved = true;
				System.out.println("Let's see now we are in Left to right");
			}
			else{
				System.out.println("The Current values in X="+currentX+" and Y="+currentY);
			}
		}
		if (moved) {
			if (currentX == finalX && currentY == finalY) {
				gameComplete = true;
			}
		}
		if(moved) {
			//the ball was moved so we'll redraw the view
		//	invalidate();
		}
		return moved;
	}

	public boolean isGameComplete() {
		return gameComplete;
	}

	public void setStartPosition(int x, int y) {
		currentX = x;
		currentY = y;
	}

	public int getFinalX() {
		return finalX;
	}

	public int getFinalY() {
		return finalY;
	}

	public void setFinalPosition(int x, int y) {
		finalX = x;
		finalY = y;
	}

	public int getCurrentX() {
		return currentX;
	}

	public int getCurrentY() {
		return currentY;
	}

	public boolean[][] getHorizontalLines() {
		return horizontalLines;
	}

	public void setHorizontalLines(boolean[][] lines) {
		horizontalLines = lines;
		sizeX = horizontalLines[0].length;
	}

	public boolean[][] getVerticalLines() {
		return verticalLines;
	}

	public void setVerticalLines(boolean[][] lines) {
		verticalLines = lines;
		sizeY = verticalLines.length;
	}
}
