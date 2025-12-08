package com.tancong.core.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tancong.core.entity.Menu;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * ===================================
 * 菜单数据扩展传输层--主要前端展示/后端传输
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/30
 */
@Data
@Accessors(chain = true)  // ✅ 添加链式调用，与父类保持一致
public class MenuDTO extends Menu {

    private boolean hasChildren = true;
    @JsonIgnore
    private Menu parent; // 父菜单 未使用

    private List<MenuDTO> children = new ArrayList<>(); // 子菜单

    public MenuDTO() {}

    public boolean isHasChildren() {
        return hasChildren;
    }

    /**
     * 获取父菜单
     * @return
     */
    public Menu getParent() {
        return parent;
    }

    /**
     * 设置父菜单
     * @param parent
     * @return
     */
    public MenuDTO setParent(Menu parent) {
        this.parent = parent;
        return this;
    }

    /**
     * 获取子菜单
     * @return
     */
    public List<MenuDTO> getChildren() {
        return children;
    }

    /**
     * 设置子菜单
     * @param children
     * @return
     */
    public MenuDTO setChildren(List<MenuDTO> children) {
        this.children = (children == null) ? new ArrayList<>() : children;
        // 设置是否包含子菜单
        this.hasChildren = !children.isEmpty();
        return this;
    }
}
