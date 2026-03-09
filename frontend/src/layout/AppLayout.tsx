import { Link, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

const NAV_ITEMS = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/users', label: 'Users' },
  { to: '/roles', label: 'Roles' },
  { to: '/permissions', label: 'Permissions' },
  { to: '/login-logs', label: 'Login Logs' },
  { to: '/operation-logs', label: 'Operation Logs' }
];

export function AppLayout() {
  const location = useLocation();
  const { logout } = useAuth();

  return (
    <div className="app-shell">
      <aside className="app-sidebar">
        <h1 className="brand">RBAC Console</h1>
        <nav className="nav">
          {NAV_ITEMS.map((item) => (
            <Link
              key={item.to}
              className={location.pathname === item.to ? 'nav-link nav-link-active' : 'nav-link'}
              to={item.to}
            >
              {item.label}
            </Link>
          ))}
        </nav>
      </aside>
      <section className="app-main">
        <header className="app-topbar">
          <button className="btn btn-light" onClick={() => void logout()}>
            Logout
          </button>
        </header>
        <main className="app-content">
          <Outlet />
        </main>
      </section>
    </div>
  );
}
