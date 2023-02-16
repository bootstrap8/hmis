<template>
  <div class="common-layout">
    <el-container>
      <el-header>
        <el-menu
            class="el-menu-demo"
            mode="horizontal"
            :ellipsis="false"
            @select="handleSelect"
        >
          <el-menu-item index="0">
            配置中心
          </el-menu-item>
          <div class="flex-grow"/>
          <el-menu-item index="home">
            <el-icon>
              <House/>
            </el-icon>
            主页
          </el-menu-item>
          <el-menu-item index="export">
            <el-icon>
              <Expand/>
            </el-icon>
            导出
          </el-menu-item>
          <el-menu-item index="import">
            <el-icon>
              <Download/>
            </el-icon>
            导入
          </el-menu-item>
          <el-menu-item index="search">
            <el-icon>
              <Search/>
            </el-icon>
            查询
          </el-menu-item>
          <el-menu-item index="history">
            <el-icon>
              <Clock/>
            </el-icon>
            历史
          </el-menu-item>
        </el-menu>
      </el-header>
      <el-container class="container">
        <el-aside class="aside">
          <el-scrollbar class="result-area" ref="myScrollbar">
            <el-row class="tac">
              <el-col :span="24">
                <el-menu class="el-menu-vertical-demo">
                  <el-tree :props="props" :load="loadNode"
                           lazy show-checkbox class="tree"
                           @node-click="clickNode"
                           :expand-on-click-node="false"
                           :highlight-current="true"/>
                </el-menu>
              </el-col>
            </el-row>
          </el-scrollbar>
        </el-aside>
        <el-main class="main">
          <el-breadcrumb separator="" v-if="ruleForm.paths.length==0">
            <el-breadcrumb-item>/</el-breadcrumb-item>
          </el-breadcrumb>
          <el-breadcrumb separator="/" v-else>
            <el-breadcrumb-item v-for="(item,index) in ruleForm.paths">{{item}}</el-breadcrumb-item>
          </el-breadcrumb>
          <el-table :data="data.leaf" style="width: 100%"
                    :border="true"
                    table-layout="fixed"
                    :stripe="true"
                    size="small"
                    :highlight-current-row="true"
                    class="leaf">
            <el-table-column fixed="left" label="操作" width="120" header-align="center" align="center">
              <template #default="scope">
                <el-button link type="primary" size="small" @click="editConfig(scope)">编辑</el-button>
                <el-popconfirm title="你确定要删除本条配置?" @confirm="deleteConfig(scope)">
                  <template #reference>
                    <el-button link type="danger" size="small">删除</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="属性名称" :show-overflow-tooltip="true" header-align="center" align="center"/>
            <el-table-column prop="strValue" label="属性值" :show-overflow-tooltip="true" header-align="center" align="left"/>
          </el-table>
        </el-main>
      </el-container>
    </el-container>

    <el-dialog v-model="dialogFormVisible" title="编辑属性">
      <el-form :model="propForm">
        <el-form-item label="属性名：" :label-width="formLabelWidth">
          <el-input v-model="propForm.name" type="text" disabled/>
        </el-form-item>
        <el-form-item label="属性值：" :label-width="formLabelWidth">
          <el-input v-model="propForm.value" type="textarea" :rows="7"/>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="updateProp">
          保存
        </el-button>
      </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
  import type Node from 'element-plus/es/components/tree/src/model/node'
  import {ArrowRight} from '@element-plus/icons-vue'
  import {Document, Menu as IconMenu, Location, Setting, House, Expand, Download, Search, Clock} from '@element-plus/icons-vue'
  import {ref, reactive} from 'vue'
  import router from '@/router/index'
  import axios from '@/network/index'
  import {msg} from '@/utils/Utils'

  const activeIndex = ref('1')
  const handleSelect = (key: string, keyPath: string[]) => {
    if ('home' === key) {
      router.push({
        path: '/zkui/home'
      })
    }
  }

  interface Tree {
    zkPath: string[];
    name: string
    leaf?: boolean
  }

  const props = {
    label: 'name',
    children: 'zones',
    isLeaf: 'leaf',
  }

  const ruleForm = reactive({
    paths: <any>[]
  })

  const data = reactive({
    leaf: <any>[]
  })

  const clickNode = (tree: Tree) => {
    let zkPath: any[] = []
    if (tree.zkPath) {
      if (tree.zkPath.length > 0) {
        tree.zkPath.forEach((p: string) => zkPath.push(p))
      }
      zkPath.push(tree.name)
    }
    ruleForm.paths = zkPath
    const url = '/hmis/manage/config/queryNodes/v1.0'
    axios({
      url, method: 'post',
      data: {zkPath: ('/' + (zkPath.length == 0 ? '' : zkPath.join('/')))}
    }).then((res: any) => {
      if (res.data.code == 1) {
        data.leaf.length = 0
        res.data.body.leaf.forEach((l: any) => {
          data.leaf.push(l)
        })
      }
    })
  }

  const loadNode = (node: Node, resolve: (data: Tree[]) => void) => {
    let zkPath: any[] = []
    if (node.data.zkPath) {
      if (node.data.zkPath.length > 0) {
        node.data.zkPath.forEach((p: string) => zkPath.push(p))
      }
      zkPath.push(node.data.name)
    }
    ruleForm.paths = zkPath
    const url = '/hmis/manage/config/queryNodes/v1.0'
    axios({
      url, method: 'post',
      data: {zkPath: ('/' + (zkPath.length == 0 ? '' : zkPath.join('/')))}
    }).then((res: any) => {
      if (res.data.code == 1) {
        resolve(res.data.body.tree.map((n: any): Tree => {
          return {zkPath: zkPath, name: n.name, leaf: n.leaf};
        }))
        data.leaf.length = 0
        res.data.body.leaf.forEach((l: any) => {
          data.leaf.push(l)
        })
      }
    })
  }

  const dialogFormVisible = ref(false)
  const formLabelWidth = ref('140px')
  const propForm = reactive({
    name: '',
    value: ''
  })
  const editConfig = (scope: any) => {
    dialogFormVisible.value = true
    propForm.name = scope.row.name
    propForm.value = scope.row.strValue
  }

  const deleteConfig = (scope: any) => {
    msg('删除成功', 'success')
  }
</script>

<style scoped>
  .flex-grow {
    flex-grow: 1;
  }

  .logo {
    background-repeat: no-repeat;
    width: 50px;
    height: 50px;
  }

  .el-header {
    background-color: transparent;
    display: block;
    justify-content: space-between;
    padding-left: 0;
    align-items: center;
    color: #fff;
    font-size: 20px;
  }

  .container {
    height: 500px;
    overflow: hidden;
  }

  .main {
    padding: 15px 0 10px 10px;
    height: 600px;
  }

  .el-menu-vertical-demo:not(.el-menu--collapse) {
    /* margin-top: 10px; */
    width: 100%;
    min-height: 600px;
  }

  .aside {
    width: 500px;
    margin-top: 0px;
    overflow: hidden;
  }

  .result-area {
    padding: 0px;
    width: 500px;
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    margin-top: 0px;
    overflow-y: auto;
  }

  .tree {
    padding: 15px 0 10px 10px;
  }

  .el-main {
    --el-main-padding: 0px 0px 0px 15px;
  }

  .leaf {
    margin-top: 20px;
  }
</style>
