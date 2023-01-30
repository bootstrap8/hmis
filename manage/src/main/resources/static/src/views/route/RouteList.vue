<template>
  <div class="route">
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item label="路由选项" prop="routeSelect">
        <el-select v-model="fq.routeSelect" class="m-2" placeholder="请选择" size="large" @change="showSelect">
          <el-option
              v-for="item in data.routeMetaSelects"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="路由关键字" prop="routeKey">
        <el-input v-model="fq.routeKey" placeholder="请输入关键字..."/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="queryRoutes">查询</el-button>
        <el-button type="warning" @click="refreshRoute">刷新路由</el-button>
        <el-button type="primary" :icon="Edit" circle @click="addRoute" title="添加路由"/>
      </el-form-item>
    </el-form>
    <el-table :data="data.routeData" style="width: 100%"
              :border="true"
              table-layout="fixed"
              :stripe="true"
              size="small"
              :highlight-current-row="true">
      <el-table-column fixed="left" label="操作" width="120" header-align="center" align="center">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="editRoute(scope)">编辑</el-button>
          <el-popconfirm title="你确定要删除本条路由吗?" @confirm="delConfirm(scope)">
            <template #reference>
              <el-button link type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
      <el-table-column prop="id" label="路标标识" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="uri" label="uri" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="predicates" label="路由谓语" :show-overflow-tooltip="true" header-align="center"/>
      <el-table-column prop="filters" label="路由过滤器" :show-overflow-tooltip="true" header-align="center"/>
    </el-table>
    <el-pagination class="page"
        v-model:page-size="fq.pageSize"
        v-model:current-page="fq.pageNum"
        layout="->, total, sizes, prev, pager, next, jumper"
        v-model:total="fq.total"
        @size-change="queryRoutes"
        @current-change="queryRoutes"
        @prev-click="queryRoutes"
        @next-click="queryRoutes"
        :small="true"
        :background="true"
        :page-sizes="[5, 10, 20, 50,100]"
    />

  </div>
</template>

<script>
  import request from '@/network'
  import {ref, reactive} from 'vue'
  import {Edit} from '@element-plus/icons-vue'
  import {vAlert} from '@/utils/Utils'
  import router from '@/router/index'

  export default {
    name: "Route",
    data() {
      return {Edit}
    },
    setup() {
      const fq = reactive({
        routeSelect: 'route_id',
        routeKey: '',
        pageNum: 1,
        pageSize: 10,
        total: 0,
      })

      const data = reactive({
        routeMetaSelects: [],
        routeData: []
      })
      return {fq, data}
    },
    methods: {
      // 查询查询条件下拉列表
      getRoutMetaSelects() {
        request({
          url: '/hmis/manage/route/queryRouteOptions/v1.0'
        }).then(res => {
          console.log('请求到路由类型配置: {}', res)
          if (res.data.code == 1) {
            this.data.routeMetaSelects = res.data.body;
          }
        }).catch(e => {
          console.error(e);
        });
      },
      // 条件切换触发
      showSelect() {
        console.log('选择的下拉列值: ', this.routeSelect)
      },
      // 查询触发
      queryRoutes() {
        console.log('查询路由列表')
        request({
          url: '/hmis/manage/route/queryAllRouteConfig/v1.0',
          params: this.fq
        }).then(res => {
          // console.log('请求到路由数据: ', res)
          if (res.data.code == 1) {
            this.fq.total = res.data.body.total;
            this.data.routeData = res.data.body.list;
          }
        }).catch(e => {
          console.error(e);
        })
      },
      delConfirm(scope) {
        this.delRoute(scope);
      },
      // 删除路由
      delRoute(scope) {
        const routeId = scope.row.id
        console.log('删除选中的元素: ', routeId)
        request({
          url: '/hmis/manage/route/deleteRouteConfig/v1.0',
          method: 'post',
          params: {id: routeId}
        }).then(res => {
          if (res.data.code == 1) {
            vAlert('操作结果', '操作成功','success')
            this.queryRoutes()
          }
        }).catch(e => {
          alert('删除失败')
        })
      },
      // 编辑路由
      editRoute(scope) {
        console.log('编辑选中的元素: ', scope.row);
        router.push({
          path: '/route/info',
          query: scope.row
        })
      },
      // 刷新路由配置
      refreshRoute() {
        request({
          url: '/hmis/manage/route/refresh/v1.0',
          method: 'post'
        }).then(res => {
          if (res.data.code == 1) {
            vAlert('操作结果', '操作成功','success')
          }
        }).catch(e => {
          alert('刷新失败');
        })
      },
      // 添加路由
      addRoute() {
        this.$router.push('/route/info');
      }
    },
    created() {
      console.log('加载完Route.vue视图')
      this.getRoutMetaSelects()
      this.queryRoutes();
    }
  }
</script>

<style scoped>

</style>
