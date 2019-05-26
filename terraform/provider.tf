provider "google" {
  credentials = "${file("./creds/key.json")}"
  project     = "barkoder"
  region      = "europe-west3"
}