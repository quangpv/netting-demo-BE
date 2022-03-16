function build() {
  ./gradlew bootJar
}

function killProcess() {
  ssh $pi <<EOF
ps ax | grep netting-backend.jar | awk '{ print \$1 }' | xargs sudo kill
EOF
}

function upload() {
  scp ./build/libs/netting-backend.jar $pi:~/netting-demo-be
}
function deploy() {
  ssh $pi "cd ~/netting-demo-be && nohup java -jar netting-backend.jar &"
}
shouldUploadResult=0

function checkToUpload() {
  fromLocal=$(md5sum build/libs/netting-backend.jar | awk '{print $1}')
  fromServer=$(ssh $pi 'md5sum ~/netting-demo-be/netting-backend.jar' | awk '{print $1}')
  if [ "$fromLocal" != "$fromServer" ]; then
    shouldUploadResult=1
  fi
}

build
killProcess
checkToUpload
if [ $shouldUploadResult == 1 ]; then
  upload
fi
deploy
