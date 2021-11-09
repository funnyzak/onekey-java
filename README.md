# OneKey-Java

SpringBoot项目开发脚手架，基于JDK 1.8。使用Maven进行包管理。

项目基于前后端分离。前端管理配套项目可从 [OneKey-Admin](https://github.com/funnyzak/onekey-admin) 获得。

主要依赖：

- nutz.dao


## 使用

项目使用了 Lombok ，使用Idea开发时需要安装插件 Lombok Plugin 。

Lombok 相关文档 [点这里](https://projectlombok.org/features/all) 。

构建步骤：

1. 拉取代码：`git clone git@github.com:funnyzak/onekey-java.git && cd onekey-java`
2. Maven构建： `mvn install && mvn idea:idea`
3. 用idea打开，并选择父工程右键添加maven支持
4. 在 `onekey-web => resources` 配置 **application.yaml** 数据库等相关信息。
5. 在`onekey-web`模块下，运行入口类启动Web项目。

---
