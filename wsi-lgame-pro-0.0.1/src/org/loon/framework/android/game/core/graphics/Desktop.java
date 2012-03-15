package org.loon.framework.android.game.core.graphics;

import java.util.ArrayList;

import org.loon.framework.android.game.core.LInput;
import org.loon.framework.android.game.core.LRelease;
import org.loon.framework.android.game.core.graphics.component.LPanel;
import org.loon.framework.android.game.core.graphics.component.LLayer;
import org.loon.framework.android.game.core.graphics.device.LGraphics;

import android.view.MotionEvent;

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

public class Desktop implements LRelease{

	// 空桌面布局
	public static final Desktop EMPTY_DESKTOP = new Desktop();

	// 输入设备监听
	protected final LInput input;

	private LContainer container;

	private LComponent component;
	private LComponent hoverComponent;
	private LComponent selectedComponent;

	private LComponent[] clickComponent = new LComponent[1];

	/**
	 * 空桌面布局
	 */
	private Desktop() {
		this.container = new LPanel(0, 0, 1, 1);
		this.input = null;
		this.setDesktop(this.container);
	}

	/**
	 * 构造一个可用桌面
	 */
	public Desktop(LInput input, int width, int height) {
		this.container = new LPanel(0, 0, width, height);
		this.input = input;
		this.setDesktop(this.container);
	}

	/**
	 * 设置桌面 Desktop
	 * 
	 * @param comp	: this.container, 即 LContainer 实例
	 */
	void setDesktop(LComponent comp) {
		if (comp.isContainer()) {	// false
			LComponent[] child = ((LContainer) comp).getComponents();
			for (int i = 0; i < child.length; i++) {
				this.setDesktop(child[i]);
			}
		}
		comp.setDesktop(this);
	}

	/**
	 * 获取容器的组件个数
	 * 
	 * @return	container.getComponentCount()
	 */
	public int size() {
		return container.getComponentCount();
	}

	public void add(LComponent comp) {
		if (comp == null) {
			return;
		}
		if (comp.isFull) {
			this.input.setRepaintMode(Screen.SCREEN_NOT_REPAINT);
		}
		this.container.add(comp);
		this.processTouchMotionEvent();
	}

	public int remove(LComponent comp) {
		int removed = this.removeComponent(this.container, comp);
		if (removed != -1) {
			this.processTouchMotionEvent();
		}
		return removed;
	}

	private int removeComponent(LContainer container, LComponent comp) {
		int removed = container.remove(comp);
		LComponent[] components = container.getComponents();
		int i = 0;
		while (removed == -1 && i < components.length - 1) {
			if (components[i].isContainer()) {
				removed = this.removeComponent((LContainer) components[i], comp);
			}
			i++;
		}

		return removed;
	}

	public int remove(Class<? extends LComponent> clazz) {
		int removed = this.removeComponent(this.container, clazz);
		if (removed != -1) {
			this.processTouchMotionEvent();
		}
		return removed;
	}

	private int removeComponent(LContainer container, Class<? extends LComponent> clazz) {
		int removed = container.remove(clazz);
		LComponent[] components = container.getComponents();
		int i = 0;
		while (removed == -1 && i < components.length - 1) {
			if (components[i].isContainer()) {
				removed = this.removeComponent((LContainer) components[i], clazz);
			}
			i++;
		}
		return removed;
	}

	/**
	 * 刷新当前桌面
	 * 
	 */
	public void update(long timer) {
		if (!this.container.isVisible()) {
			return;
		}
		this.processEvents();
		// 刷新桌面中子容器组件
		this.container.update(timer);
	}

	public void createUI(LGraphics g) {
		this.container.createUI(g);
	}

	/**
	 * 事件监听
	 * 
	 */
	private void processEvents() {

		this.processTouchMotionEvent();		// 鼠标滑动
		if (this.hoverComponent != null && this.hoverComponent.isEnabled()) {			// 鼠标事件
			this.processTouchEvent();
		}

		if (this.selectedComponent != null && this.selectedComponent.isEnabled()) {		// 键盘事件
			this.processKeyEvent();
		}
	}

	/**
	 * 鼠标运动事件
	 */
	private void processTouchMotionEvent() {
		if (this.hoverComponent != null && this.hoverComponent.isEnabled() && (this.input.isMoving())) {
			if (this.input.getTouchDY() != 0 || this.input.getTouchDY() != 0) {
				this.hoverComponent.processTouchDragged();
			}

		} else {			// 获得当前窗体下鼠标坐标
			LComponent comp = this.findComponent(this.input.getTouchX(), this.input.getTouchY());
			if (comp != null) {
				if (this.input.getTouchDX() != 0 || this.input.getTouchDY() != 0) {
					comp.processTouchMoved();
				}

				if (this.hoverComponent == null) {
					comp.processTouchEntered();

				} else if (comp != this.hoverComponent) {
					this.hoverComponent.processTouchExited();
					comp.processTouchEntered();
				}

			} else {
				if (this.hoverComponent != null) {
					this.hoverComponent.processTouchExited();
				}
			}

			this.hoverComponent = comp;
		}
	}

	/**
	 * 鼠标按下事件
	 * 
	 */
	private void processTouchEvent() {
		int pressed = this.input.getTouchPressed(), released = this.input.getTouchReleased();
		if (pressed > LInput.NO_BUTTON) {
			this.hoverComponent.processTouchPressed();
			this.clickComponent[0] = this.hoverComponent;
			if (this.hoverComponent.isFocusable()) {
				if ((pressed == MotionEvent.ACTION_DOWN || pressed == MotionEvent.ACTION_UP)
						&& this.hoverComponent != this.selectedComponent) {
					this.selectComponent(this.hoverComponent);
				}
			}
		}
		if (released > LInput.NO_BUTTON) {
			this.hoverComponent.processTouchReleased();
			// 当释放鼠标时，点击事件生效
			if (this.clickComponent[0] == this.hoverComponent) {
				this.hoverComponent.processTouchClicked();
			}
		}
	}

	/**
	 * 触发键盘事件
	 * 
	 */
	private void processKeyEvent() {
		if (this.input.getKeyPressed() != LInput.NO_KEY) {
			this.selectedComponent.keyPressed();
		}
		if (this.input.getKeyReleased() != LInput.NO_KEY && this.selectedComponent != null) {
			this.selectedComponent.processKeyReleased();
		}
	}

	/**
	 * 查找指定坐标点成员
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private LComponent findComponent(int x, int y) {
		if (this.component != null && !this.component.isContainer()) {
			return null;
		}
		// 返回子容器
		LContainer panel = (this.component == null) ? this.container : ((LContainer) this.component);
		LComponent comp = panel.findComponent(x, y);
		return comp;
	}

	/**
	 * 清除容器焦点
	 */
	public void clearFocus() {
		this.deselectComponent();
	}

	void deselectComponent() {
		if (this.selectedComponent == null) {
			return;
		}
		this.selectedComponent.setSelected(false);
		this.selectedComponent = null;
	}

	/**
	 * 查找指定容器
	 * 
	 * @param comp
	 * @return
	 */
	boolean selectComponent(LComponent comp) {
		if (!comp.isVisible() || !comp.isEnabled() || !comp.isFocusable()) {
			return false;
		}

		// 清除最后部分
		this.deselectComponent();

		// 设定选中状态
		comp.setSelected(true);
		this.selectedComponent = comp;

		return true;
	}

	void setComponentStat(LComponent comp, boolean active) {
		if (this == Desktop.EMPTY_DESKTOP) {
			return;
		}

		if (active == false) {
			if (this.hoverComponent == comp) {
				this.processTouchMotionEvent();
			}

			if (this.selectedComponent == comp) {
				this.deselectComponent();
			}

			this.clickComponent[0] = null;

			if (this.component == comp) {
				this.component = null;
			}

		} else {
			this.processTouchMotionEvent();
		}

		if (comp.isContainer()) {
			LComponent[] components = ((LContainer) comp).getComponents();
			int size = ((LContainer) comp).getComponentCount();
			for (int i = 0; i < size; i++) {
				this.setComponentStat(components[i], active);
			}
		}
	}

	void clearComponentsStat(LComponent[] comp) {
		if (this == Desktop.EMPTY_DESKTOP) {
			return;
		}

		boolean checkTouchMotion = false;
		for (int i = 0; i < comp.length; i++) {
			if (this.hoverComponent == comp[i]) {
				checkTouchMotion = true;
			}

			if (this.selectedComponent == comp[i]) {
				this.deselectComponent();
			}

			this.clickComponent[0] = null;

		}

		if (checkTouchMotion) {
			this.processTouchMotionEvent();
		}
	}

	public void validateUI() {
		this.validateContainer(this.container);
	}

	final void validateContainer(LContainer container) {
		LComponent[] components = container.getComponents();
		int size = container.getComponentCount();
		for (int i = 0; i < size; i++) {
			if (components[i].isContainer()) {
				this.validateContainer((LContainer) components[i]);
			}
		}
	}

	public ArrayList<LComponent> getComponents(Class<? extends LComponent> clazz) {
		if (clazz == null) {
			return null;
		}
		LComponent[] components = container.getComponents();
		int size = components.length;
		ArrayList<LComponent> l = new ArrayList<LComponent>(size);
		for (int i = size; i > 0; i--) {
			LComponent comp = (LComponent) components[i - 1];
			Class<? extends LComponent> cls = comp.getClass();
			if (clazz == null || clazz == cls || clazz.isInstance(comp)
					|| clazz.equals(cls)) {
				l.add(comp);
			}
		}
		return l;
	}

	public LComponent getTopComponent() {
		LComponent[] components = container.getComponents();
		int size = components.length;
		if (size > 1) {
			return components[1];
		}
		return null;
	}

	public LComponent getBottomComponent() {
		LComponent[] components = container.getComponents();
		int size = components.length;
		if (size > 0) {
			return components[size - 1];
		}
		return null;
	}

	public LLayer getTopLayer() {
		LComponent[] components = container.getComponents();
		int size = components.length;
		Class<LLayer> clazz = LLayer.class;
		for (int i = 0; i < size; i++) {
			LComponent comp = (LComponent) components[i];
			Class<? extends LComponent> cls = comp.getClass();
			if (clazz == null || clazz == cls || clazz.isInstance(comp)
					|| clazz.equals(cls)) {
				return (LLayer) comp;
			}
		}
		return null;
	}

	public LLayer getBottomLayer() {
		LComponent[] components = container.getComponents();
		int size = components.length;
		Class<LLayer> clazz = LLayer.class;
		for (int i = size; i > 0; i--) {
			LComponent comp = (LComponent) components[i - 1];
			Class<? extends LComponent> cls = comp.getClass();
			if (clazz == null || clazz == cls || clazz.isInstance(comp) || clazz.equals(cls)) {
				return (LLayer) comp;
			}
		}
		return null;
	}

	public int getWidth() {
		return this.container.getWidth();
	}

	public int getHeight() {
		return this.container.getHeight();
	}

	public void setSize(int w, int h) {
		this.container.setSize(w, h);
	}

	public LContainer getContentPane() {
		return this.container;
	}

	public void setContentPane(LContainer pane) {
		pane.setBounds(0, 0, this.getWidth(), this.getHeight());
		this.container = pane;
		this.setDesktop(this.container);
	}

	public LComponent getHoverComponent() {
		return this.hoverComponent;
	}

	public LComponent getSelectedComponent() {
		return this.selectedComponent;
	}

	public LComponent getComponent() {
		return this.component;
	}

	public void setComponent(LComponent comp) {
		if (comp != null && !comp.isVisible()) {
			throw new RuntimeException("Can't set invisible component as component component!");
		}
		this.component = comp;
	}

	public LComponent get() {
		return this.container.get();
	}

	protected void finalize() throws Throwable {
		super.finalize();
	}

	public void dispose() {
		if (container != null) {
			container.dispose();
		}
	}

}
