import { ISOtoString } from '@/services/ConvertDateService';

export default class Enrollment {
  id: number | null = null;
  motivation!: string;
  enrollmentDateTime!: string;
  volunteerName!: String;
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
      this.volunteerId = jsonObj.volunteerId;
      this.volunteerName = jsonObj.volunteerName;
      this.participating = jsonObj.participating;
      this.activityId = jsonObj.activityId;
    }
  }
}