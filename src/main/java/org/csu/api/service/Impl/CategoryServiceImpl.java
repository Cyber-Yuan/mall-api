package org.csu.api.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.csu.api.domain.Category;
import org.csu.api.persistence.CategoryMapper;
import org.csu.api.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public List<Integer> getCategoryAndAllChildren(Integer categoryId) {
        List<Integer> categoryIdList = Lists.newArrayList();
        Category category = categoryMapper.selectById(categoryId);
        // 找到所有子类别
        Set<Category> categorySet = Sets.newHashSet();
        categorySet.add(category);
        findChildCategory(categorySet, categoryId);
        // 将找到的子类别（包括自己）的ID全部放进categoryIdList
        for (Category _category:categorySet) {
            System.out.println("set id:" + _category.getId());
            categoryIdList.add(_category.getId());
        }
        return categoryIdList;
    }

    private void findChildCategory(Set<Category> categorySet, Integer categoryId) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("parent_id", categoryId);
        List<Category> childCategoryList = categoryMapper.selectList(queryWrapper);
        // 递归
        for (Category _childCategory:childCategoryList) {
            // 停止条件
            if (_childCategory != null) {
                categorySet.add(_childCategory);
            }
            findChildCategory(categorySet, _childCategory.getId());
        }
        // 停止条件
//        Category category = categoryMapper.selectById(categoryId);
//        if (category != null) {
//            categorySet.add(category);
//        }
    }
}
