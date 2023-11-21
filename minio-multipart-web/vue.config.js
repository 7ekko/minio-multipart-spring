'use strict'
const path = require('path')
// 加载配置文件
const env = process.env

function resolve (dir) {
  return path.join(__dirname, dir)
}


module.exports = {
  publicPath: './',
  outputDir: 'dist',
  assetsDir: 'static',
  lintOnSave: true,
  productionSourceMap: false,
  devServer: {
    open: true,
    host: env.VUE_APP_HOST,
    port: env.VUE_APP_PORT,
    https: false,
    hotOnly: false,
    proxy: {
      '/api': {
        target: env.VUE_APP_API_URL, // 从环境变量中读取 API 地址
        changeOrigin: true
      }
    }
  },
  pages: {
    index: {
      entry: 'src/main.js',
      template: 'public/index.html',
      filename: 'index.html'
    }
  },
  chainWebpack (config) {
    // set svg-sprite-loader
    config.module
      .rule('svg')
      .exclude.add(resolve('src/icons'))
      .end()
    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(resolve('src/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'icon-[name]'
      })
      .end()
  }
}
