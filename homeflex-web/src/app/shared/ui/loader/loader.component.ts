import { Component } from '@angular/core';

@Component({
  selector: 'app-loader',
  template: `
    <div
      class="fixed inset-0 z-[100] flex items-center justify-center bg-white/60 backdrop-blur-[2px]"
    >
      <div class="flex flex-col items-center gap-4">
        <div class="relative flex h-16 w-16 items-center justify-center">
          <div
            class="absolute h-full w-full animate-spin rounded-full border-4 border-slate-100 border-t-brand-500"
          ></div>
          <div class="h-8 w-8 rounded bg-brand-500/10 flex items-center justify-center">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="2.5"
              stroke="currentColor"
              class="h-4 w-4 text-brand-600"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="m2.25 12 8.954-8.955c.44-.439 1.152-.439 1.591 0L21.75 12M4.5 9.75v10.125c0 .621.504 1.125 1.125 1.125H9.75v-4.875c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125V21h4.125c.621 0 1.125-.504 1.125-1.125V9.75M8.25 21h8.25"
              />
            </svg>
          </div>
        </div>
        <span class="text-sm font-bold tracking-tight text-slate-900">Loading HomeFlex...</span>
      </div>
    </div>
  `,
})
export class LoaderComponent {}
