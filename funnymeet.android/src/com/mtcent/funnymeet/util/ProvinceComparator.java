package com.mtcent.funnymeet.util;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;

/**
 *省份比较器
 */
public class ProvinceComparator implements Comparator<Object> {

    Collator collator = Collator.getInstance();

    public int compare(Object element1, Object element2) {
        CollationKey key1 = collator.getCollationKey(element1.toString());
        CollationKey key2 = collator.getCollationKey(element2.toString());
        return key1.compareTo(key2);
    }
}
