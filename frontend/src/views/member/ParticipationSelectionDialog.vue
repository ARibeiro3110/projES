<template>
  <v-dialog
    :value="dialog"
    max-width="50%"
    max-height="50%"
  >
    <v-card>
      <v-form>
        <v-card-title>
          <span class="headline">Select Participant</span>
        </v-card-title>

        <v-card-text class="text-left">
          <v-text-field
            v-model="participation.rating"
            label="Rating"
            data-cy="ratingInput"
            required
          />
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn data-cy="cancelButton">Close</v-btn>
          <v-btn data-cy="makeParticipantButton">Make Participant</v-btn>
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import Participation from '@/models/participation/Participation';
import { Component, Vue, Model, Prop } from 'vue-property-decorator';

@Component({})
export default class ParticipationSelectionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Number, required: true }) readonly enrollmentVolunteerId!: number;
  @Prop({ type: Number, required: true }) readonly activityId!: number;

  participation: Participation = new Participation();

  async created(){
    this.participation = new Participation();
    this.participation.volunteerId = this.enrollmentVolunteerId;
    this.participation.activityId = this.activityId;
  }
}
</script>

