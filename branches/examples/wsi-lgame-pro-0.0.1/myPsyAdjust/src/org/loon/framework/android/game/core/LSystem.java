package org.loon.framework.android.game.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Random;

import org.loon.framework.android.game.LGameAndroid2DActivity;
import org.loon.framework.android.game.LGameAndroid2DView;
import org.loon.framework.android.game.Location;
import org.loon.framework.android.game.LMode;
import org.loon.framework.android.game.core.geom.RectBox;
import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.core.graphics.Screen;
import org.loon.framework.android.game.core.resource.Resources;
import org.loon.framework.android.game.core.timer.SystemTimer;
import org.loon.framework.android.game.utils.GraphicsUtils;
import org.loon.framework.android.game.utils.StringUtils;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.view.Display;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

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
 * @version 0.1.1
 */
public class LSystem {

	/**
	 * 框架名 LAE
	 */
	final static public String FRAMEWORK = "LAE";

	/**
	 * 包内默认的图片路径 src/assets/loon_
	 */
	final static public String FRAMEWORK_IMG_NAME = "assets/loon_";

	/**
	 * 框架版本信息
	 */
	final static public String VERSION = "1.1.0";

	/**
	 * 允许的最大窗体宽（横屏）	： 800
	 */
	public static int MAX_SCREEN_WIDTH = 800;		

	/**
	 * 允许的最大窗体高（横屏）	： 480
	 */
	public static int MAX_SCREEN_HEIGHT = 480;		

	final static public long SECOND = 1000;				// 秒
	final static public long MINUTE = SECOND * 60;		// 分
	final static public long HOUR = MINUTE * 60;		// 小时
	final static public long DAY = HOUR * 24;			// 天
	final static public long WEEK = DAY * 7;			// 周
	final static public long YEAR = DAY * 365;			// 理论上一年

	/**
	 * 行分隔符
	 */
	final static public String LS = System.getProperty("line.separator", "\n");

	/**
	 * 文件分割符, System.getProperty("file.separator", "\\")
	 */
	final static public String FS = System.getProperty("file.separator", "\\");

	// 随机数
	final static public Random random = new Random();

	/**
	 * 屏幕是否横屏
	 */
	public static boolean isSCREEN_LANDSCAPE;

	public static int IMAGE_SIZE = 0;						// 图像缩放值

	public static boolean isDEFAULT_ROTATE_CACHE = true;	// 是否启动旋转图片缓存

	final static public int DEFAULT_MAX_CACHE_SIZE = 30;	// 最大缓存数量

	final static public String encoding = "UTF-8";

	final static public String FONT_NAME = "Monospaced";

	/**
	 * 默认最大刷新率， 默认为 60
	 */
	final static public int DEFAULT_MAX_FPS = 60;

	private static LHandler handler;

	private static LGameAndroid2DView gameView;

	/**
	 * 自定义 RectBox 矩形的 实例
	 */
	public static RectBox screenRect;

	public static float scaleWidth = 1, scaleHeight = 1;

	public static boolean isLogo, isPaused, AUTO_REPAINT;

	private static String temp_file;

	private static int TMP_MAJOR = 0;

	/**
	 * 判断是否是模拟器， BULID_BRAND.indexOf("generic") != -1 && BULID_MODEL.indexOf("sdk") != -1
	 */
	private static boolean isEMULATOR;
	
	/**
	 * 判断SDK是否版本低， 即 (TMP_MAJOR < OS_16 && !EMULATOR)
	 */
	private static boolean isLOWER_VER;

	private static Handler OS_HANDLER;

	final private static String BULID_BRAND, BULID_MODEL, BULIDM_PRODUCT, BULIDM_RELEASE, BULIDM_SDK;

	final private static int OS_11 = 0, OS_15 = 1, OS_16 = 2, OS_20 = 3,
			OS_21 = 4, OS_22 = 5, OS_23 = 6, OS_30 = 7, OS_31 = 8, OS_35 = 9,
			OS_40 = 10, OS_45 = 11, OS_50 = 12, OS_60 = 13;

	static {
		BULID_BRAND = Build.BRAND.toLowerCase();
		BULID_MODEL = Build.MODEL.toLowerCase();
		BULIDM_PRODUCT = Build.PRODUCT.toLowerCase();
		BULIDM_RELEASE = Build.VERSION.RELEASE;
		BULIDM_SDK = Build.VERSION.SDK;
		if (BULIDM_RELEASE.indexOf("1.1") != -1) {
			TMP_MAJOR = OS_11;
		} else if (BULIDM_RELEASE.indexOf("1.5") != -1) {
			TMP_MAJOR = OS_15;
		} else if (BULIDM_RELEASE.indexOf("1.6") != -1) {
			TMP_MAJOR = OS_16;
		} else if (BULIDM_RELEASE.indexOf("2.0") != -1) {
			TMP_MAJOR = OS_20;
		} else if (BULIDM_RELEASE.indexOf("2.1") != -1) {
			TMP_MAJOR = OS_21;
		} else if (BULIDM_RELEASE.indexOf("2.2") != -1) {
			TMP_MAJOR = OS_22;
		} else if (BULIDM_RELEASE.indexOf("2.3") != -1) {
			TMP_MAJOR = OS_23;
		} else if (BULIDM_RELEASE.indexOf("3.0") != -1) {
			TMP_MAJOR = OS_30;
		} else if (BULIDM_RELEASE.indexOf("3.1") != -1) {
			TMP_MAJOR = OS_31;
		} else if (BULIDM_RELEASE.indexOf("3.5") != -1) {
			TMP_MAJOR = OS_35;
		} else if (BULIDM_RELEASE.indexOf("4.0") != -1) {
			TMP_MAJOR = OS_40;
		} else if (BULIDM_RELEASE.indexOf("4.5") != -1) {
			TMP_MAJOR = OS_45;
		} else if (BULIDM_RELEASE.indexOf("5.0") != -1) {
			TMP_MAJOR = OS_50;
		} else if (BULIDM_RELEASE.indexOf("6.0") != -1) {
			TMP_MAJOR = OS_60;
		} else {
			TMP_MAJOR = OS_15;
		}
		isEMULATOR = BULID_BRAND.indexOf("generic") != -1 && BULID_MODEL.indexOf("sdk") != -1;
		isLOWER_VER = (TMP_MAJOR < OS_16 && !isEMULATOR);
	}

	/***
	 * 获得指定名称资源的数据流
	 * 
	 * @param resName
	 * @return
	 */
	public final static InputStream getResourceAsStream(String resName) {
		try {
			return Resources.getResourceAsStream(resName);
		} catch (Exception e) {
			try {
				return Resources.class.getClassLoader().getResourceAsStream(resName);
			} catch (Exception ex) {
				try {
					return getActivity().getAssets().open(resName);
				} catch (IOException e1) {
					try {
						return getActivity().openFileInput(resName);
					} catch (IOException e2) {
						try {
							return new FileInputStream(new File(resName));
						} catch (FileNotFoundException e3) {
							return null;
						}
					}
				}
			}
		}
	}

	/**
	 * 执行一个位于Screen线程中的Runnable
	 * 
	 * @param runnable
	 */
	public final static void callScreenRunnable(Runnable runnable) {
		LHandler process = LSystem.getSystemHandler();
		if (process != null) {
			Screen screen = process.getScreen();
			if (screen != null) {
				synchronized (screen) {
					screen.callEvent(runnable);
				}
			}
		}
	}

	/**
	 * 设定常规图像加载方法（非全部）的默认劣化值
	 * 
	 * @param sampleSize
	 */
	public static void setPoorImage(int sampleSize) {
		if (sampleSize > 0) {
			LSystem.IMAGE_SIZE = sampleSize;
		} else {
			LSystem.IMAGE_SIZE = 0;
		}
	}

	/**
	 * 设定LayoutParams为全屏模式, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT
	 * 
	 * @return
	 */
	public static LayoutParams createFillLayoutParams() {
		return new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	}

	/**
	 * 生成一个对应指定位置的 RelativeLayout.LayoutParams
	 * 
	 * @param location
	 * @return
	 */
	public static RelativeLayout.LayoutParams createRelativeLayout(Location location) {
		return createRelativeLayout(location, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	/**
	 * 生成一个对应指定位置的 RelativeLayout.LayoutParams
	 * 
	 * @param location
	 * @return
	 */
	public static RelativeLayout.LayoutParams createRelativeLayout(Location location, int w, int h) {
		RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(w, h);
		
		if (location == Location.LEFT) {						// 左
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			
		} else if (location == Location.RIGHT) {				// 右
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			
		} else if (location == Location.TOP) {					// 上
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			
		} else if (location == Location.BOTTOM) {				// 下
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			
		} else if (location == Location.BOTTOM_LEFT) {			// 下左
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			
		} else if (location == Location.BOTTOM_RIGHT) {			// 下右
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			
		} else if (location == Location.CENTER) { 				// 全局中心
			relativeParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			relativeParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			
		} else if (location == Location.ALIGN_BASELINE) {		// 基线对齐
			relativeParams.addRule(RelativeLayout.ALIGN_BASELINE, RelativeLayout.TRUE);
			
		} else if (location == Location.ALIGN_LEFT) {			// 左对齐
			relativeParams.addRule(RelativeLayout.ALIGN_LEFT, RelativeLayout.TRUE);
			
		} else if (location == Location.ALIGN_TOP) {			// 上对齐
			relativeParams.addRule(RelativeLayout.ALIGN_TOP, RelativeLayout.TRUE);
			
		} else if (location == Location.ALIGN_RIGHT) {			// 右对齐
			relativeParams.addRule(RelativeLayout.ALIGN_RIGHT, RelativeLayout.TRUE);
			
		} else if (location == Location.ALIGN_BOTTOM) {			// 下对齐
			relativeParams.addRule(RelativeLayout.ALIGN_BOTTOM, RelativeLayout.TRUE);
			
		} else if (location == Location.ALIGN_PARENT_LEFT) {		// 父控件左
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			
		} else if (location == Location.ALIGN_PARENT_TOP) {			// 父控件上
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			
		} else if (location == Location.ALIGN_PARENT_RIGHT) {		// 父控件右
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			
		} else if (location == Location.ALIGN_PARENT_BOTTOM) {		// 父控件下
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			
		} else if (location == Location.CENTER_IN_PARENT) {			// 父控件中心
			relativeParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			
		} else if (location == Location.CENTER_HORIZONTAL) {		// 水平
			relativeParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			
		} else if (location == Location.CENTER_VERTICAL) {			// 垂直
			relativeParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			
		}

		return relativeParams;
	}

	/**
	 * 跳转到指定的Activity
	 * 
	 * @param from
	 */
	public static void action(Activity from, Class<?> clazz) {
		if (LSystem.getActivity() instanceof LGameAndroid2DActivity) {
			((LGameAndroid2DActivity) LSystem.getActivity()).setDestroy(false);
		}
		android.content.Intent intent = new android.content.Intent(from, clazz);
		from.startActivity(intent);
	}

	/**
	 * 跳转到指定的Activity,并将其设定为最初的Activity
	 * 
	 * @param from
	 * @param clazz
	 */
	public static void go(Activity from, Class<?> clazz) {
		if (LSystem.getActivity() instanceof LGameAndroid2DActivity) {
			((LGameAndroid2DActivity) LSystem.getActivity()).setDestroy(false);
		}
		android.content.Intent intent = new android.content.Intent(from, clazz);
		intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
		from.startActivity(intent);
	}

	/**
	 * 获取系统句柄 android.os.Handler()
	 * 
	 * @return 获取系统句柄 android.os.Handler()
	 */
	public static Handler getOSHandler() {
		if (OS_HANDLER == null) {
			OS_HANDLER = new android.os.Handler();
		}
		return OS_HANDLER;
	}

	public static void threadUi(final Runnable runnable) {
		
		Thread thread = new Thread() {
			public void run() {
				for (; handler == null || isLogo;) {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
				}
				Screen screen = null;
				for (; (screen = handler.getScreen()) == null;) {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
				}
				for (; !screen.isOnLoadComplete();) {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
				}
				try {
					LSystem.stopRepaint();
					LSystem.getActivity().runOnUiThread(runnable);
				} catch (Exception ex) {
					post(runnable);
				} finally {
					LSystem.startRepaint();
				}
			}
		};
		thread.setPriority(Thread.NORM_PRIORITY - 2);
		thread.start();
	}

	public static void post(final Runnable runnable) {
		getOSHandler().post(runnable);
	}

	public static void runOnUiThread(final Runnable runnable) {
		LSystem.getActivity().runOnUiThread(runnable);
	}

	public static void dispose(final Runnable runnable) {
		getOSHandler().removeCallbacks(runnable);
		getOSHandler().removeMessages(0);
	}

	public static void stopRepaint() {
		LSystem.AUTO_REPAINT = false;
		LSystem.isPaused = true;
	}

	public static void startRepaint() {
		LSystem.AUTO_REPAINT = true;
		LSystem.isPaused = false;
	}

	public static long getJavaHeap() {
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

	/**
	 * 判定当前Android系统版本是否高于指定的版本
	 * 
	 * @param ver
	 * @return
	 */
	public static boolean isAndroidVersionHigher(final int ver) {
		return Integer.parseInt(BULIDM_SDK) >= ver;
	}

	/**
	 * 判断SDK是否版本低， 即 (TMP_MAJOR < OS_16 && !EMULATOR)
	 * 
	 * @return 判断SDK是否版本低， 即 (TMP_MAJOR < OS_16 && !EMULATOR)，则返回true
	 */
	public static boolean isLowerVer() {
		return isLOWER_VER;
	}

	public static boolean isOverrunOS11() {
		return TMP_MAJOR > OS_11;
	}

	public static boolean isOverrunOS15() {
		return TMP_MAJOR > OS_15;
	}

	public static boolean isOverrunOS16() {
		return TMP_MAJOR > OS_16;
	}

	public static boolean isOverrunOS21() {
		return TMP_MAJOR > OS_21;
	}

	public static boolean isOverrunOS23() {
		return TMP_MAJOR > OS_23;
	}

	public static boolean isOverrunOS30() {
		return TMP_MAJOR > OS_30;
	}

	public static boolean isOverrunOS40() {
		return TMP_MAJOR > OS_40;
	}

	public static boolean isOverrunOS50() {
		return TMP_MAJOR > OS_50;
	}

	public static boolean isOverrunOS60() {
		return TMP_MAJOR > OS_60;
	}

	public static String getModel() {
		return BULID_MODEL;
	}

	public static String getProductName() {
		return BULIDM_PRODUCT;
	}

	public static int getMajorOSVersion() {
		return TMP_MAJOR;
	}

	public static String getOSVersion() {
		return BULIDM_RELEASE;
	}

	public static String getSDKVersion() {
		return BULIDM_SDK;
	}

	public static boolean isPaused() {
		return isPaused;
	}

	public static String getBRANDName() {
		return BULID_BRAND;
	}

	public static boolean isEmulator() {
		return isEMULATOR;
	}

	public static boolean isHTC() {
		return BULID_BRAND.indexOf("htc") != -1;
	}

	public static boolean isSamsungGalaxy() {
		final boolean isSamsung = BULID_BRAND.indexOf("samsung") != -1;
		final boolean isGalaxy = BULID_MODEL.indexOf("galaxy") != -1;
		return isSamsung && isGalaxy;
	}

	public static boolean isDroidOrMilestone() {
		final boolean isMotorola = BULID_BRAND.indexOf("moto") != -1;
		final boolean isDroid = BULID_MODEL.indexOf("droid") != -1;
		final boolean isMilestone = BULID_MODEL.indexOf("milestone") != -1;
		return isMotorola && (isDroid || isMilestone);
	}

	/**
	 * 清空框架临时资源
	 */
	public static void destroy() {
		GraphicsUtils.destroy();
		// ZipResource.destroy();
		Resources.destroy();
		LSystem.gc();
	}

	/**
	 * 获取游戏 LGameAndroid2DView 的实例 gameView
	 * 
	 * @return 获取游戏 LGameAndroid2DView 的实例 gameView
	 */
	public static LGameAndroid2DView getSurface2D() {
		return gameView;
	}

	/**
	 * 退出当前应用 Activity, 即 ((LGameAndroid2DView) handler.getView()).setRunning(false)， ((LHandler) handler).getLGameActivity().finish()
	 * 
	 */
	public static void exit() {
		synchronized (handler) {
			try {
				if (handler != null) {
					if (handler instanceof LHandler) {
						((LGameAndroid2DView) handler.getView()).setRunning(false);						// 关闭 LGameAndroid2DView 画布运行线程
						LGameAndroid2DActivity lActivity = ((LHandler) handler).getLGameActivity();		// 获取 LGameAndroid2DActivity 的 Activity
						if (lActivity.isDestroy()) {	
							lActivity.finish();		// 关闭游戏 LGameAndroid2DActivity 的 Activity
						}
					}
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 刷新绘图
	 */
	public static void repaint() {
		if (gameView != null) {
			gameView.update();
		}
	}

	/**
	 * 刷新绘图
	 * 
	 * @param bit
	 */
	public static void repaint(Bitmap bit) {
		if (gameView != null) {
			gameView.update(bit);
		}
	}

	/**
	 * 刷新绘图
	 * 
	 * @param img
	 */
	public static void repaint(LImage img) {
		if (gameView != null) {
			gameView.update(img);
		}
	}

	/**
	 * 刷新绘图并按照指定长宽比例居中显示
	 * 
	 * @param img
	 * @param w
	 * @param h
	 */
	public static void repaint(LImage img, int w, int h) {
		if (gameView != null) {
			gameView.update(img, w, h);
		}
	}

	/**
	 * 刷新绘图并按照指定长宽比例居中显示
	 * 
	 * @param bit
	 * @param w
	 * @param h
	 */
	public static void repaint(Bitmap bit, int w, int h) {
		if (gameView != null) {
			gameView.update(bit, w, h);
		}
	}

	/**
	 * 刷新绘图为指定大小并居中显示
	 * 
	 * @param img
	 * @param w
	 * @param h
	 */
	public static void repaintFull(LImage img, int w, int h) {
		if (gameView != null) {
			gameView.updateFull(img, w, h);
		}
	}

	/**
	 * 刷新绘图为指定大小并居中显示
	 * 
	 * @param bit
	 * @param w
	 * @param h
	 */
	public static void repaintFull(Bitmap bit, int w, int h) {
		if (gameView != null) {
			gameView.updateFull(bit, w, h);
		}
	}

	/**
	 * 变更画布为指定大小
	 * 
	 * @param img
	 * @param w
	 * @param h
	 */
	public static void repaintResize(LImage img, int w, int h) {
		if (gameView != null) {
			gameView.updateResize(img, w, h);
		}
	}

	/**
	 * 变更画布为指定大小
	 * 
	 * @param bit
	 * @param w
	 * @param h
	 */
	public static void repaintResize(Bitmap bit, int w, int h) {
		if (gameView != null) {
			gameView.updateResize(bit, w, h);
		}
	}

	/**
	 * 刷新图像到指定位置
	 * 
	 * @param img
	 * @param w
	 * @param h
	 */
	public static void repaintLocation(LImage img, int x, int y) {
		if (gameView != null) {
			gameView.updateLocation(img, x, y);
		}
	}

	/**
	 * 刷新图像到指定位置
	 * 
	 * @param bit
	 * @param w
	 * @param h
	 */
	public static void repaintLocation(Bitmap bit, int x, int y) {
		if (gameView != null) {
			gameView.updateLocation(bit, x, y);
		}
	}

	/**
	 * 设定刷新率
	 * 
	 * @param fps
	 */
	public static void setFPS(long fps) {
		if (gameView != null) {
			gameView.setFPS(fps);
		}
	}

	/**
	 * 返回当前刷新率
	 */
	public static long getFPS() {
		if (gameView != null) {
			return gameView.getCurrentFPS();
		}
		return 0;
	}

	/**
	 * 返回最大刷新率
	 */
	public static long getMaxFPS() {
		if (gameView != null) {
			return gameView.getMaxFPS();
		}
		return 1;
	}

	/**
	 * 返回游戏的 LGameActivity
	 * 
	 * @return	通过LHander获取LGameActivity， 即 ((LHandler) handler).getLGameActivity()
	 */
	public static Activity getActivity() {
		if (handler != null) {
			if (handler instanceof LHandler) {
				return ((LHandler) handler).getLGameActivity();
			}
		}
		return null;
	}

	/**
	 * 返回当前屏幕状态
	 * 
	 * @return
	 */
	public static String getScreenOrientation() {
		return LSystem.getScreenOrientation(getActivity());
	}

	/**
	 * 检测指定Activity的屏幕状态
	 * 
	 * @param aActivity
	 * @return
	 */
	public static String getScreenOrientation(final Activity aActivity) {
		
		final Display display = aActivity.getWindowManager().getDefaultDisplay();
		final int orientation = display.getOrientation();
		
		if (orientation == 0 && looksLikePortrait(aActivity)) {
			return "PORTRAIT";		// 竖屏
		}
		if (orientation == 1 && looksLikeLandscape(aActivity)) {
			return "LANDSCAPE";		// 横屏
		}

		if (looksLikePortrait(aActivity))
			return "PORTRAIT";
		if (looksLikeLandscape(aActivity))
			return "LANDSCAPE";
		if (looksLikeSquare(aActivity))
			return "SQUARE";

		return "PORTRAIT";
	}

	public static boolean looksLikePortrait(final Activity aActivity) {
		final Display display = aActivity.getWindowManager().getDefaultDisplay();
		return display.getWidth() < display.getHeight();
	}

	public static boolean looksLikeLandscape(final Activity aActivity) {
		final Display display = aActivity.getWindowManager().getDefaultDisplay();
		return display.getWidth() > display.getHeight();
	}

	public static boolean looksLikeSquare(final Activity aActivity) {
		final Display display = aActivity.getWindowManager().getDefaultDisplay();
		return display.getWidth() == display.getHeight();
	}

	/**
	 * 返回一个随机数
	 * 
	 * @param min	: 最小区间（包含min）
	 * @param max	: 最大区间（包含max）
	 * @return	返回一个随机数，[min, max]中的一个整数（注意：是闭区间）
	 */
	public static int getRandom(int min, int max) {
		if (max < min) {
			int tmp = max;
			min = tmp;
			max = min;
		}
		return random.nextInt(max - min + 1) + min;
	}

	/**
	 * 返回一个随机数, [min, max]
	 * 
	 * @param min	: 最小区间（包含min）
	 * @param max	: 最大区间（包含max）
	 * @return	返回一个随机数，[min, max]中的一个整数（注意：是闭区间）
	 */
	public static int getRandomBetween(int min, int max) {
		if (max < min) {
			int tmp = max;
			min = tmp;
			max = min;
		}
		return LSystem.random.nextInt(max + 1 - min) + min;
	}

	/**
	 * 获取当前系统时间，即 System.currentTimeMillis()
	 * 
	 * @return 返回一个 SystemTimer 实例，其构造函数已经获取了当前系统时间，即 System.currentTimeMillis()
	 */
	public static SystemTimer getSystemTimer() {
		return new SystemTimer();
	}

	/**
	 * 设置handler，获取LHandler对象实例，方便在LSystem层面对 LGameAndroid2DActivity 和 LGameAndroid2DView 控制与管理
	 * 
	 * @param activity		： 游戏 Activity，即 LGameAndroid2DActivity
	 * @param view			： 游戏 View， 即 LGameAndroid2DView
	 * @param landscape		： 游戏横竖屏模式， true： 横屏； false： 竖屏
	 * @param mode			： 游戏View显示模式，其几种模式为： Defalut, Max, Fill, FitFill, Ratio, MaxRatio
	 */
	public static void setupHandler(LGameAndroid2DActivity activity, LGameAndroid2DView view, boolean landscape, LMode mode) {
		gameView = view;
		handler = new LHandler(activity, view, landscape, mode);
	}

	public static String getVersionName() {
		if (handler != null) {
			if (handler instanceof LHandler) {
				return ((LHandler) handler).getLGameActivity().getVersionName();
			}
		}
		return null;
	}

	/**
	 * 
	 * @return	the path of the directory holding application cache files
	 */
	public static File getCacheFile() {
		if (handler != null) {
			if (handler instanceof LHandler) {
				return ((LHandler) handler).getLGameActivity().getCacheDir();
			}
		}
		return null;
	}

	public static File getCacheFile(String fileName) {
		final String file = LSystem.getCacheFileName();
		fileName = StringUtils.replaceIgnoreCase(fileName, "\\", "/");
		if (file != null) {
			if (fileName.startsWith("/") || fileName.startsWith("\\")) {
				fileName = fileName.substring(1, fileName.length());
			}
			if (file.endsWith("/") || file.endsWith("\\")) {
				return new File(LSystem.getCacheFileName() + fileName);
			} else {
				return new File(LSystem.getCacheFileName() + LSystem.FS + fileName);
			}
		} else {
			return new File(fileName);
		}
	}

	/**
	 * Returns the absolute path of this file. An absolute path is a path that starts at a root of the file system. On Android, there is only one root: /.
	 * 
	 * @return Returns the absolute path of this file
	 */
	public static String getCacheFileName() {
		if (temp_file == null) {
			Activity activity = null;
			if (handler != null) {
				if (handler instanceof LHandler) {
					activity = ((LHandler) handler).getLGameActivity();
				}
			}
			if (activity != null) {
				temp_file = activity.getCacheDir().getAbsolutePath();
			}
		}
		return temp_file;
	}

	/**
	 * 获取 LHandler 句柄
	 * 
	 * @return LHandler 实例 handler
	 */
	public static LHandler getSystemHandler() {
		return handler;
	}

	/**
	 * 强制进行系统资源回收
	 */
	public static void gcFinalization() {
		System.gc();
		System.runFinalization();
		System.gc();
	}

	/**
	 * 申请回收系统资源
	 * 
	 */
	final public static void gc() {
		System.gc();
	}

	/**
	 * 以指定范围内的指定概率执行gc
	 * 
	 * @param size
	 * @param rand
	 */
	final public static void gc(final int size, final long rand) {
		if (rand > size) {
			throw new RuntimeException(("GC random probability " + rand + " > " + size).intern());
		}
		if (LSystem.random.nextInt(size) <= rand) {
			LSystem.gc();
		}
	}

	/**
	 * 以指定概率使用gc回收系统资源
	 * 
	 * @param rand
	 */
	final public static void gc(final long rand) {
		gc(100, rand);
	}

	/**
	 * 读取指定文件到InputStream
	 * 
	 * @param buffer
	 * @return
	 */
	public static final InputStream read(byte[] buffer) {
		return new BufferedInputStream(new ByteArrayInputStream(buffer));
	}

	/**
	 * 读取指定文件到InputStream
	 * 
	 * @param file
	 * @return
	 */
	public static final InputStream read(File file) {
		try {
			return new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	/**
	 * 读取指定文件到InputStream
	 * 
	 * @param fileName
	 * @return
	 */
	public static final InputStream read(String fileName) {
		return read(new File(fileName));
	}

	/**
	 * 加载Properties文件
	 * 
	 * @param file
	 */
	final public static void loadPropertiesFileToSystem(final File file) {
		Properties properties = System.getProperties();
		InputStream in = LSystem.read(file);
		loadProperties(properties, in, file.getName());
	}

	/**
	 * 加载Properties文件
	 * 
	 * @param file
	 * @return
	 */
	final public static Properties loadPropertiesFromFile(final File file) {
		if (file == null) {
			return null;
		}
		Properties properties = new Properties();
		try {
			InputStream in = LSystem.read(file);
			loadProperties(properties, in, file.getName());
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	/**
	 * 加载Properties文件
	 * 
	 * @param properties
	 * @param inputStream
	 * @param fileName
	 */
	private static void loadProperties(Properties properties, InputStream inputStream, String fileName) {
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				throw new RuntimeException(("error closing input stream from file " + fileName
								+ ", ignoring , " + e.getMessage()).intern());
			}
		}
	}

	/**
	 * 写入整型数据到OutputStream
	 * 
	 * @param out
	 * @param number
	 */
	public final static void writeInt(final OutputStream out, final int number) {
		byte[] bytes = new byte[4];
		try {
			for (int i = 0; i < 4; i++) {
				bytes[i] = (byte) ((number >> (i * 8)) & 0xff);
			}
			out.write(bytes);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 从InputStream中获得整型数据
	 * 
	 * @param in
	 * @return
	 */
	final static public int readInt(final InputStream in) {
		int data = -1;
		try {
			data = (in.read() & 0xff);
			data |= ((in.read() & 0xff) << 8);
			data |= ((in.read() & 0xff) << 16);
			data |= ((in.read() & 0xff) << 24);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return data;
	}

	/**
	 * 合并hashCode和指定类型的数值生成新的Code值(以下同)
	 * 
	 * @param hashCode
	 * @param value
	 * @return
	 */
	public static int unite(int hashCode, boolean value) {
		int v = value ? 1231 : 1237;
		return unite(hashCode, v);
	}

	public static int unite(int hashCode, long value) {
		int v = (int) (value ^ (value >>> 32));
		return unite(hashCode, v);
	}

	public static int unite(int hashCode, float value) {
		int v = Float.floatToIntBits(value);
		return unite(hashCode, v);
	}

	public static int unite(int hashCode, double value) {
		long v = Double.doubleToLongBits(value);
		return unite(hashCode, v);
	}

	public static int unite(int hashCode, Object value) {
		return unite(hashCode, value.hashCode());
	}

	public static int unite(int hashCode, int value) {
		return 31 * hashCode + value;
	}
}
