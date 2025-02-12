/*
 *  Copyright https://github.com/yqhp
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.yqhp.console.model.dto;

import com.yqhp.console.repository.entity.DocExecutionRecord;
import com.yqhp.console.repository.enums.ExecutionStatus;
import lombok.Data;

import java.util.List;

/**
 * @author jiangyitao
 */
@Data
public class DeviceDocExecutionResult {
    // for action
    private Long passCount = 0L;
    private Long failureCount = 0L;
    private Integer totalCount = 0;
    private String passRate;

    private Long startTime = 0L;
    private Long endTime = 0L;
    private boolean isFinished;
    private ExecutionStatus status;
    private List<DocExecutionRecord> records;
}
