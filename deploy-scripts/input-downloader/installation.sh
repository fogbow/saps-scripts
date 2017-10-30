### Docker and dependencies installation ###
apt-get update
apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common -y
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
apt-get update
apt-get install docker-ce -y

## Install and configure NFS server ##
apt-get install nfs-kernel-server -y
mkdir -p /local/exports
echo "/local/exports *(rw,insecure,no_subtree_check,async,no_root_squash)" >> /etc/exports
service nfs-kernel-server restart

### Attach disk ###
echo ";" | sfdisk /dev/xvdb
mkfs.ext4 /dev/xvdb1
mount /dev/xvdb1 /local/exports

### Create log file ###
mkdir -p /var/log/sebal-execution
touch /var/log/sebal-execution/sebal-execution.log
chmod 777 /var/log/sebal-execution/sebal-execution.log

### Run Input Downloader Conatiner ###
docker pull fogbow/downloader-deploy:v1
docker run --name input-downloader -td fogbow/downloader-deploy:v1
#docker exec input-downloader

