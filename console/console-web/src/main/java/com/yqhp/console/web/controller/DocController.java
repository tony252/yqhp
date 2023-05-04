package com.yqhp.console.web.controller;

import com.yqhp.console.model.param.CreateDocParam;
import com.yqhp.console.model.param.TreeNodeMoveEvent;
import com.yqhp.console.model.param.UpdateDocParam;
import com.yqhp.console.repository.entity.Doc;
import com.yqhp.console.repository.enums.DocKind;
import com.yqhp.console.web.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author jiangyitao
 */
@Validated
@RestController
@RequestMapping("/doc")
public class DocController {

    @Autowired
    private DocService docService;

    @GetMapping("/{id}")
    public Doc getDocById(@PathVariable String id) {
        return docService.getDocById(id);
    }

    @GetMapping("/init")
    public List<Doc> listInit(@NotBlank(message = "projectId不能为空") String projectId) {
        return docService.listPkgTreeSortedAndAvailableByProjectIdAndKind(projectId, DocKind.JSH_INIT);
    }

    @PostMapping
    public Doc createDoc(@RequestBody @Valid CreateDocParam createDocParam) {
        return docService.createDoc(createDocParam);
    }

    @GetMapping("/{id}/copy")
    public Doc copy(@PathVariable String id) {
        return docService.copy(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        docService.deleteById(id);
    }

    @PutMapping("/{id}")
    public Doc updateDoc(@PathVariable String id, @Valid @RequestBody UpdateDocParam updateDocParam) {
        return docService.updateDoc(id, updateDocParam);
    }

    @PutMapping("/{id}/content")
    public void updateContent(@PathVariable String id, @NotNull(message = "content不能为null") String content) {
        docService.updateContent(id, content);
    }

    @PutMapping("/move")
    public void move(@Valid @RequestBody TreeNodeMoveEvent moveEvent) {
        docService.move(moveEvent);
    }
}
