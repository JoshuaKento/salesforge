import React, { useState, useEffect } from 'react';
import { apiService, Lead, LeadSearchResponse } from '../services/api';
import LeadForm from './LeadForm';
import './LeadsTable.css';

const LeadsTable: React.FC = () => {
  const [leads, setLeads] = useState<Lead[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>('');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [pageSize] = useState(10);
  
  // Filters
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [sourceFilter, setSourceFilter] = useState<string>('');
  const [searchTerm, setSearchTerm] = useState<string>('');
  
  // Modal states
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [editingLead, setEditingLead] = useState<Lead | null>(null);

  const loadLeads = async () => {
    setLoading(true);
    setError('');

    try {
      const params: any = {
        page: currentPage,
        size: pageSize,
      };

      if (statusFilter) params.status = statusFilter;
      if (sourceFilter) params.source = sourceFilter;
      if (searchTerm) params.search = searchTerm;

      const response: LeadSearchResponse = await apiService.getLeads(params);
      
      setLeads(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (err: any) {
      console.error('Error loading leads:', err);
      setError('Failed to load leads');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadLeads();
  }, [currentPage, statusFilter, sourceFilter, searchTerm]); // eslint-disable-line react-hooks/exhaustive-deps

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this lead?')) {
      try {
        await apiService.deleteLead(id);
        loadLeads(); // Reload the list
      } catch (err: any) {
        console.error('Error deleting lead:', err);
        alert('Failed to delete lead. You may not have permission.');
      }
    }
  };

  const handleEdit = (lead: Lead) => {
    setEditingLead(lead);
  };

  const handleFormSubmit = () => {
    setShowCreateForm(false);
    setEditingLead(null);
    loadLeads(); // Reload the list
  };

  const handleFormCancel = () => {
    setShowCreateForm(false);
    setEditingLead(null);
  };

  const handleLogout = async () => {
    try {
      await apiService.logout();
      window.location.reload();
    } catch (err) {
      localStorage.removeItem('auth_token');
      window.location.reload();
    }
  };

  const getUserData = () => {
    const userData = localStorage.getItem('user_data');
    return userData ? JSON.parse(userData) : null;
  };

  const user = getUserData();

  if (loading && leads.length === 0) {
    return (
      <div className="leads-container">
        <div className="loading">Loading leads...</div>
      </div>
    );
  }

  return (
    <div className="leads-container">
      {/* Header */}
      <div className="leads-header">
        <div className="header-left">
          <h1>SalesForge CRM</h1>
          <p>Welcome back, {user?.firstName} {user?.lastName}</p>
        </div>
        <div className="header-right">
          <span className="user-role">{user?.role}</span>
          <button onClick={handleLogout} className="logout-button">
            Logout
          </button>
        </div>
      </div>

      {/* Filters and Actions */}
      <div className="leads-controls">
        <div className="filters">
          <input
            type="text"
            placeholder="Search leads..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="search-input"
          />
          
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="filter-select"
          >
            <option value="">All Statuses</option>
            <option value="NEW">New</option>
            <option value="CONTACTED">Contacted</option>
            <option value="QUALIFIED">Qualified</option>
            <option value="LOST">Lost</option>
            <option value="CONVERTED">Converted</option>
          </select>

          <select
            value={sourceFilter}
            onChange={(e) => setSourceFilter(e.target.value)}
            className="filter-select"
          >
            <option value="">All Sources</option>
            <option value="WEBSITE">Website</option>
            <option value="REFERRAL">Referral</option>
            <option value="COLD_CALL">Cold Call</option>
            <option value="EMAIL">Email</option>
            <option value="TRADE_SHOW">Trade Show</option>
            <option value="SOCIAL_MEDIA">Social Media</option>
            <option value="OTHER">Other</option>
          </select>
        </div>

        <button
          onClick={() => setShowCreateForm(true)}
          className="create-button"
        >
          + Create Lead
        </button>
      </div>

      {error && (
        <div className="error-message">{error}</div>
      )}

      {/* Results info */}
      <div className="results-info">
        Showing {leads.length} of {totalElements} leads
      </div>

      {/* Table */}
      <div className="table-container">
        <table className="leads-table">
          <thead>
            <tr>
              <th>Company</th>
              <th>Contact</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Status</th>
              <th>Source</th>
              <th>Created</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {leads.map((lead) => (
              <tr key={lead.id}>
                <td className="company-cell">{lead.companyName}</td>
                <td>{lead.contactName}</td>
                <td className="email-cell">{lead.email}</td>
                <td>{lead.phone || '-'}</td>
                <td>
                  <span className={`status-badge status-${lead.status.toLowerCase()}`}>
                    {lead.status}
                  </span>
                </td>
                <td>
                  <span className="source-badge">
                    {lead.source.replace('_', ' ')}
                  </span>
                </td>
                <td className="date-cell">
                  {lead.createdAt ? new Date(lead.createdAt).toLocaleDateString() : '-'}
                </td>
                <td className="actions-cell">
                  <button
                    onClick={() => handleEdit(lead)}
                    className="edit-button"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => handleDelete(lead.id!)}
                    className="delete-button"
                    disabled={user?.role === 'SALES_REP'}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {leads.length === 0 && !loading && (
          <div className="no-results">
            No leads found. Try adjusting your filters or create a new lead.
          </div>
        )}
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="pagination">
          <button
            onClick={() => setCurrentPage(currentPage - 1)}
            disabled={currentPage === 0}
            className="pagination-button"
          >
            Previous
          </button>
          <span className="pagination-info">
            Page {currentPage + 1} of {totalPages}
          </span>
          <button
            onClick={() => setCurrentPage(currentPage + 1)}
            disabled={currentPage >= totalPages - 1}
            className="pagination-button"
          >
            Next
          </button>
        </div>
      )}

      {/* Modals */}
      {showCreateForm && (
        <LeadForm
          onSubmit={handleFormSubmit}
          onCancel={handleFormCancel}
        />
      )}

      {editingLead && (
        <LeadForm
          lead={editingLead}
          onSubmit={handleFormSubmit}
          onCancel={handleFormCancel}
        />
      )}
    </div>
  );
};

export default LeadsTable;