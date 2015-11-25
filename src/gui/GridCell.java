package gui;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class GridCell extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Refers to life or death of cell.
	private boolean value = false;
	
	GameGrid gameGrid;
	
	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}
	
	// Create cell with value defaulted to false.
	public GridCell(GameGrid gameGrid) {
		this.gameGrid = gameGrid;
		
		// Set border and colour of grid.
		setBorder(BorderFactory.createLineBorder(Color.black));
		changeColour();
		
		// Add mouse events to handle colour changing.
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!gameGrid.isRunning()) {
					value = !e.isShiftDown();
					changeColour();
					gameGrid.setMouseHeld(true);
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (!gameGrid.isRunning()) {
					gameGrid.setMouseHeld(false);
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				if (gameGrid.isMouseHeld() && !gameGrid.isRunning()) {
					value = !e.isShiftDown();
					changeColour();
				}
			}
		});
	}
	
	// Allows default value for cell.
	public GridCell(GameGrid gameGrid, boolean startVal) {
		this.gameGrid = gameGrid;
		this.setValue(startVal);
		setBorder(BorderFactory.createLineBorder(Color.black));
		changeColour();
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!gameGrid.isRunning()) {
					value = !e.isShiftDown();
					changeColour();
					gameGrid.setMouseHeld(true);
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (!gameGrid.isRunning()) {
					gameGrid.setMouseHeld(false);
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				if (gameGrid.isMouseHeld() && !gameGrid.isRunning()) {
					value = !e.isShiftDown();
					changeColour();
				}
			}
		});
	}

	private void changeColour() {
		// Colour cell according to current value: blue = alive, red = dead.
		if (value) {
			setBackground(Color.BLUE);
		} else {
			setBackground(Color.RED);
		}
	}
	
	public void flipCell() {
		// Change the state and colour of the cell.
		value = !value;
		changeColour();
	}

}
