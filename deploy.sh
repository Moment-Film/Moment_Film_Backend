IS_GREEN=$(docker ps | grep green) # green 컨테이너가 실행중인지 확인
DEFAULT_CONF="/etc/nginx/nginx.conf" # nginx.conf(기본 설정 파일)

# green 컨테이너가 실행 중이 아닌 경우(blue 실행중)
if [ -z $IS_GREEN ]; then
  echo "### BLUE => GREEN ###"
  
  echo "1. nginx.greem.conf => nginx.conf"
  sudo cp /etc/nginx/nginx.green.conf /etc/nginx/nginx.conf # nginx.green.conf 파일을 nginx.conf 파일로 복사(수정)
  
  #echo "2. get green image"
  #docker-compose pull app1 # app1 컨테이너의 이미지를 pull 받음.

  echo "2. green container Up"
  docker-compose up -d app1 # app1 컨테이너를 백그라운드로 실행.

  while [ 1 = 1 ]; do # 무한 루프 실행
    echo "3. green health check..."
    sleep 10 # 10초 대기

    REQUEST=$(curl http://localhost:8080) #localhost:8080 포트로 요청을 보냄.
    if [ -n "$REQUEST" ]; then # 위의 요청 결과가 반환되면(작동 중)
      echo "health check success"
      break; # 무한 루프 탈출
    fi
  done;

  echo "5. blue container down"
  docker-compose stop app2 # app2 컨테이너 내림.
else
  echo "### GREEN => BLUE ###"
  
  echo "1. nginx.blue.conf => nginx.conf" 
  sudo cp /etc/nginx/nginx.blue.conf /etc/nginx/nginx.conf

  #echo "1. get blue image"
  #docker-compose pull app2

  echo "2. blue container up"
  docker-compose up -d app2

  while [ 1 = 1 ]; do
    echo "3. blue health check..."
    sleep 10

    REQUEST=$(curl http://localhost:8081)
    if [ -n "$REQUEST" ]; then
      echo "health check success"
      break;
    fi
  done;

  echo "5. green container down"
  docker-compose stop app1
fi
