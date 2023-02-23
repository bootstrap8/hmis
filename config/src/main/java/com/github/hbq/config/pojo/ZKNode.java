package com.github.hbq.config.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ZKNode {

  List<String> nodeLst;
  List<LeafBean> leafBeanLSt;

  public ZKNode() {
    nodeLst = new ArrayList<>();
    leafBeanLSt = new ArrayList<>();
  }

  public List<Tree> createTree() {
    List<Tree> trees = new ArrayList<>(nodeLst.size());
    Optional.ofNullable(nodeLst).ifPresent(nl -> {
      for (String name : nl) {
        trees.add(new Tree(name));
      }
    });
    return trees;
  }

  public List<String> getNodeLst() {
    return nodeLst;
  }

  public void setNodeLst(List<String> nodeLst) {
    this.nodeLst = nodeLst;
  }

  public List<LeafBean> getLeafBeanLSt() {
    return leafBeanLSt;
  }

  public void setLeafBeanLSt(List<LeafBean> leafBeanLSt) {
    this.leafBeanLSt = leafBeanLSt;
  }

}
