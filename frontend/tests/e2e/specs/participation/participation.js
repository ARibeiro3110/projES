describe('Participation', () => {
  beforeEach(() => {
    cy.deleteAllButArs();
    cy.createParticipationDemoEntities();
  });

  afterEach(() => {
    cy.deleteAllButArs();
  });

  it('create participation', () => {
      const RATING = '5';

      cy.demoMemberLogin()

      cy.intercept('POST', '/activities/*/participations').as('participate');
      cy.intercept('GET', '/users/*/getInstitution').as('getInstitutions');
      cy.intercept('GET', '/activities/*/enrollments').as('getEnrollments');

      // check institution activities table contains 2 instances
      cy.get('[data-cy="institution"]').click();
      cy.get('[data-cy="activities"]').click();
      cy.wait('@getInstitutions');
      cy.get('[data-cy="memberActivitiesTable"] tbody tr').should('have.length', 2);

      // check first activity has 1 participation
      cy.get('[data-cy="memberActivitiesTable"] tbody tr')
          .eq(0).children().eq(4).should('contain', 1);

      // check first activity has 2 enrollments
      cy.get('[data-cy="memberActivitiesTable"] tbody tr')
        .first().find('[data-cy="showEnrollments"]').click();
      cy.wait('@getEnrollments');
      cy.get('[data-cy="activityEnrollmentsTable"] tbody tr').should('have.length', 2);

      // check first enrollment has participating as false
      cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .eq(0).children().eq(2).should('contain', false);
      
      // create a participation for the first enrollment
      cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
        .first().find('[data-cy="selectParticipantButton"]').click();
      cy.get('[data-cy="ratingInput"]').type(RATING);
      cy.get('[data-cy="makeParticipantButton"]').click();
      cy.wait('@participate');

      // verify that the first enrollment of the table has participating as true
      cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
          .eq(0).children().eq(2).should('contain', true);
        
      // verify that the first activity has 2 participations
      cy.get('[data-cy="getActivities"]').click();
      cy.wait('@getInstitutions');
      cy.get('[data-cy="memberActivitiesTable"] tbody tr')
          .eq(0).children().eq(4).should('contain', 2);

      cy.logout();
  });
});
