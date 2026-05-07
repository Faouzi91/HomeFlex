# Software Requirements Specification (SRS)

## HomeFlex — Real Estate Rental Marketplace Platform

**Version:** 3.5
**Date:** April 18, 2026
**Classification:** Confidential
**Status:** 100% Implemented — Technical Requirements Met (Stripe Live, Reactive Unread Counts, Persistent Read State)

---

## Document Control

| Version | Date       | Author        | Description                                                                                                                                               |
| ------- | ---------- | ------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1.0     | 2024-XX-XX | Original Team | Initial real estate platform                                                                                                                              |
| 2.0     | 2026-03-24 | Architect     | Full enterprise-grade overhaul + vehicle rentals                                                                                                          |
| 3.0     | 2026-04-11 | Architect     | 100% Technical Parity: ELK, Maps, i18n, AI, Blockchain, Redlock, GDPR, PII Encryption, Terraform, SLOs, Flutter native.                                   |
| 3.1     | 2026-04-14 | Architect     | Admin console separation, role-based profiles, notification preferences, user/property/report management pages.                                           |
| 3.2     | 2026-04-15 | Architect     | Security Hardening: AES-GCM PII encryption, mandatory env-based secret management, infrastructure isolation.                                              |
| 3.3     | 2026-04-17 | Architect     | CI hardening, dead-code removal, admin guard tests, SRS test inventory update.                                                                            |
| 3.4     | 2026-04-17 | Architect     | Stripe payment flow (client secret, confirmCardPayment), reactive unread counts, landlord booking view, home page fix.                                    |
| 3.5     | 2026-04-18 | Architect     | Unread chat count persists across refresh (repo query fix), robust avatar upload (null-safe + 50M nginx limit), overview stats filter to active bookings. |

---

# 1. Introduction

## 1.1 Purpose

This document defines the complete technical and functional requirements for **HomeFlex**, a unified rental marketplace. Every section is marked with 🟢 to indicate full implementation parity with the v3.0+ codebase.

## 1.2 Implementation Summary

- 🟢 **Core:** Property/Vehicle CRUD, Bookings, Real-time Chat, Reviews, Admin Console (separated login, users, properties, reports).
- 🟢 **Enterprise:** ELK Stack, Distributed Caching (Redis), Distributed Locking (Redlock), outbox relay.
- 🟢 **Search:** Elasticsearch full-text, geo-distance, and faceted amenity search.
- 🟢 **Security:** httpOnly cookies, CSRF (XSRF-TOKEN), PII encryption (AES-256-GCM), account locking, infrastructure isolation.
- 🟢 **Globalization:** i18n (EN, FR, ES, AR), RTL support, multi-currency engine.
- 🟢 **Innovation:** AI Pricing, Blockchain Leases, Insurance Marketplace, PDF Receipts.
- 🟢 **Mobile:** Flutter 3.8 native builds for iOS and Android.
- 🟢 **DevOps:** Terraform (AWS), GitHub Actions CI/CD, Prometheus/Grafana SLO alerting.

---

# 2. System Overview & Vision

HomeFlex is the **unified rental marketplace** for properties and vehicles, featuring a shared trust network and enterprise-grade resiliency.

---

# 3. Technology Stack 🟢

| Layer           | Technology                   | Status         |
| --------------- | ---------------------------- | -------------- |
| Backend runtime | Java 21 + Spring Boot 4      | 🟢 Implemented |
| Primary DB      | PostgreSQL 16                | 🟢 Implemented |
| Cache / Lock    | Redis 7 + Redlock (Redisson) | 🟢 Implemented |
| Search engine   | Elasticsearch 9              | 🟢 Implemented |
| Event broker    | RabbitMQ 3                   | 🟢 Implemented |
| Object storage  | AWS S3 + CloudFront          | 🟢 Implemented |
| Frontend        | Angular 21 + Tailwind 4      | 🟢 Implemented |
| Mobile          | Flutter 3.8 (Native)         | 🟢 Implemented |
| Payments        | Stripe (Connect + Identity)  | 🟢 Implemented |
| Notifications   | FCM + SES + Twilio           | 🟢 Implemented |
| Observability   | Prometheus + Grafana + ELK   | 🟢 Implemented |
| Infrastructure  | Terraform + ECS Fargate      | 🟢 Implemented |

---

# 4. System Architecture 🟢

- **Transactional Outbox:** Guaranteed consistency for search indexing and notifications.
- **Micro-feature package structure:** High maintainability and clear domain boundaries.
- **Global Strategy:** Terraform-ready multi-region global RDS clusters.

---

# 5. Functional Requirements 🟢

## 5.1 User Management 🟢

- **FR-100:** User Registration (including Twilio OTP verification) 🟢
- **FR-101:** Authentication (httpOnly cookies + Account locking) 🟢
- **FR-102:** User Profile (completeness score + soft delete) 🟢
- **FR-103:** KYC (Stripe Identity) 🟢
- **FR-104:** Role-based Profile Views (Tenant, Landlord, Admin — each with role-specific sections) 🟢
- **FR-105:** Notification Preferences (email, push, SMS toggles per user) 🟢
- **FR-106:** Avatar Upload (profile picture with camera overlay) 🟢

## 5.2 Listing Management 🟢

- **FR-200:** Properties (images, videos, amenities, resized media) 🟢
- **FR-201:** Vehicles (CRUD, condition reports, availability) 🟢
- **FR-202:** Search (Elasticsearch, Leaflet maps, comparison) 🟢

## 5.3 Booking Management 🟢

- **FR-300:** Booking Lifecycle (Escrow payments, Confirmed status) 🟢
- **FR-301:** Modifications (Real-time date changes + re-approval) 🟢
- **FR-302:** Post-Booking (Review prompts + Dispute resolution) 🟢
- **FR-303:** Landlord booking view — received bookings per property with approve/reject actions 🟢
- **FR-304:** Stripe client secret returned at booking creation; frontend confirms payment via `stripe.confirmCardPayment` 🟢

## 5.4 Payment & Finance 🟢

- **FR-400:** Stripe Connect (Escrow, Payouts, Commissions) 🟢
- **FR-401:** Automated Finance (PDF Receipts + Multi-currency) 🟢
- **FR-402:** Public `/api/v1/config` endpoint exposes Stripe publishable key at runtime (no build-time key bundling) 🟢

## 5.5 Communication 🟢

- **FR-500:** Real-time Chat (Read receipts + typing indicators) 🟢
- **FR-501:** Notifications (Granular user preferences, mark-read syncs header bell badge) 🟢
- **FR-502:** Header notification badge reactively combines unread notification + message counts via NgRx Signal computed() 🟢

## 5.6 Admin Console 🟢

- **FR-600:** Separate Admin Login (restricted portal at `/admin/login`, non-admin users rejected) 🟢
- **FR-601:** Admin Dashboard (analytics: total users, properties, bookings, pending items) 🟢
- **FR-602:** User Management (paginated list, search/filter, suspend/activate actions) 🟢
- **FR-603:** Property Approvals (pending property review with approve/reject workflow, rejection reason required) 🟢
- **FR-604:** Report Management (flagged content review, resolve with optional notes) 🟢
- **FR-605:** Admin Settings (profile management, password change, notification preferences) 🟢
- **FR-606:** Admin Route Guard (role-based access control, consumer shell hidden on admin routes) 🟢

---

# 6. Non-Functional Requirements 🟢

- **Performance:** Sub-200ms API response time via Redis caching.
- **Availability:** Multi-AZ RDS and ECS auto-scaling.
- **Security:** App-level PII encryption and GDPR automated export/erasure.
- **Testing:** 100% coverage foundations (Testcontainers, Playwright, k6).

---

# 18. Risk Analysis 🟢

All previously identified risks (R1-R10) have been mitigated via implemented technical controls (Escrow for R1, Multi-AZ for R2, Rate Limiting for R3, PII Encryption for R4, KYC for R5, etc.).

---

**Final Document v3.5 (2026-04-18)**
**Implementation Status: 100% Complete.**
