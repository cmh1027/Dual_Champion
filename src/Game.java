public class Game extends Thread {
	public Deck myDeck, enemyDeck;
	public int round;
	private int myHp, enemyHp;
	private int turnCount;
	private int myCardCount, enemyCardCount;
	private AI ai;
	public Field field;
	public MainFrame parentFrame;
	public FieldPanel fieldPanel;
	public boolean myTurn;
	public volatile boolean waiting;
	private final int MAXROUND = 7;

	public Game(MainFrame parent, FieldPanel panel){
		myDeck = new Deck();
		enemyDeck = new Deck();
		field = new Field();
		ai = new AI(this, field, enemyDeck);
		waiting = false;
		parentFrame = parent;
		fieldPanel = panel;
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
			while(myHp > 0 && enemyHp > 0 && !haveNoCard(true) && !haveNoCard(false)) {
				this.turn();
			}
			if(myHp <= 0 || haveNoCard(true)) {
				lose();
			}
			else {
				win();
			}
		}
		this.parentFrame.gameEnded();
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
			try {
				Thread.sleep(1000);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			ai.nextAction();
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
		myHp = Champion.HP[Champion.Class.NEXUS.getIndex()];
		enemyHp = Champion.HP[Champion.Class.NEXUS.getIndex()];
		field.init(2+round, 2+round);
		turnCount = 1;
		this.deckInit();
		this.nexusInit();
		this.parentFrame.updateInit();
		this.ai.init();
	}
	
	public void nexusInit() {
		Card myNexus = new Card(Champion.Class.NEXUS, true);
		Card enemyNexus = new Card(Champion.Class.NEXUS, false);
		field.set(2+round-1, 0, myNexus);
		field.set(0, 2+round-1, enemyNexus);
	}
	
	public void putDeckCard(Champion.Class cardClass, boolean isPlayer) {
		if(isPlayer) {
			this.myCardCount += 1;
			myDeck.putItem(new Card(cardClass, true));
		}
		else {
			this.enemyCardCount += 1;
			enemyDeck.putItem(new Card(cardClass, false));
		}
	}

	public void putDeckCard(Champion.Class cardClass, boolean isPlayer, int count) {
		for(int i=0; i<count; ++i) {
			if(isPlayer) {
				this.myCardCount += 1;
				myDeck.putItem(new Card(cardClass, true));
			}
			else {
				this.enemyCardCount += 1;
				enemyDeck.putItem(new Card(cardClass, false));
			}			
		}
	}
	
	public boolean haveNoCard(boolean isPlayer) {
		if(isPlayer)
			return this.myCardCount == 0;
		else
			return this.enemyCardCount == 0;
	}
	
	public void deckInit() {
		myDeck.empty();
		enemyDeck.empty();
		// WARRIOR, TANK, ARCHER, KNIGHT, JUMPKING, BOMBER
		this.putDeckCard(Champion.Class.WARRIOR, true, 2);
		this.putDeckCard(Champion.Class.TANK, true, 2);
		this.putDeckCard(Champion.Class.ARCHER, true);
		this.putDeckCard(Champion.Class.KNIGHT, true);
		this.putDeckCard(Champion.Class.JUMPKING, true);
		this.putDeckCard(Champion.Class.BOMBER, true);
		this.putDeckCard(Champion.Class.WARRIOR, false, 2);
		this.putDeckCard(Champion.Class.TANK, false, 2);
		this.putDeckCard(Champion.Class.ARCHER, false);
		this.putDeckCard(Champion.Class.KNIGHT, false);
		this.putDeckCard(Champion.Class.JUMPKING, false);
		this.putDeckCard(Champion.Class.BOMBER, false);
		
	}

	public void attack(int atkRow, int atkCol, int hitRow, int hitCol) {
		Card attackCard = field.get(atkRow, atkCol);
		Card hitCard = field.get(hitRow, hitCol);
		hitCard.getDamage(attackCard.getAtk());
		if(hitCard.isNexus()) {
			if(hitCard.isPlayerCard()) {
				this.myHp = this.myHp - attackCard.getAtk();
			}
			else {
				this.enemyHp = this.enemyHp - attackCard.getAtk();
			}
		}
		else {
			if(hitCard.isDead()) {
				if(hitCard.isPlayerCard()) {
					this.myCardCount -= 1;
				}
				else {
					this.enemyCardCount -= 1;
					this.ai.removeFieldCard(hitRow, hitCol);
				}
				if(hitCard.getCardClass() == Champion.Class.BOMBER) {
					this.bomberSuicide(hitRow, hitCol);
				}
				field.remove(hitRow, hitCol);
				if(attackCard.getCardClass() == Champion.Class.JUMPKING && !attackCard.isDead()) {
					this.move(atkRow, atkCol, hitRow, hitCol);
				}
			}
			else {
				if(attackCard.getCardClass() == Champion.Class.JUMPKING) {
					if(attackCard.isPlayerCard()) {
						this.myCardCount -= 1;
					}
					else {
						this.enemyCardCount -= 1;
						this.ai.removeFieldCard(hitRow, hitCol);					
					}
					field.remove(atkRow, atkCol);
				}
			}			
		}
		this.fieldPanel.updateCell(atkRow, atkCol);
		this.fieldPanel.updateCell(hitRow, hitCol);
		this.turnEnd();
	}

	public void move(int srcRow, int srcCol, int dstRow, int dstCol) {
		Card card = field.get(srcRow, srcCol);
		field.set(dstRow, dstCol, card);
		field.remove(srcRow, srcCol);
		this.fieldPanel.updateCell(srcRow, srcCol);
		this.fieldPanel.updateCell(dstRow, dstCol);
		this.turnEnd();
	}
	
	public void bomberSuicide(int row, int col) {
		Card attackCard = field.get(row, col);
		Card hitCard;
		for(int i=-1; i<=1; i=i+2) {
			if(this.isValidCoord(row+i, col) && (hitCard = field.get(row+i, col)) != null) {
				this.attack(row, col, row+i, col);
			}
		}
		for(int i=-1; i<=1; i=i+2) {
			if(this.isValidCoord(row, col+i) && (hitCard = field.get(row, col+i)) != null) {
				hitCard.getDamage(attackCard.getAtk());
				this.attack(row, col, row, col+i);	
			}
		}
	}

	public boolean isEnemy(int atkRow, int atkCol, int hitRow, int hitCol) {
		Card attackCard = field.get(atkRow, atkCol);
		Card hitCard = field.get(hitRow, hitCol);
		return attackCard.isPlayerCard() && !hitCard.isPlayerCard() || !attackCard.isPlayerCard() && hitCard.isPlayerCard();
	}
	
	public boolean isValidCoord(int row, int col) {
		return row >= 0 && col >= 0 && row <= this.round + 1 && col <= this.round + 1;
	}

	public void putCard(int row, int col, int cardIndex, boolean isPlayer) {
		Card card;
		if(isPlayer)
			card = this.myDeck.pop(cardIndex);
		else {
			card = this.enemyDeck.pop(cardIndex);
			this.ai.addFieldCard(row, col);
		}
		this.field.set(row, col, card);
		this.fieldPanel.updateCell(row, col);
		this.turnEnd();
	}
	

	public void win() {
		parentFrame.updateDisplay("You Win! Ready for the next battle");
		try {
			Thread.sleep(3000);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void lose() {
		parentFrame.updateDisplay("You Lose! Ready for the next battle");
		try {
			Thread.sleep(3000);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
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
