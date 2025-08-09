-- V3: Production Optimizations and Constraints

-- Add additional constraints for data integrity
ALTER TABLE users ADD CONSTRAINT chk_users_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');
ALTER TABLE leads ADD CONSTRAINT chk_leads_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');
ALTER TABLE contacts ADD CONSTRAINT chk_contacts_email_format CHECK (email IS NULL OR email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-- Add partial indexes for better query performance on filtered data
CREATE INDEX CONCURRENTLY idx_leads_active_status ON leads(status) WHERE status IN ('NEW', 'CONTACTED', 'QUALIFIED');
CREATE INDEX CONCURRENTLY idx_leads_recent ON leads(created_at) WHERE created_at >= (CURRENT_DATE - INTERVAL '30 days');
CREATE INDEX CONCURRENTLY idx_opportunities_open ON opportunities(stage) WHERE stage NOT IN ('CLOSED_WON', 'CLOSED_LOST');
CREATE INDEX CONCURRENTLY idx_activities_recent ON activities(activity_date) WHERE activity_date >= (CURRENT_DATE - INTERVAL '7 days');

-- Add composite indexes for common query patterns
CREATE INDEX CONCURRENTLY idx_leads_owner_status ON leads(owner_id, status);
CREATE INDEX CONCURRENTLY idx_leads_owner_created ON leads(owner_id, created_at DESC);
CREATE INDEX CONCURRENTLY idx_opportunities_owner_stage ON opportunities(owner_id, stage);
CREATE INDEX CONCURRENTLY idx_activities_user_date ON activities(user_id, activity_date DESC);

-- Add function-based indexes for case-insensitive searches
CREATE INDEX CONCURRENTLY idx_leads_company_name_lower ON leads(LOWER(company_name));
CREATE INDEX CONCURRENTLY idx_leads_contact_name_lower ON leads(LOWER(contact_name));
CREATE INDEX CONCURRENTLY idx_accounts_name_lower ON accounts(LOWER(name));

-- Create updated_at trigger function for automatic timestamp updates
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Add updated_at triggers to all tables
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_accounts_updated_at BEFORE UPDATE ON accounts FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_leads_updated_at BEFORE UPDATE ON leads FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_contacts_updated_at BEFORE UPDATE ON contacts FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_opportunities_updated_at BEFORE UPDATE ON opportunities FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_activities_updated_at BEFORE UPDATE ON activities FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Add database-level security: Row Level Security setup (commented out for initial deployment)
-- ALTER TABLE leads ENABLE ROW LEVEL SECURITY;
-- CREATE POLICY leads_owner_policy ON leads FOR ALL TO authenticated USING (owner_id = current_setting('app.current_user_id')::BIGINT);

-- Add table comments for documentation
COMMENT ON TABLE users IS 'System users with role-based access control';
COMMENT ON TABLE accounts IS 'Customer accounts/organizations';  
COMMENT ON TABLE leads IS 'Potential customers in the sales pipeline';
COMMENT ON TABLE contacts IS 'Individual contacts within customer accounts';
COMMENT ON TABLE opportunities IS 'Sales opportunities and deals';
COMMENT ON TABLE activities IS 'Interactions and tasks related to sales activities';

-- Add column comments for key fields
COMMENT ON COLUMN users.role IS 'User role: ADMIN, MANAGER, or SALES_REP';
COMMENT ON COLUMN leads.status IS 'Lead status in sales pipeline';
COMMENT ON COLUMN leads.source IS 'Source of lead generation';
COMMENT ON COLUMN opportunities.stage IS 'Sales opportunity stage';
COMMENT ON COLUMN opportunities.probability IS 'Probability of closing (0-100%)';
COMMENT ON COLUMN activities.type IS 'Type of activity: CALL, EMAIL, MEETING, TASK, NOTE';

-- Create a view for active leads with owner information
CREATE VIEW v_active_leads AS
SELECT 
    l.id,
    l.company_name,
    l.contact_name,
    l.email,
    l.phone,
    l.status,
    l.source,
    l.created_at,
    l.updated_at,
    u.first_name as owner_first_name,
    u.last_name as owner_last_name,
    u.email as owner_email
FROM leads l
JOIN users u ON l.owner_id = u.id
WHERE l.status NOT IN ('LOST', 'CONVERTED');

COMMENT ON VIEW v_active_leads IS 'Active leads with owner information for reporting';

-- Add database statistics collection
ANALYZE users;
ANALYZE accounts;
ANALYZE leads;
ANALYZE contacts;
ANALYZE opportunities;
ANALYZE activities;