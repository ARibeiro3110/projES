<template>
  <v-card class="table">
    <div class="text-h3">{{ activity.name }}</div>
    <v-data-table
      :headers="headers"
      :items="enrollments"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      data-cy="activityEnrollmentsTable"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <v-spacer />
          <v-btn
            color="primary"
            dark
            @click="getActivities"
            data-cy="getActivities"
            >Activities</v-btn
          >
        </v-card-title>
      </template>
      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip v-if="isVolunteerInActivity(item) && canAddParticipant()" bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              @click="selectParticipant(item)"
              data-cy="selectParticipantButton"
              v-on="on"
            >fa-solid fa-check
            </v-icon>
          </template>
          <span>Select Participant</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <participation-selection-dialog
      v-if="selectParticipantDialog"
      v-model="selectParticipantDialog"
      :enrollmentVolunteerId="enrollmentVolunteerId"
      :activityId="activityId"
      v-on:close-participation-selection-dialog="onCloseParticipationSelectionDialog"
      v-on:save-participation="onMakeParticipant"
      />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Enrollment from '@/models/enrollment/Enrollment';
import ParticipationSelectionDialog from '@/views/member/ParticipationSelectionDialog.vue';

@Component({
  components: {
    'participation-selection-dialog': ParticipationSelectionDialog,
  }
})
export default class InstitutionActivityEnrollmentsView extends Vue {
  activity!: Activity;
  enrollments: Enrollment[] = [];
  search: string = '';

  selectParticipantDialog: boolean = false;
  enrollmentVolunteerId: number | null = null;
  activityId: number | null = null;

  headers: object = [
    {
      text: 'Name',
      value: 'volunteerName',
      align: 'left',
      width: '25%',
    },
    {
      text: 'Motivation',
      value: 'motivation',
      align: 'left',
      width: '50%',
    },
    {
      text: 'Participating',
      value: 'participating',
      align: 'left',
      width: '20%',
    },
    {
      text: 'Application Date',
      value: 'enrollmentDateTime',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '5%',
    }
  ];

  async created() {
    this.activity = this.$store.getters.getActivity;
    if (this.activity !== null && this.activity.id !== null) {
      this.activityId = this.activity.id;
      await this.$store.dispatch('loading');
      try {
        this.enrollments = await RemoteServices.getActivityEnrollments(
          this.activity.id,
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }

  selectParticipant(enrollment: Enrollment) {
    this.enrollmentVolunteerId = enrollment.volunteerId; 
    this.selectParticipantDialog = true;
  }

  onCloseParticipationSelectionDialog() {
    this.selectParticipantDialog = false;
    this.enrollmentVolunteerId = null;
  }

  async onMakeParticipant() {
    if (this.activity !== null && this.activity.id !== null)
      this.enrollments = await RemoteServices.getActivityEnrollments(
          this.activity.id,
    );
    this.enrollmentVolunteerId = null;
    this.selectParticipantDialog = false;
  }

  async getActivities() {
    await this.$store.dispatch('setActivity', null);
    this.$router.push({ name: 'institution-activities' }).catch(() => {});
  }

  isVolunteerInActivity(enrollment: Enrollment) {
    return enrollment.participating === false;
  }

  canAddParticipant() {
    return this.activity.participations.length < this.activity.participantsNumberLimit;
  }
}
</script>

<style lang="scss" scoped>
.date-fields-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.date-fields-row {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}
</style>
