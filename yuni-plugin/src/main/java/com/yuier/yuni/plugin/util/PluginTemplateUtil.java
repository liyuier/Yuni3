package com.yuier.yuni.plugin.util;

import com.yuier.yuni.plugin.model.YuniPlugin;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;

/**
 * @Title: PluginTemplateUtil
 * @Author yuier
 * @Package com.yuier.yuni.plugin.util
 * @Date 2025/12/28 17:23
 * @description: 使用 thymeleaf 的模板工具
 */

public class PluginTemplateUtil {

    public static String renderTemplateFromJarFile(Context context, String filePath, YuniPlugin plugin) {

        String templateString = PluginUtils.loadTextFromPluginJar(plugin, filePath);

        return renderTemplateFromString(context, templateString);
    }

    public static String renderTemplateFromString(Context context, String templateString) {

        TemplateEngine stringTemplateEngine = getStringTemplateEngine();

        return stringTemplateEngine.process(templateString, context);
    }

    private static TemplateEngine getStringTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(new StringTemplateResolver());
        return templateEngine;
    }
}
