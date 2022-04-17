import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JPanel;
public class FieldPanel extends JPanel{
	public Game game;
	MainFrame parentFrame;
	CellArray cells;
	boolean selected;
	int selectedRow, selectedCol;
	enum CellColor{ EMPTY, PLAYER, OPONENT };
	public FieldPanel(MainFrame parentFrame) {
		super(new GridLayout(3, 3, 4, 4));
		this.parentFrame = parentFrame;
		this.cells = new CellArray();
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.setBackground(Color.gray);
	}
	public static class CellArray extends ArrayList<ArrayList<Cell>>{
		public Cell get(int row, int col) {
			return this.get(row).get(col);
		}
		public void add(int row, Cell c) {
			this.get(row).add(c);
		}
		public void set(int row, int col, Cell c) {
			this.get(row).set(col, c);
		}
		public void construction(int row, int col) {
			for(int i=0; i<row; ++i) {
				ArrayList<Cell> rowArray = new ArrayList<Cell>();
				for(int j=0; j<col; ++j) {
					rowArray.add(null);
				}
				this.add(rowArray);
			}
		}
	}
	public static class Cell extends JPanel{
		int row, col;
		private FieldPanel parentPanel;
		private JLabel cardInfo;
		public Cell(FieldPanel parent, int row, int col) {
			this.row = row;
			this.col = col;
			this.parentPanel = parent;
			this.addMouseListener(new CellMouseAdapter(parentPanel, this, row, col));
			this.setLayout(new GridBagLayout());
			this.cardInfo = new JLabel();
			this.cardInfo.setFont(new Font(this.cardInfo.getFont().getName(), this.cardInfo.getFont().getStyle(), 20));
			this.add(this.cardInfo);
		}
		public void setInfo(String info) {
			this.cardInfo.setText(info);
		}
		public void setInfo(String info, Color color) {
			this.setInfo(info);
			this.cardInfo.setForeground(color);
			
		}
		public void setColor(CellColor color) {
			switch(color) {
			case EMPTY:
				this.setBackground(Color.white);
				break;
			case PLAYER:
				this.setBackground(Color.cyan);
				break;
			case OPONENT:
				this.setBackground(Color.pink);
				break;
			}
		}
		
		public void setFontSize(int size) {
			this.cardInfo.setFont(new Font(this.cardInfo.getFont().getName(), this.cardInfo.getFont().getStyle(), size));
		}
		
		public void select() {
			this.setBorder(BorderFactory.createLineBorder(Color.black, 5));
	   		this.parentPanel.selected = true;
	   		this.parentPanel.selectedRow = this.row;
	   		this.parentPanel.selectedCol = this.col;
		}
		public void unselect() {
			this.setBorder(BorderFactory.createEmptyBorder());
			this.parentPanel.selected = false;
		}
		
	}
	
	public static class CellMouseAdapter extends MouseAdapter{
		int row, col;
		private FieldPanel parentPanel;
		private Cell self;
		private Game game;
		public CellMouseAdapter(FieldPanel parent, Cell self, int row, int col) {
			this.row = row;
			this.col = col;
			this.parentPanel = parent;
			this.self = self;
			this.game = parentPanel.game;
		}
        @Override
        public void mouseClicked(MouseEvent e) {
        	 int selectedRow = parentPanel.selectedRow;
        	 int selectedCol = parentPanel.selectedCol;
        	 if(game.isPlayerTurn()) {
            	 Card card = game.field.get(this.row, this.col);
            	 Card selectedCard = game.field.get(selectedRow, selectedCol);
            	 if(parentPanel.selected){
            		 if(selectedRow == this.row && selectedCol == this.col) { // unselect
            			 self.unselect();
            		 }
            		 else {
            			 if(card == null) {
            				 if(selectedCard.isMovable(selectedRow, selectedCol, this.row, this.col)) { // move
            					 parentPanel.cells.get(selectedRow, selectedCol).unselect();
                				 game.move(selectedRow, selectedCol, this.row, this.col);
                				 parentPanel.updateCell(selectedRow, selectedCol);
                				 parentPanel.updateCell(this.row, this.col); 
            				 }
            			 }
            			 else {
            				 // attack
            				 if(!card.isPlayerCard() && selectedCard.isPlayerCard() && selectedCard.isMovable(selectedRow, selectedCol, this.row, this.col)) {
            					 parentPanel.cells.get(selectedRow, selectedCol).unselect();
            					 game.attack(selectedRow, selectedCol, this.row, this.col);
                				 parentPanel.updateCell(selectedRow, selectedCol);
                				 parentPanel.updateCell(this.row, this.col);
            				 }
            			 }
            		 }
            	 }
            	 else {
            		 int selectedDeckCardIndex = parentPanel.parentFrame.getSelectedCard();
            		 if(selectedDeckCardIndex == -1) { // No card is selected
                		 if(card != null && !card.isNexus() && card.isPlayerCard()) { // Player Card exists
                			 self.select();
                		 }
                		 else {
                			 
                		 }
            		 }
            		 else {
            			 if(card == null && game.isFirstPosition(row, col)) { // put card on the field
            				 game.putCard(row, col, selectedDeckCardIndex, true);
            				 parentPanel.updateCell(row, col);
            				 parentPanel.parentFrame.updateDeckTable(selectedDeckCardIndex);
            			 }
            		 }
            	 }
        	 }
        }	
	}
    
	public void defaultUpdate() {
		cells.construction(3, 3);
		for(int row=0; row<3; ++row) {
			for(int col=0; col<3; ++col) {
				Cell cell = new Cell(this, row, col);
				cell.setBackground(Color.white);
				this.add(cell);
				cells.set(row, col, cell);
			}
		}
	}
	
	public void destroyField() {
		this.removeAll();
		this.revalidate();
		this.repaint();
	}
	
	public void init(int round) {
		this.destroyField();
		this.cells.clear();
		this.setLayout(new GridLayout(round+2, round+2, 4, 4));
		this.cells.construction(round+2, round+2);
		for(int i=0; i<round+2; ++i) {
			for(int j=0; j<round+2; ++j) {
				Cell cell = new Cell(this, i, j);
				Card card = this.game.field.get(i, j);
				cell.setFontSize(22 - 2 * round);
				if(card != null && card.isNexus()) {
					if(card.isPlayerCard()) {
						cell.setBackground(Color.blue);
						cell.setInfo(String.format("<html>Nexus<br/>HP : %d</html>", game.getMyHp()), Color.white);
					}
					else {
						cell.setBackground(Color.red);
						cell.setInfo(String.format("<html>Nexus<br/>HP : %d</html>", game.getEnemyHp()), Color.white);
					}
				}
				else {
					cell.setBackground(Color.white);
				}
				this.add(cell);
				cells.set(i, j, cell);
			}
		}
		this.revalidate();
		this.repaint();
	}
	

	public void updateCell(int row, int col) {              
		Card card = this.game.field.get(row, col);
		Cell cell = this.cells.get(row, col);
		if(card == null) {
			cell.setColor(CellColor.EMPTY);
			cell.setInfo("");
		}
		else {
			if(!card.isNexus()) {
				String info = String.format("<html>%s<br/>ATK : %d<br/>HP : %d</html>", card.getName(), card.getAtk(), card.getHp());
				cell.setInfo(info);
				if(card.isPlayerCard()) {
					cell.setColor(CellColor.PLAYER);
				}
				else {
					cell.setColor(CellColor.OPONENT);
				}
			}
			else {
				String info = String.format("<html>%s<br/>HP : %d</html>", card.getName(), card.getHp());
				cell.setInfo(info, Color.white);
			}
		}
	}
	
	public void unselectCurrentCell() {
		Cell cell = this.cells.get(this.selectedRow, this.selectedCol);
		cell.unselect();
		this.selected = false;
	}

}
