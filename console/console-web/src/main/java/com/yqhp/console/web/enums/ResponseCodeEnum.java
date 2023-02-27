package com.yqhp.console.web.enums;

import com.yqhp.common.web.model.ResponseCode;

/**
 * @author jiangyitao
 */
public enum ResponseCodeEnum implements ResponseCode {

    FIELD_ERRORS(444, "请求参数错误"),
    INTERNAL_SERVER_ERROR(500, "系统错误，请联系管理员"),

    SAVE_PROJECT_FAIL(1100, "保存项目失败，请稍后重试"),
    DEL_PROJECT_FAIL(1101, "删除项目失败，请稍后重试"),
    UPDATE_PROJECT_FAIL(1102, "更新项目失败，请稍后重试"),
    PROJECT_NOT_FOUND(1103, "项目不存在"),
    DUPLICATE_PROJECT(1104, "项目已存在"),

    SAVE_USER_PROJECT_FAIL(1300, "保存用户项目失败，请稍后重试"),
    DEL_USER_PROJECT_FAIL(1301, "删除用户项目失败，请稍后重试"),
    DUPLICATE_USER_PROJECT(1302, "用户项目已存在"),

    UPDATE_DEVICE_FAIL(1602, "更新设备失败，请稍后重试"),
    DEVICE_NOT_FOUND(1603, "设备不存在"),
    DEL_DEVICE_FAIL(1604, "删除设备失败，请稍后重试"),

    SAVE_DOC_FAIL(1700, "保存文档失败，请稍后重试"),
    DEL_DOC_FAIL(1701, "删除文档失败，请稍后重试"),
    UPDATE_DOC_FAIL(1702, "更新文档失败，请稍后重试"),
    DOC_NOT_FOUND(1703, "文档不存在"),
    DUPLICATE_DOC(1704, "文档已存在"),
    DOC_UNDELETABLE(1705, "无法删除的文档"),
    DOC_UNMOVABLE(1706, "无法移动的文档"),
    DOC_UNRENAMABLE(1707, "无法重命名的文档"),
    DOC_UNUPDATABLE(1709, "无法更新的文档"),
    FORMAT_JAVA_CODE_FAIL(1710, "格式化JAVA代码失败"),
    AVAILABLE_DOC_CONTENT_MUST_HAS_TEXT(1711, "可用的文档内容不能为空"),

    SAVE_VIEW_RESOURCE_FAIL(2100, "保存视图资源失败，请稍后重试"),
    DEL_VIEW_RESOURCE_FAIL(2101, "删除视图资源失败，请稍后重试"),
    UPDATE_VIEW_RESOURCE_FAIL(2102, "更新视图资源失败，请稍后重试"),
    VIEW_RESOURCE_NOT_FOUND(2103, "视图资源不存在"),

    SAVE_PKG_FAIL(2400, "保存包失败，请稍后重试"),
    DEL_PKG_FAIL(2401, "删除包失败，请稍后重试"),
    UPDATE_PKG_FAIL(2402, "更新包失败，请稍后重试"),
    PKG_NOT_FOUND(2403, "包不存在"),
    DUPLICATE_PKG(2404, "包已存在"),
    PKG_DOCS_NOT_EMPTY(2405, "包下文档不为空"),
    PKG_ACTIONS_NOT_EMPTY(2406, "包下Action不为空"),
    PKG_UNDELETABLE(2410, "无法删除的包"),
    PKG_UNMOVABLE(2411, "无法移动的包"),
    PKG_UNRENAMABLE(2412, "无法重命名的包"),
    PKG_UNUPDATABLE(2413, "无法更新的包"),

    SAVE_PLUGIN_FAIL(3000, "保存插件失败，请稍后重试"),
    DEL_PLUGIN_FAIL(3001, "删除插件失败，请稍后重试"),
    UPDATE_PLUGIN_FAIL(3002, "更新插件失败，请稍后重试"),
    PLUGIN_NOT_FOUND(3003, "插件不存在"),
    DUPLICATE_PLUGIN(3004, "插件已存在"),
    PROJECT_IN_USE(3005, "项目正在使用该插件"),

    SAVE_PROJECT_PLUGIN_FAIL(3100, "保存项目插件失败，请稍后重试"),
    DEL_PROJECT_PLUGIN_FAIL(3101, "删除项目插件失败，请稍后重试"),
    DUPLICATE_PROJECT_PLUGIN(3102, "项目插件已存在"),

    SAVE_ACTION_FAIL(3200, "保存Action失败，请稍后重试"),
    DEL_ACTION_FAIL(3201, "删除Action失败，请稍后重试"),
    UPDATE_ACTION_FAIL(3202, "更新Action失败，请稍后重试"),
    ACTION_NOT_FOUND(3203, "Action不存在"),
    DUPLICATE_ACTION(3204, "Action已存在"),
    ACTION_UNDELETABLE(3205, "无法删除的Action"),
    ACTION_UNMOVABLE(3206, "无法移动的Action"),
    ACTION_UNRENAMABLE(3207, "无法重命名的Action"),
    ACTION_UNUPDATABLE(3209, "无法更新的Action"),

    SAVE_PLAN_FAIL(3300, "保存计划失败，请稍后重试"),
    DEL_PLAN_FAIL(3301, "删除计划失败，请稍后重试"),
    UPDATE_PLAN_FAIL(3302, "更新计划失败，请稍后重试"),
    PLAN_NOT_FOUND(3303, "计划不存在"),
    DUPLICATE_PLAN(3304, "计划已存在"),
    NO_DEVICES_OR_ACTIONS(3305, "无可用设备或action"),
    NO_STEP_EXECUTION_RECORDS(3306, "无可用步骤执行记录"),

    SAVE_PLAN_DEVICE_FAIL(3400, "保存计划设备失败，请稍后重试"),
    DEL_PLAN_DEVICE_FAIL(3401, "删除计划设备失败，请稍后重试"),
    DUPLICATE_PLAN_DEVICE(3402, "计划设备已存在"),

    SAVE_PLAN_ACTION_FAIL(3500, "保存计划Action失败，请稍后重试"),
    DEL_PLAN_ACTION_FAIL(3501, "删除计划Action失败，请稍后重试"),
    DUPLICATE_PLAN_ACTION(3502, "计划Action已存在"),
    ;

    private final int code;
    private final String message;

    ResponseCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}