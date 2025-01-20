
package com.yuyan.imemodule.libs.pinyin4j;

import com.yuyan.imemodule.libs.pinyin4j.multipinyin.Trie;

import java.io.IOException;

/**
 * Manage all external resources required in PinyinHelper class.
 */
class ChineseToPinyinResource {
    /**
     * A hash table contains <Unicode, HanyuPinyin> pairs
     */
    private Trie unicodeToHanyuPinyinTable = null;

    /**
     * @param unicodeToHanyuPinyinTable The unicodeToHanyuPinyinTable to set.
     */
    private void setUnicodeToHanyuPinyinTable(Trie unicodeToHanyuPinyinTable) {
        this.unicodeToHanyuPinyinTable = unicodeToHanyuPinyinTable;
    }

    /**
     * @return Returns the unicodeToHanyuPinyinTable.
     */
    Trie getUnicodeToHanyuPinyinTable() {
        return unicodeToHanyuPinyinTable;
    }

    /**
     * Private constructor as part of the singleton pattern.
     */
    private ChineseToPinyinResource() {
        initializeResource();
    }

    /**
     * Initialize a hash-table contains <Unicode, HanyuPinyin> pairs
     */
    private void initializeResource() {
        try {
            final String resourceName = "pinyindb/unicode_to_hanyu_pinyin.txt";
            final String resourceMultiName = "pinyindb/multi_pinyin.txt";
            setUnicodeToHanyuPinyinTable(new Trie());
            getUnicodeToHanyuPinyinTable().load(ResourceHelper.getResourceInputStream(resourceName));
            getUnicodeToHanyuPinyinTable().loadMultiPinyin(ResourceHelper.getResourceInputStream(resourceMultiName));
        } catch (IOException ignored) {
        }
    }

    String[] parsePinyinString(String pinyinRecord) {
        if (null != pinyinRecord) {
            int indexOfLeftBracket = pinyinRecord.indexOf(Field.LEFT_BRACKET);
            int indexOfRightBracket = pinyinRecord.lastIndexOf(Field.RIGHT_BRACKET);
            String stripedString = pinyinRecord.substring(indexOfLeftBracket + Field.LEFT_BRACKET.length(), indexOfRightBracket);
            return stripedString.split(Field.COMMA);
        } else return null;
    }

    /**
     * Singleton factory method.
     *
     * @return the one and only MySingleton.
     */
    static ChineseToPinyinResource getInstance() {
        return ChineseToPinyinResourceHolder.theInstance;
    }

    /**
     * Singleton implementation helper.
     */
    private static class ChineseToPinyinResourceHolder {
        static final ChineseToPinyinResource theInstance = new ChineseToPinyinResource();
    }

    /**
     * A class encloses common string constants used in Properties files
     *
     * @author Li Min (xmlerlimin@gmail.com)
     */
    class Field {
        static final String LEFT_BRACKET = "[";

        static final String RIGHT_BRACKET = "]";

        static final String COMMA = ",";
    }
}
