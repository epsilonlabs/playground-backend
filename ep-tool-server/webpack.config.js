const path = require('path');

module.exports = {
  mode: "development",
  devtool: "inline-source-map",

  entry: {
	 highlighting: './src/main/js/highlighting.js',
  },

  output: {
    filename: 'highlighting.js',
    path: path.resolve(__dirname, 'build/webpack/public'),
    clean: true,
  },

  experiments: {
    topLevelAwait: true,
  },
};
