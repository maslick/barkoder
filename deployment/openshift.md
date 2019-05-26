## Deploy to Openshift
1. Create a new project
```
oc new-project test
```

2. Deploy database
```
oc new-app -f https://raw.githubusercontent.com/openshift/origin/master/examples/db-templates/postgresql-persistent-template.json \
  -p DATABASE_SERVICE_NAME=barkoder-db \
  -p POSTGRESQL_USER=admin \
  -p POSTGRESQL_PASSWORD=password \
  -p POSTGRESQL_DATABASE=barkoderdb

```

3. Deploy the service
```
oc new-app maslick/barkoder
```

4. Set env. variables
```
oc set env dc/barkoder \
  PGHOST=barkoder-db \
  PGDATABASE=barkoderdb \
  PGUSER=admin \
  PGPASSWORD=password \
  KC_ENABLED=false \
  KCHOST=https://keycloak.io \
  REALM=barkoder \
  CLIENT=barkoder-backend \
  CLIENT_SECRET=xxxxxxx-xxxx-xxxx-xxxx-xxxxxxx \
  CLIENT_ROLE=craftroom
```

5. Expose route
```
oc expose svc/barkoder --port=8080
open http://barkoder-test.apps.example.com/items
```