//package com.icourt.cart.config;
//
//import org.apache.commons.dbcp.BasicDataSource;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.SqlSessionTemplate;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//
//import javax.sql.DataSource;
//
///**
// * Created by Caomr on 2018/5/4.
// */
//@Configuration
//@MapperScan(basePackages = "com.icourt.cart.dao", sqlSessionTemplateRef = "cartSqlSessionTemplate")
//public class CartDBconfig {
//
//
//    @Bean("cartDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DataSource cartDataSource() {
//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setMaxActive(150);
//        dataSource.setInitialSize(150);
//        return dataSource;
//    }
//
//    @Bean(name = "cartSqlSessionFactory")
//    public SqlSessionFactory cartSqlSessionFactory(@Qualifier("cartDataSource") DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
//        bean.setDataSource(dataSource);
//        return bean.getObject();
//    }
//
//    @Bean(name = "cartTransactionManager")
//    public DataSourceTransactionManager cartTransactionManager(@Qualifier("cartDataSource") DataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }
//
//    @Bean(name = "cartSqlSessionTemplate")
//    public SqlSessionTemplate cartSqlSessionTemplate(@Qualifier("cartSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
//}
