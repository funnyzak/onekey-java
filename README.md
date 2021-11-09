# OneKey-Java [![Release Version](https://img.shields.io/github/release/funnyzak/onekey-java.svg)](https://github.com/funnyzak/onekey-java/releases/latest) [![Latest Release Download](https://img.shields.io/github/downloads/funnyzak/onekey-java/latest/total.svg)](https://github.com/funnyzak/onekey-java/releases/latest) [![Total Download](https://img.shields.io/github/downloads/funnyzak/onekey-java/total.svg)](https://github.com/funnyzak/onekey-java/releases)


SpringBoot项目开发脚手架，基于JDK 1.8。使用Maven进行包管理。

项目目标是要达到开箱即用的体验，可以快速上手开始一个项目专注业务实现。

项目基于前后端分离。前端管理配套项目可从 **[OneKey-Admin](https://github.com/funnyzak/onekey-admin)** 获得，已开源。




主要依赖：

- [Nutz 1.6](https://nutzam.github.io/nutz/)
- [SpringBoot 2.3](#)

演示地址： [oconsole.niuqi.cc](http://oconsole.niuqi.cc)



## 构建

项目使用了 Lombok ，使用Idea开发时需要安装插件 Lombok Plugin 。

Lombok 相关文档 [点这里](https://projectlombok.org/features/all) 。

构建步骤：

1. 拉取代码：`git clone git@github.com:funnyzak/onekey-java.git && cd onekey-java`
2. Maven构建： `mvn install && mvn idea:idea`
3. 用idea打开，并选择父工程右键添加maven支持
4. 在 `onekey-web => resources` 配置 **application.yaml** 数据库等相关信息。
5. 在`onekey-web`模块下，运行入口类启动Web项目。

打包：

- 在根目录执行：`mvn package` 即可。

运行：

- `java -jar jar.name.jar`即可。


## 功能

- 基于角色、用户的权限控制实现。
- 基于组织架构的数据权限实现。
- 系统登录日志。
- 业务操作日志。
- 短信发送、邮件发送模块。
- 开放平台连接器、会员管理，基于此开发对外的数据接口。
- 文章、城市 CMS功能模块。
- swagger ui集成。
- Redis集成。
- 第三方集成：阿里云OSS、短信；腾讯云COS、短信、邮件、验证码；GeoIP
- 等等...


## 模块

### onekey-baan

定义的所有数据Bean, 项目运行时通过定义直接在数据库创建对应的数据库表。

### onekey-biz

对应Bean的业务操作，权限，以及第三方开放服务接入、实现等。

### onekey-common

通用模块，封装工具类。

### onekey-web

Web前端App服务。


## 赞赏

![赞赏](./_docs/assets/img/coffee.png)



## Author

| [![twitter/funnyzak](https://s.gravatar.com/avatar/c2437e240644b1317a4a356c6d6253ee?s=70)](https://twitter.com/funnyzak "Follow @funnyzak on Twitter") |
|---|
| [funnyzak](https://yycc.me/) |



## License

Apache-2.0 License © 2021 [funnyzak](https://github.com/funnyzak)

