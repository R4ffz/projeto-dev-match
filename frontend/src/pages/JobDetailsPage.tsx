import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { ArrowLeft, Building2, Briefcase, MapPin, Wallet } from 'lucide-react';
import { LoadingState } from '../components/ui/LoadingState';
import { ErrorMessage } from '../components/ui/ErrorMessage';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { MatchScore } from '../components/jobs/MatchScore';
import { SkillsBreakdown } from '../components/jobs/SkillsBreakdown';
import { jobService } from '../services/jobService';
import { formatSalaryRange, formatSeniority, formatWorkMode } from '../utils/formatters';
import { getScoreTier, SCORE_TIER_CLASSES } from '../utils/matchScore';
import type { JobResponse } from '../types/job';
import type { MatchResult } from '../types/match';

interface ScoreRowProps {
  label: string;
  weight: string;
  score: number;
}

function ScoreRow({ label, weight, score }: ScoreRowProps) {
  const tier = getScoreTier(score);
  const classes = SCORE_TIER_CLASSES[tier];
  return (
    <div className="flex items-center gap-4 border-t border-slate-100 py-3 first:border-t-0">
      <div className="flex-1">
        <p className="text-sm font-medium text-slate-900">{label}</p>
        <p className="font-mono text-[11px] text-slate-500 tabular">peso {weight}</p>
      </div>
      <div className="w-24">
        <div className="h-1 overflow-hidden rounded-full bg-slate-100">
          <div className={['h-full', classes.bar].join(' ')} style={{ width: `${score}%` }} />
        </div>
      </div>
      <span className={['w-10 text-right font-mono text-sm font-semibold tabular', classes.text].join(' ')}>
        {score}
      </span>
    </div>
  );
}

export function JobDetailsPage() {
  const { id } = useParams<{ id: string }>();
  const numericId = id ? Number(id) : NaN;

  const [job, setJob] = useState<JobResponse | null>(null);
  const [match, setMatch] = useState<MatchResult | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!Number.isFinite(numericId)) {
      setError('Id de vaga invalido.');
      setLoading(false);
      return;
    }
    let cancelled = false;
    setLoading(true);
    setError(null);
    Promise.all([jobService.getById(numericId), jobService.getMatch(numericId)])
      .then(([j, m]) => {
        if (cancelled) return;
        setJob(j);
        setMatch(m);
      })
      .catch((err) => {
        if (cancelled) return;
        setError(err instanceof Error ? err.message : 'Erro ao carregar vaga.');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [numericId]);

  return (
    <div className="flex flex-col gap-6">
      <Link
        to="/jobs"
        className="inline-flex items-center gap-1.5 text-sm font-medium text-slate-600 transition-colors hover:text-slate-900"
      >
        <ArrowLeft aria-hidden className="h-4 w-4" />
        Voltar para vagas
      </Link>

      {loading && <LoadingState label="Carregando vaga..." />}

      {error && !loading && (
        <div className="flex flex-col gap-4">
          <ErrorMessage title="Nao foi possivel carregar a vaga">{error}</ErrorMessage>
          <Link to="/jobs">
            <Button variant="secondary" size="sm">
              Voltar para vagas
            </Button>
          </Link>
        </div>
      )}

      {!loading && !error && job && match && (
        <div className="grid grid-cols-1 gap-6 lg:grid-cols-12">
          {/* Score (mobile: primeiro; desktop: direita) */}
          <aside className="order-1 lg:order-2 lg:col-span-4">
            <Card className="p-6 lg:sticky lg:top-20">
              <MatchScore score={match.finalScore} showLabel />
              <p className="mt-4 text-sm text-slate-600">{match.explanation}</p>

              <div className="mt-6 border-t border-slate-100 pt-4">
                <p className="mb-1 text-xs font-medium uppercase tracking-wider text-slate-500">
                  Detalhe por criterio
                </p>
                <ScoreRow label="Skills" weight="60%" score={match.skillsScore} />
                <ScoreRow label="Senioridade" weight="20%" score={match.seniorityScore} />
                <ScoreRow label="Modalidade" weight="10%" score={match.workModeScore} />
                <ScoreRow label="Salario" weight="10%" score={match.salaryScore} />
              </div>
            </Card>
          </aside>

          {/* Conteudo principal */}
          <article className="order-2 lg:order-1 lg:col-span-8">
            <Card className="p-6 sm:p-8">
              <h1 className="text-2xl font-semibold tracking-tight2 text-slate-900 sm:text-3xl">
                {job.title}
              </h1>
              <p className="mt-1 flex items-center gap-1.5 text-sm text-slate-600">
                <Building2 aria-hidden className="h-4 w-4 text-slate-400" />
                {job.company}
              </p>

              <div className="mt-5 flex flex-wrap gap-2">
                <Badge variant="neutral">
                  <Briefcase aria-hidden className="h-3 w-3 text-slate-400" />
                  {formatSeniority(job.seniority)}
                </Badge>
                <Badge variant="neutral">
                  <MapPin aria-hidden className="h-3 w-3 text-slate-400" />
                  {formatWorkMode(job.workMode)}
                </Badge>
                <Badge variant="neutral">
                  <Wallet aria-hidden className="h-3 w-3 text-slate-400" />
                  <span className="font-mono tabular">
                    {formatSalaryRange(job.minSalary, job.maxSalary)}
                  </span>
                </Badge>
              </div>

              <div className="mt-6">
                <h2 className="text-xs font-medium uppercase tracking-wider text-slate-500">
                  Descricao
                </h2>
                <p className="mt-2 whitespace-pre-line text-sm leading-relaxed text-slate-700">
                  {job.description}
                </p>
              </div>

              <div className="mt-6 border-t border-slate-100 pt-6">
                <h2 className="mb-3 text-xs font-medium uppercase tracking-wider text-slate-500">
                  Comparacao de skills
                </h2>
                <SkillsBreakdown matched={match.matchedSkills} missing={match.missingSkills} />
              </div>
            </Card>
          </article>
        </div>
      )}
    </div>
  );
}
