package com.github.funnyzak.onekey.bean.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/12/13 5:31 下午
 * @description ThreeModel
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThreeModel {
    /**
     * OBJ地址
     */
    private String obj;
    /**
     * MTL地址
     */
    private String mtl;
    /**
     * 贴图地址集合
     */
    private List<String> textures;
    /**
     * 贴图总大小
     */
    private Long texturesSize;
    /**
     * 贴图数量
     */
    private Integer texturesCount;
    /**
     * 压缩包地址
     */
    private String zip;
    /**
     * OBJ大小（B）
     */
    private Long objSize;
    /**
     * MTL大小
     */
    private Long mtlSize;

    /**
     * 总大小
     */
    private Long totalFileSize;
    /**
     * 压缩包大小
     */
    private Long zipSize;
    /**
     * 总面数
     */
    private Integer facesCount;

    /**
     * 总顶点数
     */
    private Integer verticesCount;

    /**
     * 点间距
     */
    private Double verticesDistance;

    /**
     * 材质组 对应材质名称和面数
     */
    private Map<String, Integer> materialGroups;

    /**
     * 材质数量
     */
    private Integer materialGroupCount;

    /**
     * 分体组 名称和面数
     */
    private Map<String, Integer> meshGroups;

    /**
     * 分体数
     */
    private Integer meshGroupCount;

    /**
     * 总材质坐标数量
     */
    private Integer texCoordsCount;

    /**
     * 法线顶点数
     */

    private Integer normalsCount;
}