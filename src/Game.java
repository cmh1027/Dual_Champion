public class Game extends Thread {
	public Deck myDeck, enemyDeck;
	public int round, myScore, enemyScore;
	private int myHp, enemyHp;
	private int turnCount;
	private int myCardCount, enemyCardCount;
	private int myFieldCardCount, enemyFieldCardCount;
	private AI ai;
	private boolean withPlayer;
	public Field field;
	public MainFrame parentFrame;
	public FieldPanel fieldPanel;
	public boolean myTurn;
	public volatile boolean waiting;
	private final int MAXROUND = 7;

	public Game(MainFrame parent, FieldPanel panel, boolean withPlayer){
		myDeck = new Deck();
		enemyDeck = new Deck();
		field = new Field();
		ai = new AI(this, field, enemyDeck);
		waiting = false;
		parentFrame = parent;
		fieldPanel = panel;
		this.withPlayer = withPlayer;
	}
	public int getMyHp() {
		return this.myHp;
	}
	public int getEnemyHp() {
		return this.enemyHp;
	}
	
	public void run() { // Thread run method
		this.myScore = 0;
		this.enemyScore = 0;
		this.parentFrame.setScore(0, 0);
		for(round=1; round<=MAXROUND; ++round) {
			this.init();
			this.myTurn = true; // Player is first
			while(myHp > 0 && enemyHp > 0 && !haveNoCard(true) && !haveNoCard(false) && !isBlockedByEnemy(true) && !isBlockedByEnemy(false)) {
				this.turn();
			}
			if(myHp <= 0 || haveNoCard(true) || isBlockedByEnemy(true)) {
				loseRound();
			}
			else {
				winRound();
			}
			this.parentFrame.setScore(this.myScore, this.enemyScore);
			if(this.myScore > this.MAXROUND / 2 || this.enemyScore > this.MAXROUND / 2) {
				break;
			}
			else {
				try {
					Thread.sleep(3000);
				}
				catch(Exception e) {
					e.printStackTrace();
				}				
			}
		}
		if(this.myScore > this.enemyScore) {
			this.winMatch();
		}
		else if(this.myScore < this.enemyScore) {
			this.loseMatch();
		}
		else {
			this.drawMatch();
		}
		this.parentFrame.gameEnded();
	}

	public void turn(){
		if(isPlayerTurn()) {
			if(this.isEnemyAI()) {
				parentFrame.updateDisplay(String.format("Round %d-%d / Player's Turn", this.round, this.turnCount));
			}
			else {
				parentFrame.updateDisplay(String.format("Round %d-%d / Player 1's Turn", this.round, this.turnCount));
			}
			
			this.waiting = true;
			while(this.waiting) {
				;
			}
		}
		else {
			if(this.isEnemyAI()) {
				parentFrame.updateDisplay(String.format("Round %d-%d / Opponent's Turn", this.round, this.turnCount));
			}
			else {
				parentFrame.updateDisplay(String.format("Round %d-%d / Player 2's Turn", this.round, this.turnCount));
			}
			if(isEnemyAI()) {
				try {
					Thread.sleep(1000);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				ai.nextAction();				
			}
			else {
				this.waiting = true;
				while(this.waiting) {
					;
				}				
			}

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
		myCardCount = 0;
		myFieldCardCount = 0;
		enemyCardCount = 0;
		enemyFieldCardCount = 0;
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
			this.putDeckCard(cardClass, isPlayer);		
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
					this.myFieldCardCount -= 1;
				}
				else {
					this.enemyCardCount -= 1;
					this.enemyFieldCardCount -= 1;
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
						this.myFieldCardCount -= 1;
					}
					else {
						this.enemyCardCount -= 1;
						this.enemyFieldCardCount -= 1;
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
		Card hitCard;
		for(int i=-1; i<=1; i=i+2) {
			if(this.isValidCoord(row+i, col) && (hitCard = field.get(row+i, col)) != null) {
				this.attack(row, col, row+i, col);
			}
		}
		for(int i=-1; i<=1; i=i+2) {
			if(this.isValidCoord(row, col+i) && (hitCard = field.get(row, col+i)) != null) {
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
		if(isPlayer) {
			card = this.myDeck.pop(cardIndex);
			this.myFieldCardCount += 1;
		}
		else {
			card = this.enemyDeck.pop(cardIndex);
			if(this.isEnemyAI()) {
				this.ai.addFieldCard(row, col);
			}
			this.enemyFieldCardCount += 1;
		}
		this.field.set(row, col, card);
		this.fieldPanel.updateCell(row, col);
		this.turnEnd();
	}
	

	public void winRound() {
		this.myScore += 1;
		if(this.isEnemyAI()) {
			parentFrame.updateDisplay("You Win! Ready for the next battle");
		}
		else {
			parentFrame.updateDisplay("Player 1 Win! Ready for the next battle");
		}
		
	}
	
	public void loseRound() {
		this.enemyScore += 1;
		if(this.isEnemyAI()) {
			parentFrame.updateDisplay("You Lose! Ready for the next battle");
		}
		else {
			parentFrame.updateDisplay("Player 2 Win! Ready for the next battle");
		}
	}
	
	public void winMatch() {
		if(this.isEnemyAI()) {
			parentFrame.updateDisplay("You are the winner of the match!");
		}
		else {
			parentFrame.updateDisplay("Player 1 is the winner of the match!");
		}
	}
	
	public void loseMatch() {
		if(this.isEnemyAI()) {
			parentFrame.updateDisplay("Computer is the winner of the match!");
		}
		else {
			parentFrame.updateDisplay("Player 2 is the winner of the match!");
		}
		
	}
	public void drawMatch() {
		parentFrame.updateDisplay("DRAW!");
	}
	
	
	public String toString() {
		if(isPlayerTurn()) {
			return String.format("Round %d : My Turn", round);
		}
		else {
			return String.format("Round %d : Opponent's Turn", round);
		}
	}
	public boolean isFirstPosition(int row, int col, boolean isPlayer) {
		if(isPlayer) {
			return (row == round && col == 0) || (row == round+1 && col == 1);
		}
		else {
			return (row == 0 && col == round) || (row == 1 && col == round+1);
		}
		
	}
	public boolean haveSpaceToPut(boolean isPlayer) {
		if(isPlayer) {
			return this.field.get(this.round, 0) == null || this.field.get(this.round+1, 1) == null;
		}
		else {
			return this.field.get(0, this.round) == null || this.field.get(1, this.round+1) == null;
		}
	}
	public boolean isBlockedByEnemy(boolean isPlayer) {
		if(isPlayer) {
			if(this.field.get(this.round, 0) == null ||  this.field.get(this.round+1, 1) == null)
				return false;
			return this.myFieldCardCount == 0 && !this.field.get(this.round, 0).isPlayerCard() && !this.field.get(this.round+1, 1).isPlayerCard();
		}
		else {
			if(this.field.get(0, this.round) == null ||  this.field.get(1, this.round+1) == null)
				return false;
			return this.enemyFieldCardCount == 0 && this.field.get(0, this.round).isPlayerCard() && this.field.get(1, this.round+1).isPlayerCard();
		}
	}
	
	public boolean isEnemyAI() {
		return !this.withPlayer;
	}
	
	public boolean isCurrentPlayerCard(Card card) {
		return card.isPlayerCard() && this.isPlayerTurn() || !card.isPlayerCard() && !this.isPlayerTurn();
	}
}
