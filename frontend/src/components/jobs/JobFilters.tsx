import { FormEvent } from 'react';
import { Input } from '../ui/Input';
import { Select } from '../ui/Select';
import { Button } from '../ui/Button';
import { SENIORITIES, WORK_MODES } from '../../types/profile';
import { formatSeniority, formatWorkMode } from '../../utils/formatters';
import type { JobFilter } from '../../types/job';
import type { SkillResponse } from '../../types/skill';

interface JobFiltersProps {
  value: JobFilter;
  onChange: (next: JobFilter) => void;
  onClear: () => void;
  skills: SkillResponse[];
  disabled?: boolean;
}

export function JobFilters({ value, onChange, onClear, skills, disabled }: JobFiltersProps) {
  const handleSubmit = (e: FormEvent<HTMLFormElement>) => e.preventDefault();
  const hasAnyFilter = Object.values(value).some((v) => v !== undefined && v !== '');

  return (
    <form
      onSubmit={handleSubmit}
      className="rounded-xl border border-slate-200 bg-white p-4"
      aria-label="Filtros de vagas"
    >
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-5">
        <Input
          name="keyword"
          label="Busca"
          placeholder="titulo, empresa..."
          value={value.keyword ?? ''}
          onChange={(e) => onChange({ ...value, keyword: e.target.value || undefined })}
          disabled={disabled}
        />
        <Select
          name="seniority"
          label="Senioridade"
          placeholder="Todas"
          value={value.seniority ?? ''}
          onChange={(e) =>
            onChange({ ...value, seniority: (e.target.value || undefined) as JobFilter['seniority'] })
          }
          options={SENIORITIES.map((s) => ({ value: s, label: formatSeniority(s) }))}
          disabled={disabled}
        />
        <Select
          name="workMode"
          label="Modalidade"
          placeholder="Todas"
          value={value.workMode ?? ''}
          onChange={(e) =>
            onChange({ ...value, workMode: (e.target.value || undefined) as JobFilter['workMode'] })
          }
          options={WORK_MODES.map((m) => ({ value: m, label: formatWorkMode(m) }))}
          disabled={disabled}
        />
        <Select
          name="skill"
          label="Skill"
          placeholder="Todas"
          value={value.skill ?? ''}
          onChange={(e) => onChange({ ...value, skill: e.target.value || undefined })}
          options={skills.map((s) => ({ value: s.name, label: s.name }))}
          disabled={disabled}
        />
        <Input
          name="minSalary"
          label="Salario minimo"
          type="number"
          min={0}
          placeholder="ex: 5000"
          value={value.minSalary ?? ''}
          onChange={(e) => {
            const raw = e.target.value;
            onChange({ ...value, minSalary: raw === '' ? undefined : Number(raw) });
          }}
          disabled={disabled}
        />
      </div>
      {hasAnyFilter && (
        <div className="mt-3 flex justify-end">
          <Button type="button" variant="ghost" size="sm" onClick={onClear} disabled={disabled}>
            Limpar filtros
          </Button>
        </div>
      )}
    </form>
  );
}
