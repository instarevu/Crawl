#!/bin/bash


items=(B008D902Q2 B0043OV0DU B003B6NAZ2 B0001WYNKA B0071NO7I0 B00D5Q75RC B00FNPD1VW B00EI7DPOO B00G460MUC B0019LVFSU)
items+=(B00AXN3628 B000KE6E1U B000AUIFCA B007OXK1WI B003YFHCKY B00DDMJ0JE B0041RPGQ6 B00AJL9E5C B003F82I2W B0033Y0VZ4)
items+=(B00B8YSQOE B008RLUY46 B00121PZZG B001OOLF82 B007UZNS5W B005GSYXHW B003UEMOWA B005IS7PDO B00850F5L6 B000EFMLQ2)
items+=(B00407S11Y B000CSI69C B00A39FRII B00746LOQW B000NGMTOG B008KZW13Q B007PBOWK6 B003ZUN2PW B00FQCQE20 B008A3KFB8)
items+=(B000JQM1DE B008RR9ZIG B0009F3POY B004OQP3O4 B000H7YCE6 B001706UPG B00005AXIV B001PMJUKI B00005LEN4 B008X099PQ)

dir=../../../src/test/resources/test-data/amazon/
mkdir -p $dir
echo 'Removing files under : '$dir
rm -rf ${dir}/*

for item in "${items[@]}"
do
    files=$(ls -l ${dir} | wc -l)
	url='http://www.amazon.com/dp/'${item}
	echo echo $(date)' Downloaded: '$files'  |   Downloading: '$url
	wget $url -q -O ${dir}/${item}
done
