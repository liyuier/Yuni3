# Yuni3

基于 SpringBoot 实现的 OneBot 协议机器人应用。

```
.
├── docs               # 文档
├── plugin-repo        # 独立插件项目开发目录
├── sql                # 建表 SQL
├── yuni-adapter       # OneBot HTTP 与 WS 通信适配器模块
├── yuni-application   # 启动类模块
├── yuni-core          # 一些核心的模型、工具类定义 / 实现
├── yuni-dependency    # 统一依赖管理模块
├── yuni-engine        # 系统启动、事件分发、模块协调
├── yuni-event         # 对 OneBot 事件模型的定义与实现
├── yuni-permission    # 用户权限管理
├── yuni-plugin        # 插件系统实现
└── yuni-webapi        # 提供网络接口
```

## 流程图

* 系统启动

  ```mermaid
  flowchart TB

    application[启动类]
    webapi[事件入口]
    adapter[adapter 模块]
    engine[engine 模块]
    plugin[插件管理模块]
    permission[权限管理模块]
    system[通用系统启动]

    application --engine 模块基于 ApplicationRunner 代理系统启动--> engine
    engine --数据库初始化--> system
    engine --插件扫描、加载--> plugin
    engine --用户权限加载--> permission
    engine --建立到 OneBot 的 WebSocket 连接--> system

    SpringBoot@{ shape: circle, label: "Start" }
    application --SpringBoot原生启动--> SpringBoot
    SpringBoot --注册 HTTP Controller--> webapi
    SpringBoot --通过配置类注入 OneBot API 通信适配器--> adapter
  ```

* OneBot 事件处理

  ```mermaid
  flowchart TB

    webapi[事件入口]
    adapter[adapter 模块]
    engine[engine 模块]
    permission[权限管理模块]
    event[event 模块]
    plugin[插件模块]

    OneBotEvent@{shape: lean-r, label: OneBot 上报事件} --> webapi
    webapi --原始请求交给engine包装为事件--> engine
    engine --交给插件系统处理--> plugin
    subgraph pluginSystem[插件系统]
      permission --赋能用户权限检查--> plugin
      event --赋能事件-插件匹配--> plugin
    end
    subgraph executePlugin[执行插件]
      plugin --执行匹配到的插件--> execute@{ shape: lean-l, label: "执行插件" }
      adapter --赋能与 OneBot 通信--> execute
      ApplicationContext --赋能访问 SpringBoot Bean--> execute
    end
  ```

## 部署方式

~~先暂时简单写写，详细的以后再补~~

### OneBot 前台

采用 NapCat ，参考 [安装 NapCat](https://napneko.github.io/guide/install)

### 应用本体

配置文件把 yuni-application 模块下的 example.yml 改成 application.yml 就行，具体配置什么含义都写了注释的

本地部署直接~~点一下IDEA的绿色三角形~~用IDEA就行。要构建的话我自己写了个备忘录可以参考着看 https://yuier.com/posts/027-yuni-deploy

插件 jar 包丢到 `根目录/plugins` 下

### 麦麦

通过适配插件引入麦麦实现 AI 聊天能力，麦麦的部署参考 [MaiBot 部署指南](https://docs.mai-mai.org/manual/deployment/) 、 [MaiBot Napcat Adapter 文档](https://docs.mai-mai.org/manual/adapters/napcat.html)