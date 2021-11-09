package com.github.funnyzak.common.obj;

import de.javagl.obj.Obj;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/4/4 2:41 下午
 * @description ThreeObject
 */
@Data
public class ThreeObject implements Serializable {

    /**
     * 总面数
     */
    private Integer facesCount;

    /**
     * 总顶点数
     */
    private Integer verticesCount;

    /**
     * 总材质坐标数量
     */
    private Integer texCoordsCount;

    /**
     * 法线点数
     */
    private Integer normalsCount;

    /**
     * 材质组 对应材质名称和面数
     */
    private Map<String, Integer> materialGroups;

    /**
     * 材质数量
     */
    private Integer materialGroupCount;

    public Integer getMaterialGroupCount() {
        return this.materialGroups == null ? 0 : this.materialGroups.size();
    }

    /**
     * 分体组
     */
    private Map<String, Integer> meshGroups;

    /**
     * 分体数
     */
    private Integer meshGroupCount;

    public Integer getMeshGroupCount() {
        return this.meshGroups == null ? 0 : this.meshGroups.size();
    }

    /**
     * Mtl文件组
     */
    private List<String> mtlFiles;

    /**
     * MTL文件数
     */
    private Integer mtlFileCount;

    public Integer getMtlFileCount() {
        return this.mtlFiles == null ? 0 : this.mtlFiles.size();
    }

    /**
     * 原始模型信息
     */
    private Obj origin;
}