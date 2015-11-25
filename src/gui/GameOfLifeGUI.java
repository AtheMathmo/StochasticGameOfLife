package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import engine.GameEngine;

public class GameOfLifeGUI extends JApplet {
	/**
	 *  Applet to hold core components.
	 */
	private static final long serialVersionUID = 1L;
	
	final static double version = 0.2;
	
	public void init() {
		
		try {
    		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	} catch(Exception e) {
    		e.printStackTrace();
    	}

		
		System.out.println("Applet Initialising");
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
		});
	}
	
	public void start() {
		System.out.println("Applet starting");
		}
		public void stop() {
		System.out.println("Applet stopping");
		}
		public void destroy() {
		System.out.println("Applet destroyed");
		}
    
    private void createAndShowGUI() {
        System.out.println("Created GUI on EDT? "+
                SwingUtilities.isEventDispatchThread());
        
        // Build components with their compositional classes.
        TopInfoPanel topInfoPanel = new TopInfoPanel();
        GameGrid gameGrid = new GameGrid(topInfoPanel);        
        UIPanel settingsPanel = new UIPanel(gameGrid);
        
        // Add components to applet pane.
        getContentPane().add(gameGrid, BorderLayout.CENTER);
        getContentPane().add(settingsPanel, BorderLayout.EAST);
        getContentPane().add(topInfoPanel, BorderLayout.NORTH);      

    }

}

class GameGrid extends JPanel {
	/**
	 * Grid to hold cells for game of life. Also provides methods to interact with top panel and game engine.
	 */
	private static final long serialVersionUID = 1L;
	
	GridLayout c;
	GameEngine gameEngine;
	TopInfoPanel topInfoPanel;
	
	private Timer timer;
	private boolean mouseHeld = false;
	private boolean running = false;
	
	List<GridCell[]> cellList = new ArrayList<GridCell[]>();
	
	int[] gridDimensions = new int[] {80,60};
	
	public boolean isMouseHeld() {
		return mouseHeld;
	}
	
	public void setMouseHeld(boolean mouseHeld) {
		this.mouseHeld = mouseHeld;
	}

	public boolean isRunning() {
		// records when timer is running
		return running;
	}

	public GameGrid(TopInfoPanel topInfoPanel) {
		this.topInfoPanel = topInfoPanel;
		// Initialise gameEngine.
		gameEngine = new GameEngine(gridDimensions);
		// Set border and layout.
		setBorder(BorderFactory.createLineBorder(Color.black));
		c = new GridLayout(0,gridDimensions[1]);
		c.setHgap(0);
		c.setVgap(0);;
		setLayout(c);
		
		// Initialise timer to update the game.
		timer = new Timer(100,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				List<int[]> updatedIndices = gameEngine.updateGrid();
				updateCells(updatedIndices);
				
				int newState = gameEngine.updateState();
				topInfoPanel.updateMutation(newState);
			}
		});
		
		initializeComponents();
	}
	
	private void initializeComponents() {
		// Set the grid
		this.removeAll();
		this.revalidate();
		
		/*
		 * invalid code - if we want option to change size of grid later this would be framework.
		 * 
		gameEngine.setGridDimensions(gridDimensions);
		c.setColumns(gridDimensions[1]);
		setLayout(c);
		validate();
		*/
		
		// Clear the cell list and re-populate with new cells (cleared for when we reset).
		cellList.clear();
		for (int i = 0; i < gridDimensions[0]; i++) {
			GridCell[] cellArray = new GridCell[gridDimensions[1]];
			for (int j =0; j < gridDimensions[1]; j++) {
				cellArray[j] = new GridCell(this);
			}
			cellList.add(cellArray);
		}
		
		// Add the cells in the appropriate order.
		for (int i = 0; i < gridDimensions[0]; i++) {
			for (int j = 0; j < gridDimensions[1]; j++) {
				this.add(cellList.get(i)[j]);
			}
		}
		setEngineGrid();
	}
	
	private void setEngineGrid() {
		// Make engineGrid match gameGrid.
		for (int i = 0; i < gridDimensions[0]; i++) {
			for (int j = 0; j < gridDimensions[1]; j++) {
				gameEngine.setGridElement(new int[] {i,j}, cellList.get(i)[j].isValue());
			}
		}
	}
	
	private void updateCells(List<int[]> updatedIndices) {
		// Change value of cells affected by algorithm.
		for (int[] index : updatedIndices) {
			cellList.get(index[0])[index[1]].flipCell();
		}
	}
	public void takeSingleStep() {
		// Provides just one iteration of the model.
		
		// Check engineGrid matches gameGrid.
		setEngineGrid();
		
		List<int[]> updatedIndices = gameEngine.updateGrid();
		updateCells(updatedIndices);
		
		int newState = gameEngine.updateState();		
		topInfoPanel.updateMutation(newState);
		
	}
	
	public void startRunningGame() {
		// Check grid, and start timer.
		running = true;
		setEngineGrid();
		timer.start();
	}
	
	public void stopRunningGame() {
		// Check grid, and stop timer.
		running = false;
		setEngineGrid();
		timer.stop();
	}
	
	public void resetGrid(int[] newDimensions) {
		//Reset grid to new dimensions (currently defaulted to 100,100)
		gridDimensions = newDimensions;
		initializeComponents();
	}
	
	public void setReviveRate(double reviveRate) {
		// update revival rate in engine and top display.
		gameEngine.setReviveRate(reviveRate);
		topInfoPanel.updateRevivalState((reviveRate > 0) ? "On":"Off");
	}
	
	public void setMutationStayTime(int mutationStayTime) {
		// update mutation rate in engine
		gameEngine.setMutationStayTime(mutationStayTime);
	}
	
	public void setGameState(int newState) {
		// manually force the gameEngine's state.
		gameEngine.setCurrentState(newState);
		topInfoPanel.updateMutation(newState+2);
	}
}

class UIPanel extends JPanel implements ActionListener {
	/**
	 * Panel for the user to interact will. Allows control over the running of the game.
	 */
	private static final long serialVersionUID = 1L;
	JButton stepButton;
	JButton startButton;	
	JButton stopButton;
	JButton resetGridButton;
	
	JSlider reviveRateSlider;
	JLabel reviveRateLabel;
	JSlider mutationRateSlider;
	JLabel mutationRateLabel;
	
	GameGrid gameGrid;
	
	public UIPanel(GameGrid gameGrid) {
		this.gameGrid = gameGrid;
		
		setBorder(BorderFactory.createLineBorder(Color.black));
		setLayout(new GridLayout(0,1,10,50));
		
		initializeComponents();
	}
	
	private void initializeComponents() {
		stepButton = new JButton("Step Forward");
		stepButton.setActionCommand("step");
		stepButton.addActionListener(this);
		
		startButton = new JButton("Start");
		startButton.setActionCommand("start");
		startButton.addActionListener(this);
		
		stopButton = new JButton("Stop");
		stopButton.setActionCommand("stop");
		stopButton.addActionListener(this);
		
		resetGridButton = new JButton("Reset");
		resetGridButton.setActionCommand("reset");
		resetGridButton.addActionListener(this);
		
		// Create panel to hold revive rate slider and label
		JPanel reviveRateSliderHolder = new JPanel(new GridLayout(2,1));
		reviveRateSlider = new JSlider(JSlider.HORIZONTAL,0,10,0);
		reviveRateLabel = new JLabel("Revive Rate: "+0,JSlider.CENTER);
		reviveRateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		reviveRateSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				
				double reviveRate = 1.0*source.getValue()/100;
				reviveRateLabel.setText("Revive Rate: " + reviveRate);
				gameGrid.setReviveRate(reviveRate);
				
			}
		});
		
		JPanel mutationRateSliderHolder = new JPanel(new GridLayout(2,1));
		mutationRateSlider = new JSlider(JSlider.HORIZONTAL,0,100,0);
		mutationRateLabel = new JLabel("Avg turns per mutation: "+0,JSlider.CENTER);
		mutationRateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		mutationRateSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				
				// Weird calculation is as this value represents expected steps before next mutation (but we want sensible slider direction).
				int mutationStayTime = (source.getValue() == 0 ? 0:(101-source.getValue()));
				mutationRateLabel.setText("Avg turns per mutation: "+mutationStayTime);
				gameGrid.setMutationStayTime(mutationStayTime);
				
			}
		});
		
		add(stepButton);
		add(startButton);
		add(stopButton);
		add(resetGridButton);
		
		reviveRateSliderHolder.add(reviveRateLabel);
		reviveRateSliderHolder.add(reviveRateSlider);		
		add(reviveRateSliderHolder);
		
		mutationRateSliderHolder.add(mutationRateLabel);
		mutationRateSliderHolder.add(mutationRateSlider);		
		add(mutationRateSliderHolder);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("step".equals(e.getActionCommand())) {
			gameGrid.takeSingleStep();
		}
		if ("start".equals(e.getActionCommand())) {
			gameGrid.startRunningGame();
		}
		if ("stop".equals(e.getActionCommand())) {
			gameGrid.stopRunningGame();
		}
		if ("reset".equals(e.getActionCommand())) {
			gameGrid.stopRunningGame();
			gameGrid.setGameState(1);
			gameGrid.resetGrid(new int[] {80,60});
		}
		
		
	}
}

class TopInfoPanel extends JPanel {
	/**
	 * Displays information on the status of the gameEngine and gameGrid
	 */
	private static final long serialVersionUID = 1L;
	final String signature = "<html>Stochastic Game of Life v"+GameOfLifeGUI.version+"<br>Author: James Lucas</html>";
	private String mutation = "B3/S23";
	
	private JLabel signatureLabel;
	private JLabel revivalLabel;
	private JLabel currentMutationLabel;
	
	public TopInfoPanel() {
		setBorder(BorderFactory.createLineBorder(Color.black));
		setLayout(new GridLayout(0,3,20,20));
		
		initializeComponents();
	}
	
	
	private void initializeComponents() {
		signatureLabel = new JLabel(signature);
		currentMutationLabel = new JLabel("Current Mutation: "+mutation);
		revivalLabel = new JLabel("Random Revival: Off");
		
		add(signatureLabel);
		add(currentMutationLabel);
		add(revivalLabel);
	}
	
	public void updateMutation(int mutationState) {
		// Update mutation state in display.
		mutation = "B"+mutationState+"/S"+(mutationState-1)+mutationState;
		currentMutationLabel.setText("Current Mutation: "+mutation);
	}
	
	public void updateRevivalState(String state) {
		// Update revival state in display.
		revivalLabel.setText("Random Revival: "+state);
	}
}
