package vazkii.zeta.client.event;

import net.minecraft.client.gui.screens.Screen;
import vazkii.zeta.event.bus.Cancellable;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZScreenMousePressed extends IZetaPlayEvent, Cancellable {
	Screen getScreen();
	int getButton();
	double getMouseX();
	double getMouseY();

	interface Pre extends ZScreenMousePressed { }
	interface Post extends ZScreenMousePressed { }
}