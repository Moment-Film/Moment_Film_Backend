IS_GREEN=$(docker ps | grep green) # green 컨테이너가 실행중인지 확인
DEFAULT_CONF="/etc/nginx/nginx.conf"

if [ -z $IS_GREEN ]; then
  echo "### BLUE => GREEN ###"
  
  echo "1. get green image"
  docker-compose pull app1

  echo "2. green container Up"
  docker-compose up -d app1

  while [ 1 = 1 ]; do
    echo "3. green health check..."
    sleep 10

    REQUEST=$(curl http://localhost:8080)
    if [ -n "$REQUEST" ]; then
      echo "health check success"
      break;
    fi
  done;

  echo "4. reload nginx"
  sudo cp /etc/nginx/nginx.green.conf /etc/nginx/nginx.conf
  sudo nginx -s reload

  echo "5. blue container down"
  docker-compose stop app2
else
  echo "### GREEN => BLUE ###"

  echo "1. get blue image"
  docker-compose pull app2

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

  echo "4. reload nginx" 
  sudo cp /etc/nginx/nginx.blue.conf /etc/nginx/nginx.conf
  sudo nginx -s reload

  echo "5. green container down"
  docker-compose stop app1
fi
