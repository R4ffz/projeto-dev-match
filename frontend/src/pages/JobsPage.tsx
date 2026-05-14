import { useEffect, useMemo, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { Briefcase, Sparkles, ListFilter, UserCog } from 'lucide-react';
import { LoadingState } from '../components/ui/LoadingState';
import { ErrorMessage } from '../components/ui/ErrorMessage';
import { EmptyState } from '../components/ui/EmptyState';
import { JobCard } from '../components/jobs/JobCard';
import { JobFilters } from '../components/jobs/JobFilters';
import { Button } from '../components/ui/Button';
import { jobService } from '../services/jobService';
import { skillService } from '../services/skillService';
import { profileService } from '../services/profileService';
import type { JobFilter, JobResponse } from '../types/job';
import type { RecommendedJob } from '../types/match';
import type { SkillResponse } from '../types/skill';
import type { ProfileResponse, Seniority, WorkMode } from '../types/profile';

type View = 'recommended' | 'all';

function readFilterFromSearch(params: URLSearchParams): JobFilter {
  const minSalary = params.get('minSalary');
  return {
    keyword: params.get('keyword') ?? undefined,
    seniority: (params.get('seniority') as Seniority | null) ?? undefined,
    workMode: (params.get('workMode') as WorkMode | null) ?? undefined,
    skill: params.get('skill') ?? undefined,
    minSalary: minSalary ? Number(minSalary) : undefined,
  };
}

function writeFilterToSearch(view: View, filter: JobFilter): URLSearchParams {
  const params = new URLSearchParams();
  if (view !== 'recommended') params.set('view', view);
  if (filter.keyword) params.set('keyword', filter.keyword);
  if (filter.seniority) params.set('seniority', filter.seniority);
  if (filter.workMode) params.set('workMode', filter.workMode);
  if (filter.skill) params.set('skill', filter.skill);
  if (filter.minSalary !== undefined) params.set('minSalary', String(filter.minSalary));
  return params;
}

interface Item {
  job: JobResponse;
  score?: number;
}

export function JobsPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const view: View = searchParams.get('view') === 'all' ? 'all' : 'recommended';
  const filter = useMemo(() => readFilterFromSearch(searchParams), [searchParams]);

  const [profile, setProfile] = useState<ProfileResponse | null>(null);
  const [skills, setSkills] = useState<SkillResponse[]>([]);
  const [items, setItems] = useState<Item[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Carrega catalogo de skills e perfil uma unica vez (perfil so para detectar incompleto)
  useEffect(() => {
    let cancelled = false;
    Promise.all([skillService.listAll(), profileService.getMyProfile()])
      .then(([s, p]) => {
        if (cancelled) return;
        setSkills(s);
        setProfile(p);
      })
      .catch(() => {
        // erro nao bloqueia a pagina; lista de vagas ainda funciona
      });
    return () => {
      cancelled = true;
    };
  }, []);

  // Carrega lista de vagas conforme view + filtros
  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);

    const fetcher =
      view === 'recommended' && Object.values(filter).every((v) => v === undefined || v === '')
        ? jobService.listRecommended().then((list: RecommendedJob[]) =>
            list.map<Item>((r) => ({ job: r.job, score: r.finalScore })),
          )
        : jobService.list(filter).then((list: JobResponse[]) =>
            list.map<Item>((j) => ({ job: j })),
          );

    fetcher
      .then((result) => {
        if (cancelled) return;
        setItems(result);
      })
      .catch((err) => {
        if (cancelled) return;
        setError(err instanceof Error ? err.message : 'Erro ao carregar vagas.');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [view, filter]);

  const handleViewChange = (next: View) => {
    const params = writeFilterToSearch(next, {});
    setSearchParams(params);
  };

  const handleFilterChange = (next: JobFilter) => {
    // Aplicar filtro implica view "all"
    const params = writeFilterToSearch('all', next);
    setSearchParams(params);
  };

  const handleClearFilters = () => {
    setSearchParams(new URLSearchParams());
  };

  const isProfileIncomplete =
    profile !== null &&
    (profile.seniority === null ||
      profile.preferredWorkMode === null ||
      profile.desiredSalary === null);

  return (
    <div className="flex flex-col gap-6">
      <header className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight2 text-slate-900 sm:text-3xl">
            Vagas
          </h1>
          <p className="mt-1 text-sm text-slate-600">
            {view === 'recommended'
              ? 'Ordenadas pelo score de match com seu perfil.'
              : 'Lista completa, filtravel.'}
          </p>
        </div>
        <div className="flex gap-1 rounded-lg border border-slate-200 bg-white p-1">
          <button
            onClick={() => handleViewChange('recommended')}
            className={[
              'inline-flex items-center gap-1.5 rounded-md px-3 py-1.5 text-sm font-medium transition-colors',
              view === 'recommended'
                ? 'bg-slate-900 text-white'
                : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900',
            ].join(' ')}
          >
            <Sparkles aria-hidden className="h-3.5 w-3.5" />
            Recomendadas
          </button>
          <button
            onClick={() => handleViewChange('all')}
            className={[
              'inline-flex items-center gap-1.5 rounded-md px-3 py-1.5 text-sm font-medium transition-colors',
              view === 'all'
                ? 'bg-slate-900 text-white'
                : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900',
            ].join(' ')}
          >
            <ListFilter aria-hidden className="h-3.5 w-3.5" />
            Todas
          </button>
        </div>
      </header>

      {isProfileIncomplete && (
        <div className="flex items-start justify-between gap-3 rounded-xl border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-800">
          <div className="flex items-start gap-3">
            <UserCog aria-hidden className="mt-0.5 h-4 w-4 flex-shrink-0 text-amber-600" />
            <div>
              <p className="font-semibold">Complete seu perfil para um match preciso</p>
              <p className="mt-0.5 text-amber-700">
                Sem senioridade, modalidade ou pretensao salarial, o score pode aparecer baixo demais.
              </p>
            </div>
          </div>
          <Link to="/profile" className="flex-shrink-0">
            <Button variant="secondary" size="sm">
              Completar
            </Button>
          </Link>
        </div>
      )}

      {view === 'all' && (
        <JobFilters
          value={filter}
          onChange={handleFilterChange}
          onClear={handleClearFilters}
          skills={skills}
          disabled={loading}
        />
      )}

      {loading && <LoadingState variant="skeleton-grid" label="Carregando vagas..." />}

      {error && !loading && (
        <ErrorMessage title="Erro ao carregar vagas">{error}</ErrorMessage>
      )}

      {!loading && !error && items.length === 0 && (
        <EmptyState
          icon={Briefcase}
          title="Nenhuma vaga encontrada"
          description={
            view === 'recommended'
              ? 'Tente alternar para "Todas" ou ajustar seu perfil.'
              : 'Tente remover filtros para ver mais resultados.'
          }
        />
      )}

      {!loading && !error && items.length > 0 && (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {items.map((item) => (
            <JobCard key={item.job.id} job={item.job} matchScore={item.score} />
          ))}
        </div>
      )}
    </div>
  );
}
