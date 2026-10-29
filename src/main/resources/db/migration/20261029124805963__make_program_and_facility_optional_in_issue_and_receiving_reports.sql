-- Make the Program and Facility filters optional (empty = All) in the issue and receiving reports.

UPDATE report.template_parameters
SET required = false
WHERE id IN (
    'a172099f-7351-4832-a8b3-8b503c45d7b1', 'f7e74ea4-1fef-43eb-9a6e-113080158e68',
    '6d7aab1e-5210-4e29-b463-7305110e8a17', '378ec23c-3613-485e-8b23-56cdb06ac973');

-- Pin the filter order, the update above moves the rows to the end of the table.
UPDATE report.template_parameters p
SET displayorder = o.displayorder
FROM (VALUES
    ('22bc6a4c-a553-4b9c-8cc2-fe3b5b5d13e5'::uuid, 0),
    ('c9f91542-2347-453c-8f84-2d3ebed3ee66'::uuid, 1),
    ('df349525-92b9-48bd-92cb-c421b8fa2994'::uuid, 2),
    ('16f7e985-3a13-4481-b1c9-840b7302dcd1'::uuid, 3),
    ('6be6f1ef-5f4d-43b5-871f-5550449c86cd'::uuid, 4),
    ('a172099f-7351-4832-a8b3-8b503c45d7b1'::uuid, 5),
    ('f7e74ea4-1fef-43eb-9a6e-113080158e68'::uuid, 6),
    ('024b260e-7535-46ec-9f18-5711ddb480bf'::uuid, 7),
    ('4c6e461a-ae6e-48dc-b215-4c62170d073b'::uuid, 0),
    ('f5f316b6-d071-4a62-a300-25bd318f7510'::uuid, 1),
    ('944918de-dbc8-458e-a00f-203843aa7256'::uuid, 2),
    ('603ae871-0d9e-4081-987a-7bf8ef436ccf'::uuid, 3),
    ('2dd15c50-9da3-40bc-ab93-d171071d5636'::uuid, 4),
    ('6d7aab1e-5210-4e29-b463-7305110e8a17'::uuid, 5),
    ('378ec23c-3613-485e-8b23-56cdb06ac973'::uuid, 6),
    ('4c9da215-ac09-43ec-a6fa-643a0f243ad3'::uuid, 7)
) AS o (id, displayorder)
WHERE p.id = o.id;

