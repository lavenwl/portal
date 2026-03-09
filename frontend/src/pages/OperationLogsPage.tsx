import { useEffect, useState } from 'react';
import { apiRequest } from '../lib/api';
import type { OperationLog, PageResponse } from '../types';

export function OperationLogsPage() {
  const [records, setRecords] = useState<OperationLog[]>([]);
  const [error, setError] = useState('');

  async function load() {
    try {
      setError('');
      const data = await apiRequest<PageResponse<OperationLog>>('/api/operations?page=0&size=50');
      setRecords(data.records);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch operation logs');
    }
  }

  useEffect(() => {
    void load();
  }, []);

  return (
    <section className="card">
      <h2>Operation Logs</h2>
      {error ? <p className="error-text">{error}</p> : null}
      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Module</th>
            <th>Action</th>
            <th>User</th>
            <th>Success</th>
            <th>Duration</th>
            <th>Time</th>
          </tr>
        </thead>
        <tbody>
          {records.map((r) => (
            <tr key={r.id}>
              <td>{r.id}</td>
              <td>{r.module}</td>
              <td>{r.action}</td>
              <td>{r.username || '-'}</td>
              <td>{r.success === 1 ? 'Yes' : 'No'}</td>
              <td>{r.durationMs ?? 0}ms</td>
              <td>{r.operatedAt}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
