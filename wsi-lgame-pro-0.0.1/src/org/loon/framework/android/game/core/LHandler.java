package org.loon.framework.android.game.core;

import java.util.LinkedList;

import org.loon.framework.android.game.LGameAndroid2DActivity;
import org.loon.framework.android.game.LGameAndroid2DView;
import org.loon.framework.android.game.LMode;
import org.loon.framework.android.game.core.geom.RectBox;
import org.loon.framework.android.game.core.graphics.LColor;
import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.core.graphics.Screen;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.core.timer.LTimerContext;
import org.loon.framework.android.game.media.AssetsSoundManager;
import org.loon.framework.android.game.media.PlaySoundManager;
import org.loon.framework.android.game.utils.GraphicsUtils;

import android.R.integer;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project 	:	wsi-lgame-pro 
 * @author 	:	yanggang, chenpeng
 * @email 	:	yanggang2050@gmail.com
 * @site		:	http://code.google.com/p/wsi-lgame-pro/
 * @version 	:	v-0.0.1
 */

public class LHandler {

	/**
	 * 自适应屏幕的宽和高， 宽 ： deviceWidth； 高 ： deviceHeight
	 */
	private int deviceWidth, deviceHeight;

	/**
	 * 当前屏幕使用的宽和高， 宽 ： widht； 高 ： height
	 */
	private int width, height;
	
	private LGameAndroid2DActivity activity;
	private Context context;

	/**
	 * android.view.Window
	 */
	private android.view.Window window;
	/**
	 * android.view.WindowManager
	 */
	private android.view.WindowManager windowManager;

	private AssetsSoundManager assertSoundManager;
	private PlaySoundManager playSoundManager;

	private LGameAndroid2DView gameView;
	/**
	 * Screen 实例
	 */
	private Screen currentScreen;
	private LinkedList<Screen> screens;

	private boolean isInstance;

	private LTransition transition;
	private boolean waitTransition;

	public LHandler(LGameAndroid2DActivity activity, LGameAndroid2DView view, boolean isLandscape, LMode mode) {
		try {

			this.activity = activity;

			if (isLandscape) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			} else {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}

			this.context = activity.getApplicationContext();
			this.window = activity.getWindow();
			this.windowManager = activity.getWindowManager();
			this.gameView = view;
			this.screens = new LinkedList<Screen>();

			RectBox d = getScreenDimension();
			this.deviceWidth  = d.getWidth();
			this.deviceHeight = d.getHeight();

			LSystem.isSCREEN_LANDSCAPE = isLandscape;	// 横竖屏， true - 横屏； false - 竖屏
			if (isLandscape && (d.getWidth() > d.getHeight())) {			// 横屏
				this.deviceWidth  = d.getWidth();
				this.deviceHeight = d.getHeight();
			} else if (isLandscape && (d.getWidth() < d.getHeight())) {
				this.deviceHeight = d.getWidth();
				this.deviceWidth  = d.getHeight();
			} else if (!isLandscape && (d.getWidth() < d.getHeight())) {	// 竖屏
				this.deviceWidth  = d.getWidth();
				this.deviceHeight = d.getHeight();
			} else if (!isLandscape && (d.getWidth() > d.getHeight())) {
				this.deviceHeight = d.getWidth();
				this.deviceWidth  = d.getHeight();
			}

			if (mode != LMode.Max) {
				if (isLandscape) {
					this.width  = LSystem.MAX_SCREEN_WIDTH;
					this.height = LSystem.MAX_SCREEN_HEIGHT;
				} else {
					this.width  = LSystem.MAX_SCREEN_HEIGHT;
					this.height = LSystem.MAX_SCREEN_WIDTH;
				}
			} else {
				if (isLandscape) {		// 横屏
					this.width  = deviceWidth >= LSystem.MAX_SCREEN_WIDTH ? LSystem.MAX_SCREEN_WIDTH : deviceWidth;
					this.height = deviceHeight >= LSystem.MAX_SCREEN_HEIGHT ? LSystem.MAX_SCREEN_HEIGHT : deviceHeight;
				} else {
					this.width  = deviceWidth >= LSystem.MAX_SCREEN_HEIGHT ? LSystem.MAX_SCREEN_HEIGHT : deviceWidth;
					this.height = deviceHeight >= LSystem.MAX_SCREEN_WIDTH ? LSystem.MAX_SCREEN_WIDTH : deviceHeight;
				}
			}

			if (mode == LMode.Fill) {

				LSystem.scaleWidth = ((float) deviceWidth) / width;
				LSystem.scaleHeight = ((float) deviceHeight) / height;

			} else if (mode == LMode.FitFill) {

				RectBox res = GraphicsUtils.fitLimitSize(width, height, deviceWidth, deviceHeight);
				deviceWidth  = res.width;
				deviceHeight = res.height;
				LSystem.scaleWidth  = ((float) deviceWidth) / width;
				LSystem.scaleHeight = ((float) deviceHeight) / height;

			} else if (mode == LMode.Ratio) {

				deviceWidth  = View.MeasureSpec.getSize(deviceWidth);
				deviceHeight = View.MeasureSpec.getSize(deviceHeight);

				float userAspect = (float) width / (float) height;
				float realAspect = (float) deviceWidth / (float) deviceHeight;

				if (realAspect < userAspect) {
					deviceHeight = Math.round(deviceWidth / userAspect);
				} else {
					deviceWidth = Math.round(deviceHeight * userAspect);
				}

				LSystem.scaleWidth  = ((float) deviceWidth) / width;
				LSystem.scaleHeight = ((float) deviceHeight) / height;

			} else if (mode == LMode.MaxRatio) {

				deviceWidth = View.MeasureSpec.getSize(deviceWidth);
				deviceHeight = View.MeasureSpec.getSize(deviceHeight);

				float userAspect = (float) width / (float) height;
				float realAspect = (float) deviceWidth / (float) deviceHeight;

				if ((realAspect < 1 && userAspect > 1) || (realAspect > 1 && userAspect < 1)) {
					userAspect = (float) height / (float) width;
				}

				if (realAspect < userAspect) {
					deviceHeight = Math.round(deviceWidth / userAspect);
				} else {
					deviceWidth = Math.round(deviceHeight * userAspect);
				}

				LSystem.scaleWidth = ((float) deviceWidth) / width;
				LSystem.scaleHeight = ((float) deviceHeight) / height;

			} else {

				LSystem.scaleWidth = 1;
				LSystem.scaleHeight = 1;

			}

			LSystem.screenRect = new RectBox(0, 0, width, height);

			StringBuffer strBuffer = new StringBuffer();
			strBuffer.append("Mode:").append(mode);
			strBuffer.append("\nWidth:").append(width).append(", Height:" + height);
			strBuffer.append("\ndeviceWidth:").append(deviceWidth).append("; deviceHeight:" + deviceHeight);
			strBuffer.append("\nLSystem.scaleWidth:").append(LSystem.scaleWidth).append("; LSystem.scaleHeight:").append(LSystem.scaleHeight).append("; Scale:").append(isScale());
			Log.i("Android2DSize", strBuffer.toString());

		} catch (Exception ex) {
			Log.e("Android2DHandler", ex.getMessage());
			
		}
	}

	/**
	 * 判断当前游戏屏幕是否有拉伸
	 * 
	 * @return
	 */
	public boolean isScale() {
		return LSystem.scaleWidth != 1 || LSystem.scaleHeight != 1;
	}

	/**
	 * 返回AssetsSoundManager
	 * 
	 * @return
	 */
	public AssetsSoundManager getAssetsSound() {
		if (this.assertSoundManager == null) {
			this.assertSoundManager = AssetsSoundManager.getInstance();
		}
		return assertSoundManager;
	}

	/**
	 * 返回PlaySoundManager
	 * 
	 * @return
	 */
	public PlaySoundManager getPlaySound() {
		if (this.playSoundManager == null) {
			this.playSoundManager = new PlaySoundManager(context);
		}
		return playSoundManager;
	}

	/**
	 * 获得窗体实际坐标
	 * 
	 * @return
	 */
	public RectBox getScreenDimension() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("dm.density = " + dm.density);
		strBuffer.append("\ndm.xdpi = " + dm.xdpi).append("; dm.ydpi = " + dm.ydpi);
		strBuffer.append("\ndm.widthPixels = " + dm.widthPixels).append("; dm.heightPixels = " + dm.heightPixels);
		Log.i("Android2DSize", strBuffer.toString());
		
		return new RectBox((int) dm.xdpi, (int) dm.ydpi, (int) dm.widthPixels, (int) dm.heightPixels);
	}

	public int getRepaintMode() {
		if (isInstance) {
			return currentScreen.getRepaintMode();
		}
		return Screen.SCREEN_CANVAS_REPAINT;
	}

	public Bitmap getBackground() {
		if (isInstance) {
			return currentScreen.getBackground();
		}
		return null;
	}

	public boolean next() {
		if (isInstance) {
			if (currentScreen.next()) {
				return true;
			}
		}
		return false;
	}

	public void calls() {
		if (isInstance) {
			currentScreen.callEvents();
		}
	}

	public void runTimer(LTimerContext context) {
		if (isInstance) {
			if (waitTransition) {
				if (transition != null) {
					switch (transition.code) {
					case 1:
						if (!transition.completed()) {
							transition.update(context.timeSinceLastUpdate);
						} else {
							endTransition();
						}
						break;
					default:
						if (!currentScreen.isOnLoadComplete()) {
							transition.update(context.timeSinceLastUpdate);
						}
						break;
					}
				}
			} else {
				currentScreen.runTimer(context);
				return;
			}
		}
	}

	public void draw(LGraphics g) {
		if (isInstance) 
		{
			if (waitTransition) {
				if (transition != null) {
					if (transition.isDisplayGameUI) {
						currentScreen.createUI(g);
					}
					switch (transition.code) {
					case 1:
						if (!transition.completed()) {
							transition.draw(g);
						}
						break;
					default:
						if (!currentScreen.isOnLoadComplete()) {
							transition.draw(g);
						}
						break;
					}
				}
			} else {
				currentScreen.createUI(g);
				return;
			}
		}
	}

	public final void setTransition(LTransition t) {
		this.transition = t;
	}

	public final LTransition getTransition() {
		return this.transition;
	}

	private final void startTransition() {
		if (transition != null) {
			waitTransition = true;
			if (isInstance) {
				currentScreen.setLock(true);
			}
		}
	}

	private final void endTransition() {
		if (transition != null) {
			switch (transition.code) {
			case 1:
				if (transition.completed()) {
					waitTransition = false;
					transition.dispose();
				}
				break;
			default:
				waitTransition = false;
				transition.dispose();
				break;
			}
			if (isInstance) {
				currentScreen.setLock(false);
			}
		} else {
			waitTransition = false;
		}
	}

	public float getX() {
		if (isInstance) {
			return currentScreen.getX();
		}
		return 0;
	}

	public float getY() {
		if (isInstance) {
			return currentScreen.getY();
		}
		return 0;
	}

	public synchronized Screen getScreen() {
		return currentScreen;
	}

	public void runFirstScreen() {
		int size = screens.size();
		if (size > 0) {
			Object o = screens.getFirst();
			if (o != currentScreen) {
				setScreen((Screen) o, false);
			}
		}
	}

	public void runLastScreen() {
		int size = screens.size();
		if (size > 0) {
			Object o = screens.getLast();
			if (o != currentScreen) {
				setScreen((Screen) o, false);
			}
		}
	}

	public void runPreviousScreen() {
		int size = screens.size();
		if (size > 0) 
		{
			for (int i = 0; i < size; i++) 
			{
				if (currentScreen == screens.get(i)) 
				{
					if (i - 1 > -1) 
					{
						setScreen((Screen) screens.get(i - 1), false);
						return;
					}
				}
			}
		}
	}

	public void runNextScreen() {
		int size = screens.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (currentScreen == screens.get(i)) {
					if (i + 1 < size) {
						setScreen((Screen) screens.get(i + 1), false);
						return;
					}
				}
			}
		}
	}

	public void runIndexScreen(int index) {
		if (index >= 0 && index < screens.size()) {
			Object o = screens.get(index);
			if (currentScreen != o) {	// 当前 currentScreen 不是 screens.get(index)，则 setScreen()
				setScreen((Screen) screens.get(index), false);
			}
		}
	}

	public void addScreen(final Screen screen) {
		if (screen == null) {
			throw new RuntimeException("Cannot create a [IScreen] instance !");
		}
		screens.add(screen);
	}

	public Screen removeFirstScreen(){
		if(screens.getFirst() != null){
			return screens.removeFirst();
		}
		return null;
	}
	
	public Screen removeLastScreen(){
		if(screens.size() > 0){
			return screens.removeLast();
		}
		return null;
	}
	
	public Screen removeIndexScreen(int index){
		if(index >= 0 && index < screens.size()){
			return screens.remove(index);
		}
		return null;
	}
	
	/**
	 * 全屏初始化，全屏显示 FLAG_FULLSCREEN，没有标题栏 FEATURE_NO_TITLE
	 */
	public void initScreen() {
		window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		window.requestFeature(android.view.Window.FEATURE_NO_TITLE);
		try {
			window.setBackgroundDrawable(null);
		} catch (Exception e) {
		}
	}

	/**
	 * 返回 screens 的链表 LinkedList(Screen)
	 * 
	 * @return LinkedList(Screen)
	 */
	public LinkedList<Screen> getScreens() {
		return screens;
	}

	public int getScreenCount() {
		return screens.size();
	}

	/**
	 * 设置screen，默认添加 screen 到链表 LinkedList(Screen) 
	 * 
	 * @param screen	Screen 实例
	 */
	public void setScreen(final Screen screen) {
		setScreen(screen, true);
	}

	/**
	 * 设置显示 screen
	 * 
	 * @param screen	: 显示的 screen
	 * @param isAdd		: 是否添加 screen 到链表 LinkedList(Screen)
	 */
	public void setScreen(final Screen screen, boolean isAdd) {
		
		synchronized (this) {
			if (screen == null) {			// screen为空，初始化实例失败， this.isInstance = false
				this.isInstance = false;
				throw new RuntimeException("Cannot create a [Screen] instance !");
			}
			
			if (!LSystem.isLogo) {
				if (currentScreen != null) {
					setTransition(screen.onTransition());
					
				} else {
					LTransition transition = screen.onTransition();
					if (transition == null) {
						switch (LSystem.getRandomBetween(0, 3)) {
						case 0:
							transition = LTransition.newFadeIn();						// 渐变
							break;
						case 1:
							transition = LTransition.newArc();							// 弧度
							break;
						case 2:
							transition = LTransition.newSplitRandom(LColor.black);		// 从中间分割
							break;
						case 3:
							transition = LTransition.newCrossRandom(LColor.black);		// 从中间旋转
							break;
						}
					}
					setTransition(transition);
				}
			}
			
			screen.setOnLoadState(false);
			if (currentScreen == null) {
				currentScreen = screen;
			} else {
				synchronized (currentScreen) {
					currentScreen.destroy();
					currentScreen = screen;
				}
			}
			this.isInstance = true;		// 实例化成功
			
			if (screen instanceof EmulatorListener) {
				gameView.update();
				gameView.setEmulatorListener((EmulatorListener) screen);
			} else {
				gameView.setEmulatorListener(null);
			}
//			startTransition();
			screen.onCreate(LSystem.screenRect.width, LSystem.screenRect.height);
			Thread load = null;
			try {
				load = new Thread() {
					public void run() {
						for (; LSystem.isLogo;) {	// 有logo，才执行
							try {
								Thread.sleep(60);
							} catch (InterruptedException e) {
							}
						}
						screen.setClose(false);
						screen.onLoad();
						screen.setOnLoadState(true);
						screen.onLoaded();
						endTransition();
					}
				};
				load.setPriority(Thread.NORM_PRIORITY);
				load.start();	// 加载线程执行
			} catch (Exception ex) {
				throw new RuntimeException(currentScreen.getName() + " onLoad:" + ex.getMessage());
				
			} finally {
				load = null;
			}
			if (screen != null) {
				if (screen instanceof LFlickerListener) {
					setFlicker((LFlickerListener) screen);
				}
			}
			if (isAdd) {
				screens.add(screen);
			}
			Thread.yield();
		}
	}

	/**
	 * 获取屏幕screen的宽
	 * 
	 * @return width: 屏幕默认的宽，默认最大为 LSystem.MAX_SCREEN_WIDTH
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * 获取屏幕screen的高
	 * 
	 * @return height: 屏幕默认的高，默认最大为 LSystem.MAX_SCREEN_HEIGHT
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * 返回 view，即 LGameAndroid2DView 实例
	 * 
	 * @return 返回 view，即 LGameAndroid2DView 实例
	 */
	public View getView() {
		return gameView;
	}

	/**
	 * 获取屏幕的 screenRect，即 LSystem.screenRect
	 * 
	 * @return 获取屏幕的 screenRect，即 LSystem.screenRect
	 */
	public RectBox getScreenBox() {
		return LSystem.screenRect;
	}

	public LGameAndroid2DActivity getLGameActivity() {
		return activity;
	}

	public Context getContext() {
		return context;
	}

	/**
	 * 返回 android.view.Window 实例， 即 activity.getWindow()
	 * 
	 * @return 返回 android.view.Window 实例， 即 activity.getWindow()
	 */
	public Window getWindow() {
		return window;
	}

	/**
	 * 返回 android.view.WindowManager 实例，即 activity.getWindowManager()
	 * 
	 * @return 返回 android.view.WindowManager 实例，即 activity.getWindowManager()
	 */
	public WindowManager getWindowManager() {
		return windowManager;
	}

	private LFlicker flicker;

	private void setFlicker(LFlickerListener listener) {
		if (listener == null) {
			flicker = null;
			return;
		}
		if (flicker == null) {
			flicker = new LFlicker(listener);
		} else {
			flicker.setListener(listener);
		}
	}

	public boolean onTouchEvent(MotionEvent e) {
		if (isInstance) {
			try {
				if (flicker != null) {
					flicker.onTouchEvent(e);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				return currentScreen.onTouchEvent(e);
			} catch (Exception ex) {
			}
		}
		return false;

	}

	public boolean onKeyDown(int keyCode, KeyEvent e) {
		if (isInstance) {
			try {
				return currentScreen.onKeyDownEvent(keyCode, e);
			} catch (Exception ex) {
			}
		}
		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent e) {
		if (isInstance) {
			try {
				return currentScreen.onKeyUpEvent(keyCode, e);
			} catch (Exception ex) {
			}
		}
		return false;
	}

	public boolean onTrackballEvent(MotionEvent e) {
		if (isInstance) {
			try {
				return currentScreen.onTrackballEvent(e);
			} catch (Exception ex) {
			}
		}
		return false;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		if (isInstance) {
			currentScreen.onAccuracyChanged(sensor, accuracy);
		}
	}

	public void onSensorChanged(SensorEvent event) {
		if (isInstance) {
			currentScreen.onSensorChanged(event);
		}
	}

	public void onPause() {
		if (isInstance) {
			currentScreen.onPause();
		}
	}

	public void onResume() {
		if (isInstance) {
			currentScreen.onResume();
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		if (isInstance) {
			return currentScreen.onCreateOptionsMenu(menu);
		}
		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (isInstance) {
			return currentScreen.onOptionsItemSelected(item);
		}
		return false;
	}

	public void onOptionsMenuClosed(Menu menu) {
		if (isInstance) {
			currentScreen.onOptionsMenuClosed(menu);
		}
	}

	public int getDeviceWidth() {
		return deviceWidth;
	}

	public int getDeviceHeight() {
		return deviceHeight;
	}

	public Bitmap getImage() {
		if (gameView != null) {
			return gameView.getImage();
		}
		return null;
	}

	public void destroy() {
		if (isInstance) {
			isInstance = false;
			if (currentScreen != null) {
				currentScreen.destroy();
				currentScreen = null;
			}
			LImage.disposeAll();
		}
	}
}
