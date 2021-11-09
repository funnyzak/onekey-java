package com.github.funnyzak.web.controller.console.config;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.nutz.dao.Cnd;
import org.nutz.lang.Strings;
import com.github.funnyzak.bean.config.Config;
import com.github.funnyzak.bean.vo.InstallPermission;
import com.github.funnyzak.biz.ext.shiro.anno.RequiresPermissions;
import com.github.funnyzak.biz.service.config.ConfigService;
import com.github.funnyzak.common.utils.PUtils;
import com.github.funnyzak.common.Result;
import com.github.funnyzak.web.controller.console.base.ConsoleBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author potato
 */
@RestController
@RequestMapping("console/config")
@Api(value = "Config", tags = {"后端.配置模块"})
public class ConfigController extends ConsoleBaseController {

    private final ConfigService configService;

    @Autowired
    public ConfigController(ConfigService configService) {
        this.configService = configService;

        this.currentControllerName = "配置列表";
    }

    /**
     * 配置列表
     *
     * @return
     * @RequestParam page 页码
     */
    @GetMapping("list")
    @RequiresPermissions(InstallPermission.CONFIG_LIST)
    @ApiOperation("配置列表")
    public Result list(@RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page,
                       @RequestParam(value = "pageSize", defaultValue = "15") @ApiParam("页大小") int pageSize,
                       @RequestParam(value = "key", required = false) @ApiParam("搜索关键字") String key) {
        Cnd cnd = Cnd.NEW();
        if (!Strings.isEmpty(key)) {
            cnd = PUtils.cndBySearchKey(cnd, key, "name", "description");
        }
        return Result.success().addData("pager", configService.searchByPage(_fixPage(page), pageSize, cnd.desc("id")));
    }

    /**
     * 配置检索
     *
     * @param key  关键词
     * @param page 页码
     * @return
     */
    @GetMapping("search")
    @RequiresPermissions(InstallPermission.CONFIG_LIST)
    @ApiOperation("配置检索")
    public Result search(@RequestParam("key") @ApiParam("关键词") String key, @RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page) {
        return Result.success().addData("pager", configService.searchByKeyAndPage(_fixSearchKey(key), _fixPage(page), "name", "description").addParam("key", key));
    }

    /**
     * 添加配置
     *
     * @param config 待添加配置
     * @return
     */
    @PostMapping("add")
    @RequiresPermissions(InstallPermission.CONFIG_ADD)
    @ApiOperation("添加配置")
    public Result save(@RequestBody Config config) {
        _addOperationLog("添加配置", String.format("%s=%s", config.getName(), config.getValue()));
        return configService.save(config) == null ? Result.fail("保存配置失败!") : Result.success().addData("config", config);
    }

    /**
     * 获取配置详情
     *
     * @param id 配置id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions(InstallPermission.CONFIG_EDIT)
    @ApiOperation("配置详情")
    public Result detail(@PathVariable("id") @ApiParam("配置id") long id) {
        return Result.success().addData("config", configService.fetch(id));
    }

    /**
     * 删除配置
     *
     * @param id 配置id
     * @return
     */
    @GetMapping("delete/{id}")
    @RequiresPermissions(InstallPermission.CONFIG_DELETE)
    @ApiOperation("删除配置")
    public Result delete(@PathVariable("id") @ApiParam("配置id") long id) {
        _addOperationLog("删除配置", String.format("ID:%s", id));
        return configService.delete(id) == 1 ? Result.success() : Result.fail("删除配置失败!");
    }

    /**
     * 更新配置
     *
     * @param config
     * @return
     */
    @PostMapping("edit")
    @RequiresPermissions(InstallPermission.CONFIG_EDIT)
    @ApiOperation("更新配置")
    public Result update(@RequestBody Config config) {
        _addOperationLog("更新配置", String.format("%s=%s", config.getName(), config.getValue()));
        return configService.updateIgnoreNull(config) != 1 ? Result.fail("更新配置失败!") : Result.success().addData("config", config);
    }
}
