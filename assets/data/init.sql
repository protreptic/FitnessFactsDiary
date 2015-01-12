drop schema fitnessfactsdiary if exists;
create schema fitnessfactsdiary;

set schema fitnessfactsdiary;

create table measure (
	id integer primary key auto_increment,
	name varchar (255),
	description varchar (255)
);

insert into measure (name) values ('Масса тела');
