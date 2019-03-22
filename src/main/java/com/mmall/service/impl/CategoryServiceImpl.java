package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    CategoryMapper categoryMapper;


    /**
     * ####1.获取品类子节点(平级)
     */
    public ServerResponse<List<Category>>  getCategory(Integer categoryId){

        List<Category> list = categoryMapper.getCategory(categoryId);
        if(CollectionUtils.isEmpty(list)){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(list);

    }

    /**
     * ####2.增加节点


     * @param categoryId
     * @param categoryName
     * @return
     */
    public ServerResponse<String> addCategory(Integer categoryId,String categoryName) {
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByError("参数错误");
        }
        Category category = new Category();
        category.setParentId(categoryId);
        category.setName(categoryName);
        category.setStatus(true);
        int resultCount;
        resultCount =categoryMapper.insert(category);
        if (resultCount < 1) {
            return ServerResponse.createByError("添加失败");
        }
        return ServerResponse.createBySuccess("添加成功");
    }

    /**
     * .修改品类名字
     * @param categoryId
     * @param categoryName
     * @return
     */
    public  ServerResponse<String> setCategoryName(Integer categoryId,String categoryName){
     if(categoryId == null || StringUtils.isBlank(categoryName)){
         return ServerResponse.createByError("参数错误");
     }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
       int resultCount =  categoryMapper.updateByPrimaryKeySelective(category);
        if(resultCount < 1){
            return ServerResponse.createByError("修改失败");
        }
        return ServerResponse.createBySuccess("修改成功");

    }

    /**
     * 获取当前分类id及递归子节点categoryId
     * @param categoryId
     * @return
     */
    public ServerResponse<List<Integer>> getDeepCategory(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);


        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null){
            for(Category categoryItem : categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);

    }


    //递归算法,算出子节点
    private Set<Category> findChildCategory(Set<Category> categorySet ,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categorySet.add(category);
        }
        //查找子节点,递归算法一定要有一个退出的条件
        List<Category> categoryList = categoryMapper.getCategory(categoryId);
        for(Category categoryItem : categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }

}
