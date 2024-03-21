import { ISOtoString } from '@/services/ConvertDateService';
import Volunteer from '@/models/volunteer/Volunteer';

export default class Enrollment {
  id: number | null = null;
  motivation!: string;
  enrollmentDateTime!: string;
  participating!: boolean;
  activityId: number | null = null;
  volunteerId: number | null = null;

  constructor(jsonObj?: Enrollment) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.motivation = jsonObj.motivation;
      this.activityId = jsonObj.activityId;
      this.volunteerId = jsonObj.volunteerId;
      this.enrollmentDateTime = ISOtoString(jsonObj.enrollmentDateTime);
      this.participating = jsonObj.participating;
      this.activityId = jsonObj.activityId;
    }
  }
}