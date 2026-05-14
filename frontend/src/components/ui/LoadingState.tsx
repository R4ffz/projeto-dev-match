import { Loader2 } from 'lucide-react';

interface LoadingStateProps {
  label?: string;
  /** Use 'skeleton' para placeholders, 'spinner' para chamada inline. */
  variant?: 'spinner' | 'skeleton-list' | 'skeleton-grid';
}

export function LoadingState({ label = 'Carregando...', variant = 'spinner' }: LoadingStateProps) {
  if (variant === 'skeleton-list') {
    return (
      <div className="flex flex-col gap-3" aria-live="polite" aria-busy="true">
        {Array.from({ length: 4 }).map((_, i) => (
          <div key={i} className="h-20 w-full animate-pulse rounded-xl bg-slate-100" />
        ))}
        <span className="sr-only">{label}</span>
      </div>
    );
  }
  if (variant === 'skeleton-grid') {
    return (
      <div
        className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3"
        aria-live="polite"
        aria-busy="true"
      >
        {Array.from({ length: 6 }).map((_, i) => (
          <div key={i} className="h-44 animate-pulse rounded-xl bg-slate-100" />
        ))}
        <span className="sr-only">{label}</span>
      </div>
    );
  }
  return (
    <div
      className="flex items-center justify-center gap-2 py-12 text-slate-500"
      aria-live="polite"
      aria-busy="true"
    >
      <Loader2 className="h-4 w-4 animate-spin" />
      <span className="text-sm">{label}</span>
    </div>
  );
}
