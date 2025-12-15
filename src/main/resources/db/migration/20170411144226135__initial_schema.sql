--
-- Name: jasper_templates; Type: TABLE; Schema: report; Owner: postgres; Tablespace:
--

CREATE TABLE jasper_templates (
    id uuid NOT NULL,
    data bytea,
    description text,
    name text NOT NULL,
    type text
);


--
-- Name: template_parameters; Type: TABLE; Schema: report; Owner: postgres; Tablespace:
--

CREATE TABLE template_parameters (
    id uuid NOT NULL,
    datatype text,
    defaultvalue text,
    description text,
    displayname text,
    name text,
    selectExpression text,
    selectProperty text,
    displayProperty text,
    required boolean,
    templateid uuid NOT NULL
);


--
-- Name: configuration_settings; Type: TABLE; Schema: report; Owner: postgres; Tablespace:
--

CREATE TABLE configuration_settings (
    key character varying(255) NOT NULL,
    value text NOT NULL
);


--
-- Name: jasper_templates_pkey; Type: CONSTRAINT; Schema: report; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY jasper_templates
    ADD CONSTRAINT jasper_templates_pkey PRIMARY KEY (id);


--
-- Name: template_parameters_pkey; Type: CONSTRAINT; Schema: report; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY template_parameters
    ADD CONSTRAINT template_parameters_pkey PRIMARY KEY (id);


--
-- Name: configuration_settings_pkey; Type: CONSTRAINT; Schema: report; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY configuration_settings
    ADD CONSTRAINT configuration_settings_pkey PRIMARY KEY (key);


--
-- Name: uk_5878s5vb2v4y53vun95nrdvgw; Type: CONSTRAINT; Schema: report; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY jasper_templates
    ADD CONSTRAINT uk_5878s5vb2v4y53vun95nrdvgw UNIQUE (name);


--
-- Name: fk_qww3p7ho2t5jyutkllrh64khr; Type: FK CONSTRAINT; Schema: report; Owner: postgres
--

ALTER TABLE ONLY template_parameters
    ADD CONSTRAINT fk_qww3p7ho2t5jyutkllrh64khr FOREIGN KEY (templateid) REFERENCES jasper_templates(id);
