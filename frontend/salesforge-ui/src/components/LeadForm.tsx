import React, { useState, useEffect } from 'react';
import { apiService, Lead } from '../services/api';
import './LeadForm.css';

interface LeadFormProps {
  lead?: Lead;
  onSubmit: () => void;
  onCancel: () => void;
}

const LeadForm: React.FC<LeadFormProps> = ({ lead, onSubmit, onCancel }) => {
  const [formData, setFormData] = useState<Omit<Lead, 'id' | 'createdAt' | 'updatedAt' | 'owner'>>({
    companyName: '',
    contactName: '',
    email: '',
    phone: '',
    status: 'NEW',
    source: 'WEBSITE',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    if (lead) {
      setFormData({
        companyName: lead.companyName,
        contactName: lead.contactName,
        email: lead.email,
        phone: lead.phone || '',
        status: lead.status,
        source: lead.source,
      });
    }
  }, [lead]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));
    setError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      if (lead?.id) {
        // Update existing lead
        await apiService.updateLead(lead.id, { ...formData, id: lead.id });
      } else {
        // Create new lead
        await apiService.createLead(formData);
      }
      onSubmit();
    } catch (err: any) {
      console.error('Error saving lead:', err);
      setError(
        err.response?.data?.message || 
        `Failed to ${lead ? 'update' : 'create'} lead. Please try again.`
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>{lead ? 'Edit Lead' : 'Create New Lead'}</h3>
          <button className="close-button" onClick={onCancel}>×</button>
        </div>

        <form onSubmit={handleSubmit} className="lead-form">
          {error && (
            <div className="error-message">{error}</div>
          )}

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="companyName">Company Name *</label>
              <input
                type="text"
                id="companyName"
                name="companyName"
                value={formData.companyName}
                onChange={handleChange}
                required
                placeholder="Enter company name"
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="contactName">Contact Name *</label>
              <input
                type="text"
                id="contactName"
                name="contactName"
                value={formData.contactName}
                onChange={handleChange}
                required
                placeholder="Enter contact name"
                disabled={loading}
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="email">Email *</label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                required
                placeholder="Enter email address"
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="phone">Phone</label>
              <input
                type="tel"
                id="phone"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                placeholder="Enter phone number"
                disabled={loading}
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="status">Status</label>
              <select
                id="status"
                name="status"
                value={formData.status}
                onChange={handleChange}
                disabled={loading}
              >
                <option value="NEW">New</option>
                <option value="CONTACTED">Contacted</option>
                <option value="QUALIFIED">Qualified</option>
                <option value="LOST">Lost</option>
                <option value="CONVERTED">Converted</option>
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="source">Source</label>
              <select
                id="source"
                name="source"
                value={formData.source}
                onChange={handleChange}
                disabled={loading}
              >
                <option value="WEBSITE">Website</option>
                <option value="REFERRAL">Referral</option>
                <option value="COLD_CALL">Cold Call</option>
                <option value="EMAIL">Email</option>
                <option value="TRADE_SHOW">Trade Show</option>
                <option value="SOCIAL_MEDIA">Social Media</option>
                <option value="OTHER">Other</option>
              </select>
            </div>
          </div>

          <div className="form-actions">
            <button
              type="button"
              onClick={onCancel}
              className="cancel-button"
              disabled={loading}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="submit-button"
              disabled={loading || !formData.companyName || !formData.contactName || !formData.email}
            >
              {loading ? 'Saving...' : (lead ? 'Update Lead' : 'Create Lead')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LeadForm;