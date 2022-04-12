import java.util.ArrayList;
public class Field {
	private ArrayList<ArrayList<Card>> field;
	public Field() {
		field = new ArrayList<>();
	}
	public void set(int row, int column, Card card) {
		field.get(row).set(column, card);
	}
	public Card get(int row, int column) {
		return field.get(row).get(column);
	}
	public void remove(int row, int column) {
		this.set(row, column, null);
	}
	public void init(int row, int column) {
		field.clear();
		for(int i=0; i<row; ++i) {
			ArrayList<Card> rowList = new ArrayList<>();
			for(int j=0; j<column; ++j) {
				rowList.add(null);
			}
			field.add(rowList);
		}
	}
}
