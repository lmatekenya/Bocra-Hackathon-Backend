ALTER TABLE complaints
    ALTER COLUMN contact_number TYPE TEXT,
    ALTER COLUMN email TYPE TEXT;

ALTER TABLE inquiries
    ALTER COLUMN email TYPE TEXT;

ALTER TABLE cyber_incidents
    ALTER COLUMN email TYPE TEXT;

ALTER TABLE admin_users
    ADD COLUMN IF NOT EXISTS failed_attempts INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS locked_until TIMESTAMP,
    ADD COLUMN IF NOT EXISTS last_login_at TIMESTAMP;

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(80) NOT NULL,
    actor VARCHAR(120) NOT NULL,
    target VARCHAR(160),
    outcome VARCHAR(20) NOT NULL,
    details TEXT,
    source_ip VARCHAR(120),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON audit_logs (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_inquiries_submitted_at ON inquiries (submitted_at DESC);
CREATE INDEX IF NOT EXISTS idx_cyber_incidents_reported_at ON cyber_incidents (reported_at DESC);
