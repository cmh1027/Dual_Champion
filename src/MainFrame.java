
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
public class MainFrame extends JFrame{
	private FieldPanel fieldPanel;
	private JTable deckTable; 
	private DefaultTableModel deckTableModel;
	private JLabel displayBoard;
	private JButton startButton;
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
        String header[]={"Card", "hp", "atk"};
        String contents[][]={};
        deckTableModel = new DefaultTableModel(contents, header);
        deckTable = new JTable(deckTableModel);
        JScrollPane jscp1 = new JScrollPane(deckTable);
        deckTable.setRowHeight(30);
        deckTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jscp1.setLocation(580, 150);
        jscp1.setSize(190, 450);
        DefaultTableCellRenderer tScheduleCellRenderer = new DefaultTableCellRenderer();
        tScheduleCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        TableColumnModel tcmSchedule = deckTable.getColumnModel();
        for (int i = 0; i < tcmSchedule.getColumnCount(); i++) {
        	tcmSchedule.getColumn(i).setCellRenderer(tScheduleCellRenderer);
        }
        this.add(jscp1);
        this.addMouseListener(new FrameMouseAdapter(this.deckTable)); 
        ////////////////////////////////////////////////////////
        displayBoard = new JLabel();
        displayBoard.setSize(500, 30);
        displayBoard.setLocation(50, 40);
        displayBoard.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        displayBoard.setBackground(Color.white);
        displayBoard.setFont(new Font(displayBoard.getFont().getName(), Font.BOLD, 23));
        displayBoard.setOpaque(true);
        this.add(displayBoard);
        ////////////////////////////////////////////////////////
        startButton = new JButton("Start");
        startButton.setSize(130, 30);
        startButton.setLocation(600, 35);
        startButton.setFont(new Font(startButton.getFont().getName(), Font.BOLD, 15));
        startButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		game.start();
        	}
        });
        this.add(startButton);
        ////////////////////////////////////////////////////////
        quitButton = new JButton("Quit");
        quitButton.setSize(130, 30);
        quitButton.setLocation(600, 80);
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
		game = new Game(this);
		game.parentFrame = this; // connect frame and game instance
		fieldPanel.game = game;
     
	}
	public void updateInit() {
		// initialize Field GUI according to game variables
		this.initDeckTable();
		fieldPanel.init(game.round);
	}

	public void initDeckTable() {
		deckTableModel.setRowCount(0);
		for(int i=0; i<game.myDeck.size(); ++i) {
			Card card = game.myDeck.at(i);
			deckTableModel.addRow(new Object[]{card.getName(), card.getHp(), card.getAtk()});
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
}
