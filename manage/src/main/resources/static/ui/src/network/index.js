import axios from 'axios'
import store from '@/store'
import {ElLoading} from 'element-plus'

console.log('环境信息: ', process.env.NODE_ENV);

// 缺省认证信息
const defaultAuthentication = "default-token"

export default (config) => {
  let instance = axios.create(
      "development" == process.env.NODE_ENV ?
          // dev环境
          {
            baseURL: 'http://localhost:20000',
            timeout: 5000,
            headers: {defaultAuthentication}
          } :
          // prod环境
          {
            baseURL: 'http://192.168.56.2:20000',
            timeout: 5000,
            headers: {Authentication: store.getters.getAuthentication || defaultAuthentication}
          });

  let loadingInstance;

  // 添加请求拦截器
  instance.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    // 增加过度效果
    loadingInstance = ElLoading.service({
      lock: true,
      text: '加载中...',
      fullscreen: true,
      background: 'rgba(0, 0, 0, 0.7)'
    });
    return config;
  }, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
  });

  // 添加响应拦截器
  instance.interceptors.response.use(function (response) {
    // 对响应数据做点什么
    // 关闭过度效果
    loadingInstance.close()
    return response;
  }, function (error) {
    // 对响应错误做点什么
    // 关闭过度效果
    loadingInstance.close()
    return Promise.reject(error);
  });

  return instance(config);
}
