import { getScoreTier, SCORE_TIER_CLASSES } from '../../utils/matchScore';

interface MatchScoreProps {
  score: number;
  size?: 'sm' | 'lg';
  showLabel?: boolean;
}

export function MatchScore({ score, size = 'lg', showLabel = false }: MatchScoreProps) {
  const tier = getScoreTier(score);
  const classes = SCORE_TIER_CLASSES[tier];
  const clamped = Math.max(0, Math.min(100, score));

  if (size === 'sm') {
    return (
      <span
        className={[
          'inline-flex items-baseline gap-0.5 font-mono font-semibold tabular',
          classes.text,
        ].join(' ')}
        aria-label={`Score de match: ${clamped}`}
      >
        <span className="text-base leading-none">{clamped}</span>
        <span className="text-[10px] leading-none">%</span>
      </span>
    );
  }

  return (
    <div className="flex flex-col gap-2" aria-label={`Score de match: ${clamped}`}>
      {showLabel && (
        <span className="text-xs font-medium uppercase tracking-wider text-slate-500">
          Compatibilidade
        </span>
      )}
      <div className={['flex items-baseline gap-1 font-mono font-semibold tabular', classes.text].join(' ')}>
        <span className="text-4xl leading-none sm:text-5xl">{clamped}</span>
        <span className="text-xl leading-none sm:text-2xl">%</span>
      </div>
      <div className="h-1 w-full overflow-hidden rounded-full bg-slate-100">
        <div
          className={['h-full rounded-full transition-all duration-300', classes.bar].join(' ')}
          style={{ width: `${clamped}%` }}
        />
      </div>
    </div>
  );
}
