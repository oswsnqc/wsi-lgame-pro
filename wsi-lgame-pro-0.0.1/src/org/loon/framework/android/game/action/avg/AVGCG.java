package org.loon.framework.android.game.action.avg;

import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.utils.GraphicsUtils;
import org.loon.framework.android.game.utils.collection.ArrayMap;

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

public class AVGCG {

	private LImage background;

	private ArrayMap charas;

	public AVGCG() {
		charas = new ArrayMap(100);
	}

	public LImage getBackgroundCG() {
		return background;
	}

	public void noneBackgroundCG() {
		if (background != null) {
			background.dispose();
			background = null;
		}
	}

	public void setBackgroundCG(LImage backgroundCG) {
		if (background == backgroundCG) {
			return;
		}
		if (background != null) {
			background.dispose();
			background = null;
		}
		this.background = backgroundCG;
	}

	public void setBackgroundCG(String resName) {
		setBackgroundCG(GraphicsUtils.loadImage(resName));
	}

	public void addChara(String file, AVGChara role) {
		charas.put(file.replaceAll(" ", "").toLowerCase(), role);
	}

	public void addImage(String name, int x, int y, int w) {
		String keyName = name.replaceAll(" ", "").toLowerCase();
		AVGChara chara = (AVGChara) charas.get(keyName);
		if (chara == null) {
			charas.put(keyName, new AVGChara(name, x, y, w));
		} else {
			chara.setX(x);
			chara.setY(y);
		}
	}

	public AVGChara removeImage(String file) {
		return (AVGChara) charas.remove(file.replaceAll(" ", "").toLowerCase());
	}

	public void dispose() {
		for (int i = 0; i < charas.size(); i++) {
			AVGChara ch = (AVGChara) charas.get(i);
			ch.dispose();
			ch = null;
		}
		charas.clear();
		System.gc();
	}

	public void clear() {
		charas.clear();
	}

	public ArrayMap getCharas() {
		return charas;
	}

	public int count() {
		if (charas != null) {
			return charas.size();
		}
		return 0;
	}
}
