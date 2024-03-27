<template>
  <div>
    <v-card class="table">
      <v-data-table
        :headers="headers"
        :items="activities"
        :search="search"
        disable-pagination
        :hide-default-footer="true"
        :mobile-breakpoint="0"
        data-cy="volunteerActivitiesTable"
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
          </v-card-title>
        </template>
        <template v-slot:[`item.themes`]="{ item }">
          <v-chip v-for="theme in item.themes" v-bind:key="theme.id">
            {{ theme.completeName }}
          </v-chip>
        </template>
        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip v-if="item.state === 'APPROVED'" bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="red"
                v-on="on"
                data-cy="reportButton"
                @click="reportActivity(item)"
                >warning</v-icon
              >
            </template>
            <span>Report Activity</span>
          </v-tooltip>
          <v-tooltip v-if="isEnrollmentOpen(item) && !isVolunteerEnrolled(item)" bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                  class="mr-2 action-button"
                  color="blue"
                  v-on="on"
                  data-cy="enrollmentButton"
                  @click="enrollInActivity(item)"
              >fa-solid fa-sign-in</v-icon
              >
            </template>
            <span>Enroll in Activity</span>
          </v-tooltip>
        </template>
      </v-data-table>
      <enrollment-dialog
        v-if="currentEnrollment && enrollmentDialog"
        v-model="enrollmentDialog"
        v-on:close-enrollment-dialog="onCloseEnrollmentDialog"
        v-on:enroll="onEnrollInActivity"
        :enrollment="currentEnrollment"
        :volunteer="volunteer"
        :activity="currentActivity"
      />
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Enrollment from '@/models/enrollment/Enrollment';
import Activity from '@/models/activity/Activity';
import Volunteer from '@/models/volunteer/Volunteer';
import { show } from 'cli-cursor';
import EnrollmentDialog from '@/views/volunteer/EnrollmentDialog.vue';

@Component({
  methods: { show },
  components: {
    'enrollment-dialog': EnrollmentDialog,
  },
})

export default class VolunteerActivitiesView extends Vue {
  activities: Activity[] = [];
  currentActivity: Activity | null = null;
  currentEnrollment: Enrollment | null = null
  volunteer: Volunteer | null = null;
  volunteerEnrollments: Enrollment[] = [];
  enrollmentDialog: boolean = false;
  search: string = '';
  headers: object = [
    {
      text: 'Name',
      value: 'name',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Region',
      value: 'region',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Participants',
      value: 'participantsNumberLimit',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Themes',
      value: 'themes',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Description',
      value: 'description',
      align: 'left',
      width: '30%',
    },
    {
      text: 'State',
      value: 'state',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Start Date',
      value: 'formattedStartingDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'End Date',
      value: 'formattedEndingDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Application Deadline',
      value: 'formattedApplicationDeadline',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '5%',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.activities = await RemoteServices.getActivities();
      this.volunteer = this.$store.getters.getUser;
      this.volunteerEnrollments = await RemoteServices.getVolunteerEnrollments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  onCloseEnrollmentDialog() {
    this.currentActivity = null;
    this.enrollmentDialog = false;
  }

  async onEnrollInActivity(newEnrollment: Enrollment) {
    if (this.currentActivity) {
      this.currentActivity.enrollments = this.currentActivity.enrollments.filter(
          (e) => e.activityId !== newEnrollment.activityId,
      ) ?? [];
      this.currentActivity.enrollments.unshift(newEnrollment);
      this.enrollmentDialog = false;
      this.currentEnrollment = null;
    }
  }

  async reportActivity(activity: Activity) {
    if (activity.id !== null) {
      try {
        const result = await RemoteServices.reportActivity(
          this.$store.getters.getUser.id,
          activity.id,
        );
        this.activities = this.activities.filter((a) => a.id !== activity.id);
        this.activities.unshift(result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  enrollInActivity(activity: Activity) {
    this.currentEnrollment = new Enrollment();
    this.currentActivity = activity;
    this.enrollmentDialog = true;
  }

  isEnrollmentOpen(activity: Activity) {
    // check if current date is before the application deadline
    return new Date(activity.applicationDeadline) > new Date();
  }
 
  isVolunteerEnrolled(activity: Activity) {
    return this.volunteerEnrollments.some((e) => e.activityId === activity.id);
  }
}
</script>

<style lang="scss" scoped></style>
