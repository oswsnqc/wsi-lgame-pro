package org.loon.framework.android.game.media;

import java.util.Iterator;
import java.util.Map.Entry;

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

public class AssetsSoundManager {

	private static AssetsSoundManager assetsSoundManager;

	/**
	 * ArrayMap 实例， 保存 (name, AssetsSound)
	 */
	private ArrayMap soundMap = new ArrayMap(50);

	private int clipCount = 0;

	private boolean isPaused;

	private AssetsSound assetSound;

	private AssetsSoundManager() {
	}

	/**
	 * 返回音频播放管理器 AssetsSoundManager 实例
	 * 
	 * @return 返回音频播放管理器实例 AssetsSoundManager
	 */
	final static public AssetsSoundManager getInstance() {
		if (assetsSoundManager == null) {
			return (assetsSoundManager = new AssetsSoundManager());
		}
		return assetsSoundManager;
	}

	public synchronized void playSound(String name, int vol) {
		if (isPaused) {
			return;
		}
		if (soundMap.containsKey(name)) {		// 包含，直接取出并播放
			AssetsSound ass = ((AssetsSound) soundMap.get(name));
			ass.setVolume(vol);
			ass.play();
		} else {							// 不包含，添加进 soundMap 映射
			if (clipCount > 50) {	// 已满
				int idx = soundMap.size() - 1;
				String k = (String) soundMap.keySet().toArray()[idx];		// 取数组最后一元素
				AssetsSound clip = (AssetsSound) soundMap.remove(k);
				clip.stop();
				clip = null;
				clipCount--;
			}
			assetSound = new AssetsSound(name);
			assetSound.setVolume(vol);
			assetSound.play();
			
			soundMap.put(name, assetSound);	// 添加
			clipCount++;
		}
	}

	public synchronized void playSound(String name, boolean loop) {
		if (isPaused) {
			return;
		}
		if (soundMap.containsKey(name)) {
			AssetsSound ass = ((AssetsSound) soundMap.get(name));
			if (loop) {
				ass.loop();
			} else {
				ass.play();
			}
		} else {
			if (clipCount > 50) {
				int idx = soundMap.size() - 1;
				String k = (String) soundMap.keySet().toArray()[idx];
				AssetsSound ass = (AssetsSound) soundMap.remove(k);
				ass.stop();
				ass = null;
				clipCount--;
			}
			assetSound = new AssetsSound(name);
			if (loop) {
				assetSound.loop();
			} else {
				assetSound.play();
			}
			soundMap.put(name, assetSound);
			clipCount++;
		}
	}

	public synchronized void stopSound(int index) {
		AssetsSound ass = (AssetsSound) soundMap.get(index);
		if (ass != null) {
			ass.stop();
		}
	}

	public synchronized void stopSoundAll() {
		if (soundMap != null) {
			for (Iterator<?> it = soundMap.iterator(); it.hasNext();) {
				Entry<?, ?> sound = (Entry<?, ?>) it.next();
				if (sound != null) {
					AssetsSound as = (AssetsSound) sound.getValue();
					if (as != null) {
						as.stop();
					}
				}
			}
		}
	}

	public synchronized void resetSound() {
		if (assetSound != null) {
			assetSound.reset();
		}
	}

	public synchronized void stopSound() {
		if (assetSound != null) {
			assetSound.stop();
		}
	}

	public synchronized void release() {
		if (assetSound != null) {
			assetSound.release();
		}
	}

	public synchronized void setSoundVolume(int vol) {
		if (assetSound != null) {
			assetSound.setVolume(vol);
		}
	}
	
	public int getSoundVolume(){
		if(assetSound != null){
			return assetSound.getVolume();
		}
		
		return 3;
	}

	public synchronized void pause(boolean pause) {
		isPaused = pause;
	}

}
