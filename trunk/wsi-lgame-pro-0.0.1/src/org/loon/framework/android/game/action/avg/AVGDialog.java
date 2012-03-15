package org.loon.framework.android.game.action.avg;

import java.util.HashMap;
import java.util.Map;

import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.utils.GraphicsUtils;

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

final public class AVGDialog {

	private static Map<String, LImage> lazyImages;

	/**
	 * 显示对话框图片，注： 对话框以图片样式显示
	 * 
	 * @param fileName	： 图片名称
	 * @param width		： 对话框的宽
	 * @param height	： 对话框的高
	 * 
	 * @return	返回对话框的图片，即 LImage 实例
	 */
	public final static LImage getRMXPDialog(String fileName, int width, int height) {
		if (lazyImages == null) {
			lazyImages = new HashMap<String, LImage>(10);
		}
		LImage dialog = GraphicsUtils.load8888Image(fileName);		// 加载图片
		int w = dialog.getWidth();
		int[] pixels = dialog.getPixels();
		int index = -1;
		int count = 0;
		int pixel;
		for (int i = 0; i < 5; i++) {
			pixel = pixels[(141 + i) + w * 12];
			if (index == -1) {
				index = pixel;
			}
			if (index == pixel) {
				count++;
			}
		}
		if (count == 5) {
			return getRMXPDialog(dialog, width, height, 16, 5);
		} else if (count == 1) {
			return getRMXPDialog(dialog, width, height, 27, 5);
		} else if (count == 2) {
			return getRMXPDialog(dialog, width, height, 20, 5);
		} else {
			return getRMXPDialog(dialog, width, height, 27, 5);
		}
	}

	public final static LImage getRMXPloadBuoyage(String fileName, int width, int height) {
		return getRMXPloadBuoyage(GraphicsUtils.load8888Image(fileName), width, height);
	}

	public final static LImage getRMXPloadBuoyage(LImage rmxpImage, int width,
			int height) {
		if (lazyImages == null) {
			lazyImages = new HashMap<String, LImage>(10);
		}
		String keyName = ("buoyage" + width + "|" + height).intern();
		LImage lazyImage = (LImage) lazyImages.get(keyName);
		if (lazyImage == null) {
			LImage image, left, right, center, up, down = null;
			final int objWidth = 32;
			final int objHeight = 32;
			final int x1 = 128;
			final int x2 = 160;
			final int y1 = 64;
			final int y2 = 96;
			final int k = 1;

			try {
				image = GraphicsUtils.drawClipImage(rmxpImage, objWidth,
						objHeight, x1, y1, x2, y2);
				lazyImage = LImage.createImage(width, height, false);
				LGraphics g = lazyImage.getLGraphics();
				left = GraphicsUtils.drawClipImage(image, k, height, 0, 0, k,
						objHeight);
				right = GraphicsUtils.drawClipImage(image, k, height, objWidth
						- k, 0, objWidth, objHeight);
				center = GraphicsUtils.drawClipImage(image, width, height, k,
						k, objWidth - k, objHeight - k);
				up = GraphicsUtils.drawClipImage(image, width, k, 0, 0,
						objWidth, k);
				down = GraphicsUtils.drawClipImage(image, width, k, 0,
						objHeight - k, objWidth, objHeight);
				g.drawImage(center, 0, 0);
				g.drawImage(left, 0, 0);
				g.drawImage(right, width - k, 0);
				g.drawImage(up, 0, 0);
				g.drawImage(down, 0, height - k);
				g.dispose();
				lazyImages.put(keyName, lazyImage);
			} catch (Exception e) {
				return null;
			} finally {
				left = null;
				right = null;
				center = null;
				up = null;
				down = null;
				image = null;
			}
		}
		return lazyImage;

	}

	/**
	 * 绘制对话框图片，并返回对话框，以 LImage 图片形式返回
	 * 
	 * @param rmxpImage		： 对话框图片
	 * @param width			： 对话框的宽
	 * @param height		： 对话框的高
	 * @param size			： 
	 * @param offset		： 消息与边框的偏移值
	 * 
	 * @return				： 返回对话框图片， 即 LImage 实例
	 */
	private final static LImage getRMXPDialog(LImage rmxpImage, int width, int height, int size, int offset) {
		if (lazyImages == null) {
			lazyImages = new HashMap<String, LImage>(10);
		}
		String keyName = "dialog" + width + "|" + height;
		LImage lazyImage = (LImage) lazyImages.get(keyName);
		if (lazyImage == null) {	// lazyImage 为空，则创建
			try {
				final int objWidth = 64;	// 对话框边框的宽
				final int objHeight = 64;	// 对话框边框的高
				final int x1 = 128;			// 对话框边框图片的起点坐标 x		
				final int y1 = 0;			// 对话框边框图片的起点坐标 y					
				final int x2 = 192;			// 对话框边框图片的终点坐标 x	64 = 192 - 128
				final int y2 = 64;			// 对话框边框图片的终点坐标 x	64 = 64 - 0

				int center_size = objHeight - size * 2;		// 64 - 5 * 2 = 54

				LImage messageImage = null;
				LImage image = null;
				image = GraphicsUtils.drawClipImage(rmxpImage, objWidth, objHeight, x1, y1, x2, y2);	// 从 rmxpImage 裁剪出对话框的图片

				LImage centerTop = GraphicsUtils.drawClipImage(image, center_size, size, size, 0);
				LImage centerDown = GraphicsUtils.drawClipImage(image, center_size, size, size, objHeight - size);
				LImage leftTop = GraphicsUtils.drawClipImage(image, size, size, 0, 0);
				LImage leftCenter = GraphicsUtils.drawClipImage(image, size, center_size, 0, size);
				LImage leftDown = GraphicsUtils.drawClipImage(image, size, size, 0, objHeight - size);
				LImage rightTop = GraphicsUtils.drawClipImage(image, size, size, objWidth - size, 0);
				LImage rightCenter = GraphicsUtils.drawClipImage(image, size, center_size, objWidth - size, size);
				LImage rightDown = GraphicsUtils.drawClipImage(image, size, size, objWidth - size, objHeight - size);

				lazyImage = LImage.createImage(width, height, rmxpImage.getConfig());		// 创建对话框的图片
				messageImage = GraphicsUtils.drawClipImage(rmxpImage, 128, 128, 0, 0, 128, 128, false);

				LGraphics g = lazyImage.getLGraphics();		// 获取画布句柄，在其上绘制图片（拼凑）
				g.setAlpha(0.5f);

				messageImage = GraphicsUtils.getResize(messageImage, width - offset, height - offset);
				g.drawImage(messageImage, (lazyImage.getWidth() - messageImage.getWidth()) / 2, (lazyImage.getHeight() - messageImage.getHeight()) / 2);
				g.setAlpha(1.0f);

				LImage tmp = GraphicsUtils.getResize(centerTop, width - (size * 2), size);		// centerTop
				g.drawImage(tmp, size, 0);
				tmp = null;
				
				tmp = GraphicsUtils.getResize(centerDown, width - (size * 2), size);			// centerDown
				g.drawImage(tmp, size, height - size);
				tmp = null;

				g.drawImage(leftTop, 0, 0);																	// leftTop
				
				tmp = GraphicsUtils.getResize(leftCenter, leftCenter.getWidth(), width - (size * 2));		// leftCenter
				g.drawImage(tmp, 0, size);
				tmp = null;
				
				g.drawImage(leftDown, 0, height - size);													// leftDown
				
				int right = width - size;		// right 起点
				g.drawImage(rightTop, right, 0);															// rightTop
				
				tmp = GraphicsUtils.getResize(rightCenter, leftCenter.getWidth(), width - (size * 2));		// rightCenter
				g.drawImage(tmp, right, size);
				tmp = null;
				
				g.drawImage(rightDown, right, height - size);												// rightDown

				g.dispose();

				lazyImages.put(keyName, lazyImage);		// 加入到映射 Map

				image.dispose();
				messageImage.dispose();
				
				centerTop.dispose();
				centerDown.dispose();
				leftTop.dispose();
				leftCenter.dispose();
				leftDown.dispose();
				rightTop.dispose();
				rightCenter.dispose();
				rightDown.dispose();

				image = null;
				messageImage = null;
				
				centerTop = null;
				centerDown = null;
				leftTop = null;
				leftCenter = null;
				leftDown = null;
				rightTop = null;
				rightCenter = null;
				rightDown = null;
				
			} catch (Exception e) {

			}
		}
		return lazyImage;
	}

	public static void clear() {
		lazyImages.clear();
	}
}
