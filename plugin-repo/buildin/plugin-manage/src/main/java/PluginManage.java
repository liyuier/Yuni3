import com.yuier.yuni.core.enums.CommandArgRequireType;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.message.detector.command.CommandDetector;
import com.yuier.yuni.event.message.detector.command.model.CommandBuilder;
import com.yuier.yuni.event.message.detector.command.model.matched.CommandMatched;
import com.yuier.yuni.plugin.model.passive.message.CommandPlugin;

/**
 * @Title: PluginManage
 * @Author yuier
 * @Package PACKAGE_NAME
 * @Date 2025/12/28 0:20
 * @description: 插件管理入口
 */

public class PluginManage extends CommandPlugin {

    private final String HEAD = "插件";

    private final String VIEW = "查看";
    private final String VIEW_SEQ = "pluginSeq";

    private PluginShow pluginShow = new PluginShow();

    @Override
    public void execute(YuniMessageEvent eventContext) {
        CommandMatched commandMatched = eventContext.getCommandMatched();
        if (commandMatched.hasOption(VIEW)) {
            if (commandMatched.optionHasOptionalArg(VIEW)) {
                pluginShow.showPluginDetail(eventContext, commandMatched.getOptionOptionalArgValue(VIEW));
            } else {
                pluginShow.showPluginList(eventContext);
            }
        }
    }

    @Override
    public CommandDetector getDetector() {
        return new CommandDetector(CommandBuilder.create(HEAD)
                // 命令选项 -查看，携带可选参数 pluginSeq ，含义是插件序号
                .addOptionWithOptionalArg(VIEW, VIEW_SEQ, "查看指定插件详情", CommandArgRequireType.NUMBER)
                .build()) ;
    }
}
