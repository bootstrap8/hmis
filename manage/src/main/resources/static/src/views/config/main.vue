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
          <el-sub-menu index="create">
            <template #title>
              <el-icon>
                <Plus/>
              </el-icon>
              新建
            </template>
            <el-menu-item index="createFolder" @click="folderDialogFormVisible = true;folderForm.folderName=''">
              <el-icon>
                <Folder/>
              </el-icon>
              目录
            </el-menu-item>
            <el-menu-item index="createProperty" @click="propertyDialogFormVisible = true;propertyForm.propertyName='';propertyForm.propertyValue=''">
              <el-icon>
                <Document/>
              </el-icon>
              属性
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item index="delete">
            <el-icon>
              <Delete/>
            </el-icon>
            <el-popconfirm title="你确定要删除选中目录下所有配置吗?"
                           @confirm="deleteSelected"
                           :icon="WarningFilled"
                           confirm-button-type="danger"
                           cancel-button-type="info"
                           icon-color="red">
              <template #reference>
                删除
              </template>
            </el-popconfirm>
          </el-menu-item>
          <el-menu-item index="export" @click="exportSelected">
            <el-icon>
              <Expand/>
            </el-icon>
            导出
          </el-menu-item>
          <el-menu-item index="import" @click="importDialogFormVisible=true;importForm.cover=true;importRef.clearFiles('success');">
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
          <el-scrollbar class="result-area" ref="asideScrollbar">
            <el-row class="tac">
              <el-col :span="24">
                <el-menu class="el-menu-vertical-demo">
                  <el-tree :props="props" :load="loadNode"
                           lazy
                           @node-click="clickNode"
                           @node-collapse="nodeCollapse"
                           :expand-on-click-node="false"
                           :render-after-expand="false"
                           :highlight-current="true"
                           class="tree"
                  />
                </el-menu>
              </el-col>
            </el-row>
          </el-scrollbar>
        </el-aside>
        <el-main class="main">
          <el-table :data="data.leaf" style="width: 100%"
                    :border="true"
                    table-layout="fixed"
                    :stripe="true"
                    size="small"
                    :highlight-current-row="true"
                    class="leaf">
            <el-table-column fixed="left" label="操作" width="120" header-align="center" align="center">
              <template #default="scope">
                <el-button link type="primary" size="small" @click="showEditDialog(scope)">编辑</el-button>
                <el-popconfirm title="你确定要删除本条配置?"
                               @confirm="deleteConfig(scope)"
                               :icon="WarningFilled"
                               confirm-button-type="danger"
                               cancel-button-type="info"
                               icon-color="red">
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
        <el-button type="primary" @click="updateConfig">
          保存
        </el-button>
      </span>
      </template>
    </el-dialog>

    <el-dialog v-model="folderDialogFormVisible" title="创建目录 (节点收起/展开可查看)">
      <el-form :model="folderForm">
        <el-form-item label="目录名：" :label-width="formLabelWidth">
          <el-input v-model="folderForm.folderName" type="text"/>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="folderDialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveFolder">
          保存
        </el-button>
      </span>
      </template>
    </el-dialog>

    <el-dialog v-model="propertyDialogFormVisible" title="创建属性">
      <el-form :model="propertyForm">
        <el-form-item label="属性名：" :label-width="formLabelWidth">
          <el-input v-model="propertyForm.propertyName" type="text"/>
        </el-form-item>
        <el-form-item label="属性值：" :label-width="formLabelWidth">
          <el-input v-model="propertyForm.propertyValue" type="textarea" rows="7"/>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="propertyDialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProperty">
          保存
        </el-button>
      </span>
      </template>
    </el-dialog>

    <el-dialog v-model="importDialogFormVisible" title="导入配置">
      <el-form :model="importForm">
        <el-upload
            class="upload-demo"
            :action="uploadAction"
            :data="{cover: importForm.cover}"
            :drag="true"
            multiple
            ref="importRef"
            :show-file-list="true"
            :limit="20"
            :auto-upload="false"
            :on-success="(res) => { return uploadSuc(res); }"
        >
          <el-icon class="el-icon--upload">
            <upload-filled/>
          </el-icon>
          <div class="el-upload__text">
            拖拽文件至此 或 <em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              尽量按照模块来导入配置，控制导入的文件大小
            </div>
          </template>
        </el-upload>
        <el-switch v-model="importForm.cover"
                   inline-prompt
                   active-text="覆盖原配置"
                   inactive-text="不覆盖原配置"/>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="importDialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="importFile">提交</el-button>
      </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
  import type Node from 'element-plus/es/components/tree/src/model/node'
  import {ArrowRight} from '@element-plus/icons-vue'
  import {
    Document,
    Menu as IconMenu,
    Location,
    Setting,
    House,
    Expand,
    Download,
    Search,
    Clock,
    Plus,
    Folder,
    Delete,
    WarningFilled,
    UploadFilled
  } from '@element-plus/icons-vue'
  import {UploadInstance, UploadFile, UploadFiles, UploadProgressEvent} from 'element-plus'
  import {ref, reactive, computed} from 'vue'
  import router from '@/router/index'
  import axios from '@/network/index'
  import {msg, downFile} from '@/utils/Utils'

  const activeIndex = ref('1')
  // 顶部菜单
  const handleSelect = (key: string, keyPath: string[]) => {
  }

  // 加载树数据结构
  interface Tree {
    zkPath: string[]
    name: string
    leaf?: boolean
  }

  // 节点属性名指定
  const props = {
    label: 'name',
    children: 'zones',
    isLeaf: 'leaf',
  }

  // 右侧配置属性列表
  const data = reactive({
    leaf: <any>[],
    // 当前zk路径
    currentPathArray: []
  })

  const joinPath = (array: Array) => {
    return '/' + (array.length == 0 ? '' : array.join('/'))
  }

  // 点击树上节点
  const clickNode = (tree: Tree) => {
    // {zkPath, name, leaf?}

    const array = Array.from(tree.zkPath)
    array.push(tree.name)
    const currentLoadPath = joinPath(array)
    data.currentPathArray = array

    const url = '/hmis/manage/config/queryNodes/v1.0'
    axios({
      url, method: 'post',
      data: {zkPath: currentLoadPath}
    }).then((res: any) => {
      if (res.data.code == 1) {
        data.leaf = res.data.body.leaf;
      }
    })
  }

  const loadNode = (node: Node, resolve: (data: Tree[]) => void) => {
    let array: any = []
    if (node.data.name) {
      array = Array.from(node.data.zkPath)
      array.push(node.data.name)
      data.currentPathArray = array
    }
    const currentLoadPath = joinPath(data.currentPathArray)

    const url = '/hmis/manage/config/queryNodes/v1.0'
    axios({
      url, method: 'post',
      data: {zkPath: currentLoadPath}
    }).then((res: any) => {
      if (res.data.code == 1) {
        resolve(res.data.body.tree.map((n: any): Tree => {
          return {zkPath: array, name: n.name, leaf: n.leaf};
        }))
        data.leaf = res.data.body.leaf;
      }
    })
  }

  const nodeCollapse = (obj: any, node: Node, self: any) => {
    node.loaded = false
    node.childNodes = []
  }

  const dialogFormVisible = ref(false)
  const formLabelWidth = ref('140px')
  const propForm = reactive({
    zkPath: '',
    name: '',
    value: ''
  })
  let currentRow: any;
  const showEditDialog = (scope: any) => {
    propForm.zkPath = joinPath(data.currentPathArray)
    currentRow = scope.row;
    propForm.name = scope.row.name
    propForm.value = scope.row.strValue
    dialogFormVisible.value = true
  }

  const updateConfig = () => {
    axios({
      url: '/hmis/manage/config/updateProperty/v1.0',
      method: 'post',
      data: propForm
    }).then((res: any) => {
      if (res.data.code == 1) {
        msg('更新成功', 'success')
        dialogFormVisible.value = false
        currentRow.strValue = propForm.value
      }
    })
  }

  const deleteConfig = (scope: any) => {
    console.log('', scope.row);
    const currentLoadPath = joinPath(data.currentPathArray)
    axios({
      url: '/hmis/manage/config/deleteLeaves/v1.0',
      method: 'post',
      data: {
        zkPath: currentLoadPath,
        leafNames: [scope.row.name]
      }
    }).then((res: any) => {
      if (res.data.code == 1) {
        msg('删除成功', 'success')
        data.leaf = data.leaf.filter((item: any) => item.name != scope.row.name)
      }
    })
  }

  const folderDialogFormVisible = ref(false)
  const folderForm = reactive({
    zkPath: '',
    folderName: ''
  })

  const saveFolder = () => {
    folderForm.zkPath = joinPath(data.currentPathArray)
    console.log('创建目录:', folderForm)
    axios({
      url: '/hmis/manage/config/saveFolder/v1.0',
      method: 'post',
      data: folderForm
    }).then((res: any) => {
      if (res.data.code == 1) {
        msg('创建成功', 'success')
        folderDialogFormVisible.value = false

      }
    })
  }

  const propertyDialogFormVisible = ref(false)
  const propertyForm = reactive({
    zkPath: '',
    propertyName: '',
    propertyValue: ''
  })
  const saveProperty = () => {
    propertyForm.zkPath = joinPath(data.currentPathArray)
    console.log('创建属性:', propertyForm)
    axios({
      url: '/hmis/manage/config/saveProperty/v1.0',
      method: 'post',
      data: propertyForm
    }).then((res: any) => {
      if (res.data.code == 1) {
        msg('创建成功', 'success')
        propertyDialogFormVisible.value = false
        data.leaf.push({
          name: propertyForm.propertyName,
          strValue: propertyForm.propertyValue
        })
      }
    })
  }

  const deleteSelected = () => {
    const currentLoadPath = joinPath(data.currentPathArray)
    const leafNames: any = []
    data.leaf.forEach((item: any) => leafNames.push(item.name))
    axios({
      url: '/hmis/manage/config/deleteLeaves/v1.0',
      method: 'post',
      data: {
        zkPath: currentLoadPath,
        leafNames: leafNames
      }
    }).then((res: any) => {
      if (res.data.code == 1) {
        msg('删除成功', 'success')
        data.leaf = []
      }
    })
  }

  const exportSelected = () => {
    const len = data.currentPathArray.length
    const folderName = (len == 0 ? 'all' : data.currentPathArray[len - 1])
    downFile({
      url: '/hmis/manage/config/export/v1.0',
      method: 'post',
      data: {zkPath: joinPath(data.currentPathArray)},
      responseType: 'blob',
      folderName,
      fileExtension: 'txt'
    })
  }

  const importDialogFormVisible = ref(false)
  const uploadAction = ref(('development' == process.env.NODE_ENV
      ? process.env.VUE_APP_DEV_BASE_URL
      : process.env.VUE_APP_PROD_BASE_URL)
      + '/hmis/manage/config/import/v1.0')
  const importRef = ref<UploadInstance>()
  const importForm = reactive({
    cover: true
  })
  const importFile = () => {
    importRef.value!.submit()
  }
  const uploadSuc = (res: any) => {
    msg('上传成功', 'success')
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
    --el-header-padding: 0;
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
    padding: 0px 0 0px 0px;
    height: 500px;
    overflow: hidden;
  }

  .el-menu-vertical-demo:not(.el-menu--collapse) {
    /* margin-top: 10px; */
    width: 100%;
    min-height: 600px;
    overflow: hidden;
  }

  .aside {
    width: 500px;
    margin-top: 0px;
    overflow: hidden;
  }

  .result-area {
    padding: 0px;
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    margin-top: 0px;
    overflow-y: auto;
  }

  .tree {
    padding: 15px 0 10px 10px;
  }

  .el-main {
    --el-main-padding: 0px 0px 0px 0px;
    overflow: hidden;
  }

  .leaf {
    height: 500px;
  }
</style>
