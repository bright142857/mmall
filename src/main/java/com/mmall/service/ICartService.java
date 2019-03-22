package com.mmall.service;

import com.mmall.common.ServerResponse;

public interface ICartService {
    ServerResponse  add(Integer productId,Integer count,Integer userId);
    ServerResponse update(Integer productId,Integer count,Integer userId);
    ServerResponse deleteProduct(String productIds,Integer userId);
    ServerResponse list(Integer userId);
    ServerResponse checkedOrUncheckedProduct(Integer userId,Integer productId,Integer check);
    ServerResponse getCartProductCount(Integer userId);
}
