import com.yuier.yuni.core.enums.CommandArgRequireType;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.message.detector.command.CommandDetector;
import com.yuier.yuni.event.message.detector.command.model.CommandModelBuilder;
import com.yuier.yuni.plugin.model.passive.message.CommandPlugin;

/**
 * @Title: PluginManage
 * @Author yuier
 * @Package PACKAGE_NAME
 * @Date 2025/12/28 0:20
 * @description: 插件管理入口
 */

public class PluginManage extends CommandPlugin {
    @Override
    public void execute(YuniMessageEvent eventContext) {

    }

    @Override
    public CommandDetector getDetector() {
        return new CommandDetector(CommandModelBuilder.create("test")
                // 命令选项 -查看，携带可选参数 pluginSeq ，含义是插件序号
                .addOptionWithOptionalArg("-查看", "pluginSeq", "查看消息内容", CommandArgRequireType.NUMBER)
                .build()) ;
    }
}
