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
package com.yqhp.agent.driver;

import lombok.Data;

/**
 * @author jiangyitao
 */
@Data
public class DeviceInfo {
    /**
     * 品牌
     */
    private String brand;
    /**
     * 制造商
     */
    private String manufacturer;
    /**
     * 内存 kB
     */
    private Long memSize;
    /**
     * 型号
     */
    private String model;
    private String systemVersion;
    private Integer screenWidth;
    private Integer screenHeight;
}
