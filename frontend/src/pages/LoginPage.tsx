import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthLayout } from '../components/layout/AuthLayout';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { ErrorMessage } from '../components/ui/ErrorMessage';
import { authService } from '../services/authService';
import { ApiClientError } from '../services/apiClient';
import { useAuth } from '../hooks/useAuth';

export function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const result = await authService.login({ email: email.trim(), password });
      login(result.token, result.user);
      navigate('/jobs', { replace: true });
    } catch (err) {
      if (err instanceof ApiClientError) {
        setError(err.status === 401 ? 'E-mail ou senha invalidos.' : err.message);
      } else {
        setError('Erro inesperado. Tente novamente.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout title="Entre na sua conta" subtitle="Use o e-mail e a senha do seu cadastro.">
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        {error && <ErrorMessage>{error}</ErrorMessage>}

        <Input
          name="email"
          label="E-mail"
          type="email"
          autoComplete="email"
          required
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          disabled={loading}
        />
        <Input
          name="password"
          label="Senha"
          type="password"
          autoComplete="current-password"
          required
          minLength={8}
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          disabled={loading}
        />

        <Button type="submit" variant="primary" loading={loading} fullWidth>
          Entrar
        </Button>
      </form>

      <p className="mt-6 text-sm text-slate-600">
        Ainda nao tem conta?{' '}
        <Link to="/register" className="font-medium text-slate-900 underline-offset-4 hover:underline">
          Cadastre-se
        </Link>
      </p>
    </AuthLayout>
  );
}
