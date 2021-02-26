const path = require('path');
const webpack = require("webpack");

module.exports = {
    entry: './lib/index.ts',
    module: {
      rules: [
        {
          test: /\.tsx?$/,
          use: 'ts-loader',
          exclude: /node_modules/,
        },
      ],
    },
    resolve: {
      extensions: ['.tsx', '.ts', '.js'],
    },
    output: {
      filename: 'index.js',
      path: path.resolve(__dirname, 'www'),
    },
  };

/*
module.exports = {
    entry: './lib/index.ts',
    output: {
      path: path.resolve(__dirname, 'www'),
      filename: 'index.js',
      libraryTarget: 'umd',
      library: 'VoxeetSDK',
      umdNamedDefine: true
    },
    resolve: {
      extensions: ['.ts', '.tsx', '.js']
    },
    devtool: 'source-map',
//    plugins: [
//      new webpack.optimize.UglifyJsPlugin({
//        minimize: true,
//        sourceMap: true,
//        include: /\.min\.js$/,
//      })
//    ],
    module: {
      rules: [{
        test: /\.ts?$/,
        loader: 'awesome-typescript-loader',
        exclude: /node_modules/,
//        query: {
//          declaration: false,
//        }
      }]
    }
  }
  */