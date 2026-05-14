import { AlertCircle } from 'lucide-react';
import { ReactNode } from 'react';

interface ErrorMessageProps {
  title?: string;
  children: ReactNode;
  /** 'box' (default) para alerts em forms; 'inline' para usar abaixo de inputs. */
  variant?: 'box' | 'inline';
}

export function ErrorMessage({ title, children, variant = 'box' }: ErrorMessageProps) {
  if (variant === 'inline') {
    return <p className="text-xs text-rose-600">{children}</p>;
  }
  return (
    <div
      role="alert"
      className="flex items-start gap-3 rounded-lg border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700"
    >
      <AlertCircle aria-hidden className="mt-0.5 h-4 w-4 flex-shrink-0 text-rose-600" />
      <div>
        {title && <p className="font-semibold">{title}</p>}
        <div>{children}</div>
      </div>
    </div>
  );
}
