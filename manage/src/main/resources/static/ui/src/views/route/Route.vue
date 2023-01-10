<template>
  <div class="route">
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item label="路由选项" prop="routeSelect">
        <el-select v-model="routeSelect" class="m-2" placeholder="请选择" size="large" @change="showSelect">
          <el-option
              v-for="item in routeMetaSelects"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="路由关键字" prop="routeKey">
        <el-input v-model="routeKey" placeholder="请输入关键字..."/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="routeData" style="width: 100%"
              :border="true"
              table-layout="fixed"
              :stripe="true"
              size="small"
              :highlight-current-row="true">
      <el-table-column prop="id" label="路标标识"/>
      <el-table-column prop="uri" label="uri"/>
      <el-table-column prop="predicates" label="路由谓语"/>
      <el-table-column prop="filters" label="路由过滤器"/>
    </el-table>
    <el-pagination
        v-model:page-size="pageSize"
        v-model:current-page="pageNum"
        layout="total, prev, pager, next"
        v-model:total="total"
        @size-change="onSubmit"
        @current-change="onSubmit"
        @prev-click="onSubmit"
        @next-click="onSubmit"
    />
  </div>
</template>

<script>
  import request from '@/network'
  import {ref, reactive} from 'vue'

  export default {
    name: "Route",
    data() {
      return {
        routeKey: '',
        routeSelect: 'route_id',
        routeMetaSelects: [],
        pageNum: 1,
        pageSize: 1,
        total: 0,
        routeData: []
      }
    },
    methods: {
      // 查询查询条件下拉列表
      getRoutMetaSelects() {
        request({
          url: '/hmis/manage/route/queryRouteOptions/v1.0'
        }).then(res => {
          console.log('请求到路由类型配置: {}', res)
          if (res.data.code == 1) {
            this.routeMetaSelects = res.data.body;
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
      onSubmit() {
        console.log('查询路由列表')
        request({
          url: '/hmis/manage/route/queryAllRouteConfig/v1.0?pageNum=' + this.pageNum + '&pageSize=' + this.pageSize,
          // param: this.page
        }).then(res => {
          console.log('请求到路由数据: ', res)
          if (res.data.code == 1) {
            this.total = res.data.body.total;
            this.routeData = res.data.body.list;
          }
        }).catch(e => {
          console.error(e);
        })
      }
    },
    created() {
      console.log('加载完Route.vue视图')
      this.getRoutMetaSelects()
      this.onSubmit();
    }
  }
</script>

<style scoped>

</style>
