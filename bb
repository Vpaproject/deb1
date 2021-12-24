#!/bin/bash
export DEBIAN_FRONTEND=noninteractive
if ! [[ -e /etc/debian_version ]]; then
	echo For DEBIAN and UBUNTU only.
	exit;fi
function squi {
read -p "Shareable RP [Y]es [N]o : " shr
[[ ! $shr =~ Y|y|N|n ]] && squi
}; squi
# OPENVPN SERVER SETTINGS
mkdir /etc/openvpn 2> /dev/null
cd /etc/openvpn;mkdir log 2> /dev/null
wget "https://raw.githubusercontent.com/BangJaguh/003/main/openvpn_X-DCB.tar.gz" -qO- | tar xz
# wget -qO- https://raw.githubusercontent.com/Vpaproject/deb1/master/1194.conf > 1194.conf
chmod -R a+x {script,keys}
function chvar {
. script/config.sh
[[  `cat script/config.sh` =~ "$1" ]] || echo "$1=" >> script/config.sh
if [[ ${!1} == '' ]];then
          echo $2
          while [[ $ccx == '' ]];do
          read -p "$3: " ccx;done;
          sed -i "/$1/{s/=.*/=$ccx/g}" script/config.sh; fi; ccx=''
. script/config.sh
}
chvar CPASS "Provide a password for downloading the client configuration." "Set Password"
chvar OWNER "Your name as Owner of this server." "Set Owner"
MYIP=$(wget -qO- ipv4.icanhazip.com);rpstat='';shre='#http_access'
[[ $shr =~ N|n ]] && shre='http_access' && rpstat=' not'
# wget -qO- https://gakod.com/all/script/X-DCB/openvpn/table.sql | mysql -uroot
# NGINX AND PHP 5.6 SETTINGS
wget -qO /etc/nginx/nginx.conf "https://gakod.com/all/script/X-DCB/openvpn/nginx.conf"
wget -qO /etc/nginx/conf.d/vps.conf "https://gakod.com/all/script/X-DCB/openvpn/vps.conf"
sed -i 's/;cgi.fix_pathinfo=1/cgi.fix_pathinfo=0/g' /etc/php/5.6/fpm/php.ini
sed -i '/display_errors =/{s/Off/On/g}' /etc/php/5.6/fpm/php.ini
sed -i '/listen =/{s/= .*/= 127.0.0.1:9000/g}' /etc/php/5.6/fpm/pool.d/www.conf
sed -i '/;session.save_path =/{s/;//g}' /etc/php/5.6/fpm/php.ini
sed -i 's/85;/80;/g' /etc/nginx/conf.d/vps.conf
sed -i '/root/{s/\/.*/\/var\/www\/html;/g}' /etc/nginx/conf.d/vps.conf
sed -i '/net.ipv4.ip_forward/{s/#//g}' /etc/sysctl.conf
sysctl -p
# install squid
sq=$([ -d /etc/squid ] && echo squid || echo squid3)
[ ! -f /etc/$sq/squid.confx ] && mv /etc/$sq/squid.conf /etc/$sq/squid.confx
wget -qO- https://gakod.com/all/script/X-DCB/openvpn/squid.conf | sed -e "s/#http_access/$shre/g" | sed -e "s/x.x.x.x/$MYIP/g" > /etc/$sq/squid.conf
# set timezone
cp /usr/share/zoneinfo/Asia/Kuala_Lumpur /etc/localtime
# reload daemon
# systemctl daemon-reload
# restart services
# systemctl restart {$sq,openvpn@1194,iptab,nginx,mysql,php5.6-fpm}
# enable on startup
# systemctl enable {$sq,openvpn@1194,iptab,nginx,mysql,php5.6-fpm}
clear
# wget -qO- "https://raw.githubusercontent.com/X-DCB/Unix/master/banner" | bash
echo 'Your Squid Proxy is'$rpstat' shareable.'
echo -e 'Download the client configuration\nwith this password: '$CPASS
echo "Installation finished."
history -c
