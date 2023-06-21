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
package com.yqhp.console.web.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yqhp.auth.model.CurrentUser;
import com.yqhp.common.web.exception.ServiceException;
import com.yqhp.console.model.param.CreatePluginFileParam;
import com.yqhp.console.repository.entity.PluginFile;
import com.yqhp.console.repository.mapper.PluginFileMapper;
import com.yqhp.console.web.enums.ResponseCodeEnum;
import com.yqhp.console.web.service.PluginFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author jiangyitao
 */
@Service
public class PluginFileServiceImpl
        extends ServiceImpl<PluginFileMapper, PluginFile>
        implements PluginFileService {

    @Autowired
    private Snowflake snowflake;

    @Override
    public void createPluginFile(CreatePluginFileParam param) {
        PluginFile pluginFile = param.convertTo();
        pluginFile.setId(snowflake.nextIdStr());

        String currUid = CurrentUser.id();
        pluginFile.setCreateBy(currUid);
        pluginFile.setUpdateBy(currUid);

        try {
            if (!save(pluginFile)) {
                throw new ServiceException(ResponseCodeEnum.SAVE_PLUGIN_FILE_FAIL);
            }
        } catch (DuplicateKeyException e) {
            throw new ServiceException(ResponseCodeEnum.DUPLICATE_PLUGIN_FILE);
        }
    }

    @Override
    public PluginFile getPluginFileById(String id) {
        return Optional.ofNullable(getById(id))
                .orElseThrow(() -> new ServiceException(ResponseCodeEnum.PLUGIN_FILE_NOT_FOUND));
    }

    @Override
    public void deleteById(String id) {
        if (!removeById(id)) {
            throw new ServiceException(ResponseCodeEnum.DEL_PLUGIN_FILE_FAIL);
        }
    }

    @Override
    public List<PluginFile> listByPluginId(String pluginId) {
        Assert.hasText(pluginId, "pluginId must has text");
        LambdaQueryWrapper<PluginFile> query = new LambdaQueryWrapper<>();
        query.eq(PluginFile::getPluginId, pluginId);
        return list(query);
    }

    @Override
    public List<PluginFile> listInPluginIds(Collection<String> pluginIds) {
        if (CollectionUtils.isEmpty(pluginIds)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<PluginFile> query = new LambdaQueryWrapper<>();
        query.in(PluginFile::getPluginId, pluginIds);
        return list(query);
    }
}
