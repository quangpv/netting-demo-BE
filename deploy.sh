function killProcess() {
  ssh $pi <<EOF
ps ax | grep netting-backend.jar | awk '{ print \$1 }' | xargs sudo kill
EOF
}

function pullSource() {
  ssh $pi "[ -d .git ] || git clone git@github.com:quangpv/netting-demo-BE.git"
  ssh $pi "cd ~/netting-demo-BE && git checkout main && git pull"
}

function deploy() {
  ssh $pi "cd ~/netting-demo-BE && nohup ./gradlew deploy &"
}

killProcess
pullSource
deploy
