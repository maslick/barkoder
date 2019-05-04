# Db migration

## Export from Heroku
```
heroku pg:backups:capture -a barkoder
heroku pg:backups:download -a barkoder
pg_restore latest.dump > db_dump.sql
```

## Import to k8s
```
k port-forward svc/barkoder-db-postgresql 5433:5432
PG_PASSWORD="password" psql --port 5433 --host 127.0.0.1 -U barkoder -d barkoderdb
psql (11.1, server 10.7)
Type "help" for help.

barkoderdb=> \i items-dump.sql
```