提供了一个 yuni-dependency 模块专门用于统一管理依赖

root pom.xml 中维护项目结构，使用 `dependencyManagement` 引入统一管理；使用 `build` 定义构建行为

其他子项目声明 root pom.xml 为 `parent` ，自动引入统一依赖管理；在自己的 pom.xml 中只需以 `dependencies` 声明需求的依赖即可