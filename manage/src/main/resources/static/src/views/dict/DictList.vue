<template>
  <el-form :inline="true" class="demo-form-inline">
    <el-form-item label="字典选项" prop="dictOption">
      <el-select v-model="ruleForm.dictOption" class="m-2" placeholder="请选择" size="large" @change="optionChange(ruleForm.dictOption)">
        <el-option v-for="(item,index) in dictOptions" :key="item.value" :label="item.label" :value="item.value"/>
      </el-select>
    </el-form-item>
    <el-form-item label="字典关键字" prop="dictKey" v-if="ruleForm.dictOption=='enum_type'">
      <el-select v-model="ruleForm.dictKey" class="m-2" placeholder="请选择" size="large">
        <el-option v-for="(item,index) in enumTypes" :key="item.value" :label="item.label" :value="item.value"/>
      </el-select>
    </el-form-item>
    <el-form-item label="字典关键字" prop="dictKey" v-else>
      <el-input v-model="ruleForm.dictKey" placeholder="请输入关键字..."/>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submitForm">查询</el-button>
      <el-button type="primary" :icon="Edit" circle @click="addDict" title="添加字典"/>
    </el-form-item>
  </el-form>
  <el-table :data="result.data" style="width: 100%"
            :border="true"
            table-layout="fixed"
            :stripe="true"
            size="small"
            :highlight-current-row="true">
    <el-table-column fixed="left" label="操作" width="120" header-align="center" align="center">
      <template #default="scope">
        <el-button link type="primary" size="small" @click="editDict(scope)">编辑</el-button>
        <el-popconfirm title="你确定要删除本条字典吗?" @confirm="delConfirm(scope)">
          <template #reference>
            <el-button link type="danger" size="small">删除</el-button>
          </template>
        </el-popconfirm>
      </template>
    </el-table-column>
    <el-table-column prop="fieldName" label="字段名称" :show-overflow-tooltip="true" header-align="center"/>
    <el-table-column prop="fieldDesc" label="字段描述" :show-overflow-tooltip="true" header-align="center"/>
    <el-table-column prop="enumType" label="枚举值来源" :show-overflow-tooltip="true" header-align="center"/>
  </el-table>
  <el-pagination class="page"
      v-model:page-size="pg.pageSize"
      v-model:current-page="pg.pageNum"
      layout="->, total, sizes, prev, pager, next, jumper"
      v-model:total="pg.total"
      @size-change="submitForm"
      @current-change="submitForm"
      @prev-click="submitForm"
      @next-click="submitForm"
      :small="true"
      :background="true"
      :page-sizes="[5, 10, 20, 50,100]"
  />
</template>

<script lang="ts" setup>

  import request from '@/network'
  import {ref, reactive, onMounted} from 'vue'
  import {Edit} from '@element-plus/icons-vue'
  import {vAlert, Page} from '@/utils/Utils'
  import router from '@/router/index'
  import {DictForm, DictInfo} from '@/type/Dict'

  const dictOptions = reactive([
    {value: 'field_name', label: '字段名称'},
    {value: 'field_desc', label: '字段描述'},
    {value: 'enum_type', label: '枚举来源'}
  ])

  const enumTypes = reactive([
    {value: 'enum', label: '手工枚举'},
    {value: 'sql', label: '从sql查询'}
  ])

  const pg = reactive(new Page(1, 10, 0))
  const ruleForm = reactive(new DictForm('field_name', ''))

  const result = reactive({
    data: []
  })

  const optionChange = (dictOption: string): void => {
    console.log('切换到字典选项: ', dictOption)
    if ('enum_type' != dictOption) {
      ruleForm.dictKey = ''
    }
  }

  const submitForm = (): void => {
    console.log('提交表单');
    request({
      url: '/hmis/manage/dict/queryAllDict/v1.0',
      method: 'post',
      params: pg,
      data: ruleForm
    }).then((res: any) => {
      if (res.data.code == 1) {
        console.log('查询到字典基本信息列表: ', res.data.body.list)
        result.data = res.data.body.list
        pg.total = res.data.body.total;
      }
    }).catch((e: Error) => {
      console.error(e);
    })
  }

  const addDict = (): void => {
    console.log('添加字典')
    router.push('/dict/info')
  }

  const editDict = (scope: any): void => {
    console.log('编辑字典')
    router.push({
      path: '/dict/info',
      query: scope.row
    })
  }

  const delConfirm = (scope: any): void => {
    const fn = scope.row.fieldName
    console.log('删除字典配置: ', fn)
    request({
      url: '/hmis/manage/dict/deleteAllDict/v1.0',
      method: 'post',
      params: {fn: fn}
    }).then((res: any) => {
      if (res.data.code == 1) {
        vAlert('操作结果', '删除成功','success')
        submitForm()
      } else {
        vAlert('操作结果', res.data.body, 'error')
      }
    }).catch((e: Error) => {
      vAlert('操作结果', '请求异常', 'error')
    })
  }

  onMounted(() => submitForm())

</script>

<style scoped>
  .demo-form-inline {
    padding: 10px;
  }
</style>
