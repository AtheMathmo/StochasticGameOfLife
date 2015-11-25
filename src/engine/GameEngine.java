package engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Only works for dimension 2, but some messy framework for higher dimensions.
public class GameEngine {
	StatisticsEngine statisticsEngine = new StatisticsEngine();
	
	static int dimension = 2;
	int[] gridDimensions;
	
	boolean[][] cellGrid;
	boolean[][] updatedCellGrid;
	
	int[] stateSpace = new int[] {2,3,4,5,6};
	int currentState = 1;
	
	double reviveRate;
	int mutationStayTime;
	
	public int[] getGridDimensions() {
		return gridDimensions;
	}

	public void setGridDimensions(int[] gridDimensions) {
		this.gridDimensions = gridDimensions;
	}

	public boolean[][] getCellGrid() {
		return cellGrid;
	}

	public void setCellGrid(boolean[][] cellGrid) {
		this.cellGrid = cellGrid;
	}

	public void setGridElement(int[] index, boolean cellValue) {
		// Set individual elements of the grid.
		cellGrid[index[0]][index[1]] = cellValue;
		updatedCellGrid[index[0]][index[1]] = cellValue;
	}
	
	public void setCurrentState(int currentState) {
		this.currentState = currentState;
	}

	public void setReviveRate(double reviveRate) {
		// Also updates the attached statistics engine.
		this.reviveRate = reviveRate;
		statisticsEngine.setReviveProb(reviveRate);
	}
	
	public void setMutationStayTime(int mutationStayTime) {
		// Also updates the attached statistics engine.
		this.mutationStayTime = mutationStayTime;
		statisticsEngine.updateTransitionMatrix(mutationStayTime);
	}

	public GameEngine(int[] gridDimensions) {
		// Populate the grids initial state
		this.gridDimensions = gridDimensions;
		cellGrid = new boolean[gridDimensions[0]][gridDimensions[1]];
		updatedCellGrid = new boolean[gridDimensions[0]][gridDimensions[1]];
	}
	
	
	private int countAdjacentLiveCells(int[] index) {
		int liveCount = 0;

		int[] startPoint = new int[dimension];
		int[] endPoint = new int[dimension];
		// Check if we're at the edge and if so adjust accordingly
		for (int i = 0; i < dimension; i++) {
			startPoint[i] = ((index[i]==0) ? 0:index[i]-1);
			endPoint[i] = ((index[i]==gridDimensions[i]-1) ? gridDimensions[i]-1:index[i]+1);
		}
		// For higher dimensions use a recursive function (not intended for this version)
		for (int i = startPoint[0]; i <= endPoint[0]; i++) {
			for (int k = startPoint[1]; k <= endPoint[1]; k++) {
				if (!Arrays.equals(new int[] {i,k}, index)) {
					liveCount += (cellGrid[i][k] ? 1:0);
				}
			}
		}
		
		return liveCount;
	}
	
	private int getNewState() {
		// Get new mutation state according to statisticsEngine.
		currentState = statisticsEngine.getNewMState(currentState);
		return stateSpace[currentState];
	}
	
	private boolean checkBorn(int[] index) {
		//Check conditions for a cell going live.
		
		int adjCellCount = countAdjacentLiveCells(index);
		
		//i.e. if has 3 cells.
		if (adjCellCount == stateSpace[currentState]) {
			return true;
		}
		return false;
	}
	
	
	private boolean staysLive(int[] index) {
		//Check conditions for a cell dying
		
		int adjCellCount = countAdjacentLiveCells(index);

		// i.e. dies if either >3 or <=1
		if (adjCellCount > stateSpace[currentState] || adjCellCount <= stateSpace[currentState]-2) {
			return false;
		}
		return true;
	}
	
	private List<int[]> iterateOverGrid() {
		List<int[]> changedIndices = new ArrayList<int[]>();
		// Iterate over the grid.
		for (int i = 0; i < gridDimensions[0];i++) {
			for (int k = 0; k < gridDimensions[1]; k++) {
				// Treat live cells and dead cells separately
				if (cellGrid[i][k]) {
					updatedCellGrid[i][k] = staysLive(new int[] {i,k});

					// Record indices that changed
					if (updatedCellGrid[i][k] != cellGrid[i][k]) {
						changedIndices.add(new int[] {i,k});
					}
					
				} else {
					updatedCellGrid[i][k] = checkBorn(new int[] {i,k});
					
					// If still dead, see if statistics engine calls for revival.
					if (!updatedCellGrid[i][k]) {
						updatedCellGrid[i][k] = statisticsEngine.checkReviveCell();
					}
					
					// Record indices that changes
					if (updatedCellGrid[i][k] != cellGrid[i][k])
						changedIndices.add(new int[] {i,k});
				}
			}
		}
		// Copy updatedCellGrid to cellGrid.
		for (int i = 0; i < gridDimensions[0];i++) {
			for (int k = 0; k < gridDimensions[1]; k++) {
				cellGrid[i][k] = updatedCellGrid[i][k];
			}
		}
		return changedIndices;
	}
	
	public List<int[]> updateGrid() {
		// Run game of life for one step.
		return iterateOverGrid();
	}
	
	public int updateState() {
		// Check and return new mutation state.
		return getNewState();
	}
	
}
