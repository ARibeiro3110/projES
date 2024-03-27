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

      // TODO: add activity table verifications

      // TODO: add enrollment table verifications

      // TODO: add activity participation creation

      cy.logout();
  });
});
