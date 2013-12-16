#!/bin/bash

server=http://www.amazon.com
baseUri=/dp/

#ID
declare -a ID
ID[1]=B00F4MEK6E
ID[2]=B0013DQY4O
ID[3]=B007MMA4NW
ID[4]=B005DLCJX2
ID[5]=B000LP2MGY
ID[6]=B004YHKVCM
ID[7]=B001ASB5XC
ID[8]=B0053X62GK
ID[9]=B00009R66F
ID[10]=B0043H4JXU
ID[11]=0385537131
ID[12]=B003VNKNF0



#NAME
NAME[1]=Notebook
NAME[2]=Household
NAME[3]=Jeans
NAME[4]=Diaper
NAME[5]=Shoe
NAME[6]=Laundry
NAME[7]=Cookware
NAME[8]=Toy
NAME[9]=Appliance
NAME[10]=Sports
NAME[11]=Book
NAME[12]=MemoryCard


for i in {1..12}
do
	url='http://www.amazon.com/dp/'${ID[$i]}
	echo 'Downloading for  '${NAME[$i]}' -  '$url
	wget $url -q -O data/${NAME[$i]}
done
