create table user_audit_log
(
    id                 bigserial
        primary key,
    event_timestamp    timestamp    default CURRENT_TIMESTAMP            not null,
    event_type         varchar(255)                                      not null,
    user_id            varchar(255)                                      not null,
    event_details      text,
    source_application varchar(255) default 'UNKNOWN'::character varying not null
);

comment on table user_audit_log is 'Stores audit records for user events and activities';

comment on column user_audit_log.source_application is 'The name of the application that generated the audit event';

alter table user_audit_log
    owner to admin;

create index idx_user_audit_log_timestamp
    on user_audit_log (event_timestamp);

create index idx_user_audit_log_user_id
    on user_audit_log (user_id);

create index idx_user_audit_log_event_type
    on user_audit_log (event_type);

INSERT INTO public.user_audit_log (id, event_timestamp, event_type, user_id, event_details, source_application) VALUES (1, '2025-05-23 19:44:49.954076', 'DELETED', '7', '{"id":7,"name":"Kurtis Weissnat","username":"Elwyn.Skiles","email":"Telly.Hoeger@billy.biz","address":{"street":"Rex Trail","suite":"Suite 280","city":"Howemouth","zipcode":"58804-1099","geo":{"lat":"24.8918","lng":"21.8984"}},"phone":"210.067.6132","website":"elvis.io","company":{"name":"Johns Group","catchPhrase":"Configurable multimedia task-force","bs":"generate enterprise e-tailers"}}', 'KafkaConsumer');
INSERT INTO public.user_audit_log (id, event_timestamp, event_type, user_id, event_details, source_application) VALUES (2, '2025-05-23 19:52:02.803221', 'DELETED', '8', '{"id":8,"name":"Nicholas Runolfsdottir V","username":"Maxime_Nienow","email":"Sherwood@rosamond.me","address":{"street":"Ellsworth Summit","suite":"Suite 729","city":"Aliyaview","zipcode":"45169","geo":{"lat":"-14.3990","lng":"-120.7677"}},"phone":"586.493.6943 x140","website":"jacynthe.com","company":{"name":"Abernathy Group","catchPhrase":"Implemented secondary concept","bs":"e-enable extensible e-tailers"}}', 'KafkaConsumer');
