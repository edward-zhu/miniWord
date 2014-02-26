package document;

import com.sun.tools.javac.util.Pair;

import java.util.ArrayList;

/**
 * Created by EdwardZhu on 14-2-23.
 */
public class Selection {
	final private ArrayList<Glyph> items;
	final private Pair<Pos, Pos> p;

	public Pos getP1() {
		return p.fst;
	}

	public Pos getP2() {
		return p.snd;
	}

	Selection (Pos p1, Pos p2, ArrayList<Glyph> items) {
		this.p = new Pair<Pos, Pos>(p1, p2);
		this.items = items;
	}

	public ArrayList<Glyph> getItems() {
		return items;
	}

	public int size() {
		return items.size();
	}




}
