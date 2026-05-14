import { ReactNode } from 'react';

type BadgeVariant = 'default' | 'matched' | 'missing' | 'neutral';

interface BadgeProps {
  variant?: BadgeVariant;
  children: ReactNode;
  className?: string;
}

const VARIANT: Record<BadgeVariant, string> = {
  default: 'bg-slate-100 text-slate-700 border border-transparent',
  matched: 'bg-emerald-50 text-emerald-700 border border-emerald-100',
  missing: 'bg-white text-slate-500 border border-dashed border-slate-300',
  neutral: 'bg-white text-slate-700 border border-slate-200',
};

export function Badge({ variant = 'default', children, className = '' }: BadgeProps) {
  return (
    <span
      className={[
        'inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 text-xs font-medium',
        VARIANT[variant],
        className,
      ].join(' ')}
    >
      {variant === 'matched' && (
        <span aria-hidden className="h-1.5 w-1.5 rounded-full bg-emerald-500" />
      )}
      {children}
    </span>
  );
}
