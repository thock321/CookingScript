package script.nodes;

import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

import script.CookingScript;
import script.Paint;

public class CookingNode extends Node {
	
	private static WidgetChild COOK_BUTTON = Widgets.get(1371, 5);
	private static WidgetChild CANCEL_BUTTON = Widgets.get(1251,18);
    private static WidgetChild COOK_BOX = Widgets.get(1371,0);
    private static WidgetChild ACTION_BAR = Widgets.get(640,4);
    private static WidgetChild MINIMISE_BUTTON = Widgets.get(640,30);

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
		return CookingScript.getToCook() != null && Inventory.contains(CookingScript.getToCook().getId()) && Players.getLocal().getAnimation() == -1
				&& !CANCEL_BUTTON.visible();
	}

	@Override
	public void execute() {
		if (ACTION_BAR.visible()) {
			Paint.STATUS = "Minimising action bar.";
			MINIMISE_BUTTON.interact("Minimise");
			Task.sleep(200, 300);
		}
		
		if (COOK_BOX.visible()) {
			Paint.STATUS = "Clicking cook button.";
			COOK_BUTTON.click(true);
			Task.sleep(500, 600);
			Paint.STATUS = "Cooking...";
		} else {
		
			SceneObject cooker = SceneEntities.getNearestTo(Players.getLocal(), new Filter<SceneObject>() {
	
				@Override
				public boolean accept(SceneObject arg0) {
					return arg0 != null && arg0.getDefinition() != null && (arg0.getId() == 76295 && arg0.getLocation().getY() == 3185 || arg0.getDefinition().getName().toLowerCase().contains("fire") || 
							arg0.getDefinition().getName().toLowerCase().contains("range"));
				}
				
			});
			if (cooker == null) {
				Paint.STATUS = "No range or fire close by.  Please walk near one manually.";
				return;
			}
			if (!cooker.isOnScreen()) {
				Paint.STATUS = "Turning camerea to cooker.";
				Camera.turnTo(cooker);
				Task.sleep(1000, 1400);
			}
			if (!cooker.isOnScreen()) {
				Paint.STATUS = "Walking to cooker.";
				Walking.walk(cooker);
				while (Players.getLocal().isMoving() && !cooker.isOnScreen()) {
					Task.sleep(20);
				}
			}
			Paint.STATUS = "Selecting raw food.";
			Inventory.getItem(CookingScript.getToCook().getId()).getWidgetChild().interact("Use");
			if (!Inventory.isItemSelected())
				Inventory.getItem(CookingScript.getToCook().getId()).getWidgetChild().interact("Use");
			Task.sleep(100, 300);
			Paint.STATUS = "Using cooker.";
			cooker.click(false);
			Task.sleep(100, 200);
			Menu.select("Use", cooker.getDefinition().getName());
			while (Players.getLocal().isMoving()) {
				Task.sleep(20);
			}
			Task.sleep(1000, 1500);
		}
	}

}
