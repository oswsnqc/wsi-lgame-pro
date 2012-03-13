package org.loon.framework.android.game;

import java.util.LinkedList;
import java.util.List;

import org.loon.framework.android.game.core.EmulatorListener;
import org.loon.framework.android.game.core.LHandler;
import org.loon.framework.android.game.core.LInput.ClickListener;
import org.loon.framework.android.game.core.LInput.SelectListener;
import org.loon.framework.android.game.core.LInput.TextListener;
import org.loon.framework.android.game.core.LSystem;
import org.loon.framework.android.game.core.geom.RectBox;
import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.core.graphics.Screen;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email ceponline@yahoo.com.cn
 * @version 0.1.2
 */
public abstract class LGameAndroid2DActivity extends Activity implements android.hardware.SensorEventListener {

	private android.hardware.SensorManager sensorManager;
	private android.hardware.Sensor sensorAccelerometer;
	private boolean isSetupSensors, isKeyboardOpen, isLandscape, isDestroy;
	/**
	 * true: Back键无法退出游戏; false: Back键退出游戏(默认为False)，即LSystem.exit() -> lActivity.finish() 
	 */
	private boolean isBackLocked;

	private int orientation;
	private long keyTimeMillis;

	/**
	 * LGameAndroid2DView 实例
	 */
	private LGameAndroid2DView gameView;
	/**
	 * LHandler 实例
	 */
	private LHandler gameHandler;
	/**
	 * FrameLayout 实例，用于showScreen()中的 Activity.setContentView(frameLayout)
	 */
	private FrameLayout frameLayout;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		LSystem.gc();
		Log.i("Android2DActivity", "LGame 2D Engine Start");

		this.frameLayout = new FrameLayout(LGameAndroid2DActivity.this);		// 构建整个游戏使用的最底层FrameLayout
		this.isBackLocked = false;		// 当此项为True时，Back键无法退出游戏(默认为False)
		this.isDestroy = true;			// 当此项为False时，Activity在onDestroy仅关闭当前Activity，而不关闭整个程序(默认为True)
		this.onMain();
	}

	/**
	 * 设置重力感应为true
	 */
	public void setupGravity() {
		this.isSetupSensors = true;
	}

	/**
	 * 注册重力感应及管理
	 */
	private void initSensors() {
		try {
			android.hardware.SensorManager sensorService = (android.hardware.SensorManager) getSystemService(Context.SENSOR_SERVICE);
			this.sensorManager = sensorService;
			if (sensorService == null) {
				return;
			}

			List<android.hardware.Sensor> sensors = sensorManager.getSensorList(android.hardware.Sensor.TYPE_ACCELEROMETER);
			if (sensors.size() > 0) {
				sensorAccelerometer = sensors.get(0);
			}

			boolean isSupported = sensorManager.registerListener(this,
					sensorAccelerometer,
					android.hardware.SensorManager.SENSOR_DELAY_GAME);

			// 当不支持Accelerometer时卸载此监听
			if (!isSupported) {
				sensorManager.unregisterListener(this, sensorAccelerometer);
			}
		} catch (Exception ex) {
		}
	}

	/**
	 * 卸载重力感应
	 */
	private void stopSensors() {
		try {
			if (sensorManager != null) {
				this.sensorManager.unregisterListener(this);
				this.sensorManager = null;
			}
		} catch (Exception ex) {
		}
	}

	/**
	 * 在LGameAndroid2DActivity中的onCreate()调用，虚函数需要自己实现
	 */
	public abstract void onMain();

	/**
	 * 以指定倾斜方式显示游戏画面
	 * 
	 * @param landscape  : 屏幕显示方向， true - 横屏； false - 竖屏 
	 */
	public void initialization(final boolean landscape) {
		initialization(LSystem.MAX_SCREEN_WIDTH, LSystem.MAX_SCREEN_HEIGHT, landscape, LMode.Fill);
	}

	/**
	 * 以指定倾斜方式显示游戏画面
	 * 
	 * @param width		: 屏幕宽
	 * @param height	: 屏幕高	
	 * @param landscape : 屏幕显示方向， true - 横屏； false - 竖屏 
	 */
	public void initialization(final int width, final int height, final boolean landscape) {
		initialization(width, height, landscape, LMode.Fill);
	}

	/**
	 * 以指定倾斜方式显示游戏画面
	 * 
	 * @param width		: 屏幕宽
	 * @param height	: 屏幕高	
	 * @param landscape : 屏幕显示方向， true - 横屏； false - 竖屏 
	 * @param mode		: 缩放模式，默认有六种 Defalut, Max, Fill, FitFill, Ratio, MaxRatio
	 */
	public void initialization(final int width, final int height, final boolean landscape, final LMode mode) {
		maxScreen(width, height);
		initialization(landscape, mode);
	}

	/**
	 * 以指定倾斜方式，指定模式显示游戏画面
	 * 
	 * @param landscape : 屏幕显示方向， true - 横屏； false - 竖屏 
	 * @param mode		: 缩放模式，默认有六种 Defalut, Max, Fill, FitFill, Ratio, MaxRatio
	 */
	public void initialization(final boolean landscape, final LMode mode) {
		
		// 如果LSystem错误的设置了最大屏幕值则自动矫正(LGame默认按横屏计算，因此LSystem.MAX_SCREEN_WIDTH > LSystem.MAX_SCREEN_HEIGHT)
		if (landscape == false) {	// 竖屏时
			if (LSystem.MAX_SCREEN_HEIGHT > LSystem.MAX_SCREEN_WIDTH) {		// height > width, 需重设 width > height （因为引擎系统默认为横屏）
				int tmp_height = LSystem.MAX_SCREEN_HEIGHT;
				LSystem.MAX_SCREEN_HEIGHT = LSystem.MAX_SCREEN_WIDTH;
				LSystem.MAX_SCREEN_WIDTH = tmp_height;
			}
		}
		
		// 创建游戏View
		this.gameView = new LGameAndroid2DView(LGameAndroid2DActivity.this, landscape, mode);
		this.gameHandler = gameView.getGameHandler();		// 获得当前游戏操作句柄

		if (mode == LMode.Defalut) {
			this.addView(gameView, gameHandler.getWidth(), gameHandler.getHeight(), Location.CENTER);			// 添加游戏View，显示为指定大小，并居中
			
		} else if (mode == LMode.Ratio) {
			this.addView(gameView, gameHandler.getDeviceWidth(), gameHandler.getDeviceHeight(), Location.CENTER);			// 添加游戏View，显示为屏幕许可范围，并居中
			
		} else if (mode == LMode.MaxRatio) {
			this.addView(gameView, gameHandler.getDeviceWidth(), gameHandler.getDeviceHeight(), Location.CENTER);			// 添加游戏View，显示为屏幕许可的最大范围(可能比单纯的Ratio失真)，并居中
			
		} else if (mode == LMode.Max) {
			this.addView(gameView, gameHandler.getDeviceWidth(), gameHandler.getDeviceHeight(), Location.CENTER);			// 添加游戏View，显示为最大范围值，并居中
			
		} else if (mode == LMode.Fill) {
			this.addView(gameView, android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT, Location.CENTER);	// 添加游戏View，显示为全屏，并居中
		
		} else if (mode == LMode.FitFill) {
			this.addView(gameView, gameHandler.getDeviceWidth(), gameHandler.getDeviceHeight(), Location.CENTER);			// 添加游戏View，显示为按比例缩放情况下的最大值，并居中
		
		}

		// 启动重力感应
		if (isSetupSensors) {
			this.initSensors();			
		}
	}

	/**
	 * 设定游戏窗体最大值
	 * 
	 * @param w	： 设置屏幕最大宽, 即 LSystem.MAX_SCREEN_WIDTH  = w
	 * @param h	： 设置屏幕最大高, 即 LSystem.MAX_SCREEN_HEIGHT = h
	 */
	public void maxScreen(int w, int h) {
		LSystem.MAX_SCREEN_WIDTH = w;
		LSystem.MAX_SCREEN_HEIGHT = h;
	}

	/**
	 * 设定常规图像加载方法的扩大值
	 * @param sampleSize
	 */
	public void setSizeImage(int sampleSize) {
		LSystem.setPoorImage(sampleSize);
	}

	/**
	 * 取出第一个Screen并执行
	 */
	public void runFirstScreen() {
		if (gameHandler != null) {
			gameHandler.runFirstScreen();
		}
	}

	/**
	 * 取出最后一个Screen并执行
	 */
	public void runLastScreen() {
		if (gameHandler != null) {
			gameHandler.runLastScreen();
		}
	}

	/**
	 * 运行指定位置的Screen
	 */
	public void runIndexScreen(int index) {
		if (gameHandler != null) {
			gameHandler.runIndexScreen(index);
		}
	}

	/**
	 * 运行自当前Screen起的上一个Screen
	 */
	public void runPreviousScreen() {
		if (gameHandler != null) {
			gameHandler.runPreviousScreen();
		}
	}

	/**
	 * 运行自当前Screen起的下一个Screen
	 */
	public void runNextScreen() {
		if (gameHandler != null) {
			gameHandler.runNextScreen();
		}
	}

	/**
	 * 切换当前窗体为指定Screen, 通过 gameHandler.setScreen(screen) 设置，其中 gameHandler 为 LHandler 实例
	 * 
	 * @param screen	: 显示当前指定的screen
	 */
	public void setScreen(Screen screen) {
		if (gameHandler != null) {
			this.gameHandler.setScreen(screen);
		}
	}

	/**
	 * 向缓存中添加Screen数据，但是不立即执行
	 * 
	 * @param screen	: 添加的screen
	 */
	public void addScreen(Screen screen) {
		if (gameHandler != null) {
			gameHandler.addScreen(screen);
		}
	}

	/**
	 * 获得保存的Screen列表
	 * 
	 * @return LinkedList(Screen)， 返回保存的screens集合
	 */
	public LinkedList<Screen> getScreens() {
		if (gameHandler != null) {
			return gameHandler.getScreens();
		}
		return null;
	}

	/**
	 * 获得缓存的Screen总数
	 * 
	 * @return	: 获取screens总数： gameHandler.getScreenCount()
	 */
	public int getScreenCount() {
		if (gameHandler != null) {
			return gameHandler.getScreenCount();
		}
		return 0;
	}
	
	
	public RectBox getScreenDimension(){
		DisplayMetrics dm= new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;

		Log.i("getScreenDimension", "dm.xdpi = " + dm.xdpi + "; dm.ydpi = " + dm.ydpi);
		Log.i("getScreenDimension", "dm.widthPixels = " + dm.widthPixels + "; dm.heightPixels = " + dm.heightPixels + "; density = " + density);
		Log.i("getScreenDimension", "dm.widthPixels * density = " + dm.widthPixels * density + "; dm.heightPixels * density = " + dm.heightPixels * density);
		
		return new RectBox(0, 0, dm.widthPixels, dm.heightPixels);
	}

	/**
	 * 弹出输入框
	 * 
	 * @param listener	： 输入框监听
	 * @param title		： 输入框标题
	 * @param message	： 输入框显示信息
	 */
	public void showAndroidTextInput(final TextListener listener, final String title, final String message) {
		if (listener == null) {
			return;
		}
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LGameAndroid2DActivity.this);
		builder.setTitle(title);
		final android.widget.EditText input = new android.widget.EditText(LGameAndroid2DActivity.this);
		input.setText(message);
		input.setSingleLine();
		builder.setView(input);
		
		builder.setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
					public void onClick(android.content.DialogInterface dialog, int whichButton) {
						listener.input(input.getText().toString());
					}
				});
		builder.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {
					public void onCancel(android.content.DialogInterface dialog) {
						listener.cancled();
					}
				});
		builder.show();
	}

	/**
	 * 弹出 AlertDialog 对话框
	 * 
	 * @param listener	： 输入框监听
	 * @param title		： 输入框标题
	 * @param message	： 输入框显示信息
	 */
	public void showAndroidAlert(final ClickListener listener, final String title, final String message) {
		if (listener == null) {
			return;
		}

		final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(LGameAndroid2DActivity.this).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		
		alertDialog.setButton("OK", new android.content.DialogInterface.OnClickListener() {
					public void onClick(android.content.DialogInterface dialog, int whichButton) {
						listener.clicked();
					}
				});
		alertDialog.show();
	}

	/**
	 * 显示HTML文件
	 * 
	 * @param listener	： 输入框监听
	 * @param title		： 输入框标题
	 * @param url		： url网址字符串
	 */
	public void showAndroidOpenHTML(final ClickListener listener, final String title, final String url) {
		final LGameWeb web = new LGameWeb(LGameAndroid2DActivity.this, url);
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LGameAndroid2DActivity.this);
		builder.setCancelable(true);
		builder.setTitle(title);
		builder.setView(web);
		builder.setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
					public void onClick(android.content.DialogInterface dialog, int whichButton) {
						listener.clicked();
					}
				}).setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
					public void onClick(android.content.DialogInterface dialog, int whichButton) {
						listener.cancled();
					}
				});
		builder.show();
	}

	/**
	 * @param listener	： 输入框监听
	 * @param title		： 输入框标题
	 * @param text		： 选项数组，如 A. aaa; B. bbb; C. ccc
	 */
	public void showAndroidSelect(final SelectListener listener, final String title, final String text[]) {
		if (listener == null) {
			return;
		}

		final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LGameAndroid2DActivity.this);
		builder.setTitle(title);
		builder.setItems(text, new android.content.DialogInterface.OnClickListener() {
					public void onClick(android.content.DialogInterface dialog, int item) {
						listener.item(item);
					}
				});
		builder.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {
					public void onCancel(android.content.DialogInterface dialog) {
						listener.cancled();
					}
				});
		android.app.AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * 加载指定资源索引的View到当前窗体
	 * 
	 * @param layoutID
	 * @return
	 */
	public View inflate(final int layoutID) {
		final android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
		return inflater.inflate(layoutID, null);
	}

	/**
	 * 返回布局器 frameLayout
	 */
	public FrameLayout getFrameLayout() {
		return frameLayout;
	}

	/**
	 * 添加指定的View到游戏界面的指定位置
	 * 
	 * @param view		: 添加的view
	 * @param location	: view位置信息，种类有 LEFT, RIGHT, TOP, BOTTOM, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER, ALIGN_BASELINE, ALIGN_LEFT,
	 *  ALIGN_TOP, ALIGN_RIGHT, ALIGN_BOTTOM, ALIGN_PARENT_LEFT, ALIGN_PARENT_TOP, ALIGN_PARENT_RIGHT, ALIGN_PARENT_BOTTOM, CENTER_IN_PARENT, CENTER_HORIZONTAL, CENTER_VERTICAL;
	 */
	public void addView(final View view, Location location) {
		addView(view, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, location);
	}

	/**
	 * 添加指定的View到游戏界面的指定位置，并将View设置为指定大小
	 * 
	 * @param view
	 * @param location
	 */
	public void addView(final View view, int w, int h, Location location) {
		android.widget.RelativeLayout viewLayout = new android.widget.RelativeLayout(LGameAndroid2DActivity.this);
		android.widget.RelativeLayout.LayoutParams relativeParams = LSystem.createRelativeLayout(location, w, h);
		viewLayout.addView(view, relativeParams);
		addView(viewLayout);
	}

	/**
	 * 添加指定的View到游戏界面
	 * 
	 * @param view	: 添加的view
	 */
	public void addView(final View view) {
		frameLayout.addView(view, LSystem.createFillLayoutParams());
	}

	/**
	 * 从游戏界面中删除指定的View
	 * 
	 * @param view
	 */
	public void removeView(final View view) {
		frameLayout.removeView(view);
	}

	/**
	 * 显示游戏窗体，setContentView(frameLayout)， 其中 frameLayout中添加了view
	 * 
	 * 设置背景setBackgroundDrawable = null 
	 */
	public void showScreen() {
		setContentView(frameLayout);
		try {
			this.getWindow().setBackgroundDrawable(null);
		} catch (Exception e) {
		}
	}

	/**
	 * 假广告ID注入地址之一，用以蒙蔽不懂编程的工具破解者，对实际开发无意义。
	 * 
	 * @param ad
	 * @return
	 */
	public int setAD(String ad) {
		int result = 0;
		try {
			Class<LGameAndroid2DActivity> clazz = LGameAndroid2DActivity.class;
			java.lang.reflect.Field[] field = clazz.getDeclaredFields();
			if (field != null) {
				result = field.length;
			}
		} catch (Exception e) {
		}
		return result + ad.length();
	}

	/**
	 * 获得当前程序包信息
	 * 
	 * @return
	 */
	public android.content.pm.PackageInfo getPackageInfo() {
		try {
			String packName = this.getPackageName();
			return this.getPackageManager().getPackageInfo(packName, 0);
			
		} catch (Exception ex) {
		}
		return null;
	}

	/**
	 * 获得当前程序版本名称
	 * 
	 * @return this.getPackageInfo().versionName
	 */
	public String getVersionName() {
		return this.getPackageInfo().packageName == null ? this.getPackageInfo().packageName : null;
	}

	/**
	 * 获得当前程序版本号
	 * 
	 * @return	this.getPackageInfo().versionCode
	 */
	public int getVersionCode() {
		return this.getPackageInfo() == null ? this.getPackageInfo().versionCode: -1;
	}

	@Override
	public void onConfigurationChanged(android.content.res.Configuration config) {
		super.onConfigurationChanged(config);
		
		orientation = config.orientation;
		isKeyboardOpen = config.keyboardHidden == android.content.res.Configuration.KEYBOARDHIDDEN_NO;
		isLandscape = config.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE;
	}

	/**
	 * 设置游戏gameView是否显示帧数
	 * 
	 * @param flag	: true - 显示； false - 不显示
	 */
	public void setShowFPS(boolean flag) {
		if (gameView != null) {
			this.gameView.setShowFPS(flag);
		}
	}

	/**
	 * 设置游戏gameView是否显示帧数
	 * 
	 * @param flag	: true - 显示； false - 不显示
	 */
	public void setShowMemory(boolean flag) {
		if (gameView != null) {
			this.gameView.setShowMemory(flag);
		}
	}

	/**
	 * 设置游戏gameView每秒显示的帧数
	 * 
	 * @param frames	： 游戏每秒显示的帧数
	 */
	public void setFPS(long frames) {
		if (gameView != null) {
			this.gameView.setFPS(frames);
		}
	}

	public void setEmulatorListener(EmulatorListener emulator) {
		if (gameView != null) {
			gameView.setEmulatorListener(emulator);
		}
	}

	public boolean isShowLogo() {
		if (gameView != null) {
			return gameView.isShowLogo();
		}
		return false;
	}

	public void setShowLogo(boolean showLogo) {
		if (gameView != null) {
			gameView.setShowLogo(showLogo);
		}
	}

	public void setLogo(LImage img) {
		if (gameView != null) {
			this.gameView.setLogo(img);
		}
	}

	/**
	 * 获取游戏显示gameView
	 * 
	 * @return	: 返回游戏的gameView，其是LGameAndroid2DView的实例
	 */
	public LGameAndroid2DView gameView() {
		return gameView;
	}

	/**
	 * 键盘是否已显示
	 * 
	 * @return
	 */
	public boolean isKeyboardOpen() {
		return isKeyboardOpen;
	}

	/**
	 * 返回屏幕是否设置横屏
	 * 
	 * @return	 true ： 横屏； false ： 竖屏
	 */
	public boolean isLandscape() {
		return isLandscape;
	}

	/**
	 * 当前窗体方向
	 * 
	 * @return android.content.res.Configuration.ORIENTATION_LANDSCAPE ： 横屏， 否则，竖屏
	 */
	public int getOrientation() {
		return orientation;
	}

	public boolean onTouchEvent(MotionEvent e) {
		if (gameHandler == null) {
			return false;
		}
		gameHandler.onTouchEvent(e);
		return false;
	}

	public boolean onTrackballEvent(MotionEvent e) {
		if (gameHandler != null) {
			synchronized (gameHandler) {
				return gameHandler.onTrackballEvent(e);
			}
		}
		return super.onTrackballEvent(e);
	}

	/**
	 * 退出当前Activity应用
	 */
	public void close() {
		finish();
	}

	public boolean isDestroy() {
		return isDestroy;
	}

	/**
	 * 设定是否在Activity注销时强制关闭整个程序
	 * 
	 * @param isDestroy, true - 强制关闭； false - 不强制关闭，即锁定back键使其无效(isBackLocked = true)
	 */
	public void setDestroy(boolean isDestroy) {
		this.isDestroy = isDestroy;
		if (isDestroy == false) {
			this.isBackLocked = true;
		}
	}

	public boolean isBackLocked() {
		return isBackLocked;
	}

	/**
	 * 设定锁死BACK事件不处理, true: Back键无法退出游戏; false: Back键退出游戏(默认为False)
	 * 
	 * @param isBackLocked, true: Back键无法退出游戏; false: Back键退出游戏(默认为False)
	 */
	public void setBackLocked(boolean isBackLocked) {
		this.isBackLocked = isBackLocked;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e) {
		long curTime = System.currentTimeMillis();
		
		if ((curTime - keyTimeMillis) > LSystem.SECOND / 5) {		// 让每次执行键盘事件，至少间隔1/5秒
			keyTimeMillis = curTime;
			if (gameHandler != null) {
				synchronized (gameHandler) {
					
					if (!isBackLocked) {	// back 按键没有被锁定时
						if (keyCode == KeyEvent.KEYCODE_BACK) {
							gameHandler.removeLastScreen();		// 点击 back 键后，删除当前的screen，显示前一个screen
							if(gameHandler.getScreenCount() > 0){
								gameHandler.runLastScreen();
							}else {
								LSystem.exit();
							}
							return true;
						}
					}
					if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
						int vol = gameHandler.getAssetsSound().getSoundVolume();
						gameHandler.getAssetsSound().setSoundVolume(vol + 1);
					}
					if (keyCode == KeyEvent.KEYCODE_MENU) {
						return super.onKeyDown(keyCode, e);
					}
					if (gameHandler.onKeyDown(keyCode, e)) {
						return true;
					}
					
					try {
//						Thread.sleep(16);		// 在事件提交给Android前再次间隔，防止连发
					} catch (Exception ex) {
					}
					return super.onKeyDown(keyCode, e);
				}
			}
		}
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent e) {
		if (gameHandler != null) {
			synchronized (gameHandler) {
				if (keyCode == KeyEvent.KEYCODE_MENU) {
					return super.onKeyUp(keyCode, e);
				}
				if (gameHandler.onKeyUp(keyCode, e)) {
					return true;
				}
				return super.onKeyUp(keyCode, e);
			}
		}
		return true;
	}

	/**
	 * 自定义游戏挂起
	 */
	public abstract void onGamePaused();

	/**
	 * 自定义游戏激活
	 */
	public abstract void onGameResumed();

	@Override
	final protected void onPause() {
		super.onPause();
		
		if (gameHandler != null) {
			gameHandler.onPause();
		}
		if (gameView != null) {
			gameView.setPaused(true);
		}
		super.onPause();
		if (isSetupSensors) {
			stopSensors();		// 停止重力感应
		}
		onGamePaused();
	}
	
	@Override
	final protected void onResume() {
		super.onResume();
		
		if (gameHandler != null) {
			gameHandler.onResume();
		}
		if (gameView != null) {
			gameView.setPaused(false);
		}
		super.onResume();
		if (isSetupSensors) {
			initSensors();		// 恢复重力感应
		}
		onGameResumed();
	}

	@Override
	protected void onDestroy() {
		try {
			if (gameView != null) {
				gameView.setRunning(false);
				gameView = null;
				Thread.sleep(16);
			}
			super.onDestroy();
			// 当此项为True时，强制关闭整个程序
			if (isDestroy) {
				Log.i("Android2DActivity", "LGame 2D Engine Shutdown");
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected void onStop() {
		try {
			if (gameView != null) {
				this.gameView.setPaused(true);
			}
			super.onStop();
		} catch (Exception e) {
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		if (gameHandler != null) {
			if (gameHandler.onCreateOptionsMenu(menu)) {
				return true;
			}
		}
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = super.onOptionsItemSelected(item);
		if (gameHandler != null) {
			if (gameHandler.onOptionsItemSelected(item)) {
				return true;
			}
		}
		return result;
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
		if (gameHandler != null) {
			gameHandler.onOptionsMenuClosed(menu);
		}
	}

	@Override	// 重力感应，感应精度改变时触发
	public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
		if (gameHandler != null) {
			gameHandler.onAccuracyChanged(sensor, accuracy);
		}
	}

	@Override	// 重力感应，感应数值改变时触发
	public void onSensorChanged(android.hardware.SensorEvent event) {
		if (gameHandler != null) {
			gameHandler.onSensorChanged(event);
		}
	}

}