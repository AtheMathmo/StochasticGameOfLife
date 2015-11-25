package engine;

import java.util.Random;

public class StatisticsEngine {
	Random randomGenerator = new Random();
	
	private double stayProb = 1.0;
	private double reviveProb = 0.0;
	private final int stateCount = 5;
	
	private double[][] transitionMat;
	
	public double getReviveProb() {
		return reviveProb;
	}

	public void setReviveProb(double reviveProb) {
		this.reviveProb = reviveProb;
	}

	public double[][] getTransitionMat() {
		return transitionMat;
	}

	public void setTransitionMat(double[][] transitionMat) {
		this.transitionMat = transitionMat;
	}

	public StatisticsEngine() {
		buildTransitionMatrix();
	}
	
	private void buildTransitionMatrix() {
		transitionMat = new double[stateCount][stateCount];
		
		// Build transition matrix so same probability of staying everywhere, and symmetric probability of moving.
		for (int i = 0; i < stateCount; i++) {
			transitionMat[i][i] = stayProb;
			if (i == 0) {
				transitionMat[i][i+1] = 1 - stayProb;
			} else if (i == stateCount-1) {
				transitionMat[i][i-1] = 1 - stayProb;
			} else {
				transitionMat[i][i-1] = (1-stayProb)/2;
				transitionMat[i][i+1] = (1-stayProb)/2;
			}
			
		}
	}
	
	private void updateProbByStayTime(int stayTime) {
		// Calculate probability that we stay at same point given stay time. If zero we default to not moving.
		if (stayTime == 0) {
			stayProb = 1.0;
		} else if (stayTime > 0){
			stayProb = 1.0*(stayTime-1)/stayTime;
		}
	}
	
	public void updateTransitionMatrix(int stayTime) {
		// Use new stayTime to update the transition matrix
		updateProbByStayTime(stayTime);
		buildTransitionMatrix();
	}
	
	private int findNewMState(int currentState) {
		// Under markov chain, find the next state.
		double randomU = randomGenerator.nextDouble();
		double rollingCount = 0;
		
		// Find which state we adopt based on random number.
		for (int i = 0; i < stateCount; i++) {
			rollingCount += transitionMat[currentState][i];
			if ( randomU <= rollingCount) {
				return i;
			}
		}
		return (stateCount-1);
	}
	
	public int getNewMState(int currentState) {
		return findNewMState(currentState);
	}
	
	private boolean reviveCell() {
		// Check if a cell is revived.
		double randomU = randomGenerator.nextDouble();
		if (randomU < reviveProb) {
			return true;
		}
		return false;
	}
	
	public boolean checkReviveCell() {
		// Check if a cell is revived.
		return reviveCell();
	}
}
