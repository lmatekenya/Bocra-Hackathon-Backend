CREATE TABLE IF NOT EXISTS admin_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(80) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'ADMIN',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS complaints (
    id BIGSERIAL PRIMARY KEY,
    ticket_id VARCHAR(64) NOT NULL UNIQUE,
    full_name VARCHAR(160) NOT NULL,
    contact_number VARCHAR(50) NOT NULL,
    service_provider VARCHAR(120) NOT NULL,
    provider_reference VARCHAR(120),
    email VARCHAR(255) NOT NULL,
    complaint_details TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inquiries (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(120) NOT NULL,
    last_name VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL,
    inquiry_type VARCHAR(120) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'NEW',
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cyber_incidents (
    id BIGSERIAL PRIMARY KEY,
    incident_id VARCHAR(64) NOT NULL UNIQUE,
    reporter_type VARCHAR(120) NOT NULL,
    organization_name VARCHAR(160),
    incident_type VARCHAR(120) NOT NULL,
    date_of_incident VARCHAR(40) NOT NULL,
    email VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'INVESTIGATING',
    reported_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS news (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(220) NOT NULL,
    summary TEXT NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(120) NOT NULL,
    image_url VARCHAR(500),
    slug VARCHAR(220) NOT NULL UNIQUE,
    published_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tenders (
    id BIGSERIAL PRIMARY KEY,
    tender_number VARCHAR(80) NOT NULL UNIQUE,
    title VARCHAR(220) NOT NULL,
    type VARCHAR(100) NOT NULL,
    publish_date VARCHAR(30),
    closing_date VARCHAR(30),
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS documents (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(220) NOT NULL,
    category VARCHAR(120) NOT NULL,
    size VARCHAR(40),
    date VARCHAR(40),
    url VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stats (
    id VARCHAR(80) PRIMARY KEY,
    value DOUBLE PRECISION,
    suffix VARCHAR(20),
    label VARCHAR(160),
    description VARCHAR(400)
);

CREATE INDEX IF NOT EXISTS idx_complaints_created_at ON complaints (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_news_published_at ON news (published_at DESC);
CREATE INDEX IF NOT EXISTS idx_tenders_closing_date ON tenders (closing_date);
CREATE INDEX IF NOT EXISTS idx_documents_category ON documents (category);
