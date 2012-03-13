package org.loon.wsi;

import org.loon.framework.android.game.core.LSystem;
import org.loon.framework.android.game.core.graphics.Screen;
import org.loon.framework.android.game.core.graphics.component.LButton;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.core.timer.LTimerContext;

public class MainScreen extends Screen {

	LButton btnStart, btnOption, btnExit;
	
	@Override
	public void onLoad() {
		this.setBackground("assets/back1.png");
		
		btnStart = new LButton("assets/btnStart.png", 50, 40){					// start
			public void doClick(){
//				setScreen(new TitleScreen());
				setScreen(new MapScreen());
			}
		};
		btnStart.setLocation(2, 5);
		add(btnStart);

		btnOption = new LButton("assets/btnOption.png", 100, 40){				// option
			public void doClick(){
				setScreen(new OptionScreen());
			}
		};
		btnOption.setLocation(2, btnStart.getY() + btnStart.getHeight() + 20);
		add(btnOption);

		btnExit = new LButton("assets/btnExit.png", 100, 40){					// exit
			public void doClick(){
				LSystem.exit();
			}
		};
		btnExit.setLocation(2, btnOption.getY() + btnOption.getHeight() + 20);
		add(btnExit);
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
