#!/bin/sh
IP=$(grep 'iptables -t nat -A POSTROUTING -s 10.8.0.0/24 ! -d 10.8.0.0/24 -j SNAT --to ' $RCLOCAL | cut -d " " -f 14)
iptables -t nat -D POSTROUTING -s 10.8.0.0/24 ! -d 10.8.0.0/24 -j SNAT --to $IP
sed -i '/iptables -t nat -A POSTROUTING -s 10.8.0.0\/24 ! -d 10.8.0.0\/24 -j SNAT --to /d' $RCLOCAL
   if iptables -L -n | grep -qE '^ACCEPT'; then
   iptables -D INPUT -p $PROTOCOL --dport $PORT -j ACCEPT
   iptables -D FORWARD -s 10.8.0.0/24 -j ACCEPT
   iptables -D FORWARD -m state --state RELATED,ESTABLISHED -j ACCEPT
   sed -i "/iptables -I INPUT -p $PROTOCOL --dport $PORT -j ACCEPT/d" $RCLOCAL
   sed -i "/iptables -I FORWARD -s 10.8.0.0\/24 -j ACCEPT/d" $RCLOCAL
   sed -i "/iptables -I FORWARD -m state --state RELATED,ESTABLISHED -j ACCEPT/d" $RCLOCAL
   fi
