import Vue from 'vue'
import Vuex from 'vuex'
import getters from './getters'

Vue.use(Vuex)

// 使用 webpack 的 require.context 自动引入 modules 文件夹下的所有 .js 模块
const modulesFiles = require.context('./modules', true, /\.js$/)

// 使用 reduce 将每个模块引入并以模块名为 key 存储在 modules 对象中
const modules = modulesFiles.keys().reduce((modules, modulePath) => {
  // 从路径中提取模块名，例如 './app.js' => 'app'
  const moduleName = modulePath.replace(/^\.\/(.*)\.\w+$/, '$1')
  const value = modulesFiles(modulePath)
  modules[moduleName] = value.default
  return modules
}, {})

//userDate
// const teacherData = {
//   state: {
//     teacherData: {}
//   },
//   mutations: {
//     setTeacherData: (state, teacherData) => {
//       state.teacherData = teacherData
//     }
//   },
//   actions: {
//     setTeacherData({ commit }, teacherData) {
//       commit('setTeacherData', teacherData)
//     }
//   }
// }

// 创建 Vuex store 实例
const store = new Vuex.Store({
  modules, // 将模块添加到 store
  // teacherData, // 将 teacherData 添加到 store
  getters // 使用 getters
})

export default store
