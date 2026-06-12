# Yuni3

基于 SpringBoot 实现的 OneBot 协议机器人应用。

```
.
├── docs               # 文档
├── plugin-repo        # 独立插件项目开发目录
├── sql                # 建表 SQL
├── yuni-adapter       # OneBot 协议适配器（WS/HTTP 传输、OneBot 协议模型、协议→内部事件翻译）
├── yuni-application   # 启动类模块
├── yuni-core          # 核心抽象与工具（YuniBot 接口、内部事件模型、消息链、通用工具）
├── yuni-dependency    # 统一依赖管理模块
├── yuni-engine        # 系统启动、事件分发、模块协调
├── yuni-event         # 事件匹配引擎（命令探测、正则匹配、通知/请求事件匹配）
├── yuni-permission    # 用户权限管理
├── yuni-plugin        # 插件系统实现（热加载、启停管理）
└── yuni-webapi        # 提供网络接口
```

## 流程图

* 系统启动

  ```mermaid
  flowchart TB

    application[启动类]
    adapter[adapter 模块]
    engine[engine 模块]
    plugin[插件管理模块]
    permission[权限管理模块]

    application --SystemInitializeRunner 代理启动--> engine
    engine --数据库检查--> engine
    engine --用户权限加载--> permission
    engine --插件扫描、加载--> plugin
    engine --BotManager 建立 OneBot 连接（注册事件回调 + 连接传输层）--> adapter
  ```

* OneBot 事件处理

  ```mermaid
  flowchart TB

    adapter[adapter 模块<br/>OneBotBot]
    engine[engine 模块<br/>EventBridge]
    event[event 模块<br/>事件匹配]
    plugin[插件系统]
    permission[权限管理模块]

    NapCat[NapCat 推送] --> adapter
    adapter --OneBotEvent → YuniEvent 翻译<br/>→ BotEventCallback--> engine
    engine --Spring Event 发布--> plugin
    subgraph pluginSystem[插件系统]
      permission --赋能用户权限检查--> plugin
      event --赋能事件-插件匹配--> plugin
    end
    subgraph handleEvents[处理事件]
      plugin --执行匹配到的插件--> execute[执行插件]
      adapter --YuniBot.sendMessage() 赋能通信--> execute
      ApplicationContext --赋能访问 Spring Bean--> execute
    end
  ```

## 部署方式

### OneBot 前台

采用 NapCat ，参考 [安装 NapCat](https://napneko.github.io/guide/install)

### 应用本体

见 [Docker 部署](https://yuni.yuier.com/guide/quickstart/deploy-by-docker.html)、[源码部署](https://yuni.yuier.com/guide/quickstart/deploy-by-sourcecode.html)

### 麦麦

通过适配插件引入麦麦实现 AI 聊天能力，麦麦的部署参考 [MaiBot 部署指南](https://docs.mai-mai.org/manual/deployment/) 、 [MaiBot Napcat Adapter 文档](https://docs.mai-mai.org/manual/adapters/napcat.html)

## TODO

* 引入持续对话特性，为插件提供更强大的能力

* 开发可视化管理界面

* 插件市场的设计与实现

* 更多插件开发。。。