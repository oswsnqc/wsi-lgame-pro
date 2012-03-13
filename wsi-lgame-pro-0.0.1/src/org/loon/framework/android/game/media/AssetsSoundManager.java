package org.loon.framework.android.game.media;

import java.util.Iterator;
import java.util.Map.Entry;

import org.loon.framework.android.game.utils.collection.ArrayMap;

/**
 * 
 * Copyright 2008 - 2010
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
 * @version 0.1.0
 */
public class AssetsSoundManager {

	private static AssetsSoundManager assetsSoundManager;

	/**
	 * ArrayMap 实例， 保存 (name, AssetsSound)
	 */
	private ArrayMap sounds = new ArrayMap(50);

	private int clipCount = 0;

	private boolean isPaused;

	private AssetsSound assertSound;

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
		if (sounds.containsKey(name)) {		// 包含，直接取出并播放
			AssetsSound assetSound = ((AssetsSound) sounds.get(name));
			assetSound.setVolume(vol);
			assetSound.play();
		} else {							// 不包含，添加进 sounds 映射
			if (clipCount > 50) {	// 已满
				int idx = sounds.size() - 1;
				String k = (String) sounds.keySet().toArray()[idx];		// 取数组最后一元素
				AssetsSound clip = (AssetsSound) sounds.remove(k);
				clip.stop();
				clip = null;
				clipCount--;
			}
			assertSound = new AssetsSound(name);
			assertSound.setVolume(vol);
			assertSound.play();
			
			sounds.put(name, assertSound);	// 添加
			clipCount++;
		}
	}

	public synchronized void stopSound(int index) {
		AssetsSound sound = (AssetsSound) sounds.get(index);
		if (sound != null) {
			sound.stop();
		}
	}

	public synchronized void playSound(String name, boolean loop) {
		if (isPaused) {
			return;
		}
		if (sounds.containsKey(name)) {
			AssetsSound ass = ((AssetsSound) sounds.get(name));
			if (loop) {
				ass.loop();
			} else {
				ass.play();
			}
		} else {
			if (clipCount > 50) {
				int idx = sounds.size() - 1;
				String k = (String) sounds.keySet().toArray()[idx];
				AssetsSound clip = (AssetsSound) sounds.remove(k);
				clip.stop();
				clip = null;
				clipCount--;
			}
			assertSound = new AssetsSound(name);
			if (loop) {
				assertSound.loop();
			} else {
				assertSound.play();
			}
			sounds.put(name, assertSound);
			clipCount++;
		}
	}

	public synchronized void stopSoundAll() {
		if (sounds != null) {
			for (Iterator<?> it = sounds.iterator(); it.hasNext();) {
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
		if (assertSound != null) {
			assertSound.reset();
		}
	}

	public synchronized void stopSound() {
		if (assertSound != null) {
			assertSound.stop();
		}
	}

	public synchronized void release() {
		if (assertSound != null) {
			assertSound.release();
		}
	}

	public synchronized void setSoundVolume(int vol) {
		if (assertSound != null) {
			assertSound.setVolume(vol);
		}
	}
	
	public int getSoundVolume(){
		if(assertSound != null){
			return assertSound.getVolume();
		}
		
		return 3;
	}

	public synchronized void pause(boolean pause) {
		isPaused = pause;
	}

}
