import { useNavigate } from 'react-router-dom';
import { Building2 } from 'lucide-react';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { MatchScore } from './MatchScore';
import { formatSalaryRange, formatSeniority, formatWorkMode } from '../../utils/formatters';
import type { JobResponse } from '../../types/job';

interface JobCardProps {
  job: JobResponse;
  /** Score de match opcional — quando vier de /jobs/recommended */
  matchScore?: number;
}

const MAX_SKILLS_DISPLAY = 4;

export function JobCard({ job, matchScore }: JobCardProps) {
  const navigate = useNavigate();
  const visibleSkills = job.skills.slice(0, MAX_SKILLS_DISPLAY);
  const overflow = job.skills.length - visibleSkills.length;

  return (
    <Card hover onClick={() => navigate(`/jobs/${job.id}`)} className="flex h-full flex-col p-5">
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0 flex-1">
          <h3 className="truncate text-base font-semibold text-slate-900">{job.title}</h3>
          <p className="mt-0.5 flex items-center gap-1.5 text-sm text-slate-600">
            <Building2 aria-hidden className="h-3.5 w-3.5 text-slate-400" />
            <span className="truncate">{job.company}</span>
          </p>
        </div>
        {matchScore !== undefined && (
          <div className="flex-shrink-0">
            <MatchScore score={matchScore} size="sm" />
          </div>
        )}
      </div>

      <div className="mt-4 flex flex-wrap items-center gap-x-3 gap-y-1 text-xs text-slate-600">
        <span className="font-medium text-slate-700">{formatSeniority(job.seniority)}</span>
        <span aria-hidden className="text-slate-300">·</span>
        <span>{formatWorkMode(job.workMode)}</span>
        <span aria-hidden className="text-slate-300">·</span>
        <span className="font-mono tabular text-slate-700">
          {formatSalaryRange(job.minSalary, job.maxSalary)}
        </span>
      </div>

      <div className="mt-4 flex flex-wrap gap-1.5">
        {visibleSkills.map((skill) => (
          <Badge key={skill.id} variant="default">
            {skill.name}
          </Badge>
        ))}
        {overflow > 0 && (
          <Badge variant="neutral">+{overflow}</Badge>
        )}
      </div>
    </Card>
  );
}
