import { ReactNode } from 'react';
import { ChevronRight } from 'lucide-react';

interface AuthLayoutProps {
  title: string;
  subtitle: string;
  children: ReactNode;
}

/**
 * Split layout para Login e Register.
 * Esquerda (so desktop): branding + preview tecnico do match (estilo terminal).
 * Direita: formulario.
 */
export function AuthLayout({ title, subtitle, children }: AuthLayoutProps) {
  return (
    <div className="min-h-screen bg-white lg:grid lg:grid-cols-2">
      {/* Painel esquerdo - apenas desktop */}
      <aside className="relative hidden border-r border-slate-200 bg-slate-50 lg:flex lg:flex-col lg:justify-between lg:p-12">
        <div className="dot-grid absolute inset-0 opacity-50" aria-hidden />

        <div className="relative">
          <div className="flex items-center gap-2 text-slate-900">
            <ChevronRight aria-hidden className="h-5 w-5 text-emerald-600" strokeWidth={3} />
            <span className="text-base font-semibold tracking-tightish">DevMatch</span>
          </div>
          <p className="mt-8 max-w-md text-2xl font-semibold leading-tight tracking-tight2 text-slate-900">
            Match explicavel entre voce e vagas tech.
          </p>
          <p className="mt-3 max-w-md text-sm text-slate-600">
            Score de compatibilidade calculado a partir das suas skills, senioridade,
            modalidade e pretensao salarial — com o porque visivel em cada vaga.
          </p>
        </div>

        <div className="relative">
          <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
            <div className="flex items-center gap-1.5 border-b border-slate-200 bg-slate-50/80 px-4 py-2">
              <span className="h-2.5 w-2.5 rounded-full bg-slate-300" />
              <span className="h-2.5 w-2.5 rounded-full bg-slate-300" />
              <span className="h-2.5 w-2.5 rounded-full bg-slate-300" />
              <span className="ml-2 font-mono text-xs text-slate-500">
                GET /api/jobs/1/match
              </span>
            </div>
            <pre className="overflow-x-auto px-4 py-3 font-mono text-xs leading-relaxed text-slate-700">
{`{
  "jobId": 1,
  "finalScore": `}<span className="font-semibold text-emerald-600">85</span>{`,
  "skillsScore": 75,
  "seniorityScore": 100,
  "workModeScore": 100,
  "salaryScore": 100,
  "matchedSkills": ["Java", "SQL", "Spring Boot"],
  "missingSkills": ["Git"],
  "explanation": "Voce atende 3 de 4 skills exigidas..."
}`}
            </pre>
          </div>
          <p className="mt-4 font-mono text-xs text-slate-400">
            built for engineers · sem caixa preta
          </p>
        </div>
      </aside>

      {/* Formulario - sempre visivel */}
      <section className="flex items-center justify-center px-4 py-12 sm:px-6 lg:px-12">
        <div className="w-full max-w-md">
          {/* Logo mobile */}
          <div className="mb-8 flex items-center gap-2 text-slate-900 lg:hidden">
            <ChevronRight aria-hidden className="h-5 w-5 text-emerald-600" strokeWidth={3} />
            <span className="font-semibold tracking-tightish">DevMatch</span>
          </div>

          <h1 className="text-2xl font-semibold tracking-tight2 text-slate-900 sm:text-3xl">
            {title}
          </h1>
          <p className="mt-2 text-sm text-slate-600">{subtitle}</p>

          <div className="mt-8">{children}</div>
        </div>
      </section>
    </div>
  );
}
