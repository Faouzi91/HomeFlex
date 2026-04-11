variable "aws_region" {
  description = "AWS region"
  default     = "eu-west-1"
}

variable "project_name" {
  description = "Project name for resource tagging"
  default     = "homeflex"
}

variable "environment" {
  description = "Deployment environment"
  default     = "production"
}

variable "vpc_cidr" {
  description = "VPC CIDR block"
  default     = "10.0.0.0/16"
}

variable "db_password" {
  description = "PostgreSQL master password"
  sensitive   = true
}
