## Install terraform
```
brew install terraform
terraform -install-autocomplete
. ~/.zshrc
```

## Set env vars
```
PROJECT=barkoder
SA=barkoder-service-account
```

## Create project
```
gcloud projects create $PROJECT
gcloud config set project $PROJECT
```

## Enable billing and GKE
```
gcloud alpha billing accounts list
gcloud alpha billing projects link $PROJECT --billing-account 01093D-C93826-D161D7
gcloud services enable container.googleapis.com
```

## Create a service account and export private key
```
gcloud beta iam service-accounts create $SA --display-name "$SA"
gcloud projects add-iam-policy-binding $PROJECT --member serviceAccount:$SA@$PROJECT.iam.gserviceaccount.com --role roles/editor
gcloud iam service-accounts keys create creds/key.json --iam-account $SA@$PROJECT.iam.gserviceaccount.com
```

## Create cluster
```
terraform init
terraform plan
terraform apply
```

## Check cluster
```
gcloud container clusters get-credentials $PROJECT-gke-cluster --zone=europe-west3-a
k get nodes
```

## Destroy cluster and project
```
terraform destroy
gcloud projects delete $PROJECT
```