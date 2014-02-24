package document;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by EdwardZhu on 14-2-22.
 */


public class Document extends Glyph{
	int margin;
	int maxWidth;

	public DocumentListener getListener() {
		return listener;
	}

	public void setListener(DocumentListener listener) {
		this.listener = listener;
	}

	DocumentListener listener;

	public Document(int x, int y, int margin, int maxWidth) {
		super(x, y);
		children = new LinkedList<Glyph>();
		this.margin = margin;
		this.maxWidth = maxWidth;
	}

	public Row getRow(Point p) {
		for (Glyph child : children) {
			Rectangle bound = child.getBound();
			if (bound.getY() <= p.getY() &&
					bound.getY() + bound.getHeight() >= p.getY()) {
				return (Row)child;
			}
		}
		return null;
	}

	public Row getRow(int index) {
		return (Row)children.get(index);
	}

	public int getRowIndex(Point p) {
		int i = 0;
		if (p.getY() < getBound().getY()) return 0;
		if (p.getY() > getBound().getY() + getBound().getHeight()) return getSize() - 1;
		for (Glyph child : children) {
			Rectangle bound = child.getBound();
			if (bound.getY() <= p.getY() &&
					bound.getY() + bound.getHeight() >= p.getY()) {
				return i;
			}
			i++;
		}
		return 0;
	}

	public Pos getPos(Point p) {
		int row = getRowIndex(p);
		int offset = getRow(row).getOffset(p);
		return new Pos(row, offset);
	}

	public int getRowsHeight(int row) {
		int height = 0, i = 0;
		for (Iterator iter = children.iterator();iter.hasNext() && i < row;) {
			height += ((Row)iter.next()).getBound().getHeight();
			i++;
		}
		return height;
	}

	public Row addRow(int height) {
		int x = (int)getBound().getX();
		int y = (int)getBound().getY();
		int new_width = (int)getBound().getWidth();
		int new_height = (int)getBound().getHeight() + height + margin;
		getBound().setSize(new_width, new_height);
		Row row = new Row(x, y, 0, height + margin, maxWidth, margin);
		children.add(row);
		return row;
	}

	public Row addRow(Graphics g) {
		return addRowAt(g, children.size());
	}

	public Row addRow(Graphics g, String s) {
		return addRowAt(g, s, children.size());
	}

	public Row addRowAt(Graphics g, int index) {
		int x = (int)getBound().getX();
		int y = (int)getBound().getY() + getRowsHeight(index);
		Row row = new Row(g, x, y, maxWidth, margin);
		children.add(index, row);
		listener.documentChanged(this, new Pos(index, 0));
		getBound().setSize((int)getBound().getWidth(), (int)getBound().getHeight() + g.getFontMetrics().getHeight());
		return row;
	}

	public Row addRowAt(Graphics g, String s, int index) {
		int x = (int)getBound().getX();
		int y = (int)getBound().getY() + getRowsHeight(index);
		Row row = new Row(g, s, maxWidth, margin, x, y);

		for (Iterator iter = children.listIterator(index); iter.hasNext();) {
			Row r = (Row) iter.next();
			r.setVerticalLocation(r.getBound().y + row.getBound().width);
		}
		children.add(index, row);
		listener.documentChanged(this, new Pos(index, 0));
		getBound().setSize((int)getBound().getWidth(), (int)getBound().getHeight() + g.getFontMetrics().getHeight());
		return row;
	}

	public void addChara(Graphics g, char c, Pos pos) {
		System.out.printf("add %c at %d, %d\n", c, pos.getRow(), pos.getOffset());
		try {
			Row row = getRow(pos.getRow());
			row.add(g, c, pos.getOffset());
			pos.setOffset(pos.getOffset() + 1);
			listener.documentChanged(this, pos);
		}
		catch(Row.RowOutOfRoomException ex) {
			System.out.println("行出界。");
		}
	}

	public void newLine(Graphics g, Pos pos) {
		Row row = getRow(pos.getRow());
		addRowAt(g, row.getString(pos.getOffset()), pos.getRow() + 1);
		row.removeAll(pos.getOffset(), row.getSize());
		listener.documentChanged(this, new Pos(pos.getRow() + 1, 0));
	}

	public void remove(Pos pos) {
		Row row = getRow(pos.getRow());
		if (row.getSize() == 0) {
			if (getSize() > 1) {
				removeRow(pos.getRow());
				listener.documentChanged(this, new Pos(pos.getRow() - 1, getRow(pos.getRow() - 1).getSize()));
			}
		}
		else if (pos.getOffset() == 0){
			if (pos.getRow() > 0) {
				Row rowToInsert = getRow(pos.getRow() - 1);
				Row rowToDelete = getRow(pos.getRow());
				for (Glyph child : rowToDelete.children) {
					rowToInsert.add((Chara) child);
				}
				removeRow(pos.getRow());
				listener.documentChanged(this, new Pos(pos.getRow() - 1, getRow(pos.getRow() - 1).getSize()));
			}
		}
		else {
			getRow(pos.getRow()).remove(pos.getOffset());
			if(pos.offset > 0) pos.setOffset(pos.getOffset() - 1);
			listener.documentChanged(this, pos);
		}
		System.out.println(pos);

	}

	public void remove(Chara chara) {
		Row row = (Row)chara.getParent();
		remove(new Pos(children.indexOf(row), row.children.indexOf(chara) + 1));
	}

	private void removeRow(int index) {
		Row row = (Row)children.remove(index);
		int height = (int)row.getBound().getHeight();
		if (index < getSize()) {
			for (Iterator iter = children.listIterator(index); iter.hasNext();) {
				Row r = (Row)iter.next();
				r.print();
				Rectangle bd = r.getBound();
				r.setVerticalLocation((int)bd.getY() - height);
				r.print();
			}
		}
		System.out.println("Row Size: " + getSize());
		getBound().setSize((int)getBound().getWidth(), (int) (getBound().getHeight() - height));

	}

	private void removeRow(Row row) {
		removeRow(children.indexOf(row));
	}

	public void removeSelection(Selection sel) {
		for (Glyph glyph : sel.items) {
			if (glyph instanceof Row) {
				removeRow((Row)glyph);
			}
			else if (glyph instanceof Chara){
				remove((Chara)glyph);
			}
		}
		if (sel.getP1().rowDiff(sel.getP2()) != 0) {
			Pos former = Pos.former(sel.getP1(), sel.getP2());
			Row nextRow = getRow(former.getRow() + 1);
			Row formerRow = getRow(former.getRow());
			for (Iterator iter = nextRow.children.iterator(); iter.hasNext();) {
				formerRow.add((Chara) iter.next());
			}
			removeRow(nextRow);
		}

		listener.documentChanged(this, Pos.former(sel.p1, sel.p2));
	}

	public Selection getSelection(Pos p1, Pos p2) {
		ArrayList<Glyph> selection = new ArrayList<Glyph>();

		if (p1.getRow() == p2.getRow()) {
			int max = p1.getOffset() > p2.getOffset() ? p1.getOffset() : p2.getOffset();
			int min = p1.getOffset() < p2.getOffset() ? p1.getOffset() : p2.getOffset();
			if (max >= getRow(p1.getRow()).getSize()) {
				max = max - 1;
			}
			for (int i = min; i <= max; i++) {
				selection.add(getRow(p1.getRow()).children.get(i));
			}
		}
		else if (p1.getRow() != p2.getRow()) {
			Row minRow, maxRow;
			int minRowIndex, minRowOffset, maxRowIndex, maxRowOffset;
			if (p1.getRow() > p2.getRow()) {
				maxRow = getRow(p1.getRow());
				maxRowIndex = p1.getRow();
				minRow = getRow(p2.getRow());
				minRowIndex = p2.getRow();
				maxRowOffset = p1.getOffset();
				minRowOffset = p2.getOffset();
			}
			else {
				maxRow = getRow(p2.getRow());
				maxRowIndex = p2.getRow();
				minRow = getRow(p1.getRow());
				minRowIndex = p1.getRow();
				maxRowOffset = p2.getOffset();
				minRowOffset = p1.getOffset();
			}
			System.out.printf("maxRow : %d offset : %d minRow : %d offset : %d\n", maxRowIndex, maxRowOffset, minRowIndex, minRowOffset);
			for (int i = minRowOffset; i < minRow.getSize(); i++) {
				selection.add(minRow.children.get(i));
			}
			for (int i = minRowIndex + 1; i < maxRowIndex; i++) {
				selection.add(children.get(i));
			}
			if (maxRowOffset >= maxRow.getSize()) {
				maxRowOffset--;
			}
			for (int i = 0; i <= maxRowOffset; i++) {
				selection.add(maxRow.children.get(i));
			}
		}

		return new Selection(p1, p2, selection);
	}

	public Selection getInputSelection(Graphics g, String s, Pos pos) {
		ArrayList<Glyph> items = new ArrayList<Glyph>();
		Row row = getRow(pos.getRow());
		for (int i = 0; i < s.length(); i++) {
			try {
				items.add(row.add(g, s.charAt(i), pos.getOffset() + i));
			}
			catch (Row.RowOutOfRoomException ex) {

			}
		}
		Pos endPos = new Pos(pos.getRow(), pos.getOffset() + s.length() - 1);
		return new Selection(pos, endPos, items);
	}

	public boolean atRowEnd(Pos p) {
		if (getRow(p.getRow()).getSize() == p.getOffset()) {
			return true;
		}
		return false;
	}


	public LinkedList getChildren() {
		return children;
	}

	@Override
	public void paintChildren(Graphics g) {
		for (Glyph child : children) {
			child.paint(g);
		}
	}

	@Override
	public void paintComponent(Graphics g) {

	}
}
