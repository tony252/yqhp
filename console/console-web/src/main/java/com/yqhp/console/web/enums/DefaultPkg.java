package com.yqhp.console.web.enums;

import lombok.Getter;

/**
 * @author jiangyitao
 */
public enum DefaultPkg {

    COMMON("common"),
    PAGE("page"),
    JSH("jsh"),
    ;

    @Getter
    private final String name;

    DefaultPkg(String name) {
        this.name = name;
    }
}
