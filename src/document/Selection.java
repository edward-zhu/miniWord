package document;

import java.util.ArrayList;

/**
 * Created by EdwardZhu on 14-2-23.
 */
public class Selection {
	ArrayList<Glyph> items;
	Pos p1, p2;

	public Pos getP1() {
		return p1;
	}

	public void setP1(Pos p1) {
		this.p1 = p1;
	}

	public Pos getP2() {
		return p2;
	}

	public void setP2(Pos p2) {
		this.p2 = p2;
	}

	Selection (Pos p1, Pos p2, ArrayList<Glyph> items) {
		this.p1 = p1;
		this.p2 = p2;
		this.items = items;
	}

	public ArrayList<Glyph> getItems() {
		return items;
	}

	public int size() {
		return items.size();
	}



}
