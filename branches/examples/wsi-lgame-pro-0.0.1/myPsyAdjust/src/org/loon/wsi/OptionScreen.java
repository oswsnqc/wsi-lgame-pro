package org.loon.wsi;

import org.loon.framework.android.game.core.graphics.Screen;
import org.loon.framework.android.game.core.graphics.Screen.LTouch;
import org.loon.framework.android.game.core.graphics.component.LButton;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.core.timer.LTimerContext;
import org.loon.wsi.screen.AVGScreen_01;
import org.loon.wsi.screen.AVGScreen_02;

public class OptionScreen extends Screen {
LButton rotation_title, rotation_bg, rotation_on, rotation_off;
LButton btnUpload;
	
	@Override
	public void onLoad() {
		this.setBackground("assets/back2.png");

		rotation_title = new LButton("assets/m_vibrate.png", "", 100, 40) {						// title_01
			public void doClick(){
				
			}
		};
		rotation_title.setLocation(2, 50);
		add(rotation_title);
		
		rotation_bg = new LButton("assets/m_rotation.png", "", 100, 40) {						// title_01
			public void doClick(){
				
			}
		};
		rotation_bg.setLocation(2, rotation_title.getY() + rotation_title.getHeight() + 20);
		add(rotation_bg);

		rotation_on = new LButton("assets/m_rotation_on.png", "", 100, 40) {						// title_01
			public void doClick(){
				rotation_on.setVisible(true);
				rotation_off.setVisible(false);
			}
		};
		rotation_on.setLocation(2+200,  rotation_title.getY() + rotation_title.getHeight() + 20);
		rotation_on.setVisible(false);
		add(rotation_on);

		rotation_off = new LButton("assets/m_rotation_off.png", "", 100, 40) {						// title_01
			public void doClick(){
				rotation_on.setVisible(false);
				rotation_off.setVisible(true);
			}
		};
		rotation_off.setLocation(2+200,  rotation_title.getY() + rotation_title.getHeight() + 20);
		rotation_off.setVisible(true);
		add(rotation_off);

		btnUpload = new LButton("assets/m_upload.png", "", 100, 40) {						// title_01
			public void doClick(){
			}
		};
		btnUpload.setLocation(2, rotation_bg.getY() + rotation_bg.getHeight() + 20);
		btnUpload.setVisible(true);
		add(btnUpload);
	}

	@Override
	public void draw(LGraphics g) {
	}

	@Override
	public void alter(LTimerContext timer) {
		if (isOnLoadComplete()) {
			
			
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
