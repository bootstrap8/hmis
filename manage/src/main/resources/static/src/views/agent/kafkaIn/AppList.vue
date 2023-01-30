<template>
  <div class="appList">
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item label="查询类型" prop="appSelect">
        <el-select v-model="ruleForm.appSelect" class="m-2" placeholder="请选择" size="large">
          <el-option
              v-for="item in data.appSelectMeta"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="应用关键字" prop="appKey">
        <el-input v-model="ruleForm.appKey" placeholder="请输入关键字..."/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="queryAppList">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="data.appList" style="width: 100%"
              :border="true"
              table-layout="fixed"
              :stripe="true"
              size="small"
              :highlight-current-row="true">
      <el-table-column fixed="left" label="操作" width="120" header-align="center" align="center">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="queryInstances(scope)">实例</el-button>
          <el-button link type="primary" size="small" @click="showDialog(scope)">速率</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="应用名" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="desc" label="应用描述" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="tags" label="应用标签" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="fmtRegTime" label="注册时间" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="instanceSize" label="实例数量" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="permits" label="速率(条/秒)" :show-overflow-tooltip="true" header-align="center"/>
    </el-table>
    <el-pagination class="page"
                   v-model:page-size="ruleForm.page.pageSize"
                   v-model:current-page="ruleForm.page.pageNum"
                   layout="->, total, sizes, prev, pager, next, jumper"
                   v-model:total="ruleForm.page.total"
                   @size-change="queryAppList"
                   @current-change="queryAppList"
                   @prev-click="queryAppList"
                   @next-click="queryAppList"
                   :small="true"
                   :background="true"
                   :page-sizes="[5, 10, 20, 50,100]"
    />

  </div>

  <el-dialog v-model="dialogFormVisible" title="配置应用kafka入口速率">
    <el-form :model="kafkaInRuleForm">
      <el-form-item label="速率(条/秒)：" :label-width="formLabelWidth">
        <el-input v-model="kafkaInRuleForm.permits" autocomplete="off" type="number"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAppPermits">
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
    app_name: '',
    permits: 50000
  })

  const ruleForm = reactive({
    appSelect: 'name',
    appKey: '',
    page: {
      pageNum: 1,
      pageSize: 10,
      total: 0
    }
  })

  const data = reactive({
    appSelectMeta: [{value: 'name', label: '应用名称'}, {value: 'desc', label: '应用描述'}],
    appList: []
  })

  const queryAppList = (): void => {
    request({
      url: '/hmis/manage/agent/kafkaIn/queryAppList/v1.0',
      method: 'post',
      params: ruleForm.page,
      data: {
        appSelect: ruleForm.appSelect,
        appKey: ruleForm.appKey
      }
    }).then((res: any) => {
      if (res.data.code == 1) {
        data.appList = res.data.body.list;
      }
    }).catch((error: any) => {
      vAlert('操作结果', '请求异常', 'error')
    })
  }

  const queryInstances = (scope: any) => {
    router.push({
      path: '/agent/kafkaIn/instanceList',
      query: scope.row
    })
  }

  const showDialog = (scope: any) => {
    dialogFormVisible.value = true
    kafkaInRuleForm.permits = scope.row.permits;
    kafkaInRuleForm.app_name = scope.row.name;
  }

  const saveAppPermits = () => {
    dialogFormVisible.value = false
    request({
      url: '/hmis/manage/agent/kafkaIn/saveAppPermits/v1.0',
      method: 'post',
      data: {
        app_name: kafkaInRuleForm.app_name,
        permits: kafkaInRuleForm.permits
      }
    }).then((res: any) => {
      if (res.data.code == 1) {
        vAlert('操作结果', '保存成功', 'success')
        queryAppList();
      }
    }).catch((e: any) => {
      console.error(e);
      vAlert('操作结果', '请求异常', 'error')
    })
  }

  onMounted(() => queryAppList());
</script>

<style scoped>

</style>
