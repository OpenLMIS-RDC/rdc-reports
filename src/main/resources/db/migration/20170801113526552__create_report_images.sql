CREATE TABLE jasper_templates_report_images (
    jaspertemplateid uuid NOT NULL,
    reportimageid uuid NOT NULL
);

CREATE TABLE report_images (
    id uuid NOT NULL,
    data bytea,
    name text NOT NULL
);

ALTER TABLE ONLY report_images
    ADD CONSTRAINT report_images_pkey PRIMARY KEY (id);


ALTER TABLE ONLY report_images
    ADD CONSTRAINT report_images_name_uk UNIQUE (name);


ALTER TABLE ONLY jasper_templates_report_images
    ADD CONSTRAINT jasper_templates_id_fk FOREIGN KEY (jaspertemplateid) REFERENCES jasper_templates(id);


ALTER TABLE ONLY jasper_templates_report_images
    ADD CONSTRAINT report_images_id_fk FOREIGN KEY (reportimageid) REFERENCES report_images(id);
