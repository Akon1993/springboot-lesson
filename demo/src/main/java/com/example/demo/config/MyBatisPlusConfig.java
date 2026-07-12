package com.example.demo.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 配置类
 *
 * <p><b>📖 讲解要点：</b></p>
 * <ul>
 *   <li>{@code @Configuration} 替代了传统 Spring 的 XML 配置——"用代码写配置"</li>
 *   <li>{@code @Bean} 方法返回的对象被 Spring 管理，其他地方可以直接注入</li>
 *   <li><b>对比传统 Spring MVC：</b> 以前在 XML 里配 SqlSessionFactoryBean、分页插件...
 *       现在一个类加几个 @Bean 方法搞定</li>
 *   <li>分页插件是 MyBatis Plus 实现分页的关键——不配这个，{@code Page<T>} 不好使</li>
 *   <li>MetaObjectHandler 实现自动填充 createTime/updateTime</li>
 * </ul>
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 分页插件 —— MyBatis Plus 分页功能的入口
     *
     * <p>不配置这个 Bean，调用 selectPage() 时不会自动追加 LIMIT 子句。</p>
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 针对 H2 数据库注册分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.H2));
        return interceptor;
    }

    /**
     * 自动填充处理器 —— 插入/更新时自动填 createTime、updateTime
     *
     * <p>配合 Entity 中的 {@code @TableField(fill = FieldFill.INSERT)} 使用</p>
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
