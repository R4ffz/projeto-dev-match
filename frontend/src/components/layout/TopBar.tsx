import { NavLink, useNavigate } from 'react-router-dom';
import { LogOut, ChevronRight } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';

export function TopBar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  const navItem = ({ isActive }: { isActive: boolean }) =>
    [
      'inline-flex h-9 items-center rounded-md px-3 text-sm font-medium transition-colors',
      isActive
        ? 'bg-slate-100 text-slate-900'
        : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900',
    ].join(' ');

  return (
    <header className="sticky top-0 z-10 border-b border-slate-200 bg-white/80 backdrop-blur">
      <div className="mx-auto flex h-14 max-w-6xl items-center justify-between px-4 sm:px-6 lg:px-8">
        <div className="flex items-center gap-6">
          <NavLink to="/jobs" className="flex items-center gap-2 text-slate-900">
            <ChevronRight aria-hidden className="h-4 w-4 text-emerald-600" strokeWidth={3} />
            <span className="font-semibold tracking-tightish">DevMatch</span>
          </NavLink>
          <nav className="hidden gap-1 sm:flex">
            <NavLink to="/jobs" className={navItem}>
              Vagas
            </NavLink>
            <NavLink to="/profile" className={navItem}>
              Perfil
            </NavLink>
          </nav>
        </div>

        <div className="flex items-center gap-2">
          {user && (
            <span className="hidden text-xs text-slate-500 sm:inline">
              {user.email}
            </span>
          )}
          <button
            onClick={handleLogout}
            className="inline-flex h-9 items-center gap-1.5 rounded-md px-3 text-sm font-medium text-slate-600 transition-colors hover:bg-slate-100 hover:text-slate-900"
          >
            <LogOut aria-hidden className="h-4 w-4" />
            <span className="hidden sm:inline">Sair</span>
          </button>
        </div>
      </div>

      {/* Nav mobile: shorter row */}
      <nav className="flex gap-1 border-t border-slate-200 px-4 py-1 sm:hidden">
        <NavLink to="/jobs" className={navItem}>
          Vagas
        </NavLink>
        <NavLink to="/profile" className={navItem}>
          Perfil
        </NavLink>
      </nav>
    </header>
  );
}
