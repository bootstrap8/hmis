package com.github.hbq.manage.config.web;

import com.github.hbq.common.spring.boot.ctrl.Result;
import com.github.hbq.common.spring.boot.ctrl.Version;
import com.github.hbq.manage.config.pojo.LeafBean;
import com.github.hbq.manage.config.serv.NodeService;
import com.github.hbq.manage.config.serv.ZookeeperService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
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
@RestController("zkui-web-NodeCtrl")
@RequestMapping(path = "/config")
@Slf4j
public class NodeCtrl {

  @Autowired
  private ZookeeperService zookeeperService;

  @Autowired
  private NodeService nodeService;

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
      return Result.suc(nodeService.queryNodes(map));
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
      nodeService.setPropertyValue(map);
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
      nodeService.deleteLeaves(map);
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
      nodeService.createFolder(map);
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
      nodeService.createNode(map);
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
      nodeService.delete(map);
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
      nodeService.createFolder(map);
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
      nodeService.createNode(map);
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
      Set<LeafBean> leaves = nodeService.exportTree(map);
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

  @ApiOperation("导入配置")
  @Version("v1.0")
  @RequestMapping(path = "/import/{v}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseBody
  public Result<?> importFile(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam("file") MultipartFile file, @RequestParam("cover") boolean cover) {
    try {
      log.info("导入文件:{}, 大小:{}, 是否覆盖:{}", file.getOriginalFilename(), file.getSize(), cover);
      nodeService.importData(file, cover);
      return Result.suc("导入成功");
    } catch (Exception e) {
      log.error("导入配置异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("导入配置异常");
    }
  }
}
