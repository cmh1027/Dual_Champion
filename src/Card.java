public class Card {
	private int atk;
	private int hp;
	private Champion champion;
	private boolean player;
	private boolean nexus;
	public Card(Champion.Class cardClass, boolean player) {
		this.champion = new Champion(cardClass);
		this.atk = Champion.ATK[cardClass.getIndex()];
		this.hp = Champion.HP[cardClass.getIndex()];
		this.player = player;
		this.nexus = cardClass == Champion.Class.NEXUS;
	}
	public void setAtk(int atk) {
		this.atk = atk;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	public void getDamage(int damage) {
		this.hp = this.hp - damage;
	}
	public boolean isDead() {
		return this.hp <= 0;
	}
	public int getAtk() {
		return this.atk;
	}
	public int getHp() {
		return this.hp;
	}
	public Champion getCardClass() {
		return this.champion;
	}
	public String getName() {
		return this.champion.getName();
	}
	public boolean isMovable(int atkRow, int atkCol, int hitRow, int hitCol) {
		return this.champion.isMovable(atkRow, atkCol, hitRow, hitCol);
	}
	public boolean isPlayerCard() {
		return player;
	}
	public boolean isNexus() {
		return nexus;
	}
}
