export type ScoreTier = 'low' | 'mid' | 'high';

export function getScoreTier(score: number): ScoreTier {
  if (score >= 70) return 'high';
  if (score >= 40) return 'mid';
  return 'low';
}

/**
 * Classes Tailwind por tier — texto + barra.
 * Mantidas em um lugar so para garantir consistencia entre MatchScore (lg) e o badge inline do card.
 */
export const SCORE_TIER_CLASSES: Record<ScoreTier, { text: string; bar: string; bg: string }> = {
  high: { text: 'text-emerald-600', bar: 'bg-emerald-600', bg: 'bg-emerald-50' },
  mid: { text: 'text-amber-500', bar: 'bg-amber-500', bg: 'bg-amber-50' },
  low: { text: 'text-rose-600', bar: 'bg-rose-600', bg: 'bg-rose-50' },
};
