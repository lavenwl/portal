import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();

  const [account, setAccount] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError('');
    try {
      await login(account, password);
      navigate('/dashboard');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Login failed');
    }
  }

  return (
    <div className="auth-wrap">
      <form className="card auth-card" onSubmit={handleSubmit}>
        <h2>Sign In</h2>
        <label>
          Account
          <input value={account} onChange={(e) => setAccount(e.target.value)} placeholder="username or email" />
        </label>
        <label>
          Password
          <input value={password} onChange={(e) => setPassword(e.target.value)} type="password" placeholder="******" />
        </label>
        {error ? <p className="error-text">{error}</p> : null}
        <button className="btn" type="submit">Login</button>
        <p className="auth-footnote">No account? <Link to="/register">Register</Link></p>
      </form>
    </div>
  );
}
