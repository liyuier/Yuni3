package command;

import com.yuier.yuni.core.api.message.SendMessage;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.message.detector.command.CommandDetector;
import com.yuier.yuni.event.message.detector.command.model.CommandModelBuilder;
import com.yuier.yuni.plugin.model.passive.message.CommandPlugin;

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
        SendMessage response = eventContext.getChatSession().response("Hello Command!");
    }

    @Override
    public CommandDetector getDetector() {
        return new CommandDetector(CommandModelBuilder.create("test").build());
    }

}
