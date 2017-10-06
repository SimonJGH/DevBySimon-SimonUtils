package com.simon.utils.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Simon
 * @Description 手机号正则表达式校验相关的辅助类
 * @date createTime: 2016-11-28
 */
public class MobileNumUtils {
	/**
	 * 手机号正则表达式校验
	 */
	public static boolean isMobileNumber(String mobile) {
		Pattern p = Pattern
				.compile("^0?(13[0-9]|15[012356789]|18[0-9]|14[57]|17[0678])[0-9]{8}$");
		Matcher m = p.matcher(mobile);
		return m.matches();
	}
}
