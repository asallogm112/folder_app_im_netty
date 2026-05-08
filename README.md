# folder_for_im

部署流程
cvm 1:   42.192.126.232 ,  cvm2 : 1.15.178.239


srs 部署
docker run -itd --rm --name rtmp --network="host" -p 1935:1935  -p 1985:1985 -p 8080:8080 
-v /root/rtmp.conf:/usr/local/srs/conf/rtmp.conf  ossrs/srs  ./objs/srs -c conf/rtmp.conf

Redis 部署流程
docker run -itd  --restart=always  --name redis -p 6379:6379   redis

java:
docker build -t app .
docker run -itd -p 80:80 --rm --name app app


mysqldump:
mysqldump -uroot -proot  --databases db_web db_im  --add-drop-database --add-drop-table --no-data ->no-data.sql

es:
sysctl -w vm.max_map_count=262144
docker run -p 9200:9200 -p 9300:9300   -e "discovery.type=single-node" -itd 
--name es  docker.elastic.co/elasticsearch/elasticsearch:7.5.2

# folder_app_im_netty


=========手搓 netty 纪念代码============
