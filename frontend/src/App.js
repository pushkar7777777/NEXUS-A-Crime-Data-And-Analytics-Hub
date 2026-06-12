import React from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import LoginPage from './pages/LoginPage';
import ComplaintsListPage from './pages/ComplaintsListPage';
import './App.css';

function AppContent() {
  const { token, loading } = useAuth();

  if (loading) {
    return <div className="app-loading">Loading...</div>;
  }

  return token ? <ComplaintsListPage /> : <LoginPage />;
}

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

export default App;
