describe('Assessment', () => {
  beforeEach(() => {
    cy.deleteAllButArs();
    cy.createAssessmentDemoEntities();
  });

  afterEach(() => {
    cy.deleteAllButArs();
  });

  it('create assessment', () => {
    const NAME = 'A1';
    const REGION = 'Lisbon';
    const NUMBER = '1';
    const DESCRIPTION = 'Same institution is enrolled and participates';
    const REVIEW = "Speed limit is too low, should be at least 150kmh";

    cy.demoVolunteerLogin()
    // intercept get activities
    cy.intercept('GET', '/activities').as('getActivities');
    //cy.intercept('GET', '/themes/availableThemes').as('availableTeams')

    // go to list of activities
    cy.get('[data-cy="volunteerActivities"]').click();
    cy.wait('@getActivities');

    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .should('have.length', 6)
        .eq(0)
        .children()
        .should('have.length', 10)

    // check columns
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(0).children().eq(0).should('contain', NAME)
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(0).children().eq(1).should('contain', REGION)
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(0).children().eq(2).should('contain', NUMBER)
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(0).children().eq(4).should('contain', DESCRIPTION);

    //check assess button existence
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(0).find('[data-cy="assessButton"]').should('exist');
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(1).find('[data-cy="assessButton"]').should('exist');
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(2).find('[data-cy="assessButton"]').should('not.exist');
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(3).find('[data-cy="assessButton"]').should('not.exist');
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(4).find('[data-cy="assessButton"]').should('not.exist');
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(5).find('[data-cy="assessButton"]').should('exist');

     //TO CHANGE
      // intercept create assessment request and inject date values in the request body
      //cy.intercept('POST', '/`institutions/${institutionId}/assessments', (req) => {
      //    req.body = {
      //        applicationDeadline: '2024-01-13T12:00:00+00:00',
      //        startingDate: '2024-01-14T12:00:00+00:00',
      //        endingDate: '2024-01-15T12:00:00+00:00'
      //    };
      //}).as('assess');

    //create
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(0).find('[data-cy="assessButton"]')
        .click();

    // fill form
    cy.get('[data-cy="reviewInput"]').type(REVIEW);
    // save form
    cy.get('[data-cy="saveButton"]').click()
    // check request was done
    //cy.wait('@assess')
    // check results
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
      .should('have.length', 6)
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(0).find('[data-cy="assessButton"]').should('not.exist');
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(1).find('[data-cy="assessButton"]').should('not.exist');
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(2).find('[data-cy="assessButton"]').should('not.exist');
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(3).find('[data-cy="assessButton"]').should('not.exist');
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(4).find('[data-cy="assessButton"]').should('not.exist');
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(5).find('[data-cy="assessButton"]').should('exist');
    cy.logout();

    //Como membro
    //
    // Verificar que a tabela de avaliações tem uma única avaliação
    //
    // Verificar que a avaliação tem o texto inserido pelo voluntário
    cy.demoMemberLogin();

    cy.logout();
  });
});
