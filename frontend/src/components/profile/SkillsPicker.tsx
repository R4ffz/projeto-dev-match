import { Check } from 'lucide-react';
import type { SkillResponse } from '../../types/skill';

interface SkillsPickerProps {
  catalog: SkillResponse[];
  selected: string[];
  onChange: (next: string[]) => void;
  disabled?: boolean;
}

export function SkillsPicker({ catalog, selected, onChange, disabled }: SkillsPickerProps) {
  const toggle = (name: string) => {
    if (disabled) return;
    if (selected.includes(name)) {
      onChange(selected.filter((s) => s !== name));
    } else {
      onChange([...selected, name]);
    }
  };

  return (
    <div className="flex flex-col gap-2">
      <div className="flex items-center justify-between">
        <span className="text-sm font-medium text-slate-700">Skills</span>
        <span className="font-mono text-xs text-slate-500 tabular">
          {selected.length}/{catalog.length} selecionadas
        </span>
      </div>

      <div className="flex flex-wrap gap-1.5">
        {catalog.map((skill) => {
          const isSelected = selected.includes(skill.name);
          return (
            <button
              key={skill.id}
              type="button"
              disabled={disabled}
              onClick={() => toggle(skill.name)}
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
              {skill.name}
            </button>
          );
        })}
      </div>
    </div>
  );
}
