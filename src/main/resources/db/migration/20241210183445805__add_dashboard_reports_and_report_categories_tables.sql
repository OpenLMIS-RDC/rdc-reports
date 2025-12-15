CREATE TABLE IF NOT EXISTS report.report_categories (
    id UUID PRIMARY KEY NOT NULL,
    name text UNIQUE NOT NULL
);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM report.report_categories
        WHERE name = 'Default Category'
    ) THEN
        INSERT INTO report.report_categories (id, name) VALUES ('55e19d72-4d0b-4453-b627-2f3477681c24', 'Default Category');
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS report.dashboard_reports (
    id UUID PRIMARY KEY NOT NULL,
    name text UNIQUE NOT NULL,
    url text NOT NULL,
    type text NOT NULL,
    enabled boolean NOT NULL,
    showOnHomePage boolean NOT NULL,
    categoryId UUID NOT NULL,
    rightName text UNIQUE NOT NULL,
    CONSTRAINT fk_report_categories FOREIGN KEY (categoryId) REFERENCES report.report_categories (id) ON DELETE RESTRICT
);