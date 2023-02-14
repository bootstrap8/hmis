<template>
  <el-scrollbar class="result-area" ref="myScrollbar">
    <div v-for="(item,index) in result">
      {{item.user}}({{item.passed+'秒'}})&nbsp;&nbsp;<pre>{{item.text}}</pre>
      <br/>
    </div>
  </el-scrollbar>
  <el-form
      :inline="true"
      ref="ruleFormRef"
      :model="ruleForm"
      :rules="rules"
      class="demo-form-inline">
    <el-form-item label="" prop="question">
      <el-input v-model="ruleForm.question" type="textarea" :rows="rows" :cols="cols" placeholder="请输入内容..." @keydown.enter.prevent="submitForm(ruleFormRef)"/>
    </el-form-item>
    <el-form-item>
      <el-button :icon="Promotion" circle @click="submitForm(ruleFormRef)" :disabled="ruleForm.submitBtnDisabled"/>
    </el-form-item>
  </el-form>
</template>
<script lang="ts" setup>
  import {ChatGPTAPI} from 'chatgpt'
  import {onMounted, ref, reactive} from 'vue'
  import type {FormInstance, FormRules} from 'element-plus'
  import {Search, Promotion, Loading} from '@element-plus/icons-vue'

  class Chatgpt {
    user: string;
    text: any;
    passed: number;

    constructor(user: string, text: any, passed: number) {
      this.user = user;
      this.text = text;
      this.passed = passed;
    }
  }

  const rows = ref(1)
  const cols = ref(80)
  const result = reactive<Chatgpt[]>([])
  const ruleForm = reactive({
    question: '',
    submitBtnDisabled: false
  })
  const ruleFormRef = ref<FormInstance>()
  const rules = reactive<FormRules>({
    question: [{required: true, message: '请输入内容', trigger: 'blur'}]
  })

  const api = new ChatGPTAPI({apiKey: process.env.VUE_APP_OPENAI_API_KEY})

  const myScrollbar = ref()
  const scrollDown = () => {
    const t = window.setTimeout(() => {
      myScrollbar.value.scrollTo({
        top: myScrollbar.value.wrapRef.scrollHeight,
        behavior: "smooth"
      });
      window.clearTimeout(t)
    }, 500)
  }

  const submitForm = (formEl: FormInstance | undefined) => {
    ruleForm.submitBtnDisabled = true
    if (!formEl) return
    formEl.validate((valid, fields) => {
      if (valid) {
        result.push(new Chatgpt('我', ruleForm.question, 0))
        scrollDown()
        let time = new Date().getTime();
        api.sendMessage(ruleForm.question, {timeoutMs: 120000}).then((res: any) => {
          console.log('返回内容:', res.text);
          time = (new Date().getTime() - time) / 1000;
          result.push(new Chatgpt('chatgpt', res.text, time))
          ruleForm.submitBtnDisabled = false
          scrollDown()
        })
      } else {
        console.log('表单校验错误: ', fields)
        ruleForm.submitBtnDisabled = false
      }
    })
  }
</script>
<style scoped>
  .demo-form-inline {
    position: absolute;
    bottom: 11%;
  }

  .el-form--inline .el-form-item {
    margin-right: 10px;
  }

  .result-area {
    padding: 10px;
    height: 480px;
    width: 700px;
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    margin-top: 0px;
    overflow-y: auto;
    font-size: 5px;
    font-family: "Fira Code Medium";
  }

  .result-area pre {
    width: 680px;
    word-break: break-all;
    white-space: pre-wrap;
  }
</style>
