import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

public class Screen extends JPanel implements KeyListener, MouseListener {
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private boolean started = false;

	private static final int EASY_DIFFICULTY = 1;
	private static final int EASY_KEYCODE = 49;
	private static final int MED_DIFFICULTY = 2;
	private static final int MED_KEYCODE = 50;
	private static final int HARD_DIFFICULTY = 4;
	private static final int HARD_KEYCODE = 51;
	private static final int DEMON_DIFFICULTY = 8;
	private static final int DEMON_KEYCODE = 57;
	private static final int EN_KEYCODE = 52;
	private static final int FR_KEYCODE = 53;
	private static final int IT_KEYCODE = 54;
	private static final int HOME_KEY = 36;

	private static final int MAX_WORDS_ON_SCREEN = 25;
	private static final int NORMAL_FONT = 50;
	private static final int INPUT_XY = 10;
	private static final int START_DELTA = 1550;
	private static final int START_DELTA_SPECIAL = 1500;
	private static final int STANDARD_SPACING = 4;
	private static final int HALF_SPACING = 2;
	private static final int TEXT_BOX_OFFSET = 70;
	private static final int WPM_OFFSET = 2500;
	private static final double SECONDS_PM = 60;
	private static final double MS_PER_S = 1000;
	private static final double AVG_CPW = 4.5;
	private static final double SCREEN_CLEARANCE = 50;
	private static final int TIME_BOX_WIDTH = 148;
	private static final String FONT_NAME = "Arial";
	private static final int POSSIBLE_MISSES = 10;
	private static final int BUFFER_TIME = 10;
	private static final int WORD_BUFFER_COUNT = 55;
	private static final int MAX_EMPTY_WORDS = 2;

	private static final int SS_RANK = 5000;
	private static final int SS_ADD = 20;
	private static final int S_RANK = 4500;
	private static final int S_ADD = 10;
	private static final int A_RANK = 4000;
	private static final int A_ADD = 9;
	private static final int B_RANK = 3500;
	private static final int B_ADD = 8;
	private static final int C_RANK = 3000;
	private static final int C_ADD = 7;
	private static final int D_RANK = 2500;
	private static final int D_ADD = 6;
	private static final int E_RANK = 2000;
	private static final int E_ADD = 5;
	private static final int F_RANK = 1500;
	private static final int F_ADD = 4;
	private static final int SMALL_RANK = 1000;
	private static final int SMALL_ADD = 3;
	private static final int WEAK_RANK = 500;
	private static final int WEAK_ADD = 2;

	private static final int ESC_KEY = 27;
	private static final int SMALLEST_LOWER = 96;
	private static final int BIGGEST_LOWER = 123;
	private static final int SMALLEST_UPPER = 64;
	private static final int BIGGEST_UPPER = 91;
	private static final int SPACE_KEY = 32;
	private static final int BACKSPACE_KEY = 8;

	private static final String EN_DICT = "http://www.mit.edu/~ecprice/wordlist.10000";
	private static final String FR_DICT = "https://raw.githubusercontent.com/giacomodrago/ruzzlesolverpro/master/languages/French/dictionary.txt";
	private static final String IT_DICT = "https://raw.githubusercontent.com/mccwdev/bip39-dutch-wordlist/master/wordlist/italian.txt";

	private String dictionaryLink = EN_DICT;
	private int missed;
	private int score;
	private Word[] words;
	private JLabel currentWord;
	private JLabel statsOne;
	private JLabel statsTwo;
	private JLabel statsThree;
	private JLabel beginLabel;
	private ArrayList<String> wordList;
	private ArrayList<Integer> avg = new ArrayList<Integer>();
	private int cnt = 0;
	private int challengeLevel = 1;
	private int initLevel = 0;
	private boolean gameOver = false;
	private long timeAtStart;
	private String currString = "";

	public Screen() {
		words = new Word[MAX_WORDS_ON_SCREEN];
		for (int i = 0; i < this.words.length; i++)
		this.words[i] = null;
		missed = 0;
		score = 0;
		setLayout(null);
		setTitleScreen();
		setGameLabels();
		addKeyListener(this);
		addMouseListener(this);
	}

	private void instantiateWordList() {
		this.wordList = new ArrayList<String>();
		try {
			URL url = new URL(dictionaryLink);
			URLConnection urlc = url.openConnection();
			urlc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0a2) Gecko/20110613 Firefox/6.0a2");
			BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				this.wordList.add(line);
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void setTitleScreen() {
		this.beginLabel = new JLabel("<html><center><bold>[SpeedTyper]</bold></center><center>Press SPACE to begin</center><center>Choose difficulty:" +
		" 1(Easy)-3(Hard)</center><center>Choose Language: 4-EN, 5-FR, 6-IT</center><center>Hit ESC to exit</center><html>", SwingConstants.CENTER);
		add(this.beginLabel);
		this.beginLabel.setForeground(Color.white);
		this.beginLabel.setFont(new Font(FONT_NAME, Font.PLAIN, NORMAL_FONT));
		this.beginLabel.setBounds(0, 0, screenSize.width, screenSize.height);
		setFocusable(true);
	}

	private void setGameLabels() {
		this.statsOne = new JLabel("Score: " + this.score + " || Missed: " + this.missed, STANDARD_SPACING);
		this.statsOne.setForeground(Color.black);
		this.statsOne.setFont(new Font(FONT_NAME, Font.PLAIN, NORMAL_FONT));
		Dimension sizeTwo = this.statsOne.getPreferredSize();
		this.statsOne.setBounds((int)this.screenSize.getWidth() - START_DELTA, INPUT_XY, START_DELTA_SPECIAL, sizeTwo.height);
		add(this.statsOne);
		this.statsTwo = new JLabel("Time used: 0.0s", STANDARD_SPACING);
		this.statsTwo.setForeground(Color.black);
		this.statsTwo.setFont(new Font(FONT_NAME, Font.PLAIN, NORMAL_FONT));
		Dimension sizeThree = this.statsTwo.getPreferredSize();
		this.statsTwo.setBounds((int)this.screenSize.getWidth() - START_DELTA, STANDARD_SPACING + TEXT_BOX_OFFSET, START_DELTA_SPECIAL, sizeThree.height);
		add(this.statsTwo);
		this.statsThree = new JLabel("Words Per Minute: 0 word(s)", HALF_SPACING);
		this.statsThree.setForeground(Color.black);
		this.statsThree.setFont(new Font(FONT_NAME, Font.PLAIN, NORMAL_FONT));
		Dimension sizeFour = this.statsThree.getPreferredSize();
		this.statsThree.setBounds(INPUT_XY, STANDARD_SPACING + TEXT_BOX_OFFSET, WPM_OFFSET, sizeFour.height);
		add(this.statsThree);
		this.currentWord = new JLabel("[" + this.currString + "]");
		this.currentWord.setForeground(Color.black);
		this.currentWord.setFont(new Font(FONT_NAME, Font.PLAIN, NORMAL_FONT));
		Dimension sizeOne = this.currentWord.getPreferredSize();
		this.currentWord.setBounds(INPUT_XY, INPUT_XY, START_DELTA_SPECIAL, sizeOne.height);
		add(this.currentWord);
	}

	public void paintComponent(Graphics g) {
		if (!this.started) {
			updateTitle(g);
			buffer();
		}
		else if (this.started && !this.gameOver) {
			updateGameplay(g);
			gameLogic();
			buffer();
		}
		else if (this.gameOver) {
			updateEnd(g);
		}
	}

	private void updateTitle(Graphics g) {
		this.beginLabel.setText("<html><center><bold>[SpeedTyper]</bold></center><center>Press SPACE to begin</center><center>Choose difficulty:" +
		" 1(Easy)-3(Hard)</center><center>Choose Language: 4-EN, 5-FR, 6-IT</center><center>Hit ESC to exit</center><html>");
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, (int)Main.ScreenSize.getWidth(), (int)Main.ScreenSize.getHeight());
	}

	private void updateGameplay(Graphics g) {
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, (int)Main.ScreenSize.getWidth(), (int)Main.ScreenSize.getHeight());
		g.setColor(Color.blue);
		g.fillRect(0, 0, (int)Main.ScreenSize.getWidth(), TIME_BOX_WIDTH);
		long timeElapsedMS = System.currentTimeMillis();
		long tDelta = timeElapsedMS - this.timeAtStart;
		double elapsedSeconds = tDelta / MS_PER_S;
		int cpm = (int)(SECONDS_PM * this.score / elapsedSeconds / AVG_CPW);
		this.avg.add(Integer.valueOf(cpm));
		DecimalFormat stringFormat = new DecimalFormat("#.00");
		this.statsOne.setText("Score: " + this.score + " || Missed: " + this.missed);
		this.statsTwo.setText("Time used: " + stringFormat.format(elapsedSeconds) + "s");
		this.statsThree.setText("Words Per Minute: " + cpm + " word(s)");
		for (int i = 0; i < this.words.length; i++) {
			if (this.words[i] != null) {
				this.words[i].draw(g);
			}
		}
	}

	private void updateEnd(Graphics g) {
		int total = 0;
		for (int i = 0; i < this.avg.size(); i++) {
			total += ((Integer)this.avg.get(i)).intValue();
		}
		int totalAvg = total / this.avg.size();
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, (int)Main.ScreenSize.getWidth(), (int)Main.ScreenSize.getHeight());
		this.currentWord.setForeground(Color.black);
		this.statsOne.setForeground(Color.black);
		this.statsTwo.setForeground(Color.black);
		this.statsThree.setForeground(Color.black);
		this.beginLabel.setText("<html><center>Game Over!</center><center>Score: " + this.score + "</center><center>Average Words Per Minute: "
				+ totalAvg + "</center><center>ESC to exit, Home to replay</center><center>Hit 9 on title for DEMON difficulty!</center></html>");
	}

	private void gameLogic() {
		wordLogic();
		if (this.missed >= POSSIBLE_MISSES) {
			this.gameOver = true;
		}
		challengeLogic();
		generateWords();
	}

	private void wordLogic() {
		for (int i = 0; i < this.words.length; i++) {
			if (this.words[i] != null) {
				if (this.words[i].getBounds().getX() > this.screenSize.getWidth()) {
					this.missed++;
					this.words[i] = null;
				}
				else if (((this.words[i] != null)) && ((this.words[i].getBounds().getY()
				> this.screenSize.getHeight() - SCREEN_CLEARANCE))) {
					this.words[i] = null;
				}
			}
		}
		for (int i = 0; i < this.words.length; i++) {
			if (this.words[i] != null && this.words[i].checkEqual(this.currString)) {
				this.score += (this.challengeLevel) * (this.currString.length() + 1);
				this.currString = "";
				this.currentWord.setText("[" + this.currString + "]");
				this.words[i] = null;
			}
		}
	}

	private void challengeLogic() {
		if (this.score >= SS_RANK) {
			this.challengeLevel = this.initLevel + SS_ADD;
		}
		else if (this.score >= S_RANK) {
			this.challengeLevel = this.initLevel + S_ADD;
		}
		else if (this.score >= A_RANK) {
			this.challengeLevel = this.initLevel + A_ADD;
		}
		else if (this.score >= B_RANK) {
			this.challengeLevel = this.initLevel + B_ADD;
		}
		else if (this.score >= C_RANK) {
			this.challengeLevel = this.initLevel + C_ADD;
		}
		else if (this.score >= D_RANK) {
			this.challengeLevel = this.initLevel + D_ADD;
		}
		else if (this.score >= E_RANK) {
			this.challengeLevel = this.initLevel + E_ADD;
		}
		else if (this.score >= F_RANK) {
			this.challengeLevel = this.initLevel + F_ADD;
		}
		else if (this.score >= SMALL_RANK) {
			this.challengeLevel = this.initLevel + SMALL_ADD;
		}
		else if (this.score >= WEAK_RANK) {
			this.challengeLevel = this.initLevel + WEAK_ADD;
		}
	}

	private void buffer() {
		try {
			Thread.sleep(BUFFER_TIME);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		repaint();
	}

	private void generateWords() {
		this.cnt++;
		if(makeWordsOnBuffer()) {
			return;
		}
		checkWordIntersection();
		fillArrayWhenDepleted();
	}

	private boolean makeWordsOnBuffer() {
		if (this.cnt % WORD_BUFFER_COUNT == 0) {
			for (int i = 0; i < this.words.length; i++) {
				if (this.words[i] == null) {
					this.words[i] = new Word(this.challengeLevel, this.wordList);
					return true;
				}
			}
		}
		return false;
	}

	private void checkWordIntersection() {
		for (int i = 0; i < this.words.length; i++) {
			if (this.words[i] != null) {
				for (int j = 0; j < this.words.length; i = j++) {
					if (this.words[j] != null && this.words[i] != null
					&& i != j && this.words[j].getBounds().intersects(this.words[i].getBounds())) {
						this.words[j] = null;
					}
				}
			}
		}
	}

	private void fillArrayWhenDepleted() {
		int nullCnt = 0;
		for (int i = 0; i < this.words.length; i++) {
			if (this.words[i] == null) {
				nullCnt++;
			}
		}
		if (nullCnt < MAX_EMPTY_WORDS) {
			for (int i = 0; i < this.words.length; i++) {
				if (this.words[i] == null) {
					this.words[i] = new Word(this.challengeLevel, this.wordList);
				}
			}
		}
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == ESC_KEY) {
			System.exit(0);
		}
		else {
			if (e.getKeyCode() == BACKSPACE_KEY && this.currString.length() > 0) {
				this.currString = this.currString.substring(0, this.currString.length() - 1);
				this.currentWord.setText("[" + this.currString + "]"); return;
			}
			if ((e.getKeyCode() > SMALLEST_UPPER && e.getKeyCode() < BIGGEST_UPPER)
			|| (e.getKeyCode() > SMALLEST_LOWER && e.getKeyCode() < BIGGEST_LOWER)) {
				this.currString = String.valueOf(this.currString) + e.getKeyChar();
				this.currentWord.setText("[" + this.currString + "]");
			}
		}
		if(!started) {
			checkSettings(e);
		}
		if (e.getKeyCode() == SPACE_KEY) {
			instantiateWordList();
			this.timeAtStart = System.currentTimeMillis();
			this.started = true;
			this.beginLabel.setText("");
			this.currentWord.setForeground(Color.white);
			this.statsOne.setForeground(Color.white);
			this.statsTwo.setForeground(Color.white);
			this.statsThree.setForeground(Color.white);
		}
		if (e.getKeyCode() == HOME_KEY) {
			Main.reset();
		}
	}

	private void checkSettings(KeyEvent e) {
		if (e.getKeyCode() == EASY_KEYCODE) {
			challengeLevel = initLevel = EASY_DIFFICULTY;
		}
		else if (e.getKeyCode() == MED_KEYCODE) {
			challengeLevel = initLevel = MED_DIFFICULTY;
		}
		else if (e.getKeyCode() == HARD_KEYCODE) {
			challengeLevel = initLevel = HARD_DIFFICULTY;
		}
		else if (e.getKeyCode() == DEMON_KEYCODE) {
			challengeLevel = initLevel = DEMON_DIFFICULTY;
		}
		else if (e.getKeyCode() == EN_KEYCODE) {
			dictionaryLink = EN_DICT;
		}
		else if (e.getKeyCode() == FR_KEYCODE) {
			dictionaryLink = FR_DICT;
		}
		else if (e.getKeyCode() == IT_KEYCODE) {
			dictionaryLink = IT_DICT;
		}
	}

	public void keyReleased(KeyEvent e) {}

	public void keyTyped(KeyEvent e) {}

	public void mouseClicked(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}
}
