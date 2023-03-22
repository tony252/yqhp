package com.yqhp.console.model.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author jiangyitao
 */
@Data
public class CreatePluginFileParam extends UpdatePluginFileParam {
    @NotBlank(message = "pluginId不能为空")
    private String pluginId;
}
