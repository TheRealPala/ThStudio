#!/bin/bash
docker-compose up -d -f dataDB/docker-compose.yml
docker cp container_db/empty_db_dump.sql db_swe:/empty_db_dump.sql
docker exec -it db_swe mysql -u root -proot -e "source empty_db_dump.sql"
