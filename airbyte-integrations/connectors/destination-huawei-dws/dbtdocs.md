# DWS - DBT Docs for Airbyte

3) Connect to DWS, create a schema named "_airbyte_public"

2) Create ECS, install docker, install Airbyte with docker-compose, add Huawei DWS connector. (standard steps.)

3) Connect airbyte-db container and enable normalization for our connector by following these commands:
docker exec -it airbyte-db \
psql -h localhost -U docker \
-c "\c airbyte" \
-c "UPDATE public.actor_definition_version SET normalization_repository='airbyte/normalization', normalization_tag='0.4.3', supports_dbt=true, normalization_integration_type='postgres' WHERE docker_repository = 'emincanozcan/airbyte-dws-destination'"

4) Create your connection. You can use configure the settings as you want. Just ensure you selected "Normalized tabular data" under "Normalization & Transformation".

5) The first sync attempt will fail during json transformation. Wait until it finishes all attempts.

   * Go to the "job history" tab.
   
   * Find your job, click 3 dot icon, click "View logs"
   
   * In the log screen, go to the top of the file.
   
   * Find the log that contains workspace id, probably at line 1 or line 3, similar to this: /tmp/workspace/2/0/logs.log
   
   * Here the workspace is: "2/0"

6) RUN the following command, don't forget to change the workspace id (2/0) with your workspace id.

bash < (curl -sL https://gist.githubusercontent.com/emincanozcan/8588d430242d8b28f83f4c914e0dd5cb/raw/4bc4defd31c8bae0e93c23ca978cbf4cc182d85e/dws-airbyte-dbt.sh) 2/0


