ALTER TABLE report.jasper_templates ADD COLUMN categoryId UUID;

UPDATE report.jasper_templates
SET categoryId = (
    SELECT id
    FROM report.report_categories
    WHERE name = 'Default Category'
    LIMIT 1
)
WHERE categoryId IS NULL;

ALTER TABLE report.jasper_templates ALTER COLUMN categoryId SET NOT NULL;

ALTER TABLE report.jasper_templates ADD CONSTRAINT fk_category
FOREIGN KEY (categoryId) REFERENCES report.report_categories (id)
ON DELETE SET NULL;
