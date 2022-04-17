// Warrior : Atk 2 Hp 1
// Tank : Atk 1 Hp 2
// Archer : Atk 1 Hp 1 (can attack diagonally)
// Knight : Atk 1 Hp 1 (can move two blocks)
// Jumpking : Atk 1 Hp 1 (can move two blocks straight) - If the enemy dies, it moves. If it doesn't jumpking dies.
// Bomber : Atk 1 Hp 1 - If he dies, it damages everything around (no diagonal)
// Swift : It can attack and move simultaneously in one turn
// Assassin : After it moves, if the enemy is around, it can move to another three sides of the enemy

import java.lang.Math;
public class Champion {
	public static enum Class{
		NEXUS(0), WARRIOR(1), TANK(2), ARCHER(3), KNIGHT(4), JUMPKING(5), BOMBER(6);
		private final int index;
		Class(int index){
			this.index = index;
		}
		int getIndex() {
			return this.index;
		}
	}
	public static int HP[] = {30, 1, 2, 1, 1, 1, 1};
	public static int ATK[] = {0, 2, 1, 1, 1, 1, 1};
	private Class cardClass;
	public Champion(Class cardClass) {
		this.cardClass = cardClass;
	}
	public Class getCardClass() {
		return this.cardClass;
	}
	public boolean isAdjacent(int atkRow, int atkCol, int hitRow, int hitCol, int maxDistance) {
		return Math.abs(atkRow-hitRow) <= maxDistance && atkCol == hitCol || atkRow == hitRow && Math.abs(atkCol-hitCol) <= maxDistance;
	}
	public boolean isDiagonal(int atkRow, int atkCol, int hitRow, int hitCol, int maxDistance) {
		return Math.abs(atkRow-hitRow) <= maxDistance && Math.abs(atkCol-hitCol) <= maxDistance;
	}
	public boolean isMovable(int atkRow, int atkCol, int hitRow, int hitCol) {
		switch(this.cardClass) {
		case NEXUS:
			return false;
		case WARRIOR:
			if(isAdjacent(atkRow, atkCol, hitRow, hitCol, 1))
				return true;
			else
				return false;
		case TANK:
			if(isAdjacent(atkRow, atkCol, hitRow, hitCol, 1))
				return true;
			else
				return false;
		case ARCHER:
			if(isAdjacent(atkRow, atkCol, hitRow, hitCol, 1) || isDiagonal(atkRow, atkCol, hitRow, hitCol, 1))
				return true;
			else
				return false;
		case KNIGHT:
			if(isAdjacent(atkRow, atkCol, hitRow, hitCol, 2) || isDiagonal(atkRow, atkCol, hitRow, hitCol, 1))
				return true;
			else
				return false;
		case JUMPKING:
			if(isAdjacent(atkRow, atkCol, hitRow, hitCol, 2))
				return true;
			else
				return false;
		case BOMBER:
			if(isAdjacent(atkRow, atkCol, hitRow, hitCol, 1))
				return true;
			else
				return false;
		default:
			return false;
		}
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
}

