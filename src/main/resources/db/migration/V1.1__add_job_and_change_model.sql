CREATE TABLE job (
  id varchar(36) NOT NULL,
  status varchar(255) NOT NULL,
  created_at timestamp with time zone NOT NULL,
  finished_at timestamp with time zone,
  branch_id varchar(36) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_job_branch_id FOREIGN KEY (branch_id) REFERENCES branch(id)
);

ALTER TABLE branch
ADD COLUMN sub_domain varchar(255) NOT NULL;