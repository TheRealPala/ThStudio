#!/bin/bash
docker-compose -f container_db/docker-compose.yml up -d 
docker cp container_db/th_studio_empty_dump.sql db_swe:/th_studio_empty_dump.sql
docker cp container_db/th_studio_test_empty_dump.sql db_swe:/th_studio_test_empty_dump.sql
sleep 0.5
docker exec -it db_swe mysql -u root -proot -e "source th_studio_empty_dump.sql"
docker exec -it db_swe mysql -u root -proot -e "source th_studio_test_empty_dump.sql"
