package com.zjxy.smart.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Define {

    //商户后台每页条数
    public static final int ADMIN_PAGE_SIZE = 3;
    //店铺商品每页条数
    public static final int STORE_GOODS_PAGE_SIZE = 10;
    //网站商品每页条数
    public static final int INDEX_GOODS_PAGE_SIZE = 18;

    /**
     * 生成32位订单编号
     * @return
     */
    public static String generatorCodeNo(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String now = sdf.format(date);
        String codeNo = now + UUID.randomUUID().toString().replace("-", "").substring(0,18);
        return codeNo;
    }

}
