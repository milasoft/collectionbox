package milasoft.api;

import java.util.ArrayList;
import java.util.List;

import org.dreambot.api.methods.MethodContext;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;

/**
 * Methods for interacting with the collection box.
 * @author Milasoft
 */
public class CollectionBox  {
	
	public static final int INTERFACE_ID = 402;
	public static final int WITHDRAW_INVENTORY = 3;
	public static final int WITHDRAW_BANK = 4;
	public static final int CLOSE_PARENT = 2;
	public static final int CLOSE_CHILD = 11;
	public static final int ITEM_BOX = 3;
	public static final int COIN_BOX = 4;

	MethodContext c;
	
	/**
	 * Represents a slot in the collection box. Stores the widget id of the slot.
	 */
	private enum Slot {
		ONE(5), TWO(6), THREE(7), FOUR(8), FIVE(9), SIX(10), SEVEN(11), EIGHT(12);
		
		public static final Slot values[] = values();
		private int widgetId;
		
		Slot(int widgetId) {
			this.widgetId = widgetId;
		}
		
		/**
		 * Returns the widget id of the slot.
		 * @return the widget id of the slot.
		 */
		public int getWidgetId() {
			return widgetId;
		}
	}
	
	public CollectionBox(MethodContext c) {
		this.c = c;
	}
	
	/**
	 * Checks if there are any items in the collection box.
	 * @return True if the collection box is not empty. False if it is empty.
	 */
	public boolean hasItemsToCollect() {
		return !all().isEmpty();
	}
	
	/**
	 * Returns the item in the selected slot.
	 * @param slot The slot number to check.
	 * @return the item in the selected slot.
	 */
	public Item getItemInSlot(int slot) {
		return getSlotWidget(slot).getChild(ITEM_BOX).getItem();
	}
	
	/**
	 * Returns the coins in the selected slot.
	 * @param slot The slot number to check.
	 * @return the coins in the selected slot.
	 */
	public Item getCoinsInSlot(int slot) {
		return getSlotWidget(slot).getChild(COIN_BOX).getItem();
	}
	
	/**
	 * Checks if there is an item in the selected slot.
	 * @param slot The slot number to check.
	 * @return True if there is an item in the slot. False if there isn't an item in the slot.
	 */
	public boolean hasItemInSlot(int slot) {
		return getItemInSlot(slot) != null && getItemInSlot(slot).getID() != -1;
	}
	
	/**
	 * Checks if there is any coins in the selected slot.
	 * @param slot The slot number to check.
	 * @return True if there is any coins in the slot. False if there isn't any coins in the slot.
	 */
	public boolean hasCoinsInSlot(int slot) {
		return getCoinsInSlot(slot) != null && getCoinsInSlot(slot).getID() != -1;
	}
	
	/**
	 * Returns a list of all items in the collection box.
	 * @return a list of all items in the collection box.
	 */
	public List<Item> all() {
		List<Item> itemList = new ArrayList<Item>();
		for(int i = 1; i < 9; i++) {
			Item item = getItemInSlot(i);
			if(item != null && item.getID() != -1) {
				itemList.add(item);
			}
		}
		return itemList;
	}
	
	/**
	 * Opens the collection box.
	 * @return True if the collection box is open. False if it failed to open.
	 */
	public boolean open() {
		GameObject bankBooth = c.getGameObjects().closest("Bank booth", "Bank chest");
		if(bankBooth != null) {
			if(bankBooth.interact("Collect")) {
				MethodContext.sleepUntil(() -> isOpen(), 2500);
				return isOpen();
			}
		}
		return false;
	}
	
	/**
	 * Checks if the collection box is open.
	 * @return True if the collection box is open. False if it is closed.
	 */
	public boolean isOpen() {
		return getInterface() != null && getInterface().isVisible();
	}
	
	/**
	 * Closes the collection box.
	 * @return True if the collection box is closed. False if it failed to close.
	 */
	public boolean close() {
		if(isOpen()) {
			getCloseButton().interact();
			MethodContext.sleepUntil(() -> !isOpen(), 2000);
		}
		return !isOpen();
	}
	
	/**
	 * Withdraw everything from the collection box.
	 * @param toInventory True to withdraw to inventory. False to withdraw to bank.
	 * @return True if successfully withdrew all items.
	 */
	public boolean withdrawAll(boolean toInventory) {
		if(toInventory) {
			getInventoryButton().interact();			
		} else {
			getBankButton().interact();
		}
		return !hasItemsToCollect();
	}
	
	/**
	 * Withdraws the item from the selected slot.
	 * @param slot The slot to withdraw from.
	 * @param toInventory True to withdraw to inventory. False to withdraw to bank.
	 * @return True if successfully withdrew item.
	 */
	public boolean withdrawItem(int slot, boolean toInventory) {
		if(toInventory) {
			getSlotWidget(slot).getChild(ITEM_BOX).interact();
		} else {
			getSlotWidget(slot).getChild(ITEM_BOX).interact("Bank");
		}
		MethodContext.sleepUntil(() -> !hasItemInSlot(slot), 2500);
		return !hasItemInSlot(slot);
	}
	
	/**
	 * Withdraws the coins from the selected slot.
	 * @param slot The slot to withdraw from.
	 * @param toInventory True to withdraw to inventory. False to withdraw to bank.
	 * @return True if successfully withdrew coins.
	 */
	public boolean withdrawCoins(int slot, boolean toInventory) {
		if(toInventory) {
			getSlotWidget(slot).getChild(COIN_BOX).interact();
		} else {
			getSlotWidget(slot).getChild(COIN_BOX).interact("Bank");
		}
		MethodContext.sleepUntil(() -> !hasItemInSlot(slot), 2500);
		return !hasItemInSlot(slot);
	}
	
	private Widget getInterface() {
		return c.getWidgets().getWidget(INTERFACE_ID);
	}
	
	private WidgetChild getSlotWidget(int slot) {
		return c.getWidgets().getWidgetChild(INTERFACE_ID, Slot.values[slot-1].getWidgetId());
	}
	
	private WidgetChild getInventoryButton() {
		return getInterface().getChild(WITHDRAW_INVENTORY);
	}
	
	private WidgetChild getBankButton() {
		return getInterface().getChild(WITHDRAW_BANK);
	}
	
	private WidgetChild getCloseButton() {
		return getInterface().getChild(CLOSE_PARENT).getChild(CLOSE_CHILD);
	}
}