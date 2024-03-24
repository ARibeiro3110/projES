<template>
  <v-dialog
      :value="dialog"
      @input="$emit('close-dialog')"
      @keydown.esc="$emit('close-dialog')"
      max-width="75%"
      max-height="80%"
  >
    <v-card>
      <v-form>
        <v-card-title>
          <span class="headline">New Assessment </span>
        </v-card-title>

        <v-card-text class="text-left">
          <v-text-field
              v-model="review"
              label="*Review"
              data-cy="reviewInput"
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

  review: string = '';
  @Prop({ type: Object, required: true }) readonly institution!: Institution;
  @Prop({ type: Object, required: true }) readonly volunteer!: Volunteer;

  assessment: Assessment = new Assessment();

  async created() {
    this.assessment = new Assessment();
    this.assessment.institutionId = this.institution.id;
    this.assessment.volunteerId = this.volunteer.id;
  }

  async save() {
    // TODO: trabalha malandro
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
