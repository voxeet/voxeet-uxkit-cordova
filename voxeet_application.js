#!/usr/bin/env node

module.exports = function(context) {

  var APPLICATION_CLASS = "com.voxeet.toolkit.VoxeetApplication";

  var fs = require('fs'),
      path = require('path');

  var folders = [
    'platforms/android/app/src/main/',
    'platforms/android/'
  ];

  folders
  .map(folder => path.join(context.opts.projectRoot, folder))
  .forEach(folder => {
    var manifestFile = path.join(folder, 'AndroidManifest.xml');

    if (fs.existsSync(manifestFile)) {
      console.log("Found AndroidManifest, updating...");
      fs.readFile(manifestFile, 'utf8', function (err, data) {
        if (err) {
          throw new Error('Unable to find AndroidManifest.xml: ' + err);
        }

        if (data.indexOf(APPLICATION_CLASS) == -1) {
          var result = data.replace(/<application/g, '<application android:name="' + APPLICATION_CLASS + '"');
          fs.writeFile(manifestFile, result, 'utf8', function (err) {
            if (err) throw new Error('Unable to write into AndroidManifest.xml: ' + err);
          })
        }
      });
    }
  });
};
