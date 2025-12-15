-- add new column to table
ALTER TABLE report.jasper_templates
ADD COLUMN visible boolean DEFAULT true;

-- update existing entries to have default value
UPDATE report.jasper_templates SET visible = true;

-- set false for 'Pick Pack List report'
UPDATE report.jasper_templates SET visible = false WHERE id = '583ccc35-88b7-48a8-9193-6c4857d3ff60';
