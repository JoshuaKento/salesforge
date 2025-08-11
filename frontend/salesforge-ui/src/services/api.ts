import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'https://salesforge-api.onrender.com/api/v1';

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
}

export interface Lead {
  id?: number;
  companyName: string;
  contactName: string;
  email: string;
  phone?: string;
  status: 'NEW' | 'CONTACTED' | 'QUALIFIED' | 'LOST' | 'CONVERTED';
  source: 'WEBSITE' | 'REFERRAL' | 'COLD_CALL' | 'EMAIL' | 'TRADE_SHOW' | 'SOCIAL_MEDIA' | 'OTHER';
  owner?: User;
  createdAt?: string;
  updatedAt?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  expiresAt: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export interface LeadSearchResponse {
  content: Lead[];
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  empty: boolean;
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: string;
  };
}

class ApiService {
  private axios;

  constructor() {
    this.axios = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Add request interceptor to include auth token
    this.axios.interceptors.request.use((config) => {
      const token = localStorage.getItem('auth_token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    });

    // Add response interceptor for error handling
    this.axios.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          // Clear token and redirect to login
          localStorage.removeItem('auth_token');
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
  }

  // Authentication endpoints
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await this.axios.post('/auth/login', credentials);
    return response.data;
  }

  async register(userData: RegisterRequest): Promise<any> {
    const response = await this.axios.post('/auth/register', userData);
    return response.data;
  }

  async logout(): Promise<void> {
    await this.axios.post('/auth/logout');
    localStorage.removeItem('auth_token');
  }

  // Lead management endpoints
  async getLeads(params?: {
    page?: number;
    size?: number;
    status?: string;
    source?: string;
    search?: string;
    startDate?: string;
    endDate?: string;
  }): Promise<LeadSearchResponse> {
    const response = await this.axios.get('/leads', { params });
    return response.data;
  }

  async getLeadById(id: number): Promise<Lead> {
    const response = await this.axios.get(`/leads/${id}`);
    return response.data;
  }

  async createLead(lead: Omit<Lead, 'id' | 'createdAt' | 'updatedAt'>): Promise<Lead> {
    const response = await this.axios.post('/leads', lead);
    return response.data;
  }

  async updateLead(id: number, lead: Lead): Promise<Lead> {
    const response = await this.axios.put(`/leads/${id}`, lead);
    return response.data;
  }

  async partialUpdateLead(id: number, updates: Partial<Lead>): Promise<Lead> {
    const response = await this.axios.patch(`/leads/${id}`, updates);
    return response.data;
  }

  async deleteLead(id: number): Promise<void> {
    await this.axios.delete(`/leads/${id}`);
  }

  async searchLeads(query: string, page?: number, size?: number): Promise<any> {
    const response = await this.axios.get('/leads/search', {
      params: { q: query, page, size },
    });
    return response.data;
  }

  async getLeadsByStatus(status: string): Promise<Lead[]> {
    const response = await this.axios.get(`/leads/status/${status}`);
    return response.data;
  }

  async getLeadsBySource(source: string, page?: number, size?: number): Promise<LeadSearchResponse> {
    const response = await this.axios.get(`/leads/source/${source}`, {
      params: { page, size },
    });
    return response.data;
  }

  async getLeadStatistics(): Promise<any> {
    const response = await this.axios.get('/leads/stats');
    return response.data;
  }

  async getLeadCounts(): Promise<Record<string, number>> {
    const response = await this.axios.get('/leads/count');
    return response.data;
  }
}

export const apiService = new ApiService();