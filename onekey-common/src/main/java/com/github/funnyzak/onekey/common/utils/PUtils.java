package com.github.funnyzak.onekey.common.utils;


import org.nutz.castor.Castors;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.Exps;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.lang.util.NutMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author silenceace@gmail.com
 */
public class PUtils {
    public static final Logger logger = LoggerFactory.getLogger(PUtils.class);

    /**
     * 根据search key生成查询条件
     *
     * @param cnd
     * @param searchKey
     * @param fields
     * @return
     */
    public static Cnd cndBySearchKey(Cnd cnd, String searchKey, String... fields) {
        if (cnd == null) {
            cnd = Cnd.NEW();
        }
        if (StringUtils.isNullOrEmpty(searchKey)) {
            return cnd;
        }
        searchKey = String.format("%%%s%%", searchKey);

        SqlExpressionGroup expressionGroup = Exps.begin();
        int index = 0;
        for (String field : fields) {
            if (index == 0) {
                expressionGroup.and(field, "like", searchKey);
            } else {
                expressionGroup.or(field, "like", searchKey);
            }
            index++;
        }
        return cnd.and(expressionGroup);
    }

    /**
     * 从记录集找对应记录
     *
     * @param name  对比的列
     * @param value 要查找的值
     * @param list  记录集
     * @return 返回对应记录
     */
    public static NutMap nutMapByNameValue(String name, String value, List<NutMap> list) {
        return fetchByList(list, "getString", value, String.class, name);
    }

    public static <T, Y> T fetchByList(List<T> list, String getValueMethodName, Y matchValue, Class<Y> valueClass) {
        return fetchByList(list, getValueMethodName, matchValue, valueClass, null, null);
    }

    public static <T> T fetchByList(List<T> list, String getValueMethodName, Object matchValue) {
        return fetchByList(list, getValueMethodName, matchValue, Object.class);
    }

    public static <T, Z> T fetchByList(List<T> list, String getValueMethodName, Object matchValue, Class<Z> getMethodArgType, Z methodArgValue) {
        return fetchByList(list, getValueMethodName, matchValue, Object.class, getMethodArgType, methodArgValue);
    }

    /**
     * 从集合根据列值匹配查找对应对象
     *
     * @param list               集合源
     * @param getValueMethodName 获取列的方法
     * @param matchValue         匹配的值
     * @param valueClass         匹配的值的类型
     * @param getMethodArgType   如果列方法带参数，设置参数类型
     * @param methodArgValue     带参数所对应的值
     * @param <T>                对象类型
     * @param <Y>                匹配值的类型
     * @param <Z>                带参数方法的参数类型
     * @return
     */
    public static <T, Y, Z> T fetchByList(List<T> list, String getValueMethodName, Y matchValue, Class<Y> valueClass, Class<Z> getMethodArgType, Z methodArgValue) {
        for (T info : list) {
            Y value = columnValue(info, getValueMethodName, valueClass, getMethodArgType, methodArgValue);
            if (value.equals(matchValue)) {
                return info;
            }
        }
        return null;
    }

    /**
     * 从数据库记录集查找并设置LIST列表某个列数据
     *
     * @param list                          要设置列的LIST
     * @param dataMapList                   数据库记录集
     * @param searchColumnName              从记录集查找时，用来比对的列
     * @param entityCompareColumnMethodName T对象获取标识列（一般为ID列）的方法
     * @param setMapColumnMethodName        设置找到的记录集到T对象对应Map列的方法
     * @param <T>                           T对象
     * @return 返回设置好的数据
     * @throws Exception 报错信息
     */
    public static <T> List<T> setListNutMapColumnByNutMapList(List<T> list, List<NutMap> dataMapList, String searchColumnName, String entityCompareColumnMethodName, String setMapColumnMethodName) {
        return setListObjectColumnByNutMapList(list, dataMapList, searchColumnName, entityCompareColumnMethodName, null, setMapColumnMethodName, NutMap.class);
    }


    /**
     * 从数据库记录集查找并设置LIST列表某个列相关数据
     *
     * @param list                           要设置的LIST的数据
     * @param dataMapList                    设置LIST每个对象数据列所搜索的数据源
     * @param searchColumnName               从DataMap数据列所搜索比对的Map Key
     * @param entityCompareColumnMethodName  获取lIST的对象的比对列的方法
     * @param getNutMapColumnName            获取搜索的的MAP，VALUE KEY。 如留空，则获取整个 NutMap对象
     * @param setObjectColumnMethodName      设置对象数据列的方法名
     * @param setObjectColumnMethodParamType 设置对象列的方法，的参数类型列表。如getNutMapColumnName为空，则忽律此参数
     * @param <T>                            LIST的对象类型
     * @param <Y>                            设置对象列方法参数类型列表
     * @return
     */
    public static <T, Y> List<T> setListObjectColumnByNutMapList(List<T> list, List<NutMap> dataMapList, String searchColumnName, String entityCompareColumnMethodName, String getNutMapColumnName, String setObjectColumnMethodName, Class<Y> setObjectColumnMethodParamType) {
        if (dataMapList == null || dataMapList.size() == 0) {
            return list;
        }
        try {
            for (T info : list) {
                String id = TypeParse.parseString(columnValue(info, entityCompareColumnMethodName, null));
                if (StringUtils.isNullOrEmpty(id)) {
                    continue;
                }

                NutMap searchedMap = nutMapByNameValue(searchColumnName, id, dataMapList);
                if (searchedMap == null) {
                    continue;
                }

                if (!StringUtils.isNullOrEmpty(getNutMapColumnName) && setObjectColumnMethodParamType != null) {
                    setEntityColumn(info
                            , setObjectColumnMethodName
                            , setObjectColumnMethodParamType.equals(Integer.class) ? searchedMap.getInt(getNutMapColumnName)
                                    : setObjectColumnMethodParamType.equals(String.class)
                                    ? searchedMap.getString(getNutMapColumnName)
                                    : setObjectColumnMethodParamType.equals(Long.class)
                                    ? searchedMap.getLong(getNutMapColumnName) : null
                            , setObjectColumnMethodParamType);
                } else {
                    setEntityNutMapColumn(info, setObjectColumnMethodName, searchedMap);
                }
            }
            return list;
        } catch (Exception ex) {
            logger.error("从数据库记录集查找并设置LIST列表某个列数据函数出错，出错信息：", ex);
        }
        return list;
    }

    /**
     * @param list                                    要设置的对应LIST
     * @param objList                                 要查找的对应LIST
     * @param searchObjListColumnMethodNameForCompare ObjList中对象用来对比的列获取方法名
     * @param listColumnMethodNameForCompare          list中对象用来对比的列获取方法名
     * @param setListObjectColumnMethodName           比如搜索后，用来设置List中对象列的的方法名
     * @param <T>                                     LIST对象类型
     * @param <Y>                                     objList对象类型
     * @return
     */
    public static <T, Y> List<T> setListObjectColumnByObjectList(List<T> list, List<Y> objList, String searchObjListColumnMethodNameForCompare, String listColumnMethodNameForCompare, String setListObjectColumnMethodName) {
        if (objList == null || objList.size() == 0) {
            return list;
        }
        try {
            for (T info : list) {
                String id = TypeParse.parseString(columnValue(info, listColumnMethodNameForCompare, null));
                if (StringUtils.isNullOrEmpty(id)) {
                    continue;
                }

                Y searchedObject = fetchByList(objList, searchObjListColumnMethodNameForCompare, id, String.class, null, null);
                if (searchedObject == null) {
                    continue;
                }

                setEntityColumn(info, setListObjectColumnMethodName, searchedObject, objList.get(0).getClass());
            }
            return list;
        } catch (Exception ex) {
            logger.error("从INFO记录集查找并设置LIST列表某个列数据函数出错，出错信息：", ex);
        }
        return list;
    }

    public static <T> T setEntityNutMapColumn(T info, String setColumnNameMethod, Object columnValue) {
        return setEntityColumn(info, setColumnNameMethod, columnValue, NutMap.class);
    }

    public static <T> T setEntityListNutMapColumn(T info, String setColumnNameMethod, Object columnValue) {
        return setEntityColumn(info, setColumnNameMethod, columnValue, List.class);
    }

    public static <T, Y> T setEntityColumn(T info, String setColumnNameMethod, Object columnValue, Class<Y> valueType) {
        if (info == null || StringUtils.isNull(setColumnNameMethod) ||  !entityHasMethod(info.getClass(), setColumnNameMethod)) {
            return info;
        }
        try {
            Method methodSet = info.getClass().getMethod(setColumnNameMethod, valueType);
            methodSet.invoke(info, columnValue);
            return info;
        } catch (Exception ex) {
            logger.error("设置实体信息列出错==>", ex);
        }
        return info;
    }

    public static <T> NutMap entityToNutMap(T info) {
        return entityToNutMap(info, null, false);
    }

    public static <T> NutMap entityToNutMap(T info, boolean force) {
        return entityToNutMap(info, null, force);
    }

    public static <T> NutMap entityToNutMap(T info, String fields) {
        return entityToNutMap(info, fields, false);
    }

    public static <T, Y> boolean entityHasMethod(Class<T> tClass, String methodName, Class<Y> methodReturnType) {
        if (tClass == null || StringUtils.isNullOrEmpty(methodName)) {
            return false;
        }
        return Arrays.stream(tClass.getMethods()).filter(v -> v.getName().equals(methodName) && (methodReturnType == null || (methodReturnType != null && v.getReturnType().equals(methodReturnType)))).count() > 0;
    }

    public static <T> boolean entityHasMethod(Class<T> tClass, String methodName) {
        return entityHasMethod(tClass, methodName, null);
    }

    /**
     * 把一个Bean实体转换为一个NutMap类型
     *
     * @param info   具体Bean信息
     * @param fields 要转换的字段列表
     * @param <T>    Bean T
     * @param force  是否保留null属性
     * @return
     * @throws Exception
     */
    public static <T> NutMap entityToNutMap(T info, String fields, boolean force) {
        if (info == null) {
            return null;
        }
        if (StringUtils.isNullOrEmpty(fields)) {
            try {
                fields = getEntityAttributeType(info.getClass()).keySet().stream().map(v -> v).collect(Collectors.joining(","));
            } catch (Exception ex) {
                logger.error("获取对象属性列表失败，==>", ex);
                return null;
            }
        }

        String splitChar = ",";
        NutMap nutMap = new NutMap();

        try {
            for (String field : fields.split(splitChar)) {

                Object getObject = null;
                try {
                    String methodName = String.format("get%s", StringUtils.firstCodeToUpperCase(field));
                    if (entityHasMethod(info.getClass(), methodName)) {
                        Method methodGet = info.getClass().getMethod(methodName);
                        getObject = methodGet.invoke(info);
                    }
                } catch (Exception ex) {
                    logger.error("反射获取对象属性失败", ex);
                }

                if (getObject == null && !force) {
                    continue;
                }
                nutMap.setv(field, getObject);
            }
        } catch (Exception ex) {
            logger.error("把一个Bean实体转换为一个NutMap类型失败，错误信息：", ex);
        }
        return nutMap;
    }

    /**
     * 删除对象集合的重复项
     *
     * @param list
     * @param getColumnMethodName
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<T> removeObjectListDuplicate(List<T> list, String getColumnMethodName) {
        if (list == null || list.size() == 0) {
            return null;
        }
        try {
            Method methodGet = list.get(0).getClass().getMethod(getColumnMethodName);

            List<T> newList = new ArrayList<>();
            List<String> columnList = new ArrayList<>();

            for (T t : list) {
                String columnVal = methodGet.invoke(t).toString();
                if (!columnList.contains(columnVal)) {
                    columnList.add(columnVal);
                    newList.add(t);
                }
            }
            list.clear();
            return newList;
        } catch (Exception ex) {
            logger.error("移除集合重复项发生错误，数据：{}, 错误：{}", list, ex.toString());
            return null;
        }

    }

    /**
     * LIST 列相加
     *
     * @param list          要相加的LIST
     * @param getMethodName 要相加的列值的获取方法
     * @param getMethodArg  获取方法需要传的值
     * @param <T>
     * @return
     * @throws Exception
     */

    public static <T> float sumListColumnValue(List<T> list, String getMethodName, Object getMethodArg) {
        return sumListColumnValue(list, getMethodName, getMethodArg, Object.class);
    }

    public static <T, Y> float sumListColumnValue(List<T> list, String getMethodName, Y getMethodArg, Class<Y> getMethodArgClassType) {
        if (list == null) {
            return 0;
        }
        try {
            Method methodGet = getMethodArg == null ?
                    list.get(0).getClass().getMethod(getMethodName) :
                    list.get(0).getClass().getMethod(getMethodName, getMethodArgClassType);

            float sum = 0;
            for (T info : list) {
                sum += (getMethodArg == null ?
                        TypeParse.parseFloat(methodGet.invoke(info), 0F) :
                        TypeParse.parseFloat(methodGet.invoke(info, getMethodArg), 0F));
            }
            return sum;
        } catch (Exception ex) {
            logger.error("集合项相加发生错误，数据：{}, 错误：{}", list, ex.toString());
            return 0;
        }

    }

    public static <T> Float averageListColumnValue(List<T> list, String getMethodName, Object getMethodArg) {
        return averageListColumnValue(list, getMethodName, getMethodArg, getMethodArg != null ? Object.class : null);
    }

    public static <T, Y> Float averageListColumnValue(List<T> list, String getMethodName, Y getMethodArg, Class<Y> getMethodArgClassType) {
        float totalValue = sumListColumnValue(list, getMethodName, getMethodArg, getMethodArgClassType);
        return totalValue == 0L ? null : (totalValue / (list.stream().filter(v -> columnValue(v, getMethodName, getMethodArgClassType, getMethodArg) != null).count()));
    }


    public static <T, Y, Z> List<Y> parseListColumn(List<T> list, String getMethodName, Class<Y> rltClass, boolean distinct, Class<Z> getMethodArgClass, Z getMethodArg) {
        List<String> strList = getStringArrayByListColumn(list, getMethodName, distinct, getMethodArgClass, getMethodArg);
        return strList.stream().filter(v -> v != null).map(v -> Castors.me().castTo(v, rltClass)).collect(Collectors.toList());
    }

    public static <T, Y> List<Y> parseListColumn(List<T> list, String getMethodName, Class<Y> rltClass) {
        return parseListColumn(list, getMethodName, rltClass, true, null, null);
    }

    public static <T> List<Long> parseListColumn(List<T> list, String getMethodName) {
        return parseListColumn(list, getMethodName, Long.class, true, null, null);
    }


    /**
     * 读取一个List T的某一个列，并循环为String数组
     *
     * @param list          LIST T
     * @param getMethodName 获取列方法
     * @param <T>           T
     * @return 获取
     * @throws Exception
     */
    public static <T> List<String> getStringArrayByListColumn(List<T> list, String getMethodName) {
        return getStringArrayByListColumn(list, getMethodName, true);
    }

    public static <T> List<String> getStringArrayByListColumn(List<T> list, String getMethodName, boolean distinct) {
        return getStringArrayByListColumn(list, getMethodName, distinct, null, null);
    }

    /**
     * 读取一个List T的某一个列，并循环为String数组
     *
     * @param list          LIST T
     * @param getMethodName 获取列方法
     * @param getMethodArg  获取方法的参数. （参数类型必须为String）
     * @param <T>           T
     * @param distinct      是否去重
     * @return 获取
     * @throws Exception
     */
    public static <T, Y> List<String> getStringArrayByListColumn(List<T> list, String getMethodName, boolean distinct, Class<Y> getMethodArgClass, Y getMethodArg) {
        if (list == null || list.size() == 0) {
            return null;
        }
        List<String> stringArray = new ArrayList<>();
        try {

            for (T info : list) {
                String _str = TypeParse.parseString(columnValue(info, getMethodName, getMethodArgClass, getMethodArg));

                if (StringUtils.isNullOrEmpty(_str) || (distinct && stringArray.contains(_str))) {
                    continue;
                }
                stringArray.add(_str);
            }
            return stringArray;
        } catch (Exception ex) {
            logger.error(" 读取一个List T的某一个列，并循环为String数组出错，出错信息：", ex);
            return stringArray;
        }
    }

    /**
     * 从对象查找列的值
     *
     * @param info              查找对象
     * @param getMethodName     获取列值的方法
     * @param rltClass          查找到返回的值类型
     * @param getMethodArgType  如果该方法有参数，需指定参数类型
     * @param getMethodArgValue 如果该方法有参数，需传参数的值
     * @param <T>               对象类型
     * @param <Y>               返回值类型
     * @param <Z>               带参数的方法，参数类型
     * @return
     */
    public static <T, Y, Z> Y columnValue(T info, String getMethodName, Class<Y> rltClass, Class<Z> getMethodArgType, Z getMethodArgValue) {
        if (info == null || !entityHasMethod(info.getClass(), getMethodName)) {
            return null;
        }
        try {
            Method methodGet = getMethodArgType == null ?
                    info.getClass().getMethod(getMethodName) :
                    info.getClass().getMethod(getMethodName, getMethodArgType);

            Object getObj = getMethodArgType == null ?
                    methodGet.invoke(info) :
                    methodGet.invoke(info, getMethodArgValue);
            return getObj != null ? Castors.me().castTo(getObj, rltClass) : null;
        } catch (Exception ex) {
            logger.error("获取实体列信息出错", ex);
            return null;
        }
    }

    public static <T, Y> Y columnValue(T info, String getMethodName, Class<Y> rltClass) {
        return columnValue(info, getMethodName, rltClass, null, null);
    }

    public static <T> Object columnValue(T info, String getMethodName) {
        return columnValue(info, getMethodName, Object.class, null, null);
    }

    public static <T, Z> Object columnValue(T info, String getMethodName, Class<Z> getMethodArgType, Z getMethodArg) {
        return columnValue(info, getMethodName, Object.class, getMethodArgType, getMethodArg);
    }

    /**
     * 合并两个String数组
     *
     * @param arr1
     * @param arr2
     * @return
     */
    public static String[] contactStringArray(String[] arr1, String[] arr2) {
        TreeSet<String> set = new TreeSet<>();
        set.addAll(Arrays.asList(arr1));
        set.addAll(Arrays.asList(arr2));
        return set.toArray(new String[set.size()]);
    }

    /**
     * List 去重
     */
    public static <T> List<T> distinctList(List<T> list) {
        if (list == null || list.size() == 0) {
            return list;
        }

        List<T> newList = new ArrayList<T>();
        Set<T> set = new HashSet<T>();

        for (Iterator<T> iter = list.iterator(); iter.hasNext(); ) {

            T element = iter.next();
            if (element != null && set.add(element)) {
                newList.add(element);
            }
        }
        return newList;
    }

    public static <T, Y> List<T> distinctListByColumnValue(List<T> list, String getColumnValueMethodName, Class<Y> getColumnValueMethodArgType, Y getColumnValueArgValue) {
        if (list == null || list.size() == 0) {
            return list;
        }

        List<T> newList = new ArrayList<>();
        Set<Object> set = new HashSet<>();

        for (Iterator<T> iter = list.iterator(); iter.hasNext(); ) {
            T element = iter.next();
            if (element != null && set.add(columnValue(element, getColumnValueMethodName, Object.class, getColumnValueMethodArgType, getColumnValueArgValue))) {
                newList.add(element);
            }
        }
        return newList;
    }

    /**
     * 把带getName方法的枚举类型转换为Map
     *
     * @param enumType
     * @param <E>
     * @return
     */
    public static <E extends Enum<E>> NutMap enumTypeToMap(@NotNull Class<E> enumType) {
        NutMap map = new NutMap();
        for (E e : EnumSet.allOf(enumType)) {
            map.put(e.toString(), columnValue(e, "getName"));
        }
        return map;
    }

    /**
     * 获取对象所有属性 属性名称 类型
     *
     * @param tClass
     * @return
     * @throws Exception
     */
    public static <T> Map<String, String> getEntityAttributeType(@NotNull Class<T> tClass) throws Exception {
        Field[] field = tClass.getDeclaredFields();
        Map<String, String> map = new HashMap<>();
        for (int j = 0; j < field.length; j++) {
            String name = field[j].getName();
            String type = field[j].getGenericType().toString().replace("class ", "");
            map.put(name, type);
        }
        return map;
    }

    public static <E extends Enum<E>> List<E> enums2List(@NotNull Class<E> enumType, String enumStrList) {
        return enums2List(enumType, enumStrList, ",");
    }

    /**
     * 转换字符串列表为枚举类型List集合
     *
     * @param enumStrList 枚举列表
     * @param splitChar   分割符
     * @param enumType    枚举类型
     * @param <E>
     * @return
     */
    public static <E extends Enum<E>> List<E> enums2List(@NotNull Class<E> enumType, String enumStrList, String splitChar) {
        if (StringUtils.isNullOrEmpty(enumStrList)) return null;

        List<E> enumList = new ArrayList<>();
        for (String enumStr : enumStrList.split(splitChar)) {
            try {
                enumList.add(E.valueOf(enumType, enumStr));
            } catch (Exception ex) {
                logger.error("转换枚举类型失败，错误信息：", ex);
            }
        }
        return enumList;
    }

    /**
     * 获取枚举属性
     *
     * @param enumType
     * @param <E>
     * @return
     */
    public static <E extends Enum<E>> List<String> enumsPropertyList(@NotNull Class<E> enumType) {
        try {
            Map<String, String> keyMap = getEntityAttributeType(enumType);
            return keyMap.keySet().stream().filter(key -> !keyMap.get(key).equals(enumType.getName())).collect(Collectors.toList());
        } catch (Exception ex) {
            logger.error("获取枚举属性是失败，==>", ex);
            return null;
        }
    }

    /**
     * 获取枚举所有成员对象
     *
     * @param enumType
     * @param <E>
     * @return
     */
    public static <E extends Enum<E>> List<E> enumsAllList(@NotNull Class<E> enumType) {
        try {
            Map<String, String> keyMap = getEntityAttributeType(enumType);
            return keyMap.keySet().stream().filter(key -> keyMap.get(key).equals(enumType.getName())).map(key -> E.valueOf(enumType, key)).collect(Collectors.toList());
        } catch (Exception ex) {
            logger.error("获取枚举类型列表是失败，==>", ex);
            return null;
        }
    }

    /**
     * 把枚举所有成员名称和属性转换为List集合
     *
     * @param enumType
     * @param <E>
     * @return
     */
    public static <E extends Enum<E>> List<NutMap> enumsInfoList(@NotNull Class<E> enumType) {
        try {
            List<NutMap> retList = new ArrayList<>();
            for (E key : enumsAllList(enumType)) {
                NutMap map = entityToNutMap(key);
                map.addv("_origin", key);
                map.addv("_name", key.toString());
                retList.add(map);
            }
            return retList;
        } catch (Exception ex) {
            logger.error("把枚举类型转换为List集合失败，==>", ex);
            return null;
        }
    }

    public static <T> Method getMethod(T t, String methodName, Class<?>... parameterTypes) {
        try {
            if (t == null) return null;
            return t.getClass().getDeclaredMethod(methodName, parameterTypes);
        } catch (Exception ex) {
            logger.error("获取方法对象失败==>", ex);
            return null;
        }
    }
}
