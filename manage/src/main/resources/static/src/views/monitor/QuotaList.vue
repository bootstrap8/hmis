<template>
  <el-form :inline="true" class="demo-form-inline">
    <el-form-item label="应用名称" prop="appName">
      <el-input v-model="ruleForm.appName" placeholder="请输入关键字..."/>
    </el-form-item>
    <el-form-item label="指标名称" prop="quotaName">
      <el-input v-model="ruleForm.quotaName" placeholder="请输入关键字..."/>
    </el-form-item>
    <el-form-item label="指标描述" prop="quotaDesc">
      <el-input v-model="ruleForm.quotaDesc" placeholder="请输入关键字..."/>
    </el-form-item>
    <el-form-item label="指标类型" prop="quotaType">
      <el-select v-model="ruleForm.quotaType" class="m-2" placeholder="请选择" size="large">
        <el-option v-for="(item,index) in data.quotaTypes" :key="item.value" :label="item.label" :value="item.value"/>
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submitForm">查询</el-button>
    </el-form-item>
  </el-form>
  <el-table :data="result.data" style="width: 100%"
            :border="true"
            table-layout="fixed"
            size="small"
            :row-class-name="tableRowClassName">
    <el-table-column fixed="left" label="操作" width="150" header-align="center" align="center">
      <template #default="scope">
        <el-button link type="primary" size="small" @click="showThresholdDialog(scope)">阈值</el-button>
        <el-button link type="primary" size="small" @click="showInstance(scope)">实例</el-button>
        <el-button link type="primary" size="small" @click="showTrend(scope)">趋势</el-button>
      </template>
    </el-table-column>
    <el-table-column prop="hasThreshold" label="是否配置阈值" :show-overflow-tooltip="true" header-align="center"/>
    <el-table-column prop="appName" label="应用名称" :show-overflow-tooltip="true" header-align="center"/>
    <el-table-column prop="quotaName" label="指标名称" :show-overflow-tooltip="true" header-align="center"/>
    <el-table-column prop="quotaDesc" label="指标描述" :show-overflow-tooltip="true" header-align="center"/>
    <el-table-column prop="quotaUnit" label="指标单位" :show-overflow-tooltip="true" header-align="center"/>
    <el-table-column prop="quotaType" label="指标类型" :show-overflow-tooltip="true" header-align="center"/>
    <el-table-column prop="cycleTime" label="采集周期" :show-overflow-tooltip="true" header-align="center"/>
    <el-table-column prop="cycleUnit" label="周期单位" :show-overflow-tooltip="true" header-align="center"/>
  </el-table>
  <el-pagination class="page"
                 v-model:page-size="ruleForm.page.pageSize"
                 v-model:current-page="ruleForm.page.pageNum"
                 layout="->, total, sizes, prev, pager, next, jumper"
                 v-model:total="ruleForm.page.total"
                 @size-change="submitForm"
                 @current-change="submitForm"
                 @prev-click="submitForm"
                 @next-click="submitForm"
                 :small="true"
                 :background="true"
                 :page-sizes="[5, 10, 20, 50,100]"
  />

  <el-dialog v-model="dialogFormVisible" :title="trendForm.quotaName" destroy-on-close :fullscreen="fullscreen" top="5vh">
    <el-form :model="trendForm">
      <el-radio-group v-model="trendForm.timeType" @change="changeTimeType">
        <el-radio-button label="1" size="small" border>最近1小时</el-radio-button>
        <el-radio-button label="3" size="small" border>最近3小时</el-radio-button>
        <el-radio-button label="6" size="small" border>最近6小时</el-radio-button>
        <el-radio-button label="12" size="small" border>最近12小时</el-radio-button>
        <el-radio-button label="24" size="small" border>最近24小时</el-radio-button>
        <el-radio-button label="custom" size="small" border>自定义</el-radio-button>
      </el-radio-group>
      <div class="time">
        <el-date-picker v-if="showTimeComponent"
                        v-model="trendForm.time"
                        type="datetimerange"
                        range-separator="至"
                        start-placeholder="开始时间"
                        end-placeholder="结束时间"
                        size="small"
        />
      </div>
    </el-form>
    <div v-for="(item,index) in instances" :id="item.ip +':'+ item.port" :class="trendClass"></div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="queryQuotaData" type="primary">查询</el-button>
        <el-button @click="fullscreenHandle" type="success">{{fullscreenTxt}}</el-button>
        <el-button @click="dialogFormVisible = false" type="info">取消</el-button>
      </span>
    </template>
  </el-dialog>

  <el-dialog v-model="thresholdDialogFormVisible" title="趋势图" destroy-on-close top="5vh">
    <el-form :model="thresholdForm" label-width="120px">
      <el-form-item label="应用名称：">
        <el-input v-model="thresholdForm.appName" disabled/>
      </el-form-item>
      <el-form-item label="指标名称：">
        <el-input v-model="thresholdForm.quotaName" disabled/>
      </el-form-item>
      <el-form-item label="检查次数：">
        <el-input v-model="thresholdForm.checkCount" type="number"/>
      </el-form-item>
      <el-form-item label="警告超时(秒)：">
        <el-input v-model="thresholdForm.notifyTimeoutSec" type="number"/>
      </el-form-item>
      <el-form-item label="通知号码：">
        <el-input v-model="thresholdForm.phoneNums" type="textarea" row="5"/>
      </el-form-item>
      <el-form-item label="是否启用：">
        <el-switch v-model="thresholdForm.enable_b"
                   inline-prompt
                   active-text="是"
                   inactive-text="否"/>
      </el-form-item>
      <el-form-item label="警告规则：">
        <el-input v-model="thresholdForm.rule" type="textarea" row="5"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="saveThreshold" type="primary">保存</el-button>
        <el-button @click="thresholdDialogFormVisible = false" type="info">取消</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>

  import request from '@/network'
  import {onMounted, reactive, ref} from 'vue'
  import {vAlert} from '@/utils/Utils'
  import router from '@/router/index'
  import {Instance} from '@/type/Instance'

  const dialogFormVisible = ref(false)
  const fullscreen = ref(false)
  const fullscreenTxt = ref('全屏')
  const trendClass = ref('trend')
  const instances = reactive<Instance[]>([])

  const fullscreenHandle = () => {
    fullscreen.value = !fullscreen.value
    if (fullscreenTxt.value == '全屏') {
      fullscreenTxt.value = '缩小'
    } else {
      fullscreenTxt.value = '全屏'
    }
    window.setTimeout(() => {
      instances.forEach(inst => inst.resize())
    }, 500)
  }

  const data = reactive({
    quotaTypes: [
      {value: 'Data', label: '数据'},
      {value: 'Notify', label: '通知'},
      {value: 'Heartbeat', label: '心跳'}
    ]
  })

  const ruleForm = reactive({
    page: {
      pageNum: 1, pageSize: 10, total: 0
    },
    appName: '',
    quotaName: '',
    quotaDesc: '',
    quotaType: ''
  })

  const result = reactive({
    data: []
  })

  const tableRowClassName = ({row}: { row: any }) => {
    return row.hasThreshold == '是' ? 'warning-row' : ''
  }

  const submitForm = (): void => {
    console.log('提交表单');
    request({
      url: '/hmis/monitor/rule/queryQuotaInfos/v1.0',
      method: 'post',
      params: ruleForm.page,
      data: ruleForm
    }).then((res: any) => {
      if (res.data.code == 1) {
        result.data = res.data.body.list
        ruleForm.page.total = res.data.body.total;
      }
    }).catch((e: Error) => {
      console.error(e);
      vAlert('操作结果', '请求异常', 'error')
    })
  }

  // 1. 配置阈值
  const thresholdDialogFormVisible = ref(false)
  const thresholdForm = reactive({
    appName: '',
    quotaName: '',
    checkCount: 3,
    notifyTimeoutSec: 300,
    phoneNums: '',
    enable_b: false,
    enable: -1,
    rule: ''
  })
  const showThresholdDialog = (scope: any) => {
    thresholdDialogFormVisible.value = true
    thresholdForm.appName = scope.row.appName
    thresholdForm.quotaName = scope.row.quotaName
    thresholdForm.enable = -1
    request({
      url: '/hmis/monitor/rule/queryAllQuotaWarnRules/v1.0',
      method: 'post',
      data: thresholdForm
    }).then((res: any) => {
      if (res.data.code == 1) {
        let rules: any = res.data.body;
        if (rules.length > 0) {
          let rule = rules[0];
          thresholdForm.checkCount = rule.checkCount
          thresholdForm.notifyTimeoutSec = rule.notifyTimeoutSec
          thresholdForm.phoneNums = rule.phoneNums
          thresholdForm.enable = rule.enable
          thresholdForm.enable_b = rule.enable == 1
          thresholdForm.rule = rule.rule
        } else {
          thresholdForm.checkCount = 3
          thresholdForm.notifyTimeoutSec = 300
          thresholdForm.phoneNums = ''
          thresholdForm.enable = 0
          thresholdForm.enable_b = false
          thresholdForm.rule = ''
        }
      }
    })
  }
  const saveThreshold = () => {
    thresholdDialogFormVisible.value = false
    thresholdForm.enable = thresholdForm.enable_b ? 1 : 0
    request({
      url: '/hmis/monitor/rule/saveQuotaWarnRule/v1.0',
      method: 'post',
      data: thresholdForm
    }).then((res: any) => {
      if (res.data.code == 1) {
        vAlert('操作结果', '操作成功', 'success')
      } else {
        vAlert('操作结果', '操作失败', 'error')
      }
    }).catch((e: any) => {
      vAlert('操作结果', '请求异常', 'error')
    })
  }

  // 2. 展示实例
  const showInstance = (scope: any): void => {
    router.push({
      path: '/monitor/instanceList',
      query: scope.row
    })
  }

  // 3. 查询趋势
  const getDefaultDateTime = () => {
    const startTime = new Date()
    startTime.setTime(startTime.getTime() - 3600 * 1000 * 3)
    return [startTime, new Date()]
  }

  const showTimeComponent = ref(false)
  const trendForm = reactive({
    timeType: '3',
    time: getDefaultDateTime(),
    appName: '',
    quotaName: ''
  })

  // 打开趋势面板
  const showTrend = (scope: any): void => {
    // 设置表单参数
    trendForm.appName = scope.row.appName
    trendForm.quotaName = scope.row.quotaName

    // 查询实例列表
    request({
      url: '/hmis/manage/agent/kafkaIn/queryInstanceList/v1.0',
      method: 'post',
      params: {pageNum: 1, pageSize: 100},
      data: {
        appName: scope.row.appName
      }
    }).then((res: any) => {
      if (res.data.code == 1) {
        dialogFormVisible.value = true
        instances.length = 0
        res.data.body.list.forEach((instance: any) => {
          instances.push(new Instance(ruleForm.appName, instance.dataCenter, instance.ip, instance.port))
        })
        queryQuotaData()
      }
    }).catch((error: any) => {
      vAlert('操作结果', '请求异常', 'error')
    })
  }

  const changeTimeType = () => {
    if ('custom' == trendForm.timeType) {
      showTimeComponent.value = true
      trendForm.time = getDefaultDateTime()
    } else {
      showTimeComponent.value = false
      trendForm.time = []
    }
    queryQuotaData()
  }

  const queryQuotaData = () => {
    instances.forEach((instance: Instance) => {
      instance.queryData(trendForm)
    })
  }

  onMounted(() => submitForm())

</script>

<style>
  .demo-form-inline {
    padding: 10px;
  }

  .time {
    padding-top: 10px;
  }

  .trend {
    /*width: 600px;*/
    width: calc(100% - 100px);
    height: 300px;
    overflow: hidden;
  }

  .warning-row {
    --el-table-tr-bg-color: var(--el-color-danger-light-5);
  }
</style>
