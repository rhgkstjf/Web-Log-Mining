#!/bin/bash

declare -a array
array=$(echo [SaveFolderPath]/*)
P=$(date "+%Y-%m-%d")

ssh [user]@[ip] 'mkdir [SaveFolderPath]$(date "+%Y-%m-%d")'
ssh [user]@[ip] 'rm [SaveFolderPath]/Week/*'

for A in ${array}; do
        if [[ "$A" =~ "access.log" ]]; then
                if [[ "$A" =~ "gz" || "$A" =~ "other" ]]; then
                        continue
                else
                        echo ${A:20}
                        scp -rP [port] $A [user]@[ip]:[SaveFolderPath]/$(date "+%Y-%m-%d")/${A:20}
                        scp -rP [port] $A [user]@[ip]:[SaveFolderPath]/${A:20}

                fi
        fi
done

#txt file 을 만들어 두셔야합니다. 용도는 전송한 날짜 저장을 위함입니다.
ssh -p [port] [user]@[ip] 'echo $(date "+%Y-%m-%d") >> [Folderpath]/[txt file name']
