# Multi-Region Deployment Strategy (Conceptual)

# Secondary Region Provider
provider "aws" {
  alias  = "secondary"
  region = "us-east-1"
}

# RDS Global Cluster for Cross-Region Persistence
resource "aws_rds_global_cluster" "main" {
  global_cluster_identifier = "${var.project_name}-global-db"
  engine                    = "aurora-postgresql"
  engine_version            = "16.1"
  database_name             = "homeflex"
}

# Read Replica in Secondary Region
resource "aws_db_instance" "replica" {
  provider               = aws.secondary
  identifier             = "${var.project_name}-db-replica"
  replicate_source_db    = aws_db_instance.main.arn
  instance_class         = "db.t4g.medium"
  skip_final_snapshot    = true
  
  tags = {
    Name        = "${var.project_name}-db-replica"
    Environment = var.environment
  }
}

# Route53 Latency-Based Routing
resource "aws_route53_record" "api" {
  zone_id = var.route53_zone_id
  name    = "api.homeflex.com"
  type    = "A"

  latency_routing_policy {
    region = var.aws_region
  }

  set_identifier = "primary"
  alias {
    name                   = aws_lb.main.dns_name
    zone_id                = aws_lb.main.zone_id
    evaluate_target_health = true
  }
}

resource "aws_route53_record" "api_secondary" {
  provider = aws.secondary
  zone_id  = var.route53_zone_id
  name     = "api.homeflex.com"
  type     = "A"

  latency_routing_policy {
    region = "us-east-1"
  }

  set_identifier = "secondary"
  alias {
    name                   = "secondary-alb-dns-placeholder"
    zone_id                = "secondary-zone-id-placeholder"
    evaluate_target_health = true
  }
}
