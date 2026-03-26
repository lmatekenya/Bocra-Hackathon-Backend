INSERT INTO news (title, summary, content, category, image_url, slug)
VALUES
('BOCRA Launches National Cybersecurity Awareness Campaign',
 'A nationwide digital safety initiative to protect citizens and businesses from online threats.',
 'BOCRA launched a national campaign focused on practical digital hygiene, scam awareness, and coordinated response with industry and government stakeholders.',
 'Press Release',
 'https://images.unsplash.com/photo-1550751827-4bd374c3f58b?q=80&w=2070',
 'bocra-cybersecurity-awareness-campaign'),
('Type Approval Regulations Updated for 2026',
 'Updated technical standards align national compliance with modern international communications requirements.',
 'The latest type approval updates streamline import approvals, device compliance checks, and quality assurance for communications equipment entering Botswana.',
 'Regulation Update',
 'https://images.unsplash.com/photo-1518770660439-4636190af475?q=80&w=2070',
 'type-approval-regulations-updated-2026')
ON CONFLICT (slug) DO NOTHING;

INSERT INTO tenders (tender_number, title, type, publish_date, closing_date, status, description)
VALUES
('BOCRA/PT/001/2026',
 'Provision of National Cybersecurity Audit Services',
 'Open Domestic Tender',
 '2026-03-01',
 '2026-05-15',
 'OPEN',
 'BOCRA invites qualified firms to conduct a cybersecurity maturity and resilience audit across priority systems.'),
('BOCRA/EOI/002/2026',
 'Expression of Interest: Broadband Cost Modelling Framework',
 'Expression of Interest',
 '2026-02-14',
 '2026-04-30',
 'OPEN',
 'The assignment seeks a robust cost model to support evidence-based regulatory decisions and market reviews.')
ON CONFLICT (tender_number) DO NOTHING;

INSERT INTO documents (name, category, size, date, url)
VALUES
('Communications Regulatory Authority Act, 2012',
 'Acts and Legislation',
 '2.4 MB',
 'Apr 2013',
 'https://www.bocra.org.bw/sites/default/files/documents/CRA%20Act%202012.pdf'),
('BOCRA Licensing Framework',
 'Guidelines & Frameworks',
 '3.2 MB',
 'Jan 2023',
 'https://www.bocra.org.bw/sites/default/files/documents/Licensing%20Framework.pdf'),
('BOCRA Annual Report 2024/2025',
 'Annual Reports',
 '8.5 MB',
 'Oct 2025',
 'https://www.bocra.org.bw/sites/default/files/documents/Annual%20Report.pdf')
ON CONFLICT DO NOTHING;

INSERT INTO stats (id, value, suffix, label, description)
VALUES
('broadband', 1.3, 'M', 'Broadband Subscriptions', 'Active mobile and fixed broadband subscriptions nationwide.'),
('mobile', 171.4, '%', 'Mobile Penetration', 'Total mobile SIM subscriptions per 100 inhabitants.'),
('complaints', 95.2, '%', 'Complaint Resolution', 'Percentage of consumer complaints resolved within service-level timelines.')
ON CONFLICT (id) DO UPDATE
SET value = EXCLUDED.value,
    suffix = EXCLUDED.suffix,
    label = EXCLUDED.label,
    description = EXCLUDED.description;
