package com.github.funnyzak.onekey.common.obj;

import com.github.funnyzak.onekey.common.utils.Numbers;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/12/14 9:06 上午
 * @description ObjUtils
 * demo link: https://github.com/javagl/ObjSamples
 */
public class ObjUtils {

    public static ThreeObject read(String objPath) throws Exception {
        // Read an OBJ file
        InputStream objInputStream = new FileInputStream(objPath);
        Obj obj = ObjReader.read(objInputStream);
        return parseObj(obj);
    }

    public static ThreeObject parseObj(Obj obj) throws Exception {
        if (obj == null) {
            throw new Exception(".OBJ文件错误");
        }

        ThreeObject threeObj = new ThreeObject();
        threeObj.setOrigin(obj);
        threeObj.setFacesCount(obj.getNumFaces());
        threeObj.setVerticesCount(obj.getNumVertices());
        threeObj.setTexCoordsCount(obj.getNumTexCoords());
        threeObj.setNormalsCount(obj.getNumNormals());

        Map<String, Integer> materialGroups = new LinkedHashMap<>();
        Numbers.generateNumArray(obj.getNumMaterialGroups() - 1).stream().forEach(v -> {
            materialGroups.put(obj.getMaterialGroup(v).getName(), obj.getMaterialGroup(v).getNumFaces());
        });
        threeObj.setMaterialGroups(materialGroups);

        Map<String, Integer> meshGroups = new LinkedHashMap<>();
        Numbers.generateNumArray(obj.getNumGroups() - 1).stream().filter(v
                -> obj.getGroup(v).getNumFaces() > 0)
                .forEach(v -> {
                    meshGroups.put(obj.getGroup(v).getName(), obj.getGroup(v).getNumFaces());
                });
        threeObj.setMeshGroups(meshGroups);

        threeObj.setMtlFiles(obj.getMtlFileNames());
        return threeObj;
    }
}