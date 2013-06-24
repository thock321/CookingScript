package script.nodes;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.Entity;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.SceneObject;

import script.CookingScript;
import script.Paint;

public class BankingNode extends Node {

	@Override
	public boolean activate() {
		if (CookingScript.getToCook() == null) {
			CookingScript.setToCook(Inventory.getItem(new Filter<Item>() {

				@Override
				public boolean accept(Item arg0) {
					return arg0.getName().toLowerCase().contains("raw") && arg0.getName().toLowerCase().contains(CookingScript.FOOD_NAME.toLowerCase());
				}
				
			}));
		}
		return CookingScript.getToCook() == null || !Inventory.contains(CookingScript.getToCook().getId());
	}
	
	private static class BankEntity implements Locatable, Entity {
		
		private Entity bankObject;
		
		private BankEntity(Entity bankObject) {
			this.bankObject = bankObject;
		}
		
		private double getDistanceFrom(Locatable loc) {
			return Calculations.distance((Locatable) bankObject, loc);
		}
		
		private static BankEntity getNearestBankEntity(Locatable loc) {
			Locatable nearestBankObject = SceneEntities.getNearestTo(loc, new Filter<SceneObject>() {

				@Override
				public boolean accept(SceneObject arg0) {
					return arg0 != null && arg0.getDefinition() != null && arg0.getDefinition().getName().toLowerCase().contains("bank");
				}
				
			});
			return nearestBankObject != null ? new BankEntity((Entity) nearestBankObject) : getNearestBanker(loc);
		}
		
		private static BankEntity getNearestBanker(final Locatable loc) {
			NPC[] npcs = NPCs.getLoaded(new Filter<NPC>() {

				@Override
				public boolean accept(NPC arg0) {
					return arg0 != null && Calculations.distance(arg0, loc) < 30;
				}
				
			});
			BankEntity[] bankers = new BankEntity[npcs.length];
			for (int i = 0; i < bankers.length; i++) {
				bankers[i] = new BankEntity(npcs[i]);
			}
			BankEntity closestBanker = bankers[0];
			for (int i = 0; i < bankers.length; i++) {
				if (bankers[i].getDistanceFrom(loc) < closestBanker.getDistanceFrom(loc)) {
					closestBanker = bankers[i];
				}
			}
			return closestBanker;
		}

		@Override
		public Tile getLocation() {
			return ((Locatable) bankObject).getLocation();
		}

		@Override
		public boolean contains(Point arg0) {
			return bankObject.contains(arg0);
		}

		@Override
		public Point getCentralPoint() {
			return bankObject.getCentralPoint();
		}

		@Override
		public Point getNextViewportPoint() {
			return bankObject.getNextViewportPoint();
		}

		@Override
		public boolean validate() {
			return bankObject.validate();
		}

		@Override
		public boolean click(boolean arg0) {
			return bankObject.click(arg0);
		}

		@Override
		public void draw(Graphics arg0) {
			bankObject.draw(arg0);
		}

		@Override
		public Polygon[] getBounds() {
			return bankObject.getBounds();
		}

		@Override
		public boolean hover() {
			return bankObject.hover();
		}

		@Override
		public boolean interact(String arg0) {
			return bankObject.interact(arg0);
		}

		@Override
		public boolean interact(String arg0, String arg1) {
			return bankObject.interact(arg0, arg1);
		}

		@Override
		public boolean isOnScreen() {
			return bankObject.isOnScreen();
		}
		
	}
	
	private BankEntity bank;

	@Override
	public void execute() {
		bank = BankEntity.getNearestBankEntity(Players.getLocal());
		if (bank == null)
			return;
		if (Bank.isOpen()) {
			Paint.STATUS = "Banking.";
			if (CookingScript.getToCook() == null) {
				CookingScript.setToCook(Bank.getItem(new Filter<Item>() {

					@Override
					public boolean accept(Item arg0) {
						return arg0.getName().toLowerCase().contains("raw") && arg0.getName().toLowerCase().contains(CookingScript.FOOD_NAME.toLowerCase());
					}
					
				}));
				if (CookingScript.getToCook() == null)
					return;
			}
			if (Inventory.getCount() > 0) {
				Bank.depositInventory();
				Task.sleep(100, 300);
			}
			Bank.withdraw(CookingScript.getToCook().getId(), 28);
			Paint.STATUS = "Closing bank.";
			Bank.close();
			Task.sleep(100, 300);
		} else {
			if (!bank.isOnScreen()) {
				Paint.STATUS = "Turning camera towards bank.";
				Camera.turnTo(bank);
				Task.sleep(1000, 1400);
			}
			if (!bank.isOnScreen()) {
				Paint.STATUS = "Walking to bank.";
				Walking.walk(bank);
				while (Players.getLocal().isMoving() && !bank.isOnScreen()) {
					Task.sleep(20);
				}
			}
			Paint.STATUS = "Opening bank.";
			Bank.open();
			while (Players.getLocal().isMoving()) {
				Task.sleep(20);
			}
			Task.sleep(300, 600);
		}
	}

}
