#!/bin/bash
docker-compose -f container_db/docker-compose.yml up -d 
docker cp container_db/empty_db_dump.sql db_swe:/empty_db_dump.sql
sleep 0.5
docker exec -it db_swe mysql -u root -proot -e "source empty_db_dump.sql"
