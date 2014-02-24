package document;

import java.awt.*;
import java.util.LinkedList;

/**
 * Created by EdwardZhu on 14-2-21.
 */
public abstract class Glyph {
	Rectangle bound;
	LinkedList<Glyph> children;
	Glyph parent;

	Glyph(int x, int y, int width, int height) {
		bound = new Rectangle(x ,y, width, height);
	}

	Glyph(int x, int y) {
		this(x, y, 0, 0);
	}

	Glyph() {
		this(0, 0, 0 ,0);
	}

	public Rectangle getBound() {
		return bound;
	}

	public void paint(Graphics g) {
		paintComponent(g);
		paintChildren(g);
	};

	abstract public void paintChildren(Graphics g);

	abstract public void paintComponent(Graphics g);

	public boolean intersects(Point p) {
		return bound.contains(p);
	}

	public boolean intersects(int x, int y) {
		return bound.contains(x, y);
	}

	public void add(Glyph glyph) {
		glyph.parent = this;
		children.add(glyph);
	}

	public void add(int index, Glyph g) {
		g.parent = this;
		children.add(index, g);
	}

	public int getSize() {
		return children.size();
	}

	public Glyph getParent() {
		return parent;
	}
}
