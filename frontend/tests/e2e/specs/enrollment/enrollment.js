describe('Activity', () => {
    beforeEach(() => {
        cy.deleteAllButArs();
        cy.createEnrollmentDemoEntities();
    });

    afterEach(() => {
        cy.deleteAllButArs();
    });

    it('create activities', () => {
        const MOTIVATION = 'I want to make a difference';

        // e2e test as member
        cy.demoMemberLogin()
        // intercept get institutions
        cy.intercept('GET', '/users/*/getInstitution').as('getInstitutions');
        
        // go to activities list
        cy.get('[data-cy="institution"]').click();

        cy.get('[data-cy="activities"]').click();
        cy.wait('@getInstitutions');

        cy.get('[data-cy="memberActivitiesTable"] tbody tr')
            // check if there are 3 activities
            .should('have.length', 3)
            // check if the first activity has 0 applications
            .eq(0)
            .children()
            .eq(3) // applications column
            .should('contain', '0')

        cy.logout();

        // e2e test as volunteer
        cy.demoVolunteerLogin();
        cy.intercept('POST', '/activities/*/enrollments').as('enroll');

        cy.intercept('GET', '/activities').as('getActivities');
        cy.get('[data-cy="volunteerActivities"]').click();
        cy.wait('@getActivities');

        // fill dialog form and enroll
        cy.get('[data-cy=enrollmentButton]').click();
        cy.get('[data-cy="motivationInput"]').type(MOTIVATION);
        cy.get('[data-cy="saveEnrollment"]').click();
        cy.wait('@enroll');
        cy.logout();

        cy.demoAdminLogin();
        // intercept get institutions
        cy.intercept('GET', '/users/*/getInstitution').as('getInstitutions');
        // intercept get enrollments
        cy.intercept('GET', '/activities/*/enrollments').as('getEnrollments');

        // go to activities list
        cy.get('[data-cy="institution"]').click();

        cy.get('[data-cy="activities"]').click();
        cy.wait('@getInstitutions');

        // check if the first activity has 1 application
        cy.get('[data-cy="memberActivitiesTable"] tbody tr')
            .eq(0)
            .children()
            .eq(3)
            .should('contain', '1')
        
        // select Show Applications for the first activity
        cy.get('[data-cy="memberActivitiesTable"] tbody tr')
            .eq(0)
            .find('[data-cy="showEnrollments"]')
            .click();
        
        cy.wait('@getEnrollments');
        
        cy.get('[data-cy="enrollmentTable"] tbody tr')
            // check if there is 1 application
            .should('have.length', 1)
            // check if the first application has the correct motivation
            .eq(0)
            .children()
            .eq(0) // motivation column
            .should('contain', MOTIVATION);

        cy.logout();
    });
});