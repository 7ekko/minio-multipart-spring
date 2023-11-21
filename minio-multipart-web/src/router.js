import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

const constantRoutes = [

  {
    path: '/',
    name: 'a',
    component: () => import('@/views/file/index'),
    meta: {
      title: 'test',
      bodyBackground: '#fbfbfb'
    }
  }

]

const router = new Router({
  routes: constantRoutes
})

export {
  constantRoutes,
  router
}
