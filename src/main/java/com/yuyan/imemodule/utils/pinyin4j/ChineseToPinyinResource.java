/**
 * This file is part of pinyin4j (http://sourceforge.net/projects/pinyin4j/) and distributed under
 * GNU GENERAL PUBLIC LICENSE (GPL).
 * <p/>
 * pinyin4j is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * <p/>
 * pinyin4j is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with pinyin4j.
 */

/**
 *
 */
package com.yuyan.imemodule.utils.pinyin4j;

import com.yuyan.imemodule.utils.pinyin4j.multipinyin.Trie;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Manage all external resources required in PinyinHelper class.
 *
 * @author Li Min (xmlerlimin@gmail.com)
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
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    String[] parsePinyinString(String pinyinRecord) {
        if (null != pinyinRecord) {
            int indexOfLeftBracket = pinyinRecord.indexOf(Field.LEFT_BRACKET);
            int indexOfRightBracket = pinyinRecord.lastIndexOf(Field.RIGHT_BRACKET);
            String stripedString = pinyinRecord.substring(indexOfLeftBracket + Field.LEFT_BRACKET.length(), indexOfRightBracket);
            return stripedString.split(Field.COMMA);

        } else
            return null; // no record found or mal-formatted record
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
        static final String LEFT_BRACKET = "(";

        static final String RIGHT_BRACKET = ")";

        static final String COMMA = ",";
    }
}
