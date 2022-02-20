select p.name, c.name from person as p
inner join company as c on p.company_id = c.id
where p.company_id != 5;