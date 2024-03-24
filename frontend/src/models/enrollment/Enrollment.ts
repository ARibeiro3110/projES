import { ISOtoString } from '@/services/ConvertDateService';
import Activity from '@/models/activity/Activity';
import Volunteer from '@/models/volunteer/Volunteer';

export default class Enrollment {
  id: number | null = null;
  motivation!: string;
  enrollmentDateTime!: string;
  activity: Activity | null = null;
  volunteer: Volunteer | null = null;

  constructor(jsonObj?: Enrollment) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.motivation = jsonObj.motivation;
      this.activity = jsonObj.activity ? new Activity(jsonObj.activity) : null;
      this.enrollmentDateTime = ISOtoString(jsonObj.enrollmentDateTime);
    }
  }
}