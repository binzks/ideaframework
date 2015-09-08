package com.ideaframework.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Created by zhoubin on 15/9/6.
 * Spring的ApplicationContext，用于获取spring的jdbctemplate对象
 * 可以使用initFromFile方法从配置文件加载spring配置，也可以通过自动加载SpringApplicationContext来获取spring配置
 */
public class SpringApplicationContext implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /***
     * 加载spring jdbc配置文件，初始化ApplicationContext对象，用于获取jdbctemplate对象
     *
     * @param springFile spring jdbc的配置文件的路径和名称
     */
    public static void initFromFile(String springFile) {
        context = new FileSystemXmlApplicationContext("file:" + springFile);
    }


    /***
     * 获取spring注解的bean对象，主要是用于获取jdbctemplate对象
     *
     * @param name bean的id
     * @return bean对象
     */
    public static Object getBean(String name) {
        return context.getBean(name);
    }
}
