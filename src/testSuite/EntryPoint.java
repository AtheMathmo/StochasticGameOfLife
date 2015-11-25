package testSuite;

import java.util.Arrays;

import engine.GameEngine;

public class EntryPoint {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GameEngine gameEngine = new GameEngine(new int[] {10,10});
		boolean[][] cells = new boolean[10][10];
		boolean[] row = new boolean[10];
		Arrays.fill(row, true);
		Arrays.fill(cells,row);
		gameEngine.setCellGrid(cells);
		gameEngine.updateGrid();
	}

}
