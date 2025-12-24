package dynamicinjection;

import com.yuier.yuni.core.util.SpringContextUtil;
import com.yuier.yuni.event.model.context.YuniMessageEvent;
import com.yuier.yuni.event.model.message.detector.command.CommandDetector;
import com.yuier.yuni.event.model.message.detector.command.model.CommandModelBuilder;
import com.yuier.yuni.plugin.model.passive.CommandPlugin;
import lombok.extern.slf4j.Slf4j;

/**
 * @Title: InjectionResultTest
 * @Author yuier
 * @Package dynamicinjection
 * @Date 2025/12/25 1:07
 * @description: 简单测试一下动态注入的效果
 */

@Slf4j
public class InjectionResultTest extends CommandPlugin {
    @Override
    public void execute(YuniMessageEvent eventContext) {
        try {
            ExampleClass exampleBead = SpringContextUtil.getBean(ExampleClass.class);
            log.info("[InjectionResultTest] 动态注入成功！exampleBead.getValue = {}", exampleBead.getValue());
        } catch (Exception e) {
            log.error("[InjectionResultTest] 获取动态注入的类失败！", e);
        }
    }

    @Override
    public CommandDetector getDetector() {
        return new CommandDetector(CommandModelBuilder.create("test2").build());
    }
}
