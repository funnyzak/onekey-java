package com.github.funnyzak.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@ApiModel("登录模型")
public class UserLoginDto {
    /**
     * 用户名
     */
    @NonNull
    @ApiModelProperty(value = "用户名", required = true)
    private String userName;

    /**
     * 密码
     */
    @NonNull
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    /**
     * 记住用户密码
     */
    @ApiModelProperty(value = "是否记住密码", required = false)
    private boolean rememberMe = true;

    /**
     * 验证码
     */
    @ApiModelProperty(value = "验证码", required = false)
    private String captcha;

}
