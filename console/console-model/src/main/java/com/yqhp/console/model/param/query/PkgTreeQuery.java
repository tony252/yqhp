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
package com.yqhp.console.model.param.query;

import com.yqhp.console.repository.enums.PkgType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author jiangyitao
 */
@Data
public class PkgTreeQuery {
    @NotBlank(message = "项目不能为空")
    private String projectId;

    @NotNull(message = "目录类型不能为空")
    private PkgType type;

    @NotBlank(message = "parentId不能为空")
    private String parentId;

    private boolean listItem;
}
