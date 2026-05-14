import { ReactNode } from 'react';

interface CardProps {
  children: ReactNode;
  hover?: boolean;
  onClick?: () => void;
  className?: string;
  as?: 'div' | 'article' | 'section';
}

export function Card({
  children,
  hover = false,
  onClick,
  className = '',
  as = 'div',
}: CardProps) {
  const Tag = as;
  const interactive = Boolean(onClick);
  return (
    <Tag
      onClick={onClick}
      role={interactive ? 'button' : undefined}
      tabIndex={interactive ? 0 : undefined}
      onKeyDown={
        interactive
          ? (e) => {
              if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                onClick?.();
              }
            }
          : undefined
      }
      className={[
        'rounded-xl border border-slate-200 bg-white',
        hover
          ? 'transition-all duration-150 ease-out hover:border-slate-300 hover:-translate-y-px cursor-pointer focus:outline-none focus-visible:shadow-focus focus-visible:border-slate-900'
          : '',
        className,
      ].join(' ')}
    >
      {children}
    </Tag>
  );
}
