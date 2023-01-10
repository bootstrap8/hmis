export default {
  setAuthentication(state, payload) {
    state.authentication = payload.authentication;
    console.log('设置认证信息: {}', state.authentication)
  }
}
