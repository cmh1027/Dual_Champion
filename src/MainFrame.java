
import javax.swing.JFrame;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
public class MainFrame extends JFrame{
	private FieldPanel fieldPanel;
	private JTable deckTable; 
	private DefaultTableModel deckTableModel;
	private JLabel displayBoard;
	private JLabel scoreBoard;
	private JTextArea cardExplainArea;
	private JButton startAIButton;
	private JButton startPlayerButton;
	private JButton quitButton;
	private Game game;
	public static class FrameMouseAdapter extends MouseAdapter{
		private JTable deckTable; 
		public FrameMouseAdapter(JTable deckTable) {
			this.deckTable = deckTable;
		}
    	@Override
        public void mouseClicked(MouseEvent e) {
    		this.deckTable.clearSelection(); // deselect a card in the deck when the frame is clicked
        }
	}
	public static class DeckTableSelectionListener implements ListSelectionListener {
		private MainFrame parent;
		private JTable self; 
		DeckTableSelectionListener(MainFrame parent){
			this.parent = parent;
			this.self = parent.deckTable;
		}
	    @Override
	    public void valueChanged(ListSelectionEvent event) {
	    	this.parent.fieldPanel.unselectCurrentCell();
	    	int selectedIndex = self.getSelectedRow();
	    	if(selectedIndex != -1) {
		    	if(parent.game.isPlayerTurn()) {
		    		parent.printCardInfo(parent.game.myDeck.at(selectedIndex).getCardInfo());
		    	}
		    	else {
		    		
		    	}	    		
	    	}
	    	else {
	    		parent.printCardInfo("");
	    	}
	    }
	}
	public static class StartButtonListener implements ActionListener{
		private MainFrame parent;
		boolean withPlayer;
		public StartButtonListener(MainFrame parent, boolean withPlayer) {
			this.parent = parent;
			this.withPlayer = withPlayer;
		}
    	@Override
    	public void actionPerformed(ActionEvent e) {
    		Game game = new Game(parent, parent.fieldPanel, this.withPlayer);
    		parent.game = game;
    		parent.fieldPanel.game = game;
    		game.start();
    		parent.startAIButton.setEnabled(false);
    		parent.startPlayerButton.setEnabled(false);
    	}
	}
	public MainFrame() {
		super("Dual Champion");
		this.setSize(800,700);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);
		////////////////////////////////////////////////////////
        fieldPanel = new FieldPanel(this);
        fieldPanel.setSize(500, 500);
        fieldPanel.setLocation(50, 100);
        this.add(fieldPanel);
        fieldPanel.defaultUpdate();
        ////////////////////////////////////////////////////////
        String header[]={"Card", "HP", "ATK"};
        String contents[][]={};
        deckTableModel = new DefaultTableModel(contents, header) {
        	public boolean isCellEditable(int rowIndex, int mColIndex) {
        		return false;
        	}
        };
        deckTable = new JTable(deckTableModel);
        JScrollPane jscp1 = new JScrollPane(deckTable);
        deckTable.setRowHeight(30);
        deckTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jscp1.setLocation(580, 150);
        jscp1.setSize(190, 400);
        DefaultTableCellRenderer tScheduleCellRenderer = new DefaultTableCellRenderer();
        tScheduleCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        TableColumnModel tcmSchedule = deckTable.getColumnModel();
        for (int i = 0; i < tcmSchedule.getColumnCount(); i++) {
        	tcmSchedule.getColumn(i).setCellRenderer(tScheduleCellRenderer);
        }
        this.add(jscp1);
        this.addMouseListener(new FrameMouseAdapter(this.deckTable));
        this.deckTable.getSelectionModel().addListSelectionListener(new DeckTableSelectionListener(this));
        ////////////////////////////////////////////////////////
        cardExplainArea = new JTextArea();
        cardExplainArea.setSize(190, 70);
        cardExplainArea.setLocation(580, 560);
        cardExplainArea.setBackground(Color.white);
        cardExplainArea.setFont(new Font(cardExplainArea.getFont().getName(), Font.PLAIN, 11));
        cardExplainArea.setOpaque(true);
        cardExplainArea.setEnabled(false);
        cardExplainArea.setDisabledTextColor(Color.black);
        cardExplainArea.setLineWrap(true);
        cardExplainArea.setWrapStyleWord(true);
        this.add(cardExplainArea);        
        ////////////////////////////////////////////////////////
        displayBoard = new JLabel();
        displayBoard.setSize(400, 30);
        displayBoard.setLocation(50, 40);
        displayBoard.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        displayBoard.setBackground(Color.white);
        displayBoard.setFont(new Font(displayBoard.getFont().getName(), Font.BOLD, 20));
        displayBoard.setHorizontalAlignment(SwingConstants.CENTER);
        displayBoard.setOpaque(true);
        this.add(displayBoard);
        ////////////////////////////////////////////////////////
        scoreBoard = new JLabel("0 : 0");
        scoreBoard.setSize(60, 30);
        scoreBoard.setLocation(480, 40);
        scoreBoard.setFont(new Font(scoreBoard.getFont().getName(), Font.BOLD, 30));
        scoreBoard.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(scoreBoard);
        ////////////////////////////////////////////////////////
        startAIButton = new JButton("vs Computer");
        startAIButton.setSize(130, 30);
        startAIButton.setLocation(600, 20);
        startAIButton.setFont(new Font(startAIButton.getFont().getName(), Font.BOLD, 15));
        startAIButton.addActionListener(new StartButtonListener(this, false));
        this.add(startAIButton);
        ////////////////////////////////////////////////////////
        startPlayerButton = new JButton("vs Player");
        startPlayerButton.setSize(130, 30);
        startPlayerButton.setLocation(600, 60);
        startPlayerButton.setFont(new Font(startPlayerButton.getFont().getName(), Font.BOLD, 15));
        startPlayerButton.addActionListener(new StartButtonListener(this, true));
        this.add(startPlayerButton);
        ////////////////////////////////////////////////////////
        quitButton = new JButton("Quit");
        quitButton.setSize(130, 30);
        quitButton.setLocation(600, 100);
        quitButton.setFont(new Font(quitButton.getFont().getName(), Font.BOLD, 15));
        quitButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		System.exit(0);
        	}
        });
        this.add(quitButton);
        ////////////////////////////////////////////////////////
        this.setVisible(true);
     
	}
	public void updateInit() {
		// initialize Field GUI according to game variables
		initDeckTable(true);
		fieldPanel.init(game.round);
	}

	public void initDeckTable(boolean myTurn) {
		deckTableModel.setRowCount(0);
		if(myTurn) {
			for(int i=0; i<game.myDeck.size(); ++i) {
				Card card = game.myDeck.at(i);
				deckTableModel.addRow(new Object[]{card.getName(), card.getHp(), card.getAtk()});
			}			
		}
		else {
			for(int i=0; i<game.enemyDeck.size(); ++i) {
				Card card = game.enemyDeck.at(i);
				deckTableModel.addRow(new Object[]{card.getName(), card.getHp(), card.getAtk()});
			}			
		}

	}
	
	public void updateDisplay(String str) {
		this.displayBoard.setText(str);
	}
	
	public void updateDeckTable(int removedRowIndex) {
		deckTableModel.removeRow(removedRowIndex);
	}
	
	public int getSelectedCard() {
		if(this.deckTable.getSelectionModel().isSelectionEmpty())
			return -1;
		else
			return this.deckTable.getSelectedRow();
	}
	public void gameEnded() {
		this.startAIButton.setEnabled(true);
		this.startPlayerButton.setEnabled(true);
	}
	public void printCardInfo(String str) {
		this.cardExplainArea.setText(str);
	}
	
	public void setScore(int myScore, int enemyScore) {
		this.scoreBoard.setText(String.format("%d : %d", myScore, enemyScore));
	}
}
