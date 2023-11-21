import Vue from 'vue'
import App from './App.vue'
import { router } from './router'
import store from './store' // 导入 Vuex store
import 'normalize.css/normalize.css'
import Element from 'element-ui'
import './styles/element-variables.scss'
import '@/styles/index.scss'
import './icons'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'



Vue.use(Element, {
  size: 'medium'
})

Vue.config.productionTip = false

NProgress.configure({ showSpinner: false })

router.beforeEach(async (to, from, next) => {
  NProgress.start()
  // 设置页面标题
  if (to.meta.title !== undefined) {
    document.title = to.meta.title
  } else {
    document.title = '\u200E'
  }
  // 初始化路由
  store.commit('router/initRoutes')

  if (to.path) {
    // eslint-disable-next-line no-undef
    _hmt.push(['_trackPageview', '/#' + to.fullPath])
  }

  next()
})

router.afterEach(() => {
  NProgress.done()
})

Vue.prototype.$$router = router

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')

