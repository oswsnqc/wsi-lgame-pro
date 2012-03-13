package org.loon.wsi.screen;

import org.loon.framework.android.game.action.avg.AVGDialog;
import org.loon.framework.android.game.action.avg.AVGScreen;
import org.loon.framework.android.game.action.avg.command.Command;
import org.loon.framework.android.game.core.graphics.component.LMessage;
import org.loon.framework.android.game.core.graphics.component.LSelect;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.wsi.TitleScreen;

public class AVGScreen_01 extends AVGScreen {

	public AVGScreen_01() {
		super("assets/script/script_01.txt", AVGDialog.getRMXPDialog("assets/w6.png", 760, 250));
	}

	@Override
	public void onLoading() {
		
	}

	@Override
	public void drawScreen(LGraphics g) {
		
	}

	@Override
	public boolean nextScript(String message) {
		return true;
	}

	@Override
	public void onSelect(String message, int type) {
		
	}

	@Override
	public void initMessageConfig(LMessage message) {
		
	}

	@Override
	public void initSelectConfig(LSelect select) {
		
	}

	@Override
	public void initCommandConfig(Command command) {
		
	}

	@Override
	public void onExit() {
		setScreen(new TitleScreen());
	}

}
