package com.github.funnyzak.web.controller;

import com.github.funnyzak.web.utils.WebUtils;
import eu.bitwalker.useragentutils.UserAgent;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.View;
import org.nutz.mvc.view.ForwardView;
import org.nutz.mvc.view.JspView;
import org.nutz.mvc.view.ServerRedirectView;
import org.nutz.mvc.view.UTF8JsonView;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.biz.bean.UploadedFileInfo;
import com.github.funnyzak.biz.exception.BizException;
import com.github.funnyzak.common.Result;
import com.github.funnyzak.common.utils.PUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/16 3:14 下午
 * @description OpenBaseController
 */
public class BaseController {
    @Autowired
    public HttpServletRequest request;

    @Autowired
    public HttpServletResponse response;

    public final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void _addCookie(String name, String value, int age) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(age);
        response.addCookie(cookie);
    }

    public HttpServletRequest request() {
        return request;
    }

    public String _base() {
        return request.getContextPath();
    }

    public UserAgent _ua() {
        return new UserAgent(request.getHeader("user-agent"));
    }

    public int _fixPage(int page) {
        return ((page <= 0) ? 1 : page);
    }

    public String _getCookie(String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Strings.equals(cookie.getName(), name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public String _ip() {
        return Lang.getIP(request);
    }

    protected void _putSession(String key, Object value) {
        request.getSession().setAttribute(key, value);
    }

    public View _renderForward(String path, Object[] objs) {
        request.setAttribute("objs", objs);
        return new ForwardView(path);
    }

    public View _renderJson(Object[] objs) {
        UTF8JsonView view = (UTF8JsonView) UTF8JsonView.NICE;
        view.setData(objs);
        return view;
    }

    public View _renderJsp(String path, Object[] objs) {
        request.setAttribute("objs", objs);
        return new JspView(path);
    }

    public View _renderRedirect(String path) {
        return new ServerRedirectView(path);
    }

    protected <T> PageredData<NutMap> shortPagerColumns(PageredData<T> pager, String displayColumns) {
        PageredData<NutMap> pager2 = new PageredData<>();
        if (pager != null) {
            pager2.setPager(pager.getPager());
        }
        pager2.setDataList(shortListColumns(pager.getDataList(), displayColumns));
        return pager2;
    }

    protected <T> List<NutMap> shortListColumns(List<T> list, String displayColumns) {
        if (list != null && list.size() > 0) {
            return list.stream().map(info -> PUtils.entityToNutMap(info, displayColumns)).collect(Collectors.toList());
        }
        return null;
    }

    public Result resultMaybeEx(Result result) {
        try {
            return result;
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    public Result failResultByException(Exception ex) {
        logger.error("路由API报错，路由地址：{}，Request具体信息：{}，错误信息=>", request.getRequestURL(), WebUtils.httpServletRequestToString(request), ex);
        if (ex instanceof BizException) {
            return Result.fail(ex.getMessage());
        } else {
            return Result.fail("处理失败");
        }
    }

    public ResponseEntity<byte[]> responseImage(UploadedFileInfo uploadedFileInfo) throws Exception{
        FileInputStream inputStream = new FileInputStream(uploadedFileInfo.getSavePath());
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes, 0, inputStream.available());
        return ResponseEntity.ok().contentType(uploadedFileInfo.getSuffix().equals("png") ? MediaType.IMAGE_PNG : uploadedFileInfo.getSuffix().equals("gif") ? MediaType.IMAGE_GIF : MediaType.IMAGE_JPEG).body(bytes);
    }

    public void responseFileStream(String outputFile) throws Exception {
        responseFileStream(new FileInputStream(new File(outputFile)), outputFile.substring(outputFile.lastIndexOf(File.separator) + 1));
    }

    public void responseFileStream(@NotNull InputStream is, @NotNull String downName) throws Exception {
        OutputStream os = null;
        BufferedInputStream bis = null;

        try {
            os = response.getOutputStream();
            bis = new BufferedInputStream(is);

            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Type", "application/octet-stream; charset=UTF-8");

            StringBuffer contentDisposition = new StringBuffer("attachment; filename=\"");
            String fileName = URLEncoder.encode(downName, "UTF-8");
            contentDisposition.append(fileName).append("\";");
            contentDisposition.append("filename*=utf-8 ''" + fileName);
            response.setHeader("Content-disposition", contentDisposition.toString());

            byte[] buffer = new byte[500];
            int i;
            while ((i = bis.read(buffer)) != -1) {
                os.write(buffer, 0, i);
            }
            os.flush();
        } catch (FileNotFoundException e) {
            throw new BizException(e);
        } catch (IOException e) {
            throw new BizException(e);
        } finally {
            if (os != null) os.close();
            if (bis != null) bis.close();
            if (is != null) is.close();
        }
    }

    /**
     * 转换为ID列表字符串
     *
     * @param ids
     * @return
     */
    public String _IdListString(long[] ids) {
        String str = "";
        if (ids.length == 0) {
            return "";
        }
        for (long id : ids) {
            str += id + ",";
        }
        return str.substring(0, str.length() - 1);
    }

    public <T> T checkNull(T info) throws Exception {
        if (info == null) {
            throw new BizException("数据不存在");
        }
        return info;
    }
}