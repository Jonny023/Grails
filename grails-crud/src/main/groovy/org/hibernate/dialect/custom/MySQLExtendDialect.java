package org.hibernate.dialect.custom;

import org.hibernate.Hibernate;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

/**
 *  自定义方言，添加mysql函数
 * @Author Lee
 * @Description
 * @Date 2019年03月20日 14:51
 */
public class MySQLExtendDialect extends MySQLDialect {

    public MySQLExtendDialect() {
        super();
        registerFunction("convert_gbk",new SQLFunctionTemplate(StandardBasicTypes.STRING, "convert(?1 using gbk)") );
    }
}