USE test;

SHOW CREATE TABLE Users;

ALTER TABLE program RENAME TO Program;
ALTER TABLE hourinfo RENAME TO HourInfo;
ALTER TABLE users RENAME TO Users;

ALTER TABLE hourinfo RENAME COLUMN cpuUsage TO cpu_usage;
ALTER TABLE hourinfo RENAME COLUMN ramUsage TO ram_usage;
ALTER TABLE hourinfo RENAME COLUMN timeActSum TO time_act_sum;
ALTER TABLE hourinfo RENAME COLUMN timeSum TO time_sum;
ALTER TABLE hourinfo RENAME COLUMN dataPackCount TO data_pack_count;
ALTER TABLE hourinfo RENAME COLUMN creationDate TO creation_date;
ALTER TABLE users DROP COLUMN privilege;

CREATE TABLE UserGroup
(
	group_id int primary key AUTO_INCREMENT,
    group_name varchar(50) not null,
    creation_date date not null,
    gruop_status int not null
);

CREATE TABLE GroupMembers
(
	groupMembers_id int primary key AUTO_INCREMENT,
	group_id int not null,
	user_id int not null,
	privelege int not null,
	date_last_use date DEFAULT NULL,
    foreign key (group_id) references UserGroup(group_id),
    foreign key (user_id) references Users(user_id)
);

CREATE TABLE Form
(
	forms_id int primary key AUTO_INCREMENT,
    user_id int not null,
    form_name varchar(50) not null,
    settings varchar(1000) not null,
    creation_date date not null,
    date_last_use date DEFAULT NULL,
    foreign key (user_id) references Users(user_id)
);

CREATE INDEX form_user_index ON Form(user_id);