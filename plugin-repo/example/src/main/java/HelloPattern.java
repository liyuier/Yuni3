import com.yuier.yuni.event.model.context.YuniMessageEvent;
import com.yuier.yuni.event.model.message.detector.pattern.PatternDetector;
import com.yuier.yuni.plugin.model.passive.PatternPlugin;

/**
 * @Title: HelloPattern
 * @Author yuier
 * @Package PACKAGE_NAME
 * @Date 2025/12/23 20:06
 * @description:
 */

public class HelloPattern extends PatternPlugin {

    @Override
    public void execute(YuniMessageEvent eventContext) {
        eventContext.getChatSession().response("你好，我是 Yuni !");
    }

    @Override
    public PatternDetector getDetector() {
        return new PatternDetector(chain -> {
            return chain.containsString("你好");
        });
    }
}
