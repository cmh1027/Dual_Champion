import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
public class AI {
	private Game game;
	private Field field;
	private Deck AIdeck;
	private HashSet<Coordinate> fieldCards;
	private Random random;
	public AI(Game game, Field field, Deck deck){
		this.game = game;
		this.field = field;
		this.AIdeck = deck;
		this.fieldCards = new HashSet<>();
		random = new Random();
	}
	
	private static class Coordinate{
		public int row, col;
		public Coordinate(int row, int col) {
			this.row = row;
			this.col = col;
		}
	    @Override
	    public boolean equals(Object object){
	        if(object instanceof Coordinate){
	            return this.row == ((Coordinate)object).row && this.col == ((Coordinate)object).col;
	        } else {
	            return false;
	        }
	    }
	    
	    @Override
	    public int hashCode(){
	        return Objects.hash(this.row, this.col);
	    }
	}
	
	public void init() {
		this.fieldCards.clear();
	}

	public void addFieldCard(int row, int col) {
		this.fieldCards.add(new Coordinate(row, col));
	}
	
	public void removeFieldCard(int row, int col) {
		this.fieldCards.remove(new Coordinate(row, col));
	}

	public void moveFieldCard(int srcRow, int srcCol, int dstRow, int dstCol) {
		this.removeFieldCard(srcRow, srcCol);
		this.addFieldCard(dstRow, dstCol);
	}
	

	
	public boolean percent(int p) { // p percent probability
		return random.nextInt(100) < p;
	}
	
	public Coordinate randomCoord() {
		int index = this.random.nextInt(this.fieldCards.size());
		return (Coordinate) this.fieldCards.toArray()[index];
	}
	
	
	public void nextAction() {
		if(this.AIdeck.size() == 0) { // Can't put a card
			this.move();
		}
		else {
			if(this.game.haveSpaceToPut(false)) {
				if(this.percent(50) || this.fieldCards.size() == 0) { // 
					this.putCard();
				}
				else {
					this.move();
				}
			}
			else { // Can't put a card
				// Bug
				this.move();
			}
		}
	}
	
	public void putCard() {
		int deckIndex = random.nextInt(AIdeck.size());
		if(this.percent(50)) {
			if(this.field.get(0, this.game.round) == null) {
				this.game.putCard(0, this.game.round, deckIndex, false);
			}
			else {
				this.game.putCard(1, this.game.round+1, deckIndex, false);
			}
		}
		else {
			if(this.field.get(1, this.game.round+1) == null) {
				this.game.putCard(1, this.game.round+1, deckIndex, false);
			}
			else {
				this.game.putCard(0, this.game.round, deckIndex, false);
			}			
		}
	}
	
	public void move() { // move or attack
		Coordinate coord = this.randomCoord();
		int srcRow = coord.row, srcCol = coord.col;
		int dstRow, dstCol;
		while(true) {
			dstRow = srcRow;
			dstCol = srcCol;
			int _case = random.nextInt(4);
			if(_case == 0) {
				dstRow += 1;
			}
			else if(_case == 1) {
				dstRow -= 1;
			}
			else if(_case == 2) {
				dstCol += 1;
			}
			else {
				dstCol -= 1;
			}
			if(this.game.isValidCoord(dstRow, dstCol)) {
				if(this.field.get(dstRow, dstCol) == null) { // move
					this.game.move(srcRow, srcCol, dstRow, dstCol);
					this.moveFieldCard(srcRow, srcCol, dstRow, dstCol);
					break;
				}
				else { // attack
					if(this.game.isEnemy(srcRow, srcCol, dstRow, dstCol)) {
						this.game.attack(srcRow, srcCol, dstRow, dstCol);
						break;
					}
				}
			}
		}
	}
}
