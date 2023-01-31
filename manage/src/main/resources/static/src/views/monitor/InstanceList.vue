<template>
  <div class="appList">
    <el-table :data="data.instanceList" style="width: 100%"
              :border="true"
              table-layout="fixed"
              :stripe="true"
              size="small"
              :highlight-current-row="true">
      <el-table-column fixed="left" label="操作" width="120" header-align="center" align="center">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="showTrend(scope)">趋势</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="appName" label="应用名称" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="dataCenter" label="数据中心" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="ip" label="ip地址" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="hostName" label="主机名" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="port" label="端口" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="processNo" label="进程号" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="tags" label="标签" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="fmtRegTime" label="注册时间" :show-overflow-tooltip="true" header-align="center"/>
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

    <el-dialog v-model="dialogFormVisible" :title="trendForm.quotaName" :fullscreen="fullscreen">
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
      <!--      <el-tag class="ml-2" type="success">{{trendForm.ip + ':' + trendForm.port}}</el-tag>-->
      <div id="trend" class="trend"></div>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="queryQuotaData" type="primary">查询</el-button>
        <el-button @click="fullscreenHandle" type="success">{{fullscreenTxt}}</el-button>
        <el-button @click="dialogFormVisible = false" type="info">取消</el-button>
      </span>
      </template>
    </el-dialog>

  </div>

</template>

<script lang="ts" setup>
  import request from '@/network'
  import {ref, reactive, onMounted} from 'vue'
  import {vAlert, Page} from '@/utils/Utils'
  import router from '@/router/index'
  import * as echarts from 'echarts'

  const dialogTableVisible = ref(false)
  const dialogFormVisible = ref(false)
  const formLabelWidth = '140px'
  const fullscreen = ref(false)
  const fullscreenTxt = ref('全屏')
  let myChart: any = null

  const fullscreenHandle = () => {
    fullscreen.value = !fullscreen.value
    if (fullscreenTxt.value == '全屏') {
      fullscreenTxt.value = '缩小'
    } else {
      fullscreenTxt.value = '全屏'
    }
    window.setTimeout(() => myChart.resize(), 500)
  }

  const ruleForm = reactive({
    page: {
      pageNum: 1,
      pageSize: 10,
      total: 0
    }
  })

  const data = reactive({
    instanceList: [],
    qdl: []
  })

  const query = router.currentRoute.value.query;

  const queryInstanceList = (): void => {
    request({
      url: '/hmis/manage/agent/kafkaIn/queryInstanceList/v1.0',
      method: 'post',
      params: ruleForm.page,
      data: {
        appName: query.appName
      }
    }).then((res: any) => {
      if (res.data.code == 1) {
        data.instanceList = res.data.body.list
      }
    }).catch((error: any) => {
      vAlert('操作结果', '请求异常', 'error')
    })
  }

  const getDefaultDateTime = () => {
    const startTime = new Date()
    startTime.setTime(startTime.getTime() - 3600 * 1000 * 3)
    return [startTime, new Date()]
  }

  const showTimeComponent = ref(false)
  const trendForm = reactive({
    timeType: '3',
    time: getDefaultDateTime(),
    dataCenter: '',
    appName: '',
    ip: '',
    port: 8080,
    quotaName: query.quotaName
  })

  const showTrend = (scope: any) => {
    dialogFormVisible.value = true
    trendForm.dataCenter = scope.row.dataCenter
    trendForm.appName = scope.row.appName
    trendForm.ip = scope.row.ip
    trendForm.port = scope.row.port
    queryQuotaData()
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
    request({
      url: '/hmis/monitor/qd/queryQuotaDatas/v1.0',
      method: 'post',
      data: trendForm
    }).then((res: any) => {
      if (res.data.code == 1) {
        data.qdl = res.data.body;
        let xAxisData: any[] = []
        let seriesData: any[] = []
        data.qdl.forEach((item: any) => {
          xAxisData.push(item.fmtCollectTime)
          seriesData.push(item.dataValue)
        })
        if (myChart == null) {
          myChart = echarts.init(document.getElementById('trend') as HTMLElement)
        }
        myChart.setOption({
          legend: {
            type: 'plain',
          },
          tooltip: {
            trigger: 'axis',
            formatter: '时间: {b}<br/>值: {c}'
          },
          xAxis: {
            data: xAxisData
          },
          yAxis: {},
          series: [
            {
              name: trendForm.ip + ':' + trendForm.port,
              data: seriesData,
              type: 'line',
              symbolSize: 0,
              label: {
                show: false
              },
              areaStyle: {
                color: '#B1FF54A8',
                opacity: 0.3
              },
              lineStyle: {
                color: '#B1FF54A8',
                width: 1
              }
            }
          ]
        })
      }
    }).catch((error: any) => {
      vAlert('操作结果', '请求异常', 'error')
    })
  }

  onMounted(() => queryInstanceList());
</script>

<style scoped>
  .time {
    padding-top: 10px;
  }

  .trend {
    /*width: 600px;*/
    width: calc(100% - 100px);
    height: 300px;
    overflow: hidden;
  }
</style>
