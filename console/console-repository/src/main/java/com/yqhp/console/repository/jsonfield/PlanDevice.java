package com.yqhp.console.repository.jsonfield;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author jiangyitao
 */
@Data
public class PlanDevice {
    @NotBlank(message = "deviceId不能为空")
    private String deviceId;
    private boolean enabled;
}
