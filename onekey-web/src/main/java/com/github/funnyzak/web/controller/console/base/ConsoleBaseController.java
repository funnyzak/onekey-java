package com.github.funnyzak.web.controller.console.base;

import org.apache.shiro.SecurityUtils;
import org.nutz.dao.entity.Record;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import com.github.funnyzak.bean.acl.User;
import com.github.funnyzak.bean.log.OperationLog;
import com.github.funnyzak.biz.service.acl.UserService;
import com.github.funnyzak.biz.service.log.OperationLogService;
import com.github.funnyzak.biz.service.resource.ResourceService;
import com.github.funnyzak.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * BaseController class
 *
 * @author potato
 * @date 2019/07/25
 */
public class ConsoleBaseController extends BaseController {
    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ResourceService resourceService;

    private User _currentUser;

    protected String currentControllerName = "模块操作";

    public HttpServletRequest request() {
        return request;
    }

    public String _fixSearchKey(String key) {
        if ((Strings.equalsIgnoreCase("get", request.getMethod())) && (Lang.isWin())) {
            key = (Strings.isBlank(key)) ? "" : key;
            try {
                return new String(key.getBytes(StandardCharsets.ISO_8859_1), request.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                logger.debug(e.getMessage());
                return "";
            }
        }
        return ((Strings.isBlank(key)) ? "" : key);
    }

    public String _getNameSpace() {
        return null;
    }

    public String _ip() {
        return Lang.getIP(request);
    }


    /**
     * 获取当前登录用户名
     *
     * @return
     */
    protected String _loginUserName() {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        return principal == null ? null : principal.toString();
    }

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    protected User currentUser() {
        if (_currentUser != null && _currentUser.getName().equals(_loginUserName())) {
            return _currentUser;
        }
        _currentUser = _loginUserName() == null ? null : userService.findByField("name", _loginUserName());
        return _currentUser;
    }


    /**
     * 根据Record参数值获取对应记录
     *
     * @param name
     * @param value
     * @param list
     * @return
     */
    protected Record _recordByNameValue(String name, String value, List<Record> list) {
        for (Record _record : list) {
            if (_record.getString(name).equals(value)) {
                return _record;
            }
        }
        return null;
    }

    /**
     * 添加操作日志
     *
     * @param module
     * @param action
     * @param description
     * @return
     */
    protected boolean _addOperationLog(String module, String action, String description) {
        OperationLog operationLog = new OperationLog();
        operationLog.setAction(action);
        operationLog.setDescription(description);
        operationLog.setIp(_ip());
        operationLog.setModule(module);
        operationLog.setAddUserId(currentUser().getId());
        return operationLogService.save(operationLog) != null;
    }

    protected boolean _addOperationLog(String action, String description) {
        return _addOperationLog(currentControllerName, action, description);
    }
}
