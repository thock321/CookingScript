package script;

import java.awt.Graphics;

import javax.swing.SwingUtilities;

import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.core.script.job.state.Tree;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.input.Mouse.Speed;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.wrappers.node.Item;

import fs.nodes.BankingNode;

import script.nodes.CookingNode;

@Manifest(authors = { "Thock321" }, description = "Cooks food.", name = "Thock's Cooker")
public class CookingScript extends ActiveScript implements PaintListener {
	
	private static Item toCook;
	
	public static String FOOD_NAME;
	
	private Tree nodeTree = new Tree(new Node[] {new CookingNode(), new BankingNode()});
	
	@Override
	public void onStart() {
		Mouse.setSpeed(Speed.NORMAL);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new StartupGUI().setVisible(true);
			}
			
		});
	}
	
	private Node activeNode;

	@Override
	public int loop() {
		if (Game.getClientState() != Game.INDEX_MAP_LOADED || FOOD_NAME == null)
			return 1000;
		activeNode = nodeTree.state();
		if (activeNode != null) {
			nodeTree.set(activeNode);
			getContainer().submit(activeNode);
			activeNode.join();
		}
		return Random.nextInt(25, 50);
	}

	@Override
	public void onRepaint(Graphics arg0) {
		Paint.setPaint(arg0);
	}

	public static Item getToCook() {
		return toCook;
	}

	public static void setToCook(Item toCook) {
		CookingScript.toCook = toCook;
	}

}
