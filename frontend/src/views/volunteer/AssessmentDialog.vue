<template>
  <v-dialog
      :value="dialog"
      @input="$emit('close-dialog')"
      @keydown.esc="$emit('close-dialog')"
      max-width="75%"
      max-height="80%"
  >
    <v-card>
      <v-form ref="form" lazy validation>
        <v-card-title>
          <span class="headline">New Assessment </span>
        </v-card-title>

        <v-card-text class="text-left">
          <v-text-field
              v-model="assessment.review"
              label="*Review"
              data-cy="reviewInput"
              :rules="[(v) => !!v || 'Review is required',
              (v) => isSaveValid() || 'Review must have at least 10 characters']"
              required
          />
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn
              @click="$emit('close-assessment-dialog')"
              data-cy="cancelButton"
          >Close</v-btn
          >
          <v-btn
              v-if="isSaveValid()"
              @click="save" data-cy="saveButton"
          >Save</v-btn
          >
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Vue, Prop } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Assessment from '@/models/assessment/Assessment';
import Volunteer from '@/models/volunteer/Volunteer';
import Institution from '@/models/institution/Institution';

@Component({
  components: {},
})

export default class AssessmentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;

  @Prop({ type: Object, required: true }) readonly institution!: Institution;
  @Prop({ type: Object, required: true }) readonly volunteer!: Volunteer;

  assessment: Assessment = new Assessment();

  async created() {
    this.assessment = new Assessment();
    this.assessment.institutionId = this.institution.id;
    this.assessment.volunteerId = this.volunteer.id;
    this.assessment.review = '';
  }

  async save() {
    if ((this.$refs.form as Vue & { validate: () => boolean }).validate()) {
      try {
        if (this.assessment && this.assessment.institutionId) {
          const result = await RemoteServices.createAssessment(
              this.$store.getters.getUser.id,
              this.assessment.institutionId,
              this.assessment,
          );
          this.$emit('save-assessment', result);
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  isSaveValid() {
    return this.assessment.review.length > 9;
  }
}
</script>

<style scoped>
.add-theme-feedback-container {
  height: 25px;
}
.add-theme-feedback {
  font-size: 1.05rem;
  color: #1b5e20;
  text-transform: uppercase;
}

.left-text {
  text-align: left;
}

.move-right {
  margin-left: 20px;
  margin-right: 20px;
}
</style>
