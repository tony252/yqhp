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
package com.yqhp.auth.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yqhp.auth.model.CurrentUser;
import com.yqhp.auth.model.dto.UserInfo;
import com.yqhp.auth.model.param.CreateUserParam;
import com.yqhp.auth.model.param.UpdateUserParam;
import com.yqhp.auth.model.param.query.UserPageQuery;
import com.yqhp.auth.model.vo.UserVO;
import com.yqhp.auth.repository.enums.UserStatus;
import com.yqhp.auth.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author jiangyitao
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/page")
    public IPage<UserVO> pageBy(UserPageQuery query) {
        return userService.pageBy(query);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping
    public void createUser(@RequestBody @Valid CreateUserParam createUserParam) {
        userService.createUser(createUserParam);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/{userId}")
    public void updateUser(@PathVariable String userId, @RequestBody @Valid UpdateUserParam updateUserParam) {
        userService.updateUser(userId, updateUserParam);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/{userId}/status/{status}")
    public void changeStatus(@PathVariable String userId, @PathVariable UserStatus status) {
        userService.changeStatus(userId, status);
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable String userId) {
        userService.deleteById(userId);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/{userId}/resetPassword")
    public void resetPassword(@PathVariable String userId,
                              @NotBlank(message = "密码不能为空")
                              @Size(min = 5, max = 100, message = "密码长度必须在{min}-{max}之间") String password) {
        userService.resetPassword(userId, password);
    }

    @PutMapping
    public void updateUser(@RequestBody @Valid UpdateUserParam updateUserParam) {
        userService.updateUser(CurrentUser.id(), updateUserParam);
    }

    @GetMapping("/info")
    public UserInfo info() {
        return userService.getInfo();
    }

    @PostMapping("/changePassword")
    public void changePassword(@NotBlank(message = "旧密码不能为空")
                               @Size(min = 5, max = 100, message = "旧密码长度必须在{min}-{max}之间") String oldPassword,
                               @NotBlank(message = "新密码不能为空")
                               @Size(min = 5, max = 100, message = "新密码长度必须在{min}-{max}之间") String newPassword) {
        userService.changePassword(oldPassword, newPassword);
    }

    @GetMapping("/{userId}")
    public UserVO getVOById(@PathVariable String userId) {
        return userService.getVOById(userId);
    }

    @PostMapping("/users")
    public List<UserVO> listVOByIds(@RequestBody Set<String> userIds) {
        return userService.listVOByIds(userIds);
    }

    @PostMapping("/usersMap")
    public Map<String, UserVO> getVOMapByIds(@RequestBody Set<String> userIds) {
        return userService.getVOMapByIds(userIds);
    }
}
