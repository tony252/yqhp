package com.yqhp.console.model.vo;

import com.yqhp.common.web.model.OutputConverter;
import com.yqhp.console.repository.entity.DocExecutionRecord;
import com.yqhp.console.repository.entity.ExecutionRecord;
import com.yqhp.console.repository.entity.PluginExecutionRecord;
import com.yqhp.console.repository.entity.Project;
import lombok.Data;

import java.util.List;

/**
 * @author jiangyitao
 */
@Data
public class ExecutionRecordDetails extends ExecutionRecord
        implements OutputConverter<ExecutionRecordDetails, ExecutionRecord> {
    private Project project;
    private String creator;
    private List<PluginExecutionRecord> pluginExecutionRecords;
    private List<DocExecutionRecord> docExecutionRecords;
}
