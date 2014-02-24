package document;

import java.awt.*;
import java.util.*;

/**
 * Created by EdwardZhu on 14-2-22.
 */

public class Row extends Glyph {
	int maxWidth;
	int margin;


	public class RowOutOfRoomException extends Exception {
		public RowOutOfRoomException() {}
		public RowOutOfRoomException(String msg) {
			super(msg);
		}
	}

	public Row(Graphics g, String s, int maxWidth, int margin, int x, int y) {
		this(g, x, y, maxWidth, margin);
		System.out.println("add Row : " + s);
		for (int i = 0; i < s.length(); i++) {
			try {
				this.add(g, s.charAt(i));
			}
			catch (RowOutOfRoomException e) {
				e.printStackTrace();
			}
		}
	}

	public Row(Graphics g, int x, int y, int maxWidth, int margin) {
		this(x, y, 0, g.getFontMetrics().getHeight() + margin, maxWidth, margin);
	}

	public Row(int x, int y, int width, int height, int maxWidth, int margin) {
		super(x, y, width, height);
		this.maxWidth = maxWidth;
		this.margin = margin;
		children = new LinkedList<Glyph>();
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	@Override
	public void paintChildren(Graphics g) {
		for (Glyph child : children) {
			child.paint(g);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		//g.drawRect((int)bound.getX(), (int)bound.getY(), (int)bound.getWidth(), (int)bound.getHeight());
	}

	int getOffsetWidth(int offset) {
		int w = 0, i = 0;
		for(Iterator iter = children.iterator();iter.hasNext() && i < offset;) {
			Chara chara = (Chara)iter.next();
			//System.out.printf("Chara %c's Width : %d\n", chara.c, (int)chara.getBound().getWidth());
			w += chara.getBound().getWidth();
			i++;
		}
		return w;
	}

	public void add(Chara c) {
		int x = (int)bound.getX() + (int)bound.getWidth();
		int y = (int)bound.getY();
		c.getBound().setLocation(x, y);
		bound.setSize((int) bound.getWidth() + (int)c.getBound().getWidth(), (int) bound.getHeight());
		super.add(c);
	}

	public Chara add(Graphics g, char c) throws RowOutOfRoomException {

		FontMetrics fm = g.getFontMetrics();
		int height = (int)getBound().getHeight();
		int width = fm.charWidth(c);
		System.out.println("charWidth = " + String.valueOf(width) + " rowWidth = " + String.valueOf(bound.getWidth()));
		if (bound.getWidth() + width > maxWidth) {
			throw new RowOutOfRoomException();
		}
		int x = (int)bound.getX() + (int)bound.getWidth();
		int y = (int)bound.getY();

		Chara chara = new Chara(c, x, y, width, height);
		bound.setSize((int) bound.getWidth() + width, (int) bound.getHeight());
		super.add(chara);
		System.out.println("x = " + String.valueOf(x) + " y = " + String.valueOf(y));
		System.out.println("bound -> " + bound.toString());
		return chara;
	}

	public Chara add(Graphics g, char c, int offset) throws RowOutOfRoomException {
		FontMetrics fm = g.getFontMetrics();
		int height = (int)getBound().getHeight();
		int width = fm.charWidth(c);
		//System.out.println("charWidth = " + String.valueOf(width) + " rowWidth = " + String.valueOf(bound.getWidth()));
		if (bound.getWidth() + width > maxWidth) {
			throw new RowOutOfRoomException();
		}
		int x = (int)bound.getX() + getOffsetWidth(offset);
		//System.out.printf("offsetWidth : %d\n", x - (int)bound.getX());
		int y = (int)bound.getY();
		if (children.size() > 0) {
			for (Iterator iter = children.listIterator(offset);iter.hasNext();) {
				Chara chara = (Chara)iter.next();
				int oriX = (int)chara.getBound().getX();
				int oriY = (int)chara.getBound().getY();
				chara.getBound().setLocation(oriX + width, oriY);
			}
		}

		Chara chara = new Chara(c, x, y, width, height);
		add(offset, chara);
		bound.setSize((int) bound.getWidth() + width, (int) bound.getHeight());
		return chara;
	}

	public int getOffset(Point p) {
		return getOffset((int)p.getX());
	}

	public int getOffset(int x) {
		int i = 0;
		if (x > getBound().getX() + getBound().getWidth()) return children.size();
		if (x < getBound().getX()) return 0;
		for (Glyph chara : children) {

			if (chara.bound.x <= x && chara.bound.x + chara.bound.width / 2 >= x) {
				System.out.println("i  " + i);
				return i;
			}
			else if (chara.bound.x + chara.bound.width / 2 <= x && chara.bound.x + chara.bound.width >= x){
				return i + 1;
			}
			i++;
		}
		return -1;
	}

	public void removeLast() {
		if (!children.isEmpty()) {
			Chara chara = (Chara)children.removeLast();
			bound.setSize((int)(bound.getWidth() - chara.getBound().getWidth()), (int)bound.getHeight());
		}
	}

	public void remove(int offset) {

		if (children.size() >= offset && offset > 0) {
			Chara chara = (Chara)children.remove(offset - 1);
			System.out.printf("%d\n", offset);
			int width = (int)chara.getBound().getWidth();
			bound.setSize((int)(bound.getWidth() - chara.getBound().getWidth()), (int)bound.getHeight());
			for (Iterator iter = children.listIterator(offset - 1);iter.hasNext();) {
				Chara c = (Chara)iter.next();
				Rectangle rect = c.getBound();
				rect.setLocation((int)rect.getX() - width, (int)rect.getY());
			}

		}
	}

	public void removeAll(Collection<Chara> charas) {
		for (Chara chara : charas) {
			remove(chara);
		}
	}

	public void remove(Chara chara) {
		if (children.contains(chara)) {
			int offset = children.indexOf(chara);
			children.remove(chara);
			int width = (int)chara.getBound().getWidth();
			bound.setSize((int)(bound.getWidth() - chara.getBound().getWidth()), (int)bound.getHeight());
			for (Iterator iter = children.listIterator(offset);iter.hasNext();) {
				Chara c = (Chara)iter.next();
				Rectangle rect = c.getBound();
				rect.setLocation((int)rect.getX() - width, (int)rect.getY());
			}
		}
	}

	public void removeAll(int offset, int length) {
		int width = 0;
		for (Iterator iter = children.listIterator(offset);iter.hasNext() && length > 0;) {
			Chara c = (Chara)iter.next();
			iter.remove();
			width += (int)c.getBound().getWidth();
			length--;
		}
		for (Iterator iter = children.listIterator(offset);iter.hasNext();) {
			Chara c = (Chara)iter.next();
			Rectangle rect = c.getBound();
			rect.setLocation((int)rect.getX() - width, (int)rect.getY());
		}
		getBound().setSize((int)getBound().getWidth() - width, (int)getBound().getHeight());
	}

	public void print() {
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			System.out.print(((Chara)iter.next()).c);
		}
		System.out.println();
		System.out.printf("Bound %d %d %d %d\n", getBound().x, getBound().y, getBound().width, getBound().height);
	}

	public void setVerticalLocation(int y) {
		int diff = y - getBound().y;

		getBound().setLocation(getBound().x, getBound().y + diff);
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			Chara c = (Chara)iter.next();
			c.getBound().setLocation(c.getBound().x, c.getBound().y + diff);
		}
	}

	public ArrayList<Glyph> getAllFrom(int offset) {
		ArrayList<Glyph> items = new ArrayList<Glyph>();

		for (Iterator iter = children.listIterator(offset); iter.hasNext();) {
			items.add((Glyph) iter.next());
		}
		return items;
	}

	public String getString() {
		String s = "";
		for (Glyph child : children) {
			s += ((Chara)child).c;
		}
		return s;
	}

	public String getString(int offset) {
		return getString().substring(offset);
	}

}
