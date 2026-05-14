import { Link } from 'react-router-dom';
import { Compass } from 'lucide-react';
import { Button } from '../components/ui/Button';

export function NotFoundPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-white px-6 py-12">
      <div className="flex flex-col items-center gap-4 text-center">
        <Compass aria-hidden className="h-10 w-10 text-slate-300" strokeWidth={1.5} />
        <h1 className="font-mono text-sm text-slate-500">404</h1>
        <p className="text-2xl font-semibold tracking-tight2 text-slate-900">
          Pagina nao encontrada
        </p>
        <p className="max-w-sm text-sm text-slate-600">
          O endereco que voce tentou abrir nao existe ou foi movido.
        </p>
        <Link to="/jobs">
          <Button variant="primary">Ir para vagas</Button>
        </Link>
      </div>
    </div>
  );
}
