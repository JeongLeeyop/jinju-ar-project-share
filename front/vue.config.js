const path = require('path');

const devPort = 3001;

function resolve(dir) {
  return path.join(__dirname, dir);
}

module.exports = {
  lintOnSave: false,
  publicPath: process.env.NODE_ENV === 'production' ? '/' : '/',
  outputDir: 'dist',
  transpileDependencies: ['vue-daum-postcode', 'vue-js-modal'],
  devServer: {
    disableHostCheck: true,
    port: devPort,
    proxy: {
      [process.env.VUE_APP_COMMON_API]: {
        target: `${process.env.VUE_APP_TARGET}/api`,
        changeOrigin: true,
        pathRewrite: {
          [`^${process.env.VUE_APP_COMMON_API}`]: '',
        },
      },
      [process.env.VUE_APP_CLIENT_API]: {
        target: `${process.env.VUE_APP_TARGET}/api/client`,
        changeOrigin: true,
        pathRewrite: {
          [`^${process.env.VUE_APP_CLIENT_API}`]: '',
        },
      },
      [process.env.VUE_APP_OAUTH_API]: {
        target: `${process.env.VUE_APP_TARGET}/oauth`,
        changeOrigin: true,
        pathRewrite: {
          [`^${process.env.VUE_APP_OAUTH_API}`]: '',
        },
      },
    },
  },
  configureWebpack: {
    resolve: {
      alias: {
        '@': resolve('src'),
      },
    },
  },
};
