package document;

import java.awt.*;

/**
 * Created by EdwardZhu on 14-2-22.
 *
 * Pos
 * --------------------------
 * 位置表示类
 *
 */
public class Pos {
	int row, offset;

	public Pos(int row, int offset) {
		this.row = row;
		this.offset = offset;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public Point posToPoint(Document doc) {

		Pos pos = this;
		int x = (int)doc.getBound().getX();
		int y = (int)doc.getBound().getY();
		if (doc.getChildren().size() > 0) {
			for (int i = 0; i <= pos.row; i++) {
				y += doc.children.get(i).getBound().getHeight();
			}
		}
		Row row = (Row)doc.children.get(pos.row);
		if (row.children.size() > 0){
			for (int i = 0; i < pos.offset; i++) {
				x += row.children.get(i).getBound().getWidth();
			}
		}


		return new Point(x, y);
	}

	@Override
	public String toString() {
		return "[" + row + " , " + offset + "]";
	}

	public static boolean formerThan(Pos p1, Pos p2) {
		if (p1.getRow() < p2.getRow()) {
			return true;
		}
		else if (p1.getRow() == p2.getRow()) {
			if (p1.getOffset() < p2.getOffset()) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	public boolean formerThan(Pos p) {
		return Pos.formerThan(this, p);
	}

	public static Pos former(Pos p1, Pos p2) {
		if (p1.formerThan(p2)) {
			return p1;
		}
		else {
			return p2;
		}
	}

	public static Pos latter(Pos p1, Pos p2) {
		if (p1.formerThan(p2)) {
			return p2;
		}
		else {
			return p1;
		}
	}

	public boolean equalsTo(Pos p) {
		return this.getOffset() == p.getOffset() && this.getRow() == p.getRow();
	}

	public int rowDiff(Pos p) {
		return this.getRow() - p.getRow();
	}
}
