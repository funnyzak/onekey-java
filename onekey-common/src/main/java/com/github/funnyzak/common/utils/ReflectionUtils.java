package com.github.funnyzak.common.utils;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/14 5:01 下午
 * @description ReflectionUtils
 */
public class ReflectionUtils {

    private Reflections reflections;

    /**
     * 初始化工具
     *
     * @param basePackages
     * @return
     */
    public static Reflections reflections(String... basePackages) {
        return new Reflections(new ConfigurationBuilder().forPackages(basePackages == null ? new String[]{"org.skyf.potato"} : basePackages).addScanners(new SubTypesScanner()).addScanners(new FieldAnnotationsScanner()));
    }

    private static Reflections reflections() {
        return reflections(new String[]{});
    }

    /**
     * 获取子类
     *
     * @param tClass
     * @param packages
     * @param <T>
     * @return
     */
    public static <T> List<Class<? extends T>> subTypesOf(Class<T> tClass, String... packages) {
        return set2List(reflections(packages).getSubTypesOf(tClass));
    }

    /**
     * 获取所有枚举
     *
     * @param packages
     * @return
     */
    public static List<Class<? extends Enum>> allEnum(String... packages) {
        return subTypesOf(Enum.class, packages);
    }

    /**
     * 根据枚举名称获取匹配的枚举类型
     *
     * @param enumName
     * @param packages
     * @return
     */
    public static Class<? extends Enum> matchEnum(String enumName, String... packages) {
        List<Class<? extends Enum>> enumList = allEnum(packages);
        return enumList == null ? null
                : enumList.stream().filter(v -> v.getName().endsWith(enumName)).toArray().length > 0
                ? enumList.stream().filter(v -> v.getName().endsWith(enumName)).collect(Collectors.toList()).get(0) : null;
    }

    /**
     * 获取某个包下类型注解对应的方法
     *
     * @param tClass
     * @param packages
     * @param <T>
     * @return
     */
    public static <T extends Annotation> List<Method> methodsAnnotatedWith(Class<T> tClass, String... packages) {
        return set2List(reflections(packages).getMethodsAnnotatedWith(tClass));
    }

    /**
     * 获取注解对应的类
     *
     * @param tClass
     * @param packages
     * @param <T>
     * @return
     */
    public static <T extends Annotation> List<Class<?>> typesAnnotatedWith(Class<T> tClass, String... packages) {
        return set2List(reflections(packages).getTypesAnnotatedWith(tClass));
    }

    public static Class<?> classForName(String name) throws Exception {
        return Class.forName(name);
    }

    public static <T> T classInstanceForName(String name) throws Exception {
        return (T) classForName(name).newInstance();
    }

    /**
     * 获取注解对应的字段
     *
     * @param tClass
     * @param packages
     * @param <T>
     * @return
     */
    public static <T extends Annotation> List<Field> fieldsAnnotatedWith(Class<T> tClass, String... packages) {
        return set2List(reflections(packages).getFieldsAnnotatedWith(tClass));
    }

    /**
     * 获取特定参数对应的方法
     *
     * @param packageName
     * @param types
     * @param <T>
     * @return
     */
    public static <T extends Annotation> List<Method> methodsMatchParams(String packageName, Class<?>... types) {
        return set2List(reflections(new String[]{packageName}).getMethodsMatchParams(types));
    }

    /**
     * 获取特定返回类型对应的方法
     *
     * @param type
     * @param packages
     * @param <T>
     * @return
     */
    public static <T extends Annotation> List<Method> methodsReturn(Class type, String... packages) {
        return set2List(reflections(packages).getMethodsReturn(type));
    }

    /**
     * 获取资源文件
     *
     * @param pattern
     * @param packages
     * @return
     */
    public static List<String> resources(Pattern pattern, String... packages) {
        return set2List(reflections(packages).getResources(pattern));
    }

    /**
     * 获取带参数设置的注解对应的方法
     *
     * @param tClass
     * @param packages
     * @param <T>
     * @return
     */
    public static <T extends Annotation> List<Method> methodsWithAnyParamAnnotated(Class<T> tClass, String... packages) {
        return set2List(reflections(packages).getMethodsWithAnyParamAnnotated(tClass));
    }

    static <T> List<T> set2List(Set<T> sets) {
        return sets == null ? null : sets.stream().collect(Collectors.toList());
    }


    public static void main(String[] args) {
        Reflections reflections = reflections();
        reflections.getAllTypes();
        reflections.getSubTypesOf(Enum.class);

        // 获取资源文件
        Set<String> properties = reflections.getResources(Pattern.compile(".*\\.properties"));
    }
}