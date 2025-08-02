-- Insert sample users
INSERT INTO users (email, password, first_name, last_name, role, active) VALUES
('admin@example.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPHzZenye', 'Admin', 'User', 'ADMIN', true),
('manager@example.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPHzZenye', 'Sales', 'Manager', 'MANAGER', true),
('sales@example.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPHzZenye', 'Sales', 'Rep', 'SALES_REP', true);

-- Password for all users is 'password123'

-- Insert sample accounts
INSERT INTO accounts (name, industry, website, phone, employee_count, annual_revenue, address, owner_id) VALUES
('TechCorp Inc', 'Technology', 'https://techcorp.com', '+1-555-0101', 500, 10000000.00, '123 Tech Street, Silicon Valley, CA', 3),
('Global Solutions LLC', 'Consulting', 'https://globalsolutions.com', '+1-555-0102', 150, 5000000.00, '456 Business Ave, New York, NY', 3),
('Innovation Labs', 'Research', 'https://innovationlabs.com', '+1-555-0103', 75, 2500000.00, '789 Research Blvd, Boston, MA', 3);

-- Insert sample contacts
INSERT INTO contacts (first_name, last_name, email, phone, title, account_id, is_primary) VALUES
('John', 'Smith', 'john.smith@techcorp.com', '+1-555-0201', 'CTO', 1, true),
('Jane', 'Doe', 'jane.doe@techcorp.com', '+1-555-0202', 'VP Engineering', 1, false),
('Bob', 'Johnson', 'bob.johnson@globalsolutions.com', '+1-555-0203', 'CEO', 2, true),
('Alice', 'Wilson', 'alice.wilson@innovationlabs.com', '+1-555-0204', 'Research Director', 3, true);

-- Insert sample leads
INSERT INTO leads (company_name, contact_name, email, phone, status, source, owner_id) VALUES
('StartupXYZ', 'Mike Brown', 'mike@startupxyz.com', '+1-555-0301', 'NEW', 'WEBSITE', 3),
('Enterprise Corp', 'Sarah Davis', 'sarah@enterprise.com', '+1-555-0302', 'CONTACTED', 'REFERRAL', 3),
('Small Business Inc', 'Tom Wilson', 'tom@smallbiz.com', '+1-555-0303', 'QUALIFIED', 'COLD_CALL', 3);

-- Insert sample opportunities
INSERT INTO opportunities (name, amount, stage, probability, close_date, account_id, primary_contact_id, owner_id) VALUES
('TechCorp Software License', 250000.00, 'PROPOSAL', 75, '2024-03-15', 1, 1, 3),
('Global Solutions Consulting', 150000.00, 'NEGOTIATION_REVIEW', 90, '2024-02-28', 2, 3, 3),
('Innovation Labs Research Platform', 75000.00, 'QUALIFICATION', 50, '2024-04-30', 3, 4, 3);

-- Insert sample activities
INSERT INTO activities (type, subject, description, activity_date, status, user_id, contact_id, opportunity_id) VALUES
('CALL', 'Initial Discovery Call', 'Discussed requirements and timeline', '2024-01-15 10:00:00', 'COMPLETED', 3, 1, 1),
('EMAIL', 'Proposal Follow-up', 'Sent detailed proposal document', '2024-01-20 14:30:00', 'COMPLETED', 3, 1, 1),
('MEETING', 'Contract Review Meeting', 'Review contract terms with legal team', '2024-02-25 15:00:00', 'PLANNED', 3, 3, 2),
('TASK', 'Prepare Demo', 'Set up product demonstration for next week', '2024-02-20 09:00:00', 'IN_PROGRESS', 3, 4, 3);