package com.github.funnyzak.web.controller.console.cms;

import com.github.funnyzak.web.controller.console.base.ConsoleBaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.dao.Cnd;
import org.nutz.lang.Strings;
import com.github.funnyzak.bean.cms.City;
import com.github.funnyzak.biz.service.cms.CityService;
import com.github.funnyzak.common.utils.PUtils;
import com.github.funnyzak.common.Result;
import org.springframework.web.bind.annotation.*;

/**
 * @author silenceace@gmail.com
 */
@RestController
@RequestMapping("console/city")
@Api(value = "City", tags = {"后端.城市模块"})
public class CityController extends ConsoleBaseController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;

        this.currentControllerName = "城市列表";
    }


    /**
     * 城市列表
     *
     * @return
     * @RequestParam page 页码
     */
    @GetMapping("list")
    @RequiresAuthentication
    @ApiOperation("城市列表")
    public Result list(@RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page, @RequestParam(value = "pageSize", defaultValue = "15") @ApiParam("页大小") int pageSize, @RequestParam(value = "parentId", defaultValue = "-1") @ApiParam("父ID") int parentId, @RequestParam(value = "key", required = false) @ApiParam("搜索关键字") String key) {
        Cnd cnd = Cnd.NEW();
        if (!Strings.isEmpty(key)) {
            cnd = PUtils.cndBySearchKey(cnd, key, "name");
        }
        if (parentId >= 0) {
            cnd.and("parentId", "=", Integer.toString(parentId));
        }
        return Result.success().addData("pager", cityService.searchByPage(_fixPage(page), pageSize, cnd.desc("id")));
    }

    /**
     * 城市检索
     *
     * @param key  关键词
     * @param page 页码
     * @return
     */
    @GetMapping("search")
    @RequiresAuthentication
    @ApiOperation("城市检索")
    public Result search(@RequestParam("key") @ApiParam("关键词") String key, @RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page) {
        return Result.success().addData("pager", cityService.searchByKeyAndPage(_fixSearchKey(key), _fixPage(page), "name").addParam("key", key));
    }

    /**
     * 添加城市
     *
     * @param city 待添加城市
     * @return
     */
    @PostMapping("add")
    @RequiresAuthentication
    @ApiOperation("添加城市")
    public Result save(@RequestBody City city) {
        _addOperationLog("添加城市", String.format("%s>%s", city.getId(), city.getName()));
        return cityService.save(city) == null ? Result.fail("保存城市失败!") : Result.success().addData("city", city);
    }

    /**
     * 获取城市详情
     *
     * @param id 城市id
     * @return
     */
    @GetMapping("{id}")
    @RequiresAuthentication
    @ApiOperation("城市详情")
    public Result detail(@PathVariable("id") @ApiParam("城市id") long id) {
        return Result.success().addData("city", cityService.fetch(id));
    }

    /**
     * 删除城市
     *
     * @param id 城市id
     * @return
     */
    @GetMapping("delete/{id}")
    @RequiresAuthentication
    @ApiOperation("删除城市")
    public Result delete(@PathVariable("id") @ApiParam("城市id") long id) {
        _addOperationLog("删除城市", String.format("ID:%s", id));
        return cityService.delete(id) == 1 ? Result.success() : Result.fail("删除城市失败!");
    }

    /**
     * 更新城市
     *
     * @param city
     * @return
     */
    @PostMapping("edit")
    @RequiresAuthentication
    @ApiOperation("更新城市")
    public Result update(@RequestBody City city) {
        _addOperationLog("更新城市", String.format("%s>%s", city.getId(), city.getName()));
        return cityService.updateIgnoreNull(city) != 1 ? Result.fail("更新城市失败!") : Result.success().addData("city", city);
    }
}