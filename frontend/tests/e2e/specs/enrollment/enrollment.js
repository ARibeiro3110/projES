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
        // TODO: implement e2e test as member
        cy.logout();

        cy.demoVolunteerLogin();
        // TODO: implement e2e test as volunteer
        cy.logout();

        cy.demoAdminLogin();
        // TODO: implement e2e test as admin
        cy.logout();
    });
});
