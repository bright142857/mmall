package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {
    ServerResponse save(Product product);
    ServerResponse setSaleStatus(Integer productId,Integer status);
    ServerResponse<ProductDetailVo> detail(Integer productId);
    ServerResponse<PageInfo> list(int pageNum, int pageSize);
    ServerResponse<PageInfo> search(int pageNum,int pageSize,String productName,String productId);
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);



}
