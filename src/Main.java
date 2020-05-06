import java.awt.*;
import javax.swing.*;

public class Main 
{
	static Dimension ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int EXTEND_FRAME = 6;
	public static final int CLOSE_OP = 3;
  
	static JFrame frame;
	
	public static void main(String[] args) 
	{
		frame = new JFrame();
	    Screen ScreenObject = new Screen();
	    frame.add(ScreenObject);
	    frame.setExtendedState(EXTEND_FRAME);
	    frame.setUndecorated(true);
	    frame.setSize(ScreenSize);
	    frame.setVisible(true);  
	    frame.setDefaultCloseOperation(CLOSE_OP);
	}
	
	public static void reset()
	{
		frame = new JFrame();
		Screen ScreenObject = new Screen();
	    frame.add(ScreenObject);
	    frame.setExtendedState(EXTEND_FRAME);
	    frame.setUndecorated(true);
	    frame.setSize(ScreenSize);
	    frame.setVisible(true);  
	    frame.setDefaultCloseOperation(CLOSE_OP);
	}
}
