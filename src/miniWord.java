import document.Chara;

import java.util.LinkedList;

/**
 * Created by EdwardZhu on 14-2-21.
 */
public class miniWord {
	static mainWindow mainWin;
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainWin = new mainWindow();
			}
		});
	}
}