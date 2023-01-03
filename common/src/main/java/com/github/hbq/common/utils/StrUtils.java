package com.github.hbq.common.utils;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/**
 * @author hbq
 */
public final class StrUtils
{

    public static final String PREFIX_VARIABLE = "${";

    public static final String SUFFIX_VARIABLE = "}";
    public static final char placeholderSuffix = '}';
    public static final char placeholderPrefix = '{';
    public static final char placeholderDollar = '$';
    public static final char placeholderNum = '#';

    private static final char HexCharArr[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final String HexStr = "0123456789abcdef";

    /**
     * 判断字符串是否为空, <code>null</code>字符串也判定为空
     *
     * @param str
     * @return
     */
    public static boolean strEmpty(String str)
    {
        return str == null || "".equals(str) || "null".equalsIgnoreCase(str);
    }

    /**
     * 判断字符串是否不为空, <code>null</code>字符串也判定为空
     *
     * @param str
     * @return
     */
    public static boolean strNotEmpty(String str)
    {
        return !strEmpty(str);
    }

    /**
     * 判断字符串是否为 <code>${}</code> 定义的变量
     *
     * @param variable
     * @return
     */
    public static boolean isVariable(String variable)
    {
        return !strEmpty(variable) && variable.trim().startsWith(PREFIX_VARIABLE) && variable.trim().endsWith(SUFFIX_VARIABLE);
    }

    /**
     * 解析 <code>${}</code> 包裹的变量名
     *
     * @param variable
     * @return
     */
    public static String getVariableKey(String variable)
    {
        if (isVariable(variable))
        {
            variable = variable.trim();
            variable = variable.substring(PREFIX_VARIABLE.length(), variable.length() - SUFFIX_VARIABLE.length());
            int idx = variable.indexOf(':');
            if (idx > 0)
            {
                return variable.substring(0, idx);
            }
            else
            {
                return variable;
            }
        }
        else
        {
            return variable;
        }
    }

    /**
     * 根据变量KEY从spring环境配置中获取真实的变量值
     *
     * @param environment
     * @param value       这个KEY可以是带${}的全el表达式也可以使变量名
     * @return 如果查询不到变量，则返回null
     */
    public static String getVariableWithValueAnnotation(Environment environment, String value)
    {
        return getVariableWithValueAnnotation(environment, value, null);
    }

    /**
     * 根据变量KEY从spring环境配置中获取真实的变量值
     *
     * @param environment
     * @param value        这个KEY可以是带${}的全el表达式也可以使变量名
     * @param defaultValue 默认值
     * @return 如果查不到返回默认值
     */
    public static String getVariableWithValueAnnotation(Environment environment, String value, String defaultValue)
    {
        if (environment == null)
        {
            throw new NullPointerException("未提供org.springframework.core.env.Environment环境变量");
        }
        if (isVariable(value))
        {
            value = getVariableKey(value);
            int idx = value.indexOf(':');
            if (idx > 0)
            {
                value = value.substring(0, idx);
                defaultValue = value.substring(idx + 1);
            }
            return environment.getProperty(value, defaultValue);
        }
        return value;
    }

    /**
     * 对关键字做脱敏处理
     *
     * @param key        关键字
     * @param startIndex 脱敏的开始位置
     * @param endIndex   脱敏的结束位置
     * @return
     */
    public static String desensitive(String key, int startIndex, int endIndex)
    {
        if (StringUtils.isBlank(key))
        {
            return "";
        }
        char[] chars = key.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++)
        {
            if (i >= startIndex && i <= endIndex)
            {
                sb.append("*");
            }
            else
            {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 对关键字做脱敏处理
     *
     * @param key        关键字
     * @param startIndex 脱敏的开始位置
     * @return
     */
    public static String desensitive(String key, int startIndex)
    {
        if (StringUtils.isBlank(key))
        {
            return "";
        }
        return desensitive(key, startIndex, key.length() - 1);
    }

    /**
     * 文本替换，忽略正则表达式
     *
     * @param str
     * @param quote
     * @param replacement
     * @return
     */
    public static String replaceWithQuote(String str, String quote, String replacement)
    {
        String q = Pattern.quote(quote);
        return strEmpty(str) ? str : str.replaceAll(q, replacement);
    }

    /**
     * 文本替换
     *
     * @param str
     * @param regex
     * @param replacement
     * @return
     */
    public static String replaceWithRegex(String str, String regex, String replacement)
    {
        return strEmpty(str) ? str : str.replaceAll(regex, replacement);
    }

    /**
     * 使用一组替换值 逐个按照次序替换文本
     *
     * @param str
     * @param quote
     * @param replacements
     * @return
     */
    public static String replaceWithQuote(String str, String quote, List<String> replacements)
    {
        String q = Pattern.quote(quote);
        if (strEmpty(str))
        {
            return str;
        }
        Count c = Count.unsafe();
        while (str.contains(quote))
        {
            String replacement = replacements.get(c.intValue());
            if (replacement.contains(quote))
            {
                throw new IllegalArgumentException();
            }
            str = str.replaceFirst(q, replacement);
            c.incrementAndGet();
        }
        return str;
    }

    /**
     * 替换给定字符串中的占位符变量值
     *
     * @param replacement
     * @param map
     * @return
     */
    public static String replacePlaceHolders(String replacement, Map map)
    {
        return replacePlaceHolders(replacement, map, "");
    }

    /**
     * 替换给定字符串中的占位符变量值
     *
     * @param replacement
     * @param map
     * @return
     */
    public static String replacePlaceHoldersWithSymbol(String replacement, Map map, char symbol)
    {
        return replacePlaceHoldersWithSymbol(replacement, map, "", symbol);
    }

    /**
     * 替换给定字符串中的占位符变量值
     *
     * @param replacement
     * @param map
     * @param defaultValue
     * @return
     */
    public static String replacePlaceHolders(String replacement, Map map, String defaultValue)
    {
        return replacePlaceHoldersWithSymbol(replacement, map, defaultValue, placeholderDollar);
    }

    /**
     * 替换给定字符串中的占位符变量值
     *
     * @param replacement
     * @param map
     * @param defaultValue
     * @return
     */
    public static String replacePlaceHoldersWithSymbol(String replacement, Map map, String defaultValue, char symbol)
    {
        Assert.notNull(replacement, "被替换的字符串为空");
        char[] chars = replacement.toCharArray();
        Stack<Character> stack = new Stack<>();
        StringBuilder variable = new StringBuilder(35);
        StringBuilder holder = new StringBuilder(replacement.length());
        int len = chars.length;
        boolean isVariable = false;
        char c;
        for (int i = 0; i < len; i++)
        {
            c = chars[i];
            if (isPlaceHolderDollarCharacter(chars, len, c, i, symbol))
            {
                stack.push(c);
            }
            if (isVariable && c == placeholderSuffix)
            {
                String name = variable.toString();
                int x = name.indexOf(':');
                if (x > 0)
                {
                    defaultValue = name.substring(x + 1);
                    name = name.substring(0, x);
                }
                holder.append(MapUtils.getString(map, name, defaultValue));
                isVariable = false;
                stack.pop();
                variable.setLength(0);
                continue;
            }
            if (isVariable)
            {
                variable.append(c);
            }
            else
            {
                if (isPlaceHolderDollarCharacter(chars, len, c, i, symbol))
                {
                }
                else if (c == placeholderPrefix && i > 0 && chars[i - 1] == symbol)
                {
                }
                else
                {
                    holder.append(c);
                }
            }
            if (!stack.isEmpty() && stack.peek() == symbol && c == placeholderPrefix)
            {
                isVariable = true;
            }
        }
        return holder.toString();
    }

    /**
     * 将字节数组转换成16进制字符串
     *
     * @param bytes
     * @return
     */
    public static String byteArrToHex(byte[] bytes)
    {
        char[] chars = new char[bytes.length * 2];
        int i = 0;
        for (byte bt : bytes)
        {
            chars[i++] = HexCharArr[bt >>> 4 & 0xf];
            chars[i++] = HexCharArr[bt & 0xf];
        }
        return new String(chars);
    }

    /**
     * 将16进制字符串转成字节数组
     *
     * @param hexStr
     * @return
     */
    public static byte[] hexToByteArr(String hexStr)
    {
        char[] chars = hexStr.toCharArray();
        byte[] buf = new byte[chars.length / 2];
        int index = 0;
        for (int i = 0; i < chars.length; i++)
        {
            int highBit = HexStr.indexOf(chars[i]);
            int lowBit = HexStr.indexOf(chars[++i]);
            buf[index] = (byte) (highBit << 4 | lowBit);
            index++;
        }
        return buf;
    }

    private static boolean isPlaceHolderDollarCharacter(char[] chars, int len, char c, int i, char symbol)
    {
        return c == symbol && i < len - 1 && chars[i + 1] == placeholderPrefix;
    }

}
