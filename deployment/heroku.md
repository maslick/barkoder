## Heroku
```
heroku login
git clone https://github.com/maslick/barkoder.git koder && cd koder
heroku create my-barkoder
heroku addons:create heroku-postgresql:hobby-dev
heroku config:set \
  KC_ENABLED=true \
  KCHOST=https://keycloak.io \
  REALM=barkoder \
  CLIENT=barkoder-backend \
  CLIENT_SECRET=xxxxxxx-xxxx-xxxx-xxxx-xxxxxxx \
  CLIENT_ROLE=craftroom
git push heroku master
```