package com.github.funnyzak.web.controller.console.base;

import com.github.funnyzak.common.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.util.NutMap;
import com.github.funnyzak.bean.resource.ResourceInfo;
import com.github.funnyzak.bean.resource.WatermarkConfig;
import com.github.funnyzak.bean.resource.enums.WatermarkPosition;
import com.github.funnyzak.bean.resource.enums.WatermarkWay;
import com.github.funnyzak.biz.bean.UploadedFileInfo;
import com.github.funnyzak.biz.config.upload.FileUploadManager;
import com.github.funnyzak.biz.constant.JsonConstants;
import com.github.funnyzak.biz.service.resource.ResourceService;
import com.github.funnyzak.common.Result;
import com.github.funnyzak.common.id.IdCardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/12/16 12:17 下午
 * @description AppController
 */
@RestController
@RequestMapping("console/app")
@Api(value = "App", tags = {"后端.公共应用"})
public class AppController extends ConsoleBaseController {

    private final FileUploadManager fileUploadManager;
    private final ResourceService resourceService;

    @Autowired
    public AppController(FileUploadManager fileUploadManager,
                         ResourceService resourceService) {
        this.fileUploadManager = fileUploadManager;
        this.resourceService = resourceService;
    }

    @GetMapping("common/enum/{enum}")
    @ApiOperation("获取枚举信息")
    public Result enumInfo(@PathVariable("enum") @ApiParam("枚举定义名称") String enumName
            , @RequestParam(value = "type", defaultValue = "1") @ApiParam("类型") Integer type) {
        Class<? extends Enum> enumType = ReflectionUtils.matchEnum(enumName, "com.github.funnyzak");
        return Result.success().addData(JsonConstants.INFO_NAME, type == 1 ? PUtils.enumsInfoList(enumType) : type == 2 ? PUtils.enumsAllList(enumType) : PUtils.enumsAllList(enumType).stream().map(v -> v.toString()).collect(Collectors.joining(",")));
    }

    @GetMapping("common/enum/all")
    @ApiOperation("获取所有枚举")
    public Result allEnum() {
        return Result.success().addData(JsonConstants.LIST_NAME
                , ReflectionUtils
                        .allEnum("com.github.funnyzak")
                        .stream().map(v -> v.getName()).collect(Collectors.toList()));
    }

    @GetMapping("common/class/table/all")
    @ApiOperation("获取所有TableClass")
    public Result allTableClass() {
        return Result.success().addData(JsonConstants.LIST_NAME
                , ReflectionUtils
                        .typesAnnotatedWith(Table.class, "com.github.funnyzak.bean")
                        .stream().map(v -> v.getName()).collect(Collectors.toList()));
    }

    @GetMapping("common/class/json")
    @ApiOperation("获取Class JSON")
    public String classJson(@RequestParam(value = "classFullName", defaultValue = "1") @ApiParam("类名全路径") String classFullName
    ) throws Exception {
        return Json.toJson((Object) ReflectionUtils.classInstanceForName(classFullName), JsonFormat.full());
    }


    @PostMapping("image/covert/byBase64")
    @RequiresAuthentication
    @ApiOperation("转换Base64为图片")
    public Result imgBase64(@RequestBody Map<String, Object> mapData) {
        try {
            String base64Code = TypeParse.parseString(mapData.get("base64Code"));
            String pngPath = fileUploadManager.generateSavePath("png");
            File pngFile = Images.GenerateImage(base64Code, new File(pngPath));
            ResourceInfo info = resourceService.saveFile(fileUploadManager.pathFile2Info(pngFile.getAbsolutePath(), true, true), null, currentUser());
            return Result.success().addData(JsonConstants.INFO_NAME, info);
        } catch (Exception ex) {
            return Result.fail(ex.getMessage());
        }
    }


    @GetMapping("common/config/fontNameList")
    @RequiresAuthentication
    @ApiOperation("获取字体名称")
    public Result fontNameList() {
        return Result.success().addData(JsonConstants.LIST_NAME, FontUtils.getSystemFontList());
    }

    @GetMapping("id/decode")
    @RequiresAuthentication
    @ApiOperation("身份证解码")
    public Result idDecode(@RequestParam(value = "id", required = true) @ApiParam("身份证号码") String id) {
        if (!Validator.checkIdCard(id)) {
            return Result.fail("身份证号不正确");
        }
        NutMap map = new NutMap();
        NutMap baseInfoMap = IdCardUtils.analysisIdCard(id);
        map.put("baseInfo", baseInfoMap);
        return Result.success().addData(JsonConstants.INFO_NAME, map);
    }

    @GetMapping("preview")
    @RequiresAuthentication
    @ApiOperation("资源封面预览")
    public ResponseEntity<byte[]> resourceCoverPreview(
            @RequestParam(value = "origin", defaultValue = "false") @ApiParam("是否显示原图，显示原图时width、height参数失效") boolean origin
            , @RequestParam(value = "width", defaultValue = "128") @ApiParam("预览宽度") Integer width
            , @RequestParam(value = "height", defaultValue = "128") @ApiParam("预览高度") Integer height
            , @RequestParam(value = "resNum", required = false) @ApiParam("资源编号") String resNum
            , @RequestParam(value = "resKey", required = false) @ApiParam("资源Key") String resKey) throws Exception {
        ResourceInfo info = !StringUtils.isNullOrEmpty(resNum) ? resourceService.fetch(resNum) : resourceService.fetchByKey(resKey);
        if (info == null) {
            return null;
        }

        if (!origin) {
            UploadedFileInfo uploadedFileInfo = resourceService.localPreview(currentUser(), info, origin, width, height);

            if (uploadedFileInfo != null) {
                return responseImage(uploadedFileInfo);
            }
            response.setStatus(404);
        } else {
            response.setHeader("Location", info.getKey());
            response.setStatus(302);
        }
        return null;
    }

    @ResponseBody
    @GetMapping("image/watermark")
    @ApiOperation("图片添加水印")
    public ResponseEntity<byte[]> imageWatermark(@RequestParam(value = "position", defaultValue = "9") @ApiParam("位置1-9") int position,
                                                 @RequestParam(value = "way", required = false, defaultValue = "TEXT") @ApiParam("水印方式") WatermarkWay way,
                                                 @RequestParam(value = "text", required = false, defaultValue = "funnyzak") @ApiParam("文字水印文字") String text,
                                                 @RequestParam(value = "fontColor", required = false, defaultValue = "#000000") @ApiParam("文字水印颜色") String fontColor,
                                                 @RequestParam(value = "fontName", required = false, defaultValue = "微软雅黑") @ApiParam("字体名称") String fontName,
                                                 @RequestParam(value = "fontSize", required = false, defaultValue = "16") @ApiParam("文字水印大小") Integer fontSize,
                                                 @RequestParam(value = "horizontalPadding", required = false, defaultValue = "0") @ApiParam("横向间距") Integer horizontalPadding,
                                                 @RequestParam(value = "verticalPadding", required = false, defaultValue = "0") @ApiParam("纵向间距") Integer verticalPadding,
                                                 @RequestParam(value = "waterUrl", required = false) @ApiParam("水印图片") String waterUrl,
                                                 @RequestParam(value = "opacity", defaultValue = "100") @ApiParam("不透明度，0-100") Integer opacity,
                                                 @RequestParam(value = "imageUrl") @ApiParam("原图URL") String imageUrl,
                                                 @RequestParam(value = "forDownload", required = false) @ApiParam("是否下载") Boolean forDownload) throws Exception {

        String sourceImagePath = resourceService.fileLinkSaveLocal(imageUrl);
        if (StringUtils.isNullOrEmpty(sourceImagePath)) {
            return null;
        }

        WatermarkConfig config = new WatermarkConfig();
        config.setFontColor(fontColor);
        config.setFontName(fontName);
        config.setFontSize(fontSize);
        config.setPosition(WatermarkPosition.fromInteger(position));
        config.setWay(way);
        config.setText(text);
        config.setHorizontalPadding(horizontalPadding);
        config.setVerticalPadding(verticalPadding);
        config.setWatermarkUrl(waterUrl);
        config.setOpacity(opacity);

        String outputPath = resourceService.imageLinkAddWatermark(imageUrl, config);
        if (StringUtils.isNullOrEmpty(outputPath)) {
            return null;
        }

        return responseImage(fileUploadManager.pathFile2Info(outputPath));
    }
}