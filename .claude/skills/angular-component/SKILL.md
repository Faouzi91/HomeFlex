---
name: angular-component
description: >
  Generate Angular components following standalone architecture, OnPush change detection,
  signal-based state, and reactive patterns. Trigger this skill whenever the user asks to create
  an Angular component, page, view, widget, form, list, detail view, modal, or dialog.
  Also trigger for "add a screen for X", "create an Angular UI for X", or "build a form to X".
  Always apply this — never freehand Angular component generation.
---

# Angular Component Generation

## Stack Assumptions

- Angular 21+ · Standalone components (no NgModules)
- Signals (`signal`, `computed`, `effect`) for reactive state
- `OnPush` change detection — always
- Angular Material or PrimeNG (adapt imports to project)
- `inject()` function — no constructor injection
- `HttpClient` via generated OpenAPI client (not direct calls)

---

## Component Templates

### Smart (Page/Container) Component

```typescript
// <feature>/<entity>-list/<entity>-list.component.ts

import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnInit,
  signal,
  computed,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { <Entity>Service } from '../services/<entity>.service';
import { <Entity>Response } from '../models/<entity>.model';

@Component({
  selector: 'app-<entity>-list',
  standalone: true,
  imports: [CommonModule, /* Material/PrimeNG modules */],
  templateUrl: './<entity>-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class <Entity>ListComponent implements OnInit {
  private readonly <entity>Service = inject(<Entity>Service);
  private readonly router = inject(Router);

  // State
  readonly items = signal<<Entity>Response[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly currentPage = signal(0);
  readonly pageSize = signal(20);
  readonly totalElements = signal(0);

  // Derived
  readonly isEmpty = computed(() => !this.loading() && this.items().length === 0);
  readonly totalPages = computed(() => Math.ceil(this.totalElements() / this.pageSize()));

  ngOnInit(): void {
    this.loadItems();
  }

  loadItems(): void {
    this.loading.set(true);
    this.error.set(null);

    this.<entity>Service.findAll(this.currentPage(), this.pageSize()).subscribe({
      next: (page) => {
        this.items.set(page.content);
        this.totalElements.set(page.totalElements);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load <entities>. Please try again.');
        this.loading.set(false);
      },
    });
  }

  onPageChange(page: number): void {
    this.currentPage.set(page);
    this.loadItems();
  }

  navigateToDetail(id: number): void {
    this.router.navigate(['/<entities>', id]);
  }

  navigateToCreate(): void {
    this.router.navigate(['/<entities>', 'new']);
  }
}
```

### Dumb (Presentational) Component

```typescript
// <feature>/components/<entity>-card/<entity>-card.component.ts

import {
  ChangeDetectionStrategy,
  Component,
  input,
  output,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { <Entity>Response } from '../../models/<entity>.model';

@Component({
  selector: 'app-<entity>-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './<entity>-card.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class <Entity>CardComponent {
  // Inputs (signal-based)
  readonly item = input.required<<Entity>Response>();
  readonly disabled = input(false);

  // Outputs
  readonly edit = output<number>();
  readonly delete = output<number>();

  onEdit(): void {
    this.edit.emit(this.item().id);
  }

  onDelete(): void {
    this.delete.emit(this.item().id);
  }
}
```

### Reactive Form Component

```typescript
// <feature>/<entity>-form/<entity>-form.component.ts

import {
  ChangeDetectionStrategy,
  Component,
  inject,
  input,
  OnInit,
  signal,
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { <Entity>Service } from '../services/<entity>.service';

@Component({
  selector: 'app-<entity>-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, /* form controls */],
  templateUrl: './<entity>-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class <Entity>FormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly <entity>Service = inject(<Entity>Service);
  private readonly router = inject(Router);

  readonly entityId = input<number | null>(null);   // null = create mode
  readonly submitting = signal(false);
  readonly error = signal<string | null>(null);

  readonly isEditMode = computed(() => this.entityId() !== null);

  form!: FormGroup;

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      description: [''],
      status: ['ACTIVE', Validators.required],
    });

    if (this.isEditMode()) {
      this.loadEntity();
    }
  }

  private loadEntity(): void {
    this.<entity>Service.findById(this.entityId()!).subscribe({
      next: (entity) => this.form.patchValue(entity),
      error: () => this.error.set('Failed to load <entity>.'),
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    const request$ = this.isEditMode()
      ? this.<entity>Service.update(this.entityId()!, this.form.value)
      : this.<entity>Service.create(this.form.value);

    request$.subscribe({
      next: () => this.router.navigate(['/<entities>']),
      error: (err) => {
        this.error.set(err.error?.message ?? 'Operation failed.');
        this.submitting.set(false);
      },
    });
  }

  // Convenience getters for template access
  get nameControl() { return this.form.get('name')!; }
}
```

---

## File Structure

```
src/app/features/<feature>/
├── <entity>-list/
│   ├── <entity>-list.component.ts
│   └── <entity>-list.component.html
├── <entity>-form/
│   ├── <entity>-form.component.ts
│   └── <entity>-form.component.html
├── components/
│   └── <entity>-card/
│       ├── <entity>-card.component.ts
│       └── <entity>-card.component.html
├── services/
│   └── <entity>.service.ts
├── models/
│   └── <entity>.model.ts
└── <feature>.routes.ts
```

---

## Rules

### Change Detection

- Always `OnPush` — no exceptions
- Use `signal()` for mutable state, `computed()` for derived values
- Use `input()` / `output()` for component communication (Angular 17+ style)
- Never mutate objects/arrays — always replace: `this.items.set([...new])`

### Inputs/Outputs (Angular 17+)

```typescript
// Preferred — signal-based
readonly id = input.required<number>();
readonly optional = input('default');
readonly changed = output<string>();
```

### Template Patterns

```html
<!-- Loading state -->
@if (loading()) {
  <app-spinner />
} @else if (error()) {
  <app-error-banner [message]="error()!" />
} @else if (isEmpty()) {
  <app-empty-state />
} @else {
  @for (item of items(); track item.id) {
    <app-<entity>-card [item]="item" (edit)="navigateToDetail($event)" />
  }
}
```

### Form Validation Template

```html
<mat-form-field>
  <input matInput formControlName="name" placeholder="Name" />
  @if (nameControl.invalid && nameControl.touched) {
  <mat-error>
    @if (nameControl.hasError('required')) { Name is required } @if
    (nameControl.hasError('maxlength')) { Max 100 characters }
  </mat-error>
  }
</mat-form-field>
```

---

## Checklist Before Outputting

- [ ] `standalone: true` — no NgModule
- [ ] `changeDetection: ChangeDetectionStrategy.OnPush`
- [ ] State managed with `signal()`, derived state with `computed()`
- [ ] `inject()` used — no constructor injection
- [ ] `input()` / `output()` for component interface
- [ ] `@for` with `track item.id` — no `*ngFor`
- [ ] `@if` / `@else` — no `*ngIf`
- [ ] Error and loading states handled
- [ ] Form: `markAllAsTouched()` before submit guard
- [ ] No direct `HttpClient` calls — use service
