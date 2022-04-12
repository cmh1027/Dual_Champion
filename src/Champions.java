
public class Champions {
	public static enum cardClass{NEXUS, WARRIOR, TANK, ARCHER, KNIGHT, JUMPKING, BOMBER}; // SWIFT, ASSASIN
	// Warrior : Atk 2 Hp 1
	// Tank : Atk 1 Hp 2
	// Archer : Atk 1 Hp 1 (can attack diagonally)
	// Knight : Atk 1 Hp 1 (can move two blocks)
	// Jumpking : Atk 1 Hp 1 (can move two blocks straight) - If the enemy dies, it moves. If it doesn't jumpking dies.
	// Bomber : Atk 1 Hp 1 - If he dies, it damages everything around (no diagonal)
	// Swift : It can attack and move simultaneously in one turn
	// Assassin : After it moves, if the enemy is around, it can move to another three sides of the enemy
}
