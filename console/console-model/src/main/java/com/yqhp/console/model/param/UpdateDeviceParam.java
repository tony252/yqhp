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
package com.yqhp.console.model.param;

import com.yqhp.common.web.model.InputConverter;
import com.yqhp.console.repository.entity.Device;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Map;

/**
 * @author jiangyitao
 */
@Data
public class UpdateDeviceParam implements InputConverter<Device> {
    @Size(max = 128, message = "制造商长度不能超过{max}")
    private String manufacturer;
    @Size(max = 128, message = "品牌长度不能超过{max}")
    private String brand;
    @Size(max = 128, message = "型号长度不能超过{max}")
    private String model;
    @Size(max = 128, message = "CPU长度不能超过{max}")
    private String cpu;
    private Long memSize;
    @Size(max = 512, message = "图片url长度不能超过{max}")
    private String imgUrl;
    @Size(max = 16, message = "系统版本长度不能超过{max}")
    private String systemVersion;
    private Integer screenWidth;
    private Integer screenHeight;
    @Size(max = 256, message = "描述长度不能超过{max}")
    private String description;
    private Map<String, Object> extra;
}
