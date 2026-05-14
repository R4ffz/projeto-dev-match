import { useEffect, useState } from 'react';
import { LoadingState } from '../components/ui/LoadingState';
import { ErrorMessage } from '../components/ui/ErrorMessage';
import { ProfileForm } from '../components/profile/ProfileForm';
import { profileService } from '../services/profileService';
import { skillService } from '../services/skillService';
import { ApiClientError } from '../services/apiClient';
import type { ProfileResponse, UpdateProfileRequest } from '../types/profile';
import type { SkillResponse } from '../types/skill';
import type { ApiFieldError } from '../types/error';

export function ProfilePage() {
  const [profile, setProfile] = useState<ProfileResponse | null>(null);
  const [catalog, setCatalog] = useState<SkillResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);
  const [saveSuccess, setSaveSuccess] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<ApiFieldError[]>([]);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);
    Promise.all([profileService.getMyProfile(), skillService.listAll()])
      .then(([p, s]) => {
        if (cancelled) return;
        setProfile(p);
        setCatalog(s);
      })
      .catch((err) => {
        if (cancelled) return;
        setError(err instanceof Error ? err.message : 'Erro ao carregar perfil.');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, []);

  const handleSave = async (payload: UpdateProfileRequest) => {
    setSaving(true);
    setSaveError(null);
    setSaveSuccess(null);
    setFieldErrors([]);
    try {
      const updated = await profileService.updateMyProfile(payload);
      setProfile(updated);
      setSaveSuccess('Perfil salvo com sucesso.');
      // Auto-fechar a mensagem apos 3s
      setTimeout(() => setSaveSuccess(null), 3000);
    } catch (err) {
      if (err instanceof ApiClientError) {
        if (err.isValidation && err.fieldErrors) {
          setFieldErrors(err.fieldErrors);
        } else {
          setSaveError(err.message);
        }
      } else {
        setSaveError('Nao foi possivel salvar. Tente novamente.');
      }
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="mx-auto max-w-2xl">
      <header className="mb-6">
        <h1 className="text-2xl font-semibold tracking-tight2 text-slate-900 sm:text-3xl">
          Perfil
        </h1>
        <p className="mt-1 text-sm text-slate-600">
          Quanto mais completo, melhor o match com as vagas.
        </p>
      </header>

      {loading && <LoadingState label="Carregando perfil..." />}

      {error && !loading && (
        <ErrorMessage title="Erro ao carregar">{error}</ErrorMessage>
      )}

      {!loading && !error && profile && (
        <ProfileForm
          profile={profile}
          catalog={catalog}
          onSubmit={handleSave}
          saving={saving}
          fieldErrors={fieldErrors}
          successMessage={saveSuccess}
          errorMessage={saveError}
        />
      )}
    </div>
  );
}
