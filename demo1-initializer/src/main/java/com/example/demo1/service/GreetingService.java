package com.example.demo1.service;

/**
 * 问候服务接口
 *
 * <p><b>讲解要点：</b></p>
 * <ul>
 *   <li>面向接口编程——你同事讲过的原则</li>
 *   <li>接口定义"做什么"，实现类定义"怎么做"</li>
 *   <li>如果有多种语言需求，加一个 EnglishGreetingService 实现即可，调用方不用改</li>
 * </ul>
 */
public interface GreetingService {

    /**
     * 根据名字生成问候语
     * @param name 被问候的人
     * @return 问候语
     */
    String greet(String name);
}
