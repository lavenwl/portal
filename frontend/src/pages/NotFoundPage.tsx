import { Link } from 'react-router-dom';

export function NotFoundPage() {
  return (
    <section className="card">
      <h2>Not Found</h2>
      <p>The page does not exist.</p>
      <Link to="/dashboard">Back to dashboard</Link>
    </section>
  );
}
