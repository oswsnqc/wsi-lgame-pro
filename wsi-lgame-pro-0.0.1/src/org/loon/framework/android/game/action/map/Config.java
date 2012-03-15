package org.loon.framework.android.game.action.map;

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

public interface Config {

	public static final int EMPTY = -1;
	
	public static final int LEFT_UP = 0;		// (-1, -1)

	public static final int RIGHT_DOWN = 1;		// (1, 1)

	public static final int RIGHT_UP = 2;		// (1, -1)

	public static final int LEFT_DOWN = 3;		// (-1, 1)

	public static final int TLEFT = 4;			// (-1, 0)

	public static final int TRIGHT = 5;			// (1, 0)

	public static final int TUP = 6;			// (0, -1)

	public static final int TDOWN = 7;			// (0, 1)

}
