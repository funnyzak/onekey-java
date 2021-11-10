package com.github.funnyzak.onekey.web.controller.console.cms;

import com.github.funnyzak.onekey.web.controller.console.base.ConsoleBaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.Logical;
import org.nutz.dao.Cnd;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.onekey.bean.cms.Article;
import com.github.funnyzak.onekey.bean.vo.InstallPermission;
import com.github.funnyzak.onekey.biz.constant.JsonConstants;
import com.github.funnyzak.onekey.biz.ext.shiro.anno.RequiresPermissions;
import com.github.funnyzak.onekey.biz.service.cms.ArticleService;
import com.github.funnyzak.onekey.common.Result;
import com.github.funnyzak.onekey.common.utils.StringUtils;
import com.github.funnyzak.onekey.web.annotation.weblog.WebLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/6/11 6:37 下午
 * @description ArticleController
 */
@RestController
@RequestMapping("console/cms/article")
@Api(value = "Article", tags = {"后端.内容模块"})
public class ArticleController extends ConsoleBaseController {
    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("list")
    @RequiresPermissions(logical = Logical.OR, value = {InstallPermission.CMS_ARTICLE_INFO_LIST})
    @ApiOperation("文章信息列表")
    @WebLogger
    public Result list(@RequestParam(value = "typeList", required = false) @ApiParam("内容类型列表") String typeList
            , @RequestParam(value = "cateId", required = false) @ApiParam("分类ID") Long cateId
            , @RequestParam(value = "addTimeStart", required = false) @ApiParam("添加时间开始") Long addTimeStart
            , @RequestParam(value = "addTimeEnd", required = false) @ApiParam("添加时间结束") Long addTimeEnd
            , @RequestParam(value = "keyword", required = false) @ApiParam("关键字") String keyword
            , @RequestParam(value = "author", required = false) @ApiParam("作者") String author
            , @RequestParam(value = "source", required = false) @ApiParam("来源") String source
            , @RequestParam(value = "published", required = false) @ApiParam("是否发布") Boolean published
            , @RequestParam(value = "orderBy", required = false) @ApiParam("排序字段") String orderBy
            , @RequestParam(value = "orderDesc", required = false) @ApiParam("是否降序") Boolean orderDesc
            , @RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") Integer page
            , @RequestParam(value = "pageSize", defaultValue = "15") @ApiParam("页大小") Integer pageSize) {

        Cnd cnd = articleService.condition(null, currentUser()
                , StringUtils.isNullOrEmpty(typeList) ? null : Arrays.asList(typeList.split(","))
                , cateId, addTimeStart, addTimeEnd, keyword, author, source, published);

        PageredData<Article> pager = articleService.pager(page, pageSize, cnd, orderBy, orderDesc);
        return Result.success().addData(JsonConstants.PAGER_NAME, pager);
    }

    @PutMapping("")
    @RequiresPermissions(logical = Logical.OR, value = {InstallPermission.CMS_ARTICLE_INFO_ADD})
    @ApiOperation("添加文章")
    public Result save(@RequestBody Article info) {
        try {
            return Result.success().addData(JsonConstants.INFO_NAME, articleService.add(info, currentUser()));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @GetMapping("{id}")
    @RequiresPermissions(logical = Logical.OR, value = {InstallPermission.CMS_ARTICLE_INFO_DETAIL})
    @ApiOperation("文章详情")
    public Result detail(@PathVariable("id") @ApiParam("文章id") Long id) {
        return Result.success().addData(JsonConstants.INFO_NAME, articleService.fetch(id));
    }

    @DeleteMapping("{id}")
    @RequiresPermissions(logical = Logical.OR, value = {InstallPermission.CMS_ARTICLE_INFO_DELETE})
    @ApiOperation("删除文章")
    public Result delete(@PathVariable("id") @ApiParam("文章id") Long id) {
        try {
            articleService.remove(id);
            return Result.success();
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @PostMapping("")
    @RequiresPermissions(logical = Logical.OR, value = {InstallPermission.CMS_ARTICLE_INFO_EDIT})
    @ApiOperation("更新文章")
    public Result update(@RequestBody Article info) {
        try {
            return Result.success().addData(JsonConstants.INFO_NAME, articleService.edit(info, currentUser()));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }
}