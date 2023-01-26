<template>
  <div class="appList">
    <el-table :data="data.instanceList" style="width: 100%"
              :border="true"
              table-layout="fixed"
              :stripe="true"
              size="small"
              :highlight-current-row="true">
      <el-table-column fixed="left" label="操作" width="120" header-align="center">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="showDialog(scope)">速率</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="dataCenter" label="数据中心" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="ip" label="ip地址" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="hostName" label="主机名" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="port" label="端口" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="processNo" label="进程号" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="tags" label="标签" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="fmtRegTime" label="注册时间" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="permits" label="速率" :show-overflow-tooltip="true" header-align="center"/>
    </el-table>
    <el-pagination class="page"
                   v-model:page-size="ruleForm.page.pageSize"
                   v-model:current-page="ruleForm.page.pageNum"
                   layout="->, total, sizes, prev, pager, next, jumper"
                   v-model:total="ruleForm.page.total"
                   @size-change="queryInstanceList"
                   @current-change="queryInstanceList"
                   @prev-click="queryInstanceList"
                   @next-click="queryInstanceList"
                   :small="true"
                   :background="true"
                   :page-sizes="[5, 10, 20, 50,100]"
    />

  </div>

  <el-dialog v-model="dialogFormVisible" title="配置实例kafka入口速率">
    <el-form :model="kafkaInRuleForm">
      <el-form-item label="速率(条/秒)：" :label-width="formLabelWidth">
        <el-input v-model="kafkaInRuleForm.permits" autocomplete="off" type="number"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveInstancePermits">
          确认
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
  import request from '@/network'
  import {ref, reactive, onMounted} from 'vue'
  import {Edit} from '@element-plus/icons-vue'
  import {vAlert, Page} from '@/utils/Utils'
  import router from '@/router/index'

  const dialogTableVisible = ref(false)
  const dialogFormVisible = ref(false)
  const formLabelWidth = '140px'

  const kafkaInRuleForm = reactive({
    data_center: '',
    app_name: '',
    ip: '',
    port: 8080,
    permits: 50000
  })

  const ruleForm = reactive({
    page: {
      pageNum: 1,
      pageSize: 10,
      total: 0
    }
  })

  const data = reactive({
    instanceList: []
  })

  const row = router.currentRoute.value.query;

  const queryInstanceList = (): void => {
    request({
      url: '/hmis/manage/agent/kafkaIn/queryInstanceList/v1.0',
      method: 'post',
      params: ruleForm.page,
      data: {
        appName: row.name
      }
    }).then((res: any) => {
      if (res.data.code == 1) {
        data.instanceList = res.data.body.list
      }
    }).catch((error: any) => {
      vAlert('操作结果', '请求异常', 'error')
    })
  }

  const showDialog = (scope: any) => {
    dialogFormVisible.value = true
    kafkaInRuleForm.data_center = scope.row.dataCenter
    kafkaInRuleForm.app_name = scope.row.appName
    kafkaInRuleForm.ip = scope.row.ip
    kafkaInRuleForm.port = scope.row.port
    kafkaInRuleForm.permits = scope.row.permits
  }

  const saveInstancePermits = () => {
    dialogFormVisible.value = false
    request({
      url: '/hmis/manage/agent/kafkaIn/saveInstancePermits/v1.0',
      method: 'post',
      data: kafkaInRuleForm
    }).then((res: any) => {
      if (res.data.code == 1) {
        vAlert('操作结果', '保存成功', 'success')
        queryInstanceList()
      }
    }).catch((e: any) => {
      console.error(e);
      vAlert('操作结果', '请求异常', 'error')
    })
  }

  onMounted(() => queryInstanceList());
</script>

<style scoped>

</style>
