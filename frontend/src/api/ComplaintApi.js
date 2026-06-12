// API client configuration
const API_BASE = process.env.REACT_APP_API_BASE || 'http://localhost:8080/api/v1';

export const apiCall = async (endpoint, options = {}) => {
  const url = `${API_BASE}${endpoint}`;
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  };

  const response = await fetch(url, {
    method: options.method || 'GET',
    headers,
    body: options.body ? JSON.stringify(options.body) : undefined,
  });

  const data = await response.json();

  if (!response.ok) {
    throw new Error(data.message || 'API request failed');
  }

  return data;
};

export const apiCallWithAuth = async (endpoint, token, options = {}) => {
  return apiCall(endpoint, {
    ...options,
    headers: {
      Authorization: `Bearer ${token}`,
      ...options.headers,
    },
  });
};

// Auth API
export const login = (email, password, role) => {
  return apiCall('/auth/login', {
    method: 'POST',
    body: { email, password, role },
  });
};

// Complaints API
export const getComplaints = (token, regId = null) => {
  const endpoint = regId ? `/complaints?regId=${regId}` : '/complaints';
  return apiCallWithAuth(endpoint, token);
};

export const createComplaint = (token, complaintData) => {
  return apiCallWithAuth('/complaints', token, {
    method: 'POST',
    body: complaintData,
  });
};

