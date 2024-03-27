const credentials = {
  user: Cypress.env('psql_db_username'),
  host: Cypress.env('psql_db_host'),
  database: Cypress.env('psql_db_name'),
  password: Cypress.env('psql_db_password'),
  port: Cypress.env('psql_db_port'),
};

const INSTITUTION_COLUMNS = "institutions (id, active, confirmation_token, creation_date, email, name, nif, token_generation_date)";
const USER_COLUMNS = "users (user_type, id, creation_date, name, role, state, institution_id)";
const AUTH_USERS_COLUMNS = "auth_users (auth_type, id, active, email, username, user_id)";
const ACTIVITY_COLUMNS = "activity (id, application_deadline, creation_date, description, ending_date, name, participants_number_limit, region, starting_date, state, institution_id)";
const ENROLLMENT_COLUMNS = "enrollment (id, enrollment_date_time, motivation, activity_id, volunteer_id)";
const PARTICIPATION_COLUMNS = "participation (id, acceptance_date, rating, activity_id, volunteer_id)";

const now = new Date();
const tomorrow = new Date(now);
tomorrow.setDate(now.getDate() + 1);
const dayAfterTomorrow = new Date(now);
dayAfterTomorrow.setDate(now.getDate() + 2);
const yesterday = new Date(now);
yesterday.setDate(now.getDate() - 1);
const dayBeforeYesterday = new Date(now);
dayBeforeYesterday.setDate(now.getDate() - 2);


Cypress.Commands.add('deleteAllButArs', () => {
  cy.task('queryDatabase', {
    query: "DELETE FROM ENROLLMENT",
    credentials: credentials,
  });
  cy.task('queryDatabase', {
    query: "DELETE FROM PARTICIPATION",
    credentials: credentials,
  });
  cy.task('queryDatabase', {
    query: "DELETE FROM ASSESSMENT",
    credentials: credentials,
  });
  cy.task('queryDatabase', {
    query: "DELETE FROM ACTIVITY",
    credentials: credentials,
  });
  cy.task('queryDatabase', {
    query: "DELETE FROM AUTH_USERS WHERE NOT (username = 'ars')",
    credentials: credentials,
  });
  cy.task('queryDatabase', {
    query: "DELETE FROM USERS WHERE NOT (name = 'ars')",
    credentials: credentials,
  });
  cy.task('queryDatabase', {
    query: "DELETE FROM INSTITUTIONS",
    credentials: credentials,
  });
});

Cypress.Commands.add('createDemoEntities', () => {
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + INSTITUTION_COLUMNS + generateInstitutionTuple(1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(2, "MEMBER","DEMO-MEMBER", "MEMBER", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(2, "DEMO", "demo-member", 2),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(3, "VOLUNTEER","DEMO-VOLUNTEER", "VOLUNTEER", "NULL"),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(3, "DEMO", "demo-volunteer", 3),
    credentials: credentials,
  })
});

Cypress.Commands.add('createAssessmentDemoEntities', () => {
  cy.task('queryDatabase', {
    query: "INSERT INTO " + INSTITUTION_COLUMNS + generateInstitutionTuple(1),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + INSTITUTION_COLUMNS + generateOtherInstitutionTuple(2),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(2, "MEMBER","DEMO-MEMBER", "MEMBER", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(2, "DEMO", "demo-member", 2),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(3, "VOLUNTEER","DEMO-VOLUNTEER", "VOLUNTEER", "NULL"),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthVolunteerTuple(3, "DEMO", "demo-volunteer", 3),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + "VALUES " +generateActivityTuple(1, "Same institution is enrolled and participates", "A1", 1, 1) +","
        + generateActivityTuple(2, "Same institution is enrolled and participates", "A2", 1, 1) +","
        + generateActivityTuple(3, "Same institution is enrolled and does not participate", "A3", 2, 1) +","
        + generateActivityTuple(4, "Same institution is not enrolled", "A4", 2, 1) +","
        + generateActivityTuple(5, "Same institution before end date", "A5", 2, 1) +","
        + generateActivityTuple(6, "Other institution is enrolled and participates", "A6", 3, 2) +";",
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS +"VALUES "
        + generateEnrollmentTuple(1, 1, 3 ) +","
        + generateEnrollmentTuple(2, 2, 3 ) + ","
        + generateEnrollmentTuple(3, 3, 3 ) + ","
        + generateEnrollmentTuple(4, 6, 3 ) + ";",
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + PARTICIPATION_COLUMNS + "VALUES "
        + generateParticipationTuple(1, 1 , 3) + ","
        + generateParticipationTuple(2, 2 , 3) + ","
        + generateParticipationTuple(3, 6 , 3) + ";",
    credentials: credentials,
  })

})

Cypress.Commands.add('createEnrollmentDemoEntities', () => {
  cy.task('queryDatabase', {
    query: "INSERT INTO " + INSTITUTION_COLUMNS + generateInstitutionTuple(1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(2, "MEMBER","DEMO-MEMBER", "MEMBER", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(2, "DEMO", "demo-member", 2),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(3, "VOLUNTEER","DEMO-VOLUNTEER", "VOLUNTEER", "NULL"),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(3, "DEMO", "demo-volunteer", 3),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateActivityTuple(1, "Enrollment is open", "A1", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateActivityTuple(2, "Enrollment is open and it is already enrolled", "A2", 2),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateActivityTuple(3, "Enrollment is closed", "A3", 3),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS + generateEnrollmentTuple(5),
    credentials: credentials,
  })
});

function generateAuthUserTuple(id, authType, username, userId) {
  return "VALUES ('"
    + authType + "', '"
    + id + "', 't', 'demo_member@mail.com','"
    + username + "', '"
    + userId + "')"
}

function generateUserTuple(id, userType, name, role, institutionId) {
  return "VALUES ('"
    + userType + "', '"
    + id + "', '2022-02-06 17:58:21.419878', '"
    + name + "', '"
    + role + "', 'ACTIVE', "
    + institutionId + ")";
}

function generateInstitutionTuple(id) {
  return "VALUES ('"
    + id + "', 't', 'abca428c09862e89', '2022-08-06 17:58:21.402146', 'demo_institution@mail.com', 'DEMO INSTITUTION', '000000000', '2024-02-06 17:58:21.402134')";
}

function generateActivityTuple(id, description, name, participants_number_limit) {
  return "VALUES ('"
      + id + "', '2024-08-06 17:58:21.402146', '2024-08-06 17:58:21.402146', '"
      + description + "', '2024-08-08 17:58:21.402146', '"
      + name + "', '"
      + participants_number_limit + "', 'Lisbon', '2024-08-07 17:58:21.402146', 'APPROVED', '1')";
}

function generateEnrollmentTuple(id) {
  return "VALUES ('"
      + id + "', '2024-02-06 18:51:37.595713', 'sql-inserted-motivation', '2', '3')";
}

function generateActivityTuple(id, description, name, participants_number_limit) {
  return "VALUES ('"
      + id + "', '2024-08-06 17:58:21.402146', '2024-08-06 17:58:21.402146', '"
      + description + "', '2024-08-08 17:58:21.402146', '"
      + name + "', '"
      + participants_number_limit + "', 'Lisbon', '2024-08-07 17:58:21.402146', 'APPROVED', '1')";
}

function generateEnrollmentTuple(id) {
  return "VALUES ('"
      + id + "', '2024-02-06 18:51:37.595713', 'sql-inserted-motivation', '2', '3')";
}

function generateOtherInstitutionTuple(id) {
  return "VALUES ('"
      + id + "', 't', 'abca428c09862e89', '2022-08-06 17:58:21.402146','demo_institution@mail.com', 'DEMO INSTITUTION-2', '000000002', '2024-02-06 17:58:21.402134')";
}

function generateAuthVolunteerTuple(id, authType, username, userId) {
  return "VALUES ('"
      + authType + "', '"
      + id + "', 't', 'demo_volunteer@mail.com','"
      + username + "', '"
      + userId + "')"
}

function generateActivityTuple(id, description, name, participantsNumberLimit, institutionId) {
    return "('"
        + id + "', '2024-02-06 17:58:21.402146', '2024-02-06 17:58:21.402146', '" + description
        + "', '2024-02-08 10:58:21.402146', '" + name + "', '" + participantsNumberLimit + "', 'Lisbon', '2024-02-07 17:58:21.402146', 'APPROVED', '" + institutionId + "')";
}

function generateEnrollmentTuple(id, activityId, volunteerId) {
    return "('"
        + id + "', '2024-02-06 18:51:37.595713', 'sql-inserted-motivation', '" + activityId + "', '" + volunteerId + "')";
  }

function generateParticipationTuple(id, activityId, volunteerId) {
    return "('"
  + id + "', '2024-02-06 18:51:37.595713', '5', '" + activityId +"','" +volunteerId + "')";
}

