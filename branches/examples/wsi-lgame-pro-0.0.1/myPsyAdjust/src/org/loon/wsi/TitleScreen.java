package org.loon.wsi;

import org.loon.framework.android.game.core.graphics.Screen;
import org.loon.framework.android.game.core.graphics.component.LButton;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.core.timer.LTimerContext;
import org.loon.wsi.screen.AVGScreen_01;
import org.loon.wsi.screen.AVGScreen_02;

public class TitleScreen extends Screen{

	LButton title_01, title_02, title_03, title_04, title_05, title_06;
	
	@Override
	public void onLoad() {
		this.setBackground("assets/back2.png");
		
		title_01 = new LButton("assets/title_01.png", 100, 40){						// title_01
			public void doClick(){
				setScreen(new AVGScreen_01());
			}
		};
		title_01.setLocation(2, 5);
		add(title_01);
		
		title_02 = new LButton("assets/title_02.png", 200, 40){						// title_02
			public void doClick(){
				setScreen(new AVGScreen_02());
			}
		};
		title_02.setLocation(2, title_01.getY() + title_01.getHeight() + 20);
		add(title_02);
		
		title_03 = new LButton("assets/title_03.png", 100, 40){						// title_03
			public void doClick(){
				
			}
		};
		title_03.setLocation(2, title_02.getY() + title_02.getHeight() + 20);
		add(title_03);
		
		title_04 = new LButton("assets/title_04.png", 150, 40){						// title_04
			public void doClick(){
			}
		};
		title_04.setLocation(2, title_03.getY() + title_03.getHeight() + 20);
		add(title_04);

		title_05 = new LButton("assets/title_05.png", 150, 40){						// title_05
			public void doClick(){
			}
		};
		title_05.setLocation(2, title_04.getY() + title_04.getHeight() + 20);
		add(title_05);

		title_06 = new LButton("assets/title_06.png", 200, 40){						// title_06
			public void doClick(){
			}
		};
		title_06.setLocation(2, title_05.getY() + title_05.getHeight() + 20);
		add(title_06);
	}

	@Override
	public void draw(LGraphics g) {
		
	}

	@Override
	public void alter(LTimerContext timer) {
		if (isOnLoadComplete()) {
			
			title_01.setEnabled(true);				// 设定开始按钮可用
			title_02.setEnabled(true);				// 设定结束按钮可用
		}
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
