select c.name, cp.count_person from company c
inner join
(select count(id) AS count_person, company_id from person
group by company_id) cp on c.id = cp.company_id
inner join
(select max(A.count_person) as max_person from
(select count(id) AS count_person, company_id from person
group by company_id) A) find_max
on cp.count_person = find_max.max_person;