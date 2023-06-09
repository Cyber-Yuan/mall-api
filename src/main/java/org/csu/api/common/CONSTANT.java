package org.csu.api.common;

public class CONSTANT {
    public final static String Login_User = "loginUser";
    public static final String PRODUCT_ORDER_BY_PRICE_ASC = "price_asc";
    public static final String PRODUCT_ORDER_BY_PRICE_DESC = "price_desc";

    public interface ROLE{
        int CUSTOMER = 1;
        int ADMIN = 0;
    }

    public interface PRODUCT_STATUS{
        Integer ON_SALE = 1;
        Integer OFF_SALE = 0;
    }

    public interface USER_FIELD{ // 数据库中的对应字段
        String USERNAME = "username";
        String PASSWORD = "password";
        String EMAIL = "email";
        String PHONE = "phone";
        String QUESTION = "question";
        String ANSWER = "answer";
    }
}
