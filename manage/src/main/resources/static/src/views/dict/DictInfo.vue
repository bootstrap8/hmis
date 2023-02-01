<template>
  <el-form :inline="false" class="demo-form-inline" label-width="120px"
           ref="ruleFormRef"
           :model="ruleForm"
           :rules="rules"
           :size="formSize"
           status-icon
  >

    <el-form-item label="字段名称" prop="fieldName">
      <el-input v-model="ruleForm.fieldName" placeholder="请输入字段名称..." :disabled="fieldNameDisabled"/>
    </el-form-item>

    <el-form-item label="字段描述" prop="fieldDesc">
      <el-input v-model="ruleForm.fieldDesc" placeholder="请输入字段描述..."/>
    </el-form-item>

    <el-form-item label="枚举来源" prop="enumType">
      <el-select v-model="ruleForm.enumType" class="m-2" placeholder="请选择" size="large">
        <el-option v-for="(item,index) in enumTypes" :key="item.value" :label="item.label" :value="item.value"/>
      </el-select>
    </el-form-item>

    <el-form-item label="添加枚举" v-show="ruleForm.enumType=='enum'">
      <el-icon :size="20" class="icon-circle-plus-filled" @click="addEnum">
        <CirclePlusFilled/>
      </el-icon>
    </el-form-item>

    <el-form-item label="" v-show="ruleForm.enumType=='enum'" v-for="(item,index) in ruleForm.pairs">
      <div class="enums">
        <span class="pair-key">键：</span>
        <el-input v-model="item.key" @blur="showEnums"/>
        <span class="pair-value">值：</span>
        <el-input v-model="item.value" @blur="showEnums"/>
        <el-icon :size="20" class="icon-delete" @click="removeEnum(index)">
          <Delete/>
        </el-icon>
      </div>
    </el-form-item>

    <el-form-item label="来源sql" prop="enumSql" v-show="ruleForm.enumType=='sql'">
      <el-input v-model="ruleForm.enumSql" placeholder="请输入枚举sql..." type="textarea" :rows="row"/>
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="submitForm(ruleFormRef)">保存</el-button>
    </el-form-item>

  </el-form>
</template>

<script lang="ts" setup>

  import request from '@/network'
  import {ref, reactive, onMounted} from 'vue'
  import {Edit, DocumentAdd, CirclePlusFilled, Delete} from '@element-plus/icons-vue'
  import type {FormInstance, FormRules} from 'element-plus'
  import {msg, objectEmpty} from '@/utils/Utils'
  import router from '@/router/index'
  import {DictEdit, Pair} from "@/type/Dict";
  import axios from "@/network";

  const formSize = ref('default')
  const row = ref(6);
  const fieldNameDisabled = ref(false)
  const ruleFormRef = ref<FormInstance>()
  const ruleForm = reactive(new DictEdit('', '', '', '', []))
  const rules = reactive<FormRules>({
    fieldName: [{required: true, message: '字典名称不能为空', trigger: 'blur'}],
    fieldDesc: [{required: true, message: '字典描述不能为空', trigger: 'blur'}],
    enumType: [{required: true, message: '枚举类型必须要选择一个', trigger: 'blur'}],
    enumSql: [{
      validator: (rule: any, value: any, callback: any) => {
        if (ruleForm.enumType == 'sql' && value == '') {
          callback(new Error('枚举sql不能为空'))
        }
        callback()
      }, trigger: 'blur'
    }]
  })

  const enumTypes = reactive([
    {value: 'enum', label: '手工枚举'},
    {value: 'sql', label: '从sql查询'}
  ])

  const addEnum = (): void => {
    const p: Pair = new Pair('', '')
    ruleForm.pairs.push(p)
  }

  const removeEnum = (index: number): void => {
    console.log('删除枚举')
    ruleForm.pairs.splice(index, 1)
  }

  const showEnums = (): void => {
    console.log('已经添加的枚举值: ', ruleForm.pairs)
  }

  const submitForm = async (formEl: FormInstance | undefined) => {
    console.log('提交的表单: ', ruleForm)
    if (!formEl) return
    await formEl.validate((valid, fields) => {
      if (valid) {
        request({
          url: '/hmis/manage/dict/saveDictInfo/v1.0',
          method: 'post',
          data: ruleForm
        }).then((res: any) => {
          if (res.data.code == 1) {
            msg('保存成功', 'success')
            router.push('/dict/list')
          }
        }).catch((e: Error) => {
          msg('保存失败', 'error')
        })
      } else {
        console.log('表单校验错误: ', fields)
      }
    })
  }

  onMounted(() => {
    const query = router.currentRoute.value.query;
    if (!objectEmpty(query)) {
      console.log('编辑参数：', query)
      ruleForm.copyWithRouteQuery(query)
      fieldNameDisabled.value = true
      if (ruleForm.isSqlEnum()) {
        request({
          url: '/hmis/manage/dict/queryDictSqlExt/v1.0',
          params: {fn: ruleForm.fieldName}
        }).then((res: any) => {
          if (res.data.code == 1) {
            ruleForm.enumSql = res.data.body.enumSql
          }
        })
      }
      if (ruleForm.isHandEnum()) {
        request({
          url: '/hmis/manage/dict/queryDictEnumExt/v1.0',
          params: {fn: ruleForm.fieldName}
        }).then((res: any) => {
          if (res.data.code == 1) {
            ruleForm.pairs = res.data.body.pairs
          }
        })
      }
    }
  })

</script>

<style scoped>
  .demo-form-inline {
    width: 70%;
  }

  .icon-circle-plus-filled {
    cursor: pointer;
    color: #409EFC;
  }

  .icon-delete {
    cursor: pointer;
    color: #706f6f;
    margin: 5px 0 0 10px;
  }

  .enums {
    display: flex;
  }

  .pair-key {
    margin: 0 10px 0 0px;
  }

  .pair-value {
    margin: 0 10px 0 20px;
  }
</style>
