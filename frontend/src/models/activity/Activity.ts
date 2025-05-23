import { ISOtoString } from '@/services/ConvertDateService';
import Theme from '@/models/theme/Theme';
import Enrollment from '@/models/enrollment/Enrollment';
import Institution from '@/models/institution/Institution';
import Participation from '@/models/participation/Participation';

export default class Activity {
  id: number | null = null;
  name!: string;
  region!: string;
  participantsNumberLimit!: number;
  themes: Theme[] = [];
  enrollments: Enrollment[] = [];
  institution!: Institution;
  state!: string;
  creationDate!: string;
  description!: string;
  startingDate!: string;
  formattedStartingDate!: string;
  endingDate!: string;
  formattedEndingDate!: string;
  applicationDeadline!: string;
  formattedApplicationDeadline!: string;
  participations: Participation[] = [];
  numberOfParticipations!: number;
  numberOfEnrollments!: number;

  constructor(jsonObj?: Activity) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.region = jsonObj.region;
      this.participantsNumberLimit = jsonObj.participantsNumberLimit;
      this.themes = jsonObj.themes.map((themes: Theme) => {
        return new Theme(themes);
      });
      this.institution = jsonObj.institution;
      this.state = jsonObj.state;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.description = jsonObj.description;
      this.startingDate = jsonObj.startingDate;
      if (jsonObj.startingDate)
        this.formattedStartingDate = ISOtoString(jsonObj.startingDate);
      this.endingDate = jsonObj.endingDate;
      if (jsonObj.endingDate)
        this.formattedEndingDate = ISOtoString(jsonObj.endingDate);
      this.applicationDeadline = jsonObj.applicationDeadline;
      if (jsonObj.applicationDeadline)
        this.formattedApplicationDeadline = ISOtoString(
          jsonObj.applicationDeadline,
        );
      this.participations = jsonObj.participations;
      this.numberOfParticipations = jsonObj.numberOfParticipations;
      this.numberOfEnrollments = jsonObj.numberOfEnrollments;
    }
  }
}
