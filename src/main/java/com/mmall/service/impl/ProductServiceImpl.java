package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
   @Autowired
    ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 新增OR更新产品
     * @return
     */
    public ServerResponse save(Product product) {
        // 处理主图，默认第一张
        if(StringUtils.isNotEmpty(product.getSubImages())){
            String[] subimgs = product.getSubImages().split(",");
            product.setMainImage(subimgs[0]);
        }
        if (product.getId() == null) {
            //新增
            int reusltCount = productMapper.insert(product);
            if (reusltCount < 1) {
                return ServerResponse.createByError("新增产品失败");
            }
            return ServerResponse.createBySuccess("新增产品成功");

        } else {
            //修改
            int resultCount = productMapper.updateByPrimaryKeySelective(product);
            if (resultCount < 1) {
                return ServerResponse.createByError("修改产品失败");
            }
            return ServerResponse.createBySuccess("修改产品成功");
        }
    }

    /**
     * 产品上下架
     * @param productId
     * @param status
     */
    public ServerResponse setSaleStatus(Integer productId, Integer status) {
        if(productId == null || status == null){
            return ServerResponse.createByError("产品参数错误");
        }
        Product resultProduct = productMapper.selectByPrimaryKey(productId);
        if(resultProduct == null){
            return ServerResponse.createByError("没有此产品");

        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int resultCount = productMapper.updateByPrimaryKeySelective(product);
        if (resultCount < 1) {
            return ServerResponse.createByError("修改产品状态失败");
        }
        return ServerResponse.createBySuccess("修改产品状态成功");
    }

    /**
     * 产品详情
     * @param productId
     * @return
     */
    public ServerResponse<ProductDetailVo> detail(Integer productId){
        if(productId == null){
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product resultProduct = productMapper.selectByPrimaryKey(productId);
        if(resultProduct == null){
            return ServerResponse.createByError("产品已下架或删除");
        }
      ProductDetailVo productDetailVo = this.assembleProductDetailVo(resultProduct);

         return ServerResponse.createBySuccess(productDetailVo);

    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        //ftp service address
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://www.bright.com/"));
        //获取产品上级分类
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);//默认根节点
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    /**
     * 列表页
     * @param pageNum
     * @param pageSize
     * @return
     */
    public  ServerResponse<PageInfo> list(int pageNum,int pageSize){
        //startPage--start
        //填充自己的sql查询逻辑
        //pageHelper-收尾
        PageHelper.startPage(pageNum,pageSize);

        List<Product> productList = productMapper.selectList();
        List<ProductListVo>  productListVoList = Lists.newArrayList();
        for (Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo resultInfo = new PageInfo(productList);

        resultInfo.setList(productListVoList);


        return  ServerResponse.createBySuccess(resultInfo);
    }

    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    /**
     * 产品搜索
     * @param pageNum
     * @param pageSize
     * @param productName
     * @param productId
     * @return
     */

    public  ServerResponse<PageInfo> search(int pageNum,int pageSize,String productName,String productId){

        PageHelper.startPage(pageNum,pageSize);

        if(StringUtils.isNotBlank(productName))
        {
            productName = new StringBuffer().append("%").append(productName).append("%").toString();
        }

         List<Product> productList = productMapper.selectNameAndproductId(productName,productId);

        List<ProductListVo>  productListVoList = Lists.newArrayList();
        for (Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo resultInfo = new PageInfo(productList);

        resultInfo.setList(productListVoList);


        return  ServerResponse.createBySuccess(resultInfo);
    }

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByError("产品已下架或者删除");
        }
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByError("产品已下架或者删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }


    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy){
        if(StringUtils.isBlank(keyword) && categoryId == null){
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = new ArrayList<Integer>();

        if(categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)){
                //没有该分类,并且还没有关键字,这个时候返回一个空的结果集,不报错
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.getDeepCategory(category.getId()).getData();
        }
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }




































}
