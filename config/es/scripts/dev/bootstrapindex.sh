#!/bin/bash
server=http://localhost:9200

# DELETE INDEX
echo 'Deleting Index Amazon....'
sleep 30
curl -XDELETE ${server}'/amazon/'



# CREATE INDEX
echo 'Creating Index Amazon....'
curl -XPUT ${server}'/amazon'

echo 'Updating Mapping for type:item'
curl -XPUT ${server}'/amazon/item/_mapping' --data @../../config/mappings/item.json