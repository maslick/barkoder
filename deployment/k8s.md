## Deploy to Kubernetes (GKE)

### 1. Create cluster
```
alias k=kubectl
gcloud projects create barkoder
gcloud projects list
gcloud config set project barkoder

gcloud container clusters create barkoder-cluster --zone=europe-west3-a --machine-type=n1-standard-1 --num-nodes=2 --preemptible
gcloud container clusters get-credentials barkoder-cluster --zone europe-west3-a --project barkoder
gcloud container clusters resize barkoder-cluster --size 4
```

### 2. Configure ingress
```
# install tiller
k create serviceaccount tiller --namespace kube-system
k create clusterrolebinding tiller-cluster-rule --clusterrole=cluster-admin --serviceaccount=kube-system:tiller
helm init --service-account tiller
k get pods --namespace kube-system

# Install Nginx Ingress controller
helm install stable/nginx-ingress \
  --name barkoder-nginx \
  --set rbac.create=true \
  --namespace kube-system

# Make nginx-ingress ip static (GKE)
NAMESPACE=kube-system
IP_ADDRESS=$(k describe service barkoder-nginx-nginx-ingress-controller --namespace=$NAMESPACE | grep 'LoadBalancer Ingress' | rev | cut -d: -f1 | rev | xargs)
gcloud compute addresses create k8s-static-ip --addresses $IP_ADDRESS --region europe-west3

# Add DNS A records referencing $IP_ADDRESS with your DNS registrar
```

### 3. Configure SSL certificates
```
# Install cert-manager
helm install stable/cert-manager \
  --namespace kube-system \
  --set ingressShim.defaultIssuerName=letsencrypt-prod \
  --set ingressShim.defaultIssuerKind=ClusterIssuer \
  --version v0.5.2
  
# Create a cert issuer
cat <<EOF | k apply -f -
apiVersion: certmanager.k8s.io/v1alpha1
kind: ClusterIssuer
metadata:
  name: letsencrypt-prod
  namespace: default
spec:
  acme:
    email: pavel.masloff@gmail.com
    server: https://acme-v02.api.letsencrypt.org/directory
    privateKeySecretRef:
      name: letsencrypt-prod
    http01: {}
EOF
k describe clusterissuer letsencrypt-prod
```

### 4. Deploy database
```
helm install \
  --name barkoder-db \
  stable/postgresql \
  --set "postgresqlUsername=barkoder" \
  --set "postgresqlPassword=password" \
  --set "postgresqlDatabase=barkoderdb"
```

### 5. Deploy barkoder
```
# Create ingress
k apply -f k8s-barkoder-ingress.yaml

# Create deployment and service
k apply -f k8s-barkoder-api.yaml
k apply -f k8s-barkoder-web.yaml

# Apply configuration changes (optional)
k set env deploy/barkoder-web BACKEND_URL=https://koder-api.maslick.ru
k set env deploy/barkoder-api \
  PGHOST=barkoder-db-postgresql \
  PGDATABASE=barkoderdb \
  PGUSER=barkoder \
  PGPASSWORD=password \
  SSLMODE=disable \
  CLIENT_SECRET=xxx-xx-xx
```

### 6. Run the app
```
open https://koder.maslick.ru
```
