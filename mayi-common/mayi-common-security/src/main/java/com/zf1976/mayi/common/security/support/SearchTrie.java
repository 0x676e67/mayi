package com.zf1976.mayi.common.security.support;

import java.io.Serializable;
import java.util.List;

/**
 * @author mac
 * @date 2021/5/5
 */
public interface SearchTrie extends Serializable {

    /**
     * 新增字符序列
     *
     * @param sequence 字符序列
     */
    void putTrie(CharSequence sequence);

    /**
     * 新增字符序列数组
     *
     * @param charSequences 序列数组
     */
    void putTrie(CharSequence[] charSequences);

    /**
     * 新增字典数组
     *
     * @param charArray 字符串数组
     */
    void putTrie(String[] charArray);

    /**
     * 新增字符序列列表
     *
     * @param charSequenceList 序列列表
     */
    void putTrie(List<CharSequence> charSequenceList);

    /**
     * 查找字符序列
     *
     * @param charSequence 字符序列
     */
    boolean searchTrie(CharSequence charSequence);

    /**
     * 查找字符序列数组
     *
     * @param charSequences 字符序列数组
     * @return {@link boolean}
     */
    boolean searchTrie(CharSequence[] charSequences);

    /**
     * 查找字符串数组
     *
     * @param charArray 字符串数组
     * @return {@link boolean}
     */
    boolean searchTrie(String[] charArray);

    /**
     * 查找字符序列列表
     *
     * @param charSequenceList 字符序列列表
     * @return {@link boolean}
     */
    boolean searchTrie(List<CharSequence> charSequenceList);

}
