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
package com.yqhp.console.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yqhp.auth.model.CurrentUser;
import com.yqhp.console.model.param.CreatePlanParam;
import com.yqhp.console.model.param.UpdatePlanParam;
import com.yqhp.console.model.param.query.PlanPageQuery;
import com.yqhp.console.repository.entity.Plan;
import com.yqhp.console.web.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author jiangyitao
 */
@Validated
@RestController
@RequestMapping("/plan")
public class PlanController {

    @Autowired
    private PlanService planService;

    @GetMapping("/page")
    public IPage<Plan> pageBy(@Valid PlanPageQuery query) {
        return planService.pageBy(query);
    }

    @GetMapping("/{id}")
    public Plan getPlanById(@PathVariable String id) {
        return planService.getPlanById(id);
    }

    @GetMapping
    public List<Plan> listByProjectId(@NotBlank(message = "projectId不能为空") String projectId) {
        return planService.listByProjectId(projectId);
    }

    @PostMapping
    public Plan createPlan(@RequestBody @Valid CreatePlanParam param) {
        return planService.createPlan(param);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        planService.deleteById(id);
    }

    @PutMapping("/{id}")
    public Plan updatePlan(@PathVariable String id, @Valid @RequestBody UpdatePlanParam param) {
        return planService.updatePlan(id, param);
    }

    @GetMapping("/{id}/exec")
    public void exec(@PathVariable String id) {
        planService.exec(id, CurrentUser.id());
    }
}
