<template>
  <el-menu
      :default-active="activeIndex"
      class="nav"
      @select="handleSelect"
      :router="true"
      mode="horizontal"
      :collapse="true"
      :ellipsis="true"
  >
    <el-sub-menu index="/route">
      <template #title>路由管理</template>
      <el-menu-item index="/route/list">路由配置</el-menu-item>
    </el-sub-menu>
    <el-sub-menu index="/dict">
      <template #title>字典管理</template>
      <el-menu-item index="/dict/list">字典配置</el-menu-item>
    </el-sub-menu>
    <el-sub-menu index="/agent">
      <template #title>指标监控</template>
      <el-menu-item index="/agent/kafkaIn/appList">kafka入口消息流控管理</el-menu-item>
      <el-menu-item index="/monitor/quotaList">指标列表</el-menu-item>
    </el-sub-menu>
  </el-menu>
  <div class="frame">
    <router-view/>
  </div>
</template>

<script lang="ts" setup>
  import {ref} from 'vue'

  const activeIndex = ref('/route')

  const handleSelect = (key: string, keyPath: string[]) => {

  }
</script>

<style lang="scss">
  .frame {
    padding: 20px 5px 10px 5px;
  }
</style>
