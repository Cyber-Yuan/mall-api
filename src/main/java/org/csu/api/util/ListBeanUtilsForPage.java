package org.csu.api.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Supplier;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class ListBeanUtilsForPage extends BeanUtils {
    public static <S, T> Page<T> copyPageList(Page<S> sourcePage, Supplier<T> target) {
        return copyPageList(sourcePage, target, null);
    }

    public static <S, T> Page<T> copyPageList(Page<S> sourcePage, Supplier<T> target, ListBeanUtilsCallBack<S, T> CallBack) {
        Page<T> targetPage = new Page<>();
        // 获取sourcePage的各种值，传入targetPage中
        List<S> sourceRecords = sourcePage.getRecords();
        List<T> targetRecords = ListBeanUtils.copyListProperties(sourceRecords, target, CallBack);
        targetPage.setTotal(sourcePage.getTotal());
        targetPage.setSize(sourcePage.getSize());
        targetPage.setCurrent(sourcePage.getCurrent());
        targetPage.setRecords(targetRecords);
        return targetPage;
    }
}
