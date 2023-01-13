<template>
  <el-form ref="ruleFormRef"
           :model="ruleForm"
           :rules="rules"
           label-width="120px"
           class="demo-ruleForm"
           :size="formSize"
           status-icon>
    <el-form-item label="路由模板">
      <el-select v-model="tempSelect.tid" placeholder="请选择对应的模板" @change="tempChange(tempSelect.tid)">
        <el-option v-for="(item,index) in templates" :label="item.name" :value="item.tid"/>
      </el-select>
    </el-form-item>
    <el-form-item label="路由标识" prop="rid">
      <el-input v-model="ruleForm.rid" placeholder="请输入路由标识..." :disabled="ridDisabled"/>
    </el-form-item>
    <el-form-item label="路由uri" prop="uri">
      <el-input v-model="ruleForm.uri" placeholder="请输入uri..."/>
    </el-form-item>
    <el-form-item label="路由谓语" prop="predicates">
      <el-input v-model="ruleForm.predicates" type="textarea" :rows="row"/>
    </el-form-item>
    <el-form-item label="路由过滤" prop="filters">
      <el-input v-model="ruleForm.filters" type="textarea" :rows="row"/>
    </el-form-item>
    <el-form-item label="优先级" prop="order">
      <el-input v-model="ruleForm.order" placeholder="请输入路由标识..." type="number"/>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submitForm(ruleFormRef)">保存</el-button>
      <el-button @click="resetForm(ruleFormRef)">取消</el-button>
    </el-form-item>
  </el-form>
</template>

<script lang="ts" setup>

  import {ref, reactive, onMounted} from 'vue'
  import axios from '@/network'
  import type {FormInstance, FormRules} from 'element-plus'
  import {RouteTemplate, RouteInfo} from '@/type/Route'
  import {vAlert} from '@/utils/Utils'
  import router from '@/router/index'

  // 表单校验规则
  const ridDisabled=ref(false)
  const formSize = ref('default')
  const ruleFormRef = ref<FormInstance>()
  const ruleForm = reactive<RouteInfo>(new RouteInfo('', '', '', '', 0))
  const rules = reactive<FormRules>({
    rid: [{required: true, message: '路由标识不能为空', trigger: 'blur'}],
    uri: [{required: true, message: '路由uri不能为空', trigger: 'blur'}],
    predicates: [{required: true, message: '路由谓语不能为空', trigger: 'blur'}],
    filters: [{required: true, message: '路由过滤不能为空', trigger: 'blur'}],
    order: [{required: true, message: '路由优先级不能为空', trigger: 'blur'}]
  })


  const row = 8;

  const templates = reactive<RouteTemplate[]>([])
  const tempSelect = reactive<RouteTemplate>(new RouteTemplate(1, '', '', '', 0));

  const tempChange = (tid: number) => {
    const selected: RouteTemplate = templates.filter((t: RouteTemplate) => t.tid == tid)[0];
    tempSelect.copy(selected);
    ruleForm.setUseTemp(selected);
    console.log('选择中的模板: {}', tempSelect);
  }

  const submitForm = async (formEl: FormInstance | undefined) => {
    if (!formEl) return
    await formEl.validate((valid, fields) => {
      if (valid) {
        axios({
          url: '/hmis/manage/route/saveRouteConfig/v1.0',
          method: 'post',
          data: ruleForm.toRestfulObj()
        }).then((res: any) => {
          if (res.data.code == 1) {
            vAlert('保存结果', res.data.body);
            router.push('/route/list')
          }
        }).catch((e: Error) => {
          vAlert('操作结果', '保存失败','error')
        })
      } else {
        console.log('error submit!', fields)
      }
    })
  }

  const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
  }

  onMounted(() => {
    console.log('路由数据: ', router.currentRoute.value.query)
    axios({
      url: '/hmis/manage/route/queryRouteTemplates/v1.0',
      method: 'post'
    }).then(res => {
      if (res.data.code == 1) {
        templates.length = 0
        res.data.body.forEach((t: RouteTemplate) => templates.push(t));
        console.log('查询到模板信息: ', templates);
        if (Object.keys(router.currentRoute.value.query).length > 0) {
          console.log('编辑功能')
          ruleForm.copy(router.currentRoute.value.query)
          ridDisabled.value=true
        } else {
          tempChange(tempSelect.tid);
        }
      }
    }).catch(e => {
      console.error(e);
    })
  })
</script>

<style scoped>
  .demo-ruleForm {
    width: 70%;
  }
</style>
