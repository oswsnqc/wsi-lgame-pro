package org.loon.framework.android.game.action.sprite;

import java.io.Serializable;

import org.loon.framework.android.game.core.LRelease;
import org.loon.framework.android.game.core.geom.RectBox;
import org.loon.framework.android.game.core.graphics.device.LGraphics;

import android.graphics.Bitmap;

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

public interface ISprite extends Serializable, LRelease {

	public static final int TYPE_FADE_IN = 0;
	public static final int TYPE_FADE_OUT = 1;

	/**
	 * ISprite 宽， 抽象函数
	 * @return 返回宽
	 */
	public abstract int getWidth();
	
	/**
	 * ISprite 高， 抽象函数
	 * @return 返回高
	 */
	public abstract int getHeight();
	
//	/**
//	 * 设置透明度， 抽象函数
//	 * @param alpha	： 透明度
//	 */
//	public abstract void setAlpha(float alpha);
	
	/**
	 * ISprite 透明度， 抽象函数
	 * @return 返回透明度
	 */
	public abstract float getAlpha();

	/**
	 * 获取 x， 抽象函数
	 * @return	返回 x 值
	 */
	public abstract int x();

	/**
	 * 获取 y， 抽象函数
	 * @return	返回 y 值
	 */
	public abstract int y();

	/**
	 * 获取 x 值， 抽象函数
	 * @return 返回 x 值
	 */
	public abstract double getX();
	
	/**
	 * 获取 y 值， 抽象函数
	 * @return 返回 y 值
	 */
	public abstract double getY();

	/**
	 * 设置是否可见， 抽象函数
	 * @param visible	true - 可见； false - 不可见
	 */
	public abstract void setVisible(boolean visible);
	
	/**
	 * 返回是否可见， 抽象函数
	 * @return	返回是否可见， true - 可见； false - 不可见
	 */
	public abstract boolean isVisible();

	/**
	 * 创建 UI 界面， 抽象函数
	 * @param g	： LGraphics 实例
	 */
	public abstract void createUI(LGraphics g);
	/**
	 * 更新 UI 界面， 抽象函数
	 * @param elapsedTime	： 流逝间隔时间
	 */
	public abstract void update(long elapsedTime);

	/**
	 * 设置图层， 抽象函数
	 * @param layer	： 图层
	 */
	public abstract void setLayer(int layer);
	
	/**
	 * 获取图层， 抽象函数
	 * @return	返回图层
	 */
	public abstract int getLayer();

	/**
	 * 检测矩形是否碰撞， 抽象函数
	 * @return	返回交叉的矩形
	 */
	public abstract RectBox getCollisionBox();
	
	/**
	 * 获取 Bitmap， 抽象函数
	 * @return	返回 Bitmap
	 */
	public abstract Bitmap getBitmap();

}
