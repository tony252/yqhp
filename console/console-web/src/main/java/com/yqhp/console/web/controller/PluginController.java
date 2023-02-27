package com.yqhp.console.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yqhp.console.model.param.CreatePluginParam;
import com.yqhp.console.model.param.UpdatePluginParam;
import com.yqhp.console.model.param.query.PluginPageQuery;
import com.yqhp.console.repository.entity.Plugin;
import com.yqhp.console.web.service.PluginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author jiangyitao
 */
@RestController
@RequestMapping("/plugin")
public class PluginController {

    @Autowired
    private PluginService pluginService;

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/page")
    public IPage<Plugin> pageBy(PluginPageQuery query) {
        return pluginService.pageBy(query);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping
    public Plugin createPlugin(@Valid @RequestBody CreatePluginParam createPluginParam) {
        return pluginService.createPlugin(createPluginParam);
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/{id}")
    public void deletePluginById(@PathVariable String id) {
        pluginService.deletePluginById(id);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/{id}")
    public Plugin updatePlugin(@PathVariable String id, @Valid @RequestBody UpdatePluginParam updatePluginParam) {
        return pluginService.updatePlugin(id, updatePluginParam);
    }

}
