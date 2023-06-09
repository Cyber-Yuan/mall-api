package org.csu.api.service;

import java.util.List;

public interface CategoryService {
    List<Integer> getCategoryAndAllChildren(Integer categoryId);
}
