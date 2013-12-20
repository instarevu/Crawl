#!/bin/bash

server=http://www.amazon.com
baseUri=/dp/

#ID
declare -a ID

ID[1]='B008D902Q2-Jewelry::Mens Watch'
ID[2]='B0043OV0DU-Pet_Supplies::Cat Food'
ID[3]='B003B6NAZ2-Grocery_&_Gourmet_Food::Organic_RedQuinoa'
ID[4]='B0001WYNKA-Indoor_Plant::Bonsai_Tree'
ID[5]='B0071NO7I0-Musical_Instrument::Guitar'
ID[6]='B00D5Q75RC-Electronics::Speaker'
ID[7]='B00FNPD1VW-Computers::Laptop'
ID[8]='B00EI7DPOO-Home_&_Kitchen::Sandwich_Maker'
ID[9]='B00G460MUC-Cell_Phone_Accessory::iPhone_Gold'
ID[10]='B0019LVFSU-Beauty::Almond_Oil'
ID[11]='B00AXN3628-Patio_Furniture::Double_Hammock'
ID[12]='B000KE6E1U-Tools_&_Home_Improvement::Socket_Set'
ID[13]='B000AUIFCA-Health_&_Personal_Care::OralB_Toothbrush'
ID[14]='B007OXK1WI-Industrial_&_Scientific::3M_Reclosable_Fastener'
ID[15]='B003YFHCKY-Office_Products::Toner_Cartridge'
ID[16]='B00DDMJ0JE-Baby::Luvs_Diaper'
ID[17]='B0041RPGQ6-Automotive::Seat_Cushion'
ID[18]='B00AJL9E5C-Shoes::Sorel_Womens_Shoes'
ID[19]='B003F82I2W-Toys::LegoBlocks'
ID[20]='B0033Y0VZ4-Appliances::Dishwashers'


for i in {1..20}
do
    IFS='-' read -ra tokens <<< "${ID[$i]}"
	url='http://www.amazon.com/dp/'${tokens}
	echo 'Downloading: '$url
	wget $url -q -O data/${ID[$i]}
done
