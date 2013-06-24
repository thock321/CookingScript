package script;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.util.Timer;

public class Paint {
	
	public static String STATUS = "";
	
	private static int startXP;
	
	public static void setStartXP(int startXP) {
		Paint.startXP = startXP;
	}
	
	private static Timer timeRan = new Timer(0);
	
	private static Image cursor = getCursor();
	
	private static Image getCursor() {
		try {
			return ImageIO.read(new URL("http://cur.cursors-4u.net/user/images1/use3.png"));
		} catch (IOException e) {
			return null;
		}
	}
	
	private static int xpPerHour() {
		return (int) ((Skills.getExperience(Skills.COOKING) - startXP)  * 3600000D / timeRan.getElapsed());
	}
	
	public static void setPaint(Graphics graphic) {
		Graphics2D g = (Graphics2D) graphic;
		g.drawString("Time ran: " + timeRan.toElapsedString(), 13, 100);
		g.drawString("XP per hour: " + Integer.toString(xpPerHour()), 13, 115);
		g.drawString("XP Gained: " + Integer.toString(Skills.getExperience(Skills.FIREMAKING) - startXP), 13, 130);
		g.drawString("State: " + STATUS, 13, 145);
		g.drawImage(cursor, Mouse.getX() - 31, Mouse.getY() - 31, null);
	}

}
