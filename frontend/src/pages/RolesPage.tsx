import { FormEvent, useEffect, useState } from 'react';
import { apiRequest } from '../lib/api';
import type { Role } from '../types';

export function RolesPage() {
  const [roles, setRoles] = useState<Role[]>([]);
  const [error, setError] = useState('');
  const [form, setForm] = useState({ code: '', name: '', description: '' });

  async function loadRoles() {
    try {
      setError('');
      const data = await apiRequest<Role[]>('/api/roles');
      setRoles(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch roles');
    }
  }

  useEffect(() => {
    void loadRoles();
  }, []);

  async function onCreate(e: FormEvent) {
    e.preventDefault();
    try {
      await apiRequest<Role>('/api/roles', {
        method: 'POST',
        body: JSON.stringify(form)
      });
      setForm({ code: '', name: '', description: '' });
      await loadRoles();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Create role failed');
    }
  }

  return (
    <section className="card">
      <h2>Role Management</h2>
      <form className="inline-form" onSubmit={onCreate}>
        <input placeholder="code" value={form.code} onChange={(e) => setForm({ ...form, code: e.target.value })} />
        <input placeholder="name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <input placeholder="description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
        <button className="btn" type="submit">Create Role</button>
      </form>
      {error ? <p className="error-text">{error}</p> : null}
      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Code</th>
            <th>Name</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {roles.map((r) => (
            <tr key={r.id}>
              <td>{r.id}</td>
              <td>{r.code}</td>
              <td>{r.name}</td>
              <td>{r.status === 1 ? 'Active' : 'Disabled'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
