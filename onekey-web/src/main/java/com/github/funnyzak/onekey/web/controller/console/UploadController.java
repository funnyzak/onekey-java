package com.github.funnyzak.onekey.web.controller.console;

import com.github.funnyzak.onekey.web.controller.console.base.ConsoleBaseController;
import io.swagger.annotations.Api;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import com.github.funnyzak.onekey.bean.resource.ResourceInfo;
import com.github.funnyzak.onekey.bean.resource.enums.ResourceCate;
import com.github.funnyzak.onekey.biz.constant.JsonConstants;
import com.github.funnyzak.onekey.biz.service.resource.ResourceService;
import com.github.funnyzak.onekey.common.Result;
import com.github.funnyzak.onekey.web.annotation.weblog.WebLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author Leon Yang
 */
@Controller
@RequestMapping("console/upload")
@Api(value = "Upload", tags = {"后端.文件上传"})
public class UploadController extends ConsoleBaseController {

    private final ResourceService resourceService;

    @Autowired
    public UploadController(ResourceService resourceService) {
        this.resourceService = resourceService;
        this.currentControllerName = "文件上传";
    }

    @PostMapping("image/local")
    @RequiresAuthentication
    @WebLogger()
    @ResponseBody
    public Result imageLocalUpload(MultipartFile file) {
        try {
            ResourceInfo uploadedInfo = resourceService.userUpload(file, new ResourceInfo(ResourceCate.ATTACHMENT, _ip()), currentUser());
            return uploadedInfo != null ? Result.success().addData(JsonConstants.INFO_NAME, uploadedInfo) : Result.fail("上传失败");
        } catch (Exception ex) {
            return Result.fail(ex.getMessage());
        }
    }

    @PostMapping("file/local")
    @RequiresAuthentication
    @WebLogger()
    @ResponseBody
    public Result localUpload(MultipartFile file) {
        try {
            ResourceInfo uploadedInfo = resourceService.userUpload(file, new ResourceInfo(ResourceCate.ATTACHMENT, _ip()), currentUser());
            return uploadedInfo != null ? Result.success().addData(JsonConstants.INFO_NAME, uploadedInfo) : Result.fail("上传失败");
        } catch (Exception ex) {
            return Result.fail(ex.getMessage());
        }
    }
}
