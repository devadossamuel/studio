-- Students table
create table students (
  _id integer primary key autoincrement,
  name text not null unique,
  grade text not null,
  phone text not null,
  address text
);
