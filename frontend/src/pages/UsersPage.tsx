import { FormEvent, useEffect, useState } from 'react';
import { apiRequest } from '../lib/api';
import type { PageResponse, User } from '../types';

export function UsersPage() {
  const [users, setUsers] = useState<User[]>([]);
  const [keyword, setKeyword] = useState('');
  const [error, setError] = useState('');
  const [creating, setCreating] = useState(false);
  const [form, setForm] = useState({ username: '', email: '', password: '' });

  async function loadUsers(search = '') {
    try {
      setError('');
      const data = await apiRequest<PageResponse<User>>(`/api/users?page=0&size=50&keyword=${encodeURIComponent(search)}`);
      setUsers(data.records);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch users');
    }
  }

  useEffect(() => {
    void loadUsers();
  }, []);

  async function onCreate(e: FormEvent) {
    e.preventDefault();
    try {
      setCreating(true);
      await apiRequest<User>('/api/users', {
        method: 'POST',
        body: JSON.stringify(form)
      });
      setForm({ username: '', email: '', password: '' });
      await loadUsers(keyword);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Create user failed');
    } finally {
      setCreating(false);
    }
  }

  return (
    <section className="card">
      <h2>User Management</h2>
      <div className="toolbar">
        <input
          placeholder="Search username/email"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
        />
        <button className="btn btn-light" onClick={() => void loadUsers(keyword)}>Search</button>
      </div>

      <form className="inline-form" onSubmit={onCreate}>
        <input placeholder="username" value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} />
        <input placeholder="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        <input placeholder="password" type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
        <button className="btn" type="submit" disabled={creating}>Create User</button>
      </form>

      {error ? <p className="error-text">{error}</p> : null}

      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Email</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.id}</td>
              <td>{u.username}</td>
              <td>{u.email}</td>
              <td>{u.status === 1 ? 'Active' : 'Disabled'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
