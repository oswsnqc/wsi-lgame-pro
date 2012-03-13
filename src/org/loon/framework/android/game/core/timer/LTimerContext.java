package org.loon.framework.android.game.core.timer;

/**
 * 
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
 * @email ceponline@yahoo.com.cn
 * @version 0.1.1
 */
public class LTimerContext {

	/**
	 * 自从上次更新后的流逝时间
	 */
	public long timeSinceLastUpdate;
	/**
	 * 睡眠时间，以毫秒计算 ms
	 */
	public long millisSleepTime;

	public LTimerContext() {
		timeSinceLastUpdate = 0;
	}

	/**
	 * 设置 自从上次更新后的流逝时间
	 * 
	 * @param timeSinceLastUpdate	： 自从上次更新后的流逝时间
	 */
	public synchronized void setTimeSinceLastUpdate(long timeSinceLastUpdate) {
		this.timeSinceLastUpdate = timeSinceLastUpdate;
	}

	/**
	 * 获得 自从上次更新后的流逝时间
	 * 
	 * @return 自从上次更新后的流逝时间
	 */
	public synchronized long getTimeSinceLastUpdate() {
		return timeSinceLastUpdate;
	}

	/**
	 * 设置 睡眠时间
	 * 
	 * @param millisSleepTime	： 睡眠时间，以毫秒计算 ms
	 */
	public void setSleepTimeMillis(long millisSleepTime) {
		this.millisSleepTime = millisSleepTime;
	}

	/**
	 * 获取 睡眠时间
	 *  
	 * @return 睡眠时间，以毫秒计算 ms
	 */
	public long getSleepTimeMillis() {
		return millisSleepTime;
	}

}
