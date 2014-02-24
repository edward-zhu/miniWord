package document;

import java.awt.*;

/**
 * Created by EdwardZhu on 14-2-22.
 */
public class Chara extends Glyph {
	char c;

	public Chara(char c, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.c = c;
	}

	public Chara(char c) {
		this(c, 0, 0, 0, 0);
	}

	@Override
	public void paintChildren(Graphics g) {
		return;
	}

	@Override
	public void paintComponent(Graphics g) {
		char[] ch = {c};
		g.drawChars(ch, 0, 1, (int)bound.getX(), (int)bound.getY() + (int)bound.getHeight());
		//g.drawRect((int)bound.getX(), (int)bound.getY(), (int)bound.getWidth(), (int)bound.getHeight());
	}
}
