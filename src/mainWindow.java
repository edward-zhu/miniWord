import javax.swing.*;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;

/**
 * Created by EdwardZhu on 14-2-21.
 */

public class mainWindow extends JFrame{
	private JPanel rootPanel;
	private JButton button1;
	private myTextPanel textPanel;

	public mainWindow() {
		setContentPane(rootPanel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);

	}

}
