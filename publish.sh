echo Transpiling...
tsc || exit 1


echo Setting version in code...
VERSION=$(node -e "console.log(require('./package.json').version);")
echo Current version = $VERSION
sed -i '' "s/___CORDOVA_VERSION___/$VERSION/g" src/android/com/voxeet/toolkit/VoxeetCordova.java

echo building and publishing...
npm publish || exit 1

echo Reverting modification in VoxeetCordova.java...
git checkout src/android/com/voxeet/toolkit/VoxeetCordova.java

echo done
