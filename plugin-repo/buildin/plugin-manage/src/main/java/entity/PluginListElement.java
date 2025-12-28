package entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: PluginListElement
 * @Author yuier
 * @Package entity
 * @Date 2025/12/28 3:32
 * @description:
 */

@Data
@NoArgsConstructor
public class PluginListElement {
    private int pluginSeq;
    private String pluginName;
    private String pluginDescription;
    private boolean enable;
}
