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
            .eq(3)
            .should('contain', '0')

        cy.logout();

        cy.demoVolunteerLogin();
        // TODO: implement e2e test as volunteer
        cy.logout();

        cy.demoAdminLogin();
        // TODO: implement e2e test as admin
        cy.logout();
    });
});