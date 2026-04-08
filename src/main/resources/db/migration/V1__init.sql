CREATE DATABASE IF NOT EXISTS ms_contratos;
USE ms_contratos;


CREATE TABLE document_object (
  document_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
  client_id      BIGINT NOT NULL,
  site_id        BIGINT NULL,
  file_name      VARCHAR(255),
  mime_type      VARCHAR(100),
  storage_uri    VARCHAR(1000) NOT NULL,  -- S3/MinIO/Blob
  sha256         VARCHAR(64),
  size_bytes     BIGINT,
  is_immutable   CHAR(1) DEFAULT 'Y',
  created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
  created_by     VARCHAR(120) NULL,       -- actor externo (email/sub)
  KEY idx_doc_client (client_id),
  KEY idx_doc_site (site_id),
  KEY idx_doc_sha (sha256)
) ENGINE=InnoDB;

CREATE TABLE contract (
  contract_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
  client_id        BIGINT NOT NULL,
  site_id          BIGINT NULL,

  contract_number  VARCHAR(80) NOT NULL,
  contract_type    VARCHAR(50) NOT NULL,       -- o FK a catálogo interno
  category         VARCHAR(80) NULL,

  sign_date        DATE,
  start_date       DATE,
  end_date         DATE,
  duration_months  INT,

  status           VARCHAR(30) NOT NULL DEFAULT 'DRAFT', -- DRAFT/REVIEWED/ACTIVE/EXPIRED/TERMINATED
  created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       DATETIME NULL,

  UNIQUE KEY uq_contract (client_id, contract_id),
  KEY idx_contract_client (client_id, status),
  KEY idx_contract_dates (start_date, end_date)
) ENGINE=InnoDB;

CREATE TABLE contract_version (
  contract_version_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_id         BIGINT NOT NULL,
  client_id           BIGINT NOT NULL,
  site_id             BIGINT NULL,

  version_number      INT NOT NULL,
  source_document_id  BIGINT NOT NULL,
  ocr_document_id     BIGINT NULL,

  extraction_json     JSON NULL,
  model_info_json     JSON NULL,
  pipeline_status     VARCHAR(30) NOT NULL DEFAULT 'EXTRACTED', -- UPLOADED/OCR_DONE/EXTRACTED/IN_REVIEW/APPROVED

  created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by          VARCHAR(120) NULL,

  UNIQUE KEY uq_contract_ver (contract_id, contract_version_id),
  KEY idx_cv_contract (contract_id, version_number),
  KEY idx_cv_client (client_id, pipeline_status),

  FOREIGN KEY (contract_id) REFERENCES contract(contract_id),
  FOREIGN KEY (source_document_id) REFERENCES document_object(document_id),
  FOREIGN KEY (ocr_document_id) REFERENCES document_object(document_id)
) ENGINE=InnoDB;

-- =========================
-- B) PARTES (LANDLORD/TENANT/OPERATOR/GUARANTOR)
-- =========================
CREATE TABLE party (
  party_id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  client_id            BIGINT NOT NULL,
  party_type           VARCHAR(30) NOT NULL,          -- LANDLORD/TENANT/OPERATOR/GUARANTOR/OTHER
  legal_name           VARCHAR(250),
  trade_name           VARCHAR(250),
  tax_id               VARCHAR(50),
  econ_activity        VARCHAR(200),
  business_category    VARCHAR(120),
  contact_name         VARCHAR(200),
  contact_email        VARCHAR(200),
  contact_phone        VARCHAR(50),
  legal_representative VARCHAR(200),
  created_at           DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_party_client (client_id),
  KEY idx_party_tax (tax_id)
) ENGINE=InnoDB;

CREATE TABLE contract_party (
  contract_party_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_id       BIGINT NOT NULL,
  party_id          BIGINT NOT NULL,
  role              VARCHAR(300) NOT NULL,      -- LANDLORD/TENANT/OPERATOR/GUARANTOR
  is_primary        CHAR(1) DEFAULT 'N',
  KEY idx_cp_contract (contract_id),
  FOREIGN KEY (contract_id) REFERENCES contract(contract_id),
  FOREIGN KEY (party_id) REFERENCES party(party_id)
) ENGINE=InnoDB;

-- =========================
-- C) ESPACIOS (si contratos los administran)
-- Nota: si "espacios" son otro MS, aquí guardas solo referencia.
-- =========================
CREATE TABLE airport_location (
  location_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
  client_id    BIGINT NOT NULL,
  site_id      BIGINT NULL,
  terminal     VARCHAR(50),
  floor        VARCHAR(20),
  zone         VARCHAR(80),
  description  VARCHAR(250),
  KEY idx_loc_client (client_id),
  KEY idx_loc_site (site_id)
) ENGINE=InnoDB;

CREATE TABLE leased_space (
  space_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  client_id      BIGINT NOT NULL,
  site_id        BIGINT NULL,
  location_id    BIGINT NULL,
  space_code     VARCHAR(80) NOT NULL,
  area_value     DECIMAL(12,2),
  area_unit      VARCHAR(10),                 -- M2/FT2
  space_type     VARCHAR(30) NOT NULL,        -- RETAIL/FNB/SERVICES/OFFICE/STORAGE
  description    TEXT,
  territorial_exclusivity CHAR(1) DEFAULT 'N',
  exclusivity_notes VARCHAR(2000),

  UNIQUE KEY uq_space (client_id, space_id),
  KEY idx_space_client (client_id),
  KEY idx_space_site (site_id),

  FOREIGN KEY (location_id) REFERENCES airport_location(location_id)
) ENGINE=InnoDB;

CREATE TABLE contract_space (
  contract_space_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_id       BIGINT NOT NULL,
  space_id          BIGINT NOT NULL,
  effective_from    DATE,
  effective_to      DATE,
  KEY idx_cs_contract (contract_id),
  FOREIGN KEY (contract_id) REFERENCES contract(contract_id),
  FOREIGN KEY (space_id) REFERENCES leased_space(space_id)
) ENGINE=InnoDB;

-- =========================
-- D) ECONOMÍA
-- =========================
CREATE TABLE rent_base (
  rent_base_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_id    BIGINT NOT NULL,
  amount         DECIMAL(14,2),
  currency       VARCHAR(10),
  periodicity    VARCHAR(20),
  payment_due_day INT,
  FOREIGN KEY (contract_id) REFERENCES contract(contract_id)
) ENGINE=InnoDB;

CREATE TABLE rent_variable (
  rent_variable_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_id            BIGINT NOT NULL,
  percentage             DECIMAL(6,3),
  threshold_json         JSON,
  min_guarantee_amount   DECIMAL(14,2),
  min_guarantee_currency VARCHAR(10),
  notes                  VARCHAR(2000),
  FOREIGN KEY (contract_id) REFERENCES contract(contract_id)
) ENGINE=InnoDB;

CREATE TABLE rent_escalation (
  escalation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_id   BIGINT NOT NULL,
  formula       VARCHAR(1000),
  index_name    VARCHAR(200),
  periodicity   VARCHAR(20),
  extra_1       VARCHAR(500),
  extra_2       VARCHAR(500),
  extra_3       VARCHAR(500),
  extra_4       VARCHAR(500),
  extra_5       VARCHAR(500),
  notes         VARCHAR(2000),
  FOREIGN KEY (contract_id) REFERENCES contract(contract_id)
) ENGINE=InnoDB;

CREATE TABLE security_deposit (
  deposit_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_id BIGINT NOT NULL,
  amount     DECIMAL(14,2),
  currency   VARCHAR(10),
  due_date   DATE,
  refund_conditions VARCHAR(2000),
  FOREIGN KEY (contract_id) REFERENCES contract(contract_id)
) ENGINE=InnoDB;

CREATE TABLE additional_charges (
  charge_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_id BIGINT NOT NULL,
  charge_type VARCHAR(30),        -- MAINTENANCE/UTILITIES/MARKETING/INSURANCE/OTHER
  amount      DECIMAL(14,2),
  currency    VARCHAR(10),
  periodicity VARCHAR(20),
  notes       VARCHAR(2000),
  FOREIGN KEY (contract_id) REFERENCES contract(contract_id)
) ENGINE=InnoDB;

CREATE TABLE payment_terms (
  payment_terms_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_id      BIGINT NOT NULL,
  payment_method   VARCHAR(30),  -- TRANSFER/CARD/CASH/OTHER
  payment_terms_text TEXT,
  grace_period_days INT,
  late_fee_type     VARCHAR(20), -- FIXED/PERCENT
  late_fee_amount   DECIMAL(14,2),
  late_fee_percent  DECIMAL(6,3),
  late_interest_percent DECIMAL(6,3),
  FOREIGN KEY (contract_id) REFERENCES contract(contract_id)
) ENGINE=InnoDB;


CREATE TABLE clause (
  clause_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_id  BIGINT NOT NULL,
  clause_type  VARCHAR(40) NOT NULL,
  title        VARCHAR(300),
  details      LONGTEXT,
  is_critical  CHAR(1) DEFAULT 'N',
  KEY idx_clause_contract (contract_id),
  FOREIGN KEY (contract_id) REFERENCES contract(contract_id)
) ENGINE=InnoDB;

CREATE TABLE tenant_obligation (
  obligation_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_id     BIGINT NOT NULL,
  obligation_type VARCHAR(40) NOT NULL, -- HOURS/MAINTENANCE/INSURANCE/PERMITS/OPS_STANDARDS
  details         LONGTEXT,
  KEY idx_obl_contract (contract_id),
  FOREIGN KEY (contract_id) REFERENCES contract(contract_id)
) ENGINE=InnoDB;

CREATE TABLE insurance_required (
  ins_req_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_id     BIGINT NOT NULL,
  insurance_type  VARCHAR(100) NOT NULL,
  coverage_amount DECIMAL(14,2),
  currency        VARCHAR(10),
  valid_from      DATE,
  valid_to        DATE,
  notes           VARCHAR(2000),
  KEY idx_ins_contract (contract_id),
  FOREIGN KEY (contract_id) REFERENCES contract(contract_id)
) ENGINE=InnoDB;


-- EXTRACCIÓN + EVIDENCIA + REVISIÓN + AUDITORÍA

CREATE TABLE contract_field_def (
  field_def_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
  client_id       BIGINT NOT NULL,
  field_key       VARCHAR(200) NOT NULL, -- term.start_date, economic.fixed_rent.amount, etc.
  field_label     VARCHAR(200),
  field_type      VARCHAR(30) NOT NULL,  -- DATE/NUMBER/MONEY/PERCENT/TEXT/ENUM/JSON
  is_critical     CHAR(1) DEFAULT 'N',
  target_accuracy DECIMAL(5,2),
  UNIQUE KEY uq_field (client_id, field_key),
  KEY idx_field_client (client_id)
) ENGINE=InnoDB;

CREATE TABLE contract_field_value (
  field_value_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_version_id  BIGINT NOT NULL,
  client_id            BIGINT NOT NULL,
  field_def_id         BIGINT NOT NULL,

  value_date           DATE,
  value_number         DECIMAL(18,6),
  value_text           VARCHAR(4000),
  value_currency       VARCHAR(10),
  value_json           JSON,

  confidence           DECIMAL(5,4),
  validation_status    VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING/VALIDATED/REJECTED/OVERRIDDEN
  reviewed_by          VARCHAR(120) NULL,
  reviewed_at          DATETIME NULL,
  review_comment       VARCHAR(1000) NULL,

  KEY idx_fv_cv (contract_version_id),
  KEY idx_fv_status (client_id, validation_status),

  FOREIGN KEY (contract_version_id) REFERENCES contract_version(contract_version_id),
  FOREIGN KEY (field_def_id) REFERENCES contract_field_def(field_def_id)
) ENGINE=InnoDB;

CREATE TABLE evidence_snippet (
  evidence_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_version_id BIGINT NOT NULL,
  field_value_id      BIGINT NOT NULL,
  client_id           BIGINT NOT NULL,

  page_number         INT,
  snippet_text        VARCHAR(4000),
  bbox_json           JSON,
  char_start          INT,
  char_end            INT,
  source_type         VARCHAR(20) DEFAULT 'OCR', -- OCR/NATIVE_PDF_TEXT/MANUAL
  created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,

  KEY idx_ev_fv (field_value_id),

  FOREIGN KEY (contract_version_id) REFERENCES contract_version(contract_version_id),
  FOREIGN KEY (field_value_id) REFERENCES contract_field_value(field_value_id)
) ENGINE=InnoDB;

CREATE TABLE review_task (
  task_id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_version_id BIGINT NOT NULL,
  client_id           BIGINT NOT NULL,
  assigned_to         VARCHAR(120) NULL,
  status              VARCHAR(20) NOT NULL DEFAULT 'OPEN', -- OPEN/IN_PROGRESS/DONE/CANCELLED
  priority            VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',
  created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
  due_at              DATETIME NULL,
  completed_at        DATETIME NULL,

  KEY idx_rt_cv (contract_version_id),
  KEY idx_rt_status (client_id, status),

  FOREIGN KEY (contract_version_id) REFERENCES contract_version(contract_version_id)
) ENGINE=InnoDB;

CREATE TABLE review_action (
  action_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_version_id BIGINT NOT NULL,
  field_value_id      BIGINT NOT NULL,
  client_id           BIGINT NOT NULL,

  action_type         VARCHAR(20) NOT NULL, -- APPROVE/REJECT/EDIT/OVERRIDE
  old_value_json      JSON,
  new_value_json      JSON,
  comment_text        VARCHAR(1000),
  actor              VARCHAR(120),
  created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,

  KEY idx_ra_fv (field_value_id),

  FOREIGN KEY (contract_version_id) REFERENCES contract_version(contract_version_id),
  FOREIGN KEY (field_value_id) REFERENCES contract_field_value(field_value_id)
) ENGINE=InnoDB;

CREATE TABLE audit_log (
  audit_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
  client_id    BIGINT NOT NULL,
  entity_type  VARCHAR(50) NOT NULL,  -- CONTRACT, CONTRACT_VERSION, FIELD_VALUE, DOCUMENT...
  entity_id    BIGINT NOT NULL,
  action       VARCHAR(50) NOT NULL,  -- CREATE/UPDATE/EXPORT/VALIDATE/...
  actor        VARCHAR(120) NULL,
  details_json JSON,
  created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,

  KEY idx_audit_client (client_id, created_at),
  KEY idx_audit_entity (entity_type, entity_id)
) ENGINE=InnoDB;