INSERT INTO complaints (ticket_id, full_name, contact_number, service_provider, provider_reference, email, complaint_details, status, created_at)
VALUES
('BOCRA-DEMO001', 'Kagiso Moagi', '+267 71234567', 'Mascom', 'MSC-44512', 'kagiso.moagi@example.com',
 'Intermittent network outages have affected mobile money transactions for 3 days in Mogoditshane.', 'PENDING', CURRENT_TIMESTAMP - INTERVAL '3 days'),
('BOCRA-DEMO002', 'Lesego Kelebeng', '+267 72448811', 'Orange', 'ORG-88911', 'lesego.kelebeng@example.com',
 'Unexpected value-added services were activated without consent and airtime was deducted repeatedly.', 'IN_REVIEW', CURRENT_TIMESTAMP - INTERVAL '2 days'),
('BOCRA-DEMO003', 'Thato Motsamai', '+267 76557731', 'BTC', 'BTC-10293', 'thato.motsamai@example.com',
 'Fiber broadband speeds are consistently below contracted levels during business hours.', 'RESOLVED', CURRENT_TIMESTAMP - INTERVAL '1 day')
ON CONFLICT (ticket_id) DO NOTHING;

INSERT INTO inquiries (first_name, last_name, email, inquiry_type, message, status, submitted_at)
VALUES
('Neo', 'Molefe', 'neo.molefe@example.com', 'Licensing Support', 'I need guidance on required documents for an ISP class license renewal.', 'NEW', CURRENT_TIMESTAMP - INTERVAL '18 hours'),
('Palesa', 'Ditshego', 'palesa.ditshego@example.com', 'General Inquiry', 'Please share the expected timeline for the next public consultation on spectrum pricing.', 'NEW', CURRENT_TIMESTAMP - INTERVAL '12 hours'),
('Tebogo', 'Seretse', 'tebogo.seretse@example.com', 'Media Query', 'Requesting official spokesperson comments on 2026 cybersecurity readiness priorities.', 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '6 hours');

INSERT INTO cyber_incidents (incident_id, reporter_type, organization_name, incident_type, date_of_incident, email, description, status, reported_at)
VALUES
('INC-DEMO001', 'Corporate/SME', 'Gaborone Logistics (Pty) Ltd', 'Phishing/Scam', '2026-03-24', 'it@gaboronelogistics.co.bw',
 'Multiple employees received spoofed payroll emails requesting credential resets via malicious links.', 'INVESTIGATING', CURRENT_TIMESTAMP - INTERVAL '20 hours'),
('INC-DEMO002', 'Government/Parastatal', 'North District Utility Office', 'Malware/Ransomware', '2026-03-23', 'security@northutility.gov.bw',
 'Endpoint protection detected ransomware behavior on two desktop workstations and isolated them.', 'CONTAINED', CURRENT_TIMESTAMP - INTERVAL '10 hours')
ON CONFLICT (incident_id) DO NOTHING;

INSERT INTO audit_logs (event_type, actor, target, outcome, details, source_ip, created_at)
VALUES
('ADMIN_LOGIN', 'admin', NULL, 'SUCCESS', 'Administrator authenticated successfully.', '196.216.45.11', CURRENT_TIMESTAMP - INTERVAL '5 hours'),
('ADMIN_VIEW', 'admin', NULL, 'SUCCESS', 'Admin viewed complaints queue.', '196.216.45.11', CURRENT_TIMESTAMP - INTERVAL '4 hours'),
('COMPLAINT_SUBMITTED', 'PUBLIC', 'BOCRA-DEMO003', 'SUCCESS', 'Consumer complaint submitted.', '105.248.17.92', CURRENT_TIMESTAMP - INTERVAL '1 day'),
('CYBER_INCIDENT_REPORTED', 'PUBLIC', 'INC-DEMO002', 'SUCCESS', 'Cyber incident reported by public user.', '154.72.33.10', CURRENT_TIMESTAMP - INTERVAL '10 hours')
ON CONFLICT DO NOTHING;
