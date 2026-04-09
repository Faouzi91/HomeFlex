---
name: angular-service
description: >
  Generate Angular services for HTTP communication, using the OpenAPI-generated client or
  HttpClient with proper typing, error handling, and RxJS patterns. Trigger this skill whenever
  the user asks to create an Angular service, HTTP client, API integration, data service,
  or says "call the backend from Angular", "fetch X from the API", or "integrate Angular with X endpoint".
  Always apply this — never freehand Angular service generation.
---

# Angular Service Generation

## Stack Assumptions

- Angular 21+ · `HttpClient` · RxJS 7+
- OpenAPI Generator produces typed clients (preferred) — use them when available
- Services are `providedIn: 'root'` unless feature-scoped
- `inject()` — no constructor injection
- Error handling centralized via HTTP interceptor

---

## Option A: Wrapper over OpenAPI-Generated Client (Preferred)

When `openapi-generator-cli` is configured, use the generated service as a dependency:

```typescript
// features/<feature>/services/<entity>.service.ts

import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { <Entity>ControllerService } from '../../../api/services';   // generated
import {
  <Entity>Response,
  <Entity>CreateRequest,
  <Entity>UpdateRequest,
  PageResponse,
} from '../models/<entity>.model';

@Injectable({ providedIn: 'root' })
export class <Entity>Service {
  private readonly api = inject(<Entity>ControllerService);

  findAll(page = 0, size = 20): Observable<PageResponse<<Entity>Response>> {
    return this.api.<entity>ControllerFindAll({ page, size });
  }

  findById(id: number): Observable<<Entity>Response> {
    return this.api.<entity>ControllerFindById({ id });
  }

  create(request: <Entity>CreateRequest): Observable<<Entity>Response> {
    return this.api.<entity>ControllerCreate({ body: request });
  }

  update(id: number, request: <Entity>UpdateRequest): Observable<<Entity>Response> {
    return this.api.<entity>ControllerUpdate({ id, body: request });
  }

  delete(id: number): Observable<void> {
    return this.api.<entity>ControllerDelete({ id });
  }
}
```

---

## Option B: HttpClient Direct (when no generated client)

```typescript
// features/<feature>/services/<entity>.service.ts

import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  <Entity>Response,
  <Entity>CreateRequest,
  <Entity>UpdateRequest,
  PageResponse,
} from '../models/<entity>.model';

@Injectable({ providedIn: 'root' })
export class <Entity>Service {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/v1/<entities>`;

  findAll(page = 0, size = 20): Observable<PageResponse<<Entity>Response>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageResponse<<Entity>Response>>(this.baseUrl, { params });
  }

  findById(id: number): Observable<<Entity>Response> {
    return this.http.get<<Entity>Response>(`${this.baseUrl}/${id}`);
  }

  create(request: <Entity>CreateRequest): Observable<<Entity>Response> {
    return this.http.post<<Entity>Response>(this.baseUrl, request);
  }

  update(id: number, request: <Entity>UpdateRequest): Observable<<Entity>Response> {
    return this.http.put<<Entity>Response>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  uploadFile(id: number, file: File): Observable<<Entity>Response> {
    const form = new FormData();
    form.append('file', file, file.name);
    return this.http.post<<Entity>Response>(`${this.baseUrl}/${id}/files`, form);
  }
}
```

---

## Model Types

```typescript
// features/<feature>/models/<entity>.model.ts

export interface <Entity>Response {
  id: number;
  name: string;
  status: <Entity>Status;
  createdAt: string;   // ISO 8601
  updatedAt: string;
}

export interface <Entity>CreateRequest {
  name: string;
  status: <Entity>Status;
  description?: string;
}

export interface <Entity>UpdateRequest {
  name?: string;
  status?: <Entity>Status;
  description?: string;
}

export type <Entity>Status = 'ACTIVE' | 'INACTIVE' | 'PENDING';

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}
```

---

## HTTP Interceptors

### Auth Interceptor

```typescript
// core/interceptors/auth.interceptor.ts

import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(AuthService).getToken();
  if (token) {
    req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }
  return next(req);
};
```

### Error Interceptor

```typescript
// core/interceptors/error.interceptor.ts

import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { NotificationService } from '../services/notification.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const notify = inject(NotificationService);

  return next(req).pipe(
    catchError((error) => {
      switch (error.status) {
        case 401:
          router.navigate(['/login']);
          break;
        case 403:
          notify.error('You do not have permission to perform this action.');
          break;
        case 404:
          notify.error('Resource not found.');
          break;
        case 422:
          notify.error(error.error?.message ?? 'Validation error.');
          break;
        case 500:
          notify.error('Server error. Please try again later.');
          break;
      }
      return throwError(() => error);
    })
  );
};
```

Register in `app.config.ts`:

```typescript
provideHttpClient(withInterceptors([authInterceptor, errorInterceptor]));
```

---

## RxJS Patterns

### Cancel on Destroy

```typescript
private readonly destroy$ = new Subject<void>();

ngOnInit(): void {
  this.<entity>Service.findAll().pipe(
    takeUntilDestroyed(this.destroyRef)  // Angular 16+ preferred
  ).subscribe(...);
}
```

### Prefer `toSignal` in components

```typescript
// Convert Observable to Signal for template use
readonly items = toSignal(this.<entity>Service.findAll(), { initialValue: null });
```

---

## environment.ts Pattern

```typescript
// environments/environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',
};

// environments/environment.prod.ts
export const environment = {
  production: true,
  apiUrl: '/api', // reverse proxy in prod
};
```

---

## Checklist Before Outputting

- [ ] `providedIn: 'root'` (or feature-scoped if one screen only)
- [ ] `inject()` — no constructor
- [ ] All methods return typed `Observable<T>` — never `any`
- [ ] Model interfaces defined in `models/` file
- [ ] `HttpParams` for query params (not string interpolation)
- [ ] Auth + error interceptors registered in `app.config.ts`
- [ ] `environment.apiUrl` used — never hardcoded URL
- [ ] `takeUntilDestroyed` or `toSignal` used in components to avoid memory leaks
