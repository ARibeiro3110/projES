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
          <v-btn
            v-if="isRatingValid"
            data-cy="makeParticipantButton"
          >
            Make Participant
          </v-btn>
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import RemoteServices from '@/services/RemoteServices';
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

  async save() {
    if ((this.$refs.form as Vue & { validate: () => boolean }).validate()) {
      try {
        if (this.participation && this.participation.rating) {
          const result = await RemoteServices.createParticipation(
            this.$store.getters.getUser.id,
            this.participation.activityId,
            this.participation,
          );
          this.$emit('save-participation', result);
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  // rating field may be empty, but if is it filled it must be with an integer from 1 to 5
  get isRatingValid(): boolean {
    return this.participation.rating === null || this.participation.rating >= 1 && this.participation.rating <= 5;
  }
}
</script>

