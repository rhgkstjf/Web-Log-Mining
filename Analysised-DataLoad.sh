#!/bin/bash
P=$(date "+%Y-%m-%d")
scp -P [port] hadoop@[ip]:/home/hadoop/AutoLab/Week_DF/$P/* /home/[web-server-username]/Data/
echo '[web server sudo passwd]' | sudo -S mv /home/[web-server-username]/Data/* /var/www/html/FLASKAPPS/Data/$P/
