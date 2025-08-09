import React, { useState } from 'react';
import { apiService, LoginRequest } from '../services/api';
import './LoginForm.css';

interface LoginFormProps {
  onLoginSuccess: () => void;
}

const LoginForm: React.FC<LoginFormProps> = ({ onLoginSuccess }) => {
  const [formData, setFormData] = useState<LoginRequest>({
    email: '',
    password: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string>('');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));
    // Don't clear error immediately - let user see it
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await apiService.login(formData);
      
      if (response.success && response.token) {
        localStorage.setItem('auth_token', response.token);
        localStorage.setItem('user_data', JSON.stringify({
          email: response.email,
          firstName: response.firstName,
          lastName: response.lastName,
          role: response.role,
        }));
        onLoginSuccess();
      } else {
        setError('Login failed. Please check your credentials.');
      }
    } catch (err: any) {
      console.error('Login error:', err);
      setError(
        err.response?.data?.error || 
        'Login failed. Please check your credentials and try again.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <form onSubmit={handleSubmit} className="login-form">
        <div className="login-header">
          <h2>SalesForge Login</h2>
          <p>Sign in to your account</p>
        </div>

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        <div className="form-group">
          <label htmlFor="email">Email:</label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
            placeholder="Enter your email"
            disabled={loading}
          />
        </div>

        <div className="form-group">
          <label htmlFor="password">Password:</label>
          <input
            type="password"
            id="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            required
            placeholder="Enter your password"
            disabled={loading}
            minLength={8}
          />
        </div>

        <button 
          type="submit" 
          className="login-button"
          disabled={loading || !formData.email || !formData.password}
        >
          {loading ? 'Signing In...' : 'Sign In'}
        </button>

        <div className="login-footer">
          <p>Demo Credentials:</p>
          <small>Email: admin@test.com | Password: admin12345</small>
          <br />
          <small style={{ color: '#999', fontSize: '0.75rem' }}>
            Or click "Test Mode" below to bypass login
          </small>
        </div>

        <button 
          type="button" 
          onClick={() => {
            localStorage.setItem('auth_token', 'test-token-12345');
            localStorage.setItem('user_data', JSON.stringify({
              email: 'test@example.com',
              firstName: 'Test',
              lastName: 'User', 
              role: 'ADMIN'
            }));
            onLoginSuccess();
          }}
          style={{
            width: '100%',
            marginTop: '0.5rem',
            padding: '0.5rem',
            background: '#28a745',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            fontSize: '0.85rem'
          }}
        >
          🧪 Test Mode (Bypass Login)
        </button>
      </form>
    </div>
  );
};

export default LoginForm;