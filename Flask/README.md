# Flask Install

### python version 변경
### 도움받은 링크
### https://codechacha.com/ko/change-python-version/
### Ubuntu 16.04 LTS 에서 python version 을출력하면 python2 version입니다.
### 기존의 있던 python 3.5 버전 또는 2.7 버전을 쓰셔도됩니다.
### python 3 version 을 default 로 설정합니다.
### update-alternatives --config python 옵션을 통해 default python version을 바꿉니다.
### 초기에 설정을 하지않았다면 아무것도 등록되어있지 않습니다.
```sh
#밑에 명령어에서 검색되는 python 
ls /usr/bin/ | grep python
```
![Inked캡처_LI](https://user-images.githubusercontent.com/44472886/103400745-9d80d180-4b89-11eb-8e50-8e69f7ff427d.jpg)

```sh
# 밑에 명령어가 error : no alternatives for python 뜰 경우 아무것도 등록되어있지않음
sudo update-alternatives --config python

sudo update-alternatives --install /usr/bin/python python /usr/bin/python3.5

#자신이 선택한 python version이 나옵니다.
sudo update-alternatives --config python
```

### python 3.7 Install
### 도움 링크
### https://linuxize.com/post/how-to-install-python-3-7-on-ubuntu-18-04/

```sh
sudo apt update
sudo apt install software-properties-common

sudo add-apt-repository ppa:deadsnakes/ppa
#ENTER 를 눌러 계속하라고 뜬다면 ENTER를 누르세요

sudo apt install python3.7

python3.7 --version
```

### pip 3 Install

```sh
sudo apt-get install python3-pip
```

### Flask Install
```sh
sudo pip3 install flask
```


