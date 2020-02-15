package com.mmall.task;

import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author wangmingliangwx
 * @version $Id: CloseOrderTask, v 0.1 2019/4/21 20:29 wangmingliangwx Exp$
 * @Email mingliang.online@foxmail.com
 */
@Component
@Slf4j
public class CloseOrderTask {
    @Autowired
    private IOrderService iOrderService;

    /**
     *  关闭订单
     */
    @Scheduled(cron = "0 0/2 * * * ? ")
    public void   closeOrderTaskV1(){
        log.info("定时任务开始");
       Integer hour =  Integer.parseInt(
                PropertiesUtil.getProperty("order.close.outtime.hours","2"));
        // iOrderService.closeOrder(hour);
        log.info("定时任务结束");
    }

}
