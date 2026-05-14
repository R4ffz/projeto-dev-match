import { Check, Minus } from 'lucide-react';
import { Badge } from '../ui/Badge';

interface SkillsBreakdownProps {
  matched: string[];
  missing: string[];
}

export function SkillsBreakdown({ matched, missing }: SkillsBreakdownProps) {
  return (
    <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
      <div>
        <div className="mb-2 flex items-center gap-2 text-xs font-medium uppercase tracking-wider text-emerald-700">
          <Check aria-hidden className="h-3.5 w-3.5" strokeWidth={3} />
          Compativeis ({matched.length})
        </div>
        {matched.length > 0 ? (
          <div className="flex flex-wrap gap-1.5">
            {matched.map((name) => (
              <Badge key={name} variant="matched">
                {name}
              </Badge>
            ))}
          </div>
        ) : (
          <p className="text-sm text-slate-500">Nenhuma skill compativel ainda.</p>
        )}
      </div>

      <div>
        <div className="mb-2 flex items-center gap-2 text-xs font-medium uppercase tracking-wider text-slate-500">
          <Minus aria-hidden className="h-3.5 w-3.5" strokeWidth={3} />
          Faltantes ({missing.length})
        </div>
        {missing.length > 0 ? (
          <div className="flex flex-wrap gap-1.5">
            {missing.map((name) => (
              <Badge key={name} variant="missing">
                {name}
              </Badge>
            ))}
          </div>
        ) : (
          <p className="text-sm text-slate-500">Voce atende todas as skills exigidas.</p>
        )}
      </div>
    </div>
  );
}
