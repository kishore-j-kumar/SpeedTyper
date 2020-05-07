import java.util.List;
import java.util.Random;
import java.awt.*;
import java.awt.geom.*;

/** The Word class represents a word on the screen and
 * manages word-specific characteristics such as location
 * and equality.
 * @author: Kishore Kumar
 */
public class Word {

    private static final int WORD_BUFFER = 160;
	private static final int WARNING_BOUND = 600;
	private static final int DANGER_BOUND = 300;
	private static final int FONT_SIZE = 25;
	private static final String FONT = "Bahaus 93";
	private String wrd = "";
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int x;
	private int y;
	private int speed;
	private Rectangle2D fontBoundary;
	private Rectangle boundary;
	private Color c = Color.GREEN;

    /**
     * Word constructor
     * @param challengeLevel speed at which word goes
     * @param words List of all possible words
     */
	public Word(int challengeLevel, List<String> words) {
		this.x = 0;
		this.y = (int)(Math.random() * this.screenSize.getHeight()) + WORD_BUFFER;
		this.speed = challengeLevel;
		Random yourRandom = new Random();
		this.wrd = words.get(yourRandom.nextInt(words.size()));
	}

    /**
     * Updates word location and draws it.
     * @param g Graphics component
     */
	void draw(Graphics g) {
		this.x += this.speed;
		if (this.x > this.screenSize.getWidth() - WARNING_BOUND) {
			this.c = Color.yellow;
		}
		if (this.x > this.screenSize.getWidth() - DANGER_BOUND) {
			this.c = Color.red;
		}
		g.setColor(this.c);
		g.setFont(new Font(FONT, Font.PLAIN, FONT_SIZE));
		FontMetrics fm = g.getFontMetrics();
		this.fontBoundary = fm.getStringBounds(this.wrd, g);
		g.drawString(this.wrd, this.x, this.y);
		this.boundary = new Rectangle(this.x, this.y - (int)this.fontBoundary.getHeight(),
				(int)this.fontBoundary.getWidth(), (int)this.fontBoundary.getHeight());
	}

  /**
   *
   * @return Rectangle with same bounds as Word.
   */
	public Rectangle getBounds() {
		return this.boundary;
	}

    /**
     * Checks if two words are equal by comparing contents.
     * @param toCheck the String val of the word to compare it with
     * @return true if two "words" are the same
     */
	public boolean checkEqual(String toCheck) {
		if (toCheck.toLowerCase().equals(this.wrd)) {
			return true;
		}
		return false;
	}
}
