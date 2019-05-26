resource "google_container_cluster" "gke-cluster" {
  name = "barkoder-gke-cluster"
  location = "europe-west3-a"
  network = "default"

  node_pool {
    name = "default-pool"
    initial_node_count = 2
    autoscaling {
      min_node_count = 2
      max_node_count = 5
    }

    node_config {
      preemptible = true
      machine_type = "n1-standard-1"
      disk_type = "pd-standard"
      disk_size_gb = "100"
    }
  }
}