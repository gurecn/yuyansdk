package com.yuyan.imemodule.utils.pinyin4j;

import com.yuyan.imemodule.utils.pinyin4j.multipinyin.Trie;

/**
 * A class provides several utility functions to convert Chinese characters
 * (both Simplified and Tranditional) into various Chinese Romanization
 * representations
 *
 * @author Li Min (xmlerlimin@gmail.com)
 */
public class PinyinHelper {

    /**
     * 汉字转简拼
     */
    public static String getPinYinHeadChar(String str) {
        ChineseToPinyinResource resource = ChineseToPinyinResource.getInstance();
        StringBuilder resultPinyinStrBuf = new StringBuilder();
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            String result = null;//匹配到的最长的结果
            char ch = chars[i];
            Trie currentTrie = resource.getUnicodeToHanyuPinyinTable();
            int success = i;
            int current = i;
            do {
                String hexStr = Integer.toHexString(ch).toUpperCase();
                currentTrie = currentTrie.get(hexStr);
                if (currentTrie != null) {
                    if (currentTrie.getPinyin() != null) {
                        result = currentTrie.getPinyin();
                        success = current;
                    }
                    currentTrie = currentTrie.getNextTire();
                }
                current++;
                if (current < chars.length) ch = chars[current];
                else break;
            }
            while (currentTrie != null);
            if (result == null) {//如果在前缀树中没有匹配到，那么它就不能转换为拼音，直接输出或者去掉
                if (Character.isLetter(chars[i])) resultPinyinStrBuf.append(chars[i]);
            } else {
                String[] pinyinStrArray = resource.parsePinyinString(result);
                if (pinyinStrArray != null) {
                    for (String s : pinyinStrArray) {
                        resultPinyinStrBuf.append(s.charAt(0));
                        if (i == success) break;
                    }
                }
            }
            i = success;
        }
        return resultPinyinStrBuf.toString();
    }

    // ! Hidden constructor
    private PinyinHelper() {
    }
}
