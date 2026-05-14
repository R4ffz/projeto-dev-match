import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthLayout } from '../components/layout/AuthLayout';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { ErrorMessage } from '../components/ui/ErrorMessage';
import { authService } from '../services/authService';
import { ApiClientError } from '../services/apiClient';
import { useAuth } from '../hooks/useAuth';

export function RegisterPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [fieldErrorByName, setFieldErrorByName] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    setFieldErrorByName({});

    if (password.length < 8) {
      setFieldErrorByName({ password: 'A senha deve ter ao menos 8 caracteres.' });
      return;
    }
    if (password !== confirm) {
      setFieldErrorByName({ confirm: 'As senhas nao conferem.' });
      return;
    }

    setLoading(true);
    try {
      const result = await authService.register({
        name: name.trim(),
        email: email.trim(),
        password,
      });
      login(result.token, result.user);
      // Manda direto para perfil para preencher dados (melhor primeira experiencia)
      navigate('/profile', { replace: true });
    } catch (err) {
      if (err instanceof ApiClientError) {
        if (err.isValidation && err.fieldErrors) {
          const map: Record<string, string> = {};
          err.fieldErrors.forEach((fe) => {
            map[fe.field] = fe.message;
          });
          setFieldErrorByName(map);
        } else if (err.status === 409) {
          setError('Este e-mail ja esta cadastrado.');
        } else {
          setError(err.message);
        }
      } else {
        setError('Erro inesperado. Tente novamente.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout title="Crie sua conta" subtitle="Leva menos de um minuto.">
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        {error && <ErrorMessage>{error}</ErrorMessage>}

        <Input
          name="name"
          label="Nome"
          autoComplete="name"
          required
          value={name}
          onChange={(e) => setName(e.target.value)}
          error={fieldErrorByName.name}
          disabled={loading}
        />
        <Input
          name="email"
          label="E-mail"
          type="email"
          autoComplete="email"
          required
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          error={fieldErrorByName.email}
          disabled={loading}
        />
        <Input
          name="password"
          label="Senha"
          type="password"
          autoComplete="new-password"
          required
          minLength={8}
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          hint="Minimo de 8 caracteres."
          error={fieldErrorByName.password}
          disabled={loading}
        />
        <Input
          name="confirm"
          label="Confirme a senha"
          type="password"
          autoComplete="new-password"
          required
          value={confirm}
          onChange={(e) => setConfirm(e.target.value)}
          error={fieldErrorByName.confirm}
          disabled={loading}
        />

        <Button type="submit" variant="primary" loading={loading} fullWidth>
          Criar conta
        </Button>
      </form>

      <p className="mt-6 text-sm text-slate-600">
        Ja tem conta?{' '}
        <Link to="/login" className="font-medium text-slate-900 underline-offset-4 hover:underline">
          Entrar
        </Link>
      </p>
    </AuthLayout>
  );
}
