# Yuni3

基于 SpringBoot 实现的 OneBot 协议机器人应用。

* docs  文档目录
* logs  日志目录
* plugin-repo  独立插件项目开发目录
* sql  创建数据库的 sql 脚本目录
* tmp  项目临时文件目录
* yuni-adapter  适配器模块，目前主要功能是适配到 OneBot 服务器的 http 与 ws 两种通信模式
* yuni-application  启动类目录
  * plugins  构建后的插件 jar 包目录，应用启动时将会加载这个目录下所有插件 jar 包
* yuni-core  一些核心的模型、工具类定义 / 实现
* yuni-dependency  通过 dependencyManagement 提供统一依赖管理
* yuni-engine  系统启动，事件分发，模块协调
* yuni-event  对 OneBot 事件模型的定义与实现
* yuni-permission  用户权限管理
* yuni-plugin  插件管理
* yuni-webapi  提供网络接口，包括 http controller 层与到 OneBot 服务器 /event 路径的 ws 连接

