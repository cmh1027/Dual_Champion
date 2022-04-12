import java.util.Random;
import java.util.ArrayList;

public class Game extends Thread {
	public Deck myDeck, enemyDeck;
	public int round;
	private int myHp, enemyHp;
	private int turnCount;
	public Field field;
	public MainFrame parentFrame;
	public boolean myTurn;
	public volatile boolean waiting;
	private final int initialHp = 30;
	private final int MAXROUND = 1;

	public Game(MainFrame parent){
		myDeck = new Deck();
		enemyDeck = new Deck();
		field = new Field();
		waiting = false;
		parentFrame = parent;
	}
	public int getMyHp() {
		return this.myHp;
	}
	public int getEnemyHp() {
		return this.enemyHp;
	}
	
	public void run() { // Thread run method
		for(round=1; round<=MAXROUND; ++round) {
			this.init();
			this.myTurn = true; // Player is first
			while(myHp > 0 && enemyHp > 0) {
				this.turn();
			}
			if(myHp <= 0) {
				lose();
			}
			else {
				win();
			}	
		}
	}

	public void turn(){
		if(this.myTurn) {
			parentFrame.updateDisplay(String.format("Round %d-%d / Player's Turn", this.round, this.turnCount));
			this.waiting = true;
			while(this.waiting) {
				;
			}
		}
		else {
			parentFrame.updateDisplay(String.format("Round %d-%d / Opponent's Turn", this.round, this.turnCount));
			System.out.println("Opponent's turn");
		}
		this.myTurn = !this.myTurn;
		this.turnCount++;
	}
	
	public void turnEnd() {
		this.waiting = false;
	}
	
	public boolean isPlayerTurn() {
		return this.myTurn;
	}
	
	public void init() {
		myHp = initialHp;
		enemyHp = initialHp;
		myDeck.empty();
		enemyDeck.empty();
		field.init(2+round, 2+round);
		turnCount = 1;
		this.deckInit();
		this.nexusInit();
		this.parentFrame.updateInit();
	}
	
	public void nexusInit() {
		Card myNexus = new Card(0, initialHp, Champion.Class.NEXUS, true);
		Card enemyNexus = new Card(0, initialHp, Champion.Class.NEXUS, false);
		myNexus.setNexus();
		enemyNexus.setNexus();
		field.set(2+round-1, 0, myNexus);
		field.set(0, 2+round-1, enemyNexus);
	}
	
	public void deckInit() {
		final int CARDNUM = 10;
		final int minimum = 10;
		final int maximum = 90; // max is minimum + maximum
		Random random = new Random();
		for(int i=0; i<CARDNUM; ++i) {
			myDeck.putItem(new Card(minimum + random.nextInt(maximum), minimum + random.nextInt(maximum), Champion.Class.KNIGHT, true));
			enemyDeck.putItem(new Card(minimum + random.nextInt(maximum), minimum + random.nextInt(maximum), Champion.Class.WARRIOR, false));
		}
		
	}

	public void attack(int atkRow, int atkCol, int hitRow, int hitCol) {
		Card attackCard = field.get(atkRow, atkCol);
		Card hitCard = field.get(hitRow, hitCol);
		hitCard.getDamage(attackCard.getAtk());
		if(hitCard.isNexus()) {
			if(myTurn) {
				enemyHp = enemyHp - attackCard.getAtk();
			}
			else {
				myHp = myHp - attackCard.getAtk();
			}
		}
		if(hitCard.isDead()) {
			field.remove(hitRow, hitCol);
		}
		this.turnEnd();
	}

	public boolean isAttackable(int atkRow, int atkCol, int hitRow, int hitCol) {
		Card attackCard = field.get(atkRow, atkCol);
		Card hitCard = field.get(hitRow, hitCol);
		return attackCard.isPlayerCard() && !hitCard.isPlayerCard();
	}
	

	public void move(int srcRow, int srcCol, int dstRow, int dstCol) {
		Card card = field.get(srcRow, srcCol);
		field.set(dstRow, dstCol, card);
		field.remove(srcRow, srcCol);
		this.turnEnd();
	}

	public void putCard(int row, int col, int cardIndex, boolean isPlayer) {
		Card card;
		if(isPlayer)
			card = this.myDeck.pop(cardIndex);
		else
			card = this.enemyDeck.pop(cardIndex);
		this.field.set(row, col, card);
		this.turnEnd();
	}
	

	public void win() {
		System.out.println("You win");
	}
	public void lose() {
		System.out.println("You lose");
	}
	
	public String toString() {
		if(myTurn) {
			return String.format("Round %d : My Turn", round);
		}
		else {
			return String.format("Round %d : Opponent's Turn", round);
		}
	}
	public boolean isFirstPosition(int row, int col) {
		return (row == round && col == 0) || (row == round+1 && col == 1);
	}
}
