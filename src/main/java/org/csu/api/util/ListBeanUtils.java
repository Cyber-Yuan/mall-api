package org.csu.api.util;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ListBeanUtils extends BeanUtils {
    public static <S, T> List<T> copyListProperties(List<S> sourceList, Supplier<T> target) {
        return copyListProperties(sourceList, target, null);
    }

    public static <S, T> List<T> copyListProperties(List<S> sourceList, Supplier<T> target, ListBeanUtilsCallBack<S, T> CallBack) {
        // 调用BeanUtils中的copy方法，一个个赋值，再保存到list中
        List<T> resultList = new ArrayList<>();
        for (S s : sourceList) {
            T t = target.get();
            copyProperties(s, t);
            if (CallBack != null) {
                CallBack.callback(s, t);
            }
            resultList.add(t);
        }
        return resultList;
    }
}
