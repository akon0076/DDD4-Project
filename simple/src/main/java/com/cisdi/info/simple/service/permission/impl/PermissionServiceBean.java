package com.cisdi.info.simple.service.permission.impl;

import com.cisdi.info.simple.dao.permission.PermissionDao;
import com.cisdi.info.simple.dto.base.PageDTO;
import com.cisdi.info.simple.dto.base.PageResultDTO;
import com.cisdi.info.simple.entity.permission.Module;
import com.cisdi.info.simple.entity.permission.Permission;
import com.cisdi.info.simple.service.base.BaseService;
import com.cisdi.info.simple.service.permission.PermissionService;
import com.cisdi.info.simple.util.D4Util;
import com.cisdi.info.simple.util.ModuleManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class PermissionServiceBean extends BaseService implements PermissionService {
    private static Map<String, Permission> permissionMap = new HashMap<>();

    private static Logger logger = LogManager.getLogger();

    @Autowired
    private PermissionDao permissionDao;

    ///**
    // * 根据分页参数查询权限点
    // *
    // * @param pageDTO 分页参数
    // * @return
    // */
    //@Override
    //public PageResultDTO findPermissions(PageDTO pageDTO) {
    //    // 读取json文件中的模块
    //    Collection<Module> moduleCollection = ModuleManager.getAllModules();
    //    Iterator iterator = moduleCollection.iterator();
    //
    //    List<Module> moduleList = new ArrayList<>();
    //    while (iterator.hasNext()) {
    //        moduleList.add((Module) iterator.next());
    //    }
    //
    //    List<Permission> permissions = new ArrayList<>();
    //    for (Module module : moduleList) {
    //        List<Permission> modulePermissions = module.getPermissions();
    //        for (int i = 0; i < modulePermissions.size(); i++) {
    //            Permission permission = modulePermissions.get(i);
    //            permissions.add(permission);
    //        }
    //    }
    //    int startIndex = (pageDTO.getCurrentPage() - 1) * pageDTO.getPageSize();
    //    int pageSize = pageDTO.getPageSize();
    //    int end = startIndex + pageSize;
    //    long totalCount = permissions.size();
    //    end = permissions.size() < end ? permissions.size() : end;
    //    List<Permission> permissionList = permissions.subList(startIndex, end);
    //
    //    PageResultDTO pageResultDTO = new PageResultDTO();
    //    pageResultDTO.setDatas(permissionList);
    //    pageResultDTO.setTotalCount(totalCount);
    //
    //    return pageResultDTO;
    //}

    @Override
    public PageResultDTO findPermissions(PageDTO pageDTO) {
        if (pageDTO.getColumnName() == null || "".equals(pageDTO.getColumnName()) || pageDTO.getContent() == null || "".equals(pageDTO.getContent())) {
            pageDTO.setStartIndex((pageDTO.getCurrentPage() - 1) * pageDTO.getPageSize());
            List list = new ArrayList(findAllPermissions());
            List returnList = list.subList(pageDTO.getStartIndex(), pageDTO.getStartIndex() + pageDTO.getPageSize() > list.size() ? list.size() : pageDTO.getStartIndex() + pageDTO.getPageSize());
            PageResultDTO pageResultDTO = new PageResultDTO();
            pageResultDTO.setTotalCount((long) list.size());
            pageResultDTO.setDatas(returnList);
            return pageResultDTO;
        } else if (pageDTO.getSql().contains("AND")) {
            String methodName = D4Util.getAttributerGetterName(pageDTO.getColumnName());
            List<Permission> list = new ArrayList(findAllPermissions());
            List<Permission> returnedList = new ArrayList<>();
            String[] anditems = pageDTO.getContent().split("\\s+and\\s");
            for (Permission module : list) {
                String result = D4Util.invokeMethodByString(module, methodName);
                if (result == null)
                    result = "";
                boolean mark = true;
                for (int i = 0; i < anditems.length; i++) {
                    if (!result.contains(anditems[i])) {
                        mark = false;
                        break;
                    }
                }
                if (mark) {
                    returnedList.add(module);
                }
            }
            pageDTO.setStartIndex((pageDTO.getCurrentPage() - 1) * pageDTO.getPageSize());
            PageResultDTO pageResultDTO = new PageResultDTO();
            pageResultDTO.setTotalCount((long) returnedList.size());
            pageResultDTO.setDatas(returnedList.subList(pageDTO.getStartIndex(), pageDTO.getStartIndex() + pageDTO.getPageSize() > returnedList.size() ? returnedList.size() : pageDTO.getStartIndex() + pageDTO.getPageSize()));
            return pageResultDTO;
        } else {
            String methodName = D4Util.getAttributerGetterName(pageDTO.getColumnName());
            List<Permission> list = new ArrayList(findAllPermissions());
            List<Permission> returnedList = new ArrayList<>();
            String[] items = pageDTO.getContent().split("\\s+");//or
            for (Permission module : list) {
                String result = D4Util.invokeMethodByString(module, methodName);
                if (result == null)
                    result = "";
                boolean mark = false;
                for (int i = 0; i < items.length; i++) {
                    if (result.contains(items[i])) {
                        mark = true;
                        break;
                    }
                }
                if (mark) {
                    returnedList.add(module);
                }
            }
            pageDTO.setStartIndex((pageDTO.getCurrentPage() - 1) * pageDTO.getPageSize());
            PageResultDTO pageResultDTO = new PageResultDTO();
            pageResultDTO.setTotalCount((long) returnedList.size());
            pageResultDTO.setDatas(returnedList.subList(pageDTO.getStartIndex(), pageDTO.getStartIndex() + pageDTO.getPageSize() > returnedList.size() ? returnedList.size() : pageDTO.getStartIndex() + pageDTO.getPageSize()));
            return pageResultDTO;
        }
    }

    @Override
    public List<Permission> findAllPermissions() {
        // 读取json文件中的模块
        Collection<Module> moduleCollection = ModuleManager.getAllModules();
        Iterator iterator = moduleCollection.iterator();

        List<Module> moduleList = new ArrayList<>();
        while (iterator.hasNext()) {
            moduleList.add((Module) iterator.next());
        }

        List<Permission> permissions = new ArrayList<>();
        for (Module module : moduleList) {
            List<Permission> modulePermissions = module.getPermissions();
            for (int i = 0; i < modulePermissions.size(); i++) {
                Permission permission = modulePermissions.get(i);
                permissions.add(permission);
            }
        }
        return permissions;
    }

    @Override
    public List<Permission> findAllPermissionsWithIdName() {
        return null;
    }

    @Override
    public Permission findPermission(String code) {
        loadPermissions();
        return permissionMap.get(code);
    }

    @Override
    public Permission findPermissionWithForeignName(String code) {
        loadPermissions();
        return permissionMap.get(code);
    }

    @Override
    public Permission savePermission(Permission permission) {
        ModuleManager.addPermission(permission);
        permissionMap.put(permission.getCode(), permission);
        return permission;
    }

    @Override
    public Permission updatePermission(Permission permission) {
        ModuleManager.updatePermission(permission);
        permissionMap.put(permission.getCode(), permission);
        return permission;
    }

    @Override
    public void deletePermission(String permissionCode) {
        loadPermissions();
        // 这里写删除权限点的
        Permission permission = permissionMap.get(permissionCode);
        if (ModuleManager.removePermission(permission)) {
            permissionMap.remove(permissionCode);
            logger.debug("删除权限点:" + permissionCode + "成功！");
        } else {
            logger.debug("删除权限点:" + permissionCode + "失败！");
        }
    }

    @Override
    public List<Permission> getAllPermissions() {
        List<Permission> permissions = new ArrayList<Permission>();
        for (Module module : ModuleManager.getAllModules()) {
            permissions.addAll(module.getPermissions());
        }
        return permissions;
    }

    /**
     * 获取权限点总数
     *
     * @return 权限点数量
     */
    @Override
    public Integer permissionCount() {
        return permissionMap.size();
    }

    /**
     * 加载所有权限
     */
    public void loadPermissions() {
        // 读取json文件中的模块
        Collection<Module> moduleCollection = ModuleManager.getAllModules();
        Iterator iterator = moduleCollection.iterator();

        List<Module> moduleList = new ArrayList<>();
        while (iterator.hasNext()) {
            moduleList.add((Module) iterator.next());
        }

        for (Module module : moduleList) {
            List<Permission> modulePermissions = module.getPermissions();
            for (int i = 0; i < modulePermissions.size(); i++) {
                Permission permission = modulePermissions.get(i);
                String code = modulePermissions.get(i).getCode();
                permissionMap.put(code, permission);
            }
        }

    }

}
