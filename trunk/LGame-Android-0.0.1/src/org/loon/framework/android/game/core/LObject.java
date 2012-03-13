package org.loon.framework.android.game.core;

import org.loon.framework.android.game.action.map.Field2D;
import org.loon.framework.android.game.core.geom.RectBox;
import org.loon.framework.android.game.core.geom.Vector2D;

/**
 * Copyright 2008 - 2009
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
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */

/**
 * 抽象对象类， 函数或方法需要用户自己实现
 * @author homer
 *
 */
public abstract class LObject {

	protected String name;

	/**
	 * Vector2D 实例
	 */
	protected Vector2D location = new Vector2D(0, 0);

	protected int layer;

	protected RectBox rect;

	/**
	 * 更新 UI 界面， 抽象函数
	 * @param elapsedTime	： 流逝间隔时间
	 */
	public abstract void update(long elapsedTime);

	/**
	 * 在屏幕的 中心
	 */
	public void centerOnScreen() {
		LObject.centerOn(this, LSystem.screenRect.width, LSystem.screenRect.height);
	}

	/**
	 * 在屏幕的 底部
	 */
	public void bottomOnScreen() {
		LObject.bottomOn(this, LSystem.screenRect.width, LSystem.screenRect.height);
	}

	/**
	 * 在屏幕的 左边
	 */
	public void leftOnScreen() {
		LObject.leftOn(this, LSystem.screenRect.width, LSystem.screenRect.height);
	}

	/**
	 * 在屏幕的 右边
	 */
	public void rightOnScreen() {
		LObject.rightOn(this, LSystem.screenRect.width, LSystem.screenRect.height);
	}

	/**
	 * 在屏幕的 顶部
	 */
	public void topOnScreen() {
		LObject.topOn(this, LSystem.screenRect.width, LSystem.screenRect.height);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * 初始化并返回一个矩形， 即 new RectBox(x, y, w, h)
	 * @param x		： 矩形左上角的 x 值
	 * @param y		： 矩形角的 y 值
	 * @param w		： 矩形的 宽
	 * @param h		： 矩形的 高
	 * @return	返回一个矩形， 即 new RectBox(x, y, w, h)
	 */
	protected RectBox getRect(int x, int y, int w, int h) {
		if (rect == null) {
			rect = new RectBox(x, y, w, h);
		} else {
			rect.setBounds(x, y, w, h);
		}
		return rect;
	}


	/**
	 * 设置图层
	 * @param layer	： 图层
	 */
	public void setLayer(int layer) {
		this.layer = layer;
	}

	/**
	 * 获取图层
	 * @return	返回图层 layer
	 */
	public int getLayer() {
		return layer;
	}
	
	public void move_45D_up() {
		move_45D_up(1);
	}

	public void move_45D_up(int multiples) {
		location.move_multiples(Field2D.RIGHT_UP, multiples);
	}

	public void move_45D_left() {
		move_45D_left(1);
	}

	public void move_45D_left(int multiples) {
		location.move_multiples(Field2D.LEFT_UP, multiples);
	}

	public void move_45D_right() {
		move_45D_right(1);
	}

	public void move_45D_right(int multiples) {
		location.move_multiples(Field2D.RIGHT_DOWN, multiples);
	}

	public void move_45D_down() {
		move_45D_down(1);
	}

	public void move_45D_down(int multiples) {
		location.move_multiples(Field2D.LEFT_DOWN, multiples);
	}

	public void move_up() {
		move_up(1);
	}

	public void move_up(int multiples) {
		location.move_multiples(Field2D.TUP, multiples);
	}

	public void move_left() {
		move_left(1);
	}

	public void move_left(int multiples) {
		location.move_multiples(Field2D.TLEFT, multiples);
	}

	public void move_right() {
		move_right(1);
	}

	public void move_right(int multiples) {
		location.move_multiples(Field2D.TRIGHT, multiples);
	}

	public void move_down() {
		move_down(1);
	}

	public void move_down(int multiples) {
		location.move_multiples(Field2D.TDOWN, multiples);
	}

	public void move(Vector2D vector2D) {
		location.move(vector2D);
	}

	public void move(double x, double y) {
		location.move(x, y);
	}

	public void setLocation(double x, double y) {
		location.setLocation(x, y);
	}

	/**
	 * 获取 x
	 * @return	返回 x 值
	 */
	public int x() {
		return (int) location.getX();
	}

	/**
	 * 获取 y
	 * @return	返回 y 值
	 */
	public int y() {
		return (int) location.getY();
	}

	/**
	 * 获取 x 值
	 * @return 返回 x 值， 即 location.getX()
	 */
	public double getX() {
		return location.getX();
	}

	/**
	 * 获取 y 值
	 * @return 返回 y 值，即 location.getY()
	 */
	public double getY() {
		return location.getY();
	}

	public void setX(Integer x) {
		location.setX(x.intValue());
	}

	public void setX(double x) {
		location.setX(x);
	}

	public void setY(Integer y) {
		location.setY(y.intValue());
	}

	public void setY(double y) {
		location.setY(y);
	}

	/**
	 * 设置 location， 即 this.location = location
	 * @param location	： Vector2D 实例
	 */
	public void setLocation(Vector2D location) {
		this.location = location;
	}

	/**
	 * 获取 location， 注： location 是 Vector2D 实例
	 * @return	返回 location
	 */
	public Vector2D getLocation() {
		return location;
	}

	public void centerOn(final LObject object) {
		centerOn(object, getWidth(), getHeight());
	}

	/**
	 * 在 object 对象的 中心
	 * @param object	： 对象
	 * @param w			： 包含object对象的 宽
	 * @param h			： 包含object对象的 高
	 */
	public static void centerOn(final LObject object, int w, int h) {
		object.setLocation(w / 2 - object.getWidth() / 2, h / 2 - object.getHeight() / 2);
	}

	public void topOn(final LObject object) {
		topOn(object, getWidth(), getHeight());
	}

	/**
	 * 在 object 对象的 顶部
	 * @param object
	 * @param w
	 * @param h
	 */
	public static void topOn(final LObject object, int w, int h) {
		object.setLocation(w / 2 - h / 2, 0);
	}

	public void leftOn(final LObject object) {
		leftOn(object, getWidth(), getHeight());
	}

	/**
	 * 在 object 对象的 左边
	 * @param object
	 * @param w
	 * @param h
	 */
	public static void leftOn(final LObject object, int w, int h) {
		object.setLocation(0, h / 2 - object.getHeight() / 2);
	}

	public void rightOn(final LObject object) {
		rightOn(object, getWidth(), getHeight());
	}

	/**
	 * 在 object 对象的 右边
	 * @param object
	 * @param w
	 * @param h
	 */
	public static void rightOn(final LObject object, int w, int h) {
		object.setLocation(w - object.getWidth(), h / 2 - object.getHeight() / 2);
	}

	public void bottomOn(final LObject object) {
		bottomOn(object, getWidth(), getHeight());
	}

	/**
	 * 在 object 对象的 底部
	 * @param object
	 * @param w
	 * @param h
	 */
	public static void bottomOn(final LObject object, int w, int h) {
		object.setLocation(w / 2 - object.getWidth() / 2, h - object.getHeight());
	}

	/**
	 * 获取 宽， 抽象函数
	 * @return	获取 宽
	 */
	public abstract int getWidth();

	/**
	 * 获取 高， 抽象函数
	 * @return	获取 高
	 */
	public abstract int getHeight();

}
