package org.loon.wsi;

import org.loon.framework.android.game.core.graphics.Screen;
import org.loon.framework.android.game.core.graphics.component.LButton;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.core.timer.LTimerContext;
import org.loon.wsi.screen.AVGScreen_01;
import org.loon.wsi.screen.AVGScreen_02;

public class MapScreen extends Screen {

	LButton title_01, title_02, title_03, title_04, title_05, title_06;
	
	@Override
	public void onLoad() {
		this.setBackground("assets/mapScreen.png");

		title_01 = new LButton("assets/mapScreen_1.png"){						// title_01
			public void doClick(){
				setScreen(new AVGScreen_01());
			}
		};
		title_01.setLocation(178, 275);
		add(title_01);
		
		title_02 = new LButton("assets/mapScreen_1.png"){						// title_02
			public void doClick(){
				setScreen(new AVGScreen_02());
			}
		};
		title_02.setLocation(title_01.getX() + title_01.getWidth() + 120, 275);
		add(title_02);
		
	}

	@Override
	public void draw(LGraphics g) {
		
	}

	@Override
	public void alter(LTimerContext timer) {
		
	}

	@Override
	public void onTouchDown(LTouch e) {
		
	}

	@Override
	public void onTouchUp(LTouch e) {
		
	}

	@Override
	public void onTouchMove(LTouch e) {
		
	}

}
