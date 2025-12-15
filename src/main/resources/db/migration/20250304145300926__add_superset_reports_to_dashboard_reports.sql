INSERT INTO report.dashboard_reports (id, name, url, type, enabled, showonhomepage, categoryid, rightname) 
VALUES 
('824376c3-859a-44b4-9a22-cd818cb9a0b6', 'REPORTING RATE AND TIMELINESS', 'https://superset-uat.openlmis.org/superset/dashboard/reporting_rate_and_timeliness', 'SUPERSET', true, false, '55e19d72-4d0b-4453-b627-2f3477681c24', 'REPORTING_RATE_AND_TIMELINESS_RIGHT'),
('bf749b36-eb1b-4e8f-a8b4-323c75e1e62b', 'STOCK STATUS', 'https://superset-uat.openlmis.org/superset/dashboard/stock_status', 'SUPERSET', true, false, '55e19d72-4d0b-4453-b627-2f3477681c24', 'STOCK_STATUS_RIGHT'),
('aec75a9f-2143-4ea4-be85-7ca489d33f05', 'STOCKOUTS', 'https://superset-uat.openlmis.org/superset/dashboard/stockouts', 'SUPERSET', true, false, '55e19d72-4d0b-4453-b627-2f3477681c24', 'STOCKOUTS_RIGHT'),
('ea988f1f-2650-47eb-a3df-790a07395626', 'CONSUMPTION', 'https://superset-uat.openlmis.org/superset/dashboard/consumption', 'SUPERSET', true, false, '55e19d72-4d0b-4453-b627-2f3477681c24', 'CONSUMPTION_RIGHT'),
('1056f357-bc00-403e-8993-5142eb283734', 'ORDERS', 'https://superset-uat.openlmis.org/superset/dashboard/orders', 'SUPERSET', true, false, '55e19d72-4d0b-4453-b627-2f3477681c24', 'ORDERS_RIGHT'),
('8c578bf1-ac36-488c-8146-b7aa59e44d2c', 'ADJUSTMENTS', 'https://superset-uat.openlmis.org/superset/dashboard/adjustments', 'SUPERSET', true, false, '55e19d72-4d0b-4453-b627-2f3477681c24', 'ADJUSTMENTS_RIGHT'),
('0048b66b-5cba-48bb-8fd8-90e76a991e9d', 'ADMINISTRATIVE', 'https://superset-uat.openlmis.org/superset/dashboard/administrative', 'SUPERSET', true, false, '55e19d72-4d0b-4453-b627-2f3477681c24', 'ADMINISTRATIVE_RIGHT')
ON CONFLICT (id) DO NOTHING;