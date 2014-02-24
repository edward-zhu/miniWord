package document;

import java.awt.*;

/**
 * Created by EdwardZhu on 14-2-22.
 */
public interface DocumentListener {
	public void documentChanged(Document doc, Pos pos);
}
