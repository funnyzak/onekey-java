package com.github.funnyzak.onekey.web.controller.open.app;

import com.github.funnyzak.onekey.web.controller.open.dto.SendPmDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.github.funnyzak.onekey.bean.log.PmLog;
import com.github.funnyzak.onekey.bean.log.enums.PmType;
import com.github.funnyzak.onekey.bean.open.enums.ConnectorPermission;
import com.github.funnyzak.onekey.bean.resource.ResourceInfo;
import com.github.funnyzak.onekey.bean.resource.enums.ResourceCate;
import com.github.funnyzak.onekey.biz.constant.JsonConstants;
import com.github.funnyzak.onekey.biz.dto.open.OpenRequestDTO;
import com.github.funnyzak.onekey.biz.exception.BizException;
import com.github.funnyzak.onekey.biz.service.CacheService;
import com.github.funnyzak.onekey.biz.service.log.PmService;
import com.github.funnyzak.onekey.biz.service.resource.ResourceService;
import com.github.funnyzak.onekey.common.Result;
import com.github.funnyzak.onekey.web.annotation.auth.OpenAuth;
import com.github.funnyzak.onekey.web.annotation.weblog.WebLogger;
import com.github.funnyzak.onekey.web.controller.open.OpenBaseController;
import com.github.funnyzak.onekey.web.controller.open.OpenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/20 7:50 下午
 * @description OpenCmsController
 */
@RestController
@RequestMapping("open/app")
@Api(value = "OpenApp", tags = {"开放接口.应用"})
public class OpenAppController extends OpenBaseController {
    private PmService pmService;
    private CacheService cacheService;
    private ResourceService resourceService;

    @Autowired
    public OpenAppController(PmService pmService
            , ResourceService resourceService
            , CacheService cacheService) {
        this.pmService = pmService;
        this.resourceService = resourceService;
        this.cacheService = cacheService;
    }

    @PutMapping("image/upload")
    @OpenAuth(permissions = ConnectorPermission.APP_IMAGE_UPLOAD)
    @ApiOperation(value = "图片上传")
    @ResponseBody
    @WebLogger()
    public Result imageUpload(MultipartFile file) {
        try {
            ResourceInfo uploadedInfo = resourceService.userUpload(file, new ResourceInfo(ResourceCate.IMAGE, _ip()), null);
            return uploadedInfo != null ? Result.success().addData(JsonConstants.INFO_NAME, OpenUtils.Json.resource(uploadedInfo)) : Result.fail("上传失败");
        } catch (Exception ex) {
            return Result.fail(ex.getMessage());
        }
    }

    @PostMapping("send/sms")
    @ApiOperation("发送短信")
    @OpenAuth(permissions = ConnectorPermission.APP_SMS_SEND)
    @WebLogger
    public Result sendSms(@RequestBody SendPmDTO dto) {
        try {
            PmLog pmLog = new PmLog();
            pmLog.setType(PmType.SMS);
            pmLog.setIp(_ip());
            pmLog.setReceive(dto.getReceive());
            pmLog.setApp(OpenRequestDTO.getInstance().getAppId());
            if (!pmService.sendSms(pmLog,null,dto.getUse()).getSuccess()) {
                throw new BizException("短信发送失败，请稍后重试！");
            }
            return Result.success();
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }
}