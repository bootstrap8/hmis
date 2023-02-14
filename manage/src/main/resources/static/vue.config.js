const {defineConfig} = require('@vue/cli-service')
const webpack = require('webpack')
module.exports = defineConfig({
  transpileDependencies: true,
  outputDir: 'ui',
  publicPath: './',
  configureWebpack: {
    plugins: [
      new webpack.ProvidePlugin({
        Buffer: ['buffer', 'Buffer'],
      }),
    ],
    resolve: {
      fallback: {
        "path": require.resolve("path-browserify"),
        "https": require.resolve("https-browserify"),
        "http": require.resolve("stream-http"),
        "buffer": require.resolve("buffer"),
        fs: false
      },
    },
  }
})
