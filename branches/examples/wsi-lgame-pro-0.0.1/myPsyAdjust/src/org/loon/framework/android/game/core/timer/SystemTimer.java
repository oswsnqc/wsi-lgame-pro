package org.loon.framework.android.game.core.timer;

import android.util.Log;

/**
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
 * @email：ceponline@yahoo.com.cn
 * @version 0.1.3
 */
public class SystemTimer {

	/**
	 * 最后的记录时间，每计算一次流逝时间后，更新一次 lastTime = System.currentTimeMillis()
	 */
	private long lastTime = 0;

	private long virtualTime = 0;

	/**
	 * 记录的开始时间 System.currentTimeMillis()
	 */
	public SystemTimer() {
		start();
	}

	/**
	 * 记录的开始时间 System.currentTimeMillis()
	 */
	public void start() {
		lastTime = System.currentTimeMillis();
		virtualTime = 0;
	}

	/**
	 * 睡眠时间， 以微秒计算
	 * 
	 * @param goalTimeMicros	： 全局时间
	 * @return	返回流逝时间间隔 virtualTime，其中 virtualTime += System.currentTimeMillis() - lastTime， 以微妙计算
	 */
	public long sleepTimeMicros(long goalTimeMicros) {
		long time = goalTimeMicros - getTimeMicros();
		if (time > 100) {
			try {
//				Thread.sleep((int) ((time + 100) / 1000));	// 微妙转换为毫秒， sleep(ms)
				Thread.sleep(time/1000L);	// 微妙转换为毫秒， sleep(ms)
			} catch (InterruptedException ex) {
			}
		}
		return getTimeMicros();
	}

	/**
	 * 
	 * 睡眠时间， 以微秒计算
	 * 
	 * @param goalTimeMicros	： 全局时间
	 * @param timer				： SystemTimer 对象实例
	 * @return	返回流逝时间间隔 virtualTime，其中 virtualTime += System.currentTimeMillis() - lastTime， 以微妙计算
	 */
	public static long sleepTimeMicros(long goalTimeMicros, SystemTimer timer) {
		long time = goalTimeMicros - timer.getTimeMicros();
		if (time > 100) {
			try {
				Thread.sleep((int) ((time + 100) / 1000));
			} catch (InterruptedException ex) {
			}
		}
		return timer.getTimeMicros();
	}

	/**
	 * 获取流逝时间 virtualTime += System.currentTimeMillis() - lastTime，以毫秒计算
	 * 
	 * @return virtualTime，其中 virtualTime += System.currentTimeMillis() - lastTime
	 */
	public long getTimeMillis() {
		long time = System.currentTimeMillis();
		if (time > lastTime) {
			virtualTime += time - lastTime;
//			Log.i("virtualTime", "time: " + time + "; lastTime: " + lastTime + "; virtualTime: " + virtualTime);
		}
		lastTime = time;
		
		return virtualTime;
	}

	/**
	 * 获取流逝时间 virtualTime += System.currentTimeMillis() - lastTime，以微妙计算
	 * 
	 * @return virtualTime * 1000，其中 virtualTime += System.currentTimeMillis() - lastTime
	 */
	public long getTimeMicros() {
		return getTimeMillis() * 1000;
	}
}
