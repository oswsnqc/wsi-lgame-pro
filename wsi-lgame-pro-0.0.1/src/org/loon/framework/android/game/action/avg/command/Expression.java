package org.loon.framework.android.game.action.avg.command;

import java.util.Random;

import org.loon.framework.android.game.core.LSystem;

/**
 * Copyright 2008 - 2009
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
 * @version 0.1
 */
public interface Expression {

	/**
	 * 全局随机数, LSystem.random
	 */
	Random GLOBAL_RAND = LSystem.random;

	/**
	 * 默认变量1,用于记录当前选择项, 字符串值： "SELECT"
	 */
	String V_SELECT_KEY = "SELECT";

	/**
	 * 左括号标记， "("
	 */
	String BRACKET_LEFT_TAG = "(";

	/**
	 * 右括号标记， ")"
	 */
	String BRACKET_RIGHT_TAG = ")";

	/**
	 * 代码段开始标记， "begin"
	 */
	String BEGIN_TAG = "begin";

	/**
	 * 代码段结束标记， "end"
	 */
	String END_TAG = "end";

	/**
	 * 代码段调用标记， "call"
	 */
	String CALL_TAG = "call";

	/**
	 * 缓存刷新标记， "reset"
	 */
	String RESET_CACHE_TAG = "reset";
	
	/**
	 * 累计输入数据标记， "in"
	 */
	String IN_TAG = "in";

	/**
	 * 累计输入数据停止（输出）标记， "out"
	 */
	String OUT_TAG = "out";

	/**
	 * 多选标记， "selects"
	 */
	String SELECTS_TAG = "selects";

	/**
	 * 打印标记， "print"
	 */
	String PRINT_TAG = "print";

	/**
	 * 随机数标记， "rand"
	 */
	String RAND_TAG = "rand";

	/**
	 * 设定环境变量标记， "set"
	 */
	String SET_TAG = "set";

	/**
	 * 载入内部脚本标记， "include"
	 */
	String INCLUDE_TAG = "include";

	/**
	 * 条件判定标记， "if"
	 */
	String IF_TAG = "if";

	/**
	 * 条件判定结束标记， "endif"
	 */
	String IF_END_TAG = "endif";

	/**
	 * 转折标记， "else"
	 */
	String ELSE_TAG = "else";

	/**
	 * 单行注释标记， "//"
	 */
	String FLAG_L_TAG = "//";

	/**
	 * 单行注释标记， "#"
	 */
	String FLAG_C_TAG = "#";

	/**
	 * 单行注释标记， "'"
	 */
	String FLAG_I_TAG = "'";

	/**
	 * 多行注释标记， 左侧 "/*"
	 */
	String FLAG_LS_B_TAG = "/*";

	/**
	 * 多行注释标记， 左侧 *\/
	 */
	String FLAG_LS_E_TAG = "*/";

	
	String FLAG = "@";
	/**
	 * 字符串转换成字符数组标记， "@"
	 */
	char FLAG_CHAR = FLAG.toCharArray()[0];

}
