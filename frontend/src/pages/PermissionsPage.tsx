import { FormEvent, useEffect, useState } from 'react';
import { apiRequest } from '../lib/api';
import type { Permission } from '../types';

export function PermissionsPage() {
  const [permissions, setPermissions] = useState<Permission[]>([]);
  const [error, setError] = useState('');
  const [form, setForm] = useState({ code: '', name: '', type: 'API', resource: '', method: '' });

  async function loadPermissions() {
    try {
      setError('');
      const data = await apiRequest<Permission[]>('/api/permissions');
      setPermissions(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch permissions');
    }
  }

  useEffect(() => {
    void loadPermissions();
  }, []);

  async function onCreate(e: FormEvent) {
    e.preventDefault();
    try {
      await apiRequest<Permission>('/api/permissions', {
        method: 'POST',
        body: JSON.stringify(form)
      });
      setForm({ code: '', name: '', type: 'API', resource: '', method: '' });
      await loadPermissions();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Create permission failed');
    }
  }

  return (
    <section className="card">
      <h2>Permission Management</h2>
      <form className="inline-form" onSubmit={onCreate}>
        <input placeholder="code" value={form.code} onChange={(e) => setForm({ ...form, code: e.target.value })} />
        <input placeholder="name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <input placeholder="resource" value={form.resource} onChange={(e) => setForm({ ...form, resource: e.target.value })} />
        <input placeholder="method" value={form.method} onChange={(e) => setForm({ ...form, method: e.target.value })} />
        <button className="btn" type="submit">Create Permission</button>
      </form>
      {error ? <p className="error-text">{error}</p> : null}
      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Code</th>
            <th>Name</th>
            <th>Resource</th>
            <th>Method</th>
          </tr>
        </thead>
        <tbody>
          {permissions.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.code}</td>
              <td>{p.name}</td>
              <td>{p.resource || '-'}</td>
              <td>{p.method || '-'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
