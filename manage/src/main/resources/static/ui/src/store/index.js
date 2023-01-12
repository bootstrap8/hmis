import {createStore} from 'vuex'
import getters from '@/store/getters'
import mutations from '@/store/mutations'
import actions from '@/store/actions'

const state = {
  msg: 'hello vuex',
  authentication: ''
}

export default createStore({
  state,
  getters: getters,
  mutations: mutations,
  actions: actions,
  modules: {}
})
