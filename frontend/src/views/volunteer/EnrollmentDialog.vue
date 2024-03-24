<template>
  <v-dialog v-model="dialog" persistent width="1300">
    <v-card>
      <v-card-title>
        <span class="headline">
          Enroll in {{ activity.name }}
        </span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12" sm="6" md="4">
              <v-text-field
                  label="*Motivation"
                  :rules="[(v) => !!v || 'Motivation is required']"
                  required
                  v-model="newEnrollment.motivation"
                  data-cy="nameInput"
              ></v-text-field>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
            color="blue-darken-1"
            variant="text"
            @click="$emit('close-enrollment-dialog')"
        >
          Close
        </v-btn>
        <v-btn
            color="blue-darken-1"
            variant="text"
            @click="enroll"
            data-cy="saveActivity"
        >
          Enroll
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { Vue, Component, Prop, Model } from 'vue-property-decorator';
import Activity from '@/models/activity/Activity';
import Volunteer from '@/models/volunteer/Volunteer';
import Enrollment from '@/models/enrollment/Enrollment';
import RemoteServices from '@/services/RemoteServices';
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';
import { ISOtoString } from '@/services/ConvertDateService';

Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);
@Component({
  methods: { ISOtoString },
})
export default class ActivityDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Enrollment, required: true }) readonly enrollment!: Enrollment;
  @Prop({ type: Volunteer, required: true }) readonly volunteer!: Volunteer;
  @Prop({ type: Activity, required: true }) readonly activity!: Activity;

  newEnrollment: Enrollment = new Enrollment();

  cypressCondition: boolean = false;

  async created() {
    if (this.enrollment && this.volunteer) {
      this.newEnrollment = new Enrollment(this.enrollment);
      this.newEnrollment.volunteer = new Volunteer(this.volunteer);
    }
  }

  get canEnroll(): boolean {
    // TODO: only allow saving if all required fields are filled and conditions are met
    return true;
  }

  async enroll() {
    if ((this.$refs.form as Vue & { validate: () => boolean }).validate() && this.canEnroll && this.newEnrollment?.activity?.id) {
      try {
        const result = await RemoteServices.createEnrollment(this.newEnrollment.activity.id ,this.newEnrollment);
        this.$emit('enroll', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style scoped lang="scss"></style>
