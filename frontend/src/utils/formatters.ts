import type { Seniority, WorkMode } from '../types/profile';

const SENIORITY_LABELS: Record<Seniority, string> = {
  INTERN: 'Estágio',
  JUNIOR: 'Júnior',
  MID_LEVEL: 'Pleno',
  SENIOR: 'Sênior',
};

const WORK_MODE_LABELS: Record<WorkMode, string> = {
  REMOTE: 'Remoto',
  HYBRID: 'Híbrido',
  ONSITE: 'Presencial',
};

export function formatSeniority(value: Seniority): string {
  return SENIORITY_LABELS[value];
}

export function formatWorkMode(value: WorkMode): string {
  return WORK_MODE_LABELS[value];
}

const currencyFormatter = new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL',
  minimumFractionDigits: 0,
  maximumFractionDigits: 0,
});

export function formatSalary(value: number): string {
  return currencyFormatter.format(value);
}

export function formatSalaryRange(min: number, max: number): string {
  return `${formatSalary(min)} – ${formatSalary(max)}`;
}
