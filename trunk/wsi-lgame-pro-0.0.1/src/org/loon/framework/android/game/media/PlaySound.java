package org.loon.framework.android.game.media;

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

public class PlaySound {

	private final PlaySoundManager playSoundManager;

	private float playVol;

	private final int resId;

	private int playerSoundId;

	private int streamId ;

	public PlaySound(PlaySoundManager playSoundManager, int resId, float vol) {
		this.playSoundManager = playSoundManager;
		this.resId = resId;
		this.playVol = vol;
		this.playerSoundId = -1;
	}

	final int getResourceId() {
		return resId;
	}

	final void setSoundId(int id) {
		playerSoundId = id;
	}

	final int getSoundId() {
		return playerSoundId;
	}

	final void setStreamId(int id) {
		streamId = id;
	}

	final int getStreamId() {
		return streamId;
	}

	final void setVol(float vol) {
		playVol = vol;
	}

	final float getVol() {
		return playVol;
	}

	public void play() {
		playSoundManager.play(this, playVol, false);
	}

	public void play(float vol) {
		playSoundManager.play(this, vol * playVol, false);
	}

	public void play(float vol, boolean loop) {
		playSoundManager.play(this, vol * playVol, loop);
	}

	public void loop() {
		playSoundManager.play(this, playVol, true);
	}

	public void stop() {
		playSoundManager.stop(this);
	}

	public final boolean isPlaying() {
		return streamId != 0;
	}

}
