import { FormEvent, useState } from 'react';
import { Check, Save } from 'lucide-react';
import { Input } from '../ui/Input';
import { Select } from '../ui/Select';
import { Button } from '../ui/Button';
import { SkillsPicker } from './SkillsPicker';
import { ErrorMessage } from '../ui/ErrorMessage';
import { SENIORITIES, WORK_MODES } from '../../types/profile';
import { formatSeniority, formatWorkMode } from '../../utils/formatters';
import type { ProfileResponse, Seniority, UpdateProfileRequest, WorkMode } from '../../types/profile';
import type { SkillResponse } from '../../types/skill';
import type { ApiFieldError } from '../../types/error';

interface ProfileFormProps {
  profile: ProfileResponse;
  catalog: SkillResponse[];
  onSubmit: (payload: UpdateProfileRequest) => Promise<void>;
  saving: boolean;
  fieldErrors?: ApiFieldError[];
  successMessage?: string | null;
  errorMessage?: string | null;
}

interface FormState {
  seniority: Seniority | '';
  desiredSalary: string;
  preferredWorkModes: WorkMode[];
  professionalSummary: string;
  skillNames: string[];
}

function initFromProfile(p: ProfileResponse): FormState {
  return {
    seniority: p.seniority ?? '',
    desiredSalary: p.desiredSalary !== null ? String(p.desiredSalary) : '',
    preferredWorkModes: p.preferredWorkModes ?? [],
    professionalSummary: p.professionalSummary ?? '',
    skillNames: p.skills.map((s) => s.name),
  };
}

export function ProfileForm({
  profile,
  catalog,
  onSubmit,
  saving,
  fieldErrors,
  successMessage,
  errorMessage,
}: ProfileFormProps) {
  const [state, setState] = useState<FormState>(() => initFromProfile(profile));
  const [localError, setLocalError] = useState<string | null>(null);

  const fieldError = (field: string) => fieldErrors?.find((e) => e.field === field)?.message;

  const toggleWorkMode = (mode: WorkMode) => {
    if (saving) return;
    setState((s) => ({
      ...s,
      preferredWorkModes: s.preferredWorkModes.includes(mode)
        ? s.preferredWorkModes.filter((m) => m !== mode)
        : [...s.preferredWorkModes, mode],
    }));
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLocalError(null);
    if (!state.seniority || state.preferredWorkModes.length === 0) {
      setLocalError('Senioridade e pelo menos uma modalidade sao obrigatorias.');
      return;
    }
    const salary = Number(state.desiredSalary);
    if (!Number.isFinite(salary) || salary <= 0) {
      setLocalError('Informe um salario desejado maior que zero.');
      return;
    }
    await onSubmit({
      seniority: state.seniority,
      desiredSalary: salary,
      preferredWorkModes: state.preferredWorkModes,
      professionalSummary: state.professionalSummary.trim() || null,
      skillNames: state.skillNames,
    });
  };

  const workModesError = fieldError('preferredWorkModes');

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-6">
      {successMessage && (
        <div className="flex items-start gap-3 rounded-lg border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
          <Save aria-hidden className="mt-0.5 h-4 w-4 flex-shrink-0 text-emerald-600" />
          <span>{successMessage}</span>
        </div>
      )}
      {(localError || errorMessage) && (
        <ErrorMessage title="Nao foi possivel salvar">
          {localError ?? errorMessage}
        </ErrorMessage>
      )}

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <Select
          name="seniority"
          label="Senioridade"
          placeholder="Selecione"
          value={state.seniority}
          onChange={(e) =>
            setState((s) => ({ ...s, seniority: e.target.value as Seniority | '' }))
          }
          options={SENIORITIES.map((s) => ({ value: s, label: formatSeniority(s) }))}
          error={fieldError('seniority')}
          disabled={saving}
        />
        <Input
          name="desiredSalary"
          label="Pretensao salarial (BRL)"
          type="number"
          min={0}
          step={100}
          placeholder="ex: 6000"
          value={state.desiredSalary}
          onChange={(e) => setState((s) => ({ ...s, desiredSalary: e.target.value }))}
          error={fieldError('desiredSalary')}
          disabled={saving}
        />
      </div>

      <div className="flex flex-col gap-2">
        <div className="flex items-center justify-between">
          <span className="text-sm font-medium text-slate-700">Modalidades aceitas</span>
          <span className="font-mono text-xs text-slate-500 tabular">
            {state.preferredWorkModes.length}/{WORK_MODES.length} selecionadas
          </span>
        </div>
        <div className="flex flex-wrap gap-1.5">
          {WORK_MODES.map((mode) => {
            const isSelected = state.preferredWorkModes.includes(mode);
            return (
              <button
                key={mode}
                type="button"
                disabled={saving}
                onClick={() => toggleWorkMode(mode)}
                aria-pressed={isSelected}
                className={[
                  'inline-flex items-center gap-1.5 rounded-full px-3 py-1.5 text-xs font-medium transition-colors',
                  'focus:outline-none focus-visible:shadow-focus',
                  'disabled:cursor-not-allowed disabled:opacity-50',
                  isSelected
                    ? 'border border-emerald-200 bg-emerald-50 text-emerald-700 hover:bg-emerald-100'
                    : 'border border-slate-200 bg-white text-slate-700 hover:border-slate-300 hover:bg-slate-50',
                ].join(' ')}
              >
                {isSelected && <Check aria-hidden className="h-3 w-3" strokeWidth={3} />}
                {formatWorkMode(mode)}
              </button>
            );
          })}
        </div>
        {workModesError && (
          <p className="text-xs text-rose-600">{workModesError}</p>
        )}
      </div>

      <div className="flex flex-col gap-1.5">
        <label htmlFor="professionalSummary" className="text-sm font-medium text-slate-700">
          Resumo profissional
        </label>
        <textarea
          id="professionalSummary"
          name="professionalSummary"
          rows={4}
          maxLength={2000}
          placeholder="Conte rapidamente o que voce faz e o que busca."
          value={state.professionalSummary}
          onChange={(e) => setState((s) => ({ ...s, professionalSummary: e.target.value }))}
          disabled={saving}
          className={[
            'w-full rounded-lg border bg-white p-3 text-sm text-slate-900 placeholder:text-slate-400',
            'transition-shadow duration-100 ease-out focus:outline-none focus:shadow-focus',
            fieldError('professionalSummary')
              ? 'border-rose-500 focus:border-rose-600'
              : 'border-slate-200 focus:border-slate-900',
          ].join(' ')}
        />
        {fieldError('professionalSummary') && (
          <p className="text-xs text-rose-600">{fieldError('professionalSummary')}</p>
        )}
      </div>

      <SkillsPicker
        catalog={catalog}
        selected={state.skillNames}
        onChange={(skillNames) => setState((s) => ({ ...s, skillNames }))}
        disabled={saving}
      />

      <div className="flex items-center justify-end gap-2 border-t border-slate-200 pt-4">
        <Button type="submit" variant="primary" loading={saving} disabled={saving}>
          <Save aria-hidden className="h-4 w-4" />
          Salvar perfil
        </Button>
      </div>
    </form>
  );
}
