package com.example.demo3.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 配置
 *
 * <p><b>讲解要点：</b></p>
 * <ul>
 *   <li>① @Configuration + @Bean — 替代传统 Spring 的 XML 配置</li>
 *   <li>② 分页插件 — 不配这个，Page<T> 不分页（不会自动加 LIMIT）</li>
 *   <li>③ MetaObjectHandler — 插入/更新时自动填充 createTime、updateTime</li>
 *   <li>④ 对比传统 MyBatis：需要在 XML 里配 SqlSessionFactory、分页拦截器...
 *       现在一个类几个 @Bean 方法搞定</li>
 * </ul>
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 分页插件 —— MyBatis Plus 分页功能的入口
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.H2));
        return interceptor;
    }

    /**
     * 自动填充处理器 —— 配合 Entity 中的 @TableField(fill = ...) 使用
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}
