--
-- Name: jaspertemplate_requiredrights; Type: TABLE; Schema: report; Owner: postgres
--

CREATE TABLE jaspertemplate_requiredrights (
    jaspertemplateid uuid NOT NULL,
    requiredrights character varying(255)
);

--
-- Name: jaspertemplate_requiredrights fk_jaspertemplate_requiredrights__jasper_templates; Type: FK CONSTRAINT; Schema: report; Owner: postgres
--

ALTER TABLE ONLY jaspertemplate_requiredrights
    ADD CONSTRAINT fk_jaspertemplate_requiredrights__jasper_templates
    FOREIGN KEY (jaspertemplateid) REFERENCES jasper_templates(id);
