import java.util.List;
import java.util.Random;
import java.awt.*;
import java.awt.geom.*;

public class Word {
    private static final int WORD_BUFFER = 160;
    private static final int WORD_RESTART_BUFFER = 300;
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

    public Word(int challengeLevel, List<String> words) {
        this.x = 0;
        this.y = (int)(Math.random() * this.screenSize.getHeight()) + WORD_BUFFER;
        this.speed = challengeLevel;
        Random yourRandom = new Random();
        this.wrd = words.get(yourRandom.nextInt(words.size()));
    }

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

    public Rectangle getBounds()
  {
  return this.boundary;
  }

    public boolean checkEqual(String toCheck) {
        if (toCheck.toLowerCase().equals(this.wrd)) {
            return true;
        }
    return false;
    }
}
