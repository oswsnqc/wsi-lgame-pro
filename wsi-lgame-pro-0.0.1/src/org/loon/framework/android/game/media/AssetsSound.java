package org.loon.framework.android.game.media;

import org.loon.framework.android.game.core.LSystem;

import android.content.Context;

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

public class AssetsSound implements Runnable,
		android.media.MediaPlayer.OnCompletionListener,
		android.media.MediaPlayer.OnErrorListener,
		android.media.MediaPlayer.OnBufferingUpdateListener {

	public static final int PREPARED = 0;
	public static final int PLAYING = 1;
	public static final int PAUSE = 2;
	public static final int EXIT = 3;
	/**
	 * 记录播放状态， 0 - PREPARED； 1 - PLAYING； 2 - PAUSE； 3 - EXIT（status默认为0）
	 */
	private int status = 0;		

	private Object lock;

	private boolean isLoop;

	private int buffer;

	private boolean isDone;

	private boolean isStarted;
	private boolean isPaused;

	private int volume;

	/**
	 * MediaPlayer, 即 android.media.MediaPlayer
	 */
	private android.media.MediaPlayer mediaPlayer;

	/**
	 * 播放文件名（包含路径名）， 如： res/music/mvideo.mp4
	 */
	private String fileName;
	/**
	 * 获取上下文 Activity，即 LSystem.getActivity()
	 */
	private Context context;
	private Thread soundThread;

	/**
	 * 构造函数， 初始化播放文件
	 * 
	 * @param file	： 播放文件名（包含路径），如： res/music/mvideo.mp4
	 */
	public AssetsSound(String file) {
		this.lock = new Object();
		this.fileName = file;
		this.context = LSystem.getActivity();
		this.volume = 3;
	}

	public void run() {
		synchronized (this.lock) {
			if (mediaPlayer == null) {
				mediaPlayer = new android.media.MediaPlayer();
			}
		}
		try {
			mediaPlayer.setOnCompletionListener(this);
			mediaPlayer.setOnErrorListener(this);
			mediaPlayer.setOnBufferingUpdateListener(this);

			synchronized (this.lock) {
				while (fileName == null && (this.status == PREPARED || this.status == PLAYING)) {
					this.lock.wait(250);
				}
			}
			if (fileName == null) {
				synchronized (this.lock) {
					this.status = EXIT;
				}
			} else {
				setDataSource(fileName, isLoop);		// 加载文件

			}
			synchronized (this.lock) {
				while (this.status == PREPARED) {
					this.lock.wait();
				}
			}

			int currentStatus = 0;
			synchronized (this.lock) {
				currentStatus = this.status;
			}

			if (currentStatus == PLAYING) {
				mediaPlayer.start();
			}

//			synchronized (this.lock) {
//				this.isStarted = true;
//
//				while (this.status == PLAYING) {
//					if (!this.isDone) {
//						int duration = mediaPlayer == null ? 0 : mediaPlayer.getDuration();
////						int duration = 10000;
//						int position = mediaPlayer.getCurrentPosition();
//						if (duration > 0 && position + 10000 > duration) {
//							this.isDone = true;
//							this.schedule(duration, position);
//						}
//					}
//					this.lock.wait(3000);
//				}
//				this.isStarted = false;
//			}
//
//			mediaPlayer.stop();
//			mediaPlayer.release();
//			synchronized (this.lock) {
//				mediaPlayer = null;
//			}

		} catch (Exception e) {
		}
		synchronized (this.lock) {
			this.status = EXIT;
		}
		callback();

	}

	/**
	 * 音乐播放进度，重载此函数可获得音乐的播放流程
	 * 
	 * @param duration
	 * @param position
	 */
	public void schedule(int duration, int position) {

	}

	/**
	 * 回调函数，当音乐播放完毕后将调用此函数。
	 */
	public void callback() {

	}

	/**
	 * 播放音乐
	 */
	public void play() {
		try {
			synchronized (this.lock) {
				stopLoop();
				if (status != PLAYING) {
					this.isLoop = false;
					this.isPaused = false;
					this.status = PLAYING;
					soundThread = new Thread(this);		// 当前线程this上下文，运行 run()
					soundThread.start();
					this.lock.notifyAll();
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 播放指定音乐
	 * 
	 * @param file	: 指定音乐的文件
	 */
	public void play(String file) {
		try {
			synchronized (this.lock) {
				stopLoop();
				if (status != PLAYING) {
					this.isLoop = false;
					this.isPaused = false;
					this.status = PLAYING;
					this.fileName = file;
					soundThread = new Thread(this);
					soundThread.start();
					this.lock.notifyAll();
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 循环播放音乐
	 */
	public void loop() {
		try {
			synchronized (this.lock) {
				stopLoop();
				if (status != PLAYING) {
					this.isLoop = true;
					this.isPaused = false;
					this.status = PLAYING;
					soundThread = new Thread(this);
					soundThread.start();
					this.lock.notifyAll();
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 停止线程用循环
	 */
	private void stopLoop() {
		if (isLoop) {
			if (mediaPlayer != null) {
				status = EXIT;
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}
		}
	}

	/**
	 * 停止音乐播放
	 */
	public void stop() {
		synchronized (this.lock) {
			this.isLoop = false;
			this.status = EXIT;
			this.lock.notifyAll();
		}
	}

	/**
	 * 释放音乐播放所占据的空间
	 */
	public void release() {
		synchronized (this.lock) {
			if (this.mediaPlayer != null) {
				this.isLoop = false;
				this.status = EXIT;
				this.mediaPlayer.release();
				this.lock.notifyAll();
			}
		}
	}

	/**
	 * 暂停音乐播放
	 */
	public void pause() {
		synchronized (this.lock) {
			if (this.mediaPlayer != null) {
				if (this.isPaused) {
					this.mediaPlayer.start();
				} else {
					this.mediaPlayer.pause();
				}
				this.isPaused = true;
				this.lock.notifyAll();
			}
		}
	}

	/**
	 * 刷新音乐播放
	 */
	public void reset() {
		this.stop();
	}

	/**
	 * 设定音乐源, 默认不循环 即 false
	 * 
	 * @param fileName	： 音频文件名（包含路径）
	 */
	public void setDataSource(String fileName) {
		setDataSource(fileName, false);
	}

	/**
	 * 设定音乐播放源，并设置是否循环播放
	 * 
	 * @param fileName	： 音频文件名（包含路径）
	 * @param looping	： 是否循环， true - 循环； false - 不循环
	 */
	public void setDataSource(String fileName, boolean looping) {
		synchronized (this.lock) {
			try {
//				mediaPlayer.setDataSource("sdcard/test.mp4");
				
//				mediaPlayer.setDataSource(
//						context.getAssets().openFd(fileName).getFileDescriptor(), 
//						context.getAssets().openFd(fileName).getStartOffset(), 
//						context.getAssets().openFd(fileName).getLength()
//					);

				mediaPlayer.setDataSource(
						context.getResources().getAssets().openFd(fileName).getFileDescriptor(), 
						context.getResources().getAssets().openFd(fileName).getStartOffset(), 
						context.getResources().getAssets().openFd(fileName).getLength()
					);
				mediaPlayer.prepare();
				mediaPlayer.setLooping(looping);
			} catch (Exception e) {
				e.printStackTrace();
			}
			lock.notifyAll();
		}
	}

	private void exit() {
		synchronized (this.lock) {
			this.isLoop = false;
			this.status = EXIT;
			this.lock.notifyAll();
		}
		try {
			soundThread.join();
		} catch (InterruptedException e) {
		}
	}

	public void setLooping(boolean isLooping) {
		synchronized (this.lock) {
			if (this.mediaPlayer != null && this.isStarted) {
				isLoop = isLooping;
				mediaPlayer.setLooping(isLooping);
				lock.notifyAll();
			}
		}
	}

	public boolean isLooping() {
		synchronized (this.lock) {
			if (this.mediaPlayer != null && this.isStarted) {
				return mediaPlayer.isLooping();
			}
			return false;
		}
	}

	public boolean isPlaying() {
		synchronized (this.lock) {
			if (this.mediaPlayer != null && this.isStarted) {
				return mediaPlayer.isPlaying();
			}
			return false;
		}
	}

	/**
	 * 设置音量，vol值在 1 —— 10之间
	 * @param vol	： 音量值，范围为 1-10之间，即 Math.log10(vol)
	 */
	public void setVolume(int vol) {
		synchronized (this.lock) {
			if (this.mediaPlayer != null && this.isStarted) {
				this.volume = vol;
				mediaPlayer.setVolume((float) Math.log10(vol), (float) Math.log10(vol));
				lock.notifyAll();
			}
		}
	}
	
	public int getVolume(){
		return this.volume;
	}

	public int getPosition() {
		synchronized (this.lock) {
			if (this.mediaPlayer != null && this.isStarted) {
				return this.mediaPlayer.getCurrentPosition();
			}
		}
		return 0;
	}

	public int getDuration() {
		synchronized (this.lock) {
			if (this.mediaPlayer != null && this.isStarted) {
				return this.mediaPlayer.getDuration();
			}
		}
		return 0;
	}

	protected void finalize() throws Throwable {
		this.exit();
		super.finalize();
	}

	public String getName() {
		return fileName;
	}

	public void onCompletion(android.media.MediaPlayer mp) {
		synchronized (this.lock) {
			this.status = EXIT;
			this.lock.notifyAll();
		}
	}

	public boolean onError(android.media.MediaPlayer mp, int what, int extra) {
		synchronized (this.lock) {
			this.status = EXIT;
			this.lock.notifyAll();
		}
		return false;
	}

	public void onBufferingUpdate(android.media.MediaPlayer mp, int percent) {
		synchronized (this.lock) {
			this.buffer = percent;
		}
	}

	public int getBuffer() {
		synchronized (this.lock) {
			return this.buffer;
		}
	}

}
