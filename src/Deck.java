import java.util.ArrayList;

public class Deck {
	ArrayList<Card> deck;
	public Deck() {
		 deck = new ArrayList<>();
	}
	public void empty() {
		this.deck.clear();
	}
	public Card at(int index) {
		return deck.get(index);
	}

	public Card pop(int index) {
		Card card = deck.get(index);
		deck.remove(index);
		return card;
	}
	
	public int size() {
		return deck.size();
	}
	public Card getItem(int index) {
		Card card = deck.get(index);
		deck.remove(index);
		return card;
	}
	public void putItem(Card card) {
		deck.add(card);
	}
}
