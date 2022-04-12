public class Card {
	private int atk;
	private int hp;
	private Champions.cardClass cardClass;
	private boolean player;
	private boolean nexus;
	public Card(int atk, int hp, Champions.cardClass cardClass, boolean player) {
		this.atk = atk;
		this.hp = hp;
		this.cardClass = cardClass;
		this.player = player;
		this.nexus = false;
	}
	public void setAtk(int atk) {
		this.atk = atk;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	public void setCardClass(Champions.cardClass cardClass) {
		this.cardClass = cardClass;
	}
	public void getDamage(int damage) {
		this.hp = this.hp - damage;
	}
	public Boolean isDead() {
		return this.hp <= 0;
	}
	public int getAtk() {
		return this.atk;
	}
	public int getHp() {
		return this.hp;
	}
	public Champions.cardClass getCardClass() {
		return this.cardClass;
	}
	public String getName() {
		switch(this.cardClass) {
		case NEXUS:
			return "Nexus";
		case WARRIOR:
			return "Warrior";
		case TANK:
			return "Tank";
		case ARCHER:
			return "Archer";
		case KNIGHT:
			return "Knight";
		case JUMPKING:
			return "Jumpking";
		case BOMBER:
			return "Bomber";
		default:
			return "None";
		}
	}
	public boolean isPlayerCard() {
		return player;
	}
	public void setNexus() {
		this.nexus = true;
	}
	public boolean isNexus() {
		return nexus;
	}
}
