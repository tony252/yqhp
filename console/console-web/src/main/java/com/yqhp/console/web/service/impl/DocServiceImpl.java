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
import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yqhp.auth.model.CurrentUser;
import com.yqhp.common.web.exception.ServiceException;
import com.yqhp.console.model.param.CreateDocParam;
import com.yqhp.console.model.param.TreeNodeMoveEvent;
import com.yqhp.console.model.param.UpdateDocParam;
import com.yqhp.console.model.param.query.PkgTreeQuery;
import com.yqhp.console.repository.entity.Doc;
import com.yqhp.console.repository.enums.DocKind;
import com.yqhp.console.repository.enums.DocStatus;
import com.yqhp.console.repository.enums.PkgType;
import com.yqhp.console.repository.mapper.DocMapper;
import com.yqhp.console.web.common.ResourceFlags;
import com.yqhp.console.web.enums.ResponseCodeEnum;
import com.yqhp.console.web.service.DocService;
import com.yqhp.console.web.service.PkgService;
import com.yqhp.console.web.service.PlanDocService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jiangyitao
 */
@Slf4j
@Service
public class DocServiceImpl extends ServiceImpl<DocMapper, Doc>
        implements DocService {

    private static final List<DocStatus> AVAILABLE_STATUS = List.of(DocStatus.DEPRECATED, DocStatus.RELEASED);

    @Autowired
    private Snowflake snowflake;
    @Autowired
    private PlanDocService planDocService;
    @Lazy
    @Autowired
    private PkgService pkgService;

    @Override
    public Doc createDoc(CreateDocParam createDocParam) {
        Doc doc = createDocParam.convertTo();
        doc.setId(snowflake.nextIdStr());

        int maxWeight = getMaxWeightByProjectId(createDocParam.getProjectId());
        doc.setWeight(maxWeight + 1);

        String currUid = CurrentUser.id();
        doc.setCreateBy(currUid);
        doc.setUpdateBy(currUid);

        try {
            if (!save(doc)) {
                throw new ServiceException(ResponseCodeEnum.SAVE_DOC_FAIL);
            }
        } catch (DuplicateKeyException e) {
            throw new ServiceException(ResponseCodeEnum.DUPLICATE_DOC);
        }
        return getById(doc.getId());
    }

    @Override
    public Doc copy(String id) {
        Doc from = getDocById(id);
        CreateDocParam to = new CreateDocParam();
        BeanUtils.copyProperties(from, to);
        to.setName(from.getName() + "_" + System.currentTimeMillis());
        to.setFlags(null);
        return createDoc(to);
    }

    @Transactional
    @Override
    public Doc updateDoc(String id, UpdateDocParam updateDocParam) {
        Doc doc = getDocById(id);
        if (ResourceFlags.unupdatable(doc.getFlags())) {
            throw new ServiceException(ResponseCodeEnum.DOC_UNUPDATABLE);
        }
        boolean renamed = !doc.getName().equals(updateDocParam.getName());
        if (renamed && ResourceFlags.unrenamable(doc.getFlags())) {
            throw new ServiceException(ResponseCodeEnum.DOC_UNRENAMABLE);
        }

        boolean actionToInit = DocKind.JSH_ACTION.equals(doc.getKind())
                && DocKind.JSH_INIT.equals(updateDocParam.getKind());
        if (actionToInit) {
            planDocService.deleteByDocId(id);
        }

        updateDocParam.update(doc);
        update(doc);
        return getById(id);
    }

    @Override
    public void updateContent(String id, String content) {
        Doc doc = getDocById(id);
        if (ResourceFlags.unupdatable(doc.getFlags())) {
            throw new ServiceException(ResponseCodeEnum.DOC_UNUPDATABLE);
        }
        Doc toUpdate = new Doc();
        toUpdate.setId(id);
        toUpdate.setContent(content);
        update(toUpdate);
    }

    @Override
    @Transactional
    public void move(TreeNodeMoveEvent moveEvent) {
        Doc from = getDocById(moveEvent.getFrom());
        if (ResourceFlags.unmovable(from.getFlags())) {
            throw new ServiceException(ResponseCodeEnum.DOC_UNMOVABLE);
        }

        // 移动到某个文件夹内
        if (moveEvent.isInner()) {
            from.setPkgId(moveEvent.getTo());
            update(from);
            return;
        }

        String currUid = CurrentUser.id();
        Doc to = getDocById(moveEvent.getTo());

        Doc fromDoc = new Doc();
        fromDoc.setId(from.getId());
        fromDoc.setPkgId(to.getPkgId());
        fromDoc.setWeight(to.getWeight());
        fromDoc.setUpdateBy(currUid);

        List<Doc> toUpdateDocs = new ArrayList<>();
        toUpdateDocs.add(fromDoc);
        toUpdateDocs.addAll(
                listByProjectIdAndPkgIdAndWeightGeOrLe(
                        to.getProjectId(),
                        to.getPkgId(),
                        to.getWeight(),
                        moveEvent.isBefore()
                ).stream()
                        .filter(d -> !d.getId().equals(fromDoc.getId()))
                        .map(d -> {
                            Doc toUpdate = new Doc();
                            toUpdate.setId(d.getId());
                            toUpdate.setWeight(moveEvent.isBefore() ? d.getWeight() + 1 : d.getWeight() - 1);
                            toUpdate.setUpdateBy(currUid);
                            return toUpdate;
                        }).collect(Collectors.toList())
        );
        try {
            if (!updateBatchById(toUpdateDocs)) {
                throw new ServiceException(ResponseCodeEnum.UPDATE_DOC_FAIL);
            }
        } catch (DuplicateKeyException e) {
            throw new ServiceException(ResponseCodeEnum.DUPLICATE_DOC);
        }
    }

    private void update(Doc doc) {
        doc.setUpdateBy(CurrentUser.id());
        doc.setUpdateTime(LocalDateTime.now());
        try {
            if (!updateById(doc)) {
                throw new ServiceException(ResponseCodeEnum.UPDATE_DOC_FAIL);
            }
        } catch (DuplicateKeyException e) {
            throw new ServiceException(ResponseCodeEnum.DUPLICATE_DOC);
        }
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        Doc doc = getDocById(id);
        if (ResourceFlags.undeletable(doc.getFlags())) {
            throw new ServiceException(ResponseCodeEnum.DOC_UNDELETABLE);
        }
        planDocService.deleteByDocId(id);
        if (!removeById(id)) {
            throw new ServiceException(ResponseCodeEnum.DEL_DOC_FAIL);
        }
    }

    @Override
    public Doc getDocById(String id) {
        return Optional.ofNullable(getById(id))
                .orElseThrow(() -> new ServiceException(ResponseCodeEnum.DOC_NOT_FOUND));
    }

    @Override
    public List<Doc> listByProjectIdAndInPkgIds(String projectId, Collection<String> pkgIds) {
        if (CollectionUtils.isEmpty(pkgIds)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Doc> query = new LambdaQueryWrapper<>();
        query.eq(Doc::getProjectId, projectId);
        query.in(Doc::getPkgId, pkgIds);
        return list(query);
    }

    /**
     * doc的weight只能保证同一个目录有序
     * 这个方法返回pkgTree从上往下排序的doc
     */
    @Override
    public List<Doc> listPkgTreeSortedAndAvailableByProjectIdAndKind(String projectId, DocKind kind) {
        Assert.hasText(projectId, "projectId must has text");
        Assert.notNull(kind, "kind cannot be null");

        PkgTreeQuery treeQuery = new PkgTreeQuery();
        treeQuery.setProjectId(projectId);
        treeQuery.setType(PkgType.DOC);
        treeQuery.setParentId(PkgServiceImpl.ROOT_PID);
        treeQuery.setListItem(true);
        List<Tree<String>> pkgTree = pkgService.treeBy(treeQuery);
        List<String> sortedIds = new ArrayList<>();
        walk(pkgTree, sortedIds);

        return listByProjectId(projectId).stream()
                .filter(doc -> kind.equals(doc.getKind()))
                .filter(this::isAvailable)
                .sorted(Comparator.comparingInt(doc -> sortedIds.indexOf(doc.getId())))
                .collect(Collectors.toList());
    }

    private void walk(List<Tree<String>> trees, List<String> sortedIds) {
        if (CollectionUtils.isEmpty(trees)) {
            return;
        }
        for (Tree<String> tree : trees) {
            sortedIds.add(tree.getId());
            walk(tree.getChildren(), sortedIds);
        }
    }

    @Override
    public List<Doc> listInIds(Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return listByIds(ids);
    }

    @Override
    public List<Doc> listAvailableInIds(Collection<String> ids) {
        List<Doc> docs = listInIds(ids);
        return docs.stream().filter(this::isAvailable).collect(Collectors.toList());
    }

    private boolean isAvailable(Doc doc) {
        return AVAILABLE_STATUS.contains(doc.getStatus());
    }

    private List<Doc> listByProjectIdAndPkgIdAndWeightGeOrLe(String projectId, String pkgId, Integer weight, boolean ge) {
        List<Doc> docs = listByProjectIdAndPkgId(projectId, pkgId);
        return ge
                ? docs.stream().filter(doc -> doc.getWeight() >= weight).collect(Collectors.toList())
                : docs.stream().filter(doc -> doc.getWeight() <= weight).collect(Collectors.toList());
    }

    private List<Doc> listByProjectId(String projectId) {
        Assert.hasText(projectId, "projectId must has text");
        LambdaQueryWrapper<Doc> query = new LambdaQueryWrapper<>();
        query.eq(Doc::getProjectId, projectId);
        return list(query);
    }

    private List<Doc> listByProjectIdAndPkgId(String projectId, String pkgId) {
        Assert.hasText(projectId, "projectId must has text");
        Assert.hasText(pkgId, "pkgId must has text");
        LambdaQueryWrapper<Doc> query = new LambdaQueryWrapper<>();
        query.eq(Doc::getProjectId, projectId);
        query.eq(Doc::getPkgId, pkgId);
        return list(query);
    }

    private int getMaxWeightByProjectId(String projectId) {
        return listByProjectId(projectId).stream()
                .mapToInt(Doc::getWeight)
                .max().orElse(-1);
    }
}
