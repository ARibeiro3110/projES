import { ISOtoString } from '@/services/ConvertDateService';

export default class Assessment {
  id: number | null = null;
  review!: string;
  reviewDate!: string;
  institutionId: number | null = null;
  volunteerId: number | null = null;
  volunteerName!: string;

  constructor(jsonObj?: Assessment) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.review = jsonObj.review;
      this.reviewDate = ISOtoString(jsonObj.reviewDate);
      this.institutionId = jsonObj.institutionId;
      this.volunteerId = jsonObj.volunteerId;
      this.volunteerName = jsonObj.volunteerName;
    }
  }
}
