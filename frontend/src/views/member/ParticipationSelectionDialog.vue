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
            v-model="rating"
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
import { Component, Vue, Model } from 'vue-property-decorator';

@Component
export default class ParticipationSelectionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  rating: string = ''; 
}
</script>

