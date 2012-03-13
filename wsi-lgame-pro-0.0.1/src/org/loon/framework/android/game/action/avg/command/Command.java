package org.loon.framework.android.game.action.avg.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.loon.framework.android.game.core.LRelease;
import org.loon.framework.android.game.core.LSystem;
import org.loon.framework.android.game.core.resource.Resources;
import org.loon.framework.android.game.utils.CollectionUtils;
import org.loon.framework.android.game.utils.NumberUtils;
import org.loon.framework.android.game.utils.StringUtils;
import org.loon.framework.android.game.utils.collection.ArrayMap;

/**
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
 * @version 0.1.2
 */

@SuppressWarnings({"unchecked","rawtypes"})
public class Command extends Conversion implements Serializable, LRelease {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	private static HashMap scriptLazy;			// 脚本正文内容的缓存, <文件名， 正文>，类型格式为 <String, String[]>	
	private static HashMap scriptContext;		// 脚本数据缓存
	private static ArrayMap functions;			// 函数列表
	private static HashMap setEnvironmentList;	// 变量列表

	private static ArrayMap conditionEnvironmentList;	// 条件分支列表


	private StringBuffer readBuffer;	// 读入连续数据
	private String cacheCommandName;	// 缓存脚本名


	/**
	 * 注释标记中， 注释符 // 等
	 */
	private boolean flaging = false;	
	/**
	 * 判断标记中， if
	 */
	private boolean ifing = false;			
	/**
	 * 函数标记中， function
	 */
	private boolean functioning = false;	
	/**
	 * 分支标记中， else
	 */
	private boolean esleflag = false;		
	private boolean esleover = false;

	private boolean backIfBool = false;
	private boolean isClose;

	/**
	 * 执行脚本字符串， 如 play res/music/no_say_goodbye.wav
	 */
	private String executeCommand;
	private String nowPosFlagName;
	private boolean addCommand;
	private boolean isInnerCommand;

	private boolean isRead;
	private boolean isCall;
	private boolean isCache;
	private boolean if_bool;
	private boolean elseif_bool;

	private Command innerCommand;

	private List temps;
	private List printTags;
	private List randTags;
	private int offsetPos;

	private String[] scriptList;		// 脚本数据列表
	private int scriptListSize;
	private String scriptName;			// 脚本名

	/**
	 * 构造函数，载入指定脚本文件
	 * 
	 * @param fileName
	 */
	public Command(String fileName) {
		createCache(false);
		formatCommand(fileName);
	}

	/**
	 * 构造函数，载入指定脚本文件
	 * 
	 * @param in
	 */
	public Command(InputStream in) {
		createCache(false);
		formatCommand(in);
	}

	/**
	 * 构造函数，载入指定list脚本
	 * 
	 * @param resource
	 */
	public Command(String fileName, String[] res) {
		createCache(false);
		formatCommand("function", res);
	}

	public static void createCache(boolean free) {
		if (free) {		// 清除缓存时
			if (scriptContext == null) {				// HashMap
				scriptContext = new HashMap(1000);
			} else {
				scriptContext.clear();
			}
			if (functions == null) {					// ArrayMap
				functions = new ArrayMap(20);
			} else {
				functions.clear();
			}
			if (setEnvironmentList == null) {			// HashMap
				setEnvironmentList = new HashMap(20);
			} else {
				setEnvironmentList.clear();
			}
			if (conditionEnvironmentList == null) {		// ArrayMap
				conditionEnvironmentList = new ArrayMap(30);
			} else {
				conditionEnvironmentList.clear();
			}
		} else {		// 不清除缓存时
			if (scriptContext == null) {
				scriptContext = new HashMap(1000);
			}
			if (functions == null) {
				functions = new ArrayMap(20);
			}
			if (setEnvironmentList == null) {
				setEnvironmentList = new HashMap(20);
			}
			if (conditionEnvironmentList == null) {
				conditionEnvironmentList = new ArrayMap(30);
			}
		}
	}

	/**
	 * 格式化脚本命令
	 * 
	 * @param fileName	： 脚本文件名（包含路径）
	 */
	public void formatCommand(String fileName) {
		formatCommand(fileName, Command.includeFile(fileName));
	}

	public void formatCommand(InputStream in) {
		formatCommand("temp" + in.hashCode(), Command.includeFile(in));
	}

	/**
	 * 格式化脚本文件
	 * 
	 * @param fileName	： 脚本文件名（包含路径）， 如： assets/script/s1.txt
	 * @param res		： 脚本文件正文， 如： [set bg_img = "assets/background.png",  //abc123 , set role_01 = "assets/r0.png", ....]，注： 数组以逗号 “,” 隔开了
	 */
	public void formatCommand(String fileName, String[] res) {
		if (functions != null) {
			functions.clear();
		}
		if (conditionEnvironmentList != null) {
			conditionEnvironmentList.clear();
		}
		if (setEnvironmentList != null) {
			setEnvironmentList.put(V_SELECT_KEY, "-1");
		}
		if (readBuffer == null) {
			readBuffer = new StringBuffer(256);
		} else {
			readBuffer.delete(0, readBuffer.length());
		}
		this.scriptName = fileName;
		this.scriptList = res;
		this.scriptListSize = res.length;
		this.offsetPos = 0;
		this.isCache = true;
		this.flaging = false;
		this.ifing = false;
		this.esleflag = false;
		this.backIfBool = false;
		this.functioning = false;
		this.esleover = false;
		this.backIfBool = false;
		this.addCommand = false;
		this.isInnerCommand = false;
		this.isRead = false;
		this.isCall = false;
		this.isCache = false;
		this.if_bool = false;
		this.elseif_bool = false;
	}

	private boolean setupIF(String commandString, String nowPosFlagName, HashMap setEnvironmentList, ArrayMap conditionEnvironmentList) {
		boolean result = false;
		conditionEnvironmentList.put(nowPosFlagName, new Boolean(false));
		try {
			List temps = commandSplit(commandString);
			int size = temps.size();
			Object valueA = null;
			Object valueB = null;
			String condition = null;
			if (size <= 4) {
				valueA = (String) temps.get(1);
				valueB = (String) temps.get(3);
				valueA = setEnvironmentList.get(valueA) == null ? valueA : setEnvironmentList.get(valueA);
				valueB = setEnvironmentList.get(valueB) == null ? valueB : setEnvironmentList.get(valueB);
				condition = (String) temps.get(2);
			} else {
				int count = 0;
				StringBuffer sbr = new StringBuffer();
				for (Iterator it = temps.iterator(); it.hasNext();) {
					String res = (String) it.next();
					if (count > 0) {
						if (!isCondition(res)) {
							sbr.append(res);
						} else {
							valueA = sbr.toString();
							valueA = String.valueOf(exp.parse(valueA));
							sbr.delete(0, sbr.length());
							condition = res;
						}
					}
					count++;
				}
				valueB = sbr.toString();
			}

			// 非纯数字
			if (!NumberUtils.isNan((String) valueB)) {
				try {
					// 尝试四则运算公式匹配
					valueB = exp.parse(valueB);
				} catch (Exception e) {
				}
			}
			// 无法判定
			if (valueA == null || valueB == null) {
				conditionEnvironmentList.put(nowPosFlagName, new Boolean(false));
			}

			// 相等
			if ("==".equals(condition)) {
				conditionEnvironmentList.put(nowPosFlagName, new Boolean( result = valueA.toString().equals(valueB.toString())));
				// 非等
			} else if ("!=".equals(condition)) {
				conditionEnvironmentList.put(nowPosFlagName, new Boolean(result = !valueA.toString().equals(valueB.toString())));
				// 大于
			} else if (">".equals(condition)) {
				float numberA = Float.parseFloat(valueA.toString());
				float numberB = Float.parseFloat(valueB.toString());
				conditionEnvironmentList.put(nowPosFlagName, new Boolean(result = numberA > numberB));
				// 小于
			} else if ("<".equals(condition)) {
				float numberA = Float.parseFloat(valueA.toString());
				float numberB = Float.parseFloat(valueB.toString());
				conditionEnvironmentList.put(nowPosFlagName, new Boolean(result = numberA < numberB));

				// 大于等于
			} else if (">=".equals(condition)) {
				float numberA = Float.parseFloat(valueA.toString());
				float numberB = Float.parseFloat(valueB.toString());
				conditionEnvironmentList.put(nowPosFlagName, new Boolean(result = numberA >= numberB));
				// 小于等于
			} else if ("<=".equals(condition)) {
				float numberA = Float.parseFloat(valueA.toString());
				float numberB = Float.parseFloat(valueB.toString());
				conditionEnvironmentList.put(nowPosFlagName, new Boolean(result = numberA <= numberB));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 打开脚本缓存
	 * 
	 */
	public void openCache() {
		isCache = true;
	}

	/**
	 * 关闭脚本缓存
	 * 
	 */
	public void closeCache() {
		isCache = false;
	}

	/**
	 * 当前脚本行缓存名
	 * 
	 * @return
	 */
	public String nowCacheOffsetName(String cmd) {
		return (scriptName + FLAG + offsetPos + FLAG + cmd).toLowerCase();
	}

	/**
	 * 重启脚本缓存
	 * 
	 */
	public static void resetCache() {
		if (scriptContext != null) {
			scriptContext.clear();
		}
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	/**
	 * 返回当前的读入数据集合
	 * 
	 * @return
	 */
	public synchronized String[] getReads() {
		String result = readBuffer.toString();
		result = result.replaceAll(SELECTS_TAG, "");
		return StringUtils.split(result, FLAG);
	}

	/**
	 * 返回指定索引的读入数据
	 * 
	 * @param index
	 * @return
	 */
	public synchronized String getRead(int index) {
		try {
			return getReads()[index];
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 截取第一次出现的指定标记
	 * 
	 * @param messages
	 * @param startString
	 * @param endString
	 * @return
	 */
	public static String getNameTag(String messages, String startString, String endString) {
		List results = getNameTags(messages, startString, endString);
		return (results == null || results.size() == 0) ? null : (String) results.get(0);
	}

	/**
	 * 截取指定标记内容为list
	 * 
	 * @param messages
	 * @param startString
	 * @param endString
	 * @return
	 */
	public static List getNameTags(String messages, String startString, String endString) {
		return Command.getNameTags(messages.toCharArray(), startString.toCharArray(), endString.toCharArray());
	}

	/**
	 * 截取指定标记内容为list
	 * 
	 * @param messages
	 * @param startString
	 * @param endString
	 * @return
	 */
	public static List getNameTags(char[] messages, char[] startString, char[] endString) {
		int dlength = messages.length;
		int slength = startString.length;
		int elength = endString.length;
		List tagList = new ArrayList(10);
		boolean lookup = false;
		int lookupStartIndex = 0;
		int lookupEndIndex = 0;
		int length;
		StringBuffer sbr = new StringBuffer(100);
		for (int i = 0; i < dlength; i++) {
			char tag = messages[i];
			if (tag == startString[lookupStartIndex]) {
				lookupStartIndex++;
			}
			if (lookupStartIndex == slength) {
				lookupStartIndex = 0;
				lookup = true;
			}
			if (lookup) {
				sbr.append(tag);
			}
			if (tag == endString[lookupEndIndex]) {
				lookupEndIndex++;
			}
			if (lookupEndIndex == elength) {
				lookupEndIndex = 0;
				lookup = false;
				length = sbr.length();
				if (length > 0) {
					tagList.add(sbr.substring(1, sbr.length() - elength));
					sbr.delete(0, length);
				}
			}
		}
		return tagList;
	}

	/**
	 * 注入选择变量
	 * 
	 * @param type
	 */
	public void select(int type) {
		if (innerCommand != null) {
			innerCommand.setVariable(V_SELECT_KEY, String.valueOf(type));
		}
		setVariable(V_SELECT_KEY, String.valueOf(type));
	}

	public String getSelect() {
		return (String) getVariable(V_SELECT_KEY);
	}

	/**
	 * 插入变量
	 * 
	 * @param key
	 * @param value
	 */
	public void setVariable(String key, Object value) {
		setEnvironmentList.put(key, value);
	}

	/**
	 * 插入变量集合
	 * 
	 * @param vars
	 */
	public void setVariables(HashMap vars) {
		setEnvironmentList.putAll(vars);
	}

	/**
	 * 返回变量集合
	 * 
	 * @return
	 */
	public HashMap getVariables() {
		return setEnvironmentList;
	}

	public Object getVariable(String key) {
		return setEnvironmentList.get(key);
	}

	/**
	 * 删除变量
	 * 
	 * @param key
	 */
	public void removeVariable(String key) {
		setEnvironmentList.remove(key);
	}

	/**
	 * 判定脚本是否允许继续解析
	 * 
	 * @return 命令的当前行数小于总行数，即 (offsetPos < scriptListSize)
	 */
	public boolean next() {
		return (offsetPos < scriptListSize);
	}

	/**
	 * 跳转向指定索引位置
	 * 
	 * @param offset
	 * @return
	 */
	public boolean gotoIndex(final int offset) {
		boolean result = offset < scriptListSize && offset > 0 && offset != offsetPos;
		if (result) {
			offsetPos = offset;
		}
		return result;
	}

	public int getIndex() {
		return offsetPos;
	}

	/**
	 * 批处理执行脚本，并返回可用list结果
	 * 
	 * @return
	 */
	public List batchToList() {
		List reslist = new ArrayList(scriptListSize);
		for (; next();) {
			String execute = doExecute();
			if (execute != null) {
				reslist.add(execute);
			}
		}
		return reslist;
	}

	/**
	 * 批处理执行脚本，并返回可用string结果
	 * 
	 * @return
	 */
	public String batchToString() {
		StringBuffer resString = new StringBuffer(scriptListSize * 10);
		for (; next();) {
			String execute = doExecute();
			if (execute != null) {
				resString.append(execute);
				resString.append("\n");
			}
		}
		return resString.toString();
	}

	/**
	 * 解析变量赋值命令， "set"
	 * 
	 * @param cmd
	 */
	private void setupSET(String cmd) {
		if (cmd.startsWith(SET_TAG)) {
			List temps = commandSplit(cmd);		// [set, role_01, =, "assets/r0.png"]
			int len = temps.size();
			String result = null;
			if (len == 4) {
				result = temps.get(3).toString();
			} else if (len > 4) {
				StringBuffer sbr = new StringBuffer(len);
				for (int i = 3; i < temps.size(); i++) {
					sbr.append(temps.get(i));
				}
				result = sbr.toString();
			}

			if (result != null) {
				// 替换已有变量字符
				Set set = setEnvironmentList.entrySet();
				for (Iterator it = set.iterator(); it.hasNext();) {
					Entry entry = (Entry) it.next();
					if (!(result.startsWith("\"") && result.endsWith("\""))) {
						result = StringUtils.replaceMatch(result, (String) entry.getKey(), entry.getValue().toString());
					}
				}
				// 当为普通字符串时
				if (result.startsWith("\"") && result.endsWith("\"")) {
					setEnvironmentList.put(temps.get(1), result.substring(1, result.length() - 1));		// result : "assets/r0.png"
				} else if (StringUtils.isChinaLanguage(result) || StringUtils.isEnglishAndNumeric(result)) {
					setEnvironmentList.put(temps.get(1), result);
				} else {
					// 当为数学表达式时
					setEnvironmentList.put(temps.get(1), exp.parse(result));
				}
			}
			addCommand = false;
		}
	}

	/**
	 * 随机数处理
	 * 
	 */
	private void setupRandom(String cmd) {
		// 随机数判定
		if (cmd.indexOf(RAND_TAG) != -1) {
			randTags = Command.getNameTags(cmd, RAND_TAG + BRACKET_LEFT_TAG, BRACKET_RIGHT_TAG);
			if (randTags != null) {
				for (Iterator it = randTags.iterator(); it.hasNext();) {
					String key = (String) it.next();
					Object value = setEnvironmentList.get(key);
	
					if (value != null) {						// 已存在变量
						cmd = StringUtils.replaceMatch(cmd, (RAND_TAG + BRACKET_LEFT_TAG + key + BRACKET_RIGHT_TAG).intern(), value.toString());
		
					} else if (NumberUtils.isNan(key)) {		// 设定有随机数生成范围
						cmd = StringUtils.replaceMatch(cmd, (RAND_TAG + BRACKET_LEFT_TAG + key + BRACKET_RIGHT_TAG).intern(), String.valueOf(GLOBAL_RAND.nextInt(Integer.parseInt((String) key))));

					} else {									// 无设定
						cmd = StringUtils.replaceMatch(cmd, (RAND_TAG + BRACKET_LEFT_TAG + key + BRACKET_RIGHT_TAG).intern(), String.valueOf(GLOBAL_RAND.nextInt()));
					}
				}
			}
		}
	}

	private void innerCallTrue() {
		isCall = true;
		isInnerCommand = true;
	}

	private void innerCallFalse() {
		isCall = false;
		isInnerCommand = false;
		innerCommand = null;
	}

	/**
	 * 逐行执行脚本命令，如果是命令行，则返回此行； 否则，返回 null
	 * 
	 * @return	如果是命令行，则返回此行（如 fadein black， gb assets/background.png等命令行）； 否则，返回 null
	 */
	public synchronized String doExecute() {
		if (isClose) {
			return null;
		}
		this.executeCommand = null;
		this.addCommand = true;
		this.isInnerCommand = (innerCommand != null);
		this.if_bool = false;
		this.elseif_bool = false;

		try {
			if (isInnerCommand && isCall) {						// 执行call命令
				setVariables(innerCommand.getVariables());
				if (innerCommand.next()) {
					return innerCommand.doExecute();
				} else {
					innerCallFalse();
					return executeCommand;
				}

			} else if (isInnerCommand && !isCall) {				// 执行内部脚本
				setVariables(innerCommand.getVariables());
				if (innerCommand.next()) {
					return innerCommand.doExecute();
				} else {
					innerCommand = null;
					isInnerCommand = false;
					return executeCommand;
				}
			}

			nowPosFlagName = String.valueOf(offsetPos);
			int length = conditionEnvironmentList.size();
			if (length > 0) {
				Object ifResult = conditionEnvironmentList.get(length - 1);
				if (ifResult != null) {
					backIfBool = ((Boolean) ifResult).booleanValue();
				}
			}


			if (scriptList == null) {					// 空指向判定
				resetCache();
				return executeCommand;
			} else if (scriptList.length - 1 < offsetPos) {
				resetCache();
				return executeCommand;
			}

			String cmd = scriptList[offsetPos];			// 获得全行命令, 如： play res/music/no_say_goodbye.wav
			if (cmd.startsWith(RESET_CACHE_TAG)) {		// 清空脚本缓存， reset
				resetCache();
				return executeCommand;
			}

			if (isCache) {
				cacheCommandName = nowCacheOffsetName(cmd);				// 获得缓存命令行名
				Object cache = scriptContext.get(cacheCommandName);		// 读取缓存的脚本
				if (cache != null) {
					return (String) cache;
				}
			}


			if (flaging) {			// 注释中， 开头和结尾都不是 “*/”
				flaging = !(cmd.startsWith(FLAG_LS_E_TAG) || cmd.endsWith(FLAG_LS_E_TAG));
				return executeCommand;
			}

			if (!flaging) {			// 全局注释， 开头和结尾都不是 "/*"
				if (cmd.startsWith(FLAG_LS_B_TAG) && !cmd.endsWith(FLAG_LS_E_TAG)) {
					flaging = true;					// 记录注释开始
					return executeCommand;
				} else if (cmd.startsWith(FLAG_LS_B_TAG) && cmd.endsWith(FLAG_LS_E_TAG)) {
					return executeCommand;
				}
			}

			setupRandom(cmd);		// 执行随机数标记， rand 
			setupSET(cmd);			// 执行获取变量标记， set

			if (cmd.startsWith(BEGIN_TAG)) {		// 脚本 function 开始标记 begin
				temps = commandSplit(cmd);
				if (temps.size() == 2) {
					functioning = true;
					functions.put(temps.get(1), new String[0]);
					return executeCommand;
				}
			}

			if (cmd.endsWith(END_TAG)) {			//  脚本 function 结束标记 end
				functioning = false;
				return executeCommand;
			}

			if (functioning) {						// 脚本 function
				int size = functions.size() - 1;
				String[] function = (String[]) functions.get(size);
				int index = function.length;
				function = (String[]) CollectionUtils.expand(function, 1);
				function[index] = cmd;
				functions.set(size, function);
				return executeCommand;
			}

			// 执行代码段调用标记
			if (((!esleflag && !ifing) || (esleflag && ifing)) && cmd.startsWith(CALL_TAG) && !isCall) {
				temps = commandSplit(cmd);
				if (temps.size() == 2) {
					String functionName = (String) temps.get(1);
					String[] funs = (String[]) functions.get(functionName);
					if (funs != null) {
						innerCommand = new Command(scriptName + FLAG + functionName, funs);
						innerCommand.closeCache();
						innerCommand.setVariables(getVariables());
						innerCallTrue();
						return null;
					}
				}
			}

			if (!if_bool && !elseif_bool) {				// 获得循序结构条件
				if_bool = cmd.startsWith(IF_TAG);
				elseif_bool = cmd.startsWith(ELSE_TAG);
			}

			if (if_bool) {								// 条件判断a
				esleover = esleflag = setupIF(cmd, nowPosFlagName, setEnvironmentList, conditionEnvironmentList);
				addCommand = false;
				ifing = true;

			} else if (elseif_bool) {					// 条件判断b
				String[] value = StringUtils.split(cmd, " ");
				if (!backIfBool && !esleflag) {

					if (value.length > 1 && IF_TAG.equals(value[1])) {					// 存在if判断
						esleover = esleflag = setupIF(cmd.replaceAll(ELSE_TAG, "").trim(), nowPosFlagName, setEnvironmentList, conditionEnvironmentList);
						addCommand = false;

					} else if (value.length == 1 && ELSE_TAG.equals(value[0])) {		// 单纯的else
						if (!esleover) {
							esleover = esleflag = setupIF("if 1==1", nowPosFlagName, setEnvironmentList, conditionEnvironmentList);
							addCommand = false;
						}
					}
				} else {
					esleflag = false;
					addCommand = false;
					conditionEnvironmentList.put(nowPosFlagName, new Boolean(false));

				}
			}

			if (cmd.startsWith(IF_END_TAG)) {			// 分支结束
				conditionEnvironmentList.clear();
				backIfBool = false;
				addCommand = false;
				ifing = false;
				if_bool = false;
				elseif_bool = false;
				esleover = false;
				return null;
			}
			if (backIfBool) {						// 加载内部脚本
				if (cmd.startsWith(INCLUDE_TAG)) {
					if (includeCommand(cmd)) {
						return null;
					}
				}
			} else if (cmd.startsWith(INCLUDE_TAG) && !ifing && !backIfBool && !esleflag) {
				if (includeCommand(cmd)) {
					return null;
				}
			}

			if (cmd.startsWith(IN_TAG)) {			// 选择项列表，开始 in
				readBuffer.delete(0, readBuffer.length());
				isRead = true;
				return executeCommand;
			}
			if (cmd.startsWith(OUT_TAG)) {			// 选择项列表，结束 out
				isRead = false;
				addCommand = false;
				executeCommand = (SELECTS_TAG + " " + readBuffer.toString());
			}

			if (isRead) {							// 累计选择项
				readBuffer.append(cmd);
				readBuffer.append(FLAG);
				addCommand = false;
			}

			if (addCommand && ifing) {				// 输出脚本判断
				if (backIfBool && esleflag) {
					executeCommand = cmd;
				}

			} else if (addCommand) {
				executeCommand = cmd;
			}

			if (executeCommand != null) {			// 替换脚本字符串内容
				printTags = Command.getNameTags(executeCommand, PRINT_TAG + BRACKET_LEFT_TAG, BRACKET_RIGHT_TAG);
				if (printTags != null) {
					for (Iterator it = printTags.iterator(); it.hasNext();) {
						String key = (String) it.next();
						Object value = setEnvironmentList.get(key);
						if (value != null) {
							executeCommand = StringUtils.replaceMatch(executeCommand, (PRINT_TAG + BRACKET_LEFT_TAG + key + BRACKET_RIGHT_TAG).intern(), value.toString());
						} else {
							executeCommand = StringUtils.replaceMatch(executeCommand, (PRINT_TAG + BRACKET_LEFT_TAG + key + BRACKET_RIGHT_TAG).intern(), key);
						}
					}
				}

				if (isCache) {						// 注入脚本缓存
					scriptContext.put(cacheCommandName, executeCommand);
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			if (!isInnerCommand) {
				offsetPos++;
			}
		}

		return executeCommand;
	}

	/**
	 * 载入其它脚本， include
	 * 
	 * @param cmd
	 * @return
	 */
	private final boolean includeCommand(String cmd) {
		temps = commandSplit(cmd);
		StringBuffer sbr = new StringBuffer();
		for (int i = 1; i < temps.size(); i++) {
			sbr.append(temps.get(i));
		}
		String fileName = sbr.toString();
		if (fileName.length() > 0) {
			innerCommand = new Command(fileName);
			isInnerCommand = true;
			return true;
		}
		return false;
	}

	/**
	 * 读取包含指定脚本内容，即脚本文件的正文
	 * 
	 * @param fileName	: 脚本文件名（包含路径）
	 * @return	返回脚本文件正文, 以逗号 "," 隔开
	 */
	public final static String[] includeFile(String fileName) {
		if (scriptLazy == null) {
			scriptLazy = new HashMap(100);
		} else if (scriptLazy.size() > 10000) {
			scriptLazy.clear();
		}
		final int capacity = 2000;
		String key = fileName.trim().toLowerCase();			// 文件名，作为key
		String[] result = (String[]) scriptLazy.get(key);	// 读取文件名的正文，String[]数组
		if (result == null) {	// 正文为空时
			InputStream in = null;
			BufferedReader reader = null;
			result = new String[capacity];
			int length = capacity;
			int index = 0;
			try {
				in = Resources.openResource(fileName);	// 读取脚本
				reader = new BufferedReader(new InputStreamReader(in, LSystem.encoding));
				String record = null;
				for (; (record = reader.readLine()) != null;) {
					record = record.trim();
					if (record.length() > 0 && !record.startsWith(FLAG_L_TAG) && !record.startsWith(FLAG_C_TAG) && !record.startsWith(FLAG_I_TAG)) {	// 不是注释行, // # '
						if (index >= length) {
							result = (String[]) CollectionUtils.expand(result, capacity);
							length += capacity;
						}
						result[index] = record;
						index++;
					}
				}
				result = (String[]) CollectionUtils.copyOf(result, index);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			} finally {
				try {
					in.close();
					in = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (reader != null) {
					try {
						reader.close();
						reader = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			scriptLazy.put(key, result);
			return result;
		} else {
			return CollectionUtils.copyOf(result);
		}

	}

	/**
	 * 获得指定脚本内容
	 * 
	 * @param in
	 * @return
	 */
	public final static String[] includeFile(InputStream in) {
		if (scriptLazy == null) {
			scriptLazy = new HashMap(100);
		} else if (scriptLazy.size() > 10000) {
			scriptLazy.clear();
		}
		final int capacity = 2000;
		String key = String.valueOf(in.hashCode());
		String[] result = (String[]) scriptLazy.get(key);
		if (result == null) {
			BufferedReader reader = null;
			result = new String[capacity];
			int length = capacity;
			int index = 0;
			try {
				reader = new BufferedReader(new InputStreamReader(in, LSystem.encoding));
				String record = null;
				for (; (record = reader.readLine()) != null;) {
					record = record.trim();
					if (record.length() > 0 && !record.startsWith(FLAG_L_TAG) && !record.startsWith(FLAG_C_TAG) && !record.startsWith(FLAG_I_TAG)) {
						if (index >= length) {
							result = (String[]) CollectionUtils.expand(result, capacity);
							length += capacity;
						}
						result[index] = record;
						index++;
					}
				}
				result = (String[]) CollectionUtils.copyOf(result, index);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			} finally {
				try {
					in.close();
					in = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (reader != null) {
					try {
						reader.close();
						reader = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			scriptLazy.put(key, result);
			return result;

		} else {
			return CollectionUtils.copyOf(result);
		}
	}

	/**
	 * 过滤指定脚本文件内容为list
	 * 
	 * @param src
	 * @return
	 */
	public static List commandSplit(final String cmd) {		// set role_01 = "assets/r0.png"
		String result = updateOperator(cmd);				// set@role_01@=@"assets/r0.png"
		String[] cmds = result.split(FLAG);					// [set, role_01, =, "assets/r0.png"]
		return Arrays.asList(cmds);
	}

	/**
	 * 释放并清空全部缓存资源
	 * 
	 */
	public final static void releaseCache() {
		if (setEnvironmentList != null) {
			setEnvironmentList.clear();
			setEnvironmentList = null;
		}
		if (conditionEnvironmentList != null) {
			conditionEnvironmentList.clear();
			conditionEnvironmentList = null;
		}
		if (functions != null) {
			functions.clear();
			functions = null;
		}
		if (scriptContext != null) {
			scriptContext.clear();
			scriptContext = null;
		}
		if (scriptLazy != null) {
			scriptLazy.clear();
			scriptLazy = null;
		}
		System.gc();
	}

	public boolean isClose() {
		return isClose;
	}

	public void dispose() {
		this.isClose = true;
		if (readBuffer != null) {
			readBuffer = null;
		}
		if (temps != null) {
			try {
				temps.clear();
				temps = null;
			} catch (Exception e) {
			}
		}
		if (printTags != null) {
			printTags.clear();
			printTags = null;
		}
		if (randTags != null) {
			randTags.clear();
			randTags = null;
		}
		if (exp != null) {
			exp.dispose();
		}
	}
}