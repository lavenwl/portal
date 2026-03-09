import { useEffect, useState } from 'react';
import { apiRequest } from '../lib/api';
import type { LoginLog, PageResponse } from '../types';

export function LoginLogsPage() {
  const [records, setRecords] = useState<LoginLog[]>([]);
  const [error, setError] = useState('');

  async function load() {
    try {
      setError('');
      const data = await apiRequest<PageResponse<LoginLog>>('/api/logins?page=0&size=50');
      setRecords(data.records);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch login logs');
    }
  }

  useEffect(() => {
    void load();
  }, []);

  return (
    <section className="card">
      <h2>Login Logs</h2>
      {error ? <p className="error-text">{error}</p> : null}
      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>User</th>
            <th>Success</th>
            <th>Reason</th>
            <th>IP</th>
            <th>Time</th>
          </tr>
        </thead>
        <tbody>
          {records.map((r) => (
            <tr key={r.id}>
              <td>{r.id}</td>
              <td>{r.username || '-'}</td>
              <td>{r.success === 1 ? 'Yes' : 'No'}</td>
              <td>{r.reason || '-'}</td>
              <td>{r.ip || '-'}</td>
              <td>{r.loginAt}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
