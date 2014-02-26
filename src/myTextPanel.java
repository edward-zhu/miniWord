import com.sun.tools.javac.util.Pair;
import document.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodRequests;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;

/**
 * Created by EdwardZhu on 14-2-21.
 */
public class myTextPanel extends JPanel
		implements
		KeyListener, InputMethodListener,
		InputMethodRequests, MouseListener,
		DocumentListener, MouseMotionListener
{
	String text = "";
	Row row;
	Point a, b, dragged;
	Pos p1, p2;
	Caret caret;
	Document doc;
	Selection selection = null;
	Selection inputSelection = null;

	@Override
	public void documentChanged(Document doc, Pos pos) {
		caret.setPos(pos);
		repaint();
	}


	class Caret {
		public Pos getPos() {
			return pos;
		}

		public void setPos(Pos pos) {
			this.pos = pos;
		}

		Pos pos;

		Caret(int row, int offset) {
			pos = new Pos(row, offset);
		}

		public void setLocation(int x, int y) {
			doc.getPos(new Point(x, y));
		}

		public void setLocation(Point p) {
			doc.getPos(p);
		}

		public void setPos(int row, int offset) {
			pos.setRow(row);
			pos.setOffset(offset);
		}

		public void paint(Graphics g) {
			Point p = pos.posToPoint(doc);
			if (p != null) {
				g.drawLine((int)p.getX(), (int)p.getY() - g.getFontMetrics().getHeight(), (int)p.getX(), (int)p.getY());
			}
		}

		public Point getPoint() {
			return pos.posToPoint(doc);
		}

		public int getOffset() {
			return pos.getOffset();
		}

		public int getRow() {
			return pos.getRow();
		}
	}

	public myTextPanel() {
		caret = new Caret(0, 0);
		this.setFocusable(true);
		this.enableInputMethods(true);
		this.addInputMethodListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);

		doc = new Document(20, 20, 5, 1000);
		doc.setListener(this);
		System.out.println(doc.getChildren().size());

	}

	/*
		Selection Handler
	 */

	private void paintSelection(Graphics g, Selection sel) {
		if (sel != null) {
			for (Glyph child : sel.getItems()) {
				g.setColor(new Color(50, 200, 255, 100));
				Rectangle bd = child.getBound();
				g.fillRect((int)bd.getX(), (int)bd.getY(), (int)bd.getWidth(), (int)bd.getHeight());
			}
		}
	}

	/*
		JPanel Overrides
	 */

	@Override
	protected void paintComponent(Graphics g) {
		System.out.println("绘制");
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		this.getGraphics().setFont(new Font("Courier", Font.PLAIN, 14));
		if (doc.getSize() == 0) {
			doc.addRow(g);
		}
		doc.paint(g);
		caret.paint(g);
		paintSelection(g, selection);
		paintSelection(g, inputSelection);
	}

	/*
		KeyListener Implements
	 */
	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if(keyCode == KeyEvent.VK_BACK_SPACE) {
			if (selection != null) {
				doc.removeSelection(selection);
				selection = null;
			}else {
				doc.remove(caret.getPos());
			}

			//repaint();
		} else if(keyCode == KeyEvent.VK_ENTER) {
			if (doc.atRowEnd(caret.getPos())) {
				doc.addRowAt(this.getGraphics(), caret.getPos().getRow() + 1);
			}
			else {
				doc.newLine(this.getGraphics(), caret.getPos());
			}
		} else if (keyCode == KeyEvent.VK_DELETE) {
			if (selection != null) {
				doc.removeSelection(selection);
				selection = null;
				//doc.remove(caret.getPos());
			}else {
				doc.remove_back(caret.getPos());
			}

		} else if(keyCode == KeyEvent.VK_LEFT) {
			System.out.printf("(%d, %d)\n", caret.getRow(), caret.getOffset());
			if (caret.getOffset() > 0) {
				caret.setPos(caret.getRow(), caret.getOffset() - 1);
				repaint();
			}
			else {
				if (caret.getRow() > 0) {
					caret.setPos(caret.getRow() - 1, doc.getRow(caret.getRow() - 1).getSize());
					repaint();
				}
			}
		} else if(keyCode == KeyEvent.VK_RIGHT) {
			if (caret.getOffset() < doc.getRow(caret.getRow()).getSize()) {
				caret.setPos(caret.getRow(), caret.getOffset() + 1);
				repaint();
			} else {
				if (doc.getSize() - 1 > caret.getRow()) {
					caret.setPos(caret.getRow() + 1, 0);
					repaint();
				}
			}
		} else if (keyCode == KeyEvent.VK_UP) {
			if (caret.getRow() > 0) {
				Row row = doc.getRow(caret.getRow() - 1);
				Pos pos = caret.getPos();
				Point p = pos.posToPoint(doc);
				int offset = row.getOffset(p.x + 2);
				pos.setRow(pos.getRow() - 1);
				caret.setPos(caret.getRow(), offset);
				System.out.printf("offset : %d\n", offset);
				repaint();
			}
		} else if (keyCode == KeyEvent.VK_DOWN) {
			if (caret.getRow() < doc.getSize() - 1) {
				Row row = doc.getRow(caret.getRow() + 1);
				Pos pos = caret.getPos();
				// pos.setOffset(pos.getOffset() + 1);
				Point p = pos.posToPoint(doc);
				int offset = row.getOffset(p.x + 2);
				pos.setRow(pos.getRow() + 1);
				caret.setPos(caret.getRow(), offset);
				System.out.printf("offset : %d\n", offset);
				repaint();
			}
		} else if (keyCode == KeyEvent.VK_F && e.isControlDown()) {
			String str = JOptionPane.showInputDialog("请输入要搜索的字符串：");
			ArrayList<Pair<Pos, Pos>> results = doc.find(str);
			System.out.println("<---搜索结果--->");
			for (Pair<Pos, Pos> result : results) {
				System.out.println(result.fst + " -> " + result.snd);
			}
		} else if (this.getFont().canDisplay(e.getKeyChar())) {
			doc.addChara(this.getGraphics(), e.getKeyChar(), caret.getPos());
		}

	}

	@Override
	public void keyReleased(KeyEvent keyEvent) {

	}


	/*
		InputMethodRequest / Listener Implements
	 */

	@Override
	public InputMethodRequests getInputMethodRequests() {
		return this;
	}

	@Override
	public void inputMethodTextChanged(InputMethodEvent event) {
		int committedCharacterCount = event.getCommittedCharacterCount();
		AttributedCharacterIterator text = event.getText();
		StringBuffer sb = new StringBuffer();
		if (inputSelection != null) {
			doc.removeSelection(inputSelection);
			inputSelection = null;
		}
		if (committedCharacterCount > 0) {

			if (text != null) {
				char c;
				int toCopy = committedCharacterCount;
				c = text.first();
				while (toCopy-- > 0) {
					doc.addChara(this.getGraphics(), c, caret.getPos());
					sb.append(c);
					c = text.next();
				}
			}
		}
		else {

			String s = new String();
			for (char c = text.first();c != text.DONE;) {
				s += c;
				c = text.next();
			}
			inputSelection = doc.getInputSelection(this.getGraphics(), s, caret.getPos());
			Pos p2 = inputSelection.getP2();
			caret.setPos(new Pos(p2.getRow(), p2.getOffset() + 1));
			p2 = null;
			repaint();
		}

		//repaint();

	}

	@Override
	public void caretPositionChanged(InputMethodEvent i) {

	}

	@Override
	public Rectangle getTextLocation(TextHitInfo t) {
		Point p = getLocationOnScreen();
		Point cp = caret.getPoint();
		return new Rectangle((int)p.getX() + (int)cp.getX(), (int)p.getY() + (int)cp.getY() + this.getFontMetrics(this.getFont()).getHeight(), 0, 0);
	}

	@Override
	public TextHitInfo getLocationOffset(int i, int i2) {
		return null;
	}

	@Override
	public int getInsertPositionOffset() {
		return 0;
	}

	@Override
	public AttributedCharacterIterator getCommittedText(int i, int i2, Attribute[] attributes) {
		return null;
	}

	@Override
	public int getCommittedTextLength() {
		return 0;
	}

	@Override
	public AttributedCharacterIterator cancelLatestCommittedText(Attribute[] attributes) {
		return null;
	}

	@Override
	public AttributedCharacterIterator getSelectedText(Attribute[] attributes) {
		return null;
	}

	/*
		MouseEvent Implement
	 */

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == e.BUTTON3) {
			JPopupMenu popup = new JPopupMenu();
			JMenuItem item = new JMenuItem("Copy");
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionEvent) {
					System.out.println("Menuitem Clicked.");
				}
			});
			popup.add(item);
			Point p = caret.getPoint();
			popup.show(this, (int) p.getX(), (int) p.getY());
		}
		else if (e.getButton() == e.BUTTON1){
			selection = null;
			System.out.printf("Mouse %d %d\n", e.getX(), e.getY());
			Pos pos = doc.getPos(e.getPoint());
			System.out.printf("Mouse pos : %d %d", pos.getRow(), pos.getOffset());
			caret.setPos(pos);
			repaint();
		}


	}

	@Override
	public void mousePressed(MouseEvent e) {
		a = e.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		b = e.getPoint();
		Pos pa, pb;
		pa = doc.getPos(a);
		pb = doc.getPos(b);
	}

	@Override
	public void mouseEntered(MouseEvent mouseEvent) {

	}

	@Override
	public void mouseExited(MouseEvent mouseEvent) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// System.out.printf("Mouse dragged ( %d,%d )\n", e.getX(), e.getY());
		dragged = e.getPoint();
		p1 = doc.getPos(a);
		p2 = doc.getPos(dragged);
		// if(p2.getOffset() > 0) p2.setOffset(p2.getOffset() - 1);
		System.out.println("Sel Pos : " + p1 + " " + p2);
		selection = doc.getSelection(p1, p2);
		if (p1.formerThan(p2)) {
			caret.setPos(p2.getRow(), p2.getOffset() >= doc.getRow(p2.getRow()).getSize() ? p2.getOffset() : p2.getOffset() + 1);
		}
		else if(p1.equalsTo(p2)) {
			caret.setPos(p2.getRow(), p2.getOffset() + 1);
		}
		else {
			caret.setPos(p2);
		}

		System.out.printf("Selection Length %d Bound %s -> %s\n",selection.size(), p1, p2);
		repaint();


	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}
}
