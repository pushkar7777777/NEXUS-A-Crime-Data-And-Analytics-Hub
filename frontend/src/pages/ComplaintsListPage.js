import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { getComplaints } from '../api/ComplaintApi';
import './ComplaintsListPage.css';

function ComplaintsListPage() {
  const { user, token, logout } = useAuth();
  const [complaints, setComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchComplaints();
  }, [token]);

  const fetchComplaints = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await getComplaints(token);
      if (response.success) {
        setComplaints(response.data || []);
      } else {
        setError(response.message || 'Failed to fetch complaints');
      }
    } catch (err) {
      setError(err.message || 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="complaints-page">
      <header className="complaints-header">
        <h1>NEXUS - Complaints Dashboard</h1>
        <div className="header-right">
          <span className="user-info">
            {user?.email} ({user?.role})
          </span>
          <button onClick={logout} className="logout-btn">
            Logout
          </button>
        </div>
      </header>

      <main className="complaints-main">
        <h2>Complaints List (from API)</h2>

        {error && <div className="error-message">{error}</div>}

        {loading ? (
          <p>Loading complaints...</p>
        ) : complaints.length === 0 ? (
          <p>No complaints found.</p>
        ) : (
          <div className="complaints-table">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Type</th>
                  <th>Date of Incident</th>
                  <th>Location</th>
                  <th>Status</th>
                  <th>Filed By</th>
                  <th>Filed Date</th>
                </tr>
              </thead>
              <tbody>
                {complaints.map((complaint) => (
                  <tr key={complaint.complaintId}>
                    <td>{complaint.complaintId}</td>
                    <td>{complaint.complaintType}</td>
                    <td>{complaint.dateOfIncident}</td>
                    <td>{complaint.locationOfIncident}</td>
                    <td>
                      <span className={`status-badge status-${complaint.currentStatus?.toLowerCase()}`}>
                        {complaint.currentStatus}
                      </span>
                    </td>
                    <td>{complaint.filedBy}</td>
                    <td>{complaint.dateFiled ? new Date(complaint.dateFiled).toLocaleDateString() : '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <button onClick={fetchComplaints} className="refresh-btn">
          Refresh
        </button>
      </main>
    </div>
  );
}

export default ComplaintsListPage;

