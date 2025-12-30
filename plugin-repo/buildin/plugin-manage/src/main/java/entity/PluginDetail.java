package entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Title: PluginDetail
 * @Author yuier
 * @Package entity
 * @Date 2025/12/30 19:16
 * @description: 插件详情展示页
 */

@Data
@NoArgsConstructor
public class PluginDetail {

    private String name;
    private String description;
    private List<String> tips;
    private String author;
    private String version;
}
