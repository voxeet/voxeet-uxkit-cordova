module.exports = function(context) {
  var fs = require('fs');
  var Q = require('q');
  var exec = require('child_process').exec;
  var deferral = new Q.defer();

  const dir = context.opts.plugin.dir;
  const skip = "true" === process.env.VOXEET_SKIP_IOS_BUILD;

  function exec_callback(error, stdout, stderr) {
    error && console.log(error);
    stdout && console.log(stdout);
    stderr && console.log(stderr);
    deferral.resolve();
  }
  
  if (!skip && fs.existsSync(dir)) {
    exec(`carthage update --platform ios --project-directory ${dir}/src/ios`, exec_callback);
  } else {
    console.log("skipping script installation...");
    deferral.resolve();
  }

  return deferral.promise;
}