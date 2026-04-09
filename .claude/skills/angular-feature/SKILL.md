---
name: angular-feature
description: >
  Scaffold a complete Angular feature module: routes, list page, form page, service, models,
  and folder structure — all wired up and ready to use. Trigger this skill whenever the user asks
  to scaffold a full feature, generate a CRUD module, "create everything for X", "set up the
  Angular side for X", or needs a complete end-to-end feature with routing and components.
  Always apply this — it orchestrates the other Angular skills.
---

# Angular Feature Scaffolding

This skill generates the complete file tree and wires everything together.
For the content of individual files, defer to:

- `angular-component` skill → component implementation
- `angular-service` skill → service + model implementation

---

## Feature Folder Structure

```
src/app/features/<feature>/
├── <feature>.routes.ts                  ← Lazy-loaded routes
├── models/
│   └── <entity>.model.ts                ← Interfaces + types
├── services/
│   └── <entity>.service.ts              ← HTTP service
├── <entity>-list/
│   ├── <entity>-list.component.ts
│   └── <entity>-list.component.html
├── <entity>-form/
│   ├── <entity>-form.component.ts
│   └── <entity>-form.component.html
└── components/                          ← Dumb/shared components
    └── <entity>-card/
        ├── <entity>-card.component.ts
        └── <entity>-card.component.html
```

---

## Routes File

```typescript
// src/app/features/<feature>/<feature>.routes.ts

import { Routes } from '@angular/router';

export const <FEATURE>_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./<entity>-list/<entity>-list.component').then(
        (m) => m.<Entity>ListComponent
      ),
    title: '<Entity> List',
  },
  {
    path: 'new',
    loadComponent: () =>
      import('./<entity>-form/<entity>-form.component').then(
        (m) => m.<Entity>FormComponent
      ),
    title: 'New <Entity>',
  },
  {
    path: ':id',
    loadComponent: () =>
      import('./<entity>-form/<entity>-form.component').then(
        (m) => m.<Entity>FormComponent
      ),
    title: 'Edit <Entity>',
  },
];
```

## Register in App Routes

```typescript
// src/app/app.routes.ts

export const routes: Routes = [
  // ...existing routes
  {
    path: '<entities>',
    loadChildren: () =>
      import('./features/<feature>/<feature>.routes').then(
        (m) => m.<FEATURE>_ROUTES
      ),
  },
];
```

---

## Sidebar / Navigation Entry

```typescript
// In your nav config or sidebar component
{
  label: '<Entities>',
  icon: 'list',                   // Material icon name
  route: '/<entities>',
  permission: '<ENTITY>_READ',    // Optional: guard-based visibility
}
```

---

## Generation Order

When generating a complete feature, always produce files in this order:

1. **Models** (`models/<entity>.model.ts`) — interfaces first, everything depends on them
2. **Service** (`services/<entity>.service.ts`) — HTTP calls
3. **List component** — smart container with table/grid
4. **Form component** — create + edit (mode driven by `:id` param)
5. **Dumb components** — card, badge, status chip, etc.
6. **Routes** (`<feature>.routes.ts`) — wire everything
7. **App routes update** — register lazy route

---

## Route Guard (when feature needs auth)

```typescript
// core/guards/auth.guard.ts (usually exists already)
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.isAuthenticated() ? true : inject(Router).createUrlTree(['/login']);
};

// Apply in feature routes:
{
  path: '<entities>',
  canActivate: [authGuard],
  loadChildren: () => import('./features/<feature>/<feature>.routes').then(m => m.<FEATURE>_ROUTES),
}
```

---

## Checklist for Full Feature

- [ ] `models/<entity>.model.ts` with Request, Response, and enum types
- [ ] `services/<entity>.service.ts` with all CRUD methods
- [ ] `<entity>-list` smart component with pagination, loading, error states
- [ ] `<entity>-form` component handling both create and edit via `input()` id
- [ ] At least one dumb component (`<entity>-card` or similar)
- [ ] `<feature>.routes.ts` with lazy-loaded components
- [ ] Feature registered in `app.routes.ts`
- [ ] All components are `standalone: true` + `OnPush`
- [ ] No `NgModule` anywhere
- [ ] Navigation/sidebar entry added (remind user if not done)
