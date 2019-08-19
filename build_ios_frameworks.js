#!/usr/bin/env node

var exec = require('child_process').exec;

function exec_callback(error, stdout, stderr) {
  console.log(stdout);
}

exec("sh ./build_ios_frameworks.sh", exec_callback);
