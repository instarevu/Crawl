#!/bin/bash
set -o xtrace

server=http://localhost:9200
index=amazon
types=(item exitem)

# DELETE INDEX
echo 'Deleting Index.... : '${index}
#sleep 30
curl -XDELETE ${server}'/'${index}'/'


# CREATE INDEX
echo 'Creating Index.... : '${index}
curl -XPUT ${server}'/'${index}  --data @../../config/index/${index}.json

#curl -XPOST ${server}'/'${index}'/_close'
#echo 'Update settings for  Index.... : '${index}
#curl -XPUT ${server}'/'${index}'/_settings'  --data @../../config/index/${index}.json
#curl -XPOST ${server}'/'${index}'/_open'

for type in "${types[@]}"
do
    echo 'Updating Mapping for type: '${type}
    curl -XPUT ${server}'/'${index}'/'${type}'/_mapping' --data @../../config/type/${type}.json
done