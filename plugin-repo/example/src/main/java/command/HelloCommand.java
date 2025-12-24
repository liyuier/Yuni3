package command;

import com.yuier.yuni.event.model.context.YuniMessageEvent;
import com.yuier.yuni.event.model.message.detector.command.CommandDetector;
import com.yuier.yuni.event.model.message.detector.command.model.CommandModelBuilder;
import com.yuier.yuni.plugin.model.passive.CommandPlugin;

/**
 * @Title: command.HelloCommand
 * @Author yuier
 * @Package PACKAGE_NAME
 * @Date 2025/12/23 21:03
 * @description:
 */

public class HelloCommand extends CommandPlugin {

    @Override
    public void execute(YuniMessageEvent eventContext) {
        eventContext.getChatSession().response("Hello Command!");
    }

    @Override
    public CommandDetector getDetector() {
        return new CommandDetector(CommandModelBuilder.create("test").build());
    }

}
