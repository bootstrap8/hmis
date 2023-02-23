package com.github.hbq.config.web;

import cn.hutool.core.lang.Assert;
import com.github.hbq.common.spring.boot.ctrl.Result;
import com.github.hbq.common.spring.boot.ctrl.Version;
import com.github.hbq.common.spring.context.UserInfo;
import com.github.hbq.config.pojo.Backup;
import com.github.hbq.config.pojo.HistoryOperate;
import com.github.hbq.config.pojo.LeafBean;
import com.github.hbq.config.serv.ConfigService;
import com.github.hbq.config.serv.DiscoveryAdapter;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hbq
 */
@RestController("config-web-ConfigCtrl")
@RequestMapping(path = "/ui")
@Slf4j
public class ConfigCtrl {

  @Autowired
  private ConfigService configService;

  @Autowired(required = false)
  private DiscoveryAdapter discoveryAdapter;

  @ApiOperation("查询节点树信息")
  @Version("v1.0")
  @RequestMapping(path = "/queryNodes/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> queryNodes(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("查询节点树信息: {}", map);
    try {
      return Result.suc(configService.queryNodes(UserInfo.of(userInfo), map));
    } catch (Exception e) {
      log.error("查询节点树信息异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("查询节点树信息异常");
    }
  }

  @ApiOperation("更新配置")
  @Version("v1.0")
  @RequestMapping(path = "/updateProperty/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> updateProperty(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("更新配置: {}", map);
    try {
      configService.setPropertyValue(UserInfo.of(userInfo), map);
      return Result.suc("更新成功");
    } catch (Exception e) {
      log.error("更新配置异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("更新配置异常");
    }
  }

  @ApiOperation("删除属性")
  @Version("v1.0")
  @RequestMapping(path = "/deleteLeaves/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> deleteLeaves(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("删除属性: {}", map);
    try {
      configService.deleteLeaves(UserInfo.of(userInfo), map);
      return Result.suc("删除成功");
    } catch (Exception e) {
      log.error("删除属性异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("删除属性异常");
    }
  }

  @ApiOperation("创建目录节点")
  @Version("v1.0")
  @RequestMapping(path = "/createFolder/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> createFolder(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("创建目录节点: {}", map);
    try {
      configService.createFolder(UserInfo.of(userInfo), map);
      return Result.suc("创建成功");
    } catch (Exception e) {
      log.error("创建目录节点异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("创建目录节点异常");
    }
  }

  @ApiOperation("新增配置")
  @Version("v1.0")
  @RequestMapping(path = "/createProperty/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> createProperty(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("新增配置: {}", map);
    try {
      configService.createNode(UserInfo.of(userInfo), map);
      return Result.suc("保存成功");
    } catch (Exception e) {
      log.error("新增配置异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("新增配置异常");
    }
  }

  @ApiOperation("删除选中的目录和配置")
  @Version("v1.0")
  @RequestMapping(path = "/delete/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> delete(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("删除选中的目录和配置: {}", map);
    try {
      configService.delete(UserInfo.of(userInfo), map);
      return Result.suc("删除成功");
    } catch (Exception e) {
      log.error("删除选中的目录和配置异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("删除选中的目录和配置异常");
    }
  }

  @ApiOperation("创建目录")
  @Version("v1.0")
  @RequestMapping(path = "/saveFolder/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> saveFolder(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("创建目录: {}", map);
    try {
      configService.createFolder(UserInfo.of(userInfo), map);
      return Result.suc("创建成功");
    } catch (Exception e) {
      log.error("创建目录异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("创建目录异常");
    }
  }

  @ApiOperation("创建配置")
  @Version("v1.0")
  @RequestMapping(path = "/saveProperty/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> saveProperty(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("创建配置: {}", map);
    try {
      configService.createNode(UserInfo.of(userInfo), map);
      return Result.suc("创建成功");
    } catch (Exception e) {
      log.error("创建配置异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("创建配置异常");
    }
  }

  @ApiOperation("导出配置")
  @Version("v1.0")
  @RequestMapping(path = "/export/{v}", method = RequestMethod.POST)
  @ResponseBody
  public void export(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      HttpServletResponse response,
      @RequestBody Map map) {
    log.info("导出配置: {}", map);
    try {
      Set<LeafBean> leaves = configService.exportTree(UserInfo.of(userInfo), map);
      StringBuilder output = new StringBuilder(2000);
      for (LeafBean leaf : leaves) {
        output.append(leaf.getPath()).append('=')
            .append(leaf.getName()).append('=')
            .append(leaf.getStrValue()).append('\n');
      }
      response.setContentType("text/plain;charset=UTF-8");
      try (PrintWriter out = response.getWriter()) {
        out.write(output.toString());
        out.flush();
      }
    } catch (Exception e) {
      log.error("导出配置异常", e);
    }
  }

  @ApiOperation("导入txt配置文件")
  @Version("v1.0")
  @RequestMapping(path = "/import/{v}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseBody
  public Result<?> importFile(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam("file") MultipartFile file, @RequestParam("cover") boolean cover) {
    try {
      log.info("导入文件:{}, 大小:{}, 是否覆盖:{}", file.getOriginalFilename(), file.getSize(), cover);
      configService.importData(UserInfo.of(userInfo), file, cover);
      return Result.suc("导入成功");
    } catch (Exception e) {
      log.error("导入配置异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("导入配置异常");
    }
  }

  @ApiOperation("导入properties文件")
  @Version("v1.0")
  @RequestMapping(path = "/propImport/{v}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseBody
  public Result<?> propImport(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam("bootstrapFile") MultipartFile bootstrapFile,
      @RequestParam("defaultFile") MultipartFile defaultFile,
      @RequestParam("profilesFile") MultipartFile profilesFile,
      @RequestParam("cover") boolean cover) {
    try {
      log.info("导入properties文件: bootstrapFile:{}, defaultFile: {}, profilesFile: {}, cover: {}",
          bootstrapFile.getOriginalFilename(), defaultFile.getOriginalFilename(), profilesFile.getOriginalFilename(), cover);
      configService.propFileImport(bootstrapFile, defaultFile, profilesFile, cover);
      return Result.suc("导入成功");
    } catch (Exception e) {
      log.error("导入properties文件异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("导入properties文件异常");
    }
  }

  @ApiOperation("导入yaml文件")
  @Version("v1.0")
  @RequestMapping(path = "/yamlImport/{v}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseBody
  public Result<?> yamlImport(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam("bootstrapFile") MultipartFile bootstrapFile,
      @RequestParam("defaultFile") MultipartFile defaultFile,
      @RequestParam("profilesFile") MultipartFile profilesFile,
      @RequestParam("cover") boolean cover) {
    try {
      log.info("导入yaml文件: bootstrapFile:{}, defaultFile: {}, profilesFile: {}, cover: {}",
          bootstrapFile.getOriginalFilename(), defaultFile.getOriginalFilename(), profilesFile.getOriginalFilename(), cover);
      configService.yamlFileImport(bootstrapFile, defaultFile, profilesFile, cover);
      return Result.suc("导入成功");
    } catch (Exception e) {
      log.error("导入yaml文件异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("导入yaml文件异常");
    }
  }

  @ApiOperation("查询配置列表")
  @Version("v1.0")
  @RequestMapping(path = "/queryAllProperty/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> queryAllProperty(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("查询配置列表: {}", map);
    try {
      return Result.suc(configService.searchTree(UserInfo.of(userInfo), map));
    } catch (Exception e) {
      log.error("查询配置列表异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("查询配置列表异常");
    }
  }

  @ApiOperation("查询历史记录")
  @Version("v1.0")
  @RequestMapping(path = "/queryHistoryOperates/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<PageInfo> queryHistoryOperates(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
      @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
      @RequestBody Map map) {
    log.info("查询历史记录: {}, ({},{})", map, pageNum, pageSize);
    try {
      Page page = PageHelper.startPage(pageNum, pageSize);
      PageInfo<HistoryOperate> pageInfo = new PageInfo<>(configService.queryHistoryOperates(map, pageNum, pageSize));
      pageInfo.setTotal(page.getTotal());
      return Result.suc(pageInfo);
    } catch (Exception e) {
      log.error("查询历史记录异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("查询历史记录异常");
    }
  }

  @ApiOperation("手工备份")
  @Version("v1.0")
  @RequestMapping(path = "/backup/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> backup(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v) {
    log.info("执行手工备份.");
    try {
      configService.backup();
      return Result.suc("备份成功");
    } catch (Exception e) {
      log.error("手工备份异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("手工备份异常");
    }
  }

  @ApiOperation("查询备份记录")
  @Version("v1.0")
  @RequestMapping(path = "/queryBackups/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> queryBackups(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
      @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
      @RequestBody Map map) {
    log.info("查询备份记录: {}, ({},{})", map, pageNum, pageSize);
    try {
      Page page = PageHelper.startPage(pageNum, pageSize);
      PageInfo<Backup> pageInfo = new PageInfo<>(configService.queryBackups(map, pageNum, pageSize));
      pageInfo.setTotal(page.getTotal());
      return Result.suc(pageInfo);
    } catch (Exception e) {
      log.error("查询备份记录异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("查询备份记录异常");
    }
  }

  @ApiOperation("删除备份记录")
  @Version("v1.0")
  @RequestMapping(path = "/deleteBackup/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> deleteBackup(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("删除备份记录: {}", map);
    try {
      configService.deleteBackupById(map);
      return Result.suc("删除成功");
    } catch (Exception e) {
      log.error("删除备份记录异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("删除备份记录异常");
    }
  }

  @ApiOperation("恢复备份数据")
  @Version("v1.0")
  @RequestMapping(path = "/recovery/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> recovery(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("恢复备份数据: {}", map);
    try {
      configService.recovery(map);
      return Result.suc("恢复成功");
    } catch (Exception e) {
      log.error("恢复备份数据异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("恢复备份数据异常");
    }
  }

  @ApiOperation("查询应用列表")
  @Version("v1.0")
  @RequestMapping(path = "/queryAppInfos/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> queryAppInfos(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("查询应用列表: {}", map);
    try {
      Assert.notNull(discoveryAdapter,"此功能需要开启服务发现");
      return Result.suc(discoveryAdapter.queryAppInfos(map));
    } catch (Exception e) {
      log.error("查询应用列表异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("查询应用列表异常");
    }
  }

  @ApiOperation("刷新应用配置")
  @Version("v1.0")
  @RequestMapping(path = "/refreshConfig/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> refreshConfig(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("刷新应用配置: {}", map);
    try {
      Assert.notNull(discoveryAdapter,"此功能需要开启服务发现");
      discoveryAdapter.refreshConfig(map);
      return Result.suc("刷新成功");
    } catch (Exception e) {
      log.error("刷新应用配置异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("刷新应用配置异常");
    }
  }
}
